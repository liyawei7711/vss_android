package huaiye.com.vvs.ui.home;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.cmf.JniIntf;

import java.util.concurrent.TimeUnit;

import huaiye.com.vvs.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class SpeakVoiceView extends FrameLayout {
    private final int MSG_TICK = 1;
    private TextView mTvCountDown;
    private ImageView mIvSpeakVoice;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_TICK) {
                update();
                handler.sendEmptyMessageDelayed(MSG_TICK, 100);
            }
        }
    };

    public SpeakVoiceView(@NonNull Context context) {
        super(context);
    }

    public SpeakVoiceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SpeakVoiceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        View viewRoot = LayoutInflater.from(context).inflate(R.layout.layout_home_speak_voice, this, true);
        mTvCountDown = viewRoot.findViewById(R.id.tv_count_down);
        mIvSpeakVoice = viewRoot.findViewById(R.id.iv_speak_voice);
    }


    public void update() {
        long speakAmplitude = JniIntf.GetSystemProperty(JniIntf.SYSTEM_PROPERTY_ENABLE_RESAMPLE);
//        Logger.debug("feifeifei", "SpeakVoiceView " + speakAmplitude);
        mIvSpeakVoice.setVisibility(VISIBLE);
        mTvCountDown.setVisibility(View.GONE);
        //todo
        int size = (int) (speakAmplitude / 20) + 1;
        switch (size) {
            case 1:
                mIvSpeakVoice.setImageResource(R.drawable.yinliang1);
                break;
            case 2:
                mIvSpeakVoice.setImageResource(R.drawable.yinliang2);
                break;
            case 3:
                mIvSpeakVoice.setImageResource(R.drawable.yinliang3);
                break;
            case 4:
                mIvSpeakVoice.setImageResource(R.drawable.yinliang4);
                break;
            case 5:
            case 6:
                mIvSpeakVoice.setImageResource(R.drawable.yinliang5);
                break;
        }

    }

    public void speakStart() {
        handler.sendEmptyMessage(MSG_TICK);
        setVisibility(View.VISIBLE);
    }

    public void willCountDown() {
        handler.removeMessages(MSG_TICK);
        mIvSpeakVoice.setVisibility(View.GONE);
        mTvCountDown.setVisibility(View.VISIBLE);
        final int totalCount = 4;
        mTvCountDown.setText(String.valueOf(totalCount + 1));
        io.reactivex.Observable.interval(1, TimeUnit.SECONDS)
                .take(totalCount)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return totalCount - aLong;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mTvCountDown.setText(String.valueOf(aLong));
                    }
                });
    }


    public void speakEnd() {
        handler.removeMessages(MSG_TICK);
        this.mIvSpeakVoice.setVisibility(View.GONE);
        this.mTvCountDown.setVisibility(View.GONE);
        this.setVisibility(View.GONE);
    }


}
