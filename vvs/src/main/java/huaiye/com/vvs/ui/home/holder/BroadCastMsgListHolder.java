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
import huaiye.com.vvs.dao.msgs.VssMessageBean;

/**
 * author: admin
 * date: 2018/02/24
 * version: 0
 * mail: secret
 * desc: MeetHolder
 */

public class BroadCastMsgListHolder extends LiteViewHolder {

    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.tv_notice_msg_show)
    TextView tv_notice_msg_show;

    public BroadCastMsgListHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        iv_close.setOnClickListener(ocl);
        itemView.setOnClickListener(ocl);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        VssMessageBean bean = (VssMessageBean) data;
        iv_close.setTag(bean);
        itemView.setTag(bean);
        if (bean.type == AppUtils.ZHILING_IMG_TYPE_INT) {
            tv_notice_msg_show.setText(bean.fromUserName + ":[" + AppUtils.getString(R.string.img) + "]");
        } else if (bean.type == AppUtils.ZHILING_FILE_TYPE_INT) {
            tv_notice_msg_show.setText(bean.fromUserName + ":[" + AppUtils.getString(R.string.notice_file) + "]");
        } else if (bean.type == AppUtils.PLAYER_TYPE_PERSON_INT ||
                bean.type == AppUtils.PLAYER_TYPE_DEVICE_INT) {
            tv_notice_msg_show.setText(bean.fromUserName + ":[" + AppUtils.getString(R.string.notice_share) + "]");
        } else {
            tv_notice_msg_show.setText(bean.fromUserName + ":" + bean.content);
        }
    }

}
