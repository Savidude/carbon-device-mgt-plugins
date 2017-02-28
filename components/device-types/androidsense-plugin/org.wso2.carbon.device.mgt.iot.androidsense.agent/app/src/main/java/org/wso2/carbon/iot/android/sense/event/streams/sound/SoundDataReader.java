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

    private SoundDataPublisher soundDataPublisher;
    private MediaRecorder mediaRecorder;
    private Handler mHandler = new Handler();
    private Context context;

    public SoundDataReader(Context context){
        this.context = context;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        mediaRecorder = new MediaRecorder();
        soundDataPublisher = new SoundDataPublisher(context);

        soundDataPublisher.setUser();
        soundDataPublisher.setDeviceId();

        try{
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile("/dev/null");
            mediaRecorder.prepare();
            mediaRecorder.start();

            mHandler.postDelayed(mRunnable, 2000);
        } catch (IOException e) {
            Log.e("Error", "Could not start media recorder");
        }
        return null;
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaRecorder != null){
                soundDataPublisher.setTimestamp(new Date().getTime());
                soundDataPublisher.setAmplitude(mediaRecorder.getMaxAmplitude());
                soundDataPublisher.publishSoundData();
                mHandler.postDelayed(mRunnable, 2000);
            }
        }
    };

    public void stopSoundDataReader(){
        if (mediaRecorder != null){
            mediaRecorder.stop();
            mediaRecorder = null;
        }
    }
}
