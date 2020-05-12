package huaiye.com.vvs.dao.msgs;

import com.google.gson.Gson;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyKickUserMeeting;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import huaiye.com.vvs.R;
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
public class MessageData {
    static final Gson gson = new Gson();

    public static final int AUTH_KICKOUT = 8; //

    public static final int MEET_INVITE_JISHI = 1;
    public static final int MEET_INVITE_QUXIAO = 38;
    public static final int MEET_KICKOUT = 2;
    public static final int MEET_SPEAKER_CONTROL = 6;

    public static final int TALK_INVITE = 4;//对讲邀请
    public static final int TALK_SPEAKER_CONTROL = 7;

    @Column
    int nMessageType;
    @Column
    String strTitle;
    @Column
    String strContent;
    @Column
    public long nMillions;

    @Column
    String strMessageJson;

    @Column
    long userId;
    @Column
    String domainCode;
    @Column
    String key;
    @Column
    int isRead;

    protected MessageData() {
        userId = AppDatas.Auth().getUserID();
        domainCode = AppDatas.Auth().getDomainCode();
        dateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeSdf = new SimpleDateFormat("HH:mm:ss");
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public boolean getIsRead() {
        return isRead == 1;
    }

//    public static MessageData from(CNotifyUserKickout message) {
//        MessageData data = new MessageData();
//
//        data.nMessageType = AUTH_KICKOUT;
//        data.nMillions = System.currentTimeMillis();
//        data.strTitle = AppUtils.getString(R.string.load_kitout);
//        data.strContent = AppUtils.getString(R.string.other_load);
//
//        data.strMessageJson = gson.toJson(message);
//        data.key = "authkickout" + message.strUserID + "@" + message.strMacAddr;
//
//        return data;
//    }

    public static MessageData from(CNotifyKickUserMeeting message) {
        MessageData data = new MessageData();

        data.nMessageType = MEET_KICKOUT;
        data.nMillions = System.currentTimeMillis();
        data.strTitle = AppUtils.getString(R.string.meet_kitout);
        data.strContent = String.format(AppUtils.getString(R.string.meet_kitout_ed), message.strMeetingName);

        data.strMessageJson = gson.toJson(message);
        data.key = "meetkickout" + message.nMeetingID + "@" + message.strMeetingName;

        return data;
    }

    public static MessageData from(CNotifyUserJoinTalkback message, long millions) {
        MessageData data = new MessageData();

        data.nMessageType = TALK_INVITE;
        data.nMillions = millions;
        data.strTitle = AppUtils.getString(R.string.invisitor);
        data.strContent = String.format(AppUtils.getString(R.string.talk_invistor_miss), message.strFromUserName, message.strTalkbackStartTime);
        data.strMessageJson = gson.toJson(message);
        data.key = "talkinvite" + message.nTalkbackID + "@" + message.strTalkbackDomainCode;

        return data;
    }

    public static MessageData from(CNotifyInviteUserJoinMeeting message, long millions) {
        MessageData data = new MessageData();

        data.nMillions = millions;
        if (message.nMeetingType == 0) {
            data.nMessageType = MEET_INVITE_JISHI;
            data.strTitle = message.strMeetingName;
            data.strContent = String.format(AppUtils.getString(R.string.meet_invistor_miss), message.strInviteUserName, message.strMeetingStartTime);
        } else {
            data.nMessageType = MEET_INVITE_QUXIAO;
            data.strTitle = message.strMeetingName;
            data.strContent = String.format(AppUtils.getString(R.string.meet_invistor_attend), message.strInviteUserName, message.strMeetingStartTime);
        }

        data.strMessageJson = gson.toJson(message);
        data.key = "meetinvite" + message.nMeetingID + "@" + message.strMeetingDomainCode;

        return data;
    }

    public String getTitle() {
        return strTitle;
    }

    public String getContent() {
        return strContent;
    }

    public String getMessageJson() {
        return strMessageJson;
    }

    SimpleDateFormat dateSdf;
    SimpleDateFormat timeSdf;

    public String getDate() {
        Calendar now = Calendar.getInstance();

        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(nMillions);
        Date timeDate = new Date(nMillions);

        int dateOffset = now.get(Calendar.DAY_OF_YEAR) - time.get(Calendar.DAY_OF_YEAR);
        switch (dateOffset) {
            case 0:

                return timeSdf.format(timeDate);
            case 1:

                return AppUtils.getString(R.string.zuotian) + timeSdf.format(timeDate);
            case 2:

                return AppUtils.getString(R.string.qiantian) + timeSdf.format(timeDate);
            default:

                return dateSdf.format(timeDate);
        }
    }

    public int getMessageType() {
        return nMessageType;
    }

    public int getIconResource() {
        switch (nMessageType) {
            case MEET_INVITE_JISHI:
                return R.drawable.icon_liebiao_jishihuiyi;
            case TALK_INVITE:
                return R.drawable.btn_touxiang_cebianlan;
            case MEET_KICKOUT:
            case AUTH_KICKOUT:
                return R.drawable.ic_person_kickout;
        }

        return R.drawable.ic_group;
    }

}
