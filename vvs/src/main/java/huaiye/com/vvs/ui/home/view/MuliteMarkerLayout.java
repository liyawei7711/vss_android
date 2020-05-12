package huaiye.com.vvs.ui.home.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCaller;
import com.huaiye.sdk.core.SdkNotifyCallback;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdpmsgs.face.CServerNotifyAlarmInfo;
import com.huaiye.sdk.sdpmsgs.social.CNotifyUserStatus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.views.DropDownAnimator;
import huaiye.com.vvs.common.views.pickers.itemdivider.SimpleItemDecoration;
import huaiye.com.vvs.map.baidu.appcluster.MyCluster;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.device.DeviceApi;
import huaiye.com.vvs.ui.home.MainActivity;
import huaiye.com.vvs.ui.home.holder.MuliteMarkerListHolder;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: ActionBarLayout
 */

public class MuliteMarkerLayout extends FrameLayout {

    RecyclerView rv_list;
    View ll_close;

    LiteBaseAdapter<MyCluster> adapter;
    ArrayList<MyCluster> datas = new ArrayList<>();

    DropDownAnimator dropDownAnimator;
    SdkCaller userStatusCaller;
    SdkNotifyCallback<CServerNotifyAlarmInfo> deviceStatusListener;


    public MuliteMarkerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MuliteMarkerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
        View view = LayoutInflater.from(context).inflate(R.layout.main_mulite_marker_msg_layout, null);
        addView(view);
        dropDownAnimator = new DropDownAnimator(this, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,200,getResources().getDisplayMetrics()));

        rv_list = view.findViewById(R.id.rv_list);
        ll_close = view.findViewById(R.id.ll_close);

        adapter = new LiteBaseAdapter<>(context,
                datas,
                MuliteMarkerListHolder.class,
                R.layout.item_mulite_marker_layout,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyCluster bean = (MyCluster) v.getTag();

                        if (bean.bean.domainModelBean != null) return;

                        ((MainActivity) getContext()).mmk_view.closeViewImmediate();
                        ((MainActivity) getContext()).mpl_view.showInfo(bean);
                    }
                }, "");
        rv_list.setLayoutManager(new LinearLayoutManager(context));
        rv_list.setAdapter(adapter);
        rv_list.addItemDecoration(new SimpleItemDecoration(context, ActivityCompat.getColor(context,R.color.divider_cccccc),1));

        ll_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownAnimator.hide();
            }
        });


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDate(ArrayList<MyCluster> bean) {
        datas.clear();
        datas.addAll(bean);
        adapter.notifyDataSetChanged();
        rv_list.getLayoutManager().scrollToPosition(0);
        dropDownAnimator.show();
        startListener();
    }

    public void closeView(){
        stopListener();
        dropDownAnimator.hide();
    }

    public void closeViewImmediate(){
        stopListener();
        setVisibility(View.INVISIBLE);
    }

    public void updateView(){
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    private void startListener(){
        // 监听 用户状态
        userStatusCaller = HYClient.getModule(ApiSocial.class).observeUserStatus(new SdkNotifyCallback<CNotifyUserStatus>() {
            @Override
            public void onEvent(final CNotifyUserStatus data) {
                for (MyCluster temp : datas) {
                    if (temp.bean.personModelBean != null && temp.bean.personModelBean.strUserID.equals(data.strUserID)) {


                        if (data.isOnline()) {
                            temp.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_IDLE;
                            if (data.isCapturing()) {
                                temp.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_CAPTURING;
                            }
                            if (data.isTalking()) {
                                temp.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_TALKING;
                            }
                            if (data.isMeeting()) {
                                temp.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_MEETING;
                            }
                            if (data.isTrunkSpeaking()){
                                temp.bean.personModelBean.nStatus  = PersonModelBean.STATUS_ONLINE_TRUNK_SPEAKING;
                            }
                        } else {
                            temp.bean.personModelBean.nStatus = PersonModelBean.STATUS_OFFLINE;
                        }


                        if (data.nOnline == -1) {
                            datas.remove(temp);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    }

                }
            }
        });

        deviceStatusListener = new SdkNotifyCallback<CServerNotifyAlarmInfo>() {
            @Override
            public void onEvent(CServerNotifyAlarmInfo cServerNotifyAlarmInfo) {
                for (MyCluster temp : datas) {
                    if (temp.bean.deviceBean != null && temp.bean.deviceBean.strDeviceCode.equals(cServerNotifyAlarmInfo.strDeviceCode)) {
                        if (cServerNotifyAlarmInfo.nAlarmType == DeviceApi.NOTIFY_TYPE_DEIVCE_ONLINE) {
                            temp.bean.deviceBean.nOnlineState = 1;
                        } else {
                            temp.bean.deviceBean.nOnlineState = 2;
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    }

                }
            }
        };
        DeviceApi.get().addAlarmListener(deviceStatusListener);
    }
    private void stopListener(){
        if (userStatusCaller != null){
            userStatusCaller.cancel();
            userStatusCaller = null;
        }
        if (deviceStatusListener != null){
            DeviceApi.get().removeAlarmListener(deviceStatusListener);
            deviceStatusListener = null;
        }

    }
}
