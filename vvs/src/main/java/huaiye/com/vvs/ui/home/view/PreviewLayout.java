package huaiye.com.vvs.ui.home.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.baidu.mapapi.model.LatLng;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkNotifyCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyGPSStatus;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.social.CNotifyUserStatus;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.CloseMeetActivity;
import huaiye.com.vvs.bus.CloseTalkVideoActivity;
import huaiye.com.vvs.bus.CreateTalkAndVideo;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.recycle.SafeLinearLayoutManager;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.ChangeUserBean;
import huaiye.com.vvs.dao.msgs.ChatUtil;
import huaiye.com.vvs.dao.msgs.VssMessageListBean;
import huaiye.com.vvs.dao.msgs.VssMessageListMessages;
import huaiye.com.vvs.map.baidu.GPSLocation;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.contacts.ContactsApi;
import huaiye.com.vvs.models.contacts.bean.PersonBean;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.ui.chat.ChatActivity;
import huaiye.com.vvs.ui.home.MainActivity;
import huaiye.com.vvs.ui.home.holder.PreviewListHolder;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: 首页分两种模式,一种是地图模式,一种不显示地图直接显示人员列表,在设置中切换显示模式
 */

public class PreviewLayout extends FrameLayout implements View.OnClickListener {

    OnChangeChannelListener listener;
    RecyclerView rv_list;
    SwipeRefreshLayout refresh_view;

    LiteBaseAdapter<PersonModelBean> adapter;
    ArrayList<PersonModelBean> datas = new ArrayList<>();

    public PreviewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PreviewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);

        View view = LayoutInflater.from(context).inflate(R.layout.main_preview_layout, null);
        rv_list = view.findViewById(R.id.rv_list);
        refresh_view = view.findViewById(R.id.refresh_view);

        addView(view);

        adapter = new LiteBaseAdapter<>(context,
                datas,
                PreviewListHolder.class,
                R.layout.item_preview_list_user,
                new OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        final PersonModelBean bean = (PersonModelBean) view.getTag();
                        if (view.getId() == R.id.tv_zhihui) {
                            ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
                            sessionUserList.add(new SendUserBean(bean.strUserID, bean.strDomainCode, bean.strUserName));
                            sessionUserList.add(new SendUserBean(AppDatas.Auth().getUserID() + "", AppDatas.Auth().getDomainCode(), AppDatas.Auth().getUserName()));

                            List<VssMessageListBean> allBean = VssMessageListMessages.get().getMessages();
                            VssMessageListBean oldBean = null;
                            for (VssMessageListBean temp : allBean) {
                                if (temp.sessionUserList.size() == 2 &&
                                        (temp.sessionUserList.get(0).strUserID.equals(sessionUserList.get(0).strUserID) || temp.sessionUserList.get(0).strUserID.equals(sessionUserList.get(1).strUserID)
                                                && (temp.sessionUserList.get(1).strUserID.equals(sessionUserList.get(0).strUserID) || temp.sessionUserList.get(1).strUserID.equals(sessionUserList.get(1).strUserID)))) {
                                    oldBean = temp;
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
                        } else if (view.getId() == R.id.tv_watch) {
                            if (AppUtils.isTalk || AppUtils.isMeet || AppUtils.isVideo) {
                                AppUtils.showMsg(false, false);
                                return;
                            }
                            ChatUtil.get().reqGuanMo(bean.strUserID, bean.strDomainCode, bean.strUserName);
                        } else if (AppUtils.isMeet || AppUtils.isTalk || AppUtils.isVideo) {
                            ((MainActivity) getContext()).getLogicDialog()
                                    .setTitleText(AppUtils.getString(R.string.notice))
                                    .setMessageText(AppUtils.getString(R.string.other_diaodu))
                                    .setConfirmClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (AppUtils.isMeet) {
                                                EventBus.getDefault().post(new CloseMeetActivity("PreviewLayout 102 "));
                                            } else if (AppUtils.isTalk) {
                                                ((MainActivity) getContext()).waitAcceptLayout.closeWaitViewAll();
                                            } else if (AppUtils.isVideo) {
                                                EventBus.getDefault().post(new CloseTalkVideoActivity("PreviewLayout 107 "));
                                            }
                                            doDiaoduDeal(view, bean);
                                        }
                                    })
                                    .show();
                            return;
                        }

                        doDiaoduDeal(view, bean);
                    }
                }, "");
        rv_list.setLayoutManager(new SafeLinearLayoutManager(context));
        rv_list.setAdapter(adapter);

        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHttpData("150");
            }
        });

        getHttpData("154");

        // 监听 用户状态
        HYClient.getModule(ApiSocial.class).observeUserStatus(new SdkNotifyCallback<CNotifyUserStatus>() {
            @Override
            public void onEvent(final CNotifyUserStatus data) {
                if(PreviewLayout.this.getVisibility() != VISIBLE) {
                    return;
                }
                boolean hasThis = false;
                for (PersonModelBean temp : datas) {
                    if (temp.strUserID.equals(data.strUserID)) {
                        hasThis = true;
                        if (data.isOnline()) {
                            temp.nStatus = PersonModelBean.STATUS_ONLINE_IDLE;
                            if (data.isCapturing()) {
                                temp.nStatus = PersonModelBean.STATUS_ONLINE_CAPTURING;
                            }
                            if (data.isTalking()) {
                                temp.nStatus = PersonModelBean.STATUS_ONLINE_TALKING;
                            }
                            if (data.isMeeting()) {
                                temp.nStatus = PersonModelBean.STATUS_ONLINE_MEETING;
                            }
                            if (data.isTrunkSpeaking()){
                                temp.nStatus = PersonModelBean.STATUS_ONLINE_TRUNK_SPEAKING;
                            }
                        } else {
                            temp.nStatus = PersonModelBean.STATUS_OFFLINE;
                            datas.remove(temp);
                        }
                        break;
                    }
                }

                if (hasThis) {
                    if (data.isOnline()) {
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    if (data.isOnline()) {
                        getHttpData("190");
                    }
                }
            }
        });

    }

    public void refBean(ChangeUserBean bean) {
        for (PersonModelBean temp : datas) {
            if (temp.strUserID.equals(bean.strModifyUserID) && temp.strDomainCode.equals(bean.strModifyUserDomainCode)) {
                temp.strUserName = bean.strModifyUserName;
                temp.nPriority = bean.nPriority;
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void notifyPersonInfoChange(CNotifyGPSStatus cNotifyGPSStatus) {
        boolean find = false;
        for (int i = 0; i < datas.size(); i++) {
            PersonModelBean personModelBean = datas.get(i);
            //找到了,更新坐标
            if (personModelBean.strUserID.equals(cNotifyGPSStatus.strObjID)) {
                find = true;
                personModelBean.dLatitude = cNotifyGPSStatus.rGPSInfo.fLatitude;
                personModelBean.dLongitude = cNotifyGPSStatus.rGPSInfo.fLongitude;
                adapter.notifyDataSetChanged();
                break;
            }
        }

        //没找到,且不是自己 全部更新
        if (!find) {
            //拉完数据把选择设置下
            getHttpData("226");
        }
    }


    /**
     * 执行调度
     *
     * @param v
     * @param bean
     */
    private void doDiaoduDeal(View v, PersonModelBean bean) {
        switch (v.getId()) {
            case R.id.tv_phone:
                EventBus.getDefault().post(new CreateTalkAndVideo(false,
                        bean.strDomainCode,
                        bean.strUserID,
                        bean.strUserName,
                        null,
                        "preview 230"));
                break;
            case R.id.tv_video:
                EventBus.getDefault().post(new CreateTalkAndVideo(true,
                        bean.strDomainCode,
                        bean.strUserID,
                        bean.strUserName,
                        null,
                        "preview 234"));
                break;
            case R.id.tv_meet:

                ArrayList<CStartMeetingReq.UserInfo> allUser = new ArrayList<>();

                CStartMeetingReq.UserInfo user = new CStartMeetingReq.UserInfo();
                user.setDevTypeUser();
                user.strUserDomainCode = bean.strDomainCode;
                user.strUserID = bean.strUserID;
                user.strUserName = bean.strUserName;
                allUser.add(user);

                //发起列表加上自己
                CStartMeetingReq.UserInfo userBean = new CStartMeetingReq.UserInfo();
                userBean.setDevTypeUser();
                userBean.strUserDomainCode = AppDatas.Auth().getDomainCode();
                userBean.strUserID = AppDatas.Auth().getUserID()+"";
                userBean.strUserName =AppDatas.Auth().getUserName();
                allUser.add(userBean);

                ((MainActivity) getContext()).sendCreateMeetMsg(allUser);

                break;
            default:
                bean.isSelected = !bean.isSelected;
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void getHttpData(String from) {
        if(PreviewLayout.this.getVisibility() != VISIBLE) {
            return;
        }
        System.out.println("cccccccccccccccccccccccccc getHttpData " + from);
        ContactsApi.get().getPerson(-1, 9999, -1, 0, new ModelCallback<PersonBean>() {
            @Override
            public void onSuccess(PersonBean personBean) {

                datas.clear();
                if (personBean.userList != null) {

                    for (PersonModelBean temp : personBean.userList) {
                        if (temp.nStatus > PersonModelBean.STATUS_OFFLINE && !temp.strUserID.equals(AppDatas.Auth().getUserID() + "")) {
                            LatLng old = new LatLng(temp.dLatitude, temp.dLongitude);
                            LatLng newLatLng = GPSLocation.convertGPSToBaidu(old);
                            temp.dLongitude = newLatLng.longitude;
                            temp.dLatitude = newLatLng.latitude;
                            datas.add(temp);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

                refresh_view.setRefreshing(false);
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                refresh_view.setRefreshing(false);
                Logger.log("MainPresent   loadPerson   失败");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void setListener(OnChangeChannelListener listener) {
        this.listener = listener;
    }

    public interface OnChangeChannelListener {
        void onChangeChannel(TrunkChannelBean bean);

        void onStopSpeak();

        void start();
    }
}
