package huaiye.com.vvs.common.views.pickers.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.common.views.pickers.SelectItemDialog;

/**
 * author: admin
 * date: 2018/02/23
 * version: 0
 * mail: secret
 * desc: ItemHolder
 */

public class ItemHolder extends LiteViewHolder {

    @BindView(R.id.item_name)
    TextView item_name;

    public ItemHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        if (ocl != null) {
            itemView.setOnClickListener(ocl);
        }
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        SelectItemDialog.SelectBean bean = (SelectItemDialog.SelectBean) data;
        item_name.setText(bean.name);
        itemView.setTag(bean);
    }
}
