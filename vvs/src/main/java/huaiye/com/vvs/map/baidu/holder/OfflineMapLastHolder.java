package huaiye.com.vvs.map.baidu.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.models.map.bean.OfflineMapBean;

/**
 * author: admin
 * date: 2018/05/12
 * version: 0
 * mail: secret
 * desc: OfflineMapHolder
 */

public class OfflineMapLastHolder extends LiteViewHolder {
    @BindView(R.id.tv_map_name)
    TextView tv_map_name;
    @BindView(R.id.tv_map_status)
    TextView tv_map_status;
    @BindView(R.id.iv_download)
    View iv_download;
    @BindView(R.id.tv_map_size)
    TextView tv_map_size;
    Map<Integer, MKOLUpdateElement> map = new HashMap<>();

    public OfflineMapLastHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        map.putAll((Map<? extends Integer, ? extends MKOLUpdateElement>) obj);
        iv_download.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        OfflineMapBean bean = (OfflineMapBean) data;

        iv_download.setTag(R.id.offline_city_bean, bean);
        iv_download.setTag(R.id.offline_city_position, position);

        tv_map_size.setText(String.format(AppUtils.getString(R.string.map_size), AppUtils.getDataSize(bean.dataSize)));
        tv_map_name.setText(bean.cityName);

        MKOLUpdateElement element = map.get(bean.cityID);

        if (element != null) {
            iv_download.setVisibility(View.GONE);
            tv_map_status.setVisibility(View.VISIBLE);
            switch (element.status) {
                case MKOLUpdateElement.DOWNLOADING:
                    tv_map_status.setText(AppUtils.getString(R.string.download));
                    break;
                case MKOLUpdateElement.FINISHED:
                    tv_map_status.setText(AppUtils.getString(R.string.update_download));
                    break;
                case MKOLUpdateElement.WAITING:
                    tv_map_status.setText(AppUtils.getString(R.string.update_download_waite));
                    break;
                default:
                    tv_map_status.setText("");

                    iv_download.setVisibility(View.VISIBLE);
                    tv_map_status.setVisibility(View.GONE);
                    break;
            }
        } else {
            iv_download.setVisibility(View.VISIBLE);
            tv_map_status.setVisibility(View.GONE);
        }
    }

}
