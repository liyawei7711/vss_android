package huaiye.com.vvs.ui.device;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.sdkabi._api.ApiDevice;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.device.CPTZControlRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.models.device.bean.DevicePlayerBean;

/**
 * author: admin
 * date: 2017/09/05
 * version: 0
 * mail: secret
 * desc: DevicePlayRealActivity
 * 设备的播放观摩
 * TILT_UPER = 1,	    //云台上仰
 * TILT_DOWNER = 2,	//云台下俯
 * PAN_LEFTER = 3,	    //云台左转
 * PAN_RIGHTER = 4,	//云台右转
 * UP_LEFTER = 5,	    //云台上仰和左转
 * DOWN_LEFTER = 6,	//云台下俯和左转
 * UP_RIGHTER = 7,    //云台上仰和右转
 * DOWN_RIGHTER = 8,   //云台下俯和右转
 * <p>
 * ZOOM_PLUS = 9,	    // 焦距变大(倍率变大)
 * ZOOM_MINUS = 10,	//焦距变小(倍率变小)
 * <p>
 * FOCUS_NEARER= 11,	//焦点前调
 * FOCUS_FARER = 12,	//焦点后调
 */
@BindLayout(R.layout.device_realplay_activity)
public class DevicePlayRealActivity extends AppBaseActivity {

    @BindView(R.id.texture_video)
    TextureView texture_video;
    @BindView(R.id.iv_slient)
    ImageView iv_slient;
    @BindView(R.id.ll_menu)
    View ll_menu;
    @BindView(R.id.iv_fangda)
    View iv_fangda;
    @BindView(R.id.iv_suoxiao)
    View iv_suoxiao;

    @BindView(R.id.background_button)
    RelativeLayout background_button;
    //续航按钮
    @BindView(R.id.cruise_button)
    ToyView cruise_button;
    @BindView(R.id.ll_quality)
    View ll_quality;
    @BindView(R.id.tv_main_quality)
    TextView tv_main_quality;
    @BindView(R.id.tv_sub_quality)
    TextView tv_sub_quality;
    @BindView(R.id.tv_quality)
    TextView tv_quality;

    @BindExtra
    DevicePlayerBean data;

    private boolean definitionStatus = true;
    /**
     * 记录当前点击的位置
     */
    private int positionCruise;
    /**
     * 0 代表请求转动 1代表请求停止 2代表请求巡航开始 3代表请求巡航结束
     */
    private int positionCruisestop;
    //按下的位置
    private int downCommand;
    //松开的位置
    private int upCommand;
    private int cruiseCommand;
    private boolean isVoiceOpened = true;
    public boolean isPlaying = false;

    private final int USE_STREAM_MAIN    = 0;
    private final int USE_STREAM_SECOND  = 1;
    int mUseStream = USE_STREAM_SECOND;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.huaiye.sdk.logger.Logger.debug("DevicePlayRealActivity onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    protected void initActionBar() {

        com.huaiye.sdk.logger.Logger.debug("DevicePlayRealActivity initActionBar");
        if (data == null){
            com.huaiye.sdk.logger.Logger.debug("DevicePlayRealActivity data null");
        }else {
            com.huaiye.sdk.logger.Logger.debug("DevicePlayRealActivity data " + data.toString());
        }
        //没有辅码流,就不显示切换了
        if (TextUtils.isEmpty(data.strSubStreamCode)){
            tv_quality.setVisibility(View.GONE);
            mUseStream = USE_STREAM_MAIN;
        }

        startPlay();

        cruise_button.setPressListener(new ToyView.PressListener() {
            @Override
            public void onArrowPressDown(int item) {
                boolean needStop = false;
                switch (item) {
                    case 0: //云台上仰
                        upCommand = 1;
                        break;
                    case 1://云台上仰和右转
                        upCommand = 7;
                        break;
                    case 2://云台右转
                        upCommand = 4;
                        break;
                    case 3://云台下俯和右转
                        upCommand = 8;
                        break;
                    case 4://云台下俯
                        upCommand = 2;
                        break;
                    case 5://云台下俯和左转
                        upCommand = 6;
                        break;
                    case 6://云台左转
                        upCommand = 3;
                        break;
                    case 7://云台上仰和左转
                        upCommand = 5;
                        break;
                    case 8:
                        needStop = true;
                        break;
                }
                if (needStop) {
                    controlDevice(1, upCommand);
                } else {
                    controlDevice(0, upCommand);
                }
            }

            @Override
            public void onArrowPressUp(int item) {
                //被抬起的位置是
                //发送摄像头被点击的8个位置
                switch (item) {
                    case 0: //云台上仰
                        upCommand = 1;
                        break;
                    case 1://云台上仰和右转
                        upCommand = 7;
                        break;
                    case 2://云台右转
                        upCommand = 4;
                        break;
                    case 3://云台下俯和右转
                        upCommand = 8;
                        break;
                    case 4://云台下俯
                        upCommand = 2;
                        break;
                    case 5://云台下俯和左转
                        upCommand = 6;
                        break;
                    case 6://云台左转
                        upCommand = 3;
                        break;
                    case 7://云台上仰和左转
                        upCommand = 5;
                        break;
                }
                controlDevice(1, upCommand);
            }

            @Override
            public void onCenterCircleSingleTap() {

            }

            @Override
            public void onCancel() {
                controlDevice(1, upCommand);
            }
        });

        iv_fangda.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //拉远
                        controlDevice(0, 9);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        controlDevice(1, 9);
                        break;

                }
                return true;
            }
        });
        iv_suoxiao.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //拉远
                        controlDevice(0, 10);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        controlDevice(1, 10);
                        break;

                }
                return true;
            }
        });

    }

    private void controlDevice(int stop, int downCommand) {
        HYClient.getModule(ApiDevice.class).controlDevice(SdkParamsCenter.Device.ControlDevice()
                        .setnCommand(downCommand)
                        .setnSpeed(5)
                        .setnStop(stop)
                        .setStrDeviceCode(data.strDeviceCode)
                        .setStrChannelCode(data.strChannelCode)
                        .setStrDomainCode(data.strDomainCode)
                        .setStrStreamCode(data.strStreamCode),
                new SdkCallback<CPTZControlRsp>() {
                    @Override
                    public void onSuccess(CPTZControlRsp cptzControlRsp) {
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                    }
                });
    }

    @Override
    public void doInitDelay() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        com.huaiye.sdk.logger.Logger.debug("DevicePlayRealActivity onResume ");
        HYClient.getHYPlayer().pausePlayEx(false, texture_video);
    }

    @Override
    protected void onPause() {
        super.onPause();
        com.huaiye.sdk.logger.Logger.debug("DevicePlayRealActivity onPause ");
        HYClient.getHYPlayer().pausePlayEx(true, texture_video);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        com.huaiye.sdk.logger.Logger.debug("DevicePlayRealActivity onDestroy ");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        com.huaiye.sdk.logger.Logger.debug("DevicePlayRealActivity onBackPressed ");
        HYClient.getHYPlayer().stopPlayEx(null, texture_video);
    }

    @OnClick({R.id.background_button, R.id.iv_slient, R.id.iv_fangda, R.id.iv_suoxiao ,R.id.tv_quality,R.id.texture_video
                ,R.id.tv_main_quality,R.id.tv_sub_quality})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.background_button:
                if (cruise_button.getVisibility() == View.VISIBLE) {
                    cruise_button.setVisibility(View.GONE);
                    ll_menu.setVisibility(View.GONE);
                } else {
                    cruise_button.setVisibility(View.VISIBLE);
                    ll_menu.setVisibility(View.VISIBLE);
                }
                ll_quality.setVisibility(View.GONE);

                break;
            case R.id.iv_slient:
                isVoiceOpened = !isVoiceOpened;
                iv_slient.setImageResource(isVoiceOpened ? R.drawable.btn_mianti : R.drawable.btn_mianti_pressed);

                HYClient.getHYPlayer().setAudioOnEx(isVoiceOpened, texture_video);
                break;
            case R.id.tv_quality:
                ll_quality.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_main_quality:
                tv_main_quality.setTextColor(Color.parseColor("#539bf0"));
                tv_sub_quality.setTextColor(Color.parseColor("#ffffff"));
                ll_quality.setVisibility(View.GONE);
                mUseStream = USE_STREAM_MAIN;
                startPlay();
                break;
            case R.id.tv_sub_quality:
                tv_sub_quality.setTextColor(Color.parseColor("#539bf0"));
                tv_main_quality.setTextColor(Color.parseColor("#ffffff"));
                ll_quality.setVisibility(View.GONE);
                mUseStream = USE_STREAM_SECOND;
                startPlay();
                break;
        }
    }


    private void startPlay(){
        com.huaiye.sdk.logger.Logger.debug("DevicePlayRealActivity startPlay " + isPlaying);

        if (isPlaying){
            HYClient.getHYPlayer().stopPlay(new SdkCallback<VideoParams>() {
                @Override
                public void onSuccess(VideoParams videoParams) {
                    play();
                }

                @Override
                public void onError(ErrorInfo errorInfo) {

                }
            });
        }else {
            play();
        }
    }

    private void play(){
        String streamCode = mUseStream == USE_STREAM_MAIN ? data.strStreamCode:data.strSubStreamCode;
        HYClient.getHYPlayer().startPlay(Player.Params.TypeDeviceReal()
                .setPreview(texture_video)
                .setChannelCode(data.strChannelCode)
                .setDeviceCode(data.strDeviceCode)
                .setStreamCode(streamCode)
                .setDomainCode(data.strDomainCode)
                .setMixCallback(new VideoCallbackWrapper() {
                    @Override
                    public void onSuccess(VideoParams param) {
                        super.onSuccess(param);
                        com.huaiye.sdk.logger.Logger.debug("DevicePlayRealActivity play onSuccess");
                        showToast(AppUtils.getString(R.string.player_success));
                        isPlaying = true;
                    }

                    @Override
                    public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                        super.onError(param, errorInfo);
                        com.huaiye.sdk.logger.Logger.debug("DevicePlayRealActivity play onError" + errorInfo.getMessage());
                        showToast(AppUtils.getString(R.string.player_faile));
                        finish();
                    }
                }));
    }
}
