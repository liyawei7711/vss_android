package huaiye.com.vvs.map.baidu.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.dialog.SetDialog;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.map.baidu.holder.OfflineLocalMapHolder;
import huaiye.com.vvs.map.baidu.utils.MapListener;
import huaiye.com.vvs.models.map.bean.LocalOfflineMapBean;

import static com.baidu.mapapi.map.offline.MKOLUpdateElement.SUSPENDED;
import static com.baidu.mapapi.map.offline.MKOLUpdateElement.eOLDSNetError;
import static com.baidu.mapapi.map.offline.MKOLUpdateElement.eOLDSWifiError;

/**
 * author: admin
 * date: 2018/04/26
 * version: 0
 * mail: secret
 * desc: BaiDuMapActivity
 */
@BindLayout(R.layout.activity_baidu_map_offine_download)
public class OfflineMapListActivity extends AppBaseActivity {

    @BindView(R.id.rv_map_list)
    RecyclerView rv_map_list;
    @BindView(R.id.tv_map_status)
    TextView tv_map_status;
    @BindView(R.id.iv_empty_view)
    View iv_empty_view;
//    @BindView(R.id.iv_download)
//    ImageView iv_download;
//    @BindView(R.id.tv_status)
//    TextView tv_status;
//    @BindView(R.id.ll_current_city)
//    LinearLayout ll_current_city;


    SetDialog dialog;

    private LiteBaseAdapter<LocalOfflineMapBean> adapter = null;
    private ArrayList<LocalOfflineMapBean> allCity = new ArrayList<>();
    private ArrayList<String> allID = new ArrayList<>();

    LocalOfflineMapBean currentBean;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(AppUtils.getString(R.string.off_map))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                .setRightIcon(R.drawable.btn_xinzeng)
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OfflineMapListActivity.this, OfflineMapActivity.class);
                        startActivity(intent);
                    }
                });
        MapListener.get().start();

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void doInitDelay() {

    }

//    @OnClick(R.id.iv_download)
//    public void onClick() {
//        if (MCApp.getInstance().locationService.getCurrentBDLocation() != null
//                && MCApp.getInstance().locationService.getCurrentBDLocation().getCityCode() != null) {
//            MapListener.get().startLoad(Integer.parseInt(MCApp.getInstance().locationService.getCurrentBDLocation().getCityCode()));
//            initData();
//        } else {
//            showToast(AppUtils.getString(R.string.offline_no_cityid));
//        }
//    }

    private void initView() {
        dialog = new SetDialog(this);
        dialog.setCompleteListener(new SetDialog.CompleteListener() {
            @Override
            public void onComplete(String birthDay) {

            }

            @Override
            public void tocontinue(String str) {
                if (currentBean != null) {
                    if (AppUtils.getString(R.string.pause).equals(str)) {
                        MapListener.get().pauseLoad(currentBean.cityID);
                    } else {
                        MapListener.get().startLoad(currentBean.cityID);
                    }
                    upDateInfo();
                }
            }

            @Override
            public void delete() {
                if (currentBean != null) {
                    MapListener.get().removeMap(currentBean.cityID);
                    allCity.remove(currentBean);
                    changNotice();
                    adapter.notifyDataSetChanged();
//                    if (MCApp.getInstance().locationService.getCurrentBDLocation() != null
//                            && MCApp.getInstance().locationService.getCurrentBDLocation().getCityCode() != null) {
//                        if (currentBean.cityID == Integer.parseInt(MCApp.getInstance().locationService.getCurrentBDLocation().getCityCode())) {
//                            ll_current_city.setVisibility(View.VISIBLE);
//                        }
//                    }
                }
            }

            @Override
            public void update() {
                if (currentBean != null) {
                    MapListener.get().updateMap(currentBean.cityID);
                    upDateInfo();
                }
            }
        });
        adapter = new LiteBaseAdapter<>(
                this,
                allCity,
                OfflineLocalMapHolder.class,
                R.layout.item_offline_map_local,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentBean = (LocalOfflineMapBean) v.getTag(R.id.offline_bean);

                        if (dialog != null) {
                            String str = AppUtils.getString(R.string.pause);
                            if (currentBean.status == SUSPENDED ||
                                    currentBean.status == eOLDSWifiError ||
                                    currentBean.status == eOLDSNetError) {
                                str = AppUtils.getString(R.string.cont);
                            }
                            dialog.showContinue(currentBean.ratio < 100, str);
                            dialog.show();
                        }

                    }
                }, "");
        rv_map_list.setAdapter(adapter);
        rv_map_list.setLayoutManager(new LinearLayoutManager(this));
    }

    private void changNotice() {
        if (allCity.size() > 0) {
            tv_map_status.setVisibility(View.VISIBLE);
        } else {
            tv_map_status.setVisibility(View.GONE);
        }
    }

    /**
     * 更新数据
     */
    private void upDateInfo() {
        MKOLUpdateElement temp = MapListener.get().getMkOfflineMap().getUpdateInfo(currentBean.cityID);
        currentBean.size = temp.size;
        currentBean.status = temp.status;
        currentBean.ratio = temp.ratio;
        changNotice();
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MKOLUpdateElement location) {
        if (allID.contains(location.cityID + "")) {
            int pos = allID.indexOf(location.cityID + "");
            allCity.get(pos).ratio = location.ratio;
            allCity.get(pos).status = location.status;
            allCity.get(pos).size = location.size;
            adapter.notifyItemChanged(pos);
        }
    }


    @Override
    protected void onDestroy() {
        int cityid = 0;
        MKOLUpdateElement temp = MapListener.get().getMkOfflineMap().getUpdateInfo(cityid);
        if (temp != null && temp.status == MKOLUpdateElement.DOWNLOADING) {
            MapListener.get().pauseLoad(cityid);
        }
        /** * 退出时，销毁离线地图模块 */
        MapListener.get().destroy();
        super.onDestroy();
    }

    void initData() {
        allCity.clear();
        allID.clear();

        ArrayList<MKOLUpdateElement> temps = MapListener.get().getMkOfflineMap().getAllUpdateInfo();

        if (temps != null) {
//            boolean isCurrent = false;
            for (MKOLUpdateElement temp : temps) {
                allID.add(temp.cityID + "");
                LocalOfflineMapBean mapBean = new LocalOfflineMapBean();
                mapBean.cityID = temp.cityID;
                mapBean.cityName = temp.cityName;
                mapBean.ratio = temp.ratio;
                mapBean.status = temp.status;
                mapBean.geoPt = temp.geoPt;
                mapBean.size = temp.size;
                mapBean.serversize = temp.serversize;
                mapBean.level = temp.level;
                mapBean.update = temp.update;

//                if (MCApp.getInstance().locationService.getCurrentBDLocation() != null
//                        && MCApp.getInstance().locationService.getCurrentBDLocation().getCityCode() != null) {
//                    if (temp.cityID == Integer.parseInt(MCApp.getInstance().locationService.getCurrentBDLocation().getCityCode())) {
//                        mapBean.isCurrent = true;
//                        isCurrent = true;
//                    } else {
//                        mapBean.isCurrent = false;
//                    }
//                }
                allCity.add(mapBean);
            }
//            ll_current_city.setVisibility(isCurrent ? View.GONE : View.VISIBLE);
//        } else {
//            ll_current_city.setVisibility(View.VISIBLE);
        }

        if (allCity.isEmpty()) {
            rv_map_list.setVisibility(View.GONE);
            iv_empty_view.setVisibility(View.VISIBLE);
            tv_map_status.setVisibility(View.GONE);
        } else {
            rv_map_list.setVisibility(View.VISIBLE);
            iv_empty_view.setVisibility(View.GONE);
            tv_map_status.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

}

