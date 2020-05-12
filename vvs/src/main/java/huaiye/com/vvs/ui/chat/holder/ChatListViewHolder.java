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
import huaiye.com.vvs.dao.msgs.VssMessageListBean;

import static huaiye.com.vvs.common.AppUtils.BROADCAST_AUDIO_FILE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.BROADCAST_AUDIO_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.BROADCAST_VIDEO_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_DEVICE_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_PERSON_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_CODE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_FILE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_IMG_TYPE_INT;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: ChatViewHolder
 */

public class ChatListViewHolder extends LiteViewHolder {
    @BindView(R.id.view_divider)
    View view_divider;
    @BindView(R.id.view_point)
    View view_point;
    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.left_Image)
    ImageView left_Image;
    @BindView(R.id.item_name)
    TextView item_name;
    @BindView(R.id.item_content)
    TextView item_content;


    public ChatListViewHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        VssMessageListBean bean = (VssMessageListBean) data;
        itemView.setTag(bean);

        time.setVisibility(View.VISIBLE);
        time.setText(bean.getTime());

        item_name.setText(bean.sessionName);
        //session=0为广播
        if (bean.type == ZHILING_IMG_TYPE_INT) {
            item_content.setText("["+ AppUtils.getString(R.string.img)+"]");
            left_Image.setImageResource("0".equals(bean.sessionID)?R.drawable.zhilingdiaodu_guangbo:R.drawable.zhilingdiaodu_putongxiaoxi);
        } else if (bean.type == ZHILING_FILE_TYPE_INT) {
            item_content.setText("["+ AppUtils.getString(R.string.notice_file)+"]");
            left_Image.setImageResource("0".equals(bean.sessionID)?R.drawable.zhilingdiaodu_guangbo:R.drawable.zhilingdiaodu_putongxiaoxi);
        } else if (bean.type == ZHILING_CODE_TYPE_INT) {
            item_content.setText(bean.content);
            left_Image.setImageResource("0".equals(bean.sessionID)?R.drawable.zhilingdiaodu_guangbo:R.drawable.zhilingdiaodu_putongxiaoxi);
        } else if (bean.type == PLAYER_TYPE_PERSON_INT || bean.type == PLAYER_TYPE_DEVICE_INT) {
            if (bean.type == PLAYER_TYPE_PERSON_INT) {
                item_content.setText("["+AppUtils.getString(R.string.person_share)+"]");
            } else {
                item_content.setText("["+AppUtils.getString(R.string.device_share)+"]");
            }
            left_Image.setImageResource("0".equals(bean.sessionID)?R.drawable.zhilingdiaodu_guangbo:R.drawable.zhilingdiaodu_tuisong);
        }else if (bean.type == BROADCAST_AUDIO_TYPE_INT || bean.type == BROADCAST_AUDIO_FILE_TYPE_INT){
            item_content.setText("["+AppUtils.getString(R.string.audio)+"]");
            left_Image.setImageResource(R.drawable.zhilingdiaodu_guangbo);
        }else if (bean.type == BROADCAST_VIDEO_TYPE_INT){
            item_content.setText("["+AppUtils.getString(R.string.video)+"]");
            left_Image.setImageResource(R.drawable.zhilingdiaodu_guangbo);
        }

        if (bean.isRead == 1) {
            view_point.setVisibility(View.GONE);
        } else {
            view_point.setVisibility(View.VISIBLE);
        }

        if (position == datas.size() - 1) {
            view_divider.setVisibility(View.GONE);
        } else {
            view_divider.setVisibility(View.VISIBLE);
        }
    }

}
