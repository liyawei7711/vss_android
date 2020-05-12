package huaiye.com.vvs.ui.channel;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelUserBean;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.recycle.LiteViewHolder;

public class ChannelDetailItemHolder extends LiteViewHolder {

    @BindView(R.id.tv_name)
    public TextView tvName;
    @BindView(R.id.tv_id)
    public TextView tvID;

    @BindView(R.id.divider)
    View divider;


    public ChannelDetailItemHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        if (position > datas.size()){
            divider.setVisibility(View.INVISIBLE);
            return;
        }
        divider.setVisibility(View.VISIBLE);
        TrunkChannelUserBean userBean = (TrunkChannelUserBean) data;
        tvName.setText(userBean.strTcUserName);
        tvID.setText("ID: " + userBean.strTcUserID);
    }
}
