package huaiye.com.vvs.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.huaiye.cmf.sdp.SdpMsgCommonUDPMsg;
import com.huaiye.cmf.sdp.SdpMsgFindLanCaptureDeviceRsp;
import com.huaiye.cmf.sdp.SdpMsgLanCaptureDeviceNotAliveNotify;
import com.huaiye.cmf.sdp.SdpMsgLanCaptureDeviceStopped;
import com.huaiye.cmf.sdp.SdpMsgLanViewerNotAliveNotify;
import com.huaiye.samples.p2p.P2PSample;
import com.huaiye.samples.p2p.P2PSampleHandler;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.logger.Logger;

import java.util.ArrayList;

/**
 * author: admin
 * date: 2017/12/14
 * version: 0
 * mail: secret
 * desc: P2PFunctionsMenuActivity
 */
public class P2PDevicesActivity extends Activity implements P2PSampleHandler {
    ArrayList<SdpMsgFindLanCaptureDeviceRsp> datas = new ArrayList<>();

    P2PSample mP2PSample;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HYClient.getSdkOptions().P2P().setCaptureName("lyw2","");
        HYClient.getSdkOptions().P2P().setSupportP2P(true);
        mP2PSample = HYClient.getSdkSamples().P2P();


    }

    @Override
    protected void onResume() {
        super.onResume();
        mP2PSample.subscribe(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mP2PSample.unSubscribe(this);
    }


    @Override
    public void onGetScanDevices(SdpMsgFindLanCaptureDeviceRsp msg) {
        Logger.log("P2PCapture onGetScanDevices " + msg.m_strIP + " " + msg.m_strName);
        boolean isContains = false;
        for (SdpMsgFindLanCaptureDeviceRsp tmp : datas) {
            if (tmp.m_strName.equals(msg.m_strName)
                    && tmp.m_strIP.equals(msg.m_strIP)) {
                tmp.m_nCaptureState = msg.m_nCaptureState;
                isContains = true;
                break;
            }
        }

        if (!isContains) {
            datas.add(msg);
        }
    }

    @Override
    public void onGetScanDevicesOffline(SdpMsgCommonUDPMsg sdpMsgFindLanCaptureDeviceRsp) {

    }

    @Override
    public void onWatchingDeviceOffline(SdpMsgLanCaptureDeviceNotAliveNotify device) {
        Logger.log("P2PCapture onWatchingDeviceOffline " + device.m_strIP);
    }

    @Override
    public void onWatchingDeviceCaptureStopped(SdpMsgLanCaptureDeviceStopped device) {
        Logger.log("P2PCapture onWatchingDeviceCaptureStopped " + device.m_strIP);
    }

    @Override
    public void onWatchingDeviceStopWatch(SdpMsgCommonUDPMsg msg) {
        Logger.log("P2PCapture onWatchingDeviceStopWatch " + msg.m_strIP);
    }

    @Override
    public void onReceiveWatchRequest(final SdpMsgCommonUDPMsg msg) {
        Logger.log("P2PCapture onReceiveWatchRequest " + msg.m_strIP);
    }

    @Override
    public void onReceiveCommonUDPMsg(SdpMsgCommonUDPMsg sdpMsgCommonUDPMsg) {

    }

    @Override
    public void onWatcherDeviceOffline(SdpMsgLanViewerNotAliveNotify watcher) {
        Logger.log("P2PCapture onWatcherDeviceOffline " + watcher.m_strIP);
    }

    @Override
    public void onWatcherWatchStopped(SdpMsgCommonUDPMsg msg) {
        Logger.log("P2PCapture onWatcherWatchStopped " + msg.m_strIP);
    }

    @Override
    public void onReceiveTalkRequest(final SdpMsgCommonUDPMsg msg) {
        Logger.log("P2PCapture onReceiveTalkRequest " + msg.m_strIP);
    }

    @Override
    public void onTalkStopped(SdpMsgCommonUDPMsg msg) {
        Logger.log("P2PCapture onTalkStopped " + msg.m_strIP);
    }

}
