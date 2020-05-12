package huaiye.com.vvs.ui.home.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMsgFindLanCaptureDeviceRsp;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteViewHolder;

/**
 * author: admin
 * date: 2018/02/24
 * version: 0
 * mail: secret
 * desc: MeetHolder
 */

public class P2PMuliteMarkerListHolder extends LiteViewHolder {

    @BindView(R.id.iv_header)
    ImageView iv_header;
    @BindView(R.id.iv_type)
    ImageView iv_type;
    @BindView(R.id.iv_status)
    ImageView iv_status;
    @BindView(R.id.tv_type_info)
    TextView tv_type_info;

    public P2PMuliteMarkerListHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        SdpMsgFindLanCaptureDeviceRsp bean = (SdpMsgFindLanCaptureDeviceRsp) data;
        itemView.setTag(bean);

        iv_status.setVisibility(View.GONE);
        iv_type.setVisibility(View.GONE);
        iv_type.setImageResource(R.drawable.liebiao_yidongduan);

        if (bean.m_nCaptureState == 0) {
            iv_status.setImageResource(R.drawable.dian_lixian);
        } else if (bean.m_nCaptureState == 1) {
            iv_status.setImageResource(R.drawable.dian_zaixian);
        } else {
            iv_status.setImageResource(R.drawable.dian_mang);
        }

        iv_header.setImageResource(R.drawable.tip_xingming);
        tv_type_info.setText(AppUtils.getString(R.string.person_item) + bean.m_strName + " (IP:" + bean.m_strIP + ")");
    }

}
