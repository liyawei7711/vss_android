//package huaiye.com.vvs.common;
//
//import android.media.MediaPlayer;
//import android.os.Environment;
//
//import java.io.File;
//import java.io.FileInputStream;
//
//import huaiye.com.vvs.BuildConfig;
//import huaiye.com.vvs.dao.AppConstants;
//import huaiye.com.vvs.dao.AppDatas;
//import huaiye.com.vvs.dao.msgs.VssMessageBean;
//import huaiye.com.vvs.ui.chat.holder.ChatViewHolder;
//
///**
// * Created by hfx on 14-10-15.
// */
//public class MediaPlayUtil {
//    private static MediaPlayUtil mMediaPlayUtil;
//    private MediaPlayer mMediaPlayer;
//
//    private VideoPlayFailedCallBack mCallBack;
//    private VssMessageBean bean;
//    private ChatViewHolder viewHolder;
//
//    public ChatViewHolder getViewHolder() {
//        return viewHolder;
//    }
//
//    public void setLastState(VssMessageBean bean,ChatViewHolder viewHolder) {
//        this.bean = bean;
//        this.viewHolder = viewHolder;
//    }
//
//    public VssMessageBean getLastVssMessageBean() {
//        return bean;
//    }
//
//    public void setPlayOnCompleteListener(MediaPlayer.OnCompletionListener playOnCompleteListener) {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.setOnCompletionListener(playOnCompleteListener);
//        }
//    }
//    public void setPlayOnErrorListener(MediaPlayer.OnErrorListener playOnErrorListener) {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.setOnErrorListener(playOnErrorListener);
//        }
//    }
//
//    public static MediaPlayUtil getInstance() {
//        if (mMediaPlayUtil == null) {
//            mMediaPlayUtil = new MediaPlayUtil();
//        }
//        return mMediaPlayUtil;
//    }
//
//    public void setCallBack(VideoPlayFailedCallBack callBack) {
//        this.mCallBack = callBack;
//    }
//
//    private MediaPlayUtil() {
//        mMediaPlayer = new MediaPlayer();
//
//    }
//
//    public void play(String soundFilePath) {
//        if (mMediaPlayer == null) {
//            return;
//        }
//        try {
//            mMediaPlayer.reset();
//            String fileName=soundFilePath.substring(soundFilePath.lastIndexOf("/") + 1);
//            File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/audiovideo/"+fileName);
//            FileInputStream fis = new FileInputStream(file);
//            mMediaPlayer.setDataSource(fis.getFD());
//            mMediaPlayer.prepare();
//            mMediaPlayer.start();
//        } catch (Exception e) {
//
////            mCallBack.playFiled();
//            e.printStackTrace();
//        }
//    }
//
//    public interface VideoPlayFailedCallBack {
//        void playFiled();
//    }
//
//
//    public void pause() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.pause();
//        }
//    }
//
//    public void stop() {
//        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//            mMediaPlayer.stop();
//        }
//    }
//
//    public int getCurrentPosition() {
//        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//            return mMediaPlayer.getCurrentPosition();
//        } else {
//            return 0;
//        }
//    }
//
//    public int getDutation() {
//        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//            return mMediaPlayer.getDuration();
//        } else {
//            return 0;
//        }
//    }
//
//    public boolean isPlaying() {
//        if (mMediaPlayer != null) {
//            return mMediaPlayer.isPlaying();
//        } else {
//            return false;
//        }
//    }
//}
