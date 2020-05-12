package huaiye.com.vvs.bus;

import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;

import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/05/29
 * version: 0
 * mail: secret
 * desc: CloseView
 */

public class CreateMeet {
    public ArrayList<CStartMeetingReq.UserInfo> sessionUserList;

    public CreateMeet(ArrayList<CStartMeetingReq.UserInfo> sessionUserList) {
        this.sessionUserList = sessionUserList;
    }
}
