package huaiye.com.vvs.ui.home.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.models.device.TopoDeviceListResp;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DomainHolder
 */

public class TopoHolder extends LiteViewHolder {


    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_ip)
    TextView tv_ip;
    @BindView(R.id.tv_right)
    TextView tv_right;

    @BindView(R.id.view_divider)
    View view_divider;

    public TopoHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
//        tv_call.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        TopoDeviceListResp.DeviceInfo bean = (TopoDeviceListResp.DeviceInfo) data;

        tv_name.setText(bean.byname);
        tv_ip.setText(bean.ip);
        tv_right.setText(bean.noise+"");
        if (position == datas.size() - 1) {
            view_divider.setVisibility(View.GONE);
        } else {
            view_divider.setVisibility(View.VISIBLE);
        }

    }
}
