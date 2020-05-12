package huaiye.com.vvs.ui.home.present;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.core.SdkCaller;
import com.huaiye.sdk.core.SdkNotifyCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.face.CServerNotifyAlarmInfo;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.social.CNotifyUserStatus;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CGetTrunkChannelInfoRsp;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CQueryTrunkChannelListRsp;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelBean;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelUserBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.CreateMeet;
import huaiye.com.vvs.bus.CreateTalkAndVideo;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.dialog.DeviceListPopupWindow;
import huaiye.com.vvs.common.dialog.GroupListPopupWindow;
import huaiye.com.vvs.common.dialog.LogicDialog;
import huaiye.com.vvs.common.dialog.UserListPopupWindow;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.rx.RxUtils;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.ChangeUserBean;
import huaiye.com.vvs.dao.msgs.ChatUtil;
import huaiye.com.vvs.dao.msgs.VssMessageListBean;
import huaiye.com.vvs.dao.msgs.VssMessageListMessages;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.contacts.ContactsApi;
import huaiye.com.vvs.models.contacts.bean.PersonBean;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.device.DeviceApi;
import huaiye.com.vvs.models.device.bean.DeviceListBean;
import huaiye.com.vvs.models.device.bean.DevicePlayerBean;
import huaiye.com.vvs.ui.channel.ChannelDetailActivity;
import huaiye.com.vvs.ui.chat.ChatActivity;
import huaiye.com.vvs.ui.device.DevicePlayRealActivity;
import huaiye.com.vvs.ui.device.holder.DeviceHolder;
import huaiye.com.vvs.ui.device.holder.GroupHolder;
import huaiye.com.vvs.ui.device.holder.PersonHolder;
import huaiye.com.vvs.ui.home.view.IContactsView;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DeviceListPresent
 */

public class ContactsPresent {
    private final int PERSON = 0;
    private final int GROUP = 1;
    private final int DEVICE = 2;
    IContactsView iView;

    ArrayList<PersonModelBean> personList = new ArrayList<>();
    LiteBaseAdapter<PersonModelBean> personAdapter;
    Map<String, PersonModelBean> selectedAll = new HashMap<>();
//    int total_person;
//    int rel_person;
    int person_order = 0;
    boolean isOnline = true;

    ArrayList<TrunkChannelBean> groupList = new ArrayList<>();
    LiteBaseAdapter<TrunkChannelBean> groupAdapter;
    TrunkChannelBean currentGroup;
    int total_group;
    int group_order = 0;

    ArrayList<DevicePlayerBean> deviceList = new ArrayList<>();
    LiteBaseAdapter<DevicePlayerBean> deviceAdapter;
    int total_device;
    int device_order = 0;

    UserListPopupWindow userListPopupWindow;
    DeviceListPopupWindow deviceListPopupWindow;
    GroupListPopupWindow groupListPopupWindow;

    int index = PERSON;

    boolean isLoad;
    int size = 999;
//    int pagePerson = 1;
//    int pageGroup = 1;
//    int pageDevice = 1;

    SdkCaller observeUserStatus;
    SdkNotifyCallback<CServerNotifyAlarmInfo> deviceStatusListener;

    public ContactsPresent(final IContactsView iView) {
        this.iView = iView;

        userListPopupWindow = new UserListPopupWindow(iView.getContext());
        userListPopupWindow.setConfirmClickListener(new UserListPopupWindow.ConfirmClickListener() {
            @Override
            public void onClickXingShiShengXu(boolean choose) {
                person_order = 0;
                loadPerson(true);
            }

            @Override
            public void onClickXingShiJiangXu(boolean choose) {
                person_order = 1;
                loadPerson(true);
            }

            @Override
            public void onClickOnLine(boolean choose) {
                isOnline = choose;
                loadPerson(true);
            }
        });
        deviceListPopupWindow = new DeviceListPopupWindow(iView.getContext());
        deviceListPopupWindow.setConfirmClickListener(new DeviceListPopupWindow.ConfirmClickListener() {
            @Override
            public void onClickXingShiShengXu(boolean choose) {
                device_order = 0;
                com.huaiye.sdk.logger.Logger.debug("loadDevice from deviceListPopupWindow onClickXingShiShengXu" );
                loadDevice(true);
            }

            @Override
            public void onClickXingShiJiangXu(boolean choose) {
                device_order = 1;
                com.huaiye.sdk.logger.Logger.debug("loadDevice from deviceListPopupWindow onClickXingShiJiangXu" );
                loadDevice(true);
            }

        });
        groupListPopupWindow = new GroupListPopupWindow(iView.getContext());
        groupListPopupWindow.setConfirmClickListener(new GroupListPopupWindow.ConfirmClickListener() {
            @Override
            public void onClickXingShiShengXu(boolean choose) {
                group_order = 0;
                loadGroup(true);
            }

            @Override
            public void onClickXingShiJiangXu(boolean choose) {
                group_order = 1;
                loadGroup(true);
            }

        });
        personAdapter = new LiteBaseAdapter<>(iView.getContext(),
                personList,
                PersonHolder.class,
                R.layout.item_person_holder,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!PersonHolder.selected_mode) {
//                            ((ContactsActivity) iView.getContext()).iv_right_choose.performClick();
                            PersonHolder.selected_mode = !PersonHolder.selected_mode;
                            refAdapter(PersonHolder.selected_mode);
                        }

                        if (!PersonHolder.selected_mode) {
                            return;
                        }

                        PersonModelBean bean = (PersonModelBean) v.getTag();
                        if (selectedAll.containsKey(bean.strUserID)) {
                            selectedAll.remove(bean.strUserID);
                            if (selectedAll.isEmpty()) {
//                                ((ContactsActivity) iView.getContext()).iv_right_choose.performClick();
                                PersonHolder.selected_mode = !PersonHolder.selected_mode;
                                refAdapter(PersonHolder.selected_mode);
                            }
                        } else {
                            selectedAll.put(bean.strUserID, bean);
                        }

                        changeMenu();
                        personAdapter.notifyDataSetChanged();
                    }
                }, selectedAll);
//        personAdapter.setLoadListener(new LiteBaseAdapter.LoadListener() {
//            @Override
//            public boolean isLoadOver() {
//                return !isLoad;
//            }
//
//            @Override
//            public boolean isEnd() {
//                return rel_person >= total_person;
//            }
//
//            @Override
//            public void lazyLoad() {
//                loadPerson(false);
//            }
//        });

        // 监听 用户状态
        observeUserStatus = HYClient.getModule(ApiSocial.class).observeUserStatus(new SdkNotifyCallback<CNotifyUserStatus>() {
            @Override
            public void onEvent(final CNotifyUserStatus data) {
                Logger.debug("ContactsPresent observeUserStatus " + data.toString());
                boolean hasThis = false;
                for (PersonModelBean temp : personList) {
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
                        }
                        if (data.nOnline == -1) {
                            personList.remove(temp);
                        }
                        break;
                    }

                }
                if (!hasThis) {
                    loadPerson(true);
                } else {
                    personAdapter.notifyDataSetChanged();
                }
            }
        });


        // 监听 设备状态
        deviceStatusListener = new SdkNotifyCallback<CServerNotifyAlarmInfo>() {
            @Override
            public void onEvent(final CServerNotifyAlarmInfo data) {
                boolean hasThis = false;
//                DevicePlayerBean offlineDevice = null;
                //设置最新状态,更新adapter
                for (DevicePlayerBean temp : deviceList) {
                    if (temp.strDeviceCode.equals(data.strDeviceCode)) {
                        hasThis = true;
                        if (data.nAlarmType == DeviceApi.NOTIFY_TYPE_DEIVCE_OFFLINE) {
                            temp.nOnlineState = 2;
                        }else {
                            temp.nOnlineState = 1;
                        }
                        deviceAdapter.notifyDataSetChanged();
                        break;
                    }

                }

//                if (offlineDevice != null){
//                    deviceList.remove(offlineDevice);
//                    deviceAdapter.notifyDataSetChanged();
//                }

                if (!hasThis) {
                    loadDevice(true);
                }
            }
        };
        DeviceApi.get().addAlarmListener(deviceStatusListener);

        groupAdapter = new LiteBaseAdapter<>(iView.getContext(),
                groupList,
                GroupHolder.class,
                R.layout.item_group_holder,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TrunkChannelBean bean = (TrunkChannelBean) v.getTag();
                        if (currentGroup == null) {
                            for (TrunkChannelBean temp : groupList) {
                                if (temp == bean) {
                                    temp.extr = true;
                                    currentGroup = bean;
                                } else {
                                    temp.extr = false;
                                }
                            }
                        } else {
                            if (currentGroup == bean) {
                                currentGroup.extr = false;
                                currentGroup = null;
                            } else {
                                for (TrunkChannelBean temp : groupList) {
                                    if (temp == bean) {
                                        temp.extr = true;
                                        currentGroup = bean;
                                    } else {
                                        temp.extr = false;
                                    }
                                }
                            }
                        }

                        changeMenu();
                        groupAdapter.notifyDataSetChanged();
                    }
                }, "");
//        groupAdapter.setLoadListener(new LiteBaseAdapter.LoadListener() {
//            @Override
//            public boolean isLoadOver() {
//                return !isLoad;
//            }
//
//            @Override
//            public boolean isEnd() {
//                return groupList.size() >= total_group;
//            }
//
//            @Override
//            public void lazyLoad() {
//                loadGroup(false);
//            }
//        });

        deviceAdapter = new LiteBaseAdapter<>(iView.getContext(),
                deviceList,
                DeviceHolder.class,
                R.layout.item_device_holder,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        com.huaiye.sdk.logger.Logger.debug("ContactsPresent onClick start");
                        DevicePlayerBean bean = (DevicePlayerBean) v.getTag();
                        if (bean == null){
                            com.huaiye.sdk.logger.Logger.debug("ContactsPresent onClick bean null");
                        }else {
                            com.huaiye.sdk.logger.Logger.debug("ContactsPresent onClick bean " + bean.toString());
                        }
                        Intent intent = new Intent(iView.getContext(), DevicePlayRealActivity.class);
                        intent.putExtra("data", bean);
                        iView.getContext().startActivity(intent);
                    }
                }, "");
//        deviceAdapter.setLoadListener(new LiteBaseAdapter.LoadListener() {
//            @Override
//            public boolean isLoadOver() {
//                com.huaiye.sdk.logger.Logger.debug("deviceAdapter isLoadOver " + isLoad);
//                return !isLoad;
//            }
//
//            @Override
//            public boolean isEnd() {
//                com.huaiye.sdk.logger.Logger.debug("deviceAdapter isEnd " + deviceList.size() + " total " + total_device );
//                return deviceList.size() >= total_device;
//            }
//
//            @Override
//            public void lazyLoad() {
//                com.huaiye.sdk.logger.Logger.debug("loadDevice from deviceAdapter lazyLoad" );
//                loadDevice(false);
//            }
//        });

    }

    public void loadPerson(final boolean isRef) {

//        if (isRef)
//            pagePerson = 1;
//        else
//            pagePerson++;

        isLoad = true;
        ContactsApi.get().getPerson(1, size, 0, person_order,
                new ModelCallback<PersonBean>() {
                    @Override
                    public void onSuccess(PersonBean personBean) {
//                        total_person = personBean.nTotalSize;
                        loadFinish();
                        if (isRef) {
//                            rel_person = 0;
                            personList.clear();
                        }
                        if (personBean.userList != null) {
//                            rel_person += personBean.userList.size();

                            for (PersonModelBean bean : personBean.userList) {
                                if (!bean.strUserID.equals(AppDatas.Auth().getUserID() + "")) {
                                    if (bean.nStatus > PersonModelBean.STATUS_OFFLINE && bean.nSpeaking == 2){
                                        bean.nStatus = PersonModelBean.STATUS_ONLINE_TRUNK_SPEAKING;
                                    }
                                    if (isOnline) {
                                        if (bean.nStatus > PersonModelBean.STATUS_OFFLINE) {
                                            personList.add(bean);
                                        }
                                    } else {
                                        personList.add(bean);
                                    }
                                }
                            }
                        }

                        if (index == PERSON) {
                            changeViewShow(personList.isEmpty());
                        }

                        personAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        loadFinish();
                        iView.getContext().showToast(AppUtils.getString(R.string.get_person_false));
                    }
                });
    }

    public void loadGroup(final boolean isRef) {

//        if (isRef)
//            pageGroup = 1;
//        else
//            pageGroup++;

        isLoad = true;
        HYClient.getModule(ApiTalk.class)
                .queryTrunkChannel(SdkParamsCenter.Talk.QueryTrunkChannel()
                                .setnReverse(group_order)
                                .setnTrunkChannelType(TrunkChannelBean.CHANNLE_TYPE_DEFAULT|TrunkChannelBean.CHANNLE_TYPE_STATIC|TrunkChannelBean.CHANNLE_TYPE_TMP_ING)
                                .setnSize(size),
                        new SdkCallback<CQueryTrunkChannelListRsp>() {
                            @Override
                            public void onSuccess(CQueryTrunkChannelListRsp resp) {
                                total_group = resp.nTotalSize;
                                loadFinish();
                                groupList.clear();
                                ArrayList<TrunkChannelBean> trunkChannelBeans = resp.lstTrunkChannelInfo;
                                if (trunkChannelBeans != null){
                                    for (TrunkChannelBean bean : trunkChannelBeans){
                                        if (bean.nTrunkChannelType != TrunkChannelBean.CHANNLE_TYPE_TMP_FINISH){
                                            groupList.add(bean);
                                        }
                                    }
                                }

                                if (currentGroup != null) {
                                    for (TrunkChannelBean temp : groupList) {
                                        if (temp.nTrunkChannelID == currentGroup.nTrunkChannelID) {
                                            temp.extr = currentGroup.extr;
                                            break;
                                        }
                                    }
                                }

                                if (index == GROUP) {
                                    changeViewShow(groupList.isEmpty());
                                }
                                groupAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(ErrorInfo error) {
                                loadFinish();
                            }
                        });
    }

    /**
     * 加载设备
     */
    public void loadDevice(final boolean isRef) {
        com.huaiye.sdk.logger.Logger.debug("loadDevice start " + isRef);

        isLoad = true;
        DeviceApi.get().getDomainDeviceList(1, 999, device_order, new ModelCallback<DeviceListBean>() {
            @Override
            public void onSuccess(DeviceListBean deviceListBean) {
                Gson gson  = new Gson();
                String strDeviceList = gson.toJson(deviceListBean);
                com.huaiye.sdk.logger.Logger.debug("loadDevice ret =  " + strDeviceList);
                total_device = deviceListBean.nTotalSize;

                loadFinish();

                if (isRef)
                    deviceList.clear();
                if (deviceListBean.deviceList != null
                        && !deviceListBean.deviceList.isEmpty()) {
                    for (DeviceListBean.DeviceBean temp : deviceListBean.deviceList) {
                        for (DeviceListBean.ChannelBean channelBean : temp.channelList) {
                            DevicePlayerBean bean = new DevicePlayerBean();
                            bean.strChannelName = channelBean.strChannelName;
                            bean.strChannelCode = channelBean.strChannelCode;
                            bean.strDeviceCode = temp.strDeviceCode;
                            bean.strDomainCode = temp.strDomainCode;
                            bean.nOnlineState = temp.nOnlineState;
                            if (channelBean.streamList.isEmpty()){
                                continue;
                            }
                            bean.strStreamCode = channelBean.streamList.get(0).strStreamCode;
                            if (channelBean.streamList.size()> 1){
                                bean.strSubStreamCode = channelBean.streamList.get(1).strStreamCode;
                            }

//                            if (channelBean.streamList == null || channelBean.streamList.size() == 0) {
//                                com.huaiye.sdk.logger.Logger.debug("device " + channelBean.strChannelName +" stream is empty " );
//                            }else {
//                                bean.strStreamCode = channelBean.streamList.get(0).strStreamCode;
//                                if (channelBean.streamList.size()> 1){
//                                    bean.strSubStreamCode = channelBean.streamList.get(1).strStreamCode;
//                                }
//                            }
                            deviceList.add(bean);
                        }
                    }

                }
                if (index == DEVICE) {
                    changeViewShow(deviceList.isEmpty());
                }
                com.huaiye.sdk.logger.Logger.debug("device list size = " + deviceList.size());
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                com.huaiye.sdk.logger.Logger.debug("device list failure " +httpResponse.getErrorMessage());
                loadFinish();
                iView.getContext().showToast(AppUtils.getString(R.string.get_device_false));
            }
        });
    }

    /**
     * 语音
     */
    public void phoneClick() {
        switch (index) {
            case PERSON:
                String domain = null;
                String userId = null;
                String userName = null;
                for (Map.Entry<String, PersonModelBean> entry : selectedAll.entrySet()) {

                    PersonModelBean value = entry.getValue();
                    domain = value.strDomainCode;
                    userId = value.strUserID;
                    userName = value.strUserName;
                }
                iView.getContext().finish();
                final String finalDomain = domain;
                final String finalUserId = userId;
                final String finalUserName = userName;
                RxUtils rxUtils = new RxUtils();

                rxUtils.doDelayOn(1000, new RxUtils.IMainDelay() {
                    @Override
                    public void onMainDelay() {
                        EventBus.getDefault().post(new CreateTalkAndVideo(false, finalDomain, finalUserId, finalUserName, null, "ContactsPresent 481"));
                    }
                });
                break;
            case GROUP:
                break;
        }
    }

    /**
     * 指令
     */
    public void zhiHuiClick() {
        List<VssMessageListBean> allBean = VssMessageListMessages.get().getMessages();
        switch (index) {
            case PERSON:
                ArrayList<SendUserBean> sessionUserList = new ArrayList<>();

                //先添加选中的
                for (Map.Entry<String, PersonModelBean> entry : selectedAll.entrySet()) {
                    PersonModelBean value = entry.getValue();
                    sessionUserList.add(new SendUserBean(value.strUserID, value.strDomainCode, value.strUserName));
                }
                //再添加自己,这里创建人肯定是自己
                sessionUserList.add(new SendUserBean(AppDatas.Auth().getUserID() + "", AppDatas.Auth().getDomainCode(), AppDatas.Auth().getUserName()));

                //一对一对话有没有重复创建
                VssMessageListBean oldBean = null;
                if (sessionUserList.size() == 2) {
                    //allbean是本地已经存在的会话列表
                    for (VssMessageListBean bean : allBean) {
                        //本地存在的一个会话中人数为2
                        // 并且两个人与这次即将新建的人id相同
                        if (bean.sessionUserList.size() == 2 &&
                                (bean.sessionUserList.get(0).strUserID.equals(sessionUserList.get(0).strUserID) || bean.sessionUserList.get(0).strUserID.equals(sessionUserList.get(1).strUserID)
                                        && (bean.sessionUserList.get(1).strUserID.equals(sessionUserList.get(0).strUserID) || bean.sessionUserList.get(1).strUserID.equals(sessionUserList.get(1).strUserID)))) {
                            oldBean = bean;
                            break;
                        }
                    }
                }

                //如果有的话就填充listBean
                Intent intent = new Intent(iView.getContext(), ChatActivity.class);
                if (oldBean != null) {
                    intent.putExtra("listBean", oldBean);
                } else {
                    intent.putExtra("sessionUserList", sessionUserList);
                }

                iView.getContext().startActivity(intent);
                break;
            case GROUP:
                if (currentGroup != null) {

                    VssMessageListBean oldGroup = null;
                    for (VssMessageListBean bean : allBean) {
                        if (bean.sessionID.equals(currentGroup.nTrunkChannelID + "")) {
                            oldGroup = bean;
                            break;
                        }
                    }

                    Intent intent1 = new Intent(iView.getContext(), ChatActivity.class);

                    if (oldGroup != null) {
                        intent1.putExtra("listBean", oldGroup);
                    }
                    intent1.putExtra("channelName", currentGroup.strTrunkChannelName);
                    intent1.putExtra("channelId", currentGroup.nTrunkChannelID + "");
                    intent1.putExtra("sessionDomainCode", currentGroup.strTrunkChannelDomainCode + "");
                    intent1.putExtra("isGroup", true);

                    iView.getContext().startActivity(intent1);
                }

                break;
        }
    }


    /**
     * 观察
     */
    public void watchClick() {
        String domain = null;
        String userId = null;
        String userName = null;
        for (Map.Entry<String, PersonModelBean> entry : selectedAll.entrySet()) {
            PersonModelBean value = entry.getValue();
            domain = value.strDomainCode;
            userId = value.strUserID;
            userName = value.strUserName;
        }

        ChatUtil.get().reqGuanMo(userId, domain, userName);
    }

    /**
     * 会议
     */
    public void meetClick() {
        switch (index) {
            case PERSON:
                final ArrayList<CStartMeetingReq.UserInfo> allUser = new ArrayList<>();
                for (Map.Entry<String, PersonModelBean> entry : selectedAll.entrySet()) {

                    PersonModelBean value = entry.getValue();
                    CStartMeetingReq.UserInfo bean = new CStartMeetingReq.UserInfo();
                    bean.setDevTypeUser();
                    bean.strUserDomainCode = value.strDomainCode;
                    bean.strUserID = value.strUserID;
                    bean.strUserName = value.strUserName;
                    allUser.add(bean);
                }

                if (allUser.size() > 10) {
                    final LogicDialog dialog = iView.getContext().getLogicDialog()
                            .setMessageText(AppUtils.getString(R.string.user_more));

                    dialog.setConfirmText(AppUtils.getString(R.string.cancel));
                    dialog.setCancelText(AppUtils.getString(R.string.makesure));

                    dialog.setConfirmClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().post(new CreateMeet(allUser));
                            iView.getContext().finish();
                        }
                    }).setCancelClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            allUser.clear();
                        }
                    }).show();
                } else {
                    EventBus.getDefault().post(new CreateMeet(allUser));
                    iView.getContext().finish();
                }
                break;
            case GROUP:
                getGroupUser(true);
                break;
        }
    }

    /**
     * 频道点击详情
     */
    public void channelClick() {
        if (currentGroup != null) {
            Intent intent = new Intent(iView.getContext(), ChannelDetailActivity.class);
            intent.putExtra("trunkChannelBean", currentGroup);
            iView.getContext().startActivity(intent);
        }
    }

    /**
     * 视频
     */
    public void videoClick() {
        switch (index) {
            case PERSON:
                String domain = null;
                String userId = null;
                String userName = null;
                for (Map.Entry<String, PersonModelBean> entry : selectedAll.entrySet()) {

                    PersonModelBean value = entry.getValue();
                    domain = value.strDomainCode;
                    userId = value.strUserID;
                    userName = value.strUserName;
                }
                final String finalDomain = domain;
                final String finalUserId = userId;
                final String finalUserName = userName;
                new RxUtils<>().doDelayOn(1000, new RxUtils.IMainDelay() {
                    @Override
                    public void onMainDelay() {
                        EventBus.getDefault().post(new CreateTalkAndVideo(true, finalDomain, finalUserId, finalUserName, null, "ContactsPresent 641"));

                    }
                });
                break;
            case GROUP:
                break;
        }
    }

    private void getGroupUser(final boolean isMeet) {
        if (currentGroup != null) {
            HYClient.getModule(ApiTalk.class)
                    .getTrunkChannelInfo(SdkParamsCenter.Talk.GetTrunkChannelInfo()
                                    .setnTrunkChannelID(currentGroup.nTrunkChannelID)
                                    .setStrTrunkChannelDomainCode(currentGroup.strTrunkChannelDomainCode),
                            new SdkCallback<CGetTrunkChannelInfoRsp>() {
                                @Override
                                public void onSuccess(final CGetTrunkChannelInfoRsp cGetTrunkChannelInfoRsp) {
                                    if (cGetTrunkChannelInfoRsp.lstTrunkChannelUser.size() > 10) {
                                        final LogicDialog dialog = iView.getContext().getLogicDialog()
                                                .setMessageText(AppUtils.getString(R.string.user_more));

                                        dialog.setConfirmText(AppUtils.getString(R.string.cancel));
                                        dialog.setCancelText(AppUtils.getString(R.string.makesure));

                                        dialog.setConfirmClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                fromGroupCreateMeet(cGetTrunkChannelInfoRsp, isMeet);
                                            }
                                        }).show();
                                    } else {
                                        fromGroupCreateMeet(cGetTrunkChannelInfoRsp, isMeet);
                                    }
                                }

                                @Override
                                public void onError(ErrorInfo errorInfo) {

                                }
                            });
        }
    }

    /**
     * 群组创建会议
     *
     * @param cGetTrunkChannelInfoRsp
     * @param isMeet
     */
    private void fromGroupCreateMeet(CGetTrunkChannelInfoRsp cGetTrunkChannelInfoRsp, boolean isMeet) {
        ArrayList<TrunkChannelUserBean> lstTrunkChannelUser;
        if (cGetTrunkChannelInfoRsp == null) {
            iView.getContext().showToast(AppUtils.getString(R.string.get_group_false));
            return;
        }
        if (cGetTrunkChannelInfoRsp.lstTrunkChannelUser == null) {
            iView.getContext().showToast(AppUtils.getString(R.string.get_group_false));
            return;
        }
        if (cGetTrunkChannelInfoRsp.lstTrunkChannelUser.size() == 0) {
            iView.getContext().showToast(AppUtils.getString(R.string.get_group_empty));
            return;
        }
        if (isMeet) {
            ArrayList<CStartMeetingReq.UserInfo> allUser = new ArrayList<>();
            for (TrunkChannelUserBean temp : cGetTrunkChannelInfoRsp.lstTrunkChannelUser) {
                CStartMeetingReq.UserInfo bean = new CStartMeetingReq.UserInfo();
                bean.setDevTypeUser();
                bean.strUserDomainCode = temp.strTcUserDomainCode;
                bean.strUserID = temp.strTcUserID;
                bean.strUserName = temp.strTcUserName;
                allUser.add(bean);
            }

            EventBus.getDefault().post(new CreateMeet(allUser));
            iView.getContext().finish();
        } else {
            ArrayList<SendUserBean> sessionUserList = new ArrayList<>();

            for (TrunkChannelUserBean temp : cGetTrunkChannelInfoRsp.lstTrunkChannelUser) {
                sessionUserList.add(new SendUserBean(temp.strTcUserID, temp.strTcUserDomainCode, temp.strTcUserName));
            }
            sessionUserList.add(new SendUserBean(AppDatas.Auth().getUserID() + "", AppDatas.Auth().getDomainCode(), AppDatas.Auth().getUserName()));
            Intent intent = new Intent(iView.getContext(), ChatActivity.class);
            intent.putExtra("sessionUserList", sessionUserList);
            intent.putExtra("channelName", currentGroup.strTrunkChannelName);
            intent.putExtra("sessionID", currentGroup.nTrunkChannelID + "");
            intent.putExtra("sessionDomainCode", currentGroup.strTrunkChannelDomainCode + "");
            intent.putExtra("isGroup", true);
            iView.getContext().startActivity(intent);
        }
    }

    private void loadFinish() {
        isLoad = false;
        iView.getRefView().setRefreshing(false);
    }

    public LiteBaseAdapter getDeviceAdapter() {
        index = DEVICE;
        isLoad = false;
        changeMenu();
        return deviceAdapter;
    }

    public RecyclerView.Adapter getPersonAdapter() {
        index = PERSON;
        isLoad = false;
        changeMenu();
        return personAdapter;
    }

    public RecyclerView.Adapter getGroupAdapter() {
        index = GROUP;
        isLoad = false;
        changeMenu();
        return groupAdapter;
    }

    /**
     * 改变可点击的view
     */
    public void changeMenu() {
        switch (index) {
            case PERSON:
                if (selectedAll.size() == 1) {
                    PersonModelBean value = null;
                    for (Map.Entry<String, PersonModelBean> entry : selectedAll.entrySet()) {
                        value = entry.getValue();
                        break;
                    }
                    iView.personSingle(value.nPriority);
                } else if (selectedAll.size() > 1) {
                    iView.personMulite();
                } else {
                    iView.personNull();
                }
                break;
            case GROUP:
                if (currentGroup == null) {
                    iView.personNull();
                } else {
                    if (currentGroup.extr == null) {
                        iView.personNull();
                    } else {
                        if ((boolean) currentGroup.extr) {
                            iView.groupSingle();
                        } else {
                            iView.personNull();
                        }
                    }
                }
                break;
            case DEVICE:
                break;
        }
    }

    public ArrayList<PersonModelBean> getPersonList() {
        return personList;
    }

    public ArrayList<TrunkChannelBean> getGroupList() {
        return groupList;
    }

    public ArrayList<DevicePlayerBean> getDeviceList() {
        return deviceList;
    }

    public void changeViewShow(boolean isEmpty) {
        if (isEmpty) {
            iView.getEmptyView().setVisibility(View.VISIBLE);
            iView.getListView().setVisibility(View.GONE);
        } else {
            iView.getEmptyView().setVisibility(View.GONE);
            iView.getListView().setVisibility(View.VISIBLE);
        }
    }

    public void showPopu(View v) {
        switch (index) {
            case PERSON:
                userListPopupWindow.showView(v);
                break;
            case GROUP:
                groupListPopupWindow.showView(v);
                break;
            case DEVICE:
                deviceListPopupWindow.showView(v);
                break;
        }

    }

    public void refAdapter(boolean selected) {
        if (!selected) {
            selectedAll.clear();

            changeMenu();
        }
        personAdapter.notifyDataSetChanged();
    }

    /**
     * 改变数据
     *
     * @param bean
     */
    public void refBean(ChangeUserBean bean) {
        for (PersonModelBean temp : personList) {
            if (temp.strUserID.equals(bean.strModifyUserID) && temp.strDomainCode.equals(bean.strModifyUserDomainCode)) {
                temp.strUserName = bean.strModifyUserName;
                temp.nPriority = bean.nPriority;
                break;
            }
        }
        personAdapter.notifyDataSetChanged();

        for (Map.Entry<String, PersonModelBean> entry : selectedAll.entrySet()) {
            if (entry.getValue().strUserID.equals(bean.strModifyUserID) && entry.getValue().strDomainCode.equals(bean.strModifyUserDomainCode)) {
                entry.getValue().strUserName = bean.strModifyUserName;
                entry.getValue().nPriority = bean.nPriority;

                changeMenu();
                break;
            }
        }
    }


    public void destory(){
        if (observeUserStatus != null){
            observeUserStatus.cancel();
        }
        if (deviceStatusListener != null){
            DeviceApi.get().removeAlarmListener(deviceStatusListener);
            deviceStatusListener = null;
        }
    }


}
