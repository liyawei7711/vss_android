package huaiye.com.vvs.ui.home.present;

import android.graphics.Color;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.model.LatLng;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.core.SdkCaller;
import com.huaiye.sdk.core.SdkNotifyCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiDevice;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyGPSStatus;
import com.huaiye.sdk.sdpmsgs.device.CGetDomainListRsp;
import com.huaiye.sdk.sdpmsgs.face.CServerNotifyAlarmInfo;
import com.huaiye.sdk.sdpmsgs.social.CNotifyUserStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AlarmMediaPlayer;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.ChangeUserBean;
import huaiye.com.vvs.dao.msgs.MapMarkBean;
import huaiye.com.vvs.dao.msgs.PointBean;
import huaiye.com.vvs.map.baidu.GPSLocation;
import huaiye.com.vvs.map.baidu.appcluster.MyCluster;
import huaiye.com.vvs.map.baidu.utils.MapListener;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.contacts.ContactsApi;
import huaiye.com.vvs.models.contacts.bean.PersonBean;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.device.DeviceApi;
import huaiye.com.vvs.models.device.TopoDeviceGPS;
import huaiye.com.vvs.models.device.bean.DeviceListBean;
import huaiye.com.vvs.models.device.bean.DevicePlayerBean;
import huaiye.com.vvs.models.map.MapApi;
import huaiye.com.vvs.models.map.bean.BDMarkBean;
import huaiye.com.vvs.models.map.bean.MarkModelBean;
import huaiye.com.vvs.ui.home.view.IMainView;
import huaiye.com.vvs.ui.home.view.TopoDialog;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vvs.common.AppUtils.showToast;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.BOARD;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.CIRCLE;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.LINE;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.MANYBOARD;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.POINT;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: MainPresent
 */

public class MainPresent {
    IMainView iMainView;

    ArrayList<CGetDomainListRsp.DomainList> domainList = new ArrayList<>();
    ArrayList<DevicePlayerBean> deviceList = new ArrayList<>();
    ArrayList<PersonModelBean> userList = new ArrayList<>();

    ArrayList<MyCluster> personCluster = new ArrayList<>();
    ArrayList<MyCluster> deviceCluster = new ArrayList<>();
    ArrayList<MyCluster> domainCluster = new ArrayList<>();

    Map<Integer, List<Overlay>> allOverlay = new HashMap<>();
    /**
     * 用户画的标记点
     */
    ArrayList<MarkModelBean> markInfoList = new ArrayList<>();
    /**
     * 通过markInfoList设置到界面上
     */
    ArrayList<MyCluster> markCluster = new ArrayList<>();


    ArrayList<Overlay> topoOverlay = new ArrayList<>();

    boolean isFirst = true;

    long mitts;
    private Location location;
    SdkNotifyCallback<CServerNotifyAlarmInfo> deviceStatusListener;
    SdkCaller userObserverCaller;

    public Location getLocation() {
        return location;
    }

    public MainPresent(final IMainView iMainView) {
        this.iMainView = iMainView;
        DeviceApi.get().startDeviceListener();
        deviceStatusListener = new SdkNotifyCallback<CServerNotifyAlarmInfo>() {
            @Override
            public void onEvent(final CServerNotifyAlarmInfo data) {
                boolean hasThis = false;
                for (MyCluster temp : deviceCluster) {
                    if (temp.bean != null && temp.bean.deviceBean != null && temp.bean.deviceBean.strDeviceCode.equals(data.strDeviceCode)){
                        hasThis = true;
                        //当前是在线的,但新的通知是离线,就删除该点
                        if (data.nAlarmType == DeviceApi.NOTIFY_TYPE_DEIVCE_OFFLINE){
                            temp.bean.deviceBean.nOnlineState = 2;
                            iMainView.refCluster();

                        }else {
                            temp.bean.deviceBean.nOnlineState = 1;
                            iMainView.refCluster();
                        }

                    }
                }

                //当前集群点没有,且通知的设备是上线了,就刷新下
                if (!hasThis && data.nAlarmType == DeviceApi.NOTIFY_TYPE_DEIVCE_ONLINE) {
                    loadDevice();
                }
            }
        };

        DeviceApi.get().addAlarmListener(deviceStatusListener);
        // 监听 用户状态
        userObserverCaller = HYClient.getModule(ApiSocial.class).observeUserStatus(new SdkNotifyCallback<CNotifyUserStatus>() {
            @Override
            public void onEvent(final CNotifyUserStatus data) {
                Logger.debug("MainPresenter observeUserStatus " + data.toString());
                if (HYClient.getSdkOptions().P2P().isP2PRunning()) {
                    Logger.log("observeUserStatus 当前是p2p模式");
                    return;
                }
                boolean hasThis = false;
                for (MyCluster temp : personCluster) {
                    if (temp.bean.personModelBean != null && temp.bean.personModelBean.strUserID.equals(data.strUserID)) {
                        hasThis = true;
                        temp.bean.personModelBean.nDevType = data.nDevType;
                        temp.bean.personModelBean.strUserName = data.strUserName;
                        temp.bean.personModelBean.nTrunkChannelID = data.nTrunkChannelID;
                        temp.bean.personModelBean.strTrunkChannelName = data.strTrunkChannelName;
                        temp.bean.personModelBean.nSpeaking = data.nSpeaking;
                        if (data.isOnline()) {
                            temp.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_IDLE;
                            if (data.isCapturing()) {
                                temp.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_CAPTURING;
                            }
                            if (data.isTalking()) {
                                temp.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_TALKING;
                            }
                            if (data.isMeeting()) {
                                temp.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_MEETING;
                            }
                            if (data.isTrunkSpeaking()){
                                temp.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_TRUNK_SPEAKING;
                            }
                        } else {
                            //当前人员列表里面已经有了这个用户，且状态变为了下线
                            AlarmMediaPlayer.get().play(AlarmMediaPlayer.SOURCE_PERSON_VOICE);
                            temp.bean.personModelBean.nStatus = PersonModelBean.STATUS_OFFLINE;
                        }
                        iMainView.deleteCluster(temp);

                        personCluster.remove(temp);

                        if (data.nOnline != -1) {
                            MyCluster myCluster = new MyCluster(temp.bean);
                            personCluster.add(myCluster);
                            iMainView.drawCover(myCluster);
                        }

                        iMainView.refCluster();
                        break;
                    }

                }
                if (!hasThis && !data.strUserID.equals(AppDatas.Auth().getUserID()+"")) {
                    //当前人员列表里面已经没有这个用户，且通知的不是本用户,且状态变为了上线
                    if (data.isOnline()){
                        AlarmMediaPlayer.get().play(AlarmMediaPlayer.SOURCE_PERSON_VOICE);
                    }
                    loadPerson();
                }
            }
        });

//        /**
//         * 定位改变推送
//         */
//        HYClient.getModule(ApiAuth.class).observeGSPStatus(new SdkCallback<CNotifyGPSStatus>() {
//            @Override
//            public void onSuccess(CNotifyGPSStatus cNotifyGPSStatus) {
//
//            }
//
//
//            @Override
//            public void onError(ErrorInfo errorInfo) {
//
//            }
//        });

    }

    public void onGPSStatus(CNotifyGPSStatus cNotifyGPSStatus) {
        if (HYClient.getSdkOptions().P2P().isP2PRunning()) {
            Logger.log("onGPSStatus 当前是p2p模式");
            return;
        }
        if (System.currentTimeMillis() - mitts < 2 * 1000) {
            return;
        }
        mitts = System.currentTimeMillis();
        if (cNotifyGPSStatus == null) {
            return;
        }
//        Logger.debug("GPSStatus " + cNotifyGPSStatus.nObjType + " " + cNotifyGPSStatus.strObjID + " " + cNotifyGPSStatus.rGPSInfo.fLatitude + " " + cNotifyGPSStatus.rGPSInfo.fLongitude);
        if (cNotifyGPSStatus.nObjType == 1) {
            updatePersonGPS(cNotifyGPSStatus);
        } else if (cNotifyGPSStatus.nObjType == 2) {
            updateDeviceGPS(cNotifyGPSStatus);
        } else if (cNotifyGPSStatus.nObjType == 3) {
            updateDomainGPS(cNotifyGPSStatus);
        } else {
//                    updateMarkGPS(cNotifyGPSStatus);
        }
    }

    public void initLocal(Location location, boolean needMove) {
        if (location == null) {
            return;
        }
        this.location = location;
        if (location != null) {
            if (needMove)
                isFirst = true;
            if (isFirst) {
                isFirst = false;
                // 按照经纬度确定地图位置
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                // 移动到某经纬度
                iMainView.animateMapStatus(update);
                update = MapStatusUpdateFactory.zoomBy(5f);
                // 放大
                iMainView.animateMapStatus(update);
            }

            // 显示个人位置图标
            MyLocationData.Builder builder = new MyLocationData.Builder();
            builder.latitude(location.getLatitude());
            builder.longitude(location.getLongitude());
            MyLocationData data = builder.build();
            iMainView.setLocation(data);

        }
    }


    public void loadPerson() {
        Logger.debug("MainPresenter loadPerson " );

        ContactsApi.get().getPerson(-1, 9999, -1, 0, new ModelCallback<PersonBean>() {
            @Override
            public void onSuccess(PersonBean personBean) {

                userList.clear();
                if (personBean.userList != null) {
                    String myUserID = AppDatas.Auth().getUserID() + "";
                    for (int i = 0; i < personBean.userList.size(); i++) {
                        PersonModelBean person = personBean.userList.get(i);
                        //对讲中的状态是单独的,获取时就需要设置下
                        if (person.nStatus > PersonModelBean.STATUS_OFFLINE) {
                            if (person.nSpeaking == 2){
                                person.nStatus =  PersonModelBean.STATUS_ONLINE_TRUNK_SPEAKING;
                            }
                        }
                        //剔除掉自己
                        if (!person.strUserID.equals(myUserID) && person.nStatus > PersonModelBean.STATUS_OFFLINE) {
                            userList.add(person);
                        }
                    }
                    addPerson();
                } else {
                    Logger.log("MainPresent   loadPerson   为0");
                }
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                Logger.log("MainPresent   loadPerson   失败");
            }
        });
    }

    public void loadDevice() {
        DeviceApi.get().getDomainDeviceList(1, 9999, 0, new ModelCallback<DeviceListBean>() {
            @Override
            public void onSuccess(DeviceListBean deviceListBean) {

                deviceList.clear();
                if (deviceListBean.deviceList != null) {
                    for (DeviceListBean.DeviceBean temp : deviceListBean.deviceList) {
                        for (DeviceListBean.ChannelBean channelBean : temp.channelList) {
                            DevicePlayerBean bean = new DevicePlayerBean();
                            bean.strChannelName = channelBean.strChannelName;
                            bean.strChannelCode = channelBean.strChannelCode;
                            bean.strDeviceCode = temp.strDeviceCode;
                            bean.strDomainCode = temp.strDomainCode;
                            bean.fLatitude = channelBean.fLatitude;
                            bean.fLongitude = channelBean.fLongitude;
                            bean.nOnlineState = temp.nOnlineState;
                            if (channelBean.streamList.isEmpty()) {
                                continue;
                            }
                            bean.strStreamCode = channelBean.streamList.get(0).strStreamCode;
                            if (channelBean.streamList.size() >1 ){
                                bean.strSubStreamCode = channelBean.streamList.get(1).strStreamCode;
                            }
                            deviceList.add(bean);
                        }
                    }
                    addDevice();
                } else {
                    Logger.log("MainPresent   loadDevice   为0");
                }
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                Logger.log("MainPresent   loadDevice   失败");
            }
        });
    }

    public void loadDomain() {
        HYClient.getModule(ApiDevice.class)
                .getDeviceDomainList(SdkParamsCenter.Device.GetDoMainList(),
                        new SdkCallback<CGetDomainListRsp>() {
                            @Override
                            public void onSuccess(CGetDomainListRsp cGetDomainListRsp) {
                                domainList.clear();
                                if (cGetDomainListRsp.domainInfoList != null) {
                                    domainList.addAll(cGetDomainListRsp.domainInfoList);
                                    addDomain();
                                }

                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                            }
                        });
    }

    /**
     * 用户在对话框中点击了对应的类型,界面做出响应
     * @param bean
     */
    public void loadMapMark(final MarkModelBean bean) {

        if (!bean.isChoose) {
            //这个被取消了
            ArrayList<MarkModelBean> tempMark = new ArrayList<>();
            for (MarkModelBean temp : markInfoList) {//标记点
                if (temp.nMarkID == bean.nMarkID) {
                    tempMark.add(temp);
                }
            }
            //删除标记
            for(MarkModelBean temp : tempMark) {
                markInfoList.remove(temp);
                deleteMark(temp.nMarkID, POINT);
            }

            //删除overlay
            if (allOverlay.containsKey(bean.nMarkID)) {
                deleteMark(bean.nMarkID, 2);
            }
        }else {
            //选中
            MapApi.get().getMarket(bean, new ModelCallback<MapMarkBean>() {

                @Override
                public void onPreStart(HTTPRequest httpRequest) {
                    super.onPreStart(httpRequest);
                    iMainView.getContext().mZeusLoadView.setLoading();
                }

                @Override
                public void onSuccess(MapMarkBean mapMarkBean) {
                    clearMark(mapMarkBean.nMarkID);
                    dealMapMark(mapMarkBean, 1);
                }

                @Override
                public void onFailure(HTTPResponse httpResponse) {
                    super.onFailure(httpResponse);
                    Logger.log("MainPresent   loadDevice   失败");
                }

                @Override
                public void onFinish(HTTPResponse httpResponse) {
                    super.onFinish(httpResponse);
                    iMainView.getContext().mZeusLoadView.dismiss();
                }
            });
        }



    }


    public void showTopo(){
        TopoDialog dialog = TopoDialog.getInstance(topoOverlay.size() > 0);
        dialog.show(MCApp.getInstance().getTopActivity().getSupportFragmentManager(),"topo");
        dialog.setListener(new TopoDialog.ShowInMapListener() {
            @Override
            public void onShowInMap(boolean show, List<TopoDeviceGPS> list) {
                if (show){
                    showTopoOnMap(list);
                }else {
                    for (int i = 0 ; i < topoOverlay.size() ; i++){
                        Overlay overlay = topoOverlay.get(i);
                        overlay.remove();
                    }
                    topoOverlay.clear();
                }
            }
        });
    }

    private void showTopoOnMap(List<TopoDeviceGPS> gps) {

        //先画点,再画线
        for (int i = 0 ; i < gps.size() ; i++){
            TopoDeviceGPS oneTopoDevice = gps.get(i);
            //定义Maker坐标点
            LatLng point = new LatLng(oneTopoDevice.gpslat, oneTopoDevice.gpslon);
            //构建Marker图标
//            BitmapDescriptor bitmap = BitmapDescriptorFactory
//                    .fromResource(R.drawable.icon_gcoding);
            View view  = View.inflate(MCApp.getInstance().getTopActivity(),R.layout.item_topo_marker,null);
            TextView tv = view.findViewById(R.id.tv);
            tv.setText(oneTopoDevice.ip);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(view);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            Overlay overlay = iMainView.addOverlay(option);
            topoOverlay.add(overlay);
        }
        List<LatLng> points = new ArrayList<LatLng>();
        LatLng pointFirst = null;
        for (int i = 0 ; i < gps.size() ; i++){
            TopoDeviceGPS oneTopoDevice = gps.get(i);
            LatLng p1 = new LatLng(oneTopoDevice.gpslat, oneTopoDevice.gpslon);
            points.add(p1);
            if (pointFirst == null){
                pointFirst = p1;
            }
        }
        if (pointFirst != null){
            points.add(pointFirst);
        }

        if (topoOverlay.size() > 0){
            //绘制折线
            OverlayOptions ooPolyline = new PolylineOptions().width(10)
                    .color(0xAAFF0000).points(points);
            Overlay overlay = iMainView.addOverlay(ooPolyline);
            topoOverlay.add(overlay);
        }

    }


    //下载离线地图
    public void downOffLine(){
        if (MCApp.getInstance().locationService.getCurrentBDLocation() != null
                && MCApp.getInstance().locationService.getCurrentBDLocation().getCityCode() != null) {
            MKOLUpdateElement element=getOfflineMap();
            if (element!=null&&element.status==MKOLUpdateElement.FINISHED){
                showToast(AppUtils.getString(R.string.offline_map_downloaded));
                return;
            }else if (element!=null&&(element.status==MKOLUpdateElement.WAITING||element.status==MKOLUpdateElement.DOWNLOADING)){
                showToast(AppUtils.getString(R.string.offline_map_downloading));
                return;
            }else {
                showToast(AppUtils.getString(R.string.offline_map_star_downloading));
                MapListener.get().startLoad(Integer.parseInt(MCApp.getInstance().locationService.getCurrentBDLocation().getCityCode()));
            }
        } else {
            showToast(AppUtils.getString(R.string.offline_no_cityid));
        }
    }

    private MKOLUpdateElement getOfflineMap() {
        ArrayList<MKOLUpdateElement> temps = MapListener.get().getMkOfflineMap().getAllUpdateInfo();
        if (temps != null) {
            for (MKOLUpdateElement temp : temps) {
                if (MCApp.getInstance().locationService.getCurrentBDLocation() != null
                        && MCApp.getInstance().locationService.getCurrentBDLocation().getCityCode() != null) {
                    if (temp.cityID == Integer.parseInt(MCApp.getInstance().locationService.getCurrentBDLocation().getCityCode()))
                        return temp;
                }
            }
            return null;
        }else {
            return null;
        }
    }


    private void updatePersonGPS(CNotifyGPSStatus cNotifyGPSStatus) {
        boolean hasThis = false;
        for (MyCluster temp : personCluster) {
            if (temp.bean.personModelBean.strUserID.equals(cNotifyGPSStatus.strObjID)) {
                hasThis = true;
                //更新百度的定位信息也要更新对应的对象中的定位信息
                temp.bean.latLng = GPSLocation.convertGPSToBaidu(new LatLng(cNotifyGPSStatus.rGPSInfo.fLatitude, cNotifyGPSStatus.rGPSInfo.fLongitude));
                temp.bean.personModelBean.dLatitude = cNotifyGPSStatus.rGPSInfo.fLatitude;
                temp.bean.personModelBean.dLongitude = cNotifyGPSStatus.rGPSInfo.fLongitude;
                iMainView.deleteCluster(temp);

                personCluster.remove(temp);

                MyCluster myCluster = new MyCluster(temp.bean);
                personCluster.add(myCluster);

                iMainView.drawCover(myCluster);
                iMainView.refCluster();
                break;
            }
        }
        if (!hasThis) {
            loadPerson();
        }
        iMainView.notifyGPSInfoChange(cNotifyGPSStatus.strObjID, cNotifyGPSStatus);
    }

    void updateDeviceGPS(CNotifyGPSStatus cNotifyGPSStatus) {
        boolean hasThis = false;
        for (MyCluster temp : deviceCluster) {
            if (cNotifyGPSStatus.strObjDomainCode.equals(temp.bean.deviceBean.strDomainCode)
                    && temp.bean.deviceBean.strChannelCode.equals(cNotifyGPSStatus.strObjID)) {
                hasThis = true;
                //更新百度的定位信息也要更新对应的对象中的定位信息
                temp.bean.latLng = GPSLocation.convertGPSToBaidu(new LatLng(cNotifyGPSStatus.rGPSInfo.fLatitude, cNotifyGPSStatus.rGPSInfo.fLongitude));
                temp.bean.deviceBean.fLatitude = cNotifyGPSStatus.rGPSInfo.fLatitude;
                temp.bean.deviceBean.fLongitude = cNotifyGPSStatus.rGPSInfo.fLongitude;
                iMainView.deleteCluster(temp);

                deviceCluster.remove(temp);

                MyCluster myCluster = new MyCluster(temp.bean);
                deviceCluster.add(myCluster);

                iMainView.drawCover(myCluster);
                iMainView.refCluster();
                break;
            }
        }
        if (!hasThis) {
            loadDevice();
        }
        iMainView.notifyGPSInfoChange(cNotifyGPSStatus.strObjDomainCode + cNotifyGPSStatus.strObjID, cNotifyGPSStatus);
    }

    void updateDomainGPS(CNotifyGPSStatus cNotifyGPSStatus) {
        boolean hasThis = false;
        for (MyCluster temp : domainCluster) {
            if (temp.bean.domainModelBean.strDomainCode.equals(cNotifyGPSStatus.strObjID)) {
                hasThis = true;
                //更新百度的定位信息也要更新对应的对象中的定位信息
                temp.bean.latLng = GPSLocation.convertGPSToBaidu(new LatLng(cNotifyGPSStatus.rGPSInfo.fLatitude, cNotifyGPSStatus.rGPSInfo.fLongitude));
                temp.bean.domainModelBean.fLatitude = AppUtils.double2Float(cNotifyGPSStatus.rGPSInfo.fLatitude);
                temp.bean.domainModelBean.fLongitude = AppUtils.double2Float(cNotifyGPSStatus.rGPSInfo.fLongitude);
                iMainView.deleteCluster(temp);

                domainCluster.remove(temp);

                MyCluster myCluster = new MyCluster(temp.bean);
                domainCluster.add(myCluster);

                iMainView.drawCover(myCluster);
                iMainView.refCluster();
                break;
            }
        }
        if (!hasThis) {
//            loadDomain();
        }
    }

//    void updateMarkGPS(CNotifyGPSStatus cNotifyGPSStatus) {
//        boolean hasThis = false;
//        for (MyCluster temp : markCluster) {
//            if (cNotifyGPSStatus.strObjID.equals(temp.bean.markModelBean.nMarkID + "")) {
//                hasThis = true;
//                //更新百度的定位信息也要更新对应的对象中的定位信息
//                temp.bean.latLng = GPSLocation.convertGPSToBaidu(new LatLng(cNotifyGPSStatus.rGPSInfo.fLatitude, cNotifyGPSStatus.rGPSInfo.fLongitude));
//                temp.bean.markModelBean.dLatitude = cNotifyGPSStatus.rGPSInfo.fLatitude;
//                temp.bean.markModelBean.dLongitude = cNotifyGPSStatus.rGPSInfo.fLongitude;
//                iMainView.deleteCluster(temp);
//
//                markCluster.remove(temp);
//
//                MyCluster myCluster = new MyCluster(temp.bean);
//                markCluster.add(myCluster);
//
//                iMainView.drawCover(myCluster);
//                iMainView.refCluster();
//                break;
//            }
//        }
//        if (!hasThis) {
//            loadCover();
//        }
//        iMainView.notifyGPSInfoChange(cNotifyGPSStatus.strObjID, cNotifyGPSStatus);
//    }

    private void addDomain() {

        for (MyCluster temp : domainCluster) {
            iMainView.deleteCluster(temp);
        }
        domainCluster.clear();
        for (CGetDomainListRsp.DomainList temp : domainList) {
            BDMarkBean bdMarkBean = new BDMarkBean();
            bdMarkBean.domainModelBean = temp;
            bdMarkBean.latLng = GPSLocation.convertGPSToBaidu(new LatLng(temp.fLatitude, temp.fLongitude));

            MyCluster cluster = new MyCluster(bdMarkBean);
            domainCluster.add(cluster);

            iMainView.drawCover(cluster);
        }
        iMainView.refCluster();
    }

    /**
     * 添加标记到界面
     */
    private void addMark() {
        for (MyCluster temp : markCluster) {
            iMainView.deleteCluster(temp);
        }
        markCluster.clear();
        for (MarkModelBean temp : markInfoList) {
            BDMarkBean bdMarkBean = new BDMarkBean();
            bdMarkBean.markModelBean = temp;
            bdMarkBean.latLng = GPSLocation.convertGPSToBaidu(new LatLng(temp.dLatitude, temp.dLongitude));

            MyCluster cluster = new MyCluster(bdMarkBean);
            markCluster.add(cluster);

            iMainView.drawCover(cluster);
        }
        iMainView.refCluster();
    }

    public void dealMapMark(MapMarkBean bean, int nMsgType) {
        if (nMsgType == 1) {//add
            switch (bean.nType) {
                case POINT:
                    for (MarkModelBean markModelBean : markInfoList){
                        if (markModelBean.nMarkID == bean.nMarkID){
                            Logger.log("该标记已存在");
                            return;
                        }
                    }
                    for (PointBean temp : bean.lstSite) {
                        MarkModelBean modelBean = new MarkModelBean();
                        modelBean.nMarkID = bean.nMarkID;
                        modelBean.strMarkName = bean.strMarkName;
                        modelBean.dLongitude = temp.dLongitude;
                        modelBean.dLatitude = temp.dLatitude;
                        modelBean.strRemark = bean.strRemark;
                        modelBean.strLastModTime = bean.strLastModTime;
                        modelBean.strDomainCode = bean.strDomainCode;

                        markInfoList.add(modelBean);
                    }
                    addMark();
                    break;
                case LINE:
                    ArrayList<LatLng> line = new ArrayList<>();
                    for (PointBean temp : bean.lstSite) {
                        line.add(temp.getBaiDuPoint());

                        OverlayOptions title = new TextOptions()
                                .fontSize(24)
                                .fontColor(Color.parseColor(bean.strColorValue))
                                .text(bean.strMarkName)
                                .position(temp.getBaiDuPoint());
                        if (allOverlay.containsKey(bean.nMarkID)) {
//                            allOverlay.get(bean.nMarkID).add(iMainView.addOverlay(title));
                        } else {
                            List<Overlay> ols = new ArrayList<>();
                            ols.add(iMainView.addOverlay(title));
                            allOverlay.put(bean.nMarkID, ols);
                        }
                    }
                    OverlayOptions lineOption = new PolylineOptions().color(Color.parseColor(bean.strColorValue))
                            .width(5).points(line).zIndex(8);
                    List<Overlay> current_line = new ArrayList<>();
                    current_line.add(iMainView.addOverlay(lineOption));

                    if (allOverlay.containsKey(bean.nMarkID)) {
                        allOverlay.get(bean.nMarkID).addAll(current_line);
                    } else {
                        allOverlay.put(bean.nMarkID, current_line);
                    }
                    break;
                case BOARD:
                case MANYBOARD:
                    if (bean.lstSite.isEmpty()) {
                        return;
                    }
                    ArrayList<LatLng> board = new ArrayList<>();
                    for (PointBean temp : bean.lstSite) {
                        board.add(temp.getBaiDuPoint());

                        OverlayOptions title = new TextOptions()
                                .fontSize(24)
                                .fontColor(Color.parseColor(bean.strColorValue))
                                .text(bean.strMarkName)
                                .position(temp.getBaiDuPoint());
                        if (allOverlay.containsKey(bean.nMarkID)) {
//                            allOverlay.get(bean.nMarkID).add(iMainView.addOverlay(title));
                        } else {
                            List<Overlay> ols = new ArrayList<>();
                            ols.add(iMainView.addOverlay(title));
                            allOverlay.put(bean.nMarkID, ols);
                        }
                    }
                    board.add(bean.lstSite.get(0).getBaiDuPoint());
                    OverlayOptions boardOption = new PolylineOptions().color(Color.parseColor(bean.strColorValue))
                            .width(5).points(board).zIndex(8);
                    List<Overlay> current_board = new ArrayList<>();
                    current_board.add(iMainView.addOverlay(boardOption));
                    if (allOverlay.containsKey(bean.nMarkID)) {
                        allOverlay.get(bean.nMarkID).addAll(current_board);
                    } else {
                        allOverlay.put(bean.nMarkID, current_board);
                    }
                    break;
                case CIRCLE:
                    if (bean.lstSite.isEmpty()) {
                        return;
                    }
                    OverlayOptions ooCircle = new CircleOptions().fillColor(Color.TRANSPARENT)
                            .center(bean.lstSite.get(0).getBaiDuPoint()).stroke(new Stroke(5, Color.parseColor(bean.strColorValue)))
                            .radius(bean.nRadius).zIndex(8);
                    List<Overlay> current_circle = new ArrayList<>();
                    current_circle.add(iMainView.addOverlay(ooCircle));
                    if (allOverlay.containsKey(bean.nMarkID)) {
                        allOverlay.get(bean.nMarkID).addAll(current_circle);
                    } else {
                        allOverlay.put(bean.nMarkID, current_circle);
                    }

                    OverlayOptions title = new TextOptions()
                            .fontSize(24)
                            .fontColor(Color.parseColor(bean.strColorValue))
                            .text(bean.strMarkName)
                            .position(bean.lstSite.get(0).getBaiDuPoint());
                    if (allOverlay.containsKey(bean.nMarkID)) {
                        allOverlay.get(bean.nMarkID).add(iMainView.addOverlay(title));
                    } else {
                        List<Overlay> ols = new ArrayList<>();
                        ols.add(iMainView.addOverlay(title));
                        allOverlay.put(bean.nMarkID, ols);
                    }
                    break;
            }
        } else if (bean.nMsgType == 2) {//delete
            deleteMark(bean.nMarkID, bean.nType);
        } else {
            switch (bean.nType) {
                case POINT:
                    break;
                default:
                    clearMark(bean.nMarkID);
                    dealMapMark(bean, 1);
                    break;
            }
        }
    }

    /**
     * 删除标记
     *
     * @param nMarkID
     * @param nType
     */
    private void deleteMark(int nMarkID, int nType) {
        switch (nType) {
            case POINT://删除点
                for (MyCluster temp : markCluster) {
                    if (nMarkID == temp.bean.markModelBean.nMarkID) {
                        iMainView.deleteCluster(temp);
                        markCluster.remove(temp);
                        iMainView.refCluster();
                        break;
                    }
                }
                break;
            default:
                clearMark(nMarkID);
                allOverlay.remove(nMarkID);
                break;
        }
    }


    /**
     * 清除所有用户标记
     */
    public void clearAllUserMark(){
        clearMapMark();
        clearMark(-1);
    }

    /**
     * 清除标记
     */
    private void clearMapMark(){
        for (MyCluster temp : markCluster) {
            iMainView.deleteCluster(temp);
        }
        markCluster.clear();
        markInfoList.clear();
        iMainView.refCluster();
    }
    /**
     * 清除overLay
     * @param value -1的话清除全部
     */
    private void clearMark(int value) {
        for (Map.Entry<Integer, List<Overlay>> entry : allOverlay.entrySet()) {
            if (value == -1) {
                for (Overlay temp : entry.getValue()) {
                    temp.remove();
                }
                entry.getValue().clear();
            } else if (entry.getKey() == value) {
                for (Overlay temp : entry.getValue()) {
                    temp.remove();
                }
                entry.getValue().clear();
                break;
            }
        }
        if (value == -1){
            allOverlay.clear();
        }
        iMainView.refCluster();
    }

    private void addPerson() {
        for (MyCluster temp : personCluster) {
            iMainView.deleteCluster(temp);
        }
        personCluster.clear();
        for (PersonModelBean temp : userList) {
            BDMarkBean bdMarkBean = new BDMarkBean();
            bdMarkBean.personModelBean = temp;
            bdMarkBean.latLng = GPSLocation.convertGPSToBaidu(new LatLng(temp.dLatitude, temp.dLongitude));

            MyCluster cluster = new MyCluster(bdMarkBean);
            personCluster.add(cluster);

            iMainView.drawCover(cluster);
        }
        iMainView.refCluster();
    }

    public void refBean(ChangeUserBean bean) {
        for (MyCluster temp : personCluster) {
            if (temp.bean != null
                    && temp.bean.personModelBean != null
                    && temp.bean.personModelBean.strDomainCode.equals(bean.strModifyUserDomainCode)
                    && temp.bean.personModelBean.strUserID.equals(bean.strModifyUserID)) {
                temp.bean.personModelBean.strUserName = bean.strModifyUserName;
                temp.bean.personModelBean.nPriority = bean.nPriority;
                break;
            }
        }
    }

    private void addDevice() {
        for (MyCluster temp : deviceCluster) {
            iMainView.deleteCluster(temp);
        }
        deviceCluster.clear();
        for (DevicePlayerBean temp : deviceList) {
            BDMarkBean bdMarkBean = new BDMarkBean();
            bdMarkBean.deviceBean = temp;
            bdMarkBean.latLng = GPSLocation.convertGPSToBaidu(new LatLng(temp.fLatitude, temp.fLongitude));

            MyCluster cluster = new MyCluster(bdMarkBean);
            deviceCluster.add(cluster);

            iMainView.drawCover(cluster);
        }
        iMainView.refCluster();
    }



    public void clearMyCluster() {
        for (MyCluster temp : personCluster) {
            iMainView.deleteCluster(temp);
        }
        for (MyCluster temp : deviceCluster) {
            iMainView.deleteCluster(temp);
        }
        for (MyCluster temp : domainCluster) {
            iMainView.deleteCluster(temp);
        }
        for (MyCluster temp : markCluster) {
            iMainView.deleteCluster(temp);
        }

        personCluster.clear();
        deviceCluster.clear();
        domainCluster.clear();
        markCluster.clear();
        iMainView.clearMarkMark();
        clearMark(-1);

        iMainView.refCluster();
    }


    /**
     * 获取自定义标记的id列表
     * @return
     */
    public ArrayList<Integer> getCustomMarkIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (MarkModelBean pinMark:markInfoList){
             ids.add(pinMark.nMarkID);
        }
        Set<Integer> shapeIds  = allOverlay.keySet();
        for (Integer shapeID : shapeIds){
            List<Overlay> thisOverlay = allOverlay.get(shapeID);
            if (thisOverlay != null && thisOverlay.size() > 0){
                ids.add(shapeID);
            }
        }
        return ids;
    }

    public ArrayList<MyCluster> getPersonCluster(){
        return personCluster;
    }

    public void destroy(){
        if (userObserverCaller != null){
            userObserverCaller.cancel();
        }
        DeviceApi.get().removeAlarmListener(deviceStatusListener);
        DeviceApi.get().stopDeviceListener();
    }

}
