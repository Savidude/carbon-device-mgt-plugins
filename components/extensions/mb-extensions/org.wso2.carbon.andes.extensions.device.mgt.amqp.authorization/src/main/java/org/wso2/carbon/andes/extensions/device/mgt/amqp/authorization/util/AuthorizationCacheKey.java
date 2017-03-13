package org.wso2.carbon.andes.extensions.device.mgt.amqp.authorization.util;

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

public class AuthorizationCacheKey {
    String tenantDomain;
    String deviceId;
    String deviceType;
    String username;

    public AuthorizationCacheKey(String tenantDomain, String username, String deviceId, String deviceType) {
        this.username = username;
        this.tenantDomain = tenantDomain;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    @Override
    public int hashCode() {
        int result = this.deviceType.hashCode();
        result = 31 * result + ("@" + this.deviceId + "@" + this.tenantDomain + "@" + this.username).hashCode();

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AuthorizationCacheKey) && deviceType.equals(
                ((AuthorizationCacheKey) obj).deviceType) && tenantDomain.equals(
                ((AuthorizationCacheKey) obj).tenantDomain ) && deviceId.equals(
                ((AuthorizationCacheKey) obj).deviceId) && username.equals(
                ((AuthorizationCacheKey) obj).username);
    }
}
