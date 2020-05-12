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

public class DeviceBean implements Serializable {


    public String strName;

    //    {
//        "strMainUrl" : "AB0-CD1-EF2-GH3",
//            "strSubUrl" : "AB0-CD1-EF2-GH4",
//            "strName" = "xxx_channel1"
//    }
    public String strUrl;
    public String strMainUrl;
    public String strSubUrl;

}
