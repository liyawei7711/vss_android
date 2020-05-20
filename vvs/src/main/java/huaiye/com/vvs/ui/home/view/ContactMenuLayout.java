package huaiye.com.vvs.ui.home.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import huaiye.com.vvs.R;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: ActionBarLayout
 */

public class ContactMenuLayout extends FrameLayout implements View.OnClickListener {

    TextView tv_phone;
    TextView tv_video;
    TextView tv_channel;
    TextView tv_meet;
    TextView tv_watch;
    TextView tv_zhihui;

    public ContactMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ContactMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);

        View view = LayoutInflater.from(context).inflate(R.layout.contact_menu_layout, this);

        tv_phone = view.findViewById(R.id.tv_phone);
        tv_video = view.findViewById(R.id.tv_video);
        tv_channel = view.findViewById(R.id.tv_channel);
        tv_meet = view.findViewById(R.id.tv_meet);
        tv_watch = view.findViewById(R.id.tv_watch);
        tv_zhihui = view.findViewById(R.id.tv_zhihui);

        tv_phone.setOnClickListener(this);
        tv_video.setOnClickListener(this);
        tv_channel.setOnClickListener(this);
        tv_meet.setOnClickListener(this);
        tv_watch.setOnClickListener(this);
        tv_zhihui.setOnClickListener(this);
        allNull();
    }

    public void SingleUserCanWatch() {
        tv_phone.setVisibility(VISIBLE);
        tv_video.setVisibility(VISIBLE);
        tv_meet.setVisibility(VISIBLE);
        tv_watch.setVisibility(VISIBLE);
        tv_zhihui.setVisibility(VISIBLE);
        tv_channel.setVisibility(GONE);
        setVisibility(VISIBLE);
    }
    public void SingleUserNotWatch() {
        tv_phone.setVisibility(VISIBLE);
        tv_video.setVisibility(VISIBLE);
        tv_meet.setVisibility(VISIBLE);
        tv_watch.setVisibility(GONE);
        tv_zhihui.setVisibility(VISIBLE);
        tv_channel.setVisibility(GONE);
        setVisibility(VISIBLE);
    }


    public void P2PUserController() {
        tv_phone.setVisibility(GONE);
        tv_video.setVisibility(VISIBLE);
        tv_meet.setVisibility(GONE);
        tv_watch.setVisibility(VISIBLE);
        tv_zhihui.setVisibility(GONE);
        tv_channel.setVisibility(GONE);
        setVisibility(VISIBLE);
    }

    public void MuliteUser() {
        tv_phone.setVisibility(GONE);
        tv_video.setVisibility(GONE);
        tv_meet.setVisibility(VISIBLE);
        tv_watch.setVisibility(GONE);
        tv_zhihui.setVisibility(VISIBLE);
        tv_channel.setVisibility(GONE);
        setVisibility(VISIBLE);
    }

    public void SingleGroup() {
        tv_phone.setVisibility(GONE);
        tv_video.setVisibility(GONE);
        tv_meet.setVisibility(VISIBLE);
        tv_watch.setVisibility(GONE);
        tv_zhihui.setVisibility(VISIBLE);
        tv_channel.setVisibility(VISIBLE);
        setVisibility(VISIBLE);
    }

    public void allNull() {
        tv_phone.setVisibility(GONE);
        tv_video.setVisibility(GONE);
        tv_meet.setVisibility(GONE);
        tv_watch.setVisibility(GONE);
        tv_zhihui.setVisibility(GONE);
        tv_channel.setVisibility(GONE);
        setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_phone:
                listener.onPhoneClick();
                break;
            case R.id.tv_video:
                listener.onVideoClick();
                break;
            case R.id.tv_channel:
                listener.onChannelClick();
                break;
            case R.id.tv_meet:
                listener.onMeetClick();
                break;
            case R.id.tv_watch:
                listener.onWatchClick();
                break;
            case R.id.tv_zhihui:
                listener.onZhiHuiClick();
                break;
        }
    }

    OnMentClickListener listener;

    public void setListener(OnMentClickListener listener) {
        this.listener = listener;
    }

    public interface OnMentClickListener {
        void onPhoneClick();

        void onVideoClick();

        void onChannelClick();

        void onMeetClick();

        void onWatchClick();

        void onZhiHuiClick();
    }

}
