package org.wso2.carbon.iot.android.sense.event.streams.sound;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.wso2.carbon.iot.android.sense.util.SenseDataHolder;

import java.io.IOException;
import java.util.Date;

/**
 * Created by wso2123 on 2/13/17.
 */
public class SoundDataReader extends AsyncTask<Void, Void, Long>{

    private SoundData soundData;
    private MediaRecorder mediaRecorder;
    private Handler mHandler = new Handler();

    public SoundDataReader(){
    }

    @Override
    protected Long doInBackground(Void... voids) {
        mediaRecorder = new MediaRecorder();
        soundData = new SoundData();
        try{
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile("/dev/null");
            mediaRecorder.prepare();
            mediaRecorder.start();

            mHandler.postDelayed(mRunnable, 5000);
        } catch (IOException e) {
            Log.e("Error", "Could not start media recorder");
        }
        return null;
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            soundData.setTimestamp(new Date().getTime());
            soundData.setAmplitude(mediaRecorder.getMaxAmplitude());
            SenseDataHolder.getSoundDataHolder().add(soundData);
            mHandler.postDelayed(mRunnable, 5000);
        }
    };
}
