package org.wso2.carbon.iot.android.sense.event.streams.sound;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.iot.android.sense.constants.SenseConstants;
import org.wso2.carbon.iot.android.sense.data.publisher.mqtt.AndroidSenseMQTTHandler;
import org.wso2.carbon.iot.android.sense.data.publisher.mqtt.transport.MQTTTransportHandler;
import org.wso2.carbon.iot.android.sense.data.publisher.mqtt.transport.TransportHandlerException;
import org.wso2.carbon.iot.android.sense.util.LocalRegistry;

import java.util.Date;

/**
 * Created by wso2123 on 2/8/17.
 */
public class SoundDataPublisher {
    private static final String TAG = SoundDataPublisher.class.getName();

    private String user;
    private String deviceId;

    private double amplitude;
    private long timestamp;

    private Context context;

    public SoundDataPublisher(Context context) {
        this.context = context;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser() {
        this.user = LocalRegistry.getUsername(context);;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId() {
        this.deviceId = LocalRegistry.getDeviceId(context);;
    }

    public void publishSoundData() {
        JSONObject soundData = new JSONObject();
        JSONObject eventData = new JSONObject();

        try {
            JSONObject metadata = new JSONObject();
            metadata.put("owner", user);
            metadata.put("deviceId", deviceId);
            metadata.put("time", getTimestamp());
            eventData.put("metaData", metadata);

            JSONObject payloadData = new JSONObject();
            payloadData.put("amplitude", getAmplitude());
            eventData.put("payloadData", payloadData);

            MQTTTransportHandler mqttTransportHandler = AndroidSenseMQTTHandler.getInstance(context);
            if (!mqttTransportHandler.isConnected()) {
                mqttTransportHandler.connect();
            }
            soundData.put("event", eventData);
            String topic = LocalRegistry.getTenantDomain(context) + "/" + SenseConstants.DEVICE_TYPE + "/" + deviceId + "/sound";
            mqttTransportHandler.publishDeviceData(user, deviceId, soundData.toString(), topic);
        } catch (JSONException e) {
            Log.e(TAG, "Json Data Parsing Exception", e);
        } catch (TransportHandlerException e) {
            Log.e(TAG, "Data Publish Failed", e);
        }
    }
}
