package huaiye.com.vvs.ui.chat.holder;

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

import static huaiye.com.vvs.common.AppUtils.ZHILING_IMG_TYPE_INT;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: ChatViewHolder
 */

public class ChatSearchListViewHolder extends LiteViewHolder {
    @BindView(R.id.view_divider)
    View view_divider;
    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.left_Image)
    ImageView left_Image;
    @BindView(R.id.item_name)
    TextView item_name;
    @BindView(R.id.item_content)
    TextView item_content;


    public ChatSearchListViewHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        VssMessageBean bean = (VssMessageBean) data;
        itemView.setTag(bean);

        time.setVisibility(View.VISIBLE);
        time.setText(AppUtils.getTimeHour(bean.time));

        item_name.setText(bean.sessionName);
        if (bean.type == ZHILING_IMG_TYPE_INT) {
            item_content.setText("[" + AppUtils.getString(R.string.img) + "]");
        } else if (bean.type == AppUtils.ZHILING_FILE_TYPE_INT) {
            item_content.setText("[" + AppUtils.getString(R.string.notice_file) + "]");
        } else if (bean.type == AppUtils.PLAYER_TYPE_PERSON_INT ||
                bean.type == AppUtils.PLAYER_TYPE_DEVICE_INT) {
            item_content.setText("[" + AppUtils.getString(R.string.notice_share) + "]");
        } else {
            item_content.setText(bean.content);
        }

        if (position == datas.size() - 1) {
            view_divider.setVisibility(View.GONE);
        } else {
            view_divider.setVisibility(View.VISIBLE);
        }
    }

}
