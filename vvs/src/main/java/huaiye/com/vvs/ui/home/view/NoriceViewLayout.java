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

public class NoriceViewLayout extends FrameLayout implements View.OnClickListener {

    TextView tv_preview;
    TextView tv_toggle;

    public NoriceViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NoriceViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
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

    OnRightClickListener listener;

    public void setListener(OnRightClickListener listener) {
        this.listener = listener;
    }

    public interface OnRightClickListener {
        void onPreviewClick();

        void onToggleClick();
    }

}
