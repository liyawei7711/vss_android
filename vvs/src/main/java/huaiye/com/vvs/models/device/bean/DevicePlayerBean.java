package huaiye.com.vvs.models.device.bean;

import java.io.Serializable;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DeviceListBean
 */

public class DevicePlayerBean implements Serializable {

    public String strChannelName;

    public String strChannelCode;
    public String strDeviceCode;
    public String strStreamCode;
    public String strSubStreamCode;
    public String strDomainCode;
    public int nOnlineState;

    public double fLatitude;
    public double fLongitude;
    public boolean isSelected;

    @Override
    public String toString() {
        return "DevicePlayerBean{" +
                "strChannelName='" + strChannelName + '\'' +
                ", strChannelCode='" + strChannelCode + '\'' +
                ", strDeviceCode='" + strDeviceCode + '\'' +
                ", strStreamCode='" + strStreamCode + '\'' +
                ", fLatitude='" + fLatitude + '\'' +
                ", fLongitude='" + fLongitude + '\'' +
                ", strDomainCode='" + strDomainCode + '\'' +
                ", fLatitude=" + fLatitude +
                ", fLongitude=" + fLongitude +
                ", strSubStreamCode=" + strSubStreamCode +
                '}';
    }
}
