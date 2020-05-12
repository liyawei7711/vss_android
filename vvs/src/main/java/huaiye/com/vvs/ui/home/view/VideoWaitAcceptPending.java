package huaiye.com.vvs.ui.home.view;

import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import huaiye.com.vvs.bus.WaitViewAllFinish;
import huaiye.com.vvs.common.AppBaseActivity;

/**
 * app在后台时,缓存VideoWaitAcceptDialog,等待app恢复到前台时播放
 * @author lxf
 */
public class VideoWaitAcceptPending {
    VideoWaitAcceptDialog pendingDialog;
    CNotifyUserJoinTalkback pendingData;

    public VideoWaitAcceptPending(){

    }


    public void setPendingDialog(VideoWaitAcceptDialog pendingDialog,CNotifyUserJoinTalkback data) {
        this.pendingDialog = pendingDialog;
        this.pendingData = data;
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    public void onResume(AppBaseActivity appBaseActivity){
        if (pendingDialog != null){
            pendingDialog.show(appBaseActivity.getSupportFragmentManager(),VideoWaitAcceptDialog.TAG);
        }
        taskFinish();
    }


    public void cancel(){
        taskFinish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo) {
        if (pendingData != null
                && pendingData.nTalkbackID == cNotifyTalkbackStatusInfo.nTalkbackID
                && cNotifyTalkbackStatusInfo.isTalkingStopped()) {
            taskFinish();
            EventBus.getDefault().post(new WaitViewAllFinish("waitacceptlayout onEvent(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo)"));
        }
    }

    private void taskFinish(){
        pendingDialog = null;
        pendingData = null;
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
