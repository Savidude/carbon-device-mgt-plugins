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


import io.swagger.annotations.*;
import org.wso2.carbon.apimgt.annotations.api.Scope;
import org.wso2.carbon.apimgt.annotations.api.Scopes;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "blebeacon"),
                                @ExtensionProperty(name = "context", value = "/blebeacon"),
                        })
                }
        ),
        tags = {
                @Tag(name = "blebeacon", description = "")
        }
)
@Scopes(
        scopes = {
                @Scope(
                        name = "Enroll device",
                        description = "",
                        key = "perm:blebeacon:enroll",
                        permissions = {"/device-mgt/devices/enroll/blebeacon"}
                )
        }
)
/**
 * This is the controller API which is used to control agent side functionality
 */
public interface DeviceTypeService {

    @Path("device/iBeacon/register")
    @POST
    Response registerIBeacon(@QueryParam("beaconName") String beaconName, @QueryParam("uuid") String uuid,
                             @QueryParam("major") String major, @QueryParam("minor") String minor,
                             @QueryParam("location") String location);

    @Path("device/eddystone/register")
    @POST
    Response registerEddystone(@QueryParam("beaconName") String beaconName, @QueryParam("namespace") String namespace,
                               @QueryParam("instance") String instance, @QueryParam("location") String location);

}