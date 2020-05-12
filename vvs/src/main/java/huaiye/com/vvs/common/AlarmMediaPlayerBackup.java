package huaiye.com.vvs.common;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;

import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.dao.msgs.VssMessageBean;
import huaiye.com.vvs.ui.chat.holder.ChatViewHolder;

/**
 * 要注意AppAudioManager,start开始之后再使用AppAudioManager会造成状态混乱
 */
public class AlarmMediaPlayerBackup {
    public static final int CALL_VOICE = 1;
    public static final int PTT_VOICE = 2;
    public static final int ALARM_VOICE = 3;
    public static final int ERROR_VOICE = 4;
    public static final int SOS_VOICE = 5;
    public static final int PERSON_VOICE = 6;
    public static final int ZHILLING_VOICE = 7;


    private MediaPlayer mMediaPlayer;
    private MediaPlayer mBroadcastPlayer;
    private Context mContext;

    private String TAG = AlarmMediaPlayerBackup.class.getSimpleName();

    private static AlarmMediaPlayerBackup mInstance;
    private boolean isPlaying;
    private BroadcastListener broadcastListener;
    AudioManager mAudioManager ;

    private int previousMode;
    private boolean previousSpeakon;
    public static AlarmMediaPlayerBackup get() {
        if (mInstance == null) {
            synchronized (AlarmMediaPlayerBackup.class) {
                if (mInstance == null) {
                    mInstance = new AlarmMediaPlayerBackup(MCApp.getInstance());
                }
            }
        }
        return mInstance;
    }

    private AlarmMediaPlayerBackup(Context context) {
        this.mContext = context;
        mAudioManager = (AudioManager) MCApp.getInstance().getSystemService(Context.AUDIO_SERVICE);
    }

    public void play(int type) {

        Logger.log(TAG + " Play start " + type);
        if (mMediaPlayer == null) {
            Logger.log(TAG + " Play create " + type);
            mMediaPlayer = createPlayer();
        }

        //多次播放只播放第一次,其他的不进行操作
        if (mMediaPlayer.isPlaying() || isPlaying) {
            Logger.log(TAG + " Play isAlarmPlaying" + type);
            return;
        }

        Logger.log(TAG + " Play will play " + type);

        requestFocus();
        try {
            setPlayerSource(mMediaPlayer, type);
            mMediaPlayer.prepare();
            //每次都从头开始,这样提示音就是一致的
            mMediaPlayer.seekTo(0);
            isPlaying = true;
            mMediaPlayer.start();

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Logger.log(TAG + " Play onCompletion");
                    stop();
                    mAudioManager.abandonAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                        @Override
                        public void onAudioFocusChange(int focusChange) {
                            Logger.log(TAG + " mAudioManager abandonAudioFocus " + focusChange);
                        }
                    });
                }
            });

        } catch (IOException e) {
            Logger.log(TAG + " Play error " + e);
            e.printStackTrace();
        }
    }

    /**
     * 播放暂停,每次都释放重新创建
     */
    public void stop() {
        isPlaying = false;

        if (mMediaPlayer != null) {
            releaseFocus();
            Logger.log(TAG + " Play stop not null " + mMediaPlayer.isPlaying());
            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 必须调用destroy来释放MediaPlayer
     */

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    private MediaPlayer createPlayer() {
        MediaPlayer player = new MediaPlayer();
        //接通时前，音频播放走媒体音量
        if (Build.VERSION.SDK_INT >= 21) {
            AudioAttributes.Builder builder = new AudioAttributes.Builder();
            builder.setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);
            player.setAudioAttributes(builder.build());
        } else {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        return player;

    }

    private void setPlayerSource(MediaPlayer player, int type) {
        AssetManager am = mContext.getAssets();//获得该应用的AssetManager
        AssetFileDescriptor afd = null;

        try {
            switch (type) {
                case CALL_VOICE:
                    afd = am.openFd("call.wav");
                    player.setLooping(true); //循环播放
                    break;
                case SOS_VOICE:
                    afd = am.openFd("sos.wav");
                    player.setLooping(true);
                    break;
                case PERSON_VOICE:
                    afd = am.openFd("person.wav");
                    player.setLooping(false);
                    break;
                case ZHILLING_VOICE:
                    afd = am.openFd("zhiling.wav");
                    player.setLooping(false);
                    break;
                case ERROR_VOICE:
                    afd = am.openFd("error.wav");
                    player.setLooping(false);
                    break;
                case PTT_VOICE:
                    afd = am.openFd("ptt.wav");
                    player.setLooping(false);
                    break;
                case ALARM_VOICE:
                    afd = am.openFd("alarm.wav");
                    player.setLooping(false);
                    break;

            }
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                    afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private VssMessageBean bean;
    private ChatViewHolder viewHolder;
    private VideoParams videoParams;
    private HashMap<String,Boolean> playMap = new HashMap<>();

    public HashMap<String,Boolean> getPlayMap(){
        return playMap;
    }

    public ChatViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setLastState(VssMessageBean bean, ChatViewHolder viewHolder) {
        this.bean = bean;
        this.viewHolder = viewHolder;
    }
    public void setVideoParam(VideoParams videoParams){
        this.videoParams=videoParams;
    }
    public VideoParams getVideoParam(){
        return videoParams;
    }

    public VssMessageBean getLastVssMessageBean() {
        return bean;
    }

    public void setPlayBroadcastListener(BroadcastListener broadcastListener) {
        this.broadcastListener = broadcastListener;
    }

    public void playBroadcast(String soundFilePath) {
        Logger.log(TAG + " mBroadcastPlayer start " + soundFilePath);
        if (mBroadcastPlayer == null) {
            Logger.log(TAG + " mBroadcastPlayer create " + soundFilePath);
            mBroadcastPlayer = createPlayer();
        }
        Logger.log(TAG + " mBroadcastPlayer will play " + soundFilePath);

        try {
            File file = new File(AppUtils.audiovideoPath + "/" + AppUtils.subPath(soundFilePath));
            FileInputStream fis = new FileInputStream(file);
            mBroadcastPlayer.setDataSource(fis.getFD());
            mBroadcastPlayer.prepare();
            //每次都从头开始,这样提示音就是一致的
            mBroadcastPlayer.seekTo(0);
            mBroadcastPlayer.start();

            mBroadcastPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Logger.log(TAG + " mBroadcastPlayer onCompletion");
                    if (broadcastListener != null)
                        broadcastListener.completeListener();
                }
            });

        } catch (IOException e) {
            Logger.log(TAG + " mBroadcastPlayer error " + e);
            if (broadcastListener != null)
                broadcastListener.error();
            e.printStackTrace();
        }
    }


    private void requestFocus(){
        mAudioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                Logger.log(TAG + " mAudioManager requestAudioFocus " + focusChange);
            }
        },AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        previousMode = mAudioManager.getMode();
        previousSpeakon = mAudioManager.isSpeakerphoneOn();
        mAudioManager.setMode(AudioManager.MODE_RINGTONE);
        mAudioManager.setSpeakerphoneOn(true);
    }

    private void releaseFocus(){
        mAudioManager.setMode(previousMode);
        mAudioManager.setSpeakerphoneOn(previousSpeakon);
    }

    public void stopBroadcast() {
        if (mBroadcastPlayer != null) {
            Logger.log(TAG + " mBroadcastPlayer stop not null " + mBroadcastPlayer.isPlaying());
            mBroadcastPlayer.pause();
            mBroadcastPlayer.stop();
            mBroadcastPlayer.reset();
            mBroadcastPlayer.release();
            mBroadcastPlayer = null;
        }
    }

    public boolean isPlayingBroadcast() {
        if (mBroadcastPlayer != null) {
            return mBroadcastPlayer.isPlaying();
        } else {
            return false;
        }
    }

    public interface BroadcastListener {
        void completeListener();

        void error();
    }
}
