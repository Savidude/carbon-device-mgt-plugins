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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var senseClient = null;

window.onload = function () {
    console.log();
    connect();
}

function connect() {
    var target = "wss://localhost:9443/android_sense";
    if ('WebSocket' in window) {
        senseClient = new WebSocket(target);
    } else if ('MozWebSocket' in window) {
        senseClient = new MozWebSocket(target);
    } else {
        console.log('WebSocket is not supported by this browser.');
    }

    senseClient.onmessage = function (event) {
        var message = event.data;
        var type = message.split('||')[0];
        var reply = message.split('||')[1];

        if (type.valueOf() == new String("Image").valueOf()) {
            document.getElementById('img').setAttribute('src', 'data:image/jpeg;base64,' + reply);
            document.getElementById('zoom-image').setAttribute('href', 'data:image/jpeg;base64,' + reply);
        }
    }
}