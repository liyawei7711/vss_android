package huaiye.com.vvs.ui.home.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.media.player.sdk.params.talk.inner.TalkingUserRealImpl;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.SDKInnerMessageCode;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.AcceptDiaoDu;
import huaiye.com.vvs.bus.WaitViewAllFinish;
import huaiye.com.vvs.common.AlarmMediaPlayer;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ScreenNotify;
import huaiye.com.vvs.common.rx.RxUtils;

import static android.view.View.VISIBLE;

public class VideoWaitAcceptDialog extends DialogFragment {

    public static final String TAG = "VideoWaitAcceptDialog";
    CNotifyUserJoinTalkback data;
    @BindView(R.id.texture)
    TextureView textureView;
    @BindView(R.id.iv_type)
    ImageView iv_type;
    @BindView(R.id.tv_name_id)
    TextView tv_name_id;
    @BindView(R.id.tv_notic)
    TextView tv_notic;
    @BindView(R.id.iv_refuse)
    ImageView iv_refuse;
    @BindView(R.id.iv_accept)
    ImageView iv_accept;
    Unbinder unbinder;
    DialogInterface.OnDismissListener onDismissListener;
    DialogInterface.OnShowListener    onShowListener;
//    AppAudioManagerWrapper appAudioManagerWrapper;

    public static VideoWaitAcceptDialog getInstance(CNotifyUserJoinTalkback data) {
        Gson gson = new Gson();
        String strParams = gson.toJson(data);
        Bundle bundle = new Bundle();
        bundle.putString("params", strParams);
        VideoWaitAcceptDialog videoWaitAcceptDialog = new VideoWaitAcceptDialog();
        videoWaitAcceptDialog.setArguments(bundle);
        return videoWaitAcceptDialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Dialog_FullScreen);
//        appAudioManagerWrapper = new AppAudioManagerWrapper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_wait_accept_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        String strParams = getArguments().getString("params");
        if (TextUtils.isEmpty(strParams)) {
            dismiss();
            return;
        }
        if (onShowListener != null){
            onShowListener.onShow(getDialog());
        }
        (getActivity()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Gson gson = new Gson();
        data = gson.fromJson(strParams, CNotifyUserJoinTalkback.class);
        init(data);
        startPreview();
    }

    private void startPreview() {
//        appAudioManagerWrapper.start();
        TalkingUserRealImpl usr = new TalkingUserRealImpl(null);
        usr.setBlur(true);
        usr.setAudioOn(false);
        usr.setTalkingUserDomainCode(data.strFromUserDomainCode)
                .setTalkingUserID(data.strFromUserName)
                .setTalkingUserTokenID(data.strFromUserTokenID)
                .setPlayerVideoScaleType(SdkBaseParams.VideoScaleType.ASPECT_FIT)
                .setPreview(textureView);

        HYClient.getHYPlayer()
                .startPlay(usr.setMixCallback(new VideoCallbackWrapper() {

                    @Override
                    public void onSuccess(VideoParams param) {
                        super.onSuccess(param);
                        Logger.debug("VideoWaitAcceptDialog success");
                        //播放视频后,播放的焦点变了,重新播放下来电音量
                        AlarmMediaPlayer.get().stop();
                        AlarmMediaPlayer.get().play(AlarmMediaPlayer.SOURCE_CALL_VOICE);
                    }

                    @Override
                    public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                        super.onError(param, errorInfo);

                        if (param.getPreview() != null) {

                        }


                        SdkCallback.ErrorInfo error = null;
                        if (errorInfo.getRespMessageId() == 4715) {
                            // 获取对方URL失败
                            error = new SdkCallback.ErrorInfo(
                                    SDKInnerMessageCode.TALK_REALPLAY_ERROR,
                                    "Get User Video URL Fail",
                                    errorInfo.getRespMessageId()
                            );
                        } else {
                            // 播放器播放失败
                            error = new SdkCallback.ErrorInfo(
                                    -1,
                                    "Video Player Fail",
                                    errorInfo.getRespMessageId()
                            );
                        }

                        TalkingUserRealImpl usr = (TalkingUserRealImpl) param;
                        String key = usr.getTalkingUserDomainCode() + ":" + usr.getTalkingUserID();
                        if (error != null){
                            Logger.debug(error.toString());
                        }
                    }

                    @Override
                    public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                        super.onVideoStatusChanged(param, msg);
                        switch (msg.GetMessageType()) {
                            case SdkMsgNotifyPlayStatus.SelfMessageId:
                                SdkMsgNotifyPlayStatus playStatus = (SdkMsgNotifyPlayStatus) msg;
                                if (playStatus.isStopped()) {
                                }
                                break;
                        }

                        TalkingUserRealImpl usr = (TalkingUserRealImpl) param;
                        String key = usr.getTalkingUserDomainCode() + ":" + usr.getTalkingUserID();
                    }
                }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (appAudioManagerWrapper != null){
//            appAudioManagerWrapper.stop();
//        }
        EventBus.getDefault().unregister(this);
        (getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick({R.id.iv_refuse, R.id.iv_accept})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_refuse:

                refuseBtn();
                break;
            case R.id.iv_accept:
                dismissAllowingStateLoss();
                HYClient.getHYPlayer()
                        .stopPlay(null);
                EventBus.getDefault().post(new AcceptDiaoDu(null, data, null, 0));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo) {
        if (data != null
                && data.nTalkbackID == cNotifyTalkbackStatusInfo.nTalkbackID
                && cNotifyTalkbackStatusInfo.isTalkingStopped()) {
            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.diaodu_has_end));
            EventBus.getDefault().post(new WaitViewAllFinish("waitacceptlayout onEvent(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo)"));
            dismiss();
        }

    }


    public void refuseBtn() {
        dismissAllowingStateLoss();
        HYClient.getHYPlayer()
                .stopPlay(null);

        // 对讲邀请50063405
        if (tv_notic.getText().toString().equals(AppUtils.getString(R.string.waite_duifang_accept_this_diaodu))) {
            HYClient.getModule(ApiTalk.class).quitTalking(SdkParamsCenter.Talk.QuitTalk()
                    .setTalkDomainCode(data.strTalkbackDomainCode)
                    .setTalkId(data.nTalkbackID), null);
        } else {
            HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                    .setTalkId(data.nTalkbackID)
                    .setTalkDomainCode(data.strTalkbackDomainCode), null);
        }
        EventBus.getDefault().post(new WaitViewAllFinish("waitacceptlayout onclick iv_refuse 260"));
    }

    private void init(CNotifyUserJoinTalkback data) {

        iv_type.setVisibility(VISIBLE);
        String userFormat = null;
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
        }
        tv_name_id.setText(userFormat);
        tv_notic.setText(strNotice);

        ScreenNotify.get().showScreenNotify(getContext(), userFormat, strNotice);

        if (data != null && data.isForceInvite()) {
            new RxUtils<>().doDelayOn(500, new RxUtils.IMainDelay() {
                @Override
                public void onMainDelay() {
                    iv_accept.performClick();
                }
            });
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }


    public void setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
    }



    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        refuseBtn();
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }


}
