package huaiye.com.vvs.ui.home.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMsgCommonUDPMsg;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi.abilities.talk.callback.CallbackQuitTalk;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserCancelJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;
import com.huaiye.sdk.sdpmsgs.talk.CQuitTalkbackRsp;
import com.huaiye.sdk.sdpmsgs.talk.CStartTalkbackReq;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.AcceptDiaoDu;
import huaiye.com.vvs.bus.WaitViewAllFinish;
import huaiye.com.vvs.common.AlarmMediaPlayer;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ErrorMsg;
import huaiye.com.vvs.common.ScreenNotify;
import huaiye.com.vvs.common.rx.RxUtils;
import huaiye.com.vvs.dao.msgs.AppMessages;
import huaiye.com.vvs.dao.msgs.CallRecordManage;
import huaiye.com.vvs.ui.talk.TalkVideoActivity;

import static huaiye.com.vvs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: 语音和视频邀请对话框,点击拒绝或加入后关闭,不过目前还加入了语音会话的界面
 */

public class WaitAcceptLayout extends FrameLayout implements View.OnClickListener {

    FrameLayout fl_root;
    ImageView iv_type;
    ImageView iv_refuse;
    ImageView iv_accept;
    TextView tv_name_id;
    TextView tv_notic;
    View ll_show_view;

    CNotifyInviteUserJoinMeeting meetData;
    CNotifyUserJoinTalkback talkData;
    SdpMsgCommonUDPMsg udpMsg;
    long millis;

    FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

//    public TalkViewLayout getTalkView() {
//        return tvl_view;
//    }

    public WaitAcceptLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setVisibility(GONE);
        View view = LayoutInflater.from(context).inflate(R.layout.main_wait_accept_layout, null);

        iv_type = view.findViewById(R.id.iv_type);
        iv_refuse = view.findViewById(R.id.iv_refuse);
        iv_accept = view.findViewById(R.id.iv_accept);
        tv_name_id = view.findViewById(R.id.tv_name_id);
        tv_notic = view.findViewById(R.id.tv_notic);
        ll_show_view = view.findViewById(R.id.ll_show_view);
        fl_root = view.findViewById(R.id.fl_root);

        addView(view);

        iv_refuse.setOnClickListener(this);
        iv_accept.setOnClickListener(this);


    }

    public WaitAcceptLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public WaitAcceptLayout(@NonNull Context context) {
        this(context, null);
    }

    /**
     * 创建语音会话等待框
     *
     * @param strToUserDomainCode
     * @param strToUserID
     * @param strToUserName
     */
    public void createTalk(boolean hasVideo, String strToUserDomainCode, String strToUserID, String strToUserName) {
        CStartTalkbackReq.ToUser toUser = new CStartTalkbackReq.ToUser();
        toUser.strToUserDomainCode = strToUserDomainCode;
        toUser.strToUserID = strToUserID;
        toUser.strToUserName = strToUserName;

        talkData = null;
        udpMsg = null;
        meetData = null;

        tv_name_id.setText(strToUserName + "(ID:" + strToUserID + ")");
        tv_notic.setText(AppUtils.getString(R.string.waite_accept_notice));
        iv_type.setVisibility(VISIBLE);

        if (hasVideo) {
            TalkVideoActivity.createTalk(getContext(), strToUserDomainCode, strToUserID, strToUserName,"", null);
        } else {
            showThisView(false);

            reAddTalkView();
            AppUtils.getTvl_view(getContext()).setVisibility(GONE);

            AppUtils.getTvl_view(getContext()).createTalk(toUser);

        }

    }


    /**
     * 被邀请对讲
     *
     * @param data
     * @param millis
     */
    public void showWaitTalk(CNotifyUserJoinTalkback data, SdpMsgCommonUDPMsg udpMsg, long millis) {
        //等待接听的界面正在展示说明正在准备接电话,直接拒绝另外一个
        if (ll_show_view.getVisibility() == VISIBLE && getVisibility() == VISIBLE) {
            HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                    .setTalkId(data.nTalkbackID)
                    .setTalkDomainCode(data.strTalkbackDomainCode), null);
            return;
        }
        this.talkData = data;
        this.udpMsg = udpMsg;
        this.meetData = null;
        this.millis = millis;
        iv_type.setVisibility(VISIBLE);
        String userFormat;
        String strNotice = "";
        if (data != null) {
            userFormat = data.strFromUserName + "(ID:" + data.strFromUserID + ")";
            if (data.getRequiredMediaMode() == SdkBaseParams.MediaMode.AudioAndVideo) {
                strNotice = AppUtils.getString(R.string.waite_you_video_diaodu);
                iv_type.setImageResource(R.drawable.tip_shipindiaodu);

            } else {
                strNotice = AppUtils.getString(R.string.waite_you_talk_diaodu);
                iv_type.setImageResource(R.drawable.tip_yuyindiaodu);
                ScreenNotify.get().showScreenNotify(getContext(), userFormat, AppUtils.getString(R.string.waite_you_talk_diaodu));
            }
        } else {
            userFormat = udpMsg.m_strIP;
            strNotice = AppUtils.getString(R.string.waite_you_video_diaodu);
            iv_type.setImageResource(R.drawable.tip_shipindiaodu);
        }

        tv_name_id.setText(userFormat);
        tv_notic.setText(strNotice);

        ScreenNotify.get().showScreenNotify(getContext(), userFormat, strNotice);
        showThisView(true);

        if (data != null && data.isForceInvite()) {
            new RxUtils<>().doDelayOn(500, new RxUtils.IMainDelay() {
                @Override
                public void onMainDelay() {
                    iv_accept.performClick();
                }
            });
        }
    }

    /**
     * 被邀请会议
     *
     * @param data
     * @param millis
     */
    public void showMeetTalk(CNotifyInviteUserJoinMeeting data, long millis) {
        Logger.debug("WaitAcceptLayout showMeetTalk start" );
        //等待接听的界面正在展示说明正在准备接电话,直接拒绝另外一个
        if (ll_show_view.getVisibility() == VISIBLE && getVisibility() == VISIBLE) {
            Logger.debug("WaitAcceptLayout showMeetTalk AlarmMediaPlayer isPlaying" );
            return;
        }
        this.meetData = data;
        this.talkData = null;
        this.millis = millis;
        String strMeetFormat = AppUtils.getString(R.string.meet) + "ID: " + data.nMeetingID;
        String strUserFormat = data.strInviteUserName + AppUtils.getString(R.string.waite_you_attend_meet);
        tv_name_id.setText(data.strInviteUserName + AppUtils.getString(R.string.waite_you_attend_meet));
        tv_notic.setText(AppUtils.getString(R.string.meet) + "ID: " + data.nMeetingID);
        iv_type.setImageResource(R.drawable.tip_huiyidiaodu);
        ScreenNotify.get().showScreenNotify(getContext(), strUserFormat, strMeetFormat);

        showThisView(true);

        if (data.isForceInvite()) {
            new RxUtils<>().doDelayOn(500, new RxUtils.IMainDelay() {
                @Override
                public void onMainDelay() {
                    iv_accept.performClick();
                }
            });
        }
    }

    public void isThisIp(String ip){
        if(udpMsg != null && udpMsg.m_strIP.equals(ip)) {
            showToast(AppUtils.getString(R.string.duifang_has_end));
            EventBus.getDefault().post(new WaitViewAllFinish("isThisIp 240"));
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_refuse:
                refuseBtn();
                break;
            case R.id.iv_accept:
                if (listener != null)
                    listener.onDisFromView();
                setVisibility(GONE);
                EventBus.getDefault().post(new AcceptDiaoDu(meetData, talkData, udpMsg, millis));
                break;
        }
    }

    public void refuseBtn() {
        if (talkData != null) {
            // 对讲邀请50063405
            if (tv_notic.getText().toString().equals(AppUtils.getString(R.string.waite_duifang_accept_this_diaodu))) {
                HYClient.getModule(ApiTalk.class).quitTalking(SdkParamsCenter.Talk.QuitTalk()
                        .setTalkDomainCode(talkData.strTalkbackDomainCode)
                        .setTalkId(talkData.nTalkbackID), null);
            } else {
                HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                        .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                        .setTalkId(talkData.nTalkbackID)
                        .setTalkDomainCode(talkData.strTalkbackDomainCode), null);
                CallRecordManage.get().updateCall(talkData.nMsgSessionID);
            }
            EventBus.getDefault().post(new WaitViewAllFinish("waitacceptlayout onclick iv_refuse 260"));
        } else if (meetData != null) {
            // 会议邀请
            HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                    .setMeetID(meetData.nMeetingID)
                    .setMeetDomainCode(meetData.strMeetingDomainCode), null);
            CallRecordManage.get().updateCall(meetData.nMsgSessionID);
            EventBus.getDefault().post(new WaitViewAllFinish("waitacceptlayout onclick iv_refuse 267"));
        } else {
//                    tvvl_view.endTalk();
            if (!TextUtils.isEmpty(AppUtils.getTvl_view(getContext()).strTalkDomainCode)) {
                AppUtils.getTvl_view(getContext()).closeVideo();
                EventBus.getDefault().post(new WaitViewAllFinish("waitacceptlayout onclick iv_refuse 271"));
            }else {
                //无网络情况，音频点击取消
                HYClient.getSdkSamples().P2P().respTalkRequest(SdkBaseParams.AgreeMode.Refuse, udpMsg);
                EventBus.getDefault().post(new WaitViewAllFinish("waitacceptlayout onclick iv_refuse 271"));
            }
        }
    }

    /**
     * 接受语音会话
     */
    public void acceptTalkInvite(CNotifyUserJoinTalkback bean, long millis) {
        if (listener != null) {
            listener.onShowFromView();
        }
        setVisibility(VISIBLE);

        toTalk(bean, millis);
    }

    public void toTalk(CNotifyUserJoinTalkback talkData, long millis) {
        AppMessages.get().del(millis);
        if (talkData.getRequiredMediaMode() == SdkBaseParams.MediaMode.AudioAndVideo) {
            setVisibility(GONE);
            TalkVideoActivity.joinTalk(getContext(), talkData.strTalkbackDomainCode, talkData.nTalkbackID, null);
        } else {
            setVisibility(VISIBLE);
            ll_show_view.setVisibility(GONE);
            AppMessages.get().del(millis);

            reAddTalkView();
            AppUtils.getTvl_view(getContext()).setVisibility(GONE);

            AppUtils.getTvl_view(getContext()).joinTalk(talkData.strTalkbackDomainCode, talkData.nTalkbackID, talkData);
        }
    }

    /**
     * 语音会话途中,来了新的语音会话邀请,用户点击确认后,关闭老的并建立新的会话
     *
     * @param bean
     * @param millis
     */
    public void closeAndJoinTalk(final CNotifyUserJoinTalkback bean, final long millis) {
        AppMessages.get().del(millis);
        AppUtils.getTvl_view(getContext()).endTalk(new CallbackQuitTalk() {
            @Override
            public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                return false;
            }

            @Override
            public void onSuccess(CQuitTalkbackRsp cQuitTalkbackRsp) {
                AlarmMediaPlayer.get().stop();
                if (bean.getRequiredMediaMode() == SdkBaseParams.MediaMode.AudioAndVideo) {
                    //关闭语音会话界面,打开语音视频界面
                    closeWaitViewAll();
                    EventBus.getDefault().post(new AcceptDiaoDu(null, bean, udpMsg, millis));
                } else {
                    if (listener != null) {
                        listener.onShowFromView();
                    }
                    setVisibility(VISIBLE);
                    ll_show_view.setVisibility(GONE);
                    EventBus.getDefault().post(new AcceptDiaoDu(null, bean, udpMsg, millis));
                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.qiehuan_meet_video_false));
            }
        });
    }


    /**
     * 语音会话途中,来了新的会议会话邀请,用户点击确认后,关闭老的并建立新的会话
     *
     * @param bean
     * @param millis
     */
    public void closeAndJoinMeet(final CNotifyInviteUserJoinMeeting bean, final long millis) {
        AppMessages.get().del(millis);
        AppUtils.getTvl_view(getContext()).endTalk(new CallbackQuitTalk() {
            @Override
            public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                return false;
            }

            @Override
            public void onSuccess(CQuitTalkbackRsp cQuitTalkbackRsp) {
                AlarmMediaPlayer.get().stop();
                EventBus.getDefault().post(new AcceptDiaoDu(bean, null, udpMsg, millis));

            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                ((AppBaseActivity) getContext()).showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyMeetingStatusInfo cNotifyMeetingStatusInfo) {
        if (meetData != null && meetData.nMeetingID == cNotifyMeetingStatusInfo.nMeetingID &&
                cNotifyMeetingStatusInfo.isMeetFinished()) {
            EventBus.getDefault().post(new WaitViewAllFinish(""));
            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.meet_diaodu_has_end));
            HYClient.getModule(ApiMeet.class).observeMeetingStatus(null);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyInviteUserCancelJoinMeeting cNotifyMeetingStatusInfo) {
        if (meetData != null && meetData.nMeetingID == cNotifyMeetingStatusInfo.nMeetingID && getVisibility() == VISIBLE) {
            EventBus.getDefault().post(new WaitViewAllFinish("waitacceptlayout onclick iv_refuse 267"));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo) {
        if (talkData != null
                && talkData.nTalkbackID == cNotifyTalkbackStatusInfo.nTalkbackID
                && cNotifyTalkbackStatusInfo.isTalkingStopped()) {
            EventBus.getDefault().post(new WaitViewAllFinish("waitacceptlayout onEvent(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo)"));
            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.diaodu_has_end));
            HYClient.getModule(ApiTalk.class).observeTalkingStatus(null);
        }

    }


    private void showThisView(boolean isWaite) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ll_show_view.setVisibility(VISIBLE);
        iv_refuse.setVisibility(VISIBLE);
        iv_accept.setVisibility(isWaite ? VISIBLE : GONE);

        if (listener != null) {
            listener.onShowFromView();
        }

        setVisibility(VISIBLE);
    }

    /**
     * 语音调度关闭等待view
     */
    public void closeWaitMenuView() {
        ll_show_view.setVisibility(GONE);
    }

    /**
     * 关闭当前view
     */
    public void closeWaitViewAll() {
        if (getVisibility() == View.GONE) {
            return;
        }
        ((AppBaseActivity) getContext()).getLogicDialog().dismiss();

        if (!AppUtils.isTalkViewNull()) {
            removeTalkView();
            AppUtils.getTvl_view(getContext()).onDestroy();
        }

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (listener != null)
            listener.onDisFromView();
        setVisibility(GONE);
        ScreenNotify.get().dismissNotify(MCApp.getInstance());

    }


    public void closeIfTalkDisconnect() {
        if (getVisibility() != View.VISIBLE) {
            return;
        }
        if (!AppUtils.isTalkViewNull()) {
            AppUtils.getTvl_view(getContext()).closeIfTalkDisconnect();
        }
    }

    public void onBackPressed() {
        //只处理等待接听的时候
        if (!AppUtils.isTalk){
            refuseBtn();
        }else {
            AppUtils.getTvl_view(getContext()).onBackPressed();
        }
    }

    public void onResume() {
        if (!AppUtils.isTalkViewNull()) {
            AppUtils.getTvl_view(getContext()).onResume();
        }
    }

    public void onPause() {
        if (!AppUtils.isTalkViewNull()) {
            AppUtils.getTvl_view(getContext()).onPause();
        }
    }

    public void onDestroy() {
        if (!AppUtils.isTalkViewNull()) {
            removeTalkView();
            AppUtils.getTvl_view(getContext()).onDestroy();
        }
    }

    OnDisFromViewListener listener;

    public void setListener(OnDisFromViewListener listener) {
        this.listener = listener;
    }

    public void removeTalkView() {
        fl_root.removeView(AppUtils.getTvl_view(getContext()));
    }

    public void reAddTalkView() {
        flp.gravity = Gravity.BOTTOM;
        fl_root.addView(AppUtils.getTvl_view(getContext()), flp);
        AppUtils.getTvl_view(getContext()).setParentLayout(this);
    }

    public interface OnDisFromViewListener {
        void onDisFromView();

        void onShowFromView();
    }

}
