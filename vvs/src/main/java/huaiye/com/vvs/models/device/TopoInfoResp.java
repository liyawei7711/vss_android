package huaiye.com.vvs.models.device;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * desc :设备拓扑信息
 * author: lxf
 * time : 2018-13-19
 */
public class TopoInfoResp implements Serializable  {
    public int nResultCode;
    public String strResultDescribe ;
    public ArrayList<TopoInfo> lstMeshDeviceTop;

    public static class TopoInfo{
        /**
         * srcdev : {"strIP":"192.168.2.3","strByname":"3#","nNoise":-99,"strMac":"1dsad"}
         * desdev : {"strIP":"192.168.2.1","strByname":"1#","nNoise":-105,"strMac":"1dsad"}
         * nSnr : 42
         * nQuality : 42
         * nNoise : 42
         * nSignal : 42
         * nRecvMode : 42
         * nSendMode : 42
         */

        public TopoDeviceInfo srcdev;
        public TopoDeviceInfo desdev;
        public int nSnr;
        public int nQuality;
        public int nNoise;
        public int nSignal;
        public int nRecvMode;
        public int nSendMode;

        public static class TopoDeviceInfo {
            /**
             * strIP : 192.168.2.3
             * strByname : 3#
             * nNoise : -99
             * strMac : 1dsad
             */

            public String strIP;
            public String strByname;
            public int nNoise;
            public String strMac;
        }


    }
}
