package huaiye.com.vvs.ui.meet.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMsgCaptureQualityNotify;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.util.concurrent.TimeUnit;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.rx.CommonSubscriber;
import huaiye.com.vvs.common.views.WindowManagerUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: MeetMediaMenuView
 */
@BindLayout(R.layout.view_meet_media_header)
public class MeetMediaMenuTopView extends RelativeLayout {

    @BindView(R.id.ll_left)
    View ll_left;
    @BindView(R.id.menu_iv_voice)
    ImageView menu_iv_voice;
    @BindView(R.id.tv_meet_name)
    TextView tv_meet_name;
    @BindView(R.id.tv_meet_time)
    TextView tv_meet_time;
    @BindView(R.id.tv_inhao)
    TextView tv_inhao;
    @BindView(R.id.record_status)
    View record_status;

    private static Disposable mDisposable;
    CommonSubscriber subscriber;

    public MeetMediaMenuTopView(Context context) {
        this(context, null);
    }

    public MeetMediaMenuTopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeetMediaMenuTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Injectors.get().injectView(this);

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

                tv_meet_time.setText(strH + ":" + strM + ":" + strS);

                WindowManagerUtils.showTime(strH + ":" + strM + ":" + strS);
            }
        };
    }

    /**
     * 改变质量
     *
     * @param msg
     */
    public void changeQuality(SdpMsgCaptureQualityNotify msg) {
        int max = AppUtils.getCapturePresetOptionMax();
        float pre = ((float) msg.m_nCurQuality + 1) / (max + 1);
        try {
            tv_inhao.setText(AppUtils.getCaptureZhiLiangTxt(pre));
            menu_iv_voice.setImageResource(AppUtils.getCaptureZhiLiangImg(pre));
        } catch (Exception e) {

        }
    }

    /**
     * 计时开始
     */
    public void startTime() {
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 展示名称
     *
     * @param str
     */
    public void showName(String str) {
        tv_meet_name.setText(str);
    }

    public void showLeft(boolean value) {
        ll_left.setVisibility(value ? INVISIBLE : VISIBLE);
    }

    public void isRecord(boolean value) {
        record_status.setVisibility(value ? VISIBLE : INVISIBLE);
    }

    public void onDestory() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        if (mDisposable != null) {
            mDisposable = null;
        }
    }

}
