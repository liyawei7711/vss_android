package huaiye.com.vvs.ui.talk;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import com.huaiye.cmf.sdp.SdpMsgCommonUDPMsg;
import com.huaiye.cmf.sdp.SdpMsgFindLanCaptureDeviceRsp;
import com.huaiye.samples.p2p.P2PSample;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdpmsgs.talk.CStartTalkbackReq;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.MeetInvistor;
import huaiye.com.vvs.bus.TalkInvistor;
import huaiye.com.vvs.common.AlarmMediaPlayer;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.dao.msgs.CaptureMessage;
import huaiye.com.vvs.models.auth.KickOutHandler;
import huaiye.com.vvs.models.auth.KickOutUIObserver;
import huaiye.com.vvs.models.p2p.SdpMsgCommonUDPMsgWrap;
import huaiye.com.vvs.models.p2p.SdpMsgFindLanCaptureDeviceRspWrap;
import huaiye.com.vvs.ui.home.view.TalkVideoViewLayout;

/**
 * author: admin
 * date: 2018/06/12
 * version: 0
 * mail: secret
 * desc: MeetActivity
 */

@BindLayout(R.layout.activity_meet_view)
public class TalkVideoActivity extends AppBaseActivity {
    @BindView(R.id.fl_root)
    FrameLayout fl_root;

    @BindExtra
    public boolean isCreate;
    @BindExtra
    public String strToUserDomainCode;
    @BindExtra
    public String strToUserID;
    @BindExtra
    public String strToUserName;
    @BindExtra
    public String strTalkbackDomainCode;
    @BindExtra
    public int nTalkbackID;
    @BindExtra
    public String strExtParam;
    @BindExtra
    public SdpMsgFindLanCaptureDeviceRspWrap deviceWrap;
    @BindExtra
    public SdpMsgCommonUDPMsgWrap msgWrap;

    SdpMsgFindLanCaptureDeviceRsp device;
    SdpMsgCommonUDPMsg msg;

    AlarmMediaPlayer mMediaPlayer;
    public P2PSample mP2PSample;

    public static boolean activityShow;

    KickOutUIObserver mKickoutObserver;

    public static void createTalk(Context context, String strToUserDomainCode, String strToUserID, String strToUserName, String strExtParam, SdpMsgFindLanCaptureDeviceRsp device) {
        Intent intent = new Intent(context, TalkVideoActivity.class);
        intent.putExtra("isCreate", true);
        intent.putExtra("strToUserDomainCode", strToUserDomainCode);
        intent.putExtra("strToUserID", strToUserID);
        intent.putExtra("strToUserName", strToUserName);
        intent.putExtra("strExtParam", strExtParam);
        if (device != null) {
            intent.putExtra("deviceWrap", new SdpMsgFindLanCaptureDeviceRspWrap(device));
        }
        context.startActivity(intent);
        activityShow = true;
    }

    public static void joinTalk(Context context, String strTalkbackDomainCode, int nTalkbackID, SdpMsgCommonUDPMsg msg) {
        Intent intent = new Intent(context, TalkVideoActivity.class);
        intent.putExtra("isCreate", false);
        intent.putExtra("strTalkbackDomainCode", strTalkbackDomainCode);
        intent.putExtra("nTalkbackID", nTalkbackID);
        if (msg != null) {
            intent.putExtra("msgWrap", new SdpMsgCommonUDPMsgWrap(msg));
        }
        context.startActivity(intent);
        activityShow = true;
    }

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);

        mP2PSample = HYClient.getSdkSamples().P2P();
    }

    @Override
    public void doInitDelay() {
        if (deviceWrap != null) {
            device = deviceWrap.convert();
        }

        if (msgWrap != null) {
            msg = msgWrap.convert();
        }
        mKickoutObserver = new KickOutUIObserver();
        mKickoutObserver.start(new KickOutHandler() {
            @Override
            public void onKickOut() {
                if (!AppUtils.isVideoViewNull()) {
                    AppUtils.getTvvl_view(getSelf()).createError("talkVideoActivity kick out");
                }
            }
        });
        AppUtils.getTvvl_view(this).setAppBaseActivity(this);
        fl_root.addView(AppUtils.getTvvl_view(this));
        AppUtils.getTvvl_view(this).setiChangeSize(new TalkVideoViewLayout.IChangeSize() {
            @Override
            public void changeSize() {
                AppUtils.getTvvl_view(TalkVideoActivity.this).removeActivity();
                fl_root.removeView(AppUtils.getTvvl_view(TalkVideoActivity.this));
                activityShow = false;
                finish();
            }

            @Override
            public void removeAll() {
                AppUtils.getTvvl_view(TalkVideoActivity.this).removeActivity();
                fl_root.removeView(AppUtils.getTvvl_view(TalkVideoActivity.this));
                activityShow = false;
                finish();
            }
        });

        if (!AppUtils.isVideo) {
            if (isCreate) {
                mMediaPlayer = AlarmMediaPlayer.get();
                CStartTalkbackReq.ToUser toUser = new CStartTalkbackReq.ToUser();
                toUser.strToUserDomainCode = strToUserDomainCode;
                toUser.strToUserID = strToUserID;
                toUser.strToUserName = strToUserName;
                AppUtils.getTvvl_view(this).createTalk(toUser, strExtParam, device);
            } else {
                AppUtils.getTvvl_view(this).joinTalk(strTalkbackDomainCode, nTalkbackID, msg);
            }
        }

    }

    public void onEvent(CaptureMessage bean) {
//        ChatUtil.get().rspGuanMo(bean.fromUserId, bean.fromUserDomain, bean.fromUserName);
    }

    @Override
    public void onTalkInvite(TalkInvistor data) {
        if (data == null) {
            AppUtils.getTvvl_view(this).closeTalkVideo(null);
            return;
        }
        if (data.talk == null && data.p2p_talk == null) {
            Logger.debug("TalkVideoActivity data.talk  null");
            AppUtils.getTvvl_view(this).closeIfTalkDisconnect();
            return;
        }
        AppUtils.getTvvl_view(this).onTalkInvite(this, data.talk, null, data.millis);
    }

    @Override
    public void onMeetInvite(MeetInvistor data) {

        if (data == null) {
            AppUtils.getTvvl_view(this).closeTalkVideo(null);
            return;
        }

        if (data.meet == null) {
            Logger.debug("TalkVideoActivity data.talk  null");
            AppUtils.getTvvl_view(this).closeIfTalkDisconnect();
            return;
        }
        if (data.meet.nMeetingStatus != 1) {
            return;
        }

        if (data.meet.isSelfMeetCreator()) {
            return;
        }
//        AppUtils.getTvvl_view().onMeetInvite(this, data.meet, data.millis);
    }

    @Override
    public void onBackPressed() {
        AppUtils.getTvvl_view(this).onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!AppUtils.isVideoViewNull()) {
            AppUtils.getTvvl_view(this).onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AppUtils.isVideoViewNull()) {
            AppUtils.getTvvl_view(this).onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityShow = false;
        mKickoutObserver.stop();
        if (!AppUtils.isVideoViewNull()) {
            AppUtils.getTvvl_view(TalkVideoActivity.this).removeActivity();
            fl_root.removeView(AppUtils.getTvvl_view(TalkVideoActivity.this));
        }

        if (HYClient.getSdkSamples().P2P().isBeingWatched() ||
                HYClient.getSdkSamples().P2P().isTalking()) {
            HYClient.getSdkSamples().P2P().stopAll();
        }

    }
}
