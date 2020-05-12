package huaiye.com.vvs.models.device;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCaller;
import com.huaiye.sdk.core.SdkNotifyCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiDevice;
import com.huaiye.sdk.sdpmsgs.face.CServerNotifyAlarmInfo;

import java.util.ArrayList;

import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.device.bean.DeviceListBean;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DeviceApi
 */

public class DeviceApi {
    public static final int NOTIFY_TYPE_DEIVCE_ONLINE  = 1;
    public static final int NOTIFY_TYPE_DEIVCE_OFFLINE  = 2;
    static DeviceApi deviceApi;
    String URL;
    SdkCaller observeDeviceStatus;
    private ArrayList<SdkNotifyCallback<CServerNotifyAlarmInfo>> listeners;

    private DeviceApi() {
        URL = AppDatas.Constants().getAddressBaseURL9200() + "sie/httpjson/get_domain_device_list";
        listeners = new ArrayList<>();
    }


    public static DeviceApi get() {
        if (deviceApi == null){
            deviceApi = new DeviceApi();
        }
        return deviceApi;
    }

    public void getDomainDeviceList(int nPage, int nSize, int nReverse, final ModelCallback<DeviceListBean> callback) {
        URL = AppDatas.Constants().getAddressBaseURL9200() + "sie/httpjson/get_domain_device_list";
        Https.post(URL)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addParam("nPage", nPage)
                .addParam("nSize", nSize)
                .addParam("nReverse", nReverse)
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .setHttpCallback(new ModelCallback<DeviceListBean>() {
                    @Override
                    public void onSuccess(DeviceListBean deviceListBean) {
                        if (deviceListBean != null && callback != null) {
                            callback.onSuccess(deviceListBean);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                    }
                })
                .build()
                .requestNowAsync();
    }


    public void startDeviceListener(){
        Logger.log("DeviceApi startDeviceListener");
        // 监听 用户状态
        observeDeviceStatus = HYClient.getModule(ApiDevice.class).ObserverDeviceAlarm(new SdkNotifyCallback<CServerNotifyAlarmInfo>() {
            @Override
            public void onEvent(final CServerNotifyAlarmInfo data) {
                Logger.log("DeviceApi startDeviceListener onEvent" +data);
               for (SdkNotifyCallback<CServerNotifyAlarmInfo> listener : listeners){
                   listener.onEvent(data);
               }
            }
        });
    }

    public void stopDeviceListener(){
        Logger.log("DeviceApi stopDeviceListener");
        if (observeDeviceStatus != null){
            observeDeviceStatus.cancel();
        }
        listeners.clear();
    }

    public void addAlarmListener(SdkNotifyCallback<CServerNotifyAlarmInfo> listener){
        listeners.add(listener);
    }

    public void removeAlarmListener(SdkNotifyCallback<CServerNotifyAlarmInfo> listener){
        listeners.remove(listener);
    }

}
