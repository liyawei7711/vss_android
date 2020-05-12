package huaiye.com.vvs.common.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;

/**
 * author: admin
 * date: 2018/02/23
 * version: 0
 * mail: secret
 * desc: AlertPopupWindow
 */

public class UserListPopupWindow extends PopupWindow implements View.OnClickListener {

    ConfirmClickListener confirmClickListener;
    Context context;
    TextView tv_name_shengxu;
    TextView tv_name_jiangxu;
    TextView tv_online;
    ImageView iv_online_status;

    boolean name_shengxu = true;
    boolean name_jiangxu;
    boolean online = true;

    public UserListPopupWindow(Context context) {
        super(context);
        this.context = context;
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(AppUtils.getSize(190));

        Drawable drawable = new ColorDrawable(Color.parseColor("#00000000"));
        setBackgroundDrawable(drawable);// 点击外部消失
        setOutsideTouchable(true); // 点击外部消失
        setFocusable(true); // 点击back键消失

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });

        View contentView = LayoutInflater.from(context).inflate(R.layout.popu_user_list, null);
        setContentView(contentView);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tv_name_shengxu = contentView.findViewById(R.id.tv_name_shengxu);
        tv_name_jiangxu = contentView.findViewById(R.id.tv_name_jiangxu);
        tv_online = contentView.findViewById(R.id.tv_online);
        iv_online_status = contentView.findViewById(R.id.iv_online_status);

        tv_name_shengxu.setOnClickListener(this);
        tv_name_jiangxu.setOnClickListener(this);
        tv_online.setOnClickListener(this);
        iv_online_status.setOnClickListener(this);
    }

    public void setConfirmClickListener(ConfirmClickListener listener) {
        this.confirmClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (confirmClickListener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_name_shengxu:
                if (name_shengxu) return;

                name_shengxu = !name_shengxu;
                showColor(name_shengxu, tv_name_shengxu);
                name_jiangxu = !name_shengxu;
                showColor(name_jiangxu, tv_name_jiangxu);
                confirmClickListener.onClickXingShiShengXu(name_shengxu);
                break;
            case R.id.tv_name_jiangxu:
                if (name_jiangxu) return;

                name_jiangxu = !name_jiangxu;
                showColor(name_jiangxu, tv_name_jiangxu);
                name_shengxu = !name_jiangxu;
                showColor(name_shengxu, tv_name_shengxu);
                confirmClickListener.onClickXingShiJiangXu(name_jiangxu);
                break;
            case R.id.tv_online:
            case R.id.iv_online_status:
                online = !online;
                showColor(online, tv_online);
                if (online) {
                    iv_online_status.setImageResource(R.drawable.ic_choice_checked);
                } else {
                    iv_online_status.setImageResource(R.drawable.ic_choice);
                }
                confirmClickListener.onClickOnLine(online);
                break;

        }
        dismiss();
    }

    public void showColor(boolean isSelected, TextView view) {
        if (isSelected) {
            view.setTextColor(Color.parseColor("#3f74fd"));
        } else {
            view.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    public void showView(View view) {
        if (Build.VERSION.SDK_INT < 24) {
            this.showAsDropDown(view);
        } else {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int y = location[1];
            this.showAtLocation(view,
                    Gravity.NO_GRAVITY, location[0],
                    y + view.getHeight());
        }

        showColor(name_shengxu, tv_name_shengxu);
        showColor(online, tv_online);
        if (online) {
            iv_online_status.setImageResource(R.drawable.ic_choice_checked);
        } else {
            iv_online_status.setImageResource(R.drawable.ic_choice);
        }
    }


    public interface ConfirmClickListener {

        void onClickXingShiShengXu(boolean choose);

        void onClickXingShiJiangXu(boolean choose);

        void onClickOnLine(boolean choose);
    }

}
