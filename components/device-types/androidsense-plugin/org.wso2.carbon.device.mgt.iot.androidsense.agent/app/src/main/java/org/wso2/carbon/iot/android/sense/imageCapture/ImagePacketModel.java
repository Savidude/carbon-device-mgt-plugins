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


public class ImagePacketModel {

    private String data;
    private String id;
    private int pos;
    private double size;

    public ImagePacketModel(String data, String id, int pos, double size) {
        this.data = data;
        this.id = id;
        this.pos = pos;
        this.size = size;
    }

    @Override
    public String toString() {
        return "{\"data\": \"" + data + "\", \"id\": \"" + id + "\", \"pos\": " + pos +
                ", \"size\": " + size + "}";
    }
}
