package huaiye.com.vvs.ui.home.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelBean;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.recycle.LiteViewHolder;

/**
 * author: admin
 * date: 2018/02/24
 * version: 0
 * mail: secret
 * desc: MeetHolder
 */

public class TrunkChannelHolder extends LiteViewHolder {

    @BindView(R.id.tv_trunk_name)
    TextView tv_trunk_name;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;

    public TrunkChannelHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
        ivArrow.setOnClickListener(ocl);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        TrunkChannelBean bean = (TrunkChannelBean) data;
        itemView.setTag(bean);
        ivArrow.setTag(bean);

        tv_trunk_name.setText(bean.strTrunkChannelName);
        if (bean.extr != null) {
            boolean value = (boolean) bean.extr;
            if (value) {
                tv_trunk_name.setTextColor(Color.parseColor("#539bf0"));
            } else {
                tv_trunk_name.setTextColor(ActivityCompat.getColor(context,R.color.white));
            }
        } else {
            tv_trunk_name.setTextColor(ActivityCompat.getColor(context,R.color.white));
        }

    }
}
