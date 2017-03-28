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

package org.wso2.carbon.iot.android.sense.imageCapture;


import android.content.Context;
import android.util.Log;

import org.wso2.carbon.iot.android.sense.constants.SenseConstants;
import org.wso2.carbon.iot.android.sense.data.publisher.mqtt.AndroidSenseMQTTHandler;
import org.wso2.carbon.iot.android.sense.data.publisher.mqtt.transport.MQTTTransportHandler;
import org.wso2.carbon.iot.android.sense.data.publisher.mqtt.transport.TransportHandlerException;
import org.wso2.carbon.iot.android.sense.util.LocalRegistry;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ImageDataPublisher {
    private static final String TAG = ImageDataPublisher.class.getName();
    private static final int PACKET_SIZE = 3000;

    private int start;
    private int end;
    private String id;
    private int pos;
    private int size;

    private String encodedImage;

    private MQTTTransportHandler mqttTransportHandler;
    private String topic;

    public ImageDataPublisher(String encodedImage, Context context) {
        this.start = 0;
        this.end = PACKET_SIZE;
        this.id = shortUUID();
        this.pos = 0;
        this.size = encodedImage.length();
        this.encodedImage = encodedImage;

        this.mqttTransportHandler = AndroidSenseMQTTHandler.getInstance(context);
        if (!mqttTransportHandler.isConnected()) {
            mqttTransportHandler.connect();
        }
        String deviceId = LocalRegistry.getDeviceId(context);
        this.topic = LocalRegistry.getTenantDomain(context) + "/" + SenseConstants.DEVICE_TYPE + "/" + deviceId + "/image_data";
    }

    public void publishImageData() {
        double packetCount = Math.ceil(size/PACKET_SIZE);

        while (start < size) {
            ImagePacketModel model;
            if (pos != packetCount) {
                model = new ImagePacketModel(encodedImage.substring(start, end), id, pos, packetCount);
            } else {
                model = new ImagePacketModel(encodedImage.substring(start, encodedImage.length()-1), id, pos, packetCount);
            }
            end += PACKET_SIZE;
            start += PACKET_SIZE;
            pos++;

            try {
                mqttTransportHandler.publishDeviceData(null, null, model.toString(), topic);
            } catch (TransportHandlerException e) {
                Log.e(TAG, "Data Publish Failed", e);
            }
        }
    }

    private String shortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes(StandardCharsets.UTF_8)).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }

}
