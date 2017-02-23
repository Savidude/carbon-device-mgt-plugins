/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package org.wso2.carbon.iot.android.sense.speech.command;

import android.content.Context;
import android.util.Log;

import org.wso2.carbon.iot.android.sense.data.publisher.mqtt.AndroidSenseMQTTHandler;
import org.wso2.carbon.iot.android.sense.data.publisher.mqtt.transport.MQTTTransportHandler;
import org.wso2.carbon.iot.android.sense.data.publisher.mqtt.transport.TransportHandlerException;
import org.wso2.carbon.iot.android.sense.util.LocalRegistry;

public class CommandRecognition {
    private final String TAG = CommandRecognition.class.getName();

    private static final String BULB = "BULB";
    private static final String ON_COMMAND = "ON";
    private static final String OFF_COMMAND = "OFF";

    private static final String RASPBERRYPI_ID = "123";

    private String command;
    private Context context;

    public CommandRecognition(String command, Context context) {
        this.command = command;
        this.context = context;
    }

    public String executeCommand(){
        command = command.toUpperCase();
        if (command.contains(BULB)){
            if (command.contains(ON_COMMAND)){
                sendCommandViaMqtt(ON_COMMAND);
                return "Bulb on command executed";
            } else if (command.contains(OFF_COMMAND)){
                sendCommandViaMqtt(OFF_COMMAND);
                return "Bulb off command executed";
            }
        }

        return "Invalid command";
    }

    private void sendCommandViaMqtt(String state){
        try {
            MQTTTransportHandler mqttTransportHandler = AndroidSenseMQTTHandler.getInstance(context);
            if (!mqttTransportHandler.isConnected()) {
                mqttTransportHandler.connect();
            }

            String user = LocalRegistry.getUsername(context);
            String deviceId = LocalRegistry.getDeviceId(context);
            String message = BULB + ":" + state;
            String publishTopic = LocalRegistry.getTenantDomain(context) + "/raspberrypi/" + RASPBERRYPI_ID;
            mqttTransportHandler.publishDeviceData(user, deviceId, message, publishTopic);
        } catch (TransportHandlerException e) {

            Log.e(TAG, "Data Publish Failed", e);
        }
    }
}
