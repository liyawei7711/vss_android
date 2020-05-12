package huaiye.com.vvs.map.baidu.holder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.map.baidu.utils.MapListener;
import huaiye.com.vvs.models.map.bean.OfflineMapBean;

/**
 * author: admin
 * date: 2018/05/12
 * version: 0
 * mail: secret
 * desc: OfflineMapHolder
 */

public class OfflineMapHolder extends LiteViewHolder {
    @BindView(R.id.tv_status_title)
    TextView tv_status_title;
    @BindView(R.id.tv_map_name)
    TextView tv_map_name;
    @BindView(R.id.tv_map_status)
    TextView tv_map_status;
    @BindView(R.id.iv_download)
    View iv_download;
    @BindView(R.id.iv_status)
    ImageView iv_status;
    @BindView(R.id.tv_map_size)
    TextView tv_map_size;

    @BindView(R.id.rv_map_city)
    RecyclerView rv_map_city;


    public OfflineMapHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);

        iv_download.setOnClickListener(ocl);
        itemView.setOnClickListener(ocl);

        rv_map_city.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        OfflineMapBean bean = (OfflineMapBean) data;
        iv_download.setTag(R.id.offline_bean, bean);
        iv_download.setTag(R.id.offline_position, position);
        itemView.setTag(R.id.offline_bean, bean);
        itemView.setTag(R.id.offline_position, position);

        rv_map_city.setAdapter((RecyclerView.Adapter) extr);

        tv_map_name.setText(bean.cityName);
        tv_map_size.setText(AppUtils.getDataSize(bean.dataSize));
        tv_map_status.setText("");

        if (bean.cityType == 0 || bean.cityType == 2) {
            rv_map_city.setVisibility(View.GONE);
            tv_map_size.setVisibility(View.GONE);
            iv_status.setVisibility(View.GONE);
        } else {
            if (bean.childCities == null) {
                iv_status.setVisibility(View.GONE);
                rv_map_city.setVisibility(View.GONE);
                tv_map_size.setVisibility(View.GONE);
            } else if (bean.childCities.isEmpty()) {
                iv_status.setVisibility(View.GONE);
                tv_map_size.setVisibility(View.GONE);
                rv_map_city.setVisibility(View.GONE);
            } else {
                tv_map_size.setVisibility(View.VISIBLE);
                rv_map_city.setVisibility(View.VISIBLE);
                iv_status.setVisibility(View.VISIBLE);
            }
        }

        rv_map_city.setVisibility(bean.openStatus ? View.VISIBLE : View.GONE);
        iv_status.setBackgroundResource(bean.openStatus ?
                R.drawable.btn_shouqi : R.drawable.btn_zhankai);

        if (bean.cityName.equals(AppUtils.getString(R.string.zhixiashi))) {
            tv_status_title.setVisibility(View.VISIBLE);
            tv_status_title.setText(AppUtils.getString(R.string.area_search));
        } else {
            tv_status_title.setVisibility(View.GONE);
            tv_status_title.setText("");
        }

        MKOLUpdateElement element = MapListener.get().getMkOfflineMap().getUpdateInfo(bean.cityID);
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

        if (element == null) {
            if (bean.cityType == 0 || bean.cityType == 2) {
                tv_map_size.setVisibility(View.GONE);
                iv_status.setVisibility(View.GONE);
                iv_download.setVisibility(View.VISIBLE);
            } else {
                if (bean.childCities == null) {
                    tv_map_size.setVisibility(View.VISIBLE);
                    iv_status.setVisibility(View.GONE);
                    iv_download.setVisibility(View.VISIBLE);
                    tv_map_status.setText(AppUtils.getDataSize(bean.dataSize));
                } else if (bean.childCities.isEmpty()) {
                    tv_map_size.setVisibility(View.VISIBLE);
                    iv_status.setVisibility(View.GONE);
                    iv_download.setVisibility(View.VISIBLE);
                } else {
                    tv_map_size.setVisibility(View.GONE);
                    iv_status.setVisibility(View.VISIBLE);
                    iv_download.setVisibility(View.GONE);
                }
            }
        }
    }

}
