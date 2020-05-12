package huaiye.com.vvs.ui.meet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.cmf.JniIntf;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMsgCaptureQualityNotify;
import com.huaiye.cmf.sdp.SdpUITask;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.exts.GesturedTextureLayer;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.MediaStatus;
import com.huaiye.sdk.media.capture.HYCapture;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.media.usb.HYUsbCameraManager;
import com.huaiye.sdk.sdkabi._api.ApiIO;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.meet.ParamsJoinMeet;
import com.huaiye.sdk.sdkabi._params.meet.ParamsQuitMeet;
import com.huaiye.sdk.sdkabi.abilities.io.callback.CallbackStartFileConvert;
import com.huaiye.sdk.sdkabi.abilities.meet.callback.CallbackJoinMeet;
import com.huaiye.sdk.sdkabi.abilities.meet.callback.CallbackQuitMeet;
import com.huaiye.sdk.sdpmsgs.io.CNotifyReconnectStatus;
import com.huaiye.sdk.sdpmsgs.io.CStartFileConvertRsp;
import com.huaiye.sdk.sdpmsgs.io.NotifyFileConvertStatus;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CInviteUserMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CJoinMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingPicZoomRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingUserRaiseRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyKickUserMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingPushVideo;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingRaiseInfo;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyPeerUserMeetingInfo;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;
import com.huaiye.sdk.sdpmsgs.whiteboard.CNotifyUpdateWhiteboard;
import com.huaiye.sdk.sdpmsgs.whiteboard.CNotifyWhiteboardStatus;
import com.huaiye.sdk.sdpmsgs.whiteboard.CStartWhiteboardShareRsp;
import com.huaiye.sdk.sdpmsgs.whiteboard.CStopWhiteboardShareRsp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.AcceptDiaoDu;
import huaiye.com.vvs.bus.CloseMeetActivity;
import huaiye.com.vvs.bus.FinishDiaoDu;
import huaiye.com.vvs.bus.PhoneStatus;
import huaiye.com.vvs.bus.ShowChangeSizeView;
import huaiye.com.vvs.common.AppAudioManagerWrapper;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppBaseFragment;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ErrorMsg;
import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.common.dialog.LogicDialog;
import huaiye.com.vvs.common.rx.RxUtils;
import huaiye.com.vvs.common.views.PermissionUtils;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.auth.AppAuth;
import huaiye.com.vvs.dao.msgs.AppMessages;
import huaiye.com.vvs.dao.msgs.CallRecordManage;
import huaiye.com.vvs.dao.msgs.MessageData;
import huaiye.com.vvs.dao.msgs.VssMessageBean;
import huaiye.com.vvs.dao.msgs.VssMessageListBean;
import huaiye.com.vvs.dao.msgs.VssMessageListMessages;
import huaiye.com.vvs.models.auth.bean.Upload;
import huaiye.com.vvs.ui.chat.ChatActivity;
import huaiye.com.vvs.ui.home.ContactsChooseActivity;
import huaiye.com.vvs.ui.meet.fragments.MeetBoardFragment;
import huaiye.com.vvs.ui.meet.fragments.MeetMembersLayoutFragment;
import huaiye.com.vvs.ui.meet.fragments.MeetMembersNewFragment;
import huaiye.com.vvs.ui.meet.views.MeetMediaMenuTopView;
import huaiye.com.vvs.ui.meet.views.MeetMediaMenuView;

import static android.app.Activity.RESULT_OK;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_HD;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_VGA;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_camera;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_player;
import static huaiye.com.vvs.common.AppUtils.ctx;
import static huaiye.com.vvs.common.AppUtils.getSize;
import static huaiye.com.vvs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 */
public class MeetViewLayoutNew extends FrameLayout implements SdpUITask.SdpUIListener {

    GesturedTextureLayer layer_gesture;

    View content;
    View iv_change;
    View iv_jinyan;
    View iv_change_size;
    TextureView texture_video;
    TextView tv_notice;
    ImageView iv_encrypt;
    MeetMediaMenuView menu_meet_media;
    MeetMediaMenuTopView menu_meet_media_top;

    MeetBoardFragment mMeetBoardFragment;
    MeetMembersNewFragment mMeetMembersFragment;
    MeetMembersLayoutFragment mMeetLayoutFragment;

    // 是否是推送模式
    boolean isInPicturePushMode;

    boolean isMeetStarter;
    public String strMeetDomainCode;
    public int nMeetID;
    SdkBaseParams.MediaMode mMediaMode;
//    boolean mEncrypt;

    private AppBaseFragment currentFragment;
    private CNotifyWhiteboardStatus status;
    protected AppAudioManagerWrapper audio;
    ShareLocalPopupWindow shareLocalPopupWindow;

    SdpUITask mSdpUITask;
    RxUtils rxUtils;
    boolean isClickClose;

    CNotifyInviteUserJoinMeeting currentMeetingInvite;
    CNotifyUserJoinTalkback currentTalkInvite;

    AppBaseActivity appBaseActivity;

    public void setAppBaseActivity(AppBaseActivity appBaseActivity) {
        this.appBaseActivity = appBaseActivity;

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (mSdpUITask == null) {
            mSdpUITask = new SdpUITask();
            mSdpUITask.setSdpMessageListener(this);
            mSdpUITask.registerSdpNotify(CNotifyPeerUserMeetingInfo.SelfMessageId);
            mSdpUITask.registerSdpNotify(CNotifyMeetingRaiseInfo.SelfMessageId);
            mSdpUITask.registerSdpNotify(CNotifyUpdateWhiteboard.SelfMessageId);
        }

        if (mMeetBoardFragment == null)
            mMeetBoardFragment = new MeetBoardFragment();

        if (mMeetMembersFragment == null)
            mMeetMembersFragment = new MeetMembersNewFragment();

        if (mMeetLayoutFragment == null)
            mMeetLayoutFragment = new MeetMembersLayoutFragment();

        if (appBaseActivity != null) {
            FragmentTransaction ft = appBaseActivity.getSupportFragmentManager().beginTransaction();
            try {
                ft.add(R.id.content, mMeetMembersFragment)
                        .add(R.id.content, mMeetLayoutFragment)
                        .add(R.id.content, mMeetBoardFragment)
                        .hide(mMeetMembersFragment)
                        .hide(mMeetLayoutFragment)
                        .hide(mMeetBoardFragment)
                        .commit();
            } catch (Exception ignored) {

            }
        }
        setIdandOther(null);

    }

    public MeetViewLayoutNew(@NonNull Context context) {
        this(context, null);
    }

    public MeetViewLayoutNew(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.activity_meet_new, null);
        layer_gesture = view.findViewById(R.id.layer_gesture);
        texture_video = view.findViewById(R.id.texture_video);

        content = view.findViewById(R.id.content);
        tv_notice = view.findViewById(R.id.tv_notice);
        iv_change = view.findViewById(R.id.iv_change);
        iv_jinyan = view.findViewById(R.id.iv_jinyan);
        iv_change_size = view.findViewById(R.id.iv_change_size);

        menu_meet_media = view.findViewById(R.id.menu_meet_media);
        menu_meet_media.setIdAndDomain(nMeetID, strMeetDomainCode);
        menu_meet_media_top = view.findViewById(R.id.menu_meet_media_top);
        iv_encrypt = view.findViewById(R.id.iv_encrypt);

        addView(view);

        iv_change.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMeetBoardFragment.isVisible()) {
                    hideAll();
                } else {
                    changeFragment(mMeetBoardFragment);
                }
            }
        });

        if (rxUtils == null)
            rxUtils = new RxUtils();

        addListeners();

    }

    /**
     * 设置相关的id参数
     */
    private void setIdandOther(CGetMeetingInfoRsp rsp) {
        if (mMeetBoardFragment != null) {
            mMeetBoardFragment.setMeetID(nMeetID);
            mMeetBoardFragment.setMeetDomaincode(strMeetDomainCode);
        }

        if (mMeetMembersFragment != null) {
            mMeetMembersFragment.setMeetID(nMeetID);
            mMeetMembersFragment.setMeetDomaincode(strMeetDomainCode);
        }
        if (mMeetLayoutFragment != null) {
            mMeetLayoutFragment.setMeetID(nMeetID);
            mMeetLayoutFragment.setMeetDomaincode(strMeetDomainCode);
        }

        if (menu_meet_media != null) {
            menu_meet_media.hideMasterView(isMeetStarter);
        }

        if (rsp != null && mMeetMembersFragment != null) {
            mMeetMembersFragment.setIsMeetStarter(rsp.strMainUserID.equals(AppDatas.Auth().getUserID() + ""), rsp.strMainUserID);
        }
    }

    /**
     * 获取会议信息
     */
    private void requestInfo() {
        HYClient.getModule(ApiMeet.class)
                .requestMeetDetail(SdkParamsCenter.Meet.RequestMeetDetail()
                                .setnListMode(1)
                                .setMeetID(nMeetID)
                                .setMeetDomainCode(strMeetDomainCode),
                        new SdkCallback<CGetMeetingInfoRsp>() {
                            @Override
                            public void onSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                                isMeetStarter = cGetMeetingInfoRsp.strMainUserID.equals(AppDatas.Auth().getUserID() + "");
                                if (menu_meet_media != null) {
                                    if (cGetMeetingInfoRsp.nRecordID == 0 && isMeetStarter) {
                                        menu_meet_media.canStartRecord(true);
                                    } else {
                                        menu_meet_media.canStartRecord(false);
                                    }

                                    menu_meet_media.hideMasterView(isMeetStarter);
                                    menu_meet_media.canStartRecord(isMeetStarter);

                                }

                                setIdandOther(cGetMeetingInfoRsp);

                                if (menu_meet_media_top != null) {
                                    menu_meet_media_top.showName(cGetMeetingInfoRsp.strMeetingName + "(ID:" + nMeetID + ")");
                                }
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {

                            }
                        });
    }

    void addListeners() {
        if (layer_gesture == null) return;
        layer_gesture.setHelperEnable(true);
        layer_gesture.setEventCallback(new GesturedTextureLayer.Callback() {
            @Override
            public void onDoubleTap(int x, int y) {
                // 画面缩放
                Point point = new Point();
                point.x = x;
                point.y = y;

                if (HYClient.getHYPlayer().Meet(texture_video) == null) {
                    return;
                }

                HYClient.getHYPlayer().Meet(texture_video)
                        .zoom(point, new SdkCallback<CMeetingPicZoomRsp>() {
                            @Override
                            public void onSuccess(CMeetingPicZoomRsp resp) {

                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(errorInfo.getMessage());
                            }

                        });

            }

            @Override
            public void onSingleTap(int x, int y) {
                if (isInPicturePushMode) {
                    isInPicturePushMode = false;

                } else {
                    // 显示菜单
                    if (menu_meet_media.getVisibility() == View.VISIBLE) {
                        hideMenu();
                    } else if (mMeetMembersFragment != null && mMeetLayoutFragment != null &&
                            mMeetBoardFragment != null && (mMeetMembersFragment.isVisible()
                            || mMeetLayoutFragment.isVisible()
                            || mMeetBoardFragment.isVisible())) {

                        if (appBaseActivity == null) {
                            return;
                        }
                        FragmentManager fm = appBaseActivity.getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction()
                                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out);
                        if (mMeetMembersFragment.isVisible()) {
                            menu_meet_media.showHandUpRed(false);
                            ft.hide(mMeetMembersFragment);
                        } else if (mMeetLayoutFragment.isVisible()) {
                            mMeetLayoutFragment.setEnable(false);
                            ft.hide(mMeetLayoutFragment);
                        } else if (mMeetBoardFragment.isVisible()) {
                            ft.hide(mMeetBoardFragment);
                        }
                        try {
                            ft.commit();
                        } catch (Exception e) {
                        }

                    } else {
                        menu_meet_media.setVisibility(View.VISIBLE);
                        menu_meet_media_top.setVisibility(View.VISIBLE);

                        rxUtils.doDelay(5000, new RxUtils.IMainDelay() {
                            @Override
                            public void onMainDelay() {
                                if (shareLocalPopupWindow != null && shareLocalPopupWindow.isShowing()) {
                                    return;
                                }
                                menu_meet_media.setVisibility(View.GONE);
                                menu_meet_media_top.setVisibility(View.GONE);
                            }
                        }, "hide");
                    }
                }
            }

            @Override
            public void onScroll(int startX, int startY, int endX, int endY) {
                // 画面交换
//                Point point1 = new Point();
//                point1.x = startX;
//                point1.y = startY;
//
//                Point point2 = new Point();
//                point2.x = endX;
//                point2.y = endY;
//
//                HYClient.getHYPlayer().Meet(texture_video).swap(point1, point2,
//                        new SdkCallback<CMeetingPicSwapRsp>() {
//                            @Override
//                            public void onSuccess(CMeetingPicSwapRsp resp) {
//                                showToast("画面交换成功");
//                            }
//
//                            @Override
//                            public void onError(ErrorInfo errorInfo) {
//                                showToast(errorInfo.getMessage());
//                            }
//
//                        });
            }

            @Override
            public void onPointError() {
                // 坐标错误
//                showToast("坐标不合理，请重新取坐标");
            }
        });

        menu_meet_media.setCallback(new MeetMediaMenuView.Callback() {

            @Override
            public void onMeetExitClicked() {
                // 退出会议
                onBackPressed();
            }

            @Override
            public void onMemberListClicked() {
                // 人员列表
                hideMenu();

                changeFragment(mMeetMembersFragment);
            }

            @Override
            public void onMeetInviteClicked() {
                // 会议邀请
                HYClient.getModule(ApiMeet.class).requestMeetDetail(
                        SdkParamsCenter.Meet.RequestMeetDetail()
                                .setMeetDomainCode(strMeetDomainCode)
                                .setnListMode(1)
                                .setMeetID(nMeetID), new SdkCallback<CGetMeetingInfoRsp>() {
                            @Override
                            public void onSuccess(final CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                                new RxUtils<ArrayList<CGetMeetingInfoRsp.UserInfo>>()
                                        .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<ArrayList<CGetMeetingInfoRsp.UserInfo>>() {

                                            @Override
                                            public ArrayList<CGetMeetingInfoRsp.UserInfo> doOnThread() {
                                                ArrayList<CGetMeetingInfoRsp.UserInfo> tempAll = new ArrayList();
                                                for (CGetMeetingInfoRsp.UserInfo temp : cGetMeetingInfoRsp.listUser) {
                                                    if (temp.nJoinStatus == 2) {
                                                        tempAll.add(temp);
                                                    }
                                                }

                                                return tempAll;
                                            }

                                            @Override
                                            public void doOnMain(ArrayList<CGetMeetingInfoRsp.UserInfo> data) {
                                                if (appBaseActivity == null) {
                                                    return;
                                                }

                                                Intent intent = new Intent(appBaseActivity, ContactsChooseActivity.class);
                                                intent.putExtra("users", data);
                                                appBaseActivity.startActivityForResult(intent, 1000);
                                            }
                                        });
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                            }
                        });

            }

            @Override
            public void onInnerCameraClicked() {
                // 摄像头切换
                HYClient.getHYCapture().toggleInnerCamera();
            }

            @Override
            public void onPlayerVoiceClicked(boolean isVoiceOpened) {
                // 静音
                HYClient.getHYPlayer().setAudioOnEx(isVoiceOpened, texture_video);
            }

            @Override
            public void onPlayerVideoClicked(boolean isVideoOpened) {
                HYClient.getHYCapture().setCaptureVideoOn(isVideoOpened);
            }

            @Override
            public void showLayoutChange() {
                hideMenu();
                mMeetLayoutFragment.requestLayoutInfo();
                changeFragment(mMeetLayoutFragment);
            }

            @Override
            public void showSharePop(View view) {
                if (status != null) {
                    toggleWhiteBoard();
                } else if (shareLocalPopupWindow != null) {
                    shareLocalPopupWindow.showView(view);
                }
            }

            @Override
            public void onHandUp() {
                HYClient.getModule(ApiMeet.class).raiseHandsInMeeting(
                        SdkParamsCenter.Meet.UserRaise()
                                .setStrMeetingDomainCode(strMeetDomainCode)
                                .setnMeetingID(nMeetID), new SdkCallback<CMeetingUserRaiseRsp>() {

                            @Override
                            public void onSuccess(CMeetingUserRaiseRsp info) {
                                showToast(AppUtils.getString(R.string.hand_up_success));
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(ErrorMsg.getMsg(ErrorMsg.raise_hands_err_code));
                            }
                        });
            }

            @Override
            public void startRecordSuccess() {
                menu_meet_media_top.isRecord(true);
            }

            @Override
            public void onOutSideCameraClicked() {
                if (HYUsbCameraManager.get().haveUSBCamera()){
                    HYClient.getHYCapture().requestUsbCamera();
                }else {
                    AppUtils.showToast(AppUtils.getString(R.string.no_find_usb_camera));
                }
            }

            @Override
            public void onCaptureVoiceClicked() {
                menu_meet_media.toggleCaptureAudio();
            }
        });
        iv_change_size.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PermissionUtils.XiaoMiMobilePermission(AppUtils.ctx)) {
                    return;
                }

                if (iChangeSize != null) {
                    iChangeSize.changeSize();
                }

                if (appBaseActivity != null) {
                    ((MeetActivity) appBaseActivity).showHide();
                }
            }
        });

    }

    /**
     * 展示fragment
     *
     * @param fragment
     */
    private void changeFragment(AppBaseFragment fragment) {
        currentFragment = fragment;
        LayoutParams lp = (LayoutParams) content.getLayoutParams();
        if (fragment instanceof MeetMembersLayoutFragment ||
                fragment instanceof MeetBoardFragment) {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            lp.width = getSize(250);
        }

        if (content != null)
            content.setLayoutParams(lp);

        if (appBaseActivity != null) {
            try {
                FragmentManager fm = appBaseActivity.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out);
                ft.show(fragment);
                ft.commit();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 隐藏菜单
     */
    private void hideMenu() {
        if (menu_meet_media != null) {
            menu_meet_media.setVisibility(View.GONE);
//            menu_meet_media.getHandler().removeCallbacksAndMessages(null);
        }
        if (menu_meet_media_top != null) {
            menu_meet_media_top.setVisibility(View.GONE);
//            menu_meet_media_top.getHandler().removeCallbacksAndMessages(null);
        }
    }

    /**
     * 加入会议
     */
    @SuppressLint("WrongConstant")
    private void joinMeet() {
        AppUtils.isMeet = true;
        if (shareLocalPopupWindow == null && appBaseActivity != null) {

            shareLocalPopupWindow = new ShareLocalPopupWindow(appBaseActivity);
            shareLocalPopupWindow.setConfirmClickListener(new ShareLocalPopupWindow.ConfirmClickListener() {
                @Override
                public void onShareImg() {
                    if (status != null) {
                        showToast(AppUtils.getString(R.string.whiteboard_open_success));
                        return;
                    }
                    startWhiteBoard(1, 2);

                }

                @Override
                public void onOpenWhiteBoard() {
                    toggleWhiteBoard();
                }

                @Override
                public void onShareFile() {
                    if (status != null) {
                        showToast(AppUtils.getString(R.string.whiteboard_open_success));
                        return;
                    }
                    startWhiteBoard(1, 1);
                }

                @Override
                public void onCancel() {
                    menu_meet_media.setVisibility(View.GONE);
                    menu_meet_media_top.setVisibility(View.GONE);
                }
            });
        }
        shareLocalPopupWindow.init();
        Logger.debug("MeetViewLayout", "joinMeet strMeetDomainCode " + strMeetDomainCode + " nMeetID " + nMeetID + " mMediaMode " + mMediaMode);
        Logger.debug("MeetViewLayout", "joinMeet texture_video " + texture_video.getVisibility());
        ParamsJoinMeet paramsJoinMeet = SdkParamsCenter.Meet.JoinMeet()
                .setMeetDomainCode(strMeetDomainCode)
                .setMeetID(nMeetID)
                .setAgreeMode(SdkBaseParams.AgreeMode.Agree)
                .setCameraIndex(SP.getInteger(STRING_KEY_camera, -1) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                .setMediaMode(mMediaMode)
                .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_AUTO)
//                .setPlayerVideoScaleType(SdkBaseParams.VideoScaleType.ASPECT_FIT)
                .setMeetScreen(SdkBaseParams.MeetScreen.Multiple)
                .setIsAutoStopCapture(true)
                .setPlayerPreview(texture_video)
                .setnIsOnlyAudio(0);

        setVisibility(VISIBLE);
        if (AppUtils.isCaptureLayoutShowing) {
            paramsJoinMeet.setIsAutoStopCapture(false);
        } else {
            HYClient.getHYCapture().stopCapture(null);
        }
        startAudio();
        HYClient.getModule(ApiMeet.class)
                .joinMeeting(paramsJoinMeet, new CallbackJoinMeet() {
                    @Override
                    public void onAgreeMeet(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
                    }

                    @Override
                    public void onRefuseMeet(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
                    }

                    @Override
                    public void onNoResponse(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
                    }

                    @Override
                    public void onUserOffline(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
                    }

                    @Override
                    public void onRepeatInvitation(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
                    }

                    @Override
                    public void onMeetStatusChanged(final CNotifyMeetingStatusInfo cNotifyMeetingStatusInfo) {
                        Logger.debug( "MeetViewLayoutNew ,joinMeet CNotifyMeetingStatusInfo " + cNotifyMeetingStatusInfo);
                        meetStatusChange(cNotifyMeetingStatusInfo);
                    }

                    @Override
                    public void onKickedFromMeet(CNotifyKickUserMeeting cNotifyKickUserMeeting) {
                        // 被踢出会议
                        AppMessages.get().add(MessageData.from(cNotifyKickUserMeeting));

                        showToast(AppUtils.getString(R.string.you_bei_kitout_meet));
                        delayFinish("被踢出会议");
                    }

                    @Override
                    public void onUserRaiseOfMeet(CNotifyMeetingRaiseInfo cNotifyMeetingRaiseInfo) {
                        onUserRaiseOfMeetActivity(cNotifyMeetingRaiseInfo);
                    }

                    @Override
                    public void onVideoLimiteMeet(CNotifyMeetingPushVideo info) {
                        if (info.nVideolimited == 1) {
                            showToast(AppUtils.getString(R.string.you_leave_draw));
                        } else {
                            showToast(AppUtils.getString(R.string.you_join_draw));
                        }
                    }

                    @Override
                    public void onCaptureStatusChanged(SdpMessageBase msg) {
                        if (msg instanceof CNotifyReconnectStatus){
                            CNotifyReconnectStatus cNotifyReconnectStatus = (CNotifyReconnectStatus) msg;
                            if (cNotifyReconnectStatus.getConnectionStatus() == SdkBaseParams.ConnectionStatus.Connecting
                                    || cNotifyReconnectStatus.getConnectionStatus() == SdkBaseParams.ConnectionStatus.Disconnected){
                                if (menu_meet_media_top != null){
                                    SdpMsgCaptureQualityNotify newMsg = new SdpMsgCaptureQualityNotify();
                                    newMsg.m_nCurQuality = -1;
                                    menu_meet_media_top.changeQuality( newMsg);
                                }
                            }
                            return;
                        }

                        switch (MediaStatus.get(msg)) {
                            case CAPTURE_QUALITY:
                                if (menu_meet_media_top != null)
                                    menu_meet_media_top.changeQuality((SdpMsgCaptureQualityNotify) msg);
                                break;
                        }
                    }

                    @Override
                    public void onMeetFinished() {

                    }

                    @Override
                    public void onReceiverWhiteboardStatus(CNotifyWhiteboardStatus status) {
                        boolean isOwner = status.strInitiatorDomainCode.equals(HYClient.getSdkOptions().User().getDomainCode()) &&
                                status.strInitiatorTokenID.equals(HYClient.getSdkOptions().User().getUserTokenId());
                        if (mMeetBoardFragment != null)
                            mMeetBoardFragment.changeOpenStatus(status.nStatus == 1);
                        if (status.nStatus == 1) {
                            openBoardView(status, isOwner);
                        } else {
                            closeBoardView(isOwner);
                        }
                    }

                    @Override
                    public void onSuccess(CJoinMeetingRsp cJoinMeetingRsp) {
                        Logger.debug("MeetViewLayout"+ " joinMeet success " + cJoinMeetingRsp);


                        requestInfo();

                        setIdandOther(null);

                        if (TextUtils.isEmpty(SP.getString(STRING_KEY_player))) {
                            SP.setParam(STRING_KEY_player, STRING_KEY_VGA);
                        }
                        if (SP.getString(STRING_KEY_player).equals(STRING_KEY_HD)) {
                            HYClient.getHYPlayer().Meet(texture_video).setPlayQuality(SdkBaseParams.PlayQuality.HD);
                        } else {//VGA
                            HYClient.getHYPlayer().Meet(texture_video).setPlayQuality(SdkBaseParams.PlayQuality.VGA);
                        }

                        if (menu_meet_media_top != null) {
                            if (appBaseActivity != null) {
                                appBaseActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            } else {
                                ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            }
                            menu_meet_media_top.startTime();
                        }

                        if (menu_meet_media == null) return;

                        menu_meet_media.changeSound(true);

                        iv_change_size.setVisibility(VISIBLE);
                        if (HYClient.getSdkOptions().encrypt().isEncryptBind()){
                            iv_encrypt.setVisibility(View.VISIBLE);
                        }else {
                            iv_encrypt.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onAfterSuccess(CGetMeetingInfoRsp userList){

                    }
                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        Logger.log("MeetViewLayoutNew error" );
                        if (errorInfo.getCode() == ErrorMsg.meet_not_exit_code ||
                                errorInfo.getCode() == ErrorMsg.meet_not_koten_code) {
                            showToast(ErrorMsg.getMsg(ErrorMsg.meet_not_exit_code));
                        } else {
                            showToast(ErrorMsg.getMsg(ErrorMsg.joine_err_code));
                        }
                        delayFinish("771");
                    }
                });
    }

    private void startAudio() {
        audio = new AppAudioManagerWrapper();
        audio.start();
    }

    private void stopAudio() {
        if (audio != null) {
            audio.stop();
            audio = null;
        }
    }

    /**
     * 打开关闭白板
     */
    private void toggleWhiteBoard() {
        if (status == null) {
            startWhiteBoard(0, 0);
        } else if (status.strInitiatorDomainCode.equals(HYClient.getSdkOptions().User().getDomainCode()) &&
                status.strInitiatorTokenID.equals(HYClient.getSdkOptions().User().getUserTokenId())) {
            stopWhiteBoard();
        } else {
            showToast(AppUtils.getString(R.string.no_start_user));
        }
    }

    /**
     * 结束
     */
    private void delayFinish(String from) {
        finishActivityAndView(from);
    }

    private void finishActivityAndView(String from) {
        if (appBaseActivity != null) {
            ((MeetActivity) appBaseActivity).finishMeet("finishActivityAndView " + from);
        }
        EventBus.getDefault().post(new ShowChangeSizeView(false));
        onDestroy();
    }

    private void onUserOfflineActivity(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
    }

    private void onNoResponseActivity(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
        if (isMeetStarter) {
            showToast(cNotifyPeerUserMeetingInfo.strToUserName + AppUtils.getString(R.string.no_xiangying));
        }
    }

    private void onAgreeMeetActivity(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
        if (cNotifyPeerUserMeetingInfo.strUserID.equals(HYClient.getSdkOptions().User().getUserId())
                && cNotifyPeerUserMeetingInfo.strToUserDomainCode.equals(HYClient.getSdkOptions().User().getDomainCode())) {
        } else {
            if (mMeetMembersFragment != null)
                mMeetMembersFragment.refUser();
        }
    }

    private void onRefuseMeetActivity(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
        if (isMeetStarter) {
            showToast(cNotifyPeerUserMeetingInfo.strToUserName + AppUtils.getString(R.string.refuse));
        }
    }

    private void onUserRaiseOfMeetActivity(CNotifyMeetingRaiseInfo cNotifyMeetingRaiseInfo) {
        showToast(cNotifyMeetingRaiseInfo.strUserName + AppUtils.getString(R.string.has_handup));

        if (mMeetMembersFragment == null) return;

        mMeetMembersFragment.changeDataHandUp(cNotifyMeetingRaiseInfo);
        if (!mMeetMembersFragment.isVisible()) {
            menu_meet_media.showHandUpRed(true);
        }
    }

    private void meetStatusChange(final CNotifyMeetingStatusInfo cNotifyMeetingStatusInfo) {
        if (cNotifyMeetingStatusInfo.nMeetingStatus == 2) {
            quitMeet(isMeetStarter);

            showToast(AppUtils.getString(R.string.meet_diaodu_has_end));
        } else {

            if (mMeetMembersFragment != null) {
                mMeetMembersFragment.changeOneKey(cNotifyMeetingStatusInfo.isMeetMute());
                mMeetMembersFragment.refUser();
            }

            for (CNotifyMeetingStatusInfo.User temp : cNotifyMeetingStatusInfo.lstMeetingUser) {
                if (temp.strUserID.equals(AppDatas.Auth().getUserID() + "")) {
                } else {
                    if (temp.nPartType == 0) {
                        showToast(temp.strUserName + AppUtils.getString(R.string.meet_join));
                    } else if (temp.nPartType == 1) {
                        showToast(temp.strUserName + AppUtils.getString(R.string.meet_leave));
                    }
                }
            }
            new RxUtils<CNotifyMeetingStatusInfo.User>()
                    .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<CNotifyMeetingStatusInfo.User>() {
                        @Override
                        public CNotifyMeetingStatusInfo.User doOnThread() {
                            CNotifyMeetingStatusInfo.User tempAll = null;
                            for (CNotifyMeetingStatusInfo.User temp : cNotifyMeetingStatusInfo.lstMeetingUser) {
                                if (temp.strUserID.equals(AppDatas.Auth().getUserID() + "")) {
                                    tempAll = temp;
                                    break;
                                }
                            }
                            return tempAll;
                        }

                        @Override
                        public void doOnMain(CNotifyMeetingStatusInfo.User tempAll) {
                            if (tempAll.nMuteStatus == SdkBaseParams.MuteStatus.Mute.value()) {
                                iv_jinyan.setVisibility(View.GONE);
                            } else {
                                iv_jinyan.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }

    /**
     * 打开白板界面SetPlayerSurface
     *
     * @param status
     * @param isOwner
     */
    private void openBoardView(CNotifyWhiteboardStatus status, boolean isOwner) {
        this.status = status;
        iv_change.setVisibility(View.VISIBLE);
        menu_meet_media.setWhiteBoardTxt(AppUtils.getString(R.string.local_share), false, isOwner);
        if (shareLocalPopupWindow != null && isOwner) {
            shareLocalPopupWindow.changeStatue(AppUtils.getString(R.string.white_board_close));
        }

        hideMenu();

        if (mMeetBoardFragment != null)
            mMeetBoardFragment.openWhiteBoard(false, null, false);

        changeFragment(mMeetBoardFragment);
    }

    /**
     * 关闭白板界面
     *
     * @param isOwner
     */
    public void closeBoardView(boolean isOwner) {
        this.status = null;
        iv_change.setVisibility(View.GONE);
        exitWhiteBoardDeal();
        menu_meet_media.setWhiteBoardTxt(AppUtils.getString(R.string.local_share), true, isOwner);
        if (shareLocalPopupWindow != null) {
            shareLocalPopupWindow.changeStatue(AppUtils.getString(R.string.whit_board_open));
        }
    }

    /**
     * 退出会议
     *
     * @param isFinish
     */
    public void quitMeet(boolean isFinish) {
        isClickClose = true;

        stopWhiteBoard();
        ParamsQuitMeet paramsQuitMeet = SdkParamsCenter.Meet.QuitMeet()
                .setQuitMeetType(isFinish ? SdkBaseParams.QuitMeetType.Finish : SdkBaseParams.QuitMeetType.Quit)
                .setMeetDomainCode(strMeetDomainCode)
                .setStopCapture(true)
                .setMeetID(nMeetID);
        if (AppUtils.isCaptureLayoutShowing) {
            paramsQuitMeet.setStopCapture(false);
        }
        HYClient.getModule(ApiMeet.class)
                .quitMeeting(paramsQuitMeet, null);

        finishActivityAndView("quitmeet");
    }

    /**
     * 改变不同会议
     *
     * @param temp
     */
    public void changeCurrentMeet(CNotifyInviteUserJoinMeeting temp) {
        menu_meet_media_top.isRecord(false);
        isMeetStarter = temp.isSelfMeetCreator();
        strMeetDomainCode = temp.strMeetingDomainCode;
        nMeetID = temp.nMeetingID;
        mMediaMode = temp.getRequiredMediaMode();
        joinMeet();
    }


    /**
     * 开启白本
     */
    private void startWhiteBoard(final int type, final int choose) {
        HYClient.getModule(ApiMeet.class)
                .startWhiteBoard(SdkParamsCenter.Meet.StartWhiteBoard()
                                .setnMeetingID(nMeetID)
                                .setnShareType(type),
                        new SdkCallback<CStartWhiteboardShareRsp>() {
                            @Override
                            public void onSuccess(CStartWhiteboardShareRsp cStartWhiteboardShareRsp) {
                                if (type == 0) {
                                    return;
                                }
                                startFileAndPhoto(choose);
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(ErrorMsg.getMsgWhiterBoard(errorInfo.getCode()));
                                if (errorInfo.getCode() == ErrorMsg.white_board_has_exist_code) {
                                    startFileAndPhoto(choose);
                                }
                            }
                        });
    }

    private void startFileAndPhoto(int choose) {
        if (appBaseActivity == null) {
            return;
        }
        if (choose == 1) {
            Intent intent = new Intent(appBaseActivity, ChooseFilesActivity.class);
            intent.putExtra("nMeetID", nMeetID);
            appBaseActivity.startActivityForResult(intent, 1001);
        } else {
            Intent intent = new Intent(appBaseActivity, ChoosePhotoActivity.class);
            intent.putExtra("nMeetID", nMeetID);
            appBaseActivity.startActivityForResult(intent, 1001);
        }
    }

    /**
     * 关闭白板
     */
    public void stopWhiteBoard() {
        exitWhiteBoardDeal();
        HYClient.getModule(ApiMeet.class).stopWhiteBoard(SdkParamsCenter.Meet.StopWhiteBoard().setnMeetingID(nMeetID),
                new SdkCallback<CStopWhiteboardShareRsp>() {
                    @Override
                    public void onSuccess(CStopWhiteboardShareRsp cStopWhiteboardShareRsp) {
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                    }
                });
    }

    public void onResume() {
        menu_meet_media.reSetVoice();
        HYClient.getHYPlayer().onPlayFront();
        if (rxUtils != null) {
            rxUtils.doDelay(200, new RxUtils.IMainDelay() {
                @Override
                public void onMainDelay() {
                    ToggleBackgroundState();
                }
            }, "toggle");
        }

    }

    public void onPause() {
        HYClient.getHYPlayer().onPlayBackground();
        ToggleBackgroundState();
    }

    public void ToggleBackgroundState() {
        try {

            JniIntf.SetCapturerPreviewTexture(null);

        } catch (Exception e) {
            Logger.log("ToggleBackgroundState Exception..." + e);
        }
    }

    public void hideAll() {

        if (currentFragment instanceof MeetMembersNewFragment) {
            menu_meet_media.showHandUpRed(false);
        }

        currentFragment = null;

        if (appBaseActivity == null) {
            return;
        }
        try {
            FragmentTransaction ft = appBaseActivity.getSupportFragmentManager().beginTransaction();
            ft.hide(mMeetMembersFragment)
                    .hide(mMeetLayoutFragment)
                    .hide(mMeetBoardFragment)
                    .commit();
        } catch (Exception e) {
        }
    }

    public void onBackPressed() {

        if ((mMeetMembersFragment != null && mMeetMembersFragment.isVisible()) ||
                (mMeetLayoutFragment != null && mMeetLayoutFragment.isVisible()) ||
                (mMeetBoardFragment != null && mMeetBoardFragment.isVisible())) {
            hideAll();
            return;
        }

        final LogicDialog dialog = ((MCApp) ctx).getTopActivity().getLogicDialog().setMessageText(isMeetStarter ? AppUtils.getString(R.string.is_jiesan_diaodu) : AppUtils.getString(R.string.is_exite_diaodu));
        if (isMeetStarter) {
            dialog.setConfirmText(AppUtils.getString(R.string.exite));
            dialog.setCancelText(AppUtils.getString(R.string.jiesan));
        } else {
            dialog.setConfirmText(AppUtils.getString(R.string.exite));
            dialog.setCancelText(AppUtils.getString(R.string.cancel));
        }
        dialog.setConfirmClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                quitMeet(false);
            }
        }).setCancelClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMeetStarter) {
                    quitMeet(isMeetStarter);
                } else {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        }).show();
    }

    public void closeIfTalkDisconnect() {
        HYClient.getModule(ApiMeet.class)
                .requestMeetDetail(SdkParamsCenter.Meet.RequestMeetDetail()
                                .setnListMode(1)
                                .setMeetID(nMeetID)
                                .setMeetDomainCode(strMeetDomainCode),
                        new SdkCallback<CGetMeetingInfoRsp>() {
                            @Override
                            public void onSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                                Logger.debug("MeetViewLayoutNew", " closeIfTalkDisconnect " + cGetMeetingInfoRsp.isMeetFinished());
                                if (cGetMeetingInfoRsp.isMeetFinished()) {
                                    closeMeet(null);
                                    return;
                                }
                                ArrayList<CGetMeetingInfoRsp.UserInfo> listUser = cGetMeetingInfoRsp.listUser;
                                boolean containMyself = false;
                                if (listUser != null && listUser.size() > 0) {
                                    for (CGetMeetingInfoRsp.UserInfo oneUser : listUser) {
                                        if (oneUser.strUserID.equals(AppAuth.get().getUserID() + "")) {
                                            containMyself = true;
                                            break;
                                        }
                                    }
                                }
                                if (!containMyself) {
                                    closeMeet(null);
                                    return;
                                }

                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {

                            }
                        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeMeet(CloseMeetActivity bean) {
        if (AppUtils.isMeet)
            quitMeet(false);
    }

    public void endMeet(final CallbackQuitMeet callbackQuitMeet) {
        HYClient.getModule(ApiMeet.class)
                .quitMeeting(SdkParamsCenter.Meet.QuitMeet()
                        .setQuitMeetType(SdkBaseParams.QuitMeetType.Quit)
                        .setMeetDomainCode(strMeetDomainCode)
                        .setStopCapture(false)
                        .setMeetID(nMeetID), new CallbackQuitMeet() {
                    @Override
                    public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                        return callbackQuitMeet.isContinueOnStopCaptureError(errorInfo);
                    }

                    @Override
                    public void onSuccess(Object o) {
                        finishActivityAndView("endMeet");
                        callbackQuitMeet.onSuccess(o);
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        callbackQuitMeet.onError(errorInfo);
                    }
                });

    }

    boolean hasBusy;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PhoneStatus status) {
        if (status.isBusy) {
            hasBusy = true;
            menu_meet_media.closeVoice();
        } else {
            menu_meet_media.reSetVoice();

            if (hasBusy) {
                hasBusy = false;
                if (rxUtils != null) {
                    rxUtils.doDelayOn(300, new RxUtils.IMainDelay() {
                        @Override
                        public void onMainDelay() {
                            stopAudio();
                            startAudio();
                        }
                    });
                }
            }

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
        if (currentTalkInvite == null) {
            return;
        }

        if (info.nTalkbackID == currentTalkInvite.nTalkbackID
                && info.isTalkingStopped() && ((MCApp) ctx).getTopActivity().getLogicTimeDialog().isShowing()) {
            ((MCApp) ctx).getTopActivity().getLogicTimeDialog().dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VssMessageBean bean) {
        tv_notice.setText(AppUtils.getString(R.string.receive) + bean.fromUserName + AppUtils.getString(R.string.place_watch));
        tv_notice.setTag(bean);
        tv_notice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PermissionUtils.XiaoMiMobilePermission(AppUtils.ctx)) {
                    return;
                }

                if (appBaseActivity == null) {
                    return;
                }
                VssMessageBean tag = (VssMessageBean) v.getTag();
                if (tag == null) {
                    return;
                }

                VssMessageListBean bean = VssMessageListMessages.get().getMessages(tag.sessionID);
                bean.isRead = 1;
                VssMessageListMessages.get().isRead(bean);

                Intent intent = new Intent(appBaseActivity, ChatActivity.class);
                intent.putExtra("listBean", bean);
                appBaseActivity.startActivity(intent);

                iv_change_size.performClick();
            }
        });
        tv_notice.setVisibility(VISIBLE);
        rxUtils.doDelay(5000, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                tv_notice.setText("");
                tv_notice.setVisibility(GONE);
            }
        }, System.currentTimeMillis() + "");

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                onInviteClicked((ArrayList<CStartMeetingReq.UserInfo>) data.getSerializableExtra("data"));
            } else {
                Upload upload = (Upload) data.getSerializableExtra("updata");

                HYClient.getModule(ApiIO.class)
                        .startFileConvert(SdkParamsCenter.IO.StartFileConvert()
                                        .setStrFilePath(upload.file1_name),
                                new CallbackStartFileConvert() {
                                    @Override
                                    public void startResult(CStartFileConvertRsp cStartFileConvertRsp) {
                                        if (cStartFileConvertRsp.nResultCode != 0) {
                                            status = null;
                                            showToast(AppUtils.getString(R.string.file_trans_false));
                                            exitWhiteBoardDeal();
                                        }
                                    }

                                    @Override
                                    public void convertResult(NotifyFileConvertStatus notifyFileConvertStatus) {
                                        if (notifyFileConvertStatus.isConvertSuccess()) {
                                            if (mMeetBoardFragment != null)
                                                mMeetBoardFragment.openWhiteBoard(true, notifyFileConvertStatus, true);
                                        } else {
                                            status = null;
                                            showToast(AppUtils.getString(R.string.file_trans_false));
                                            exitWhiteBoardDeal();
                                        }
                                    }

                                    @Override
                                    public void onSuccess(Object o) {

                                    }

                                    @Override
                                    public void onError(ErrorInfo errorInfo) {
                                        exitWhiteBoardDeal();
                                    }
                                });

            }
        }
    }

    private void exitWhiteBoardDeal() {
        if (mMeetBoardFragment != null) {
            mMeetBoardFragment.exiteWhiteBoard();
        }
    }

    /**
     * 邀请返回
     */
    void onInviteClicked(ArrayList<CStartMeetingReq.UserInfo> users) {
        HYClient.getModule(ApiMeet.class).inviteUser(SdkParamsCenter.Meet.InviteMeet()
                        .setMeetDomainCode(strMeetDomainCode)
                        .setMeetID(nMeetID)
                        .setUsers(users),
                new SdkCallback<CInviteUserMeetingRsp>() {
                    @Override
                    public void onSuccess(CInviteUserMeetingRsp cInviteUserMeetingRsp) {
                        showToast(AppUtils.getString(R.string.has_invisitor_success));
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(ErrorMsg.invite_user_err_code));
                    }
                });

    }

    public void onDestroy() {

        AppUtils.isMeet = false;

        if (rxUtils != null) {
            rxUtils.clearAll();
        }

        if (menu_meet_media_top != null) {
            menu_meet_media_top.onDestory();
        }

        destruct();

        stopAudio();

        HYClient.getHYCapture().setCameraConferenceMode(HYCapture.CameraConferenceMode.PORTRAIT);

        EventBus.getDefault().unregister(this);

        try {
            if (appBaseActivity != null) {
                FragmentTransaction ft = appBaseActivity.getSupportFragmentManager().beginTransaction();
                ft.remove(mMeetMembersFragment).remove(mMeetLayoutFragment).remove(mMeetBoardFragment).commit();
            }
        } catch (Exception e) {
            Logger.log("Meet Destory onDestroy Error " + e.getMessage());
        }
        currentFragment = null;
        mMeetMembersFragment = null;
        mMeetLayoutFragment = null;
        mMeetBoardFragment = null;

        setVisibility(GONE);

        EventBus.getDefault().post(new FinishDiaoDu("meet view layout 1294"));

        AppUtils.reSetMeetView();

        if (appBaseActivity != null) {
            appBaseActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            if (MCApp.getInstance().getTopActivity() != null) {
                MCApp.getInstance().getTopActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        appBaseActivity = null;

    }

    @Override
    public void onSdpMessage(SdpMessageBase sdpMessageBase, int i) {
        switch (sdpMessageBase.GetMessageType()) {
            case CNotifyPeerUserMeetingInfo.SelfMessageId:
                CNotifyPeerUserMeetingInfo peerInfo = (CNotifyPeerUserMeetingInfo) sdpMessageBase;

                if (peerInfo.nMeetingID != nMeetID
                        || !peerInfo.strMeetingDomainCode.equals(strMeetDomainCode)) {
                    return;
                }

                if (peerInfo.nIsAgree == 0) {
                    // 对方拒绝
                    onRefuseMeetActivity(peerInfo);
                } else if (peerInfo.nIsAgree == 1) {
                    // 对方同意
                    onAgreeMeetActivity(peerInfo);
                } else if (peerInfo.nIsAgree == 2) {
                    // 对方无人接听
                    onNoResponseActivity(peerInfo);
                } else if (peerInfo.nIsAgree == 3) {
                    // 对方离线
                    onUserOfflineActivity(peerInfo);
                } else if (peerInfo.nIsAgree == 4) {
                    // 重复邀请
                }
                break;
            case CNotifyMeetingRaiseInfo.SelfMessageId:
                CNotifyMeetingRaiseInfo raiseInfo = (CNotifyMeetingRaiseInfo) sdpMessageBase;

                if (raiseInfo.nMeetingID != nMeetID
                        || !raiseInfo.strMeetingDomainCode.equals(strMeetDomainCode)) {
                    return;
                }
                onUserRaiseOfMeetActivity(raiseInfo);
                break;
            case CNotifyUpdateWhiteboard.SelfMessageId:
                CNotifyUpdateWhiteboard info = (CNotifyUpdateWhiteboard) sdpMessageBase;
                if (info.nMeetingID != nMeetID) {
                    return;
                }
                mMeetBoardFragment.notiyUpdate(info);
                break;
        }
    }

    private void destruct() {
        if (mSdpUITask != null) {
            mSdpUITask.exit();
            mSdpUITask = null;
        }
    }

    public void startJoinMeet(boolean b, String strMeetingDomainCode, int nMeetingID, SdkBaseParams.MediaMode requiredMediaMode) {
        this.isMeetStarter = b;
        this.strMeetDomainCode = strMeetingDomainCode;
        this.nMeetID = nMeetingID;
//        this.mEncrypt = isEncrypt;
        this.mMediaMode = requiredMediaMode;

        HYClient.getHYCapture().setCameraConferenceMode(HYCapture.CameraConferenceMode.LANDSCAPE);

        joinMeet();
    }

    public void onMeetInvite(AppBaseActivity activity, final CNotifyInviteUserJoinMeeting data, final long millis) {

        if (data == null) {
            quitMeet(false);
            return;
        }

        if (data.nMeetingStatus != 1) {
            return;
        }

        if (data.isForceInvite()) {
            return;
        }

        if (((MCApp) ctx).getTopActivity().getLogicTimeDialog().isShowing()) {
//            HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
//                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
//                    .setMeetID(data.nMeetingID)
//                    .setMeetDomainCode(data.strMeetingDomainCode), null);
            return;
        }
        currentMeetingInvite = data;
        final CNotifyInviteUserJoinMeeting temp = data;

        //观摩态，自己邀请自己，同时是这个会议的时候，直接进入
        if ((data.strInviteUserTokenID.equals(AppDatas.Auth().getData("tokenId")) ||
                data.strInviteUserTokenID.equals(AppDatas.Auth().getUserID() + ""))
                && nMeetID == data.nMeetingID) {
            AppMessages.get().del(millis);
            closeBoardView(false);

            HYClient.getHYPlayer().stopPlayEx(new SdkCallback<VideoParams>() {
                @Override
                public void onSuccess(VideoParams params) {
                    changeCurrentMeet(temp);
                }

                @Override
                public void onError(ErrorInfo errorInfo) {

                }
            }, texture_video);

            return;
        }

        ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setCancelable(false);

        // 会议中来会议邀请，对话框提示
        ((MCApp) ctx).getTopActivity().getLogicTimeDialog()
                .setTitleText(AppUtils.getString(R.string.invisitor_title))
                .setMessageText(data.strInviteUserName + AppUtils.getString(R.string.is_accept_meet_diaodu))
                .setCancelClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppMessages.get().del(millis);
                        HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                                .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                                .setMeetID(data.nMeetingID)
                                .setMeetDomainCode(data.strMeetingDomainCode), null);
                        CallRecordManage.get().updateCall(data.nMsgSessionID);
                    }
                })
                .setConfirmClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CallRecordManage.get().updateCall(data.nMsgSessionID);
                        AppMessages.get().del(millis);

                        closeBoardView(false);
                        stopWhiteBoard();
                        HYClient.getModule(ApiMeet.class)
                                .quitMeeting(SdkParamsCenter.Meet.QuitMeet()
                                        .setQuitMeetType(SdkBaseParams.QuitMeetType.Quit)
                                        .setMeetDomainCode(strMeetDomainCode)
                                        .setStopCapture(false)
                                        .setMeetID(nMeetID), new CallbackQuitMeet() {
                                    @Override
                                    public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                                        return true;
                                    }

                                    @Override
                                    public void onSuccess(Object o) {
                                        changeCurrentMeet(temp);
                                    }

                                    @Override
                                    public void onError(ErrorInfo errorInfo) {
                                    }
                                });

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                currentMeetingInvite = null;
                currentTalkInvite = null;
            }
        });
        ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setMeetMessage(true, data.nMeetingID + "", data.strMeetingDomainCode).show();
    }

    public void onTalkInvite(AppBaseActivity activity, final CNotifyUserJoinTalkback data, final long millis) {

        if (data == null) {
            return;
        }

        if (data.isForceInvite()) {
            return;
        }

        if (((MCApp) ctx).getTopActivity().getLogicTimeDialog().isShowing()) {
            // 会议中不接受对讲
            HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                    .setTalkId(data.nTalkbackID)
                    .setTalkDomainCode(data.strTalkbackDomainCode), null);
            return;
        }
        currentTalkInvite = data;
        final CNotifyUserJoinTalkback temp = data;
        // 会议中来会议邀请，对话框提示
        String str = "";
        if (temp.getRequiredMediaMode() == SdkBaseParams.MediaMode.AudioAndVideo) {
            str = AppUtils.getString(R.string.video);
        } else {
            str = AppUtils.getString(R.string.talk);
        }
        ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setCancelable(false);
        ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setTitleText(AppUtils.getString(R.string.invisitor_title))
                .setMessageText(data.strFromUserName + AppUtils.getString(R.string.invisitor_you) + str + "，" + AppUtils.getString(R.string.qiehuandao) + str + AppUtils.getString(R.string.diaodu_shifou))
                .setCancelClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 会议中不接受对讲
                        AppMessages.get().del(millis);
                        HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                                .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                                .setTalkId(temp.nTalkbackID)
                                .setTalkDomainCode(temp.strTalkbackDomainCode), null);
                        CallRecordManage.get().updateCall(temp.nMsgSessionID);
                    }
                })
                .setConfirmClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppMessages.get().del(millis);
                        HYClient.getModule(ApiMeet.class)
                                .quitMeeting(SdkParamsCenter.Meet.QuitMeet()
                                        .setQuitMeetType(SdkBaseParams.QuitMeetType.Quit)
                                        .setMeetDomainCode(strMeetDomainCode)
                                        .setStopCapture(false)
                                        .setMeetID(nMeetID), new CallbackQuitMeet() {
                                    @Override
                                    public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                                        return true;
                                    }

                                    @Override
                                    public void onSuccess(Object o) {
                                        finishActivityAndView("onsuccess 1461");
                                        EventBus.getDefault().post(new AcceptDiaoDu(null, temp, null, millis));
                                    }

                                    @Override
                                    public void onError(ErrorInfo errorInfo) {
                                        showToast(AppUtils.getString(R.string.qiehuan_meet_video_false));
                                    }
                                });
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                currentMeetingInvite = null;
                currentTalkInvite = null;
            }
        });
        ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setMeetMessage(false, "", "").show();
    }

    public void setRation(int value) {
        texture_video.setRotation(value);
    }

    public boolean hasParent() {
        return appBaseActivity != null;
    }

    public void removeActivity() {
        appBaseActivity = null;
    }

    IChangeSize iChangeSize;

    public void setiChangeSize(IChangeSize iChangeSize) {
        this.iChangeSize = iChangeSize;
    }

    public interface IChangeSize {

        void changeSize();
    }
}
