/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.bleagent.api;

import org.wso2.bleagent.api.constants.BLEAgentConstants;
import org.wso2.bleagent.api.database.ActionTable;
import org.wso2.bleagent.api.database.BeaconTable;
import org.wso2.bleagent.api.database.Beacon_LocationTable;
import org.wso2.bleagent.api.database.ProfileTable;
import org.wso2.bleagent.api.database.impl.ActionTableImpl;
import org.wso2.bleagent.api.database.impl.BeaconTableImpl;
import org.wso2.bleagent.api.database.impl.Beacon_LocationTableImpl;
import org.wso2.bleagent.api.database.impl.ProfileTableImpl;
import org.wso2.bleagent.api.dto.DeviceJSON;
import org.wso2.bleagent.api.util.APIUtil;
import org.wso2.bleagent.api.util.Action;
import org.wso2.bleagent.api.util.ZipArchive;
import org.wso2.bleagent.api.util.ZipUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.application.extension.APIManagementProviderService;
import org.wso2.carbon.apimgt.application.extension.dto.ApiApplicationKey;
import org.wso2.carbon.apimgt.application.extension.exception.APIManagerException;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.identity.jwt.client.extension.JWTClient;
import org.wso2.carbon.identity.jwt.client.extension.dto.AccessTokenInfo;
import org.wso2.carbon.identity.jwt.client.extension.exception.JWTClientException;
import org.wso2.carbon.user.api.UserStoreException;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.util.UUID;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This is the API which is used to control and manage device type functionality
 */
public class DeviceTypeServiceImpl implements DeviceTypeService {

    private static final String KEY_TYPE = "PRODUCTION";
    private static Log log = LogFactory.getLog(DeviceTypeService.class);
    private static ApiApplicationKey apiApplicationKey;
    private ConcurrentHashMap<String, DeviceJSON> deviceToIpMap = new ConcurrentHashMap<>();

    private static String shortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes(StandardCharsets.UTF_8)).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }

    @Path("device/eddystone/connect")
    @POST
    public Response eddystoneConnect(@QueryParam("namespace") String namespace,
                              @QueryParam("instance") String instance,
                              @QueryParam("profile") String profile){
        Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        BeaconTable beaconTable = new BeaconTableImpl();
        Beacon_LocationTable beacon_locationTable = new Beacon_LocationTableImpl();
        ActionTable actionTable = new ActionTableImpl();

        String beaconId = beaconTable.getEddystoneId(namespace, instance);
        if (beaconId!=null){
            int locationId = beacon_locationTable.getLocationId(beaconId);
            if(locationId!=-1){
                Action action = actionTable.getAction(Integer.parseInt(profile), locationId);
                if(action.getType()!=null){
                    response = Response.status(Response.Status.ACCEPTED.getStatusCode()).entity(action.toString()).build();
                }else{
                    response = Response.status(Response.Status.NOT_FOUND).build();
                }
            }
        }

        return response;
    }




















//    /**
//     * @param agentInfo device owner,id
//     * @return true if device instance is added to map
//     */
//    @Path("device/register")
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response registerDevice(final DeviceJSON agentInfo) {
//        String deviceId = agentInfo.deviceId;
//        if ((agentInfo.deviceId != null) && (agentInfo.owner != null)) {
//            deviceToIpMap.put(deviceId, agentInfo);
//            return Response.status(Response.Status.OK).build();
//        }
//        return Response.status(Response.Status.NOT_ACCEPTABLE).build();
//    }

    /**
     * To download device type agent source code as zip file
     * @param deviceName   name for the device type instance
     * @param sketchType   folder name where device type agent was installed into server
     * @return  Agent source code as zip file
     */
    @Path("device/download")
    @GET
    @Produces("application/zip")
    public Response downloadSketch(@QueryParam("deviceName") String deviceName,
                                   @QueryParam("sketchType") String sketchType) {
        try {
            ZipArchive zipFile = createDownloadFile(APIUtil.getAuthenticatedUser(), deviceName, sketchType);
            Response.ResponseBuilder response = Response.ok(FileUtils.readFileToByteArray(zipFile.getZipFile()));
            response.status(Response.Status.OK);
            response.type("application/zip");
            response.header("Content-Disposition", "attachment; filename=\"" + zipFile.getFileName() + "\"");
            Response resp = response.build();
            zipFile.getZipFile().delete();
            return resp;
        } catch (IllegalArgumentException ex) {
            return Response.status(400).entity(ex.getMessage()).build();//bad request
        } catch (DeviceManagementException ex) {
            log.error(ex.getMessage(), ex);
            return Response.status(500).entity(ex.getMessage()).build();
        } catch (JWTClientException ex) {
            log.error(ex.getMessage(), ex);
            return Response.status(500).entity(ex.getMessage()).build();
        } catch (APIManagerException ex) {
            log.error(ex.getMessage(), ex);
            return Response.status(500).entity(ex.getMessage()).build();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return Response.status(500).entity(ex.getMessage()).build();
        } catch (UserStoreException ex) {
            log.error(ex.getMessage(), ex);
            return Response.status(500).entity(ex.getMessage()).build();
        }
    }

    /**
     * Register device into device management service
     * @param deviceId unique identifier for given device type instance
     * @param name  name for the device type instance
     * @return check whether device is installed into cdmf
     */
    private int register(String deviceId, String name) {
        int profileId = -1;
        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(deviceId);
            deviceIdentifier.setType(BLEAgentConstants.DEVICE_TYPE);
            if (APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                return profileId;
            }
            Device device = new Device();
            device.setDeviceIdentifier(deviceId);
            EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
            enrolmentInfo.setDateOfEnrolment(new Date().getTime());
            enrolmentInfo.setDateOfLastUpdate(new Date().getTime());
            enrolmentInfo.setStatus(EnrolmentInfo.Status.ACTIVE);
            enrolmentInfo.setOwnership(EnrolmentInfo.OwnerShip.BYOD);
            device.setName(name);
            device.setType(BLEAgentConstants.DEVICE_TYPE);
            enrolmentInfo.setOwner(APIUtil.getAuthenticatedUser());
            device.setEnrolmentInfo(enrolmentInfo);

            ProfileTable profileTable = new ProfileTableImpl();
            profileId = profileTable.addProfile(name);
            if (APIUtil.getDeviceManagementService().enrollDevice(device) && profileId!=-1) {
                APIUtil.registerApiAccessRoles(APIUtil.getAuthenticatedUser());
            }
            return profileId;
        } catch (DeviceManagementException e) {
            log.error(e.getMessage(), e);
            return profileId;
        }
    }

    private ZipArchive createDownloadFile(String owner, String deviceName, String sketchType)
            throws DeviceManagementException, JWTClientException, APIManagerException,
            UserStoreException {
        //create new device id
        String deviceId = shortUUID();
        if (apiApplicationKey == null) {
            String applicationUsername = PrivilegedCarbonContext.getThreadLocalCarbonContext().getUserRealm()
                    .getRealmConfiguration().getAdminUserName();
            applicationUsername = applicationUsername + "@" + APIUtil.getAuthenticatedUserTenantDomain();
            APIManagementProviderService apiManagementProviderService = APIUtil.getAPIManagementProviderService();
            String[] tags = {BLEAgentConstants.DEVICE_TYPE};
            apiApplicationKey = apiManagementProviderService.generateAndRetrieveApplicationKeys(
                    BLEAgentConstants.DEVICE_TYPE, tags, KEY_TYPE, applicationUsername, true,
                    BLEAgentConstants.APIM_APPLICATION_TOKEN_VALIDITY_PERIOD);
        }
        JWTClient jwtClient = APIUtil.getJWTClientManagerService().getJWTClient();
        String scopes = "device_type_" + BLEAgentConstants.DEVICE_TYPE + " device_" + deviceId;
        AccessTokenInfo accessTokenInfo = jwtClient.getAccessToken(apiApplicationKey.getConsumerKey(),
                apiApplicationKey.getConsumerSecret(), owner + "@" + APIUtil.getAuthenticatedUserTenantDomain(), scopes);

        //create token
        String accessToken = accessTokenInfo.getAccessToken();
        String refreshToken = accessTokenInfo.getRefreshToken();
        int status = register(deviceId, deviceName);
        if (status==-1) {
            String msg = "Error occurred while registering the device with " + "id: " + deviceId + " owner:" + owner;
            throw new DeviceManagementException(msg);
        }
        ZipUtil ziputil = new ZipUtil();
        ZipArchive zipFile = ziputil.createZipFile(owner, APIUtil.getTenantDomainOftheUser(), sketchType,
                deviceId, deviceName, accessToken, refreshToken, status);
        return zipFile;
    }
}