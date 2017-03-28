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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.iot.androidsense.plugins.constants.AndroidSenseConstants;
import org.wso2.carbon.device.mgt.iot.androidsense.plugins.internal.AndroidSenseManagementDataHolder;
import org.wso2.carbon.device.mgt.iot.androidsense.plugins.mqtt.MqttConfig;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterConfiguration;
import org.wso2.carbon.event.input.adapter.core.MessageType;
import org.wso2.carbon.event.input.adapter.core.exception.InputEventAdapterException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AndroidSenseUtil {
    private static Log log = LogFactory.getLog(AndroidSenseUtil.class);

    public static void setupMqttInputAdapter() throws IOException {
        if (!MqttConfig.getInstance().isEnabled()) {
            return;
        }
        InputEventAdapterConfiguration inputEventAdapterConfiguration =
                createMqttInputEventAdapterConfiguration(AndroidSenseConstants.MQTT_ADAPTER_NAME,
                        AndroidSenseConstants.MQTT_ADAPTER_TYPE, MessageType.TEXT);
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(
                    AndroidSenseConstants.DEVICE_TYPE_PROVIDER_DOMAIN, true);
            AndroidSenseManagementDataHolder.getInstance().getInputEventAdapterService()
                    .create(inputEventAdapterConfiguration, new AndroidSenseEventAdapterSubscription());
        } catch (InputEventAdapterException e) {
            log.error("Unable to create Input Event Adapter : " + AndroidSenseConstants.MQTT_ADAPTER_NAME, e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    private static InputEventAdapterConfiguration createMqttInputEventAdapterConfiguration (String name, String type,
                                                                                            String msgFormat) throws IOException{
        InputEventAdapterConfiguration inputEventAdapterConfiguration = new InputEventAdapterConfiguration();
        inputEventAdapterConfiguration.setName(name);
        inputEventAdapterConfiguration.setType(type);
        inputEventAdapterConfiguration.setMessageFormat(msgFormat);

        Map<String, String> mqttAdapterProperties = new HashMap<>();
        mqttAdapterProperties.put(AndroidSenseConstants.USERNAME_PROPERTY_KEY, MqttConfig.getInstance().getUsername());
        mqttAdapterProperties.put(AndroidSenseConstants.PASSWORD_PROPERTY_KEY, MqttConfig.getInstance().getPassword());
        mqttAdapterProperties.put(AndroidSenseConstants.DCR_PROPERTY_KEY, MqttConfig.getInstance().getDcrUrl());
        mqttAdapterProperties.put(AndroidSenseConstants.BROKER_URL_PROPERTY_KEY, MqttConfig.getInstance().getUrl());
        mqttAdapterProperties.put(AndroidSenseConstants.SCOPES_PROPERTY_KEY, MqttConfig.getInstance().getScopes());
        mqttAdapterProperties.put(AndroidSenseConstants.CLEAR_SESSION_PROPERTY_KEY, MqttConfig.getInstance()
                .getClearSession());
        mqttAdapterProperties.put(AndroidSenseConstants.QOS_PROPERTY_KEY, MqttConfig.getInstance().getQos());
        mqttAdapterProperties.put(AndroidSenseConstants.CLIENT_ID_PROPERTY_KEY, "");
        mqttAdapterProperties.put(AndroidSenseConstants.TOPIC, AndroidSenseConstants.SUBSCRIBED_TOPIC);
        mqttAdapterProperties.put(AndroidSenseConstants.CONTENT_TRANSFORMATION,
                AndroidSenseMqttContentTransformer.class.getName());
        mqttAdapterProperties.put(AndroidSenseConstants.CONTENT_VALIDATION, "default");
        mqttAdapterProperties.put(AndroidSenseConstants.RESOURCE, "input-event");
        inputEventAdapterConfiguration.setProperties(mqttAdapterProperties);

        return inputEventAdapterConfiguration;
    }
}
