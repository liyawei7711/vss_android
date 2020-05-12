package huaiye.com.vvs.ui.home.view;

import android.content.Context;

import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MyLocationData;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;

import huaiye.com.vvs.models.map.bean.BDMarkBean;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: IMainView
 */

public interface IWaiteView {

    Context getContext();

    WaitAcceptLayout getWaitView();

    void changeCurrentMeet(CNotifyInviteUserJoinMeeting cNotifyInviteUserJoinMeeting);

    void changeCurrentMeetBefore();
}
