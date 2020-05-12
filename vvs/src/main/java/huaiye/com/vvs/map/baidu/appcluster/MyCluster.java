package huaiye.com.vvs.map.baidu.appcluster;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.model.LatLng;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.map.baidu.clusterutil.clustering.ClusterItem;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.map.bean.BDMarkBean;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: MyCluster
 * 这个对象包含了好几个位置对象
 * 1 bean.latLng 是百度的定位信息,用来在地图上显示和展示给用户看的
 * 2 MarkModelBean,PersonModelBean等中的latitude longitude是服务器返回的GPS坐标,不过也可能被转换过
 * 所以定位信息需要注意
 */

public class MyCluster implements ClusterItem {
    public final BDMarkBean bean;
    public String str;

    public MyCluster(BDMarkBean bean) {
        this.bean = bean;
    }

    public MyCluster setShow(String str) {
        this.str = str;
        return this;
    }

    @Override
    public LatLng getPosition() {
        return bean.latLng;
    }

    @Override
    public BitmapDescriptor getBitmapDescriptor() {
        if (bean.deviceBean != null) {
            if (bean.deviceBean.nOnlineState == 1){
                return BitmapDescriptorFactory
                        .fromResource(R.drawable.ditubofang);
            }else {
                return BitmapDescriptorFactory
                        .fromResource(R.drawable.ditubofang_lixian);
            }
        } else if (bean.personModelBean != null) {
            if (bean.personModelBean.nDevType == AppUtils.DevType_ANDROID ||
                    bean.personModelBean.nDevType == AppUtils.DevType_IOS) {
                if (bean.personModelBean.nStatus == PersonModelBean.STATUS_OFFLINE) {
                    return BitmapDescriptorFactory.fromResource(R.drawable.lixian);
                } else if (bean.personModelBean.nStatus == PersonModelBean.STATUS_ONLINE_IDLE) {
                    return BitmapDescriptorFactory.fromResource(R.drawable.kongxian);
                } else {
                    return BitmapDescriptorFactory.fromResource(R.drawable.manglu);
                }
            } else {
                if (bean.personModelBean.nStatus == 0) {
                    return BitmapDescriptorFactory.fromResource(R.drawable.diannaolixian);
                } else if (bean.personModelBean.nStatus == 1) {
                    return BitmapDescriptorFactory.fromResource(R.drawable.diannaozaixian);
                } else {
                    return BitmapDescriptorFactory.fromResource(R.drawable.diannaomanglu);
                }
            }
        } else if (bean.domainModelBean != null) {
            return BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_openmap_focuse_mark);
        } else if (bean.p2pDeviceBean != null) {
            return BitmapDescriptorFactory.fromResource(R.drawable.kongxian);
        } else {
            return BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_gcoding);
        }

    }
}
