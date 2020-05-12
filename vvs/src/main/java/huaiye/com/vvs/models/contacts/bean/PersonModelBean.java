package huaiye.com.vvs.models.contacts.bean;

import java.io.Serializable;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: MarkBean
 */

public class PersonModelBean implements Serializable {
    public final static int STATUS_OFFLINE        = 0;

    public final static int STATUS_ONLINE_IDLE = 1;
    public final static int STATUS_ONLINE_CAPTURING = 2;
    public final static int STATUS_ONLINE_TALKING = 3;
    public final static int STATUS_ONLINE_MEETING = 4;
    public final static int STATUS_ONLINE_TRUNK_SPEAKING = 5;

    public String strUserID;
    public String strUserTokenID;
    public String strLoginName;
    public String strUserName;
    public int nRoleType;
    public int nDevType;
    public int nPriority;
    public int nSex;
    public int nSpeaking;
    public int nTrunkChannelID;
    public String strTrunkChannelDomainCode;
    public String strTrunkChannelName;
    public String strMobilePhone;
    public String strLastLoginTime;
    public String strRemark;
    public int nStatus;
    public double dLongitude;
    public double dLatitude;
    public double dHeight;
    public double dSpeed;
    public String strCollectTime;
    public String strDomainCode;
    public boolean isSelected;

    public String addressDetail;
    public String addressDistance;

    @Override
    public String toString() {
        return "PersonModelBean{" +
                "strUserID='" + strUserID + '\'' +
                ", strLoginName='" + strLoginName + '\'' +
                ", strUserName='" + strUserName + '\'' +
                ", nRoleType=" + nRoleType +
                ", nPriority=" + nPriority +
                ", nSex=" + nSex +
                ", strMobilePhone='" + strMobilePhone + '\'' +
                ", strLastLoginTime='" + strLastLoginTime + '\'' +
                ", strRemark='" + strRemark + '\'' +
                ", nStatus=" + nStatus +
                ", dLongitude=" + dLongitude +
                ", dLatitude=" + dLatitude +
                ", dHeight=" + dHeight +
                ", dSpeed=" + dSpeed +
                ", strCollectTime='" + strCollectTime + '\'' +
                ", strDomainCode='" + strDomainCode + '\'' +
                '}';
    }
}
