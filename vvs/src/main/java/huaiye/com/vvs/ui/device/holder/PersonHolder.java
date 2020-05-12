package huaiye.com.vvs.ui.device.holder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DomainHolder
 */

public class PersonHolder extends LiteViewHolder {

    public static boolean selected_mode = false;

    @BindView(R.id.iv_header)
    ImageView iv_header;
    @BindView(R.id.iv_online_status)
    ImageView iv_online_status;
    @BindView(R.id.view_status)
    View view_status;
    @BindView(R.id.fl_status)
    View fl_status;
    @BindView(R.id.cb_status)
    CheckBox cb_status;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.tv_user_id)
    TextView tv_user_id;
    @BindView(R.id.view_divider)
    View view_divider;

    public PersonHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
        view_status.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        PersonModelBean bean = (PersonModelBean) data;
        itemView.setTag(bean);
        view_status.setTag(bean);
        tv_user_name.setText(bean.strUserName);
        tv_user_id.setText("ID: " + bean.strUserID);

        if (selected_mode) {
            fl_status.setVisibility(View.VISIBLE);
        } else {
            fl_status.setVisibility(View.GONE);
        }

        if (bean.nStatus == PersonModelBean.STATUS_OFFLINE) {
            tv_status.setText(AppUtils.getString(R.string.offline_1));
            iv_online_status.setImageResource(R.drawable.dian_lixian);
        } else if (bean.nStatus == PersonModelBean.STATUS_ONLINE_IDLE) {
            tv_status.setText(AppUtils.getString(R.string.empty_kongxian_1));
            iv_online_status.setImageResource(R.drawable.dian_zaixian);
        } else if (bean.nStatus == PersonModelBean.STATUS_ONLINE_CAPTURING) {
            tv_status.setText(AppUtils.getString(R.string.capture_ing_1));
            iv_online_status.setImageResource(R.drawable.dian_mang);
        } else if (bean.nStatus == PersonModelBean.STATUS_ONLINE_TALKING) {
            tv_status.setText(AppUtils.getString(R.string.talk_ing_1));
            iv_online_status.setImageResource(R.drawable.dian_mang);
        } else if (bean.nStatus == PersonModelBean.STATUS_ONLINE_MEETING) {
            tv_status.setText(AppUtils.getString(R.string.meet_ing_1));
            iv_online_status.setImageResource(R.drawable.dian_mang);
        } else if (bean.nStatus == PersonModelBean.STATUS_ONLINE_TRUNK_SPEAKING) {
            tv_status.setText(AppUtils.getString(R.string.trunk_speak_ing_1));
            iv_online_status.setImageResource(R.drawable.dian_mang);
        }else {
            tv_status.setText(AppUtils.getString(R.string.offline_1));
            iv_online_status.setImageResource(R.drawable.dian_lixian);
        }

        Map<String, PersonModelBean> selectedAll = (Map<String, PersonModelBean>) extr;
        cb_status.setChecked(selectedAll.containsKey(bean.strUserID));

        if (position == datas.size() - 1) {
            view_divider.setVisibility(View.GONE);
        } else {
            view_divider.setVisibility(View.VISIBLE);
        }

    }
}
