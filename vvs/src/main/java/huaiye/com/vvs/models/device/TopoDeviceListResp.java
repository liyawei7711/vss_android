package huaiye.com.vvs.models.device;

import java.util.ArrayList;

/**
 * desc :自组网设备列表
 * author: lxf
 * time : 2018-13-19
 */
public class TopoDeviceListResp {
    public int nResultCode;
    public String strResultDescribe ;
    public ArrayList<DeviceInfo> lstAdDeviceInfo;



    public static class  DeviceInfo{

        /**
         * byname : test1
         * ip : 192.168.1.66
         * devtype : 1
         * noise : -56
         */

        public String byname;
        public String ip;
        public int devtype;
        public int noise;
    }

}
