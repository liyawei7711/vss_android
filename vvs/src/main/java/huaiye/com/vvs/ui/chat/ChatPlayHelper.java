package huaiye.com.vvs.ui.chat;

import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;

import java.util.HashMap;

public class ChatPlayHelper {
    private static ChatPlayHelper instance = new ChatPlayHelper();
    public static final int INVALID_POSITION = -1;


    private VideoParams videoParams;
    private HashMap<String,Boolean> playMap = new HashMap<>();
    private int lastItemPosition = INVALID_POSITION;

    public HashMap<String,Boolean> getPlayMap(){
        return playMap;
    }


    public void setVideoParam(VideoParams videoParams){
        this.videoParams=videoParams;
    }
    public VideoParams getVideoParam(){
        return videoParams;
    }

    public void setLastItemPosition(int lastItemPosition) {
        this.lastItemPosition = lastItemPosition;
    }

    public int getLastItemPosition() {
        return lastItemPosition;
    }

    public static ChatPlayHelper get(){
        return instance;
    }
}
