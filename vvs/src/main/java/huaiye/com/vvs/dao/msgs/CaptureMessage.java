package huaiye.com.vvs.dao.msgs;

/**
 * author: admin
 * date: 2018/05/30
 * version: 0
 * mail: secret
 * desc: CaptureMessage
 */

public class CaptureMessage {
    public String fromUserId;
    public String fromUserDomain;
    public String fromUserName;
    public String sessionID;

    public CaptureMessage(String fromUserId, String fromUserDomain, String fromUserName,String sessionID) {
        this.fromUserId = fromUserId;
        this.fromUserDomain = fromUserDomain;
        this.fromUserName = fromUserName;
        this.sessionID = sessionID;
    }
}
