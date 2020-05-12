package huaiye.com.vvs.ui.meet.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.meet.CBeginMeetingRecordRsp;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ErrorMsg;

import static huaiye.com.vvs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: MeetMediaMenuView
 */
@BindLayout(R.layout.view_meet_media)
public class MeetMediaMenuView extends RelativeLayout {
    @BindView(R.id.menu_iv_voice)
    ImageView menu_iv_voice;
    boolean isVoiceOpened = true;

    @BindView(R.id.menu_iv_mic_layer)
    View menu_iv_mic_layer;
    @BindView(R.id.menu_iv_camera_video)
    View menu_iv_camera_video;
    @BindView(R.id.menu_iv_camera_layer)
    View menu_iv_camera_layer;
    @BindView(R.id.menu_iv_waizhi)
    View menu_iv_waizhi;

    @BindView(R.id.menu_iv_video)
    ImageView menu_iv_video;
    @BindView(R.id.menu_iv_hand_up)
    View menu_iv_hand_up;
    @BindView(R.id.menu_iv_layout)
    View menu_iv_layout;
    @BindView(R.id.view_red)
    View view_red;

    @BindView(R.id.ll_share)
    View ll_share;
    //    @BindView(R.id.tv_white_board)
//    TextView tv_white_board;
    @BindView(R.id.iv_white_img)
    ImageView iv_white_img;

    boolean isVideoOpened = true;

    @BindView(R.id.menu_iv_mic)
    ImageView menu_iv_mic;
    @BindView(R.id.tv_video_title)
    TextView tv_video_title;
    @BindView(R.id.menu_iv_invite_layer)
    LinearLayout menu_iv_invite_layer;
    @BindView(R.id.menu_iv_startrecord)
    LinearLayout menu_iv_startrecord;

    Callback mCallback;
    boolean isCloseVideo;
    boolean isAudioOn = true;
    private int nMeetID;
    private String strMeetDomainCode;

    public void setIdAndDomain(int nMeetID, String strMeetDomainCode) {
        this.nMeetID = nMeetID;
        this.strMeetDomainCode = strMeetDomainCode;
    }

    public MeetMediaMenuView(Context context) {
        this(context, null);
    }

    public MeetMediaMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeetMediaMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Injectors.get().injectView(this);
        isWatch();
    }

    @OnClick({
            R.id.menu_iv_exit_layer,
            R.id.menu_iv_members_layer,
            R.id.menu_iv_invite_layer,
            R.id.menu_iv_camera_layer,
            R.id.menu_iv_waizhi,
            R.id.menu_iv_voice_layer,
            R.id.menu_iv_camera_video,
            R.id.menu_iv_hand_up,
            R.id.menu_iv_mic_layer,
            R.id.ll_share,
            R.id.menu_iv_startrecord,
            R.id.menu_iv_layout
    })
    void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.menu_iv_exit_layer:
                if (mCallback != null) {
                    mCallback.onMeetExitClicked();
                }
                break;
            case R.id.menu_iv_members_layer:
                if (mCallback != null) {
                    mCallback.onMemberListClicked();
                }
                break;
            case R.id.menu_iv_invite_layer:
                if (mCallback != null) {
                    mCallback.onMeetInviteClicked();
                }
                break;
            case R.id.menu_iv_camera_layer:
                if (mCallback != null) {
                    mCallback.onInnerCameraClicked();
                }
                break;
            case R.id.menu_iv_waizhi:
                if (mCallback != null) {
                    mCallback.onOutSideCameraClicked();
                }
                break;
            case R.id.menu_iv_voice_layer:
                toggleVoice();
                break;
            case R.id.menu_iv_mic_layer:
                if (mCallback != null) {
                    mCallback.onCaptureVoiceClicked();
                }
                break;
            case R.id.menu_iv_hand_up:
                if (mCallback != null) {
                    mCallback.onHandUp();
                }
                break;
            case R.id.menu_iv_layout:
                if (mCallback != null) {
                    mCallback.showLayoutChange();
                }
                break;
            case R.id.menu_iv_camera_video:
                toggleVideo();
                break;
            case R.id.ll_share:
                if (mCallback != null) {
                    mCallback.showSharePop(ll_share);
                }
                break;
            case R.id.menu_iv_startrecord:
                startRecord();
                break;
        }
    }

    /**
     * 开启录像
     */
    private void startRecord() {
        HYClient.getModule(ApiMeet.class).startMeetingRecord(SdkParamsCenter.Meet.StartMeetRecord()
                        .setnMeetingID(nMeetID)
                        .setStrMeetingDomainCode(strMeetDomainCode),
                new SdkCallback<CBeginMeetingRecordRsp>() {
                    @Override
                    public void onSuccess(CBeginMeetingRecordRsp cBeginMeetingRecordRsp) {
                        menu_iv_startrecord.setVisibility(GONE);
                        if (mCallback != null) {
                            mCallback.startRecordSuccess();
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(ErrorMsg.start_record_code));
                    }

                });
    }

    /**
     * 控制声音
     */
    private void toggleVoice() {
        isVoiceOpened = !isVoiceOpened;
        menu_iv_voice.setImageResource(isVoiceOpened ? R.drawable.btn_huiyi__jinyin : R.drawable.btn_huiyi__jinyin_pressed);

        if (mCallback != null) {
            mCallback.onPlayerVoiceClicked(isVoiceOpened);
        }
    }

    /**
     * 改变声音
     *
     * @param sound
     */
    public void changeSound(boolean sound) {
        isVoiceOpened = sound;
        menu_iv_voice.setImageResource(isVoiceOpened ? R.drawable.btn_huiyi__jinyin : R.drawable.btn_huiyi__jinyin_pressed);

        if (mCallback != null) {
            mCallback.onPlayerVoiceClicked(isVoiceOpened);
        }
    }

    public void setWhiteBoardTxt(String str, boolean isClose, boolean isOwner) {
//        tv_white_board.setText(str);
        if (isOwner) {
            iv_white_img.setImageResource(isClose ? R.drawable.btn_huiyi_baiban : R.drawable.btn_huiyi_baiban_press);
        } else {
            iv_white_img.setImageResource(R.drawable.btn_huiyi_baiban);
        }
    }

    public void hideInvisitor() {
        menu_iv_invite_layer.setVisibility(GONE);
    }

    /**
     * 控制视频
     */
    private void toggleVideo() {
        if (isCloseVideo) {
            return;
        }
        isVideoOpened = !isVideoOpened;
        menu_iv_video.setImageResource(isVideoOpened ? R.drawable.btn_huiyi_shipin : R.drawable.btn_huiyi_shipin_press);
        tv_video_title.setText(isVideoOpened ? AppUtils.getString(R.string.video_close) : AppUtils.getString(R.string.video_open));
        if (mCallback != null) {
            mCallback.onPlayerVideoClicked(isVideoOpened);
        }
    }

    /**
     * 关闭视频
     */
    public void closeVideo() {
        isVideoOpened = false;
        menu_iv_video.setImageResource(isVideoOpened ? R.drawable.btn_huiyi_shipin : R.drawable.btn_huiyi_shipin_press);

        if (mCallback != null) {
            mCallback.onPlayerVideoClicked(isVideoOpened);
        }
    }

    /**
     * 开启视频
     */
    public void openVideo() {
        isVideoOpened = true;
        menu_iv_video.setImageResource(isVideoOpened ? R.drawable.btn_huiyi_shipin : R.drawable.btn_huiyi_shipin_press);

        if (mCallback != null) {
            mCallback.onPlayerVideoClicked(isVideoOpened);
        }
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void toggleCaptureAudio() {
        isAudioOn = HYClient.getHYCapture().toggleCaptureAudio();
        menu_iv_mic.setImageResource(isAudioOn ? R.drawable.btn_huiyi_jinmai : R.drawable.btn_huiyi_jinmai_press);
    }

    /**
     * 关闭声音 和 听筒
     */
    public void closeVoice() {
        HYClient.getHYCapture().setCaptureAudioOn(false);
        menu_iv_mic.setImageResource(R.drawable.btn_huiyi_jinmai_press);

        menu_iv_voice.setImageResource(R.drawable.btn_huiyi__jinyin_pressed);
        if (mCallback != null) {
            mCallback.onPlayerVoiceClicked(false);
        }

    }

    public void reSetVoice() {
        HYClient.getHYCapture().setCaptureAudioOn(isAudioOn);
        menu_iv_mic.setImageResource(isAudioOn ? R.drawable.btn_huiyi_jinmai : R.drawable.btn_huiyi_jinmai_press);

        menu_iv_voice.setImageResource(isVoiceOpened ? R.drawable.btn_huiyi__jinyin : R.drawable.btn_huiyi__jinyin_pressed);
        if (mCallback != null) {
            mCallback.onPlayerVoiceClicked(isVoiceOpened);
        }
    }

    /**
     * 开启声音
     */
    public void openVoice() {
        HYClient.getHYCapture().setCaptureAudioOn(true);
        menu_iv_mic.setImageResource(R.drawable.btn_huiyi_jinmai);
    }

    /**
     * 创建人员特有的view展示
     *
     * @param isMeetStarter
     */
    public void hideMasterView(boolean isMeetStarter) {
        menu_iv_hand_up.setVisibility(isMeetStarter ? GONE : VISIBLE);
        menu_iv_layout.setVisibility(isMeetStarter ? VISIBLE : GONE);
    }

    public void showHandUpRed(boolean value) {
        view_red.setVisibility(value ? VISIBLE : GONE);
    }

    public void setVideoEnable(boolean isCloseVideo) {
        this.isCloseVideo = isCloseVideo;
    }

    /**
     * 观摩模式的菜单状态
     */
    public void isWatch() {
        menu_iv_mic_layer.setVisibility(VISIBLE);
        menu_iv_camera_video.setVisibility(VISIBLE);
        menu_iv_camera_layer.setVisibility(VISIBLE);
        menu_iv_waizhi.setVisibility(VISIBLE);
        ll_share.setVisibility(GONE);
    }

    public void canStartRecord(boolean canOpen) {
        menu_iv_startrecord.setVisibility(canOpen ? VISIBLE : GONE);
    }

    public interface Callback {

        void onMeetExitClicked();

        void onMemberListClicked();

        void onMeetInviteClicked();

        void onInnerCameraClicked();

        void onCaptureVoiceClicked();

        void onPlayerVoiceClicked(boolean isVoiceOpened);

        void onPlayerVideoClicked(boolean isVideoOpened);

        void showLayoutChange();

        void showSharePop(View view);

        void onHandUp();

        void startRecordSuccess();

        void onOutSideCameraClicked();
    }
}
