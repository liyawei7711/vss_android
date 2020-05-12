package huaiye.com.vvs.ui.talk;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiye.cmf.JniIntf;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMsgCaptureQualityNotify;
import com.huaiye.cmf.sdp.SdpMsgCommonUDPMsg;
import com.huaiye.cmf.sdp.SdpMsgFindLanCaptureDeviceRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.MediaStatus;
import com.huaiye.sdk.media.capture.HYCapture;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._model.UserModel;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.talk.ParamsQuitTalk;
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
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.AcceptDiaoDu;
import huaiye.com.vvs.bus.CloseTalkVideoActivity;
import huaiye.com.vvs.bus.CloseView;
import huaiye.com.vvs.bus.FinishDiaoDu;
import huaiye.com.vvs.bus.MeetInvistor;
import huaiye.com.vvs.bus.PhoneStatus;
import huaiye.com.vvs.bus.ShowChangeSizeView;
import huaiye.com.vvs.bus.TalkInvistor;
import huaiye.com.vvs.bus.WaitViewAllFinish;
import huaiye.com.vvs.common.AppAudioManagerWrapper;
import huaiye.com.vvs.common.AppBaseFragment;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.DoubleClickListener;
import huaiye.com.vvs.common.ErrorMsg;
import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.common.rx.CommonSubscriber;
import huaiye.com.vvs.common.rx.RxUtils;
import huaiye.com.vvs.common.views.PermissionUtils;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.AppMessages;
import huaiye.com.vvs.dao.msgs.VssMessageBean;
import huaiye.com.vvs.dao.msgs.VssMessageListBean;
import huaiye.com.vvs.dao.msgs.VssMessageListMessages;
import huaiye.com.vvs.ui.chat.ChatActivity;
import huaiye.com.vvs.ui.home.MainActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_VGA;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_camera;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_capture;
import static huaiye.com.vvs.common.AppUtils.isVideo;

/**
 * author: admin
 * date: 2018/07/20
 * version: 0
 * mail: secret
 * desc: TalkVideoFragment
 */

@BindLayout(R.layout.main_talk_video_layout_fragment)
public class TalkVideoFragment extends AppBaseFragment {
    @BindView(R.id.ll_root)
    RelativeLayout ll_root;
    @BindView(R.id.tv_notice)
    TextView tv_notice;

    TextureView texture_bigger;
    @BindView(R.id.texture_smaller)
    TextureView texture_smaller;

    @BindView(R.id.iv_change_size)
    View iv_change_size;
    @BindView(R.id.iv_change_camera)
    View iv_change_camera;
    @BindView(R.id.iv_waizhi)
    View iv_waizhi;
    @BindView(R.id.ll_bottom)
    View ll_bottom;
    @BindView(R.id.ll_mic)
    View ll_mic;
    @BindView(R.id.ll_zhiliang)
    View ll_zhiliang;
    @BindView(R.id.iv_mic)
    ImageView iv_mic;
    @BindView(R.id.tv_end_talk)
    View tv_end_talk;
    @BindView(R.id.tv_waite)
    View tv_waite;
    @BindView(R.id.ll_speaker)
    View ll_speaker;

    @BindView(R.id.iv_speaker)
    ImageView iv_speaker;
    @BindView(R.id.tv_speaker)
    TextView tv_speaker;
    @BindView(R.id.tv_talk_time)
    TextView tv_talk_time;
    @BindView(R.id.menu_iv_voice)
    ImageView menu_iv_voice;
    @BindView(R.id.tv_inhao)
    TextView tv_inhao;

    boolean isTalkStarter;
    CStartTalkbackReq.ToUser toUser;
    int nTalkID;
    String strTalkDomainCode;

    AppAudioManagerWrapper audio;
    boolean isChangeTalk;
    /**
     * 本地声音
     */
    private boolean isVoiceOpened = true;
    /**
     * 是否可以说话
     */
    private boolean isAudioOn;

    CNotifyInviteUserJoinMeeting currentMeetingInvite;
    CNotifyUserJoinTalkback currentTalkInvite;
    private SdpMsgCommonUDPMsg currentUDMsg;
    private String currentIP;
    private IChangeSize iChangeSize;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onViewCreated(view, savedInstanceState);
        getNavigate().setVisibility(GONE);

        texture_smaller.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                if (v.getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    switchPreviewPos(false);
                }
            }
        });

    }

    /**
     * 初始化必备
     */
    private void init() {
        Log.i("AppBaseOpen", "open_fragment init " + this.getClass().getSimpleName());
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        initSound();

        texture_bigger = new TextureView(getContext());
        texture_bigger.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ll_root.addView(texture_bigger, 0);

        texture_bigger.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                if (v.getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    switchPreviewPos(false);
                }
            }
        });
        switchPreviewPos(true);

        isVoiceOpened = true;
        iv_mic.setImageResource(isVoiceOpened ? R.drawable.btn_jingyin : R.drawable.btn_quxiaojingyin);
        HYClient.getHYPlayer().setAudioOnEx(isVoiceOpened, texture_bigger);

        isAudioOn = true;
        HYClient.getHYCapture().setCaptureAudioOn(isAudioOn);
        iv_speaker.setImageResource(isAudioOn ? R.drawable.btn_jinmai : R.drawable.btn_quxiaojinmai);
        tv_speaker.setText(isAudioOn ? AppUtils.getString(R.string.jinmai) : AppUtils.getString(R.string.jiejinmai));

    }

    /**
     * 交换预览窗口
     */
    void switchPreviewPos(boolean isInit) {

        RelativeLayout.LayoutParams smallerParams = (RelativeLayout.LayoutParams) texture_smaller.getLayoutParams();
        RelativeLayout.LayoutParams biggerParams = (RelativeLayout.LayoutParams) texture_bigger.getLayoutParams();

        RelativeLayout parent = (RelativeLayout) texture_smaller.getParent();


        if (isInit) {
            if (smallerParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                int width;
                int height = AppUtils.getSize(160);
                switch (SP.getParam(STRING_KEY_capture, "").toString()) {
                    case STRING_KEY_VGA:
                        width = AppUtils.getSize(120);
                        break;
                    default:
                        width = AppUtils.getSize(90);
                        break;
                }
                smallerParams = new RelativeLayout.LayoutParams(width, height);
                smallerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                texture_smaller.setLayoutParams(smallerParams);

                parent.bringChildToFront(texture_smaller);
                parent.bringChildToFront(ll_bottom);
                parent.bringChildToFront(iv_change_camera);
                parent.bringChildToFront(iv_waizhi);
                parent.bringChildToFront(iv_change_size);
            }
        } else {
            texture_smaller.setLayoutParams(biggerParams);
            texture_bigger.setLayoutParams(smallerParams);

            if (smallerParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                // 采集预览全屏
                // 采集->右上角  播方->底部全屏

                parent.bringChildToFront(texture_smaller);
                parent.bringChildToFront(ll_bottom);
                parent.bringChildToFront(iv_change_camera);
                parent.bringChildToFront(iv_waizhi);
                parent.bringChildToFront(iv_change_size);
            } else {
                // 播放预览全屏
                // 采集->底部全屏 播放->右上角

                parent.bringChildToFront(texture_bigger);
                parent.bringChildToFront(ll_bottom);
                parent.bringChildToFront(iv_change_camera);
                parent.bringChildToFront(iv_waizhi);
                parent.bringChildToFront(iv_change_size);
            }
        }

        parent.bringChildToFront(ll_zhiliang);
        parent.bringChildToFront(tv_notice);

    }

    private void setSpeakOn() {
        new RxUtils<>().doDelayOn(500, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                HYClient.getHYAudioMgr().from(getContext()).setSpeakerphoneOn(true);
            }
        });
    }

    public void createTalk(CStartTalkbackReq.ToUser user, SdpMsgFindLanCaptureDeviceRsp device) {
        AppUtils.isVideo = true;
        this.toUser = user;
        this.isTalkStarter = true;
        init();

        tv_waite.setVisibility(VISIBLE);

        resetCaptureSize();
        HYClient.getHYCapture().stopCapture(null);
        HYClient.getHYCapture().setCameraConferenceMode(HYCapture.CameraConferenceMode.PORTRAIT);
        if (device != null) {
            if (((MainActivity) getContext()).mP2PSample == null) {
                showToast(AppUtils.getString(R.string.p2p_error_init));
                realClose();
                return;
            }
            currentIP = device.m_strIP;
            ((MainActivity) getContext()).mP2PSample.setPlayerPreview(texture_bigger);
            ((MainActivity) getContext()).mP2PSample.setCapturePreview(texture_smaller);
            ((MainActivity) getContext()).mP2PSample.requestTalk(device.m_strIP, new SdkCallback<SdkBaseParams.AgreeMode>() {
                @Override
                public void onSuccess(SdkBaseParams.AgreeMode resp) {
                    if (resp == SdkBaseParams.AgreeMode.Refuse) {
                        showToast(AppUtils.getString(R.string.duifang_refuse_talk_video));
                        realClose();
                    } else {
                        tv_waite.setVisibility(GONE);
                        EventBus.getDefault().post(new CloseView("TalkVideoViewLayout onAgreeTalk"));
                        setSpeakOn();
                    }
                }

                @Override
                public void onError(ErrorInfo error) {
                    showToast(ErrorMsg.getMsg(ErrorMsg.start_talk_err_code));
                    realClose();
                }
            });
            return;
        }
        HYClient.getModule(ApiTalk.class).startTalking(SdkParamsCenter.Talk.StartTalk()
                .setTalkMode(SdkBaseParams.TalkMode.Normal)
                .setTalkName(AppUtils.getString(R.string.video_start) + " " + AppDatas.Auth().getUserName() + " " + AppUtils.getString(R.string.to_start) + " " + user.strToUserName)
                .setAutoStopCapture(true)
                .setOpenRecord(true)
                .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT)
//                .setPlayerVideoScaleType(SdkBaseParams.VideoScaleType.ASPECT_CROP)
//                .setCaptureVideoScaleType(SdkBaseParams.VideoScaleType.ASPECT_CROP)
                .setCameraIndex(SP.getInteger(STRING_KEY_camera, -1) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                .setCapturePreview(texture_smaller)
                .setPlayerPreview(texture_bigger)
                .addInvitedUserInfo(toUser), new CallbackStartTalk() {
            @Override
            public void onTalkStatusChanged(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo) {
            }

            @Override
            public void onRefuseTalk(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                showToast(AppUtils.getString(R.string.duifang_refuse_talk_video));
                realClose();
            }

            @Override
            public void onAgreeTalk(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
//                showToast("onAgreeTalk");
                tv_waite.setVisibility(GONE);
                EventBus.getDefault().post(new CloseView("TalkVideoViewLayout onAgreeTalk"));
                setSpeakOn();
                start();
            }

            @Override
            public TextureView onPlayerPreviewNotEnough() {
//                showToast("onPlayerPreviewNotEnough");
                return null;
            }

            @Override
            public void onUserRealPlayError(UserModel userModel, ErrorInfo errorInfo) {
//                showToast("onUserRealPlayError");
            }

            @Override
            public void onNoResponse(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                showToast(AppUtils.getString(R.string.duifang_no_xiangying));
                createError("TalkVideoView 对方不响应");
            }

            @Override
            public void onUserOffline(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                showToast(AppUtils.getString(R.string.duifang_offline));
                createError("TalkVideoView 对方不在线");
            }

            @Override
            public void onUserQuitTalk(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                showToast(AppUtils.getString(R.string.duifang_exite_talk_video));
                createError("TalkVideoView 对方退出了视频调度");
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
//                showToast("onUserVideoStatusChanged");
            }

            @Override
            public void onUserSpeakerStatusChanged(CNotifyUserSpeakSet cNotifyUserSpeakSet) {
//                showToast("onUserSpeakerStatusChanged");
            }

            @Override
            public void onTalkFinished() {
//                showToast("onTalkFinished");
            }

            @Override
            public void onSuccess(CStartTalkbackRsp cStartTalkbackRsp) {
//                showToast("onSuccess");
                strTalkDomainCode = cStartTalkbackRsp.strTalkbackDomainCode;
                nTalkID = cStartTalkbackRsp.nTalkbackID;
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                showToast(ErrorMsg.getMsg(ErrorMsg.start_talk_err_code));
                realClose();
            }
        });
    }

    private void initSound() {
        audio = new AppAudioManagerWrapper();
        audio.start();
    }

    public void closeIfTalkDisconnect() {
        HYClient.getModule(ApiTalk.class).requestTalkDetail(SdkParamsCenter.Talk.RequestTalkDetail()
                .setTalkDomainCode(strTalkDomainCode)
                .setTalkID(nTalkID), new SdkCallback<CGetTalkbackInfoRsp>() {
            @Override
            public void onSuccess(CGetTalkbackInfoRsp resp) {
                com.huaiye.sdk.logger.Logger.debug("TalkVideoViewLayout", "closeIfTalkDisconnect resp  " + resp.nStatus + " " + resp.listUserInfo.size());
                if (resp.listUserInfo == null || resp.listUserInfo.size() == 0 || resp.isTalkFinished()) {
                    createError("对话关闭");
                }
            }

            @Override
            public void onError(ErrorInfo error) {
                com.huaiye.sdk.logger.Logger.debug("TalkVideoViewLayout", "closeIfTalkDisconnect onError  " + error.getMessage());

            }
        });
    }

    public void joinTalk(String domain, int id, SdpMsgCommonUDPMsg udpMsg) {
        AppUtils.isVideo = true;
        this.strTalkDomainCode = domain;
        this.nTalkID = id;
        this.isTalkStarter = false;
        init();

        tv_waite.setVisibility(GONE);

        resetCaptureSize();
        HYClient.getHYCapture().setCameraConferenceMode(HYCapture.CameraConferenceMode.PORTRAIT);

        if (udpMsg != null) {
            if (((MainActivity) getContext()).mP2PSample == null) {
                showToast(AppUtils.getString(R.string.p2p_error_init));
                realClose();
                return;
            }
            currentIP = udpMsg.m_strIP;
            ((MainActivity) getContext()).mP2PSample.setPlayerPreview(texture_bigger);
            ((MainActivity) getContext()).mP2PSample.setCapturePreview(texture_smaller);
            ((MainActivity) getContext()).mP2PSample.respTalkRequest(SdkBaseParams.AgreeMode.Agree, udpMsg);
            return;
        }

        HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                        .setCapturePreview(texture_smaller)
                        .setPlayerPreview(texture_bigger)
                        .setCameraIndex(SP.getInteger(STRING_KEY_camera, -1) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                        .setMediaMode(SdkBaseParams.MediaMode.AudioAndVideo)
                        .setCaptureTrunkMessage("AndroidSdk Capture From Talk")
                        .setIsAutoStopCapture(true)
//                        .setPlayerVideoScaleType(SdkBaseParams.VideoScaleType.ASPECT_CROP)
//                        .setCaptureVideoScaleType(SdkBaseParams.VideoScaleType.ASPECT_CROP)
                        .setAgreeMode(SdkBaseParams.AgreeMode.Agree)
                        .setTalkDomainCode(strTalkDomainCode)
                        .setTalkId(nTalkID)
                        .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT),
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
                            createError("对话关闭");
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
                        iv_mic.setImageResource(R.drawable.btn_jingyin);

                        isChangeTalk = false;
                        setSpeakOn();
                        start();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(ErrorMsg.join_talk_err_code));
                        realClose();
                    }
                });
    }

    private void resetCaptureSize() {
        switch (SP.getParam(STRING_KEY_capture, "").toString()) {
            case STRING_KEY_VGA:
                texture_smaller.getLayoutParams().width = AppUtils.getSize(120);
                break;
            default:
                texture_smaller.getLayoutParams().width = AppUtils.getSize(90);
                break;
        }
    }

    /**
     * 创建出错
     */
    private void createError(String from) {
        if (!TextUtils.isEmpty(strTalkDomainCode)) {
            endTalk();
        } else {
            if (((MainActivity) getContext()).mP2PSample != null && isVideo) {
                ((MainActivity) getContext()).mP2PSample.stopAll();
            }
        }
        realClose();

        EventBus.getDefault().post(new FinishDiaoDu("talk view fragment 532"));
    }

    private void realClose() {
        onDestroy();
        if (iChangeSize != null)
            iChangeSize.removeAll();
        EventBus.getDefault().post(new WaitViewAllFinish("TalkVideoViewLayout 481"));
        EventBus.getDefault().post(new ShowChangeSizeView(false));
    }

    void endTalk() {
        ParamsQuitTalk paramsQuitTalk = SdkParamsCenter.Talk.QuitTalk()
                .setTalkDomainCode(strTalkDomainCode)
                .setTalkId(nTalkID)
                .setStopCapture(true);
        if (AppUtils.isCaptureLayoutShowing) {
            paramsQuitTalk.setStopCapture(false);
        }
        HYClient.getModule(ApiTalk.class).quitTalking(paramsQuitTalk, null);
    }

    public void ToggleBackgroundState(final boolean enterBackground) {
        try {

            JniIntf.SetCapturerPreviewTexture(enterBackground ? null
                    : texture_smaller.getSurfaceTexture());
        } catch (Exception e) {
            Logger.log("ToggleBackgroundState Exception..." + e);
        }

    }

    @OnClick({R.id.ll_mic, R.id.tv_end_talk, R.id.ll_speaker, R.id.iv_change_camera, R.id.iv_waizhi, R.id.iv_change_size})
    public void onClick(View v) {
        if (v.getId() == R.id.tv_end_talk) {
            onBackPressed();
            return;
        }
        if (!HYClient.getHYPlayer().isVideoRendering(texture_bigger)) {
            showToast(AppUtils.getString(R.string.duifang_has_no_add));
            return;
        }
        switch (v.getId()) {
            case R.id.ll_mic:
                isVoiceOpened = !isVoiceOpened;
                iv_mic.setImageResource(isVoiceOpened ? R.drawable.btn_jingyin : R.drawable.btn_quxiaojingyin);
                HYClient.getHYPlayer().setAudioOnEx(isVoiceOpened, texture_bigger);
                break;
            case R.id.ll_speaker:
                isAudioOn = HYClient.getHYCapture().toggleCaptureAudio();
                iv_speaker.setImageResource(isAudioOn ? R.drawable.btn_jinmai : R.drawable.btn_quxiaojinmai);
                tv_speaker.setText(isAudioOn ? AppUtils.getString(R.string.jinmai) : AppUtils.getString(R.string.jiejinmai));
                break;
            case R.id.iv_change_camera:
                // 摄像头切换
                HYClient.getHYCapture().toggleInnerCamera();
                break;
            case R.id.iv_waizhi:
                // 摄像头切换
                HYClient.getHYCapture().requestUsbCamera();
                break;
            case R.id.iv_change_size:
                if (PermissionUtils.XiaoMiMobilePermission(AppUtils.ctx)) {
                    return;
                }
                if (iChangeSize != null) {
                    iChangeSize.changeSize();
                }
                EventBus.getDefault().post(new ShowChangeSizeView(true));
                break;
        }
    }


    @Override
    public void onDestroy() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        currentUDMsg = null;
        strTalkDomainCode = "";
        currentIP = "";
        nTalkID = -1;

        clearPlayer();
        if (texture_smaller != null) {
            texture_smaller.destroyDrawingCache();
        }

        if (audio != null) {
            audio.stop();
            audio = null;
        }

        EventBus.getDefault().post(new FinishDiaoDu("talk view fragment 599"));
        EventBus.getDefault().unregister(this);

        AppUtils.isVideo = false;
        super.onDestroy();
    }

    private void clearPlayer() {
        if (texture_bigger != null) {
            texture_bigger.destroyDrawingCache();
            ll_root.removeView(texture_bigger);
            texture_bigger = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTalkInvite(TalkInvistor data) {
        if (data == null) {
            closeTalkVideo(null);
            return;
        }
        if (data.talk == null && data.p2p_talk == null) {
            Logger.debug("TalkVideoActivity data.talk  null");
            closeIfTalkDisconnect();
            return;
        }
        onTalkInvite(data.talk, data.p2p_talk, data.millis);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMeetInvite(MeetInvistor data) {
        if (data == null) {
            AppUtils.getTvvl_view(getContext()).closeTalkVideo(null);
            return;
        }

        if (data.meet == null) {
            Logger.debug("TalkVideoActivity data.talk  null");
            closeIfTalkDisconnect();
            return;
        }
        if (data.meet.nMeetingStatus != 1) {
            return;
        }

        if (data.meet.isSelfMeetCreator()) {
            return;
        }
        onMeetInvite(data.meet, data.millis);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeTalkVideo(CloseTalkVideoActivity bean) {
        if (AppUtils.isVideo)
            createError("567");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PhoneStatus status) {
        if (status.isBusy) {
            createError("TalkVideoViewLayout onEvent(PhoneStatus status)");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyMeetingStatusInfo info) {
        if (currentMeetingInvite == null) {
            return;
        }
        if (info.nMeetingID == currentMeetingInvite.nMeetingID
                && info.isMeetFinished() && ((MainActivity) getActivity()).getLogicTimeDialog().isShowing()) {
            ((MainActivity) getActivity()).getLogicTimeDialog().dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyTalkbackStatusInfo info) {
        if (info.nTalkbackID == nTalkID && info.isTalkingStopped() && !isChangeTalk) {
            showToast(AppUtils.getString(R.string.duifang_has_end));
            realClose();
        }

        if (currentTalkInvite == null) {
            return;
        }
        if (info.nTalkbackID == currentTalkInvite.nTalkbackID
                && info.isTalkingStopped() && ((MainActivity) getActivity()).getLogicTimeDialog().isShowing()) {
            ((MainActivity) getActivity()).getLogicTimeDialog().dismiss();
        }
    }

    public void stopTalk(String ip) {
        if (ip.equals(currentIP)) {
            showToast(AppUtils.getString(R.string.duifang_has_end));
            realClose();
        }
        if (currentUDMsg != null && ip.equals(currentUDMsg.m_strIP)) {
            ((MainActivity) getActivity()).getLogicTimeDialog().dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VssMessageBean bean) {
        tv_notice.setText(AppUtils.getString(R.string.receive) + bean.fromUserName + AppUtils.getString(R.string.place_watch));
        tv_notice.setTag(bean);
        tv_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PermissionUtils.XiaoMiMobilePermission(AppUtils.ctx)) {
                    return;
                }

                VssMessageBean tag = (VssMessageBean) v.getTag();
                if (tag == null) {
                    return;
                }

                VssMessageListBean bean = VssMessageListMessages.get().getMessages(tag.sessionID);
                bean.isRead = 1;
                VssMessageListMessages.get().isRead(bean);

                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("listBean", bean);
                getContext().startActivity(intent);

                iv_change_size.performClick();
            }
        });
        tv_notice.setVisibility(VISIBLE);
        new RxUtils<>().doDelay(5000, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                tv_notice.setText("");
                tv_notice.setVisibility(GONE);
            }
        }, System.currentTimeMillis() + "");

    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppUtils.isVideo) {
            HYClient.getHYCapture().onCaptureFront();
            HYClient.getHYPlayer().onPlayFront();

            new RxUtils<>().doDelayOn(30, new RxUtils.IMainDelay() {
                @Override
                public void onMainDelay() {
                    ToggleBackgroundState(false);
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (AppUtils.isVideo) {
            HYClient.getHYCapture().onCaptureBackground();
            HYClient.getHYPlayer().onPlayBackground();
            ToggleBackgroundState(true);
        }
    }

    public void onBackPressed() {
        ((MainActivity) getActivity()).getLogicDialog()
                .setTitleText(AppUtils.getString(R.string.notice))
                .setMessageText(AppUtils.getString(R.string.is_exite_video_diaodu))
                .setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createError("TalkVideoViewLayout onBackPressed");
                    }
                })
                .show();
    }

    public void onMeetInvite(final CNotifyInviteUserJoinMeeting data, final long millis) {

        if (data == null) {
            return;
        }
        if (data.nMeetingStatus != 1) {
            return;
        }

        if (data.isForceInvite()) {
            return;
        }

        if (((MainActivity) getActivity()).getLogicTimeDialog().isShowing()) {
            HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                    .setMeetID(data.nMeetingID)
                    .setMeetDomainCode(data.strMeetingDomainCode), null);
            return;
        }
        currentMeetingInvite = data;
        final CNotifyInviteUserJoinMeeting temp = data;
        // 会议中来会议邀请，对话框提示
        ((MainActivity) getActivity()).getLogicTimeDialog().setMessageText(temp.strInviteUserName + AppUtils.getString(R.string.is_accept_meet_diaodu))
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

                        if (!TextUtils.isEmpty(strTalkDomainCode)) {
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
                                    realClose();
                                    EventBus.getDefault().post(new AcceptDiaoDu(temp, null, null, millis));
                                }

                                @Override
                                public void onError(ErrorInfo errorInfo) {
                                    showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                                }
                            });
                        } else {
                            ((MainActivity) getContext()).mP2PSample.stopAll();
                            realClose();
                            EventBus.getDefault().post(new AcceptDiaoDu(temp, null, null, millis));
                        }

                    }
                });
        ((MainActivity) getActivity()).getLogicTimeDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                currentMeetingInvite = null;
                currentTalkInvite = null;
            }
        });
        ((MainActivity) getActivity()).getLogicTimeDialog().setMeetMessage(true, data.nMeetingID + "", data.strMeetingDomainCode).show();
    }

    public void onTalkInvite(final CNotifyUserJoinTalkback data, final SdpMsgCommonUDPMsg udpMsg, final long millis) {

        if (data.isForceInvite()) {
            return;
        }

        if (((MainActivity) getActivity()).getLogicTimeDialog().isShowing()) {
            if (data != null) {
                // 会议中不接受对讲
                HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                        .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                        .setTalkId(data.nTalkbackID)
                        .setTalkDomainCode(data.strTalkbackDomainCode), null);
            } else if (udpMsg != null && ((MainActivity) getContext()).mP2PSample != null) {
                ((MainActivity) getContext()).mP2PSample.respTalkRequest(SdkBaseParams.AgreeMode.Refuse, udpMsg);
            }

            return;
        }

        if (data != null) {
            currentTalkInvite = data;
            final CNotifyUserJoinTalkback temp = data;
            // 会议中来会议邀请，对话框提示
            String str = "";
            if (temp.getRequiredMediaMode() == SdkBaseParams.MediaMode.AudioAndVideo) {
                str = AppUtils.getString(R.string.video);
            } else {
                str = AppUtils.getString(R.string.talk);
            }
            ((MainActivity) getActivity()).getLogicTimeDialog().setTitleText(AppUtils.getString(R.string.invisitor_title))
                    .setMessageText(data.strFromUserName + AppUtils.getString(R.string.invisitor_you) + str + "，" + AppUtils.getString(R.string.qiehuandao) + str + AppUtils.getString(R.string.diaodu_shifou))
                    .setCancelClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 会议中不接受对讲
                            AppMessages.get().del(millis);
                            HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                                    .setTalkId(temp.nTalkbackID)
                                    .setTalkDomainCode(temp.strTalkbackDomainCode), null);
                        }
                    }).setConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppMessages.get().del(millis);
                    isChangeTalk = true;
                    String tempDomainCode = strTalkDomainCode;
                    int tempnTalkID = nTalkID;

                    strTalkDomainCode = temp.strTalkbackDomainCode;
                    nTalkID = temp.nTalkbackID;
                    HYClient.getModule(ApiTalk.class).quitTalking(SdkParamsCenter.Talk.QuitTalk()
                            .setTalkDomainCode(tempDomainCode)
                            .setTalkId(tempnTalkID)
                            .setStopCapture(true), new CallbackQuitTalk() {
                        @Override
                        public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                            return false;
                        }

                        @Override
                        public void onSuccess(CQuitTalkbackRsp cQuitTalkbackRsp) {

                            clearPlayer();

                            if (temp.getRequiredMediaMode() == SdkBaseParams.MediaMode.AudioAndVideo) {
                                isTalkStarter = false;
                                toUser = null;
//                                ((MainActivity) getActivity()).showTalkVideoView();
                                joinTalk(strTalkDomainCode, nTalkID, udpMsg);
                            } else {
                                realClose();
                                EventBus.getDefault().post(new AcceptDiaoDu(null, temp, udpMsg, millis));
                            }

                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            showToast(AppUtils.getString(R.string.qiehuan_meet_video_false));
                        }
                    });
                }
            });
            ((MainActivity) getActivity()).getLogicTimeDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    currentMeetingInvite = null;
                    currentTalkInvite = null;
                    currentUDMsg = null;
                }
            });
            ((MainActivity) getActivity()).getLogicTimeDialog().setMeetMessage(false, "", "").show();

        } else if (udpMsg != null) {
            currentUDMsg = udpMsg;
            String str = AppUtils.getString(R.string.video);
            ((MainActivity) getActivity()).getLogicTimeDialog().setTitleText(AppUtils.getString(R.string.invisitor_title))
                    .setMessageText(udpMsg.m_strIP + AppUtils.getString(R.string.invisitor_you) + str + "，" + AppUtils.getString(R.string.qiehuandao) + str + AppUtils.getString(R.string.diaodu_shifou))
                    .setCancelClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 会议中不接受对讲
                            AppMessages.get().del(millis);
                            if (((MainActivity) getContext()).mP2PSample != null) {
                                ((MainActivity) getContext()).mP2PSample.respTalkRequest(SdkBaseParams.AgreeMode.Refuse, udpMsg);
                            }
                        }
                    }).setConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppMessages.get().del(millis);
                    isChangeTalk = true;
                    String tempDomainCode = strTalkDomainCode;
                    int tempnTalkID = nTalkID;

                    if (!TextUtils.isEmpty(tempDomainCode)) {
                        HYClient.getModule(ApiTalk.class).quitTalking(SdkParamsCenter.Talk.QuitTalk()
                                .setTalkDomainCode(tempDomainCode)
                                .setTalkId(tempnTalkID)
                                .setStopCapture(true), new CallbackQuitTalk() {
                            @Override
                            public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                                return false;
                            }

                            @Override
                            public void onSuccess(CQuitTalkbackRsp cQuitTalkbackRsp) {

                                clearPlayer();

                                isTalkStarter = false;
                                toUser = null;
//                                ((MainActivity) getActivity()).showTalkVideoView();
                                joinTalk(strTalkDomainCode, nTalkID, udpMsg);
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(AppUtils.getString(R.string.qiehuan_meet_video_false));
                            }
                        });
                    } else {
                        ((MainActivity) getContext()).mP2PSample.stopAll();
                        clearPlayer();

                        isTalkStarter = false;
                        toUser = null;
//                        ((MainActivity) getActivity()).showTalkVideoView();
                        joinTalk(strTalkDomainCode, nTalkID, udpMsg);
                    }
                }
            });
            ((MainActivity) getActivity()).getLogicTimeDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    currentMeetingInvite = null;
                    currentTalkInvite = null;
                    currentUDMsg = null;
                }
            });
            ((MainActivity) getActivity()).getLogicTimeDialog().setMeetMessage(false, "", "").show();
        } else {
            createError("TalkVideoViewLayout onTalkInvite data == null");
        }

    }

    public void setiChangeSize(IChangeSize iChangeSize) {
        this.iChangeSize = iChangeSize;
    }

    public interface IChangeSize {
        void changeSize();

        void removeAll();
    }

    Disposable mDisposable;
    CommonSubscriber subscriber;

    private void start() {

        ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        stopTime();

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

                tv_talk_time.setText(strH + ":" + strM + ":" + strS);

            }
        };

        startTime();
    }

    private void startTime() {
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        setSpeakOn();
    }

    private void stopTime() {
        tv_talk_time.setText("");
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        if (mDisposable != null) {
            mDisposable = null;
        }
        subscriber = null;
    }

}
