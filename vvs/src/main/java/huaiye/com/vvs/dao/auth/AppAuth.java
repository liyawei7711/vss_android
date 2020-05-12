package huaiye.com.vvs.dao.auth;

import android.text.TextUtils;

import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.models.auth.bean.AuthUser;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: AppAuth
 */

public class AppAuth {
    public static final String PWD = "password";
    public static final String SIE_HTTP_PORT = "nSieHttpPort";

    private AppAuth() {

    }

    public void setData(String key, String strUserTokenID) {
        put(key, strUserTokenID);
    }

    public String getData(String key) {
        return get(key);
    }


    static class Holder {
        static final AppAuth SINGLETON = new AppAuth();
    }

    public static AppAuth get() {
        return Holder.SINGLETON;
    }

    public void setAuthUser(AuthUser user) {

        // 缓存
        put(PWD, user.password);
        put("strUserID", user.strUserID);
        put("strUserName", user.strUserName);
        put("loginName", user.loginName);
        put("strDomainCode", user.strDomainCode);
        put("nPriority", user.nPriority);

        put("strSieIP", user.strSieIP);
        put("nSiePort", user.nSiePort);
        put("strToken", user.strToken);
        put(SIE_HTTP_PORT, user.nSieHttpPort);
    }


    public String getToken() {
        return get("strToken");
    }

    public void put(String key, Object code) {
        SP.setParam(key, code);
    }

    private String get(String key) {
        return SP.getParam(key, "").toString();
    }

    private String get(String key, String def) {
        return SP.getParam(key, def).toString();
    }

    public String getPassword() {
        return get(PWD);
    }

    public long getUserID() {
        return Long.parseLong(get("strUserID"));
    }

    public int getPriority() {
        return SP.getInteger("nPriority", 0);
    }

    public String getUserName() {
        return get("strUserName");
    }

    public String getUserLoginName() {
        return get("loginName");
    }

    public String getDomainCode() {
        return get("strDomainCode");
    }

    public String getSieIP() {
        return get("strSieIP");
    }

    public int getSiePort() {
        return Integer.parseInt(get("nSiePort", "9000"));
    }

    public int getSieHttpPort() {
        return Integer.parseInt(get(SIE_HTTP_PORT, "8000"));
    }


    public void setEncryptPsw(String encryptPsw){
        String strUserID = get("strUserID");
        if (TextUtils.isEmpty(strUserID)){
            return ;
        }
        put(strUserID+"_encrypt_psw",encryptPsw);
    }
    public String getEncryptPsw(){
        String strUserID = get("strUserID");
        if (TextUtils.isEmpty(strUserID)){
            return "";
        }
        String psw = get(strUserID+"_encrypt_psw");
        return "12345678";
    }

}
