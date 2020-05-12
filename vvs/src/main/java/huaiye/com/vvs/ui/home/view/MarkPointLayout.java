package huaiye.com.vvs.ui.home.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkNotifyCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyGPSStatus;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.social.CNotifyUserStatus;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.CloseMeetActivity;
import huaiye.com.vvs.bus.CloseTalkVideoActivity;
import huaiye.com.vvs.bus.CreateMeet;
import huaiye.com.vvs.bus.CreateTalkAndVideo;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.rx.RxUtils;
import huaiye.com.vvs.common.views.DropDownAnimator;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.ChangeUserBean;
import huaiye.com.vvs.dao.msgs.ChatUtil;
import huaiye.com.vvs.dao.msgs.VssMessageListBean;
import huaiye.com.vvs.dao.msgs.VssMessageListMessages;
import huaiye.com.vvs.map.baidu.appcluster.MyCluster;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.device.bean.DevicePlayerBean;
import huaiye.com.vvs.models.map.MapApi;
import huaiye.com.vvs.ui.chat.ChatActivity;
import huaiye.com.vvs.ui.device.DevicePlayRealActivity;
import huaiye.com.vvs.ui.home.MainActivity;

import static huaiye.com.vvs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: ActionBarLayout
 */

public class MarkPointLayout extends FrameLayout implements View.OnClickListener {

    OnMenuListener listener;

    TextView tv_type_info;
    TextView tv_address;
    TextView tv_lat;
    TextView tv_info;

    View tv_phone;
    View tv_video;
    View tv_meet;
    View tv_watch;
    View tv_zhihui;

    View ll_menu;
    View ll_info_wrap;

    MyCluster currentInfo;

    DropDownAnimator dropDownAnimator;

    public MarkPointLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MarkPointLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);

        View view = LayoutInflater.from(context).inflate(R.layout.mark_click_menu_user, null);
        addView(view);

        tv_type_info = view.findViewById(R.id.tv_type_info);
        tv_address = view.findViewById(R.id.tv_address);
        tv_lat = view.findViewById(R.id.tv_lat);
        tv_info = view.findViewById(R.id.tv_info);

        tv_phone = view.findViewById(R.id.tv_phone);
        tv_video = view.findViewById(R.id.tv_video);
        tv_meet = view.findViewById(R.id.tv_meet);
        tv_watch = view.findViewById(R.id.tv_watch);
        tv_zhihui = view.findViewById(R.id.tv_zhihui);
        ll_menu = view.findViewById(R.id.ll_menu);
        ll_info_wrap = view.findViewById(R.id.ll_info_wrap);

        tv_phone.setOnClickListener(this);
        tv_video.setOnClickListener(this);
        tv_meet.setOnClickListener(this);
        tv_watch.setOnClickListener(this);
        tv_zhihui.setOnClickListener(this);

        ll_info_wrap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //仅仅是拦截点击消息 不让他传递到地图界面上
            }
        });

        dropDownAnimator = new DropDownAnimator(this, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220, getResources().getDisplayMetrics()));
        // 监听 用户状态
        HYClient.getModule(ApiSocial.class).observeUserStatus(new SdkNotifyCallback<CNotifyUserStatus>() {
            @Override
            public void onEvent(final CNotifyUserStatus data) {
                if (currentInfo != null && currentInfo.bean.personModelBean != null && currentInfo.bean.personModelBean.strUserID.equals(data.strUserID)) {
                    if (data.isOnline()) {
                        currentInfo.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_IDLE;
                        if (data.isCapturing()) {
                            currentInfo.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_CAPTURING;
                        }
                        if (data.isTalking()) {
                            currentInfo.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_TALKING;
                        }
                        if (data.isMeeting()) {
                            currentInfo.bean.personModelBean.nStatus = PersonModelBean.STATUS_ONLINE_MEETING;
                        }
                        if (data.isTrunkSpeaking()){
                            currentInfo.bean.personModelBean.nStatus  = PersonModelBean.STATUS_ONLINE_TRUNK_SPEAKING;
                        }
                    } else {
                        currentInfo.bean.personModelBean.nStatus = PersonModelBean.STATUS_OFFLINE;
                    }
                    if (data.nOnline == -1) {
                        closeThisView();
                    } else {
                        renderView();
                    }
                }
            }
        });

    }

    public void closeThisView() {
        currentInfo = null;
        Logger.debug("MarkPointLayout  closeThisView");
        dropDownAnimator.hide();
//        setVisibility(View.INVISIBLE);
    }

    public void updateView(CNotifyGPSStatus cNotifyGPSStatus) {
        if (currentInfo.bean.personModelBean != null) {
            currentInfo.bean.personModelBean.dLatitude = cNotifyGPSStatus.rGPSInfo.fLatitude;
            currentInfo.bean.personModelBean.dLongitude = cNotifyGPSStatus.rGPSInfo.fLongitude;
            currentInfo.bean.personModelBean.strLastLoginTime = cNotifyGPSStatus.rGPSInfo.strCollectTime;

        } else if (currentInfo.bean.deviceBean != null) {
            currentInfo.bean.deviceBean.fLatitude = cNotifyGPSStatus.rGPSInfo.fLatitude;
            currentInfo.bean.deviceBean.fLongitude = cNotifyGPSStatus.rGPSInfo.fLongitude;

        } else if (currentInfo.bean.markModelBean != null) {
            currentInfo.bean.markModelBean.dLatitude = cNotifyGPSStatus.rGPSInfo.fLatitude;
            currentInfo.bean.markModelBean.dLongitude = cNotifyGPSStatus.rGPSInfo.fLongitude;
        }
        renderView();
        //更新的话 不需要动画
//        dropDownAnimator.show();
        setVisibility(View.VISIBLE);

        Logger.debug("MarkPointLayout  updateView");

    }

    /**
     * 当前是否正在展示这个id相关信息
     *
     * @param id
     * @return
     */
    public boolean isShowing(String id) {
        if (currentInfo == null) {
            return false;
        }
        if (currentInfo.bean.personModelBean != null) {
            return currentInfo.bean.personModelBean.strUserID.equals(id);
        } else if (currentInfo.bean.deviceBean != null) {
            String deviceID = currentInfo.bean.deviceBean.strDomainCode + currentInfo.bean.deviceBean.strChannelCode;
            return deviceID.equals(id);
        } else if (currentInfo.bean.markModelBean != null) {
            return (currentInfo.bean.markModelBean.nMarkID + "").equals(id);
        }
        return false;
    }

    public void showInfo(MyCluster info) {
        if (info.bean.domainModelBean != null) return;

        currentInfo = info;
        renderView();
        dropDownAnimator.show();
//        setVisibility(View.VISIBLE);

        Logger.debug("MarkPointLayout  showInfo");

    }


    private void renderView() {
        getAddressDistance();

        if (currentInfo != null) {

            ((MainActivity) getContext()).animateMapStatus(MapStatusUpdateFactory.newLatLng(currentInfo.getPosition()));

            tv_address.setText(AppUtils.getString(R.string.load_quick));

            double lat = 0;
            double lng = 0;
            if (((MainActivity) getContext()).currentMapData != null) {
                lat = ((MainActivity) getContext()).currentMapData.latitude;
                lng = ((MainActivity) getContext()).currentMapData.longitude;
            }
            double showLatitude  = BigDecimal.valueOf(currentInfo.bean.latLng.latitude).setScale(6,BigDecimal.ROUND_DOWN).doubleValue();
            double showLongitude = BigDecimal.valueOf(currentInfo.bean.latLng.longitude).setScale(6,BigDecimal.ROUND_DOWN).doubleValue();

            if (currentInfo.bean.personModelBean != null) {
                tv_phone.setVisibility(VISIBLE);
                tv_video.setVisibility(VISIBLE);
                tv_meet.setVisibility(VISIBLE);
                if (currentInfo.bean.personModelBean.nPriority > AppDatas.Auth().getPriority()) {
                    tv_watch.setVisibility(VISIBLE);
                } else {
                    tv_watch.setVisibility(GONE);
                }
                tv_zhihui.setVisibility(VISIBLE);
                if (currentInfo.bean.personModelBean.strUserID.equals(AppDatas.Auth().getUserID() + "")) {
                    ll_menu.setVisibility(GONE);
                } else {
                    ll_menu.setVisibility(VISIBLE);
                }

                tv_type_info.setText(currentInfo.bean.personModelBean.strUserName + "(ID:" + currentInfo.bean.personModelBean.strUserID + ")");
                //给用户展示的全是百度坐标,所以直接用百度坐标计算和显示
                tv_lat.setText(AppUtils.getString(R.string.weidu) + showLatitude + ",  " + AppUtils.getString(R.string.jingdu) + showLongitude);
                Logger.debug("GPSStatus MarkPoint lat " + currentInfo.bean.personModelBean.dLatitude + "," + currentInfo.bean.personModelBean.dLongitude);
                Logger.debug("GPSStatus MarkPoint my  lat " + lat + "," + lng);
                Long distance = Math.round(DistanceUtil.getDistance(
                        currentInfo.bean.latLng,
                        new LatLng(lat, lng)));
                tv_info.setText("(" + AppUtils.getString(R.string.distance_me) + distance
                        + AppUtils.getString(R.string.distance_dan) + "/" + currentInfo.bean.personModelBean.strLastLoginTime + AppUtils.getString(R.string.time_ref_location) + ")");
            } else if (currentInfo.bean.deviceBean != null) {
                ll_menu.setVisibility(VISIBLE);
                tv_phone.setVisibility(GONE);
                tv_video.setVisibility(GONE);
                tv_meet.setVisibility(GONE);
                tv_watch.setVisibility(VISIBLE);
                tv_zhihui.setVisibility(GONE);

                tv_type_info.setText(currentInfo.bean.deviceBean.strChannelName + "(ID:" + currentInfo.bean.deviceBean.strChannelCode + ")");



//                tv_lat.setText(AppUtils.getString(R.string.weidu) + currentInfo.bean.latLng.latitude + "," + AppUtils.getString(R.string.jingdu) + currentInfo.bean.latLng.longitude);
                tv_lat.setText(AppUtils.getString(R.string.weidu) + showLatitude + ",  " + AppUtils.getString(R.string.jingdu) + showLongitude);

                //给用户展示的全是百度坐标,所以直接用百度坐标计算和显示
                Long distance = Math.round(DistanceUtil.getDistance(
                        currentInfo.bean.latLng,
                        new LatLng(lat, lng)));

                tv_info.setText("(" + AppUtils.getString(R.string.distance_me) + distance + AppUtils.getString(R.string.distance_dan) + ")");
            } else if (currentInfo.bean.markModelBean != null) {
                ll_menu.setVisibility(GONE);

                tv_type_info.setText(currentInfo.bean.markModelBean.strMarkName + "(ID:" + currentInfo.bean.markModelBean.nMarkID + ")");
//                tv_lat.setText(AppUtils.getString(R.string.weidu) + currentInfo.bean.latLng.latitude + "," + AppUtils.getString(R.string.jingdu) + currentInfo.bean.latLng.longitude);
                tv_lat.setText(AppUtils.getString(R.string.weidu) + showLatitude + ",  " + AppUtils.getString(R.string.jingdu) + showLongitude);

                //给用户展示的全是百度坐标,所以直接用百度坐标计算和显示
                Long distance = Math.round(DistanceUtil.getDistance(
                        currentInfo.bean.latLng,
                        new LatLng(lat, lng)));

                tv_info.setText("(" + AppUtils.getString(R.string.distance_me) + distance + AppUtils.getString(R.string.distance_dan) + ")");
            } else if (currentInfo.bean.p2pDeviceBean != null) {
                ll_menu.setVisibility(VISIBLE);
                tv_phone.setVisibility(GONE);
                tv_video.setVisibility(VISIBLE);
                tv_meet.setVisibility(GONE);
                tv_watch.setVisibility(VISIBLE);
                tv_zhihui.setVisibility(GONE);

                tv_type_info.setText(currentInfo.bean.p2pDeviceBean.m_strName + "(IP:" + currentInfo.bean.p2pDeviceBean.m_strIP + ")");
                tv_lat.setText(AppUtils.getString(R.string.weidu) + showLatitude + ",  " + AppUtils.getString(R.string.jingdu) + showLongitude);
//                tv_lat.setText(AppUtils.getString(R.string.weidu) + currentInfo.bean.latLng.latitude + "," + AppUtils.getString(R.string.jingdu) + currentInfo.bean.latLng.longitude);

            }
        }
    }

    private void getAddressDistance() {

        if (currentInfo != null) {
            LatLng latLng = null;
            if (currentInfo.bean.personModelBean != null) {
                latLng = new LatLng(currentInfo.bean.personModelBean.dLatitude, currentInfo.bean.personModelBean.dLongitude);
            } else if (currentInfo.bean.deviceBean != null) {
                latLng = new LatLng(currentInfo.bean.deviceBean.fLatitude, currentInfo.bean.deviceBean.fLongitude);

            } else if (currentInfo.bean.markModelBean != null) {
                latLng = new LatLng(currentInfo.bean.markModelBean.dLatitude, currentInfo.bean.markModelBean.dLongitude);
            } else if (currentInfo.bean.p2pDeviceBean != null) {
                latLng = new LatLng(currentInfo.bean.latLng.latitude, currentInfo.bean.latLng.longitude);
            }

            MapApi.get().getUserAddress(latLng, new OnGetGeoCoderResultListener() {

                //经纬度转换成地址

                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    } else {
                        tv_address.setText(result.getAddress());
                    }
                }

                @Override
                public void onGetGeoCodeResult(GeoCodeResult result) {
                }
            });
        }

    }


    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.tv_zhihui) {
            if (currentInfo != null) {
                if (currentInfo.bean.personModelBean != null) {
                    ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
                    sessionUserList.add(new SendUserBean(currentInfo.bean.personModelBean.strUserID, currentInfo.bean.personModelBean.strDomainCode, currentInfo.bean.personModelBean.strUserName));
                    sessionUserList.add(new SendUserBean(AppDatas.Auth().getUserID() + "", AppDatas.Auth().getDomainCode(), AppDatas.Auth().getUserName()));

                    List<VssMessageListBean> allBean = VssMessageListMessages.get().getMessages();
                    VssMessageListBean oldBean = null;
                    for (VssMessageListBean bean : allBean) {
                        if (bean.sessionUserList.size() == 2 &&
                                (bean.sessionUserList.get(0).strUserID.equals(sessionUserList.get(0).strUserID) || bean.sessionUserList.get(0).strUserID.equals(sessionUserList.get(1).strUserID)
                                        && (bean.sessionUserList.get(1).strUserID.equals(sessionUserList.get(0).strUserID) || bean.sessionUserList.get(1).strUserID.equals(sessionUserList.get(1).strUserID)))) {
                            oldBean = bean;
                            break;
                        }
                    }

                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    if (oldBean != null) {
                        intent.putExtra("listBean", oldBean);
                    } else {
                        intent.putExtra("sessionUserList", sessionUserList);
                    }

                    getContext().startActivity(intent);
                }
            }
        } else if (view.getId() == R.id.tv_watch) {
            if (currentInfo != null) {

                if (AppUtils.isTalk || AppUtils.isMeet || AppUtils.isVideo) {
                    AppUtils.showMsg(false, false);
                    return;
                }

                if (currentInfo.bean.personModelBean != null) {
                    if (currentInfo.bean.personModelBean.nStatus == PersonModelBean.STATUS_OFFLINE) {
                        showToast(AppUtils.getString(R.string.offline_other));
                        return;
                    }
                    ChatUtil.get().reqGuanMo(currentInfo.bean.personModelBean.strUserID, currentInfo.bean.personModelBean.strDomainCode, currentInfo.bean.personModelBean.strUserName);
                } else if (currentInfo.bean.deviceBean != null) {
                    DevicePlayerBean bean = currentInfo.bean.deviceBean;
                    Intent intent = new Intent(getContext(), DevicePlayRealActivity.class);
                    intent.putExtra("data", bean);
                    getContext().startActivity(intent);
                } else if (currentInfo.bean.p2pDeviceBean != null) {
                    EventBus.getDefault().post(currentInfo.bean.p2pDeviceBean);
                }
            }
        } else if (AppUtils.isMeet || AppUtils.isTalk || AppUtils.isVideo) {
            ((MainActivity) getContext()).getLogicDialog()
                    .setTitleText(AppUtils.getString(R.string.notice))
                    .setMessageText(AppUtils.getString(R.string.other_diaodu))
                    .setConfirmClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (AppUtils.isMeet) {
                                EventBus.getDefault().post(new CloseMeetActivity("MarkPointLayout 245 "));
                            } else if (AppUtils.isTalk) {
                                ((MainActivity) getContext()).waitAcceptLayout.closeWaitViewAll();
                            } else if (AppUtils.isVideo) {
                                EventBus.getDefault().post(new CloseTalkVideoActivity("MarkPointLayout 250 "));
                            }
                            new RxUtils<>().doDelayOn(200, new RxUtils.IMainDelay() {
                                @Override
                                public void onMainDelay() {
                                    doDiaoduDeal(view);
                                }
                            });

                        }
                    }).show();
            return;
        }
        doDiaoduDeal(view);
    }


    /**
     * 执行调度
     *
     * @param v
     * @param v
     */
    private void doDiaoduDeal(View v) {
        switch (v.getId()) {
            case R.id.tv_phone:
                if (currentInfo != null) {
                    if (currentInfo.bean.personModelBean != null) {
                        if (currentInfo.bean.personModelBean.nStatus == PersonModelBean.STATUS_OFFLINE) {
                            showToast(AppUtils.getString(R.string.offline_other));
                            return;
                        }
                        EventBus.getDefault().post(new CreateTalkAndVideo(false,
                                currentInfo.bean.personModelBean.strDomainCode,
                                currentInfo.bean.personModelBean.strUserID,
                                currentInfo.bean.personModelBean.strUserName,
                                null,
                                "Mark 429"));
                    }
                }
                break;
            case R.id.tv_video:
                if (currentInfo != null) {
                    if (currentInfo.bean.personModelBean != null) {
                        if (currentInfo.bean.personModelBean.nStatus == PersonModelBean.STATUS_OFFLINE) {
                            showToast(AppUtils.getString(R.string.offline_other));
                            return;
                        }
                        EventBus.getDefault().post(new CreateTalkAndVideo(true,
                                currentInfo.bean.personModelBean.strDomainCode,
                                currentInfo.bean.personModelBean.strUserID,
                                currentInfo.bean.personModelBean.strUserName,
                                null,
                                "Mark 458"));
                    } else if (currentInfo.bean.p2pDeviceBean != null) {
                        EventBus.getDefault().post(new CreateTalkAndVideo(true,
                                "",
                                "",
                                "",
                                currentInfo.bean.p2pDeviceBean,
                                "Mark 458"));
                    }
                }
                break;
            case R.id.tv_meet:
                if (currentInfo != null) {
                    if (currentInfo.bean.personModelBean != null) {
                        if (currentInfo.bean.personModelBean.nStatus == PersonModelBean.STATUS_OFFLINE) {
                            showToast(AppUtils.getString(R.string.offline_other));
                            return;
                        }
                        ArrayList<CStartMeetingReq.UserInfo> allUser = new ArrayList<>();
                        CStartMeetingReq.UserInfo bean = new CStartMeetingReq.UserInfo();
                        bean.setDevTypeUser();
                        bean.strUserDomainCode = currentInfo.bean.personModelBean.strDomainCode;
                        bean.strUserID = currentInfo.bean.personModelBean.strUserID;
                        bean.strUserName = currentInfo.bean.personModelBean.strUserName;
                        allUser.add(bean);
                        EventBus.getDefault().post(new CreateMeet(allUser));
                    }
                }

                break;
        }
    }

    public void refInfo(ChangeUserBean bean) {
        if (getVisibility() == VISIBLE
                && currentInfo != null
                && currentInfo.bean != null
                && currentInfo.bean.personModelBean != null
                && currentInfo.bean.personModelBean.strDomainCode.equals(bean.strModifyUserDomainCode)
                && currentInfo.bean.personModelBean.strUserID.equals(bean.strModifyUserID)) {
            currentInfo.bean.personModelBean.strUserName = bean.strModifyUserName;
            currentInfo.bean.personModelBean.nPriority = bean.nPriority;
            renderView();
        }
    }

    public void setListener(OnMenuListener listener) {
        this.listener = listener;
    }

    public interface OnMenuListener {

    }
}
