package huaiye.com.vvs.ui.home.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMsgCaptureQualityNotify;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.media.MediaStatus;
import com.huaiye.sdk.media.capture.HYCapture;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.params.talk.inner.TalkingUser;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._model.UserModel;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.talk.ParamsJoinTalk;
import com.huaiye.sdk.sdkabi._params.talk.ParamsQuitTalk;
import com.huaiye.sdk.sdkabi._params.talk.ParamsStartTalk;
import com.huaiye.sdk.sdkabi.abilities.talk.callback.CallbackJoinTalk;
import com.huaiye.sdk.sdkabi.abilities.talk.callback.CallbackQuitTalk;
import com.huaiye.sdk.sdkabi.abilities.talk.callback.CallbackStartTalk;
import com.huaiye.sdk.sdpmsgs.io.CNotifyReconnectStatus;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CGetTalkbackInfoRsp;
import com.huaiye.sdk.sdpmsgs.talk.CJoinTalkbackRsp;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyKickUserTalkback;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackPeerUserOption;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserSpeakSet;
import com.huaiye.sdk.sdpmsgs.talk.CQuitTalkbackRsp;
import com.huaiye.sdk.sdpmsgs.talk.CStartTalkbackReq;
import com.huaiye.sdk.sdpmsgs.talk.CStartTalkbackRsp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.AcceptDiaoDu;
import huaiye.com.vvs.bus.CloseView;
import huaiye.com.vvs.bus.FinishDiaoDu;
import huaiye.com.vvs.bus.PhoneStatus;
import huaiye.com.vvs.bus.WaitViewAllFinish;
import huaiye.com.vvs.common.AppAudioManagerWrapper;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ErrorMsg;
import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.common.rx.CommonSubscriber;
import huaiye.com.vvs.common.rx.RxUtils;
import huaiye.com.vvs.common.views.WindowManagerUtils;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.AppMessages;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static huaiye.com.vvs.common.AppUtils.STRING_KEY_camera;
import static huaiye.com.vvs.common.AppUtils.ctx;
import static huaiye.com.vvs.common.AppUtils.showToast;


/**
 * author: admin
 * date: 2018/01/17
 * version: 0
 * mail: secret
 * desc: TalkActivity
 */

public class TalkViewLayout extends FrameLayout implements View.OnClickListener {

    public static String TAG = TalkViewLayout.class.getSimpleName();
    TextView tv_time;
    TextView tv_name;
    View ll_menu;
    View ll_bottom;
    View ll_mic;
    ImageView iv_mic;
    View tv_end_talk;
    View ll_speaker;
    ImageView iv_speaker;
    TextView tv_speaker;
    ImageView menu_iv_voice;
    TextView tv_inhao;

    boolean isTalkStarter;
    CStartTalkbackReq.ToUser toUser;
    int nTalkID;
    String strTalkDomainCode;

    AppAudioManagerWrapper audio;
    boolean isChangeTalk;
    RxUtils rxUtils;
    /**
     * 本地声音
     */
    private boolean isVoiceOpened = true;
    /**
     * 是否加入聊天成功
     */
    private boolean isSuccess;

    /**
     * 计时开始
     */
    private Disposable mDisposable;
    CommonSubscriber subscriber;

    CNotifyInviteUserJoinMeeting currentMeetingInvite;
    CNotifyUserJoinTalkback currentTalkInvite;

    String otherId;
    String otherDomain;
    /**
     * 是否可以说话
     */
    private boolean isAudioOn;


    private View parentLayout;

    public TalkViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(context).inflate(R.layout.main_talk_layout, null);
        tv_time = view.findViewById(R.id.tv_time);
        tv_name = view.findViewById(R.id.tv_name);
        ll_bottom = view.findViewById(R.id.ll_bottom);
        ll_mic = view.findViewById(R.id.ll_mic);
        iv_mic = view.findViewById(R.id.iv_mic);
        tv_end_talk = view.findViewById(R.id.tv_end_talk);
        ll_speaker = view.findViewById(R.id.ll_speaker);
        iv_speaker = view.findViewById(R.id.iv_speaker);
        tv_speaker = view.findViewById(R.id.tv_speaker);
        ll_menu = view.findViewById(R.id.ll_menu);
        menu_iv_voice = view.findViewById(R.id.menu_iv_voice);
        tv_inhao = view.findViewById(R.id.tv_inhao);

        ll_mic.setOnClickListener(this);
        tv_end_talk.setOnClickListener(this);
        ll_speaker.setOnClickListener(this);

        addView(view);

        setVisibility(GONE);

    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public TalkViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TalkViewLayout(@NonNull Context context) {
        this(context, null);
    }


    /**
     * 初始化必备
     */
    private void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (rxUtils == null)
            rxUtils = new RxUtils();


        isVoiceOpened = true;
        iv_mic.setImageResource(isVoiceOpened ? R.drawable.btn_jingyin : R.drawable.btn_quxiaojingyin);
        HYClient.getHYPlayer().setAudioOnEx(isVoiceOpened);

        isAudioOn = true;
        HYClient.getHYCapture().setCaptureAudioOn(isAudioOn);
        iv_speaker.setImageResource(isAudioOn ? R.drawable.btn_jinmai : R.drawable.btn_quxiaojinmai);
        tv_speaker.setText(isAudioOn ? AppUtils.getString(R.string.jinmai) : AppUtils.getString(R.string.jiejinmai));

    }

    /**
     * 如果会话连接断开了就关闭本地
     */
    public void closeIfTalkDisconnect() {
        HYClient.getModule(ApiTalk.class).requestTalkDetail(SdkParamsCenter.Talk.RequestTalkDetail()
                .setTalkDomainCode(strTalkDomainCode)
                .setTalkID(nTalkID), new SdkCallback<CGetTalkbackInfoRsp>() {
            @Override
            public void onSuccess(CGetTalkbackInfoRsp resp) {
                com.huaiye.sdk.logger.Logger.debug(TAG, "closeIfTalkDisconnect resp  " + resp.nStatus + " " + resp.listUserInfo.size());
                if (resp.listUserInfo == null || resp.listUserInfo.size() == 0 || resp.isTalkFinished()) {
                    closeVideo();
                }
            }

            @Override
            public void onError(ErrorInfo error) {
                com.huaiye.sdk.logger.Logger.debug(TAG, "closeIfTalkDisconnect onError  " + error.getMessage());

            }
        });
    }

    void joinTalk(String domain, int id, CNotifyUserJoinTalkback bean) {
        AppUtils.isTalk = true;
        isSuccess = false;
        tv_name.setText(AppUtils.getString(R.string.ing_with) + bean.strFromUserName + AppUtils.getString(R.string.ing_with_talk));
        this.otherId = bean.strFromUserID;
        this.otherDomain = bean.strFromUserDomainCode;
        this.strTalkDomainCode = domain;
        this.nTalkID = id;
        this.isTalkStarter = false;
        init();
        //这里只显示自己没用,因为父控件接收到了CNotifyTalkbackStatusInfo消息走到了closeWaitViewAll把自己隐藏了
        //所以需要连带着父控件也显示出来
        if (parentLayout != null) {
            parentLayout.setVisibility(View.VISIBLE);
        }
        setVisibility(VISIBLE);

        ParamsJoinTalk paramsJoinTalk = SdkParamsCenter.Talk.JoinTalk()
                .setCameraIndex(SP.getInteger(STRING_KEY_camera, -1) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                .setMediaMode(SdkBaseParams.MediaMode.Audio)
                .setCaptureTrunkMessage("AndroidSdk Capture From Talk")
                .setIsAutoStopCapture(true)
                .setAgreeMode(SdkBaseParams.AgreeMode.Agree)
                .setTalkDomainCode(strTalkDomainCode)
                .setTalkId(nTalkID)
                .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT);
        if (!AppUtils.isCaptureLayoutShowing) {
            HYClient.getHYCapture().stopCapture(null);
        } else {
            paramsJoinTalk
                    .setMediaMode(SdkBaseParams.MediaMode.AudioAndVideo)
                    .setIsAutoStopCapture(false);
        }
        HYClient.getHYCapture().setCameraConferenceMode(HYCapture.CameraConferenceMode.PORTRAIT);
        HYClient.getModule(ApiTalk.class).joinTalking(paramsJoinTalk,
                new CallbackJoinTalk() {
                    @Override
                    public TextureView onPlayerPreviewNotEnough() {
                        return null;

                    }

                    @Override
                    public void onTalkStatusChanged(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo) {
                    }

                    @Override
                    public void onUserRealPlayError(UserModel userModel, ErrorInfo errorInfo) {
                    }

                    @Override
                    public void onKickedFromTalk(CNotifyKickUserTalkback cNotifyKickUserTalkback) {
                    }

                    @Override
                    public void onCaptureStatusChanged(SdpMessageBase bean) {
                        if (bean instanceof CNotifyReconnectStatus){
                            CNotifyReconnectStatus cNotifyReconnectStatus = (CNotifyReconnectStatus) bean;
                            if (cNotifyReconnectStatus.getConnectionStatus() == SdkBaseParams.ConnectionStatus.Connecting
                                    || cNotifyReconnectStatus.getConnectionStatus() == SdkBaseParams.ConnectionStatus.Disconnected){
                                int pre = 0;
                                tv_inhao.setText(AppUtils.getCaptureZhiLiangTxt(pre));
                                menu_iv_voice.setImageResource(AppUtils.getCaptureZhiLiangImg(pre));
                            }
                            return;
                        }

                        switch (MediaStatus.get(bean)) {
                            case CAPTURE_QUALITY:
                                SdpMsgCaptureQualityNotify msg = (SdpMsgCaptureQualityNotify) bean;
                                int max = AppUtils.getCapturePresetOptionMax();
                                float pre = ((float) msg.m_nCurQuality + 1) / (max + 1);
                                tv_inhao.setText(AppUtils.getCaptureZhiLiangTxt(pre));
                                menu_iv_voice.setImageResource(AppUtils.getCaptureZhiLiangImg(pre));
                                break;
                        }
                    }

                    @Override
                    public void onUserVideoStatusChanged(UserModel userModel, SdpMessageBase sdpMessageBase) {
                        //收到对方关闭的消息 就关闭自己
                        if (sdpMessageBase instanceof SdkMsgNotifyPlayStatus && ((SdkMsgNotifyPlayStatus) sdpMessageBase).isStopped()) {
                            closeVideo();
                        }
                    }

                    @Override
                    public void onUserSpeakerStatusChanged(CNotifyUserSpeakSet cNotifyUserSpeakSet) {
                    }

                    @Override
                    public void onTalkFinished() {
                    }

                    @Override
                    public void onSuccess(CJoinTalkbackRsp cJoinTalkbackRsp) {
//                        iv_speaker.setImageResource(R.drawable.ic_loudspeaker_on);
//                        tv_speaker.setText("免提");
//                        isSpeakerOn = true;
                        initSound();
                        startTime();
                        ll_menu.setVisibility(VISIBLE);
                        iv_mic.setImageResource(R.drawable.btn_jingyin);

                        isSuccess = true;
                        isChangeTalk = false;
                        setSpeakOn();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        MCApp.getInstance().getTopActivity().showToast(ErrorMsg.getMsg(ErrorMsg.join_talk_err_code));
                        createError("from joinTalk");
                    }
                });
    }

    public void createTalk(CStartTalkbackReq.ToUser user) {
        AppUtils.isTalk = true;
        isSuccess = false;
        this.otherId = user.strToUserID;
        this.otherDomain = user.strToUserDomainCode;
        this.toUser = user;
        this.isTalkStarter = true;
        tv_name.setText(AppUtils.getString(R.string.ing_with) + user.strToUserName + AppUtils.getString(R.string.ing_with_talk));
        setVisibility(VISIBLE);
        init();

        HYClient.getHYCapture().setCameraConferenceMode(HYCapture.CameraConferenceMode.PORTRAIT);
        ParamsStartTalk paramsStartTalk = SdkParamsCenter.Talk.StartTalk()
                .setTalkMode(SdkBaseParams.TalkMode.Normal)
                .setTalkName(AppUtils.getString(R.string.audio_start) + " " + AppDatas.Auth().getUserName() + " " + AppUtils.getString(R.string.to_start) + " " + user.strToUserName)
                .setMemberMediaMode(SdkBaseParams.MediaMode.Audio)
                .setSelfMediaMode(SdkBaseParams.MediaMode.Audio)
                .setAutoStopCapture(true)
                .setOpenRecord(true)
                .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT)
                .setCameraIndex(SP.getInteger(STRING_KEY_camera, -1) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                .addInvitedUserInfo(toUser);
        if (!AppUtils.isCaptureLayoutShowing) {
            HYClient.getHYCapture().stopCapture(null);
        } else {
            paramsStartTalk
                    .setSelfMediaMode(SdkBaseParams.MediaMode.AudioAndVideo)
                    .setAutoStopCapture(false);
        }

        HYClient.getModule(ApiTalk.class).startTalking(paramsStartTalk, new CallbackStartTalk() {
            @Override
            public void onTalkStatusChanged(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo) {
            }

            @Override
            public void onRefuseTalk(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                showToast(AppUtils.getString(R.string.duifang_refuse_talk_talk_video));
                isSuccess = false;
                closeVideo();
            }

            @Override
            public void onAgreeTalk(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                EventBus.getDefault().post(new CloseView("TalkView Create onAgree"));
                initSound();

                ll_menu.setVisibility(VISIBLE);
                isSuccess = true;
                startTime();
                setSpeakOn();
            }

            @Override
            public TextureView onPlayerPreviewNotEnough() {
                return null;
            }

            @Override
            public void onUserRealPlayError(UserModel userModel, ErrorInfo errorInfo) {
            }

            @Override
            public void onNoResponse(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                showToast(AppUtils.getString(R.string.duifang_no_xiangying));
                createError("talkview 对方不响应");
            }

            @Override
            public void onUserOffline(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                showToast(AppUtils.getString(R.string.duifang_offline));
                createError("talk 对方不在线");
            }

            @Override
            public void onUserQuitTalk(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                showToast(AppUtils.getString(R.string.duifang_exite_talk_talk_video));
                createError("talkview 对方退出了语音调度");
            }

            @Override
            public void onCaptureStatusChanged(SdpMessageBase base) {
                if (base instanceof CNotifyReconnectStatus){
                    CNotifyReconnectStatus cNotifyReconnectStatus = (CNotifyReconnectStatus) base;
                    if (cNotifyReconnectStatus.getConnectionStatus() == SdkBaseParams.ConnectionStatus.Connecting
                            || cNotifyReconnectStatus.getConnectionStatus() == SdkBaseParams.ConnectionStatus.Disconnected){
                        int pre = 0;
                        tv_inhao.setText(AppUtils.getCaptureZhiLiangTxt(pre));
                        menu_iv_voice.setImageResource(AppUtils.getCaptureZhiLiangImg(pre));
                    }
                    return;
                }

                switch (MediaStatus.get(base)) {
                    case CAPTURE_QUALITY:
                        SdpMsgCaptureQualityNotify msg = (SdpMsgCaptureQualityNotify) base;
                        int max = AppUtils.getCapturePresetOptionMax();
                        float pre = ((float) msg.m_nCurQuality + 1) / (max + 1);
                        tv_inhao.setText(AppUtils.getCaptureZhiLiangTxt(pre));
                        menu_iv_voice.setImageResource(AppUtils.getCaptureZhiLiangImg(pre));
                        break;
                }

            }

            @Override
            public void onUserVideoStatusChanged(UserModel userModel, SdpMessageBase sdpMessageBase) {
                //收到对方关闭的消息 就关闭自己
                if (sdpMessageBase instanceof SdkMsgNotifyPlayStatus && ((SdkMsgNotifyPlayStatus) sdpMessageBase).isStopped()) {
                    closeVideo();
                }
            }

            @Override
            public void onUserSpeakerStatusChanged(CNotifyUserSpeakSet cNotifyUserSpeakSet) {
            }

            @Override
            public void onTalkFinished() {
            }

            @Override
            public void onSuccess(CStartTalkbackRsp cStartTalkbackRsp) {
                strTalkDomainCode = cStartTalkbackRsp.strTalkbackDomainCode;
                nTalkID = cStartTalkbackRsp.nTalkbackID;
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                MCApp.getInstance().getTopActivity().showToast(ErrorMsg.getMsg(ErrorMsg.start_talk_err_code));
                createError("from createTalk");
            }
        });


    }

    private void initSound() {
        audio = new AppAudioManagerWrapper();
        audio.start();
    }


    public void setParentLayout(View parentlayout) {
        this.parentLayout = parentlayout;
    }

    public void startTime() {

        MCApp.getInstance().getTopActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        subscriber = new CommonSubscriber<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                mDisposable = d;
            }

            @Override
            public void onNext(Long o) {
                int hour = (int) (o / (60 * 60));
                int min = (int) ((o - hour * 3600) / 60);
                int second = (int) (o - hour * 3600 - min * 60);
                String strH, strM, strS;
                strH = hour < 10 ? "0" + hour : "" + hour;
                strM = min < 10 ? "0" + min : "" + min;
                strS = second < 10 ? "0" + second : "" + second;

                tv_time.setText("持续时间" + strH + ":" + strM + ":" + strS);

                WindowManagerUtils.showTime(strH + ":" + strM + ":" + strS);
            }
        };

        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private void setSpeakOn() {
        rxUtils.doDelay(500, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                HYClient.getHYAudioMgr().from(getContext()).setSpeakerphoneOn(true);
            }
        }, "speakon");
    }


    /**
     * 创建出错
     */
    private void createError(String from) {
        closeVideo();
    }

    public void closeVideo() {
        //无网络情况，音频点击取消
        EventBus.getDefault().post(new FinishDiaoDu("talk view layout 516"));
        if (TextUtils.isEmpty(strTalkDomainCode) || nTalkID < 0) {
            return;
        }
        endTalk();

    }

    private void endTalk() {
        endTalk(null);
    }

    public void endTalk(CallbackQuitTalk callbackQuitTalk) {
        ParamsQuitTalk paramsQuitTalk = SdkParamsCenter.Talk.QuitTalk()
                .setTalkDomainCode(strTalkDomainCode)
                .setTalkId(nTalkID)
                .setStopCapture(true);
        if (AppUtils.isCaptureLayoutShowing) {
            paramsQuitTalk.setStopCapture(false);
        }

        HYClient.getModule(ApiTalk.class).quitTalking(paramsQuitTalk, callbackQuitTalk);
    }

    public void onBackPressed() {
        ((MCApp) ctx).getTopActivity().getLogicDialog()
                .setTitleText(AppUtils.getString(R.string.notice))
                .setMessageText(AppUtils.getString(R.string.is_exite_talk_diaodu))
                .setConfirmClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeRel();
                    }
                })
                .show();
    }

    public void closeRel() {
        closeVideo();
        EventBus.getDefault().post(new WaitViewAllFinish("talkviewlayout onBackPressed"));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_end_talk) {
            onBackPressed();
            return;
        }
        if (!isSuccess) {
            MCApp.getInstance().getTopActivity().showToast(AppUtils.getString(R.string.weijiaru_talk_diaodu));
            return;
        }
        switch (v.getId()) {
            case R.id.ll_mic:
                isVoiceOpened = !isVoiceOpened;
                iv_mic.setImageResource(isVoiceOpened ? R.drawable.btn_jingyin : R.drawable.btn_quxiaojingyin);
                TalkingUser user = HYClient.getHYPlayer().Talk()
                        .getTalkingUserVideoRef(otherId, otherDomain);
                HYClient.getHYPlayer().setAudioOn(isVoiceOpened, user);
                break;
            case R.id.ll_speaker:
                isAudioOn = HYClient.getHYCapture().toggleCaptureAudio();
                iv_speaker.setImageResource(isAudioOn ? R.drawable.btn_jinmai : R.drawable.btn_quxiaojinmai);
                tv_speaker.setText(isAudioOn ? AppUtils.getString(R.string.jinmai) : AppUtils.getString(R.string.jiejinmai));
                break;
        }
    }

    public void onDestroy() {

        endTalk();

        AppUtils.isTalk = false;

        strTalkDomainCode = "";
        nTalkID = -1;

        if (getVisibility() == GONE) {
            return;
        }

        if (audio != null) {
            audio.stop();
            audio = null;
        }

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        if (subscriber != null) {
            subscriber = null;
        }
        if (mDisposable != null) {
            mDisposable = null;
        }

        if (rxUtils != null) {
            rxUtils.clearAll();
        }
        setVisibility(GONE);

        if (ll_menu != null) {
            ll_menu.setVisibility(GONE);
        }
        EventBus.getDefault().post(new FinishDiaoDu("talk view layout 517"));
        EventBus.getDefault().unregister(this);
        isSuccess = false;
        AppUtils.reSetTalkView();

        if(MCApp.getInstance().getTopActivity() != null) {
            MCApp.getInstance().getTopActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PhoneStatus status) {
        if (status.isBusy) {
            createError("TalkView phonestatus");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyMeetingStatusInfo info) {
        if (currentMeetingInvite == null) {
            return;
        }
        if (info.nMeetingID == currentMeetingInvite.nMeetingID
                && info.isMeetFinished() && ((MCApp) ctx).getTopActivity().getLogicTimeDialog().isShowing()) {
            ((MCApp) ctx).getTopActivity().getLogicTimeDialog().dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyTalkbackStatusInfo info) {
        if (info.nTalkbackID == nTalkID && info.isTalkingStopped() && !isChangeTalk) {
            MCApp.getInstance().getTopActivity().showToast(AppUtils.getString(R.string.talk_has_end));
            closeVideo();
        }

        if (currentTalkInvite == null) {
            return;
        }
        if (info.nTalkbackID == currentTalkInvite.nTalkbackID
                && info.isTalkingStopped() && ((MCApp) ctx).getTopActivity().getLogicTimeDialog().isShowing()) {
            ((MCApp) ctx).getTopActivity().getLogicTimeDialog().dismiss();
        }
    }


    public void onResume() {
        if (getVisibility() == GONE) {
            return;
        }
//        HYClient.getHYCapture().onCaptureFront();
//        HYClient.getHYPlayer().onPlayFront();
    }

    public void onPause() {
        if (getVisibility() == GONE) {
            return;
        }
//        HYClient.getHYCapture().onCaptureBackground();
//        HYClient.getHYPlayer().onPlayBackground();
    }


    public void onMeetInvite(final CNotifyInviteUserJoinMeeting data, final long millis) {
        if (getVisibility() == GONE) {
            return;
        }

        if (data == null) {
            return;
        }
        if (data.nMeetingStatus != 1) {
            return;
        }
        if (data.isForceInvite()) {
            return;
        }
        if (((MCApp) ctx).getTopActivity().getLogicTimeDialog().isShowing()) {
            HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                    .setMeetID(data.nMeetingID)
                    .setMeetDomainCode(data.strMeetingDomainCode), null);
            return;
        }
        currentMeetingInvite = data;
        final CNotifyInviteUserJoinMeeting temp = data;
        // 会议中来会议邀请，对话框提示
        ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setMessageText(temp.strInviteUserName + AppUtils.getString(R.string.is_accept_meet_diaodu))
                .setTitleText(AppUtils.getString(R.string.invisitor_title))
                .setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppMessages.get().del(millis);
                        HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                                .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                                .setMeetID(temp.nMeetingID)
                                .setMeetDomainCode(temp.strMeetingDomainCode), null);
                    }
                })
                .setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppMessages.get().del(millis);
                        HYClient.getModule(ApiTalk.class).quitTalking(SdkParamsCenter.Talk.QuitTalk()
                                .setTalkDomainCode(strTalkDomainCode)
                                .setTalkId(nTalkID)
                                .setStopCapture(true), new CallbackQuitTalk() {
                            @Override
                            public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                                return true;
                            }

                            @Override
                            public void onSuccess(CQuitTalkbackRsp cQuitTalkbackRsp) {
                                EventBus.getDefault().post(new AcceptDiaoDu(temp, null, null, millis));
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                MCApp.getInstance().getTopActivity().showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                            }
                        });
                    }
                });
        ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                currentMeetingInvite = null;
                currentTalkInvite = null;
            }
        });
        ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setMeetMessage(true, data.nMeetingID + "", data.strMeetingDomainCode).show();
    }


}
