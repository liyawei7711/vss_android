package huaiye.com.vvs.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huaiye.sdk.logger.Logger;
import com.ttyy.commonanno.Finder;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;

/**
 * author: admin
 * date: 2018/01/24
 * version: 0
 * mail: secret
 * desc: LogicDialog
 */
@BindLayout(R.layout.dialog_logic)
public class LogicDialog extends Dialog {

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

    public LogicDialog(@NonNull Context context) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable());
        getWindow().setGravity(Gravity.CENTER);

        mContentView = new FrameLayout(context);
        Injectors.get().inject(Finder.View, mContentView, this);
        //在安卓4.4上,硬件加速会耗费大量的内存,4.4的手机内存普遍不高.会造成绘制错误,所以在4.4以及以下停用硬件加速
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mContentView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        setContentView(mContentView);
    }

    public LogicDialog setTitleText(String text) {
        tv_title.setText(text);
        return this;
    }

    public LogicDialog setMessageText(String text) {
        tv_message.setText(text);
        return this;
    }

    public LogicDialog setCancelText(String text) {
        tv_cancel.setText(text);
        return this;
    }

    public LogicDialog setConfirmText(String text) {
        tv_confirm.setText(text);
        return this;
    }

    public LogicDialog setCancelClickListener(View.OnClickListener clickListener) {
        this.mCancelClickListener = clickListener;
        return this;
    }

    public LogicDialog setConfirmClickListener(View.OnClickListener clickListener) {
        this.mConfirmClickListener = clickListener;
        return this;
    }

    public LogicDialog setCancelButtonVisibility(int value) {
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

    public LogicDialog setConfirmButtonVisibility(int value) {
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

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            Logger.log("LogicDialog ERROR dismiss " + e.toString());
        }
        setCancelable(true);
        setCancelButtonVisibility(View.VISIBLE);
        setConfirmButtonVisibility(View.VISIBLE);
        setTitleText(AppUtils.getString(R.string.notice));
        setCancelText(AppUtils.getString(R.string.cancel));
        setConfirmText(AppUtils.getString(R.string.makesure));
        mCancelClickListener = null;
        mConfirmClickListener = null;
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            Logger.log("LogicDialog ERROR show " + e.toString());
        }
    }
}
