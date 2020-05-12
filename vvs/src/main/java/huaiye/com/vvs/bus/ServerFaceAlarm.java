package huaiye.com.vvs.bus;

import com.huaiye.cmf.sdp.SdpMsgFRAlarmNotify;
import com.huaiye.sdk.sdpmsgs.face.CServerNotifyAlarmInfo;

/**
 * author: admin
 * date: 2018/05/29
 * version: 0
 * mail: secret
 * desc: LocalFaceAlarm
 */

public class ServerFaceAlarm {

    private final CServerNotifyAlarmInfo cServerNotifyAlarmInfo;

    public ServerFaceAlarm(CServerNotifyAlarmInfo cServerNotifyAlarmInfo) {
        this.cServerNotifyAlarmInfo = cServerNotifyAlarmInfo;
    }
}
