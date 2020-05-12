package huaiye.com.vvs.dao;

import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.dao.auth.AppAuth;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: AppConstants
 */

public class AppConstants {
    final String DEFAULT_PUBLIC = "58.240.131.58";
    String strAddress = "192.168.1.66";
    int nPort = 8000;
    int nFilePort = 80;
    String strFileAddress = "192.168.1.66";

    String strAddressPublic;
    int nPortPublic;
    String strAddressPrivate;
    int nPortPrivate;

    protected AppConstants() {
        strAddress = SP.getParam("IP", strAddress).toString();
        nPort = Integer.parseInt(SP.getParam("Port", nPort).toString());

        strAddressPublic = SP.getParam("IP_PUBLIC", DEFAULT_PUBLIC).toString();
        nPortPublic = Integer.parseInt(SP.getParam("Port_PUBLIC", nPort).toString());

        strAddressPrivate = SP.getParam("IP_PRIVATE", strAddress).toString();
        nPortPrivate = Integer.parseInt(SP.getParam("Port_PRIVATE", nPort).toString());
    }

    public String getAddressIP() {
        return strAddress;
    }

    public int getAddressPort() {
        return nPort;
    }


    public String getStrAddressPublic() {
        return strAddressPublic;
    }

    public int getnPortPublic() {
        return nPortPublic;
    }

    public String getStrAddressPrivate() {
        return strAddressPrivate;
    }

    public int getnPortPrivate() {
        return nPortPrivate;
    }

    public AppConstants setAddress(String ip, int port) {
        strAddress = ip;
        nPort = port;

        SP.setParam("IP", strAddress);
        SP.setParam("Port", nPort);

        return this;
    }

    public AppConstants setAddressPublic(String ip,int port){
        strAddressPublic = ip;
        nPortPublic = port;
        SP.setParam("IP_PUBLIC", strAddressPublic);
        SP.setParam("Port_PUBLIC", nPortPublic);
        return this;
    }


    public AppConstants setAddressPrivate(String ip,int port){
        strAddressPrivate = ip;
        nPortPrivate = port;
        SP.setParam("IP_PRIVATE", strAddressPrivate);
        SP.setParam("Port_PRIVATE", nPortPrivate);
        return this;
    }

    public AppConstants setCurrentSelect(boolean isPublic){
        SP.setParam("currentSelectAddress", isPublic ? 0 : 1);
        return this;
    }


    public boolean isSelectPublic(){
        int selected = SP.getInteger("currentSelectAddress",0);
        if (selected == 0){
            return true;
        }else {
            return false;
        }
    }


    public String getSieAddress() {
        return "http://" + AppDatas.Constants().getAddressIP() + ":" + AppAuth.get().getSieHttpPort() + "/sie/httpjson/";
    }

    public String getAddressBaseURL() {
        return "http://" + strAddress + ":" + nPort + "/";
    }

    public String getFileAddressURL() {
        return "http://" + strFileAddress + ":" + nFilePort + "/";
    }

    public String getFileVoidAddressURL() {
        return "http://" + strFileAddress + ":" + nFilePort;
    }


    public String getAddressWithoutPort() {
        return "http://" + strAddress;
    }

    public String getAddressBaseURL9200() {
        return "http://" + strAddress + ":9200/";
    }

    public String getAddressBaseURLTarget() {
        return "http://" + strAddress + ":" + nPort + "/ECSFileServer/";
    }

    public void setFilePort(int nAppUpdatePort) {
        nFilePort = nAppUpdatePort;
    }

    public void setFileAddress(String strFileAddress) {
        this.strFileAddress = strFileAddress;
    }
}
