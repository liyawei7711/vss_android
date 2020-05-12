package huaiye.com.vvs.ui.home.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.capture.Capture;
import com.huaiye.sdk.media.capture.HYCapture;
import com.huaiye.sdk.sdpmsgs.video.CStartMobileCaptureRsp;

import java.util.ArrayList;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.dao.MediaFileDao;
import huaiye.com.vvs.dao.msgs.CaptureMessage;
import huaiye.com.vvs.dao.msgs.ChatUtil;
import huaiye.com.vvs.dao.msgs.StopCaptureMessage;

import static huaiye.com.vvs.common.AppUtils.CAPTURE_TYPE;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_HD1080P;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_HD720P;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_VGA;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_camera;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_capture;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_false;
import static huaiye.com.vvs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: ActionBarLayout
 */

public class CaptureViewLayout extends FrameLayout implements View.OnClickListener {

    public View iv_camera;
    ImageView iv_shanguang;
    View iv_change;
    View iv_waizhi;
    View iv_close;
    View iv_suofang;
    View view_cover;
    View fl_root;
    TextureView ttv_capture;

    ArrayList<String> userId = new ArrayList<>();

    private final int CAPTURE_STATUS_NONE = 0;
    private final int CAPTURE_STATUS_STARTING = 1;
    private final int CAPTURE_STATUS_CAPTURING = 2;

    int     captureStatus;
    boolean isPaused;

    MediaFileDao.MediaFile mMediaFile;

    /**
     * pc 客户端会多次发消息,采集开始的时候缓存起来
     */
    ArrayList<CaptureMessage> pendingMsg = new ArrayList<>();

    boolean isFromGuanMo;//是否是观摩启动

    public boolean isCapture() {
        return captureStatus > CAPTURE_STATUS_NONE;
    }

    public CaptureViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CaptureViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);

        View view = LayoutInflater.from(context).inflate(R.layout.main_capture_layout, null);

        iv_camera = view.findViewById(R.id.iv_camera);
        iv_shanguang = view.findViewById(R.id.iv_shanguang);
        iv_change = view.findViewById(R.id.iv_change);
        iv_waizhi = view.findViewById(R.id.iv_waizhi);
        ttv_capture = view.findViewById(R.id.ttv_capture);
        view_cover = view.findViewById(R.id.view_cover);
        iv_close = view.findViewById(R.id.iv_close);
        fl_root = view.findViewById(R.id.fl_root);
        iv_suofang = view.findViewById(R.id.iv_suofang);

        addView(view);

        iv_close.setOnClickListener(this);
        iv_camera.setOnClickListener(this);
        iv_shanguang.setOnClickListener(this);
        iv_change.setOnClickListener(this);
        iv_waizhi.setOnClickListener(this);
        iv_suofang.setOnClickListener(this);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        com.huaiye.sdk.logger.Logger.debug("CaptureViewLayout start " + HYClient.getSdkOptions().encrypt().isEncryptBind());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_camera:
                if (HYClient.getMemoryChecker().checkEnough()) {
                    HYClient.getHYCapture().snapShotCapture(MediaFileDao.get()
                            .getImgRecordFile(), new SdkCallback<String>() {
                        @Override
                        public void onSuccess(String resp) {
                            if (getVisibility() != GONE) {
                                ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.jieping_success));
                            }
                        }

                        @Override
                        public void onError(ErrorInfo error) {

                        }
                    });
                } else {
                    if (getVisibility() != GONE) {
                        ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.local_size_max));
                    }
                }
                break;
            case R.id.iv_shanguang:
                if (HYClient.getHYCapture().getCurrentCameraIndex() == HYCapture.Camera.Foreground) {
                    showToast(AppUtils.getString(R.string.cameraindex_notice));
                    return;
                }
                HYClient.getHYCapture().setTorchOn(!HYClient.getHYCapture().isTorchOn());
                if (HYClient.getHYCapture().isTorchOn()) {
                    iv_shanguang.setImageResource(R.drawable.btn_shanguangdeng_press);
                } else {
                    iv_shanguang.setImageResource(R.drawable.btn_shanguangdeng);
                }
                break;
            case R.id.iv_change:
                HYClient.getHYCapture().toggleInnerCamera();
                break;
            case R.id.iv_waizhi:
                HYClient.getHYCapture().requestUsbCamera();

                if (HYClient.getHYCapture().getCurrentCameraIndex() == HYCapture.Camera.USB) {

                } else {
                    showToast(AppUtils.getString(R.string.out_camera));
                }
                break;
            case R.id.iv_close:
                stopCapture();
                break;
            case R.id.iv_suofang:
                toggleBigSmall();
                break;
        }
    }

    public void onResume() {
        Logger.debug("CaptureiewLayout  onResume() " + isCapturing() );
        isPaused = false;
        if (isCapturing())
            HYClient.getHYCapture().setPreviewWindow(ttv_capture);
    }

    public void onPause() {
        isPaused = true;
        if (isCapturing())
            HYClient.getHYCapture().setPreviewWindow(null);
    }


    public void onDestroy() {
        if (MCApp.getInstance().getTopActivity() != null) {
            MCApp.getInstance().getTopActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if (isCapturing() || isCapturStarting() ) {
            stopCapture();
        }
    }

    public void stopCapture() {
        if (isCapturing()||isCapturStarting()) {

            AppUtils.isCaptureLayoutShowing = false;
            if (mMediaFile != null) {
                mMediaFile.recordEnd();
                mMediaFile = null;
            }
            HYClient.getHYCapture().stopCapture(null);
            HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
            view_cover.setVisibility(VISIBLE);
            captureStatus = CAPTURE_STATUS_NONE;
            userId.clear();
            if (getVisibility() != GONE) {
                ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.stop_capture_success));
            }
            isFromGuanMo = false;

            if (iCaptureStateChangeListener != null) {
                iCaptureStateChangeListener.onClose();
            }
        }
        //不管停止成功不成功,都要gone掉
        setVisibility(GONE);

    }

    /**
     * 开启观摩
     *
     * @param bean
     */
    public void startCaptureFromUser(CaptureMessage bean) {
        if (!userId.contains(bean.fromUserId)) {
            userId.add(bean.fromUserId);
        }
        if (isCapturStarting()){
            pendingMsg.add(bean);
            Logger.debug("CaptureViewLayout startCaptureFromUser isCapturStarting");
            //do nothing
            return;
        }
        if (isCapturing()) {
            setVisibility(VISIBLE);
            Logger.debug("CaptureViewLayout startCaptureFromUser");
            sendPlayerMessage(bean);
        } else {
            isFromGuanMo = true;
            toggleCapture(bean);
        }
    }

    /**
     * 关闭观摩
     *
     * @param bean
     */
    public void stopCaptureFromUser(StopCaptureMessage bean) {
        if (userId.contains(bean.fromUserId)) {
            userId.remove(bean.fromUserId);
        }
        if (userId.size() <= 0) {
            if (isFromGuanMo) {
                stopCapture();
            }
        }

    }

    private void sendPlayerMessage(CaptureMessage user) {
        if (user == null) {
            return;
        }
//        if (user.length < 1) {
//            return;
//        }
        com.huaiye.sdk.logger.Logger.debug("CaptureViewLayout sendPlayerMessage " );

        ChatUtil.get().rspGuanMo(user.fromUserId, user.fromUserDomain, user.fromUserName,user.sessionID);
    }

    public void toggleCapture(final CaptureMessage users) {
        if (isCapturing()) {
            stopCapture();
        } else {
            ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getLayoutParams().height = calcHeightHeight();

            setVisibility(VISIBLE);
            AppUtils.isCaptureLayoutShowing = true;
            captureStatus = CAPTURE_STATUS_STARTING;

            mMediaFile = MediaFileDao.get().getVideoRecordFile();

            boolean captureType = Boolean.parseBoolean(SP.getParam(CAPTURE_TYPE, STRING_KEY_false).toString());
            if (captureType) {
//                HYClient.getSdkOptions().Capture().setCaptureOfflineMode(true);
            } else {
//                HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
            }
            Capture.Params params = Capture.Params.get()
                    .setEnableServerRecord(true)
                    .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT)
                    .setRecordPath(captureType ? mMediaFile.getRecordPath() : null)
                    .setCameraIndex(SP.getInteger(STRING_KEY_camera, -1) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                    .setPreview(ttv_capture);
            if (isPaused){
                params.setPreview(null);
            }
            HYClient.getHYCapture().startCapture(params,
                    new Capture.Callback() {
                        @Override
                        public void onRepeatCapture() {
                            captureStatus = CAPTURE_STATUS_CAPTURING;
                            view_cover.setVisibility(GONE);
                            Logger.debug("CaptureViewLayout onRepeatCapture");
                            sendPlayerMessage(users);


                        }

                        @Override
                        public void onSuccess(CStartMobileCaptureRsp resp) {
                            view_cover.setVisibility(GONE);
                            captureStatus = CAPTURE_STATUS_CAPTURING;
                            if (resp != null){
                                Logger.debug("CaptureViewLayout onSuccess " + resp.toString());
                            }else {
                                Logger.debug("CaptureViewLayout onSuccess resp null " );
                            }
                            if (!AppUtils.isVideo && !AppUtils.isVideo && !AppUtils.isMeet && getVisibility() != GONE) {
                                ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.capture_success));
                            }
                            sendPlayerMessage(users);
                            if (pendingMsg.size() > 0){
                                for (int i = 0 ; i < pendingMsg.size() ; i++){
                                    sendPlayerMessage(pendingMsg.get(i));
                                }
                                pendingMsg.clear();
                            }
                            if (iCaptureStateChangeListener != null) {
                                iCaptureStateChangeListener.onOpen();
                            }
                        }

                        @Override
                        public void onError(ErrorInfo error) {
                            if (!AppUtils.isVideo && !AppUtils.isVideo && !AppUtils.isMeet && getVisibility() != GONE) {
                                ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.capture_false));
                                onDestroy();
                            }
                        }

                        @Override
                        public void onCaptureStatusChanged(SdpMessageBase msg) {
                        }
                    });
        }
    }





    public void toggleShowHide() {
        if (!isCapturing()) {
            return;
        }

        if (getVisibility() == VISIBLE) {
            setVisibility(INVISIBLE);

            if (iCaptureStateChangeListener != null) {
                iCaptureStateChangeListener.hide();
            }
        } else {
            setVisibility(VISIBLE);

            if (iCaptureStateChangeListener != null) {
                iCaptureStateChangeListener.show();
            }
        }
    }

    private boolean isCapturing(){
        return captureStatus == CAPTURE_STATUS_CAPTURING;
    }

    private boolean isCapturStarting(){
        return captureStatus == CAPTURE_STATUS_STARTING;
    }

    public void toggleBigSmall() {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (getLayoutParams().width == LayoutParams.MATCH_PARENT) {
            layoutParams.width = AppUtils.getSize(250);
            layoutParams.height = calcHeightHeight();
            FrameLayout.LayoutParams lp = (LayoutParams) fl_root.getLayoutParams();
            lp.height = LayoutParams.MATCH_PARENT;
            fl_root.setLayoutParams(lp);

        } else {
            layoutParams.width = LayoutParams.MATCH_PARENT;
            layoutParams.height = LayoutParams.MATCH_PARENT;

            if (SP.getParam(STRING_KEY_capture, "").toString().equals(STRING_KEY_VGA)) {
                //手机一般都是16:9的,放大后要注意4:3的情况
                FrameLayout.LayoutParams lp = (LayoutParams) fl_root.getLayoutParams();
                lp.height = (int) (1.3333 * Float.valueOf(AppUtils.getScreenWidth()));
                fl_root.setLayoutParams(lp);
            }
        }
        setLayoutParams(layoutParams);
    }


    private int calcHeightHeight() {
        int height = AppUtils.getSize(332);
        switch (SP.getParam(STRING_KEY_capture, "").toString()) {
            case STRING_KEY_VGA:
                height = AppUtils.getSize(332);
                break;
            case STRING_KEY_HD720P:
                height = AppUtils.getSize(415);
                break;
            case STRING_KEY_HD1080P:
                height = AppUtils.getSize(415);
                break;
        }
        return height;
    }


    ICaptureStateChangeListener iCaptureStateChangeListener;

    public void setiCaptureStateChangeListener(ICaptureStateChangeListener iCaptureStateChangeListener) {
        this.iCaptureStateChangeListener = iCaptureStateChangeListener;
    }

    public interface ICaptureStateChangeListener {
        void onOpen();

        void onClose();

        void hide();

        void show();
    }

}
