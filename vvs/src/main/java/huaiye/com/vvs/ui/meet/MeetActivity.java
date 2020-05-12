package huaiye.com.vvs.ui.meet;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.MeetInvistor;
import huaiye.com.vvs.bus.ShowChangeSizeView;
import huaiye.com.vvs.bus.TalkInvistor;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.dao.msgs.AppMessages;
import huaiye.com.vvs.dao.msgs.CaptureMessage;
import huaiye.com.vvs.models.auth.KickOutHandler;
import huaiye.com.vvs.models.auth.KickOutUIObserver;

/**
 * author: admin
 * date: 2018/06/12
 * version: 0
 * mail: secret
 * desc: MeetActivity
 */

@BindLayout(R.layout.activity_meet_view)
public class MeetActivity extends AppBaseActivity {
    @BindView(R.id.fl_root)
    FrameLayout fl_root;
    @BindExtra
    public boolean isMaster;


    @BindExtra
    public String strDomainCode;
    @BindExtra
    public int nMeetingID;
    @BindExtra
    public long millis;
    @BindExtra
    public SdkBaseParams.MediaMode nVoiceIntercom;

    public static boolean activityShow;
    KickOutUIObserver mKickoutObserver;


    public static void stratMeet(Context context, boolean isMaster, String strDomainCode, int nMeetingID, long millis, SdkBaseParams.MediaMode nVoiceIntercom) {
        Intent intent = new Intent(context, MeetActivity.class);
        intent.putExtra("isMaster", isMaster);
        intent.putExtra("strDomainCode", strDomainCode);
        intent.putExtra("nMeetingID", nMeetingID);
        intent.putExtra("millis", millis);
        intent.putExtra("nVoiceIntercom", nVoiceIntercom);
        context.startActivity(intent);
        activityShow = true;
    }

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        AppUtils.getMeet_view(this).setAppBaseActivity(this);
        fl_root.addView(AppUtils.getMeet_view(this));
        AppUtils.getMeet_view(this).setiChangeSize(new MeetViewLayoutNew.IChangeSize() {
            @Override
            public void changeSize() {
                fl_root.removeView(AppUtils.getMeet_view(MeetActivity.this));
                activityShow = false;
                finish();
            }
        });

        if (!AppUtils.isMeet) {
            AppMessages.get().del(millis);
            AppUtils.getMeet_view(this).startJoinMeet(isMaster, strDomainCode, nMeetingID, nVoiceIntercom);
        }

        mKickoutObserver = new KickOutUIObserver();
        mKickoutObserver.start(new KickOutHandler() {
            @Override
            public void onKickOut() {
                if (!AppUtils.isMeetViewNull()){
                    AppUtils.getMeet_view(getSelf()).quitMeet(false);
                }
            }
        });
    }

    public void onEvent(CaptureMessage bean) {
//        ChatUtil.get().rspGuanMo(bean.fromUserId, bean.fromUserDomain, bean.fromUserName);
    }

    @Override
    public void onTalkInvite(TalkInvistor data) {
        Logger.debug("MeetViewLayoutNew", " onTalkInvite ");
        if (data == null) {
            AppUtils.getMeet_view(this).closeMeet(null);
            return;
        }

        if (data.talk == null && data.p2p_talk == null) {
            AppUtils.getMeet_view(this).closeIfTalkDisconnect();
            return;
        }
        AppUtils.getMeet_view(this).onTalkInvite(this, data.talk, data.millis);
    }

    public void onMeetInvite(MeetInvistor data) {

        if (data == null) {
            AppUtils.getMeet_view(this).closeMeet(null);
            return;
        }

        if (data.meet == null) {
            AppUtils.getMeet_view(this).closeIfTalkDisconnect();
            return;
        }
        if (data.meet.nMeetingStatus != 1) {
            return;
        }

        if (data.meet.isSelfMeetCreator()) {
            return;
        }
        AppUtils.getMeet_view(this).onMeetInvite(this, data.meet, data.millis);
    }

    public void showHide() {
        EventBus.getDefault().post(new ShowChangeSizeView(true));
    }

    public void finishMeet(String str) {
        Logger.log("finishMeet " + str);
        activityShow = false;
        if (fl_root != null && !AppUtils.isMeetViewNull()) {
            fl_root.removeView(AppUtils.getMeet_view(this));
        }
        finish();
    }

    public void hideAll() {
        AppUtils.getMeet_view(this).hideAll();
    }

    @Override
    public void onBackPressed() {
        AppUtils.getMeet_view(this).onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AppUtils.getMeet_view(this).onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUtils.getMeet_view(this).onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.getMeet_view(this).onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mKickoutObserver.stop();
        activityShow = false;
        if (AppUtils.isMeetViewNull()){
            return;
        }
        AppUtils.getMeet_view(this).removeActivity();
        if (fl_root != null) {
            fl_root.removeView(AppUtils.getMeet_view(this));
        }
    }
}
