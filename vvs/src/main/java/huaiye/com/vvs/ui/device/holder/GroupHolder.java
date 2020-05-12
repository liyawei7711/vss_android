package huaiye.com.vvs.ui.device.holder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelBean;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.recycle.LiteViewHolder;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DomainHolder
 */

public class GroupHolder extends LiteViewHolder {

    @BindView(R.id.iv_header)
    ImageView iv_header;
    @BindView(R.id.cb_status)
    CheckBox cb_status;
    @BindView(R.id.tv_group_name)
    TextView tv_group_name;
    @BindView(R.id.tv_group_id)
    TextView tv_group_id;
    @BindView(R.id.view_status)
    View view_status;

    public GroupHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
        view_status.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        TrunkChannelBean bean = (TrunkChannelBean) data;
        itemView.setTag(bean);
        view_status.setTag(bean);
        tv_group_name.setText(bean.strTrunkChannelName);
        tv_group_id.setText("ID: " + bean.nTrunkChannelID);
        if (bean.extr != null && (boolean) bean.extr) {
            cb_status.setChecked(true);
        } else {
            cb_status.setChecked(false);
        }
    }

}
