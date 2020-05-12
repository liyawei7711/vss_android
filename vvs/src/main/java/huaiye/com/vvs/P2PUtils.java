package huaiye.com.vvs;

import com.huaiye.cmf.sdp.SdpMsgCommonUDPMsg;
import com.huaiye.cmf.sdp.SdpMsgFindLanCaptureDeviceRsp;
import com.huaiye.cmf.sdp.SdpMsgLanCaptureDeviceNotAliveNotify;
import com.huaiye.cmf.sdp.SdpMsgLanCaptureDeviceStopped;
import com.huaiye.cmf.sdp.SdpMsgLanViewerNotAliveNotify;
import com.huaiye.samples.p2p.P2PSample;
import com.huaiye.samples.p2p.P2PSampleHandler;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;

/**
 * author: admin
 * date: 2018/08/23
 * version: 0
 * mail: secret
 * desc: P2PUtils
 */

public class P2PUtils implements P2PSampleHandler {


    /**
     * 无中心
     */
    public P2PSample mP2PSample;

    public void startP2P(String name) {
        Logger.log("start p2p");
        HYClient.getSdkOptions().P2P().setCaptureName(name,"");
        HYClient.getSdkOptions().P2P().setSupportP2P(true);
        mP2PSample = HYClient.getSdkSamples().P2P();
        mP2PSample.subscribe(this);
        mP2PSample.startP2P();
    }

    public void stopP2P(String name) {
        Logger.log("stop p2p");
        HYClient.getSdkSamples().P2P().stopP2P();
    }

    @Override
    public void onGetScanDevices(SdpMsgFindLanCaptureDeviceRsp msg) {
        System.out.println("dddddddddddddddd onGetScanDevices " + msg);
    }

    @Override
    public void onGetScanDevicesOffline(SdpMsgCommonUDPMsg sdpMsgFindLanCaptureDeviceRsp) {

    }

    @Override
    public void onWatchingDeviceOffline(SdpMsgLanCaptureDeviceNotAliveNotify sdpMsgLanCaptureDeviceNotAliveNotify) {
        System.out.println("dddddddddddddddd p2p onWatchingDeviceOffline " + sdpMsgLanCaptureDeviceNotAliveNotify);
    }

    @Override
    public void onWatchingDeviceCaptureStopped(SdpMsgLanCaptureDeviceStopped sdpMsgLanCaptureDeviceStopped) {
        System.out.println("dddddddddddddddd p2p onWatchingDeviceCaptureStopped " + sdpMsgLanCaptureDeviceStopped);
    }

    @Override
    public void onWatchingDeviceStopWatch(SdpMsgCommonUDPMsg sdpMsgCommonUDPMsg) {
        System.out.println("dddddddddddddddd p2p onWatchingDeviceStopWatch " + sdpMsgCommonUDPMsg);
    }

    @Override
    public void onReceiveWatchRequest(SdpMsgCommonUDPMsg msg) {
        System.out.println("dddddddddddddddd p2p onReceiveWatchRequest " + msg);

        mP2PSample.respWatchRequest(SdkBaseParams.AgreeMode.Agree, msg.m_strIP);

    }

    @Override
    public void onReceiveCommonUDPMsg(SdpMsgCommonUDPMsg sdpMsgCommonUDPMsg) {

    }

    @Override
    public void onWatcherDeviceOffline(SdpMsgLanViewerNotAliveNotify sdpMsgLanViewerNotAliveNotify) {
        System.out.println("dddddddddddddddd p2p onWatcherDeviceOffline " + sdpMsgLanViewerNotAliveNotify);
    }

    @Override
    public void onWatcherWatchStopped(SdpMsgCommonUDPMsg sdpMsgCommonUDPMsg) {
        System.out.println("dddddddddddddddd p2p onWatcherWatchStopped " + sdpMsgCommonUDPMsg);
    }

    @Override
    public void onReceiveTalkRequest(SdpMsgCommonUDPMsg sdpMsgCommonUDPMsg) {
        System.out.println("dddddddddddddddd p2p onReceiveTalkRequest " + sdpMsgCommonUDPMsg);
    }

    @Override
    public void onTalkStopped(SdpMsgCommonUDPMsg sdpMsgCommonUDPMsg) {
        System.out.println("dddddddddddddddd p2p onTalkStopped " + sdpMsgCommonUDPMsg);
    }
}
