package huaiye.com.vvs.ui.home.present;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.contacts.ContactsApi;
import huaiye.com.vvs.models.contacts.bean.PersonBean;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.device.DeviceApi;
import huaiye.com.vvs.models.device.bean.DeviceListBean;
import huaiye.com.vvs.models.device.bean.DevicePlayerBean;
import huaiye.com.vvs.ui.device.holder.DeviceChooseHolder;
import huaiye.com.vvs.ui.device.holder.PersonChooseHolder;
import huaiye.com.vvs.ui.home.view.IContactsView;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DeviceListPresent
 */

public class ContactsChoosePresent {
    private final int PERSON = 0;
    private final int DEVICE = 2;
    IContactsView iView;

    ArrayList<PersonModelBean> personList = new ArrayList<>();
    LiteBaseAdapter<PersonModelBean> personAdapter;
    Map<String, PersonModelBean> selectedAll = new HashMap<>();
    int rel_person;
    int total_person;

    ArrayList<DevicePlayerBean> deviceList = new ArrayList<>();
    LiteBaseAdapter<DevicePlayerBean> deviceAdapter;
    int total_device;

    ArrayList<CGetMeetingInfoRsp.UserInfo> users;
    int index = PERSON;

    boolean isLoad;
    int size = 20;
    int pagePerson = 1;
    int pageDevice = 1;

    public ContactsChoosePresent(final IContactsView iView, ArrayList<CGetMeetingInfoRsp.UserInfo> users) {
        this.iView = iView;
        this.users = users;

        personAdapter = new LiteBaseAdapter<>(iView.getContext(),
                personList,
                PersonChooseHolder.class,
                R.layout.item_person_holder,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PersonModelBean bean = (PersonModelBean) v.getTag();
                        if (selectedAll.containsKey(bean.strUserID)) {
                            selectedAll.remove(bean.strUserID);
                        } else {
                            selectedAll.put(bean.strUserID, bean);
                        }

                        personAdapter.notifyDataSetChanged();
                    }
                }, selectedAll);
        personAdapter.setLoadListener(new LiteBaseAdapter.LoadListener() {
            @Override
            public boolean isLoadOver() {
                return !isLoad;
            }

            @Override
            public boolean isEnd() {
                return rel_person >= total_person;
            }

            @Override
            public void lazyLoad() {
                loadPerson(false);
            }
        });


        deviceAdapter = new LiteBaseAdapter<>(iView.getContext(),
                deviceList,
                DeviceChooseHolder.class,
                R.layout.item_choose_device_holder,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DevicePlayerBean bean = (DevicePlayerBean) v.getTag();
                        bean.isSelected = !bean.isSelected;
                        deviceAdapter.notifyDataSetChanged();
                    }
                }, "");
        deviceAdapter.setLoadListener(new LiteBaseAdapter.LoadListener() {
            @Override
            public boolean isLoadOver() {
                return !isLoad;
            }

            @Override
            public boolean isEnd() {
                return deviceList.size() >= total_device;
            }

            @Override
            public void lazyLoad() {
                loadDevice(false);
            }
        });

    }

    public void loadPerson(final boolean isRef) {

        if (isRef)
            pagePerson = 1;
        else
            pagePerson++;

        isLoad = true;
        ContactsApi.get().getPerson(pagePerson, size, 0, 0,
                new ModelCallback<PersonBean>() {
                    @Override
                    public void onSuccess(PersonBean personBean) {
                        loadFinish();
                        if (isRef) {
                            rel_person = 0;
                            personList.clear();
                        }
                        if (personBean.userList != null) {
                            rel_person += personBean.userList.size();
                            for (PersonModelBean bean : personBean.userList) {
                                if (bean.strUserID.equals(AppDatas.Auth().getUserID() + "")) {
                                    continue;
                                }
                                boolean contact = false;
                                for (CGetMeetingInfoRsp.UserInfo temp : users) {
                                    if (bean.strUserID.equals(temp.strUserID)) {
                                        contact = true;
                                        break;
                                    }
                                }
                                if (!contact) {
                                    personList.add(bean);
                                }
                            }


                        }
                        changeViewShow(personList.isEmpty());

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

    /**
     * 加载设备
     */
    public void loadDevice(final boolean isRef) {

        if (isRef)
            pageDevice = 1;
        else
            pageDevice++;

        isLoad = true;
        DeviceApi.get().getDomainDeviceList(pageDevice, size, 0, new ModelCallback<DeviceListBean>() {
            @Override
            public void onSuccess(DeviceListBean deviceListBean) {
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
                            if (channelBean.streamList.isEmpty()) {
                                continue;
                            }
                            bean.strStreamCode = channelBean.streamList.get(0).strStreamCode;
                            bean.strSubStreamCode = channelBean.streamList.get(1).strStreamCode;
                            deviceList.add(bean);
                        }
                    }

                }
                if (index == DEVICE) {
                    changeViewShow(deviceList.isEmpty());
                }
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                loadFinish();
                iView.getContext().showToast(AppUtils.getString(R.string.get_device_false));
            }
        });
    }

    /**
     * 会议
     */
    public void meetClick() {

        ArrayList<CStartMeetingReq.UserInfo> allUser = new ArrayList<>();
        for (Map.Entry<String, PersonModelBean> entry : selectedAll.entrySet()) {

            PersonModelBean value = entry.getValue();
            CStartMeetingReq.UserInfo bean = new CStartMeetingReq.UserInfo();
            bean.setDevTypeUser();
            bean.strUserDomainCode = value.strDomainCode;
            bean.strUserID = value.strUserID;
            bean.strUserName = value.strUserName;
            allUser.add(bean);
        }


        for (DevicePlayerBean temp : deviceList) {
            if (temp.isSelected) {
                CStartMeetingReq.UserInfo bean = new CStartMeetingReq.UserInfo();
                bean.setDevTypeDevice();
                bean.strUserDomainCode = temp.strDomainCode;
                bean.strUserID = temp.strDomainCode
                        + "-"
                        + temp.strDeviceCode
                        + "-"
                        + temp.strChannelCode
                        + "-"
                        + temp.strStreamCode;
                bean.strUserName = temp.strChannelName;
                allUser.add(bean);
            }
        }
        Intent intent = new Intent();
        intent.putExtra("data", allUser);
        iView.getContext().setResult(Activity.RESULT_OK, intent);
        iView.getContext().finish();


    }

    private void loadFinish() {
        isLoad = false;
        iView.getRefView().setRefreshing(false);
    }

    public RecyclerView.Adapter getPersonAdapter() {
        index = PERSON;
        isLoad = false;
        return personAdapter;
    }

    public LiteBaseAdapter getDeviceAdapter() {
        index = DEVICE;
        isLoad = false;
        return deviceAdapter;
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

    public ArrayList<PersonModelBean> getPersonList() {
        return personList;
    }

    public ArrayList<DevicePlayerBean> getDeviceList() {
        return deviceList;
    }

}
