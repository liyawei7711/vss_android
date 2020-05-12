package huaiye.com.vvs.common.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.OnLongClick;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;

/**
 * author: admin
 * date: 2017/09/07
 * version: 0
 * mail: secret
 * desc: NavigateView
 */
@BindLayout(R.layout.view_navigator)
public class NavigateView extends LinearLayout {

    @BindView(R.id.navigate_container)
    View navigate_container;

    @BindView(R.id.tv_con_status)
    TextView tv_con_status;

    @BindView(R.id.view_left)
    View view_left;
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.tv_left)
    TextView tv_left;
    OnClickListener mLeftClickListener;

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.view_load)
    View view_load;

    @BindView(R.id.view_right)
    View view_right;
    @BindView(R.id.iv_right)
    ImageView iv_right;
    @BindView(R.id.tv_right)
    TextView tv_right;

    @BindView(R.id.view_right2)
    View view_right2;
    @BindView(R.id.iv_right2)
    ImageView iv_right2;
    @BindView(R.id.tv_right2)
    TextView tv_right2;

    OnClickListener mRightClickListener;
    OnClickListener mRightClickListener2;
    OnLongClickListener mRightLongClickListener;

    public NavigateView(Context context) {
        this(context, null);
    }

    public NavigateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Injectors.get().injectView(this);
    }

    @OnClick({R.id.view_left, R.id.view_right, R.id.view_right2})
    void onViewClicked(View v) {

        switch (v.getId()) {
            case R.id.view_left:
                if (mLeftClickListener != null) {
                    mLeftClickListener.onClick(v);
                }
                break;
            case R.id.view_right:
                if (mRightClickListener != null) {
                    mRightClickListener.onClick(v);
                }
                break;
            case R.id.view_right2:
                if (mRightClickListener2 != null) {
                    mRightClickListener2.onClick(v);
                }
                break;
        }
    }

    @OnLongClick(R.id.view_right)
    void onViewLongClicked(View v) {
        switch (v.getId()) {
            case R.id.view_right:

                if (mRightLongClickListener != null) {
                    mRightLongClickListener.onLongClick(v);
                }

                break;
        }
    }

    public NavigateView showLoading() {
        view_load.setVisibility(View.VISIBLE);
        return this;
    }

    public NavigateView dismissLoading() {
        view_load.setVisibility(View.GONE);
        return this;
    }

    public NavigateView setTitlText(String text) {
        tv_title.setText(text);
        return this;
    }

    public NavigateView setTitleColor(int color) {
        tv_title.setTextColor(color);
        return this;
    }

    public NavigateView setTitleColor(ColorStateList color) {
        tv_title.setTextColor(color);
        return this;
    }

    public NavigateView setLeftIcon(int id) {
        view_left.setVisibility(View.VISIBLE);
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setImageDrawable(AppUtils.getResourceDrawable(id));
        return this;
    }

    public NavigateView setLeftTitle(String text) {
        view_left.setVisibility(View.VISIBLE);
        tv_left.setText(text);
        tv_left.setVisibility(VISIBLE);
        iv_left.setVisibility(GONE);
        return this;
    }

    public NavigateView setRightIcon(int id) {
        view_right.setVisibility(View.VISIBLE);
        iv_right.setVisibility(View.VISIBLE);
        iv_right.setImageDrawable(AppUtils.getResourceDrawable(id));
        return this;
    }

    public NavigateView setRight2Icon(int id) {
        view_right2.setVisibility(View.VISIBLE);
        iv_right2.setVisibility(View.VISIBLE);
        iv_right2.setImageDrawable(AppUtils.getResourceDrawable(id));
        return this;
    }

    public NavigateView setRightText(String text) {
        view_right.setVisibility(View.VISIBLE);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText(text);
        return this;
    }

    public NavigateView setRight2Text(String text) {
        view_right2.setVisibility(View.VISIBLE);
        tv_right2.setVisibility(View.VISIBLE);
        tv_right2.setText(text);
        return this;
    }

    public NavigateView setRightTextColor(int color) {
        tv_right.setTextColor(color);
        return this;
    }

    public NavigateView setRight2TextColor(int color) {
        tv_right2.setTextColor(color);
        return this;
    }

    public NavigateView hideLeftIcon() {
        view_left.setVisibility(View.GONE);
        return this;
    }

    public NavigateView hideRightIcon() {
        view_right.setVisibility(View.GONE);
        return this;
    }

    public NavigateView hideRight2Icon() {
        view_right2.setVisibility(View.GONE);
        return this;
    }

    public NavigateView setContentColor(int color) {
        navigate_container.setBackgroundColor(color);
        return this;
    }

    public NavigateView setLeftClickListener(OnClickListener listener) {
        mLeftClickListener = listener;
        return this;
    }

    public NavigateView setRightClickListener(OnClickListener listener) {
        mRightClickListener = listener;
        return this;
    }

    public NavigateView setRight2ClickListener(OnClickListener listener) {
        mRightClickListener2 = listener;
        return this;
    }

    public NavigateView setRightLongClickListener(OnLongClickListener listener) {
        mRightLongClickListener = listener;
        return this;
    }

    public void setConnStatus(SdkBaseParams.ConnectionStatus status) {
        switch (status) {
            case Connected:
                tv_con_status.setVisibility(View.VISIBLE);
                tv_con_status.setText(AppUtils.getString(R.string.has_connected));
                tv_con_status.setTextColor(AppUtils.getResourceColor(R.color.green));
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_con_status.setVisibility(View.GONE);
                    }
                }, 2200);
                break;
            case Connecting:
                tv_con_status.setVisibility(View.VISIBLE);
                tv_con_status.setText(AppUtils.getString(R.string.has_connecting));
                tv_con_status.setTextColor(AppUtils.getResourceColor(R.color.purple_connecting));
                break;
            case Disconnected:
                tv_con_status.setVisibility(View.VISIBLE);
                tv_con_status.setText(AppUtils.getString(R.string.has_connected_false));
                tv_con_status.setTextColor(AppUtils.getResourceColor(R.color.red_disconnect));
                break;
        }
    }

    public void setRightEnable(boolean b) {
        view_right.setEnabled(b);
    }

    public View getRightView() {
        return view_right;
    }


}
