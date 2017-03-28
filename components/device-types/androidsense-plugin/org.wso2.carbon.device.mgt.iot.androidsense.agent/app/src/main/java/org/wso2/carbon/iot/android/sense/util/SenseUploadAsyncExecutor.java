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

package org.wso2.carbon.iot.android.sense.util;

import android.os.AsyncTask;
import android.util.Log;

import org.wso2.carbon.iot.android.sense.constants.SenseConstants;
import org.wso2.carbon.iot.android.sense.util.dto.AndroidSenseManagerService;
import org.wso2.carbon.iot.android.sense.util.dto.OAuthRequestInterceptor;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import feign.Client;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;

/**
 * Created by wso2123 on 3/24/17.
 */
public class SenseUploadAsyncExecutor extends AsyncTask<String, Void, Map<String, String>> {
    private static final String STATUS = "status";

    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
    };

    Client disableHostnameVerification = new Client.Default(getTrustedSSLSocketFactory(), new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    });

    @Override
    protected Map<String, String> doInBackground(String... strings) {
        String endpoint = strings[0];
        String accessToken = strings[1];
        String deviceId = strings[2];
        String encodedImage = strings[3];
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(STATUS, "200");

        AndroidSenseManagerService androidSenseManagerService = Feign.builder().client(disableHostnameVerification)
                .requestInterceptor(new OAuthRequestInterceptor(accessToken))
                .contract(new JAXRSContract()).encoder(new JacksonEncoder()).decoder(new JacksonDecoder())
                .target(AndroidSenseManagerService.class, endpoint + SenseConstants.REGISTER_CONTEXT);
        androidSenseManagerService.register(deviceId, encodedImage);
//        androidSenseManagerService.upload(deviceId, encodedImage);

        return responseMap;
    }

    private SSLSocketFactory getTrustedSSLSocketFactory() {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            Log.e(SenseUploadAsyncExecutor.class.getName(), "Invalid Certificate");
            return null;
        }

    }
}
