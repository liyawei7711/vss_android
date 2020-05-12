package huaiye.com.vvs.bus;

import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;

import java.io.Serializable;

/**
 * author: admin
 * date: 2018/05/29
 * version: 0
 * mail: secret
 * desc: ChannelInvistor
 */

public class MeetInvistor implements Serializable {
    public CNotifyInviteUserJoinMeeting meet;
    public long millis;

    public MeetInvistor(CNotifyInviteUserJoinMeeting meet, long millis) {
        this.meet = meet;
        this.millis = millis;
    }
}
