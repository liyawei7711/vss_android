package huaiye.com.vvs.ui.home.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DomainHolder
 */

public class SosHolder extends LiteViewHolder {


    @BindView(R.id.tv_sos_name)
    TextView tv_sos_name;
    @BindView(R.id.tv_sos_id)
    TextView tv_sos_id;
    @BindView(R.id.tv_busy)
    TextView tv_busy;
    @BindView(R.id.tv_call)
    TextView tv_call;
    @BindView(R.id.view_divider)
    View view_divider;

    public SosHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        tv_call.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        PersonModelBean bean = (PersonModelBean) data;
        tv_call.setTag(bean);
        tv_sos_name.setText(bean.strUserName);
        tv_sos_id.setText(bean.strUserID);

        if (bean.nStatus == PersonModelBean.STATUS_ONLINE_IDLE){
            tv_busy.setVisibility(View.GONE);
        }else {
            tv_busy.setVisibility(View.VISIBLE);
        }

        if (position == datas.size() - 1) {
            view_divider.setVisibility(View.GONE);
        } else {
            view_divider.setVisibility(View.VISIBLE);
        }

    }
}
