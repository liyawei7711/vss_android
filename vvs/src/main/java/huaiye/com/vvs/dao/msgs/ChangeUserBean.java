package huaiye.com.vvs.dao.msgs;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: VssMessageBean
 */

public class ChangeUserBean implements Serializable {


    @SerializedName("strUserID")
    public String strModifyUserID;
    public int nPriority;
    @SerializedName("strDomainCode")
    public String strModifyUserDomainCode;
    @SerializedName("strUserName")
    public String strModifyUserName;

}
