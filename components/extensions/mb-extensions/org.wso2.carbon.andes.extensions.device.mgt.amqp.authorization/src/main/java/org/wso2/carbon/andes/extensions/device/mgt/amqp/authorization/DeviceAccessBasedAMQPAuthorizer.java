package org.wso2.carbon.andes.extensions.device.mgt.amqp.authorization;

/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import feign.Feign;
import feign.FeignException;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.andes.extensions.device.mgt.amqp.authorization.client.OAuthRequestInterceptor;
import org.wso2.carbon.andes.extensions.device.mgt.amqp.authorization.client.dto.AuthorizationRequest;
import org.wso2.carbon.andes.extensions.device.mgt.amqp.authorization.client.dto.DeviceAccessAuthorizationAdminService;
import org.wso2.carbon.andes.extensions.device.mgt.amqp.authorization.client.dto.DeviceAuthorizationResult;
import org.wso2.carbon.andes.extensions.device.mgt.amqp.authorization.client.dto.DeviceIdentifier;
import org.wso2.carbon.andes.extensions.device.mgt.amqp.authorization.config.AuthorizationConfigurationManager;
import org.wso2.carbon.andes.extensions.device.mgt.amqp.authorization.util.AuthorizationCacheKey;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.andes.configuration.enums.AMQPAuthorizationPermissionLevel;
import org.wso2.andes.server.security.auth.amqp.IAuthorizer;

import javax.cache.Cache;
import javax.cache.CacheConfiguration;
import javax.cache.Caching;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeviceAccessBasedAMQPAuthorizer implements IAuthorizer {
    private static Log logger = LogFactory.getLog(DeviceAccessBasedAMQPAuthorizer.class);

    private static final String CACHE_MANAGER_NAME = "amqpAuthorizationCacheManager";
    private static final String CACHE_NAME = "amqpAuthorizationCache";
    private static final String CDMF_SERVER_BASE_CONTEXT = "/api/device-mgt/v1.0";

    private static AuthorizationConfigurationManager amqpAuthorizationConfiguration;
    private static DeviceAccessAuthorizationAdminService deviceAccessAuthorizationAdminService;

    public DeviceAccessBasedAMQPAuthorizer(){
        amqpAuthorizationConfiguration = AuthorizationConfigurationManager.getInstance();
        deviceAccessAuthorizationAdminService = Feign.builder()
                .requestInterceptor(new OAuthRequestInterceptor())
                .contract(new JAXRSContract()).encoder(new GsonEncoder()).decoder(new GsonDecoder())
                .target(DeviceAccessAuthorizationAdminService.class,
                        amqpAuthorizationConfiguration.getDeviceMgtServerUrl() + CDMF_SERVER_BASE_CONTEXT);
    }

    public boolean isAuthorizedForTopic(String topic, String username, AMQPAuthorizationPermissionLevel permissionLevel){
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME, true);

        String topics[] = topic.split("/");
        String tenantDomainFromTopic = topics[0];

        if(!tenantDomainFromTopic.equals(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME)){
            return false;
        }

        Cache<AuthorizationCacheKey, Boolean> cache = getCache();
        if (topics.length < 3){
            AuthorizationCacheKey key = new AuthorizationCacheKey(tenantDomainFromTopic, username, "", "");
            if (cache.get(key) != null && cache.get(key)) {
                return true;
            }
            AuthorizationRequest request = new AuthorizationRequest();
            request.setTenantDomain(tenantDomainFromTopic);
            try {
                DeviceAuthorizationResult authorizationResult = deviceAccessAuthorizationAdminService.isAuthorized(request);
                if (authorizationResult != null) {
                    cache.put(key, true);
                    return true;
                }
                return false;
            } catch (FeignException e) {
                logger.error(e.getMessage(), e);
                return false;
            } finally {
                PrivilegedCarbonContext.endTenantFlow();
                return false;
            }
        }

        String deviceType = topics[1];
        String deviceId = topics[2];
        AuthorizationCacheKey key = new AuthorizationCacheKey(tenantDomainFromTopic, username, deviceId, deviceType);
        if (cache.get(key) != null && cache.get(key)) {
            return true;
        }

        List<String> requiredPermission;
        if (permissionLevel == AMQPAuthorizationPermissionLevel.SUBSCRIBE){
            requiredPermission = amqpAuthorizationConfiguration.getSubscriberPermissions();
        } else {
            requiredPermission = amqpAuthorizationConfiguration.getPublisherPermissions();
        }

        AuthorizationRequest authorizationRequest = new AuthorizationRequest();
        authorizationRequest.setTenantDomain(tenantDomainFromTopic);
        if (requiredPermission != null){
            authorizationRequest.setPermissions(requiredPermission);
        }
        authorizationRequest.setUsername(username);
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(deviceId);
        deviceIdentifier.setType(deviceType);
        List<DeviceIdentifier> deviceIdentifiers = new ArrayList<>();
        deviceIdentifiers.add(deviceIdentifier);
        authorizationRequest.setDeviceIdentifiers(deviceIdentifiers);

        try {
            DeviceAuthorizationResult deviceAuthorizationResult = deviceAccessAuthorizationAdminService.isAuthorized(authorizationRequest);
            List<DeviceIdentifier> devices = deviceAuthorizationResult.getAuthorizedDevices();
            if (devices != null && devices.size() > 0) {
                DeviceIdentifier authorizedDevice = devices.get(0);
                if (authorizedDevice.getId().equals(deviceId) && authorizedDevice.getType().equals(deviceType)) {
                    cache.put(key, true);
                    return true;
                }
            }
        } catch (FeignException e) {
            logger.error(e.getMessage(), e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
            return false;
        }


}

    private Cache<AuthorizationCacheKey,Boolean> getCache() {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(
                org.wso2.carbon.base.MultitenantConstants.SUPER_TENANT_DOMAIN_NAME, true);
        try {
            if (amqpAuthorizationConfiguration.getCacheDuration() == 0) {
                return Caching.getCacheManagerFactory().getCacheManager(CACHE_MANAGER_NAME).getCache(CACHE_NAME);
            } else {
                return Caching.getCacheManagerFactory().getCacheManager(CACHE_MANAGER_NAME).<AuthorizationCacheKey, Boolean>createCacheBuilder(CACHE_NAME).
                        setExpiry(CacheConfiguration.ExpiryType.MODIFIED, new CacheConfiguration.Duration(
                                TimeUnit.SECONDS, amqpAuthorizationConfiguration.getCacheDuration())).
                        setStoreByValue(false).build();
            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }
}
