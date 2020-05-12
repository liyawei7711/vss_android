package huaiye.com.vvs.push;


import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;

import com.huaiye.sdk.logger.Logger;

import huaiye.com.vvs.MCApp;

/**
 * 音量监听
 */
public class VolumeObserver extends ContentObserver {


    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public VolumeObserver(Handler handler) {
        super(handler);

    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        MCApp app = MCApp.getInstance();
        if (app == null){
            return;
        }
        AudioManager audioManager = (AudioManager) app.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int currentVolumeCall = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        int mode = audioManager.getMode();
        if (mode == AudioManager.MODE_IN_COMMUNICATION){
            int callVolume = audioManager.getStreamVolume( AudioManager.STREAM_VOICE_CALL );
            int maxCallVolume = audioManager.getStreamMaxVolume( AudioManager.STREAM_VOICE_CALL);
            int maxMusic = audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC);
            int needMusic = callVolume * maxMusic/  maxCallVolume ;
            //使用flag 0 可以不会有声音震动的反馈
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,needMusic,0);
            Logger.debug("VolumeObserver " + " callVolume " + callVolume + " maxCallVolume " + maxCallVolume + " maxMusic " + maxMusic + " needMusic   " + needMusic);

        }
//        Logger.debug("VolumeObserver currVolume:" + currentVolume + " currentVolumeCall " + currentVolumeCall +  " mode " + mode);
    }
}
