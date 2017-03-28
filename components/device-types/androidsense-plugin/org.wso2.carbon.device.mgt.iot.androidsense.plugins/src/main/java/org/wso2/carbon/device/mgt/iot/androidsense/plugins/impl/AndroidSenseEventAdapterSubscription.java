package org.wso2.carbon.device.mgt.iot.androidsense.plugins.impl;

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

import org.json.JSONObject;
import org.wso2.carbon.device.mgt.iot.androidsense.plugins.model.ImageModel;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterSubscription;

import java.util.HashMap;
import java.util.Map;

public class AndroidSenseEventAdapterSubscription implements InputEventAdapterSubscription {
    private Map<String, ImageModel> images = new HashMap<>();

    @Override
    public void onEvent(Object o) {
        String message = (String) o;
        JSONObject image = new JSONObject(message);
        String id = image.getString("id");
        String data = image.getString("data");
        int pos = image.getInt("pos");
        double size = image.getDouble("size");
        createImage(id, data, pos, size);
    }

    private void createImage(String id, String data, int pos, double size) {
        ImageModel imageModel = images.get(id);

        if (imageModel == null) {
            imageModel = new ImageModel();
            imageModel.setImageData(new String[(int)size]);
            imageModel.setLength(0);
            images.put(id, imageModel);
        }

        if (imageModel.getLength() >= size) {
            imageModel.getImageData()[pos] = data;
            imageModel.setLength(imageModel.getLength() + 1);
            if (imageModel.getLength() <= (size + 1)) {
                StringBuilder displayImage = new StringBuilder("Image||");
                for (String image: imageModel.getImageData()) {
                    displayImage.append(image);
                }
                images.remove(id);
//                DigitalDisplayWebSocketServerEndPoint.sendMessage(sessionId, displayScreenShot);
            }
        }
    }
}
