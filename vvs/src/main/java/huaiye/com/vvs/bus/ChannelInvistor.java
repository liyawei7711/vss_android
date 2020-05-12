package huaiye.com.vvs.bus;

import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CNotifyUserInviteTrunkChannel;

/**
 * author: admin
 * date: 2018/05/29
 * version: 0
 * mail: secret
 * desc: ChannelInvistor
 */

public class ChannelInvistor {
    public CNotifyUserInviteTrunkChannel channel;
    public long millis;

    public ChannelInvistor(CNotifyUserInviteTrunkChannel channel, long millis) {
        this.channel = channel;
        this.millis = millis;
    }
}
