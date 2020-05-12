package huaiye.com.vvs.ui.home;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import huaiye.com.vvs.R;
import huaiye.com.vvs.models.map.bean.MarkModelBean;

import static huaiye.com.vvs.dao.msgs.MapMarkBean.BOARD;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.CIRCLE;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.LINE;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.MANYBOARD;
import static huaiye.com.vvs.dao.msgs.MapMarkBean.POINT;

public class MyMapMarkAdapter extends BaseQuickAdapter<MarkModelBean,BaseViewHolder> {

    private boolean inEditMode ;

    public MyMapMarkAdapter(int layoutResId, @Nullable List<MarkModelBean> data) {
        super(layoutResId, data);
        inEditMode = false;
    }

    @Override
    protected void convert(BaseViewHolder helper, MarkModelBean item) {
        View fl_status = helper.getView(R.id.fl_status);

        ImageView cb_status = helper.getView(R.id.cb_status);
        ImageView iv_img_logo = helper.getView(R.id.iv_img_logo);
        TextView tv_mark_name = helper.getView(R.id.tv_mark_name);
        TextView tv_mark_id = helper.getView(R.id.tv_mark_id);
        View view_divider = helper.getView(R.id.view_divider);
        View llItem = helper.getView(R.id.ll_item);

        MarkModelBean bean =  item;
        tv_mark_name.setText(bean.strMarkName);
        tv_mark_id.setText("ID: " + bean.nMarkID);


        if (inEditMode){
            cb_status.setVisibility(View.VISIBLE);
            cb_status.setSelected(bean.isEditChoose);
            llItem.setSelected(false);
        }else {
            cb_status.setVisibility(View.GONE);
            cb_status.setSelected(false);
            llItem.setSelected(bean.isChoose);
        }

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



        if (helper.getAdapterPosition() == getItemCount() - 1) {
            view_divider.setVisibility(View.GONE);
        } else {
            view_divider.setVisibility(View.VISIBLE);
        }
    }


    public void setInEditMode(boolean inEditMode){
        this.inEditMode = inEditMode;
    }
}
