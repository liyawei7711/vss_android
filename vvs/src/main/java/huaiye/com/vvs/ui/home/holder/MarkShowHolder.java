package huaiye.com.vvs.ui.home.holder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.models.map.bean.MarkModelBean;

import static huaiye.com.vvs.dao.msgs.MapMarkBean.BOARD;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.CIRCLE;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.LINE;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.MANYBOARD;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.POINT;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DomainHolder
 */

public class MarkShowHolder extends LiteViewHolder {

    @BindView(R.id.fl_status)
    View fl_status;

    @BindView(R.id.cb_status)
    ImageView cb_status;
    @BindView(R.id.iv_img_logo)
    ImageView iv_img_logo;
    @BindView(R.id.tv_mark_name)
    TextView tv_mark_name;
    @BindView(R.id.tv_mark_id)
    TextView tv_mark_id;
    @BindView(R.id.view_divider)
    View view_divider;

    public MarkShowHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        MarkModelBean bean = (MarkModelBean) data;
        itemView.setTag(bean);
        tv_mark_name.setText(bean.strMarkName);
        tv_mark_id.setText("ID: " + bean.nMarkID);

//        Map<Integer, String> all = (Map<Integer, String>) extr;
        cb_status.setSelected(bean.isChoose);

        tv_mark_name.setTextColor(Color.parseColor("#333333"));
        switch (bean.nType) {
            case POINT:
                iv_img_logo.setImageResource(R.drawable.icon_dian_press);
                break;
            case LINE:
                iv_img_logo.setImageResource(R.drawable.icon_xian_press);
                break;
            case BOARD:
            case MANYBOARD:
            case CIRCLE:
                iv_img_logo.setImageResource(R.drawable.icon_mian_press);
                break;
        }


        if (position == datas.size() - 1) {
            view_divider.setVisibility(View.GONE);
        } else {
            view_divider.setVisibility(View.VISIBLE);
        }

    }
}
