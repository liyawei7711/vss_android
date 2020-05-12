package huaiye.com.vvs.models.contacts.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: MarkBean
 */

public class SosPersonBean implements Serializable {
    public int nResultCode;
    public String strResultDescribe;
    public ArrayList<SosPersonModelBean> seekHelpInfoList = new ArrayList<>();
}
