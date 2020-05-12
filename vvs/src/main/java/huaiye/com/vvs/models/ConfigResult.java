package huaiye.com.vvs.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/08/13
 * version: 0
 * mail: secret
 * desc: GanXiBean
 */

public class ConfigResult implements Serializable {

    public int nResultCode;
    public String strResultDescribe;

    public ArrayList<Info> lstVssConfigParaInfo;

    public ArrayList<Info>  getLstVssConfigParaInfo() {
        return lstVssConfigParaInfo;
    }

    public void setLstVssConfigParaInfo(ArrayList<Info>  lstVssConfigParaInfo) {
        this.lstVssConfigParaInfo = lstVssConfigParaInfo;
    }

    public static class Info{
        String strVssConfigParaName;
        String strVssConfigParaValue;

        public String getStrVssConfigParaName() {
            return strVssConfigParaName;
        }

        public void setStrVssConfigParaName(String strVssConfigParaName) {
            this.strVssConfigParaName = strVssConfigParaName;
        }

        public String getStrVssConfigParaValue() {
            return strVssConfigParaValue;
        }

        public void setStrVssConfigParaValue(String strVssConfigParaValue) {
            this.strVssConfigParaValue = strVssConfigParaValue;
        }
    }

}
