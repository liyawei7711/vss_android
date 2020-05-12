package huaiye.com.vvs.ui.device.holder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.models.device.bean.DevicePlayerBean;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DomainHolder
 */

public class DeviceChooseHolder extends LiteViewHolder {

    @BindView(R.id.tv_device_name)
    TextView tv_device_name;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.view_divider)
    View view_divider;
    @BindView(R.id.cb_status)
    CheckBox cb_status;

    public DeviceChooseHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        DevicePlayerBean bean = (DevicePlayerBean) data;
        itemView.setTag(bean);

        tv_device_name.setText(bean.strChannelName + AppUtils.getString(R.string.device_name));

        cb_status.setChecked(bean.isSelected);

        if (bean.nOnlineState == 1) {
            tv_status.setText(AppUtils.getString(R.string.online));
        } else {
            tv_status.setText(AppUtils.getString(R.string.offline));
        }

        if (position == datas.size() - 1) {
            view_divider.setVisibility(View.GONE);
        } else {
            view_divider.setVisibility(View.VISIBLE);
        }
    }
}
