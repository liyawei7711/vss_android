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

public class VssMessageListBean implements Serializable {

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
    public String lastUserId;
    @Column
    public String lastUserDomain;
    @Column
    public String lastUserName;
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
    public int isRead;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getTime() {
        return AppUtils.getTimeHour(time);
    }
}
