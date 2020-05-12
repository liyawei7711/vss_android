package huaiye.com.vvs.models.device.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DeviceListBean
 */

public class DeviceListBean implements Serializable {


    public int nResultCode;
    public int nTotalSize;
    public String strResultDescribe;
    public ArrayList<DeviceBean> deviceList;


    public static class DeviceBean implements Serializable {
        public String strDeviceCode;
        public String strDeviceName;
        public String strDomainCode;
        public String strDevIP;

        public int nDevPort;
        public int nDevFormType;
        public int nDevTransferType;
        public int nDevProtocolType;
        public int nOnlineState;


        public ArrayList<ChannelBean> channelList;
    }

    public static class ChannelBean implements Serializable {
        public String strChannelCode;
        public String strChannelName;
        public String strDeviceCode;
        public String strPosition;
        public String strChannelToken;
        public String strDomainCode;

        public int nChannelType;
        public double fLatitude;
        public double fLongitude;
//        public long fHeight;

        public ArrayList<StreamBean> streamList;
    }

    public static class StreamBean implements Serializable {
        public String strStreamCode;
        public String strStreamName;
        public int nStreamType;
    }

}
