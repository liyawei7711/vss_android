package huaiye.com.vvs.ui.home.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.map.baidu.appcluster.MyCluster;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;

/**
 * author: admin
 * date: 2018/02/24
 * version: 0
 * mail: secret
 * desc: MeetHolder
 */

public class MuliteMarkerListHolder extends LiteViewHolder {

    @BindView(R.id.iv_header)
    ImageView iv_header;
    @BindView(R.id.iv_type)
    ImageView iv_type;
    @BindView(R.id.iv_status)
    ImageView iv_status;
    @BindView(R.id.tv_type_info)
    TextView tv_type_info;

    public MuliteMarkerListHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        MyCluster bean = (MyCluster) data;
        itemView.setTag(bean);

        iv_status.setVisibility(View.GONE);
        iv_type.setVisibility(View.GONE);
        if (bean.bean.personModelBean != null) {
            iv_status.setVisibility(View.VISIBLE);
            iv_type.setVisibility(View.VISIBLE);
            if (bean.bean.personModelBean.nDevType == AppUtils.DevType_ANDROID ||
                    bean.bean.personModelBean.nDevType == AppUtils.DevType_IOS) {
                iv_type.setImageResource(R.drawable.liebiao_yidongduan);
            } else {
                iv_type.setImageResource(R.drawable.liebiao_pcduan);
            }

            if (bean.bean.personModelBean.nStatus == PersonModelBean.STATUS_OFFLINE) {
                iv_status.setImageResource(R.drawable.dian_lixian);
            } else if (bean.bean.personModelBean.nStatus == PersonModelBean.STATUS_ONLINE_IDLE) {
                iv_status.setImageResource(R.drawable.dian_zaixian);
            } else {
                iv_status.setImageResource(R.drawable.dian_mang);
            }
            iv_header.setImageResource(R.drawable.tip_xingming);
            tv_type_info.setText(AppUtils.getString(R.string.person_item) + bean.bean.personModelBean.strUserName + " (ID:" + bean.bean.personModelBean.strUserID + ")");
        } else if (bean.bean.deviceBean != null) {
            iv_status.setVisibility(View.VISIBLE);
            if (bean.bean.deviceBean.nOnlineState == 1) {
                iv_status.setImageResource(R.drawable.dian_zaixian);
            } else {
                iv_status.setImageResource(R.drawable.dian_lixian);
            }
            tv_type_info.setText(AppUtils.getString(R.string.device_item) + bean.bean.deviceBean.strChannelName);
            iv_header.setImageResource(R.drawable.liebiao_bofangqi);
        } else if (bean.bean.markModelBean != null) {
            tv_type_info.setText(AppUtils.getString(R.string.market_item) + bean.bean.markModelBean.strMarkName);
            iv_header.setImageResource(R.drawable.liebiao_putong);
        } else if (bean.bean.domainModelBean != null) {
            tv_type_info.setText(AppUtils.getString(R.string.domain_item) + bean.bean.domainModelBean.strDomainName);
            iv_header.setImageResource(R.drawable.liebiao_yu);
        } else {
            iv_type.setVisibility(View.VISIBLE);
            iv_type.setImageResource(R.drawable.liebiao_yidongduan);
            tv_type_info.setText(bean.bean.p2pDeviceBean.m_strName+" IP("+bean.bean.p2pDeviceBean.m_strName+")");
            iv_header.setImageResource(R.drawable.tip_xingming);
        }
    }

}
