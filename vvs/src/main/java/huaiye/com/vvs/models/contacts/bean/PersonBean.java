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

public class PersonBean implements Serializable {
    public int nResultCode;
    public int nTotalSize;
    public String strResultDescribe;
    public ArrayList<PersonModelBean> userList = new ArrayList<>();
}
