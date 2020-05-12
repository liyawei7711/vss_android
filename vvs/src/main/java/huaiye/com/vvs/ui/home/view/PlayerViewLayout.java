package huaiye.com.vvs.ui.home.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.huaiye.cmf.JniIntf;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMsgFindLanCaptureDeviceRsp;
import com.huaiye.cmf.sdp.SdpMsgGetMediaInfoRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.VideoDebugInfoCallback;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppAudioManagerWrapper;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.dao.msgs.ChatUtil;
import huaiye.com.vvs.dao.msgs.PlayerMessage;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.device.bean.DevicePlayerBean;
import huaiye.com.vvs.ui.home.MainActivity;

import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_DEVICE_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_PERSON_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_only_audio;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: 强制采集其他用户的预览界面
 */

public class PlayerViewLayout extends FrameLayout implements View.OnClickListener {
    TextureView ttv_player;
    ImageView iv_jingyin;
    View fl_parent;
    View iv_change_layout;
    View view_cover;
    View iv_close;

    Gson gson = new Gson();
    boolean isBig;
    boolean isVoiceOpened = true;
    boolean is43 = true;

    public boolean isPlayer;
    private PlayerMessage bean;

    private int playerId = -1;
    AppAudioManagerWrapper appAudio;

    public PlayerViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PlayerViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);

        View view = LayoutInflater.from(context).inflate(R.layout.main_player_layout, null);

        iv_jingyin = view.findViewById(R.id.iv_jingyin);
        iv_change_layout = view.findViewById(R.id.iv_change_layout);
        ttv_player = view.findViewById(R.id.ttv_player);
        iv_close = view.findViewById(R.id.iv_close);
        view_cover = view.findViewById(R.id.view_cover);
        fl_parent = view.findViewById(R.id.fl_parent);

        addView(view);

        iv_jingyin.setOnClickListener(this);
        iv_change_layout.setOnClickListener(this);
        iv_close.setOnClickListener(this);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void clickClose() {
        if (bean != null) {
            ChatUtil.get().closeGuanMoClose(bean.userId, bean.userDomain, bean.userName);
        }
        stopPlayer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                clickClose();
                break;
            case R.id.iv_change_layout:
                if (isBig) {
                    FrameLayout.LayoutParams lp = (LayoutParams) getLayoutParams();
                    lp.width = LayoutParams.MATCH_PARENT;
                    lp.height = AppUtils.getSize(240);
                    setLayoutParams(lp);
                    FrameLayout.LayoutParams lp2 = (LayoutParams) fl_parent.getLayoutParams();
                    lp2.width = LayoutParams.MATCH_PARENT;
                    lp2.height = AppUtils.getSize(240);
                    fl_parent.setLayoutParams(lp2);

                    ttv_player.getLayoutParams().width = AppUtils.getSize(180);

                    ((AppBaseActivity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    JniIntf.SetPlayerSurface(playerId, new Surface(ttv_player.getSurfaceTexture()));
                } else {
                    FrameLayout.LayoutParams lp = (LayoutParams) getLayoutParams();
                    lp.width = LayoutParams.MATCH_PARENT;
                    lp.height = LayoutParams.MATCH_PARENT;
                    setLayoutParams(lp);
                    FrameLayout.LayoutParams lp2 = (LayoutParams) fl_parent.getLayoutParams();
                    lp2.width = LayoutParams.MATCH_PARENT;
                    lp2.height = LayoutParams.MATCH_PARENT;
                    fl_parent.setLayoutParams(lp2);

                    ttv_player.getLayoutParams().width = AppUtils.getScreenWidth() * 3 / 4;

                    ((AppBaseActivity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    JniIntf.SetPlayerSurface(playerId, new Surface(ttv_player.getSurfaceTexture()));
                }

                isBig = !isBig;

                break;
            case R.id.iv_jingyin:
                /**
                 * 控制声音
                 */

                isVoiceOpened = !isVoiceOpened;
                iv_jingyin.setImageResource(isVoiceOpened ? R.drawable.btn_mianti : R.drawable.btn_mianti_pressed);

                HYClient.getHYPlayer().setAudioOnEx(isVoiceOpened, ttv_player);

                break;
        }
    }

    private void changeSmall() {
        FrameLayout.LayoutParams lp = (LayoutParams) getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = AppUtils.getSize(240);
        setLayoutParams(lp);
        FrameLayout.LayoutParams lp2 = (LayoutParams) fl_parent.getLayoutParams();
        lp2.width = LayoutParams.MATCH_PARENT;
        lp2.height = AppUtils.getSize(240);
        fl_parent.setLayoutParams(lp2);

        ttv_player.getLayoutParams().width = AppUtils.getSize(180);

        ((AppBaseActivity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        isBig = false;
    }

    VideoCallbackWrapper videoCallbackWrapper = new VideoCallbackWrapper() {
        @Override
        public void onSuccess(VideoParams param) {
            super.onSuccess(param);
            playerId = param.getSessionID();
            view_cover.setVisibility(GONE);
//                        getInfo();
            isVoiceOpened = true;
            iv_jingyin.setImageResource(isVoiceOpened ? R.drawable.btn_mianti : R.drawable.btn_mianti_pressed);
            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.video_watch_success));
        }

        @Override
        public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
            super.onError(param, errorInfo);
            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.video_watch_false));
            if (bean != null){
                ChatUtil.get().closeGuanMoClose(bean.userId, bean.userDomain, bean.userName);
            }
            isPlayer = false;
            closeView();
        }

        @Override
        public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
            super.onVideoStatusChanged(param, msg);
            switch (msg.GetMessageType()) {
                case SdkMsgNotifyPlayStatus.SelfMessageId:
                    SdkMsgNotifyPlayStatus playStatus = (SdkMsgNotifyPlayStatus) msg;

                    if (playStatus.isStopped()) {
                        if (!playStatus.isOperationFromUser()) {
                            isPlayer = false;
                            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.duifang_close));
                            closeView();
                        }
                    }

                    break;
            }
        }
    };

    public void startPlayer(PlayerMessage bean) {
        ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (isPlayer) {
            return;
        }
        this.bean = bean;
        setVisibility(VISIBLE);
        isPlayer = true;
        changeSmall();

        VideoParams videoParams = null;
        if (bean.type == PLAYER_TYPE_INT) {
            if (PLAYER_TYPE_only_audio.equals(bean.content)) {
                ((MainActivity) getContext()).getLogicDialog().setTitleText(AppUtils.getString(R.string.notice))
                        .setMessageText(AppUtils.getString(R.string.only_audio))
                        .setCancelButtonVisibility(GONE)
                        .show();
            }
            videoParams = Player.Params.TypeUserReal()
                    .setUserDomainCode(bean.userDomain)
                    .setUserTokenID(bean.userTokenId)
                    .setPreview(ttv_player)
                    .setMixCallback(videoCallbackWrapper);
        } else if (bean.type == PLAYER_TYPE_PERSON_INT) {
            PersonModelBean personModelBean = gson.fromJson(bean.content, PersonModelBean.class);
            videoParams = Player.Params.TypeUserReal()
                    .setUserDomainCode(personModelBean.strDomainCode)
                    .setUserTokenID(personModelBean.strUserTokenID)
                    .setPreview(ttv_player)
                    .setMixCallback(videoCallbackWrapper);
        } else if (bean.type == PLAYER_TYPE_DEVICE_INT) {
            DevicePlayerBean devicePlayerBean = gson.fromJson(bean.content, DevicePlayerBean.class);

            videoParams = Player.Params.TypeDeviceReal()
                    .setPreview(ttv_player)
                    .setChannelCode(devicePlayerBean.strChannelCode)
                    .setDeviceCode(devicePlayerBean.strDeviceCode)
                    .setStreamCode(devicePlayerBean.strStreamCode)
                    .setDomainCode(devicePlayerBean.strDomainCode)
                    .setMixCallback(new VideoCallbackWrapper() {
                        @Override
                        public void onSuccess(VideoParams param) {
                            super.onSuccess(param);
                            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.player_success));
                        }

                        @Override
                        public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                            super.onError(param, errorInfo);
                            isPlayer = false;
                            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.player_faile));
                            closeView();
                        }

                        @Override
                        public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                            super.onVideoStatusChanged(param, msg);
                            switch (msg.GetMessageType()) {
                                case SdkMsgNotifyPlayStatus.SelfMessageId:
                                    //收到对方关闭的消息 就关闭自己
                                    SdkMsgNotifyPlayStatus playStatus = (SdkMsgNotifyPlayStatus) msg;

                                    if (playStatus.isStopped()) {
                                        if (!playStatus.isOperationFromUser()) {
                                            isPlayer = false;
                                            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.duifang_close));
                                            closeView();
                                        }
                                    }
                                    break;
                            }
                        }
                    });
        }

        if (videoParams != null) {
            appAudio = new AppAudioManagerWrapper();
            appAudio.start();
            HYClient.getHYPlayer().startPlay(videoParams);
        }

    }

    public void startPlayer(SdpMsgFindLanCaptureDeviceRsp bean) {
        ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (isPlayer) {
            return;
        }
        setVisibility(VISIBLE);
        isPlayer = true;
        changeSmall();

        ((MainActivity)getContext()).mP2PSample.setPlayerPreview(ttv_player);
        ((MainActivity)getContext()).mP2PSample.setCapturePreview(ttv_player);
        ((MainActivity)getContext()).mP2PSample.requestWatch(bean.m_strIP, new SdkCallback<SdkBaseParams.AgreeMode>() {
            @Override
            public void onSuccess(SdkBaseParams.AgreeMode resp) {
                view_cover.setVisibility(GONE);
                isVoiceOpened = true;
                iv_jingyin.setImageResource(isVoiceOpened ? R.drawable.btn_mianti : R.drawable.btn_mianti_pressed);
                ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.video_watch_success));
            }

            @Override
            public void onError(ErrorInfo error) {
                ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.video_watch_false));

                isPlayer = false;
                closeView();
            }
        });

    }

    public void getInfo() {
        HYClient.getHYPlayer().requestMediaInfoEx(new VideoDebugInfoCallback() {
            @Override
            public void onGetMediaInfoSuccess(final VideoParams param, final SdpMsgGetMediaInfoRsp info) {
                if (info.m_nVideoHeight == 0 || info.m_nVideoWidth == 0) {
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            getInfo();
//                        }
//                    }, 1000);
                } else {

                    if (info.m_nVideoHeight * 3 == info.m_nVideoWidth * 4) {
                        is43 = true;
                        ttv_player.getLayoutParams().width = AppUtils.getSize(180);
                    } else {
                        is43 = false;
                        ttv_player.getLayoutParams().width = AppUtils.getSize(135);
                    }
                    JniIntf.SetPlayerSurface(param.getSessionID(), new Surface(ttv_player.getSurfaceTexture()));
                }
            }
        }, ttv_player);
    }

    public void onResume() {
        if (isPlayer) {
            HYClient.getHYPlayer().pausePlayEx(false, ttv_player);
        }
    }

    public void onPause() {
        if (isPlayer) {
            HYClient.getHYPlayer().pausePlayEx(true, ttv_player);
        }
    }

    public void stopPlayer() {
        isPlayer = false;
        playerId = -1;

        if(((MainActivity)getContext()).mP2PSample != null) {
            ((MainActivity)getContext()).mP2PSample.stopWatching();
        }
        HYClient.getHYPlayer().stopPlayEx(null, ttv_player);

        closeView();
    }

    private void closeView() {
        bean = null;
        view_cover.setVisibility(VISIBLE);
        setVisibility(GONE);
        isPlayer = false;
        ((AppBaseActivity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (appAudio != null){
            appAudio.stop();
        }
    }

    OnMenuClickListener listener;

    public void setListener(OnMenuClickListener listener) {
        this.listener = listener;
    }

    public interface OnMenuClickListener {
        void onChangeLayoutClick(boolean value);
    }

}
