package huaiye.com.vvs.models.auth.bean;

import java.io.Serializable;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: AuthUser
 */

public class AuthUser implements Serializable {

    public int nResultCode;
    public String strResultDescribe;
    public int nRoleID;
    public int nPriority;

    public String strDomainCode;// : "34010000",
    public String strUserID;// : "34010000",
    public String strUserName;// : "34010000",
    public String strSieIP;// : "34010000",
    public String nSiePort;// : "34010000",
    public String nSieHttpPort;// : "34010000",
    public String password;// : "34010000",
    public String loginName;// : "34010000",
    public String strToken;// : "34010000",

}
