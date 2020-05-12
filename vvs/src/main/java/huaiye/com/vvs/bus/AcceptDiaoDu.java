package huaiye.com.vvs.bus;

import com.huaiye.cmf.sdp.SdpMsgCommonUDPMsg;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;

/**
 * author: admin
 * date: 2018/05/29
 * version: 0
 * mail: secret
 * desc: CloseView
 */

public class AcceptDiaoDu {
    public CNotifyInviteUserJoinMeeting meetData;
    public CNotifyUserJoinTalkback talkData;
    public SdpMsgCommonUDPMsg udpMsg;
    public long millis;

    public AcceptDiaoDu(CNotifyInviteUserJoinMeeting meetData, CNotifyUserJoinTalkback talkData, SdpMsgCommonUDPMsg udpMsg, long millis) {
        this.meetData = meetData;
        this.talkData = talkData;
        this.udpMsg = udpMsg;
        this.millis = millis;
    }
}
