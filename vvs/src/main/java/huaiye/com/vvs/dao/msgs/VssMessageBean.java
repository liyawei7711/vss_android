package huaiye.com.vvs.dao.msgs;

import com.google.gson.Gson;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import java.io.Serializable;
import java.util.ArrayList;

import huaiye.com.vvs.common.AppUtils;
import ttyy.com.datasdao.annos.Column;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: VssMessageBean
 */

public class VssMessageBean implements Serializable {

    @Column
    public int type;
    @Column
    public int groupType;
    @Column
    public String groupDomainCode;
    @Column
    public String groupID;
    @Column
    public String sessionID;
    @Column
    public String sessionName;
    @Column
    public String fromUserId;
    @Column
    public String fromUserTokenId;
    @Column
    public String fromUserDomain;
    @Column
    public String fromUserName;
    @Column
    public String content;
    @Column
    public ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
    @Column
    public long time;
    @Column
    public String ownerId;
    @Column
    public String ownerDomain;
    @Column
    public boolean isRead;
    @Column
    public int nEncrypt;

    public String contentSrc;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getTime() {
        return AppUtils.getTimeHour(time);
    }

    public boolean contactUser(String string) {
        if (sessionUserList == null)
            return false;
        for (SendUserBean user : sessionUserList) {
            if (user.strUserName.contains(string)) {
                return true;
            }
        }

        if (fromUserName.contains(string)) {
            return true;
        }
        return false;
    }

    public boolean contactSessionName(String string) {
        if (sessionName == null) return false;
        if (string == null) return false;
        if (sessionName.contains(string)) return true;
        return false;
    }

    public boolean contactFromUserName(String string) {
        if (fromUserName == null) return false;
        if (string == null) return false;
        if (fromUserName.contains(string)) return true;
        return false;
    }

    public boolean contactSessionId(String string) {
        if (fromUserId == null) return false;
        if (string == null) return false;
        if (fromUserId.contains(string)) return true;
        return false;

    }

    public boolean contactContent(String string) {
        if (content == null) return false;
        if (string == null) return false;
        if (content.contains(string)) return true;
        return false;
    }
}
