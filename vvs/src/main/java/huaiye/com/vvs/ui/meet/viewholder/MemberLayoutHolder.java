package huaiye.com.vvs.ui.meet.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.ui.meet.basemodel.SelectedModel;

/**
 * Created by Administrator on 2018\2\27 0027.
 */

public class MemberLayoutHolder extends LiteViewHolder {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_state)
    ImageView ivState;

    public MemberLayoutHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        SelectedModel<CGetMeetingInfoRsp.UserInfo> bean = (SelectedModel<CGetMeetingInfoRsp.UserInfo>) data;
        itemView.setTag(bean);

        tvName.setText(bean.bean.strUserName);

        if (bean.isChecked) {
            ivState.setImageResource(R.drawable.gouxuan);
        } else {
            ivState.setImageResource(R.drawable.weigouxuan);
        }

        if (position == 0) {
            tvTitle.setText(AppUtils.getString(R.string.yishangqiang));
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            if (bean.bean.inVideo() != ((SelectedModel<CGetMeetingInfoRsp.UserInfo>) datas.get(position - 1)).bean.inVideo()
                    && ((SelectedModel<CGetMeetingInfoRsp.UserInfo>) datas.get(position - 1)).bean.nCombineStatus == 1) {
                tvTitle.setText(AppUtils.getString(R.string.weishangqiang));
                tvTitle.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
        }
    }
}
