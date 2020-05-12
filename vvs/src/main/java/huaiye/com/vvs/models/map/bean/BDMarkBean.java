package huaiye.com.vvs.models.map.bean;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.model.LatLng;
import com.huaiye.cmf.sdp.SdpMsgFindLanCaptureDeviceRsp;
import com.huaiye.sdk.sdpmsgs.device.CGetDomainListRsp;

import java.io.Serializable;

import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.device.bean.DevicePlayerBean;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: BDMarkBean
 */

public class BDMarkBean implements Serializable {
    public LatLng latLng = new LatLng(31.988401, 118.779742);

    public MarkModelBean markModelBean;
    public PersonModelBean personModelBean;
    public CGetDomainListRsp.DomainList domainModelBean;
    public DevicePlayerBean deviceBean;
    public SdpMsgFindLanCaptureDeviceRsp p2pDeviceBean;

    @Override
    public String toString() {
        return "BDMarkBean{" +
                "latLng=" + latLng +
                ", markModelBean=" + markModelBean +
                ", personModelBean=" + personModelBean +
                ", deviceBean=" + deviceBean +
                ", p2pDeviceBean=" + p2pDeviceBean +
                '}';
    }

    public String toLat() {
        return "latLng=" + latLng ;
    }
}
