package org.wso2.carbon.iot.android.sense.event.streams.sound;

import android.content.Intent;

import java.util.Date;

/**
 * Created by wso2123 on 2/8/17.
 */
public class SoundData {

    private double amplitude;
    private long timestamp;

//    SoundData(Intent intent){
//        timestamp = new Date().getTime();
//        amplitude = intent.getIntExtra(SoundManager.AMPLITUDE, 0);
//    }

    public SoundData(){

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
}
