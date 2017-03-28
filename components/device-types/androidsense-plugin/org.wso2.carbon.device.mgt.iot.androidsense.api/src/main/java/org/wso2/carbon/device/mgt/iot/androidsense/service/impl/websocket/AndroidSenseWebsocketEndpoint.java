package org.wso2.carbon.device.mgt.iot.androidsense.service.impl.websocket;

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

import javax.inject.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint(value = "/{sessionId}")
@Singleton
public class AndroidSenseWebsocketEndpoint {
    private static Log log = LogFactory.getLog(AndroidSenseWebsocketEndpoint.class);
    private static Map<String, Session> clientSessions = new HashMap<>();
    private static Session session;

    /**
     * This method will be invoked when a client requests for a
     * WebSocket connection.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession, @PathParam("sessionId") String sessionId) {
        session = userSession;
    }

    /**
     * This method will be invoked when a client closes a WebSocket
     * connection.
     *
     * @param userSession the userSession which is opened.
     */
    @OnClose
    public void onClose(Session userSession) {
        clientSessions.values().remove(userSession);

    }

    @OnError
    public void onError(Throwable t) {
        log.error("Error occurred " + t);
    }

    /**
     * This method will be invoked when a message received from device
     * to send client.
     *
     * @param message the message sent by device to client
     */
    public static void sendMessage(String message) {
        session.getAsyncRemote().sendText(message);
    }
}
