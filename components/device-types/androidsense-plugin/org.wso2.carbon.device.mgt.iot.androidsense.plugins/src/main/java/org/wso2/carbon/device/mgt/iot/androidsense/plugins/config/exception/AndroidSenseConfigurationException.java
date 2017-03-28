package org.wso2.carbon.device.mgt.iot.androidsense.plugins.config.exception;

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

public class AndroidSenseConfigurationException extends Exception{

    private static final long serialVersionUID = -3151279431229070297L;

    public AndroidSenseConfigurationException(int errorCode, String message) {
        super(message);
    }

    public AndroidSenseConfigurationException(int errorCode, String message, Throwable cause) {
        super(message, cause);
    }

    public AndroidSenseConfigurationException(String msg, Exception nestedEx) {
        super(msg, nestedEx);
    }

    public AndroidSenseConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AndroidSenseConfigurationException(String msg) {
        super(msg);
    }

    public AndroidSenseConfigurationException() {
        super();
    }

    public AndroidSenseConfigurationException(Throwable cause) {
        super(cause);
    }
}
