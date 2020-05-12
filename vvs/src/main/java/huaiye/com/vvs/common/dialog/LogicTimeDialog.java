package huaiye.com.vvs.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserCancelJoinMeeting;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackStatusInfo;
import com.ttyy.commonanno.Finder;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.rx.RxUtils;

/**
 * author: admin
 * date: 2018/01/24
 * version: 0
 * mail: secret
 * desc: LogicDialog
 */
@BindLayout(R.layout.dialog_logic)
public class LogicTimeDialog extends Dialog {

    FrameLayout mContentView;

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_message)
    TextView tv_message;
    @BindView(R.id.tv_confirm)
    TextView tv_confirm;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;

    View.OnClickListener mCancelClickListener;
    View.OnClickListener mConfirmClickListener;

    boolean isMeet;
    String meetId = "";
    String meetDomain = "";


    public LogicTimeDialog(@NonNull Context context) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable());
        getWindow().setGravity(Gravity.CENTER);

        mContentView = new FrameLayout(context);
        Injectors.get().inject(Finder.View, mContentView, this);

        setContentView(mContentView);

        rxUtils = new RxUtils();
    }

    public LogicTimeDialog setTitleText(String text) {
        tv_title.setText(text);
        return this;
    }

    public LogicTimeDialog setMessageText(String text) {
        tv_message.setText(text);
        return this;
    }

    public LogicTimeDialog setCancelText(String text) {
        tv_cancel.setText(text);
        return this;
    }

    public LogicTimeDialog setConfirmText(String text) {
        tv_confirm.setText(text);
        return this;
    }

    public LogicTimeDialog setCancelClickListener(View.OnClickListener clickListener) {
        this.mCancelClickListener = clickListener;
        return this;
    }

    public LogicTimeDialog setConfirmClickListener(View.OnClickListener clickListener) {
        this.mConfirmClickListener = clickListener;
        return this;
    }

    public LogicTimeDialog setCancelButtonVisibility(int value) {
        if (value == View.VISIBLE) {
            tv_cancel.setVisibility(View.VISIBLE);
            if (tv_confirm.getVisibility() == View.VISIBLE) {
                divider.setVisibility(View.VISIBLE);
            }
        } else {
            tv_cancel.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        return this;
    }

    public LogicTimeDialog setConfirmButtonVisibility(int value) {
        if (value == View.VISIBLE) {
            tv_confirm.setVisibility(View.VISIBLE);
            if (tv_cancel.getVisibility() == View.VISIBLE) {
                divider.setVisibility(View.VISIBLE);
            }
        } else {
            tv_confirm.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

        return this;
    }

    @OnClick({R.id.tv_cancel, R.id.tv_confirm})
    void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                if (mCancelClickListener != null) {
                    mCancelClickListener.onClick(v);
                }
                break;
            case R.id.tv_confirm:
                if (mConfirmClickListener != null) {
                    mConfirmClickListener.onClick(v);
                }
                break;
        }

        dismiss();
    }

    RxUtils rxUtils;

    @Override
    public void show() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        try {
            super.show();
        } catch (Exception e) {
            Logger.log("LogicTimeDialog ERROR show " + e.toString());
        }

        rxUtils.doDelay(29 * 1000, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                dismiss();
            }
        }, "111");

    }

    /**
     * 对方取消邀请的话,提示对话框要隐藏
     * @param cNotifyMeetingStatusInfo
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyInviteUserCancelJoinMeeting cNotifyMeetingStatusInfo) {
        if (isMeet && meetId.equals(cNotifyMeetingStatusInfo.nMeetingID + "") && isShowing()) {
            dismiss();
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo) {
        if (isShowing() && cNotifyTalkbackStatusInfo.isTalkingStopped()){
            dismiss();
        }
    }



    @Override
    public void dismiss() {
        EventBus.getDefault().unregister(this);
        try {
            super.dismiss();
        } catch (Exception e) {
            Logger.log("LogicTimeDialog ERROR dismiss " + e.toString());
        }

        if (rxUtils != null) {
            rxUtils.clearTag("111");
        }

        setCancelable(true);
        setCancelButtonVisibility(View.VISIBLE);
        setConfirmButtonVisibility(View.VISIBLE);
        setTitleText(AppUtils.getString(R.string.notice));
        setCancelText(AppUtils.getString(R.string.cancel));
        setConfirmText(AppUtils.getString(R.string.makesure));
        mConfirmClickListener = null;
        mCancelClickListener = null;

        this.isMeet = false;
        this.meetId = "";
        this.meetDomain = "";

    }

    public LogicTimeDialog setMeetMessage(boolean isMeet, String id, String domain) {
        this.isMeet = isMeet;
        this.meetId = id;
        this.meetDomain = domain;
        return this;
    }
}
