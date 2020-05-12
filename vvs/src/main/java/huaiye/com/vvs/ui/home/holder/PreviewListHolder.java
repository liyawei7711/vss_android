package huaiye.com.vvs.ui.home.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.map.MapApi;
import huaiye.com.vvs.ui.home.MainActivity;

/**
 * author: admin
 * date: 2018/02/24
 * version: 0
 * mail: secret
 * desc: MeetHolder
 */

public class PreviewListHolder extends LiteViewHolder {

    private static final String TAG = PreviewListHolder.class.getSimpleName();
    @BindView(R.id.iv_header)
    ImageView iv_header;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_id)
    TextView tv_id;
    @BindView(R.id.tv_status)
    TextView tv_status;

    @BindView(R.id.ll_menu_info)
    View ll_menu_info;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_lat)
    TextView tv_lat;
    @BindView(R.id.tv_info)
    TextView tv_info;

    @BindView(R.id.tv_phone)
    View tv_phone;
    @BindView(R.id.tv_video)
    View tv_video;
    @BindView(R.id.tv_meet)
    View tv_meet;
    @BindView(R.id.tv_watch)
    View tv_watch;
    @BindView(R.id.tv_zhihui)
    View tv_zhihui;

    public PreviewListHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
        tv_phone.setOnClickListener(ocl);
        tv_video.setOnClickListener(ocl);
        tv_meet.setOnClickListener(ocl);
        tv_watch.setOnClickListener(ocl);
        tv_zhihui.setOnClickListener(ocl);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        final PersonModelBean bean = (PersonModelBean) data;
        itemView.setTag(bean);
        tv_phone.setTag(bean);
        tv_video.setTag(bean);
        tv_meet.setTag(bean);
        tv_watch.setTag(bean);
        tv_zhihui.setTag(bean);

        if (bean.nPriority > AppDatas.Auth().getPriority()) {
            tv_watch.setVisibility(View.VISIBLE);
        } else {
            tv_watch.setVisibility(View.GONE);
        }

        if (bean.isSelected) {
            ll_menu_info.setVisibility(View.VISIBLE);
            if (bean.addressDetail == null) {
                MapApi.get().getUserAddress(new LatLng(bean.dLatitude, bean.dLongitude), new OnGetGeoCoderResultListener() {

                    //经纬度转换成地址

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                        } else {
                            bean.addressDetail = result.getAddress();
                            tv_address.setText(bean.addressDetail == null ? AppUtils.getString(R.string.load_quick) : bean.addressDetail);
                        }
                    }

                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult result) {
                    }
                });
            }
            if (((MainActivity) context).currentMapData != null) {
                //从服务器请求下来的坐标需要转换为百度坐标,这样百度sdk计算距离的时候才算的对
                bean.addressDistance = Math.round(DistanceUtil.getDistance(new LatLng(bean.dLatitude, bean.dLongitude), new LatLng(((MainActivity) context).currentMapData.latitude, ((MainActivity) context).currentMapData.longitude))) + "";
            }
        } else {
            ll_menu_info.setVisibility(View.GONE);
        }

        tv_name.setText(bean.strUserName);
        tv_id.setText("ID: " + bean.strUserName);

        tv_address.setText(bean.addressDetail == null ? AppUtils.getString(R.string.load_quick) : bean.addressDetail);
        tv_lat.setText(bean.dLatitude + "," + bean.dLongitude);
        tv_info.setText("(" + (TextUtils.isEmpty(bean.addressDistance) ? AppUtils.getString(R.string.load_distance) : bean.addressDistance) + "/"
                + bean.strLastLoginTime + AppUtils.getString(R.string.time_ref_location) + ")");

        switch (bean.nStatus) {
            case PersonModelBean.STATUS_OFFLINE:
            case PersonModelBean.STATUS_ONLINE_IDLE:
                tv_status.setText("");
                break;
            case PersonModelBean.STATUS_ONLINE_CAPTURING:
                tv_status.setText(AppUtils.getString(R.string.capture_ing));
                break;
            case PersonModelBean.STATUS_ONLINE_TALKING:
                tv_status.setText(AppUtils.getString(R.string.talk_ing));
                break;
            case PersonModelBean.STATUS_ONLINE_MEETING:
                tv_status.setText(AppUtils.getString(R.string.meet_ing));
                break;
            case PersonModelBean.STATUS_ONLINE_TRUNK_SPEAKING:
                tv_status.setText(AppUtils.getString(R.string.trunk_speak_ing));
                break;
        }

    }

}
