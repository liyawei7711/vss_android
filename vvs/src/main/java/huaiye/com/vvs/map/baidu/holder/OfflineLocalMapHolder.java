package huaiye.com.vvs.map.baidu.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.common.views.CircleProgressView;
import huaiye.com.vvs.models.map.bean.LocalOfflineMapBean;

import static com.baidu.mapapi.map.offline.MKOLUpdateElement.eOLDSWifiError;

/**
 * author: admin
 * date: 2018/05/12
 * version: 0
 * mail: secret
 * desc: OfflineMapHolder
 */

public class OfflineLocalMapHolder extends LiteViewHolder {
    @BindView(R.id.tv_map_name)
    TextView tv_map_name;
    @BindView(R.id.tv_map_status)
    TextView tv_map_status;
    @BindView(R.id.tv_map_size)
    TextView tv_map_size;
    @BindView(R.id.pb_progress)
    CircleProgressView pb_progress;
    @BindView(R.id.tv_map_current)
    TextView tv_map_current;


    public OfflineLocalMapHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);

        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        LocalOfflineMapBean bean = (LocalOfflineMapBean) data;
        itemView.setTag(R.id.offline_bean, bean);
        itemView.setTag(R.id.offline_position, position);

        tv_map_name.setText(bean.cityName);
        tv_map_size.setText(String.format(AppUtils.getString(R.string.map_size), AppUtils.getDataSize(bean.size)));
//        if(bean.status == eOLDSWifiError) {
//            tv_map_status.setText(AppUtils.getString(R.string.map_load_error));
//        } else {
//            tv_map_status.setText("");
//        }
        tv_map_current.setVisibility(bean.isCurrent ? View.VISIBLE : View.GONE);

        pb_progress.setCurrentProgress(bean.ratio);
    }

}
