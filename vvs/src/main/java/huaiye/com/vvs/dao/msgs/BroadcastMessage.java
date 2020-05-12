package huaiye.com.vvs.dao.msgs;

import com.google.gson.Gson;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.CreateMeet;
import huaiye.com.vvs.bus.CreateTalkAndVideo;
import huaiye.com.vvs.bus.MeetInvistor;
import huaiye.com.vvs.bus.TalkInvistor;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.dao.AppDatas;
import ttyy.com.datasdao.annos.Column;

/**
 * author: admin
 * date: 2018/01/17
 * version: 0
 * mail: secret
 * desc: MessageData
 */
public class BroadcastMessage {


    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    public  static final int DOWNING = 3;

    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_VIDEO = 2;


    @Column
    int type;
    @Column
    int state;
    @Column
    String down_path;
    @Column
    String save_path;

    @Column
    long userId;
    @Column
    String domainCode;


    protected BroadcastMessage() {
        userId = AppDatas.Auth().getUserID();
        domainCode = AppDatas.Auth().getDomainCode();
    }

    public BroadcastMessage(int type,String path) {
        this.type = type;
        down_path = path;
    }


    public static BroadcastMessage downFile(VssMessageBean message) {
        BroadcastMessage data = new BroadcastMessage();
        data.type = message.type == AppUtils.BROADCAST_VIDEO_TYPE_INT?BroadcastMessage.TYPE_VIDEO:BroadcastMessage.TYPE_AUDIO;
        data.state = BroadcastMessage.DOWNING;
        data.down_path = message.content;

        return data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDown_path() {
        return down_path;
    }

    public void setDown_path(String down_path) {
        this.down_path = down_path;
    }

    public String getSave_path() {
        return save_path;
    }

    public void setSave_path(String save_path) {
        this.save_path = save_path;
    }
}
