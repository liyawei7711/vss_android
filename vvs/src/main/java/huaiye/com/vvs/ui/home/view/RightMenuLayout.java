package huaiye.com.vvs.ui.home.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import huaiye.com.vvs.R;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: ActionBarLayout
 */

public class RightMenuLayout extends FrameLayout implements View.OnClickListener {

    View tv_preview;
    public ImageView tv_toggle;

    public RightMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RightMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);

        View view = LayoutInflater.from(context).inflate(R.layout.main_right_menu_layout, null);

        tv_preview = view.findViewById(R.id.tv_preview);
        tv_toggle = view.findViewById(R.id.tv_toggle);

        addView(view);

        tv_preview.setOnClickListener(this);
        tv_toggle.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_preview:
                listener.onPreviewClick();
                break;
            case R.id.tv_toggle:
                listener.onToggleClick();
                break;
        }
    }

    public CaptureViewLayout.ICaptureStateChangeListener getStateChangeListener() {
        return new CaptureViewLayout.ICaptureStateChangeListener() {
            @Override
            public void onOpen() {
                tv_preview.setVisibility(VISIBLE);
                tv_toggle.setImageResource(R.drawable.selector_shipincaiji_open_btn);
            }

            @Override
            public void onClose() {
                tv_preview.setVisibility(GONE);
                tv_toggle.setImageResource(R.drawable.selector_shipincaiji_btn);
            }

            @Override
            public void hide() {

            }

            @Override
            public void show() {

            }

        };
    }

    OnRightClickListener listener;

    public void setListener(OnRightClickListener listener) {
        this.listener = listener;
    }

    public interface OnRightClickListener {
        void onPreviewClick();

        boolean onToggleClick();
    }

}
