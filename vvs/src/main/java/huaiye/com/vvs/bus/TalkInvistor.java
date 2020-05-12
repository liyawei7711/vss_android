package huaiye.com.vvs.bus;


import com.huaiye.cmf.sdp.SdpMsgCommonUDPMsg;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;

/**
 * author: admin
 * date: 2018/05/29
 * version: 0
 * mail: secret
 * desc: ChannelInvistor
 */

public class TalkInvistor {
    public CNotifyUserJoinTalkback talk;
    public SdpMsgCommonUDPMsg p2p_talk;
    public long millis;

    public TalkInvistor(CNotifyUserJoinTalkback talk, SdpMsgCommonUDPMsg p2p_talk, long millis) {
        this.talk = talk;
        this.millis = millis;
        this.p2p_talk = p2p_talk;
    }
}
