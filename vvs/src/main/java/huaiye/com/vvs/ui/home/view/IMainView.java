package huaiye.com.vvs.ui.home.view;

import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyGPSStatus;

import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.map.baidu.appcluster.MyCluster;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: IMainView
 */

public interface IMainView {

    void setLocation(MyLocationData data);

    void drawCover(MyCluster data);

    void animateMapStatus(MapStatusUpdate update);

    AppBaseActivity getContext();

    void clearMarkMark();

    void refCluster();
    void deleteCluster(MyCluster data);

    void notifyGPSInfoChange(String id, CNotifyGPSStatus cNotifyGPSStatus);



    Overlay addOverlay(OverlayOptions lineOption);
}
