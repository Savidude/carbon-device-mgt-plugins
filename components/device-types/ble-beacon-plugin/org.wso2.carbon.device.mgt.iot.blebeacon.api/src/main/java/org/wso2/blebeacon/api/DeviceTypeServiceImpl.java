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

package org.wso2.blebeacon.api;

import org.wso2.blebeacon.api.constants.BLEBeaconConstants;
import org.wso2.blebeacon.api.database.BeaconTable;
import org.wso2.blebeacon.api.database.Beacon_LocationTable;
import org.wso2.blebeacon.api.database.LocationTable;
import org.wso2.blebeacon.api.database.impl.BeaconTableImpl;
import org.wso2.blebeacon.api.database.impl.Beacon_LocationTableImpl;
import org.wso2.blebeacon.api.database.impl.LocationTableImpl;
import org.wso2.blebeacon.api.dto.DeviceJSON;
import org.wso2.blebeacon.api.util.APIUtil;
import org.wso2.blebeacon.api.util.EddystoneProperties;
import org.wso2.blebeacon.api.util.IBeaconProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.application.extension.dto.ApiApplicationKey;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
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

    @Path("device/iBeacon/register")
    @POST
    public Response registerIBeacon(@QueryParam("beaconName") String beaconName, @QueryParam("uuid") String uuid,
                             @QueryParam("major") String major, @QueryParam("minor") String minor,
                             @QueryParam("location") String location){

        try {
            String deviceId = shortUUID();

            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(deviceId);
            deviceIdentifier.setType(BLEBeaconConstants.DEVICE_TYPE);
            if (APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
            }

            Device device = new Device();
            device.setDeviceIdentifier(deviceId);
            EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
            enrolmentInfo.setDateOfEnrolment(new Date().getTime());
            enrolmentInfo.setDateOfLastUpdate(new Date().getTime());
            enrolmentInfo.setStatus(EnrolmentInfo.Status.ACTIVE);
            device.setName(beaconName);
            device.setType(BLEBeaconConstants.DEVICE_TYPE);
            enrolmentInfo.setOwnership(EnrolmentInfo.OwnerShip.BYOD);
            enrolmentInfo.setOwner(APIUtil.getAuthenticatedUser());
            device.setEnrolmentInfo(enrolmentInfo);

            IBeaconProperties properties = new IBeaconProperties(deviceId, beaconName, uuid, major, minor, location);

            BeaconTable beaconTable = new BeaconTableImpl();
            if(APIUtil.getDeviceManagementService().enrollDevice(device) && beaconTable.addIBeacon(properties)){
                if(addLocation(deviceId, location)){
                    return Response.status(Response.Status.OK).build();
                } else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }
            }else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } catch (DeviceManagementException e) {
            log.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("device/eddystone/register")
    @POST
    public Response registerEddystone(@QueryParam("beaconName") String beaconName, @QueryParam("namespace") String namespace,
                               @QueryParam("instance") String instance, @QueryParam("location") String location){
        try {
            String deviceId = shortUUID();

            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(deviceId);
            deviceIdentifier.setType(BLEBeaconConstants.DEVICE_TYPE);
            if (APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
            }

            Device device = new Device();
            device.setDeviceIdentifier(deviceId);
            EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
            enrolmentInfo.setDateOfEnrolment(new Date().getTime());
            enrolmentInfo.setDateOfLastUpdate(new Date().getTime());
            enrolmentInfo.setStatus(EnrolmentInfo.Status.ACTIVE);
            device.setName(beaconName);
            device.setType(BLEBeaconConstants.DEVICE_TYPE);
            enrolmentInfo.setOwnership(EnrolmentInfo.OwnerShip.BYOD);
            enrolmentInfo.setOwner(APIUtil.getAuthenticatedUser());
            device.setEnrolmentInfo(enrolmentInfo);

            String namespaceId = "0x"+namespace;
            String instanceId = "0x"+instance;

            EddystoneProperties properties = new EddystoneProperties(deviceId, beaconName, namespaceId, instanceId, location);

            BeaconTable beaconTable = new BeaconTableImpl();
            if(APIUtil.getDeviceManagementService().enrollDevice(device) && beaconTable.addEddystone(properties)){
                if(addLocation(deviceId, location)){
                    return Response.status(Response.Status.OK).build();
                } else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }
            }else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } catch (DeviceManagementException e) {
            log.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean addLocation(String deviceId, String location){
        boolean status = false;

        LocationTable locationTable = new LocationTableImpl();
        int locationId = locationTable.addLocation(location);
        if(locationId>0){
            Beacon_LocationTable beacon_locationTable = new Beacon_LocationTableImpl();
            if(beacon_locationTable.mapBeaconToLocation(deviceId, locationId)){
                status = true;
            }
        }

        return status;
    }
}