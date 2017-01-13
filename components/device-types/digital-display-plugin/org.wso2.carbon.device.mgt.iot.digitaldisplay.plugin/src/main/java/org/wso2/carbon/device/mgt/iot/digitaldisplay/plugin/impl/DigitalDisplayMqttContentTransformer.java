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
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.input.adapter.extension.ContentTransformer;
import org.wso2.carbon.device.mgt.iot.digitaldisplay.plugin.constants.DigitalDisplayConstants;
import org.wso2.carbon.device.mgt.iot.digitaldisplay.plugin.exception.DigitalDisplayDeviceMgtPluginException;

import java.security.PublicKey;
import java.util.Map;

public class DigitalDisplayMqttContentTransformer implements ContentTransformer {
    @Override
    public Object transform(Object message, Map<String, Object> dynamicProperties) {
        return message;
    }
}
