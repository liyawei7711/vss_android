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
import huaiye.com.vvs.map.baidu.appcluster.MyCluster;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.p2p.SdpMsgFindLanCaptureDeviceRspWrap;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DomainHolder
 */

public class P2PPersonHolder extends LiteViewHolder {

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

    public P2PPersonHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
        view_status.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        SdpMsgFindLanCaptureDeviceRspWrap bean = (SdpMsgFindLanCaptureDeviceRspWrap) data;
        itemView.setTag(bean);
        view_status.setTag(bean);
        tv_user_name.setText(bean.m_strName);
        tv_user_id.setText("ID: " + bean.m_strIP);

        if (selected_mode) {
            fl_status.setVisibility(View.VISIBLE);
        } else {
            fl_status.setVisibility(View.GONE);
        }
        view_status.setVisibility(View.GONE);
        if (bean.m_nCaptureState == 0) {
            tv_status.setText(AppUtils.getString(R.string.empty_kongxian_1));
            iv_online_status.setImageResource(R.drawable.dian_zaixian);
        } else {
            tv_status.setText(AppUtils.getString(R.string.capture_ing_1));
            iv_online_status.setImageResource(R.drawable.dian_mang);
        }

        if (position == datas.size() - 1) {
            view_divider.setVisibility(View.GONE);
        } else {
            view_divider.setVisibility(View.VISIBLE);
        }

    }
}
