package huaiye.com.vvs.map.baidu.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.recycle.SafeLinearLayoutManager;
import huaiye.com.vvs.map.baidu.holder.OfflineMapHolder;
import huaiye.com.vvs.map.baidu.holder.OfflineMapLastHolder;
import huaiye.com.vvs.map.baidu.utils.MapListener;
import huaiye.com.vvs.models.map.bean.OfflineMapBean;

import static com.baidu.mapapi.map.offline.MKOLUpdateElement.FINISHED;

/**
 * author: admin
 * date: 2018/04/26
 * version: 0
 * mail: secret
 * desc: BaiDuMapActivity
 */
@BindLayout(R.layout.activity_baidu_map_offine_download)
public class OfflineMapActivity extends AppBaseActivity {

    @BindView(R.id.rv_map_list)
    RecyclerView rv_map_list;

    private LiteBaseAdapter<OfflineMapBean> adapter = null;
    private LiteBaseAdapter<OfflineMapBean> adapterLast;
    private ArrayList<OfflineMapBean> allCity = new ArrayList<>();
    private Map<Integer, MKOLUpdateElement> map = new HashMap<>();

    int Currentposition;
    OfflineMapBean currentBean;


    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(AppUtils.getString(R.string.city_map))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });


        initView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MKOLUpdateElement location) {
        MKOLUpdateElement ele = map.get(location.cityID);
        if (location.ratio == 100) {
            location.status = FINISHED;
        }
        if (ele == null) map.put(location.cityID, location);

        if (ele != null && ele.status == location.status) return;

        map.put(location.cityID, location);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void doInitDelay() {
        initData(null);
    }

    private void initView() {
        map.clear();
        ArrayList<MKOLUpdateElement> temps = MapListener.get().getMkOfflineMap().getAllUpdateInfo();
        if (temps != null) {
            for (MKOLUpdateElement ele : temps) {
                map.put(ele.cityID, ele);
            }
        }
        adapterLast = new LiteBaseAdapter<>(
                this,
                null,
                OfflineMapLastHolder.class,
                R.layout.item_offline_map_last,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OfflineMapBean bean = (OfflineMapBean) v.getTag(R.id.offline_city_bean);
                        MapListener.get().startLoad(bean.cityID);
                    }
                }, map);
        adapter = new LiteBaseAdapter<>(
                this,
                allCity,
                OfflineMapHolder.class,
                R.layout.item_offline_map,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OfflineMapBean bean = (OfflineMapBean) v.getTag(R.id.offline_bean);
                        int position = (int) v.getTag(R.id.offline_position);

                        if (v.getId() == R.id.iv_download) {
                            MapListener.get().startLoad(bean.cityID);
                            return;
                        }

                        if (currentBean == null) {
                            bean.openStatus = !bean.openStatus;

                            currentBean = bean;
                            Currentposition = position;

                            adapter.notifyDataSetChanged();

                        } else {
                            currentBean.openStatus = !currentBean.openStatus;
                            adapter.notifyItemChanged(Currentposition);
                            if (currentBean == bean) {
                                currentBean = null;
                                return;
                            } else {

                                bean.openStatus = !bean.openStatus;

                                currentBean = bean;
                                Currentposition = position;

                                adapter.notifyDataSetChanged();
                            }
                        }


                        if (bean.childCities != null) {
                            adapterLast.setDatas(bean.childCities);
                        } else {
                            adapterLast.setDatas(new ArrayList<OfflineMapBean>());
                        }
                        adapterLast.notifyDataSetChanged();
                    }
                }, adapterLast);
        rv_map_list.setAdapter(adapter);
        rv_map_list.setLayoutManager(new SafeLinearLayoutManager(this));


    }

    @Override
    protected void onDestroy() {
        /** * 退出时，销毁离线地图模块 */
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BDLocation bean) {
//        BDLocation location = locationService.getLastUploadLocations();
//        if (location.getCity() == null) {
//            return;
//        }
//        if (location != null) {
//            if (allCity.size() > 0) {
//                if (allCity.get(0).cityType == 0) {
//                    OfflineMapBean offlineMapBean = new OfflineMapBean();
//                    offlineMapBean.cityName = location.getCity();
//                    allCity.get(0).cityID = Integer.parseInt(location.getCityCode());
//                    offlineMapBean.cityType = 2;
//                    allCity.add(0, offlineMapBean);
//                    adapter.notifyDataSetChanged();
//                } else {
//                    if (!allCity.get(0).cityName.equals(location.getCity())) {
//                        allCity.get(0).cityName = location.getCity();
//                        allCity.get(0).cityID = Integer.parseInt(location.getCityCode());
//                        adapter.notifyItemChanged(0);
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//
//            }
//        }
    }

    void initData(BDLocation location) {

        if (location != null) {
            if (allCity.size() > 0) {
                allCity.get(0).cityName = location.getCity();
                adapter.notifyItemChanged(0);
                return;
            } else {
                OfflineMapBean offlineMapBean = new OfflineMapBean();
                offlineMapBean.cityName = location.getCity();
                allCity.add(offlineMapBean);
            }
        }


        ArrayList<MKOLSearchRecord> temps = MapListener.get().getMkOfflineMap().getOfflineCityList();
        OfflineMapBean zhixia = new OfflineMapBean();
        zhixia.cityType = 1;
        zhixia.cityName = AppUtils.getString(R.string.zhixiashi);

        for (MKOLSearchRecord temp : temps) {
            OfflineMapBean mapBean = new OfflineMapBean();
            mapBean.cityID = temp.cityID;
            mapBean.size = temp.size;
            mapBean.dataSize = temp.dataSize;
            mapBean.cityName = temp.cityName;
            mapBean.cityType = temp.cityType;

            if (temp.cityType != 0 && temp.childCities == null) {
                zhixia.childCities.add(mapBean);
                if (!allCity.contains(zhixia)) {
                    allCity.add(zhixia);
                }
            } else {
                allCity.add(mapBean);
            }


            if (temp.childCities != null) {
                for (MKOLSearchRecord tempCity : temp.childCities) {

                    OfflineMapBean mapBeanCity = new OfflineMapBean();
                    mapBeanCity.cityID = tempCity.cityID;
                    mapBeanCity.size = tempCity.size;
                    mapBeanCity.dataSize = tempCity.dataSize;
                    mapBeanCity.cityName = tempCity.cityName;
                    mapBeanCity.cityType = tempCity.cityType;

                    mapBean.childCities.add(mapBeanCity);
                }
            }

        }

        adapter.notifyDataSetChanged();
    }
}

