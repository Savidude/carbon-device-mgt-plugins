package org.wso2.carbon.device.mgt.iot.digitaldisplay.plugin.impl;

/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.wso2.carbon.device.mgt.iot.digitaldisplay.plugin.model.ScreenShotModel;
import org.wso2.carbon.device.mgt.iot.digitaldisplay.plugin.websocket.DigitalDisplayWebSocketServerEndPoint;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterSubscription;

import java.util.HashMap;
import java.util.Map;

public class DigitalDisplayEventAdapterSubsciption implements InputEventAdapterSubscription {
    private Map<String, ScreenShotModel> screenshots = new HashMap<>();

    @Override
    public void onEvent(Object o) {
        String message = (String) o;
        String[] messageData = message.split("::");

        String sessionId = messageData[0];
        if(messageData.length == 2){
            String responseMessage = messageData[1];
            DigitalDisplayWebSocketServerEndPoint.sendMessage(sessionId, new StringBuilder(responseMessage));
        }  else if (messageData.length == 3){
            String response = messageData[2];
            JSONObject schreenShot = new JSONObject(response);
            String picId = schreenShot.getString("pic_id");
            String data = schreenShot.getString("data");
            int pos = schreenShot.getInt("pos");
            int length = schreenShot.getInt("size");
            createScreenshot(sessionId, picId, pos, length, data);
        }
    }

    private void createScreenshot(String sessionId, String picId, int pos, int length, String data){
        ScreenShotModel screenShotModel = screenshots.get(picId);

        if (screenShotModel == null) {
            screenShotModel = new ScreenShotModel();
            screenShotModel.setScreenShotData(new String[length + 1]);
            screenShotModel.setLength(0);
            screenshots.put(picId, screenShotModel);
        }
        if (screenShotModel.getLength() <= length) {
            screenShotModel.getScreenShotData()[pos] = data;
            screenShotModel.setLength(screenShotModel.getLength() + 1);
            if (screenShotModel.getLength() == (length + 1)) {
                StringBuilder displayScreenShot = new StringBuilder("Screenshot||");
                for (String screenshot : screenShotModel.getScreenShotData()) {
                    displayScreenShot.append(screenshot);
                }
                screenshots.remove(picId);
                DigitalDisplayWebSocketServerEndPoint.sendMessage(sessionId, displayScreenShot);
            }
        }
    }
}

