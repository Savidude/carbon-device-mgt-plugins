package org.wso2.carbon.device.mgt.iot.androidsense.plugins.config;

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
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.iot.androidsense.plugins.config.exception.AndroidSenseConfigurationException;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class AndroidSenseConfig {
    private static final Log log = LogFactory.getLog(AndroidSenseConfig.class);
    private static final String DEVICE_TYPE_CONFIG_PATH =
            CarbonUtils.getEtcCarbonConfigDirPath() + File.separator + "device-mgt-plugins" + File.separator
                    + "android-sense.xml";
    private static AndroidSenseConfig androidSenseConfig = new AndroidSenseConfig();
    private static DeviceManagementConfiguration deviceManagementConfiguration;

    public static AndroidSenseConfig getInstance(){
        return androidSenseConfig;
    }

    public static void initialize() throws AndroidSenseConfigurationException {
        File configFile = new File(DEVICE_TYPE_CONFIG_PATH);

        try {
            Document doc = convertToDocument(configFile);

            /* Un-marshaling Webapp Authenticator configuration */
            JAXBContext ctx = JAXBContext.newInstance(DeviceManagementConfiguration.class);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            //unmarshaller.setSchema(getSchema());
            deviceManagementConfiguration = (DeviceManagementConfiguration) unmarshaller.unmarshal(doc);
        } catch (JAXBException e) {
            throw new AndroidSenseConfigurationException("Error occurred while un-marshalling the file " +
                    DEVICE_TYPE_CONFIG_PATH, e);
        }
    }

    public DeviceManagementConfiguration getDeviceTypeConfiguration() {
        return  deviceManagementConfiguration;
    }

    private static Document convertToDocument(File file) throws AndroidSenseConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            return docBuilder.parse(file);
        } catch (Exception e) {
            throw new AndroidSenseConfigurationException("Error occurred while parsing file, while converting " +
                    "to a org.w3c.dom.Document", e);
        }
    }
}
