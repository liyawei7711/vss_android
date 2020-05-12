package huaiye.com.vvs.ui.home.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.CreateTalkAndVideo;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.dao.msgs.CallRecordMessage;
import huaiye.com.vvs.dao.msgs.VssMessageListBean;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.meet.bean.MeetList;

import static huaiye.com.vvs.common.AppUtils.ZHILING_CODE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_IMG_TYPE_INT;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: ChatViewHolder
 */

public class CallRecordListViewHolder extends LiteViewHolder {
    @BindView(R.id.view_divider)
    View view_divider;
    @BindView(R.id.meet_view_divider)
    View meet_view_divider;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.talk_type)
    ImageView talk_type;
    @BindView(R.id.left_Image)
    ImageView left_Image;
    @BindView(R.id.item_name)
    TextView item_name;
    @BindView(R.id.item_content)
    TextView item_content;
    @BindView(R.id.talk_view)
    LinearLayout talk_view;
    @BindView(R.id.meet_view)
    LinearLayout meet_view;
    @BindView(R.id.meeting_name)
    TextView meeting_name;


    public CallRecordListViewHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        CallRecordMessage bean = (CallRecordMessage) data;
        itemView.setTag(bean);

        if (bean.getIsRecord() == 1) {
            time.setVisibility(View.VISIBLE);
            meet_view.setVisibility(View.GONE);
            meet_view_divider.setVisibility(View.GONE);
            talk_view.setVisibility(View.VISIBLE);
            view_divider.setVisibility(View.VISIBLE);
            time.setText(bean.getDate());
            talk_type.setImageResource(bean.getIconResource());
            item_content.setText(bean.getDer() + bean.getDomain());
            item_name.setText(bean.getName());
        } else {
            meet_view.setVisibility(View.VISIBLE);
            meet_view_divider.setVisibility(View.VISIBLE);
            talk_view.setVisibility(View.GONE);
            view_divider.setVisibility(View.GONE);
            meeting_name.setText(String.format(AppUtils.getString(R.string.convened_meeting),
                    bean.strMainUserName,bean.strMainUserID));
        }

        if (position == datas.size() - 1) {
            view_divider.setVisibility(View.GONE);
        }
    }
}
