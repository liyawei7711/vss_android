package huaiye.com.vvs.ui.home.present;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.core.SdkCaller;
import com.huaiye.sdk.core.SdkNotifyCallback;
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
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.ChangeUserBean;
import huaiye.com.vvs.dao.msgs.ChatUtil;
import huaiye.com.vvs.dao.msgs.VssMessageListBean;
import huaiye.com.vvs.dao.msgs.VssMessageListMessages;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.callrecord.CallRecordApi;
import huaiye.com.vvs.models.contacts.ContactsApi;
import huaiye.com.vvs.models.contacts.bean.PersonBean;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.device.DeviceApi;
import huaiye.com.vvs.models.device.bean.DeviceListBean;
import huaiye.com.vvs.models.device.bean.DevicePlayerBean;
import huaiye.com.vvs.models.meet.bean.MeetList;
import huaiye.com.vvs.ui.channel.ChannelDetailActivity;
import huaiye.com.vvs.ui.chat.ChatActivity;
import huaiye.com.vvs.ui.device.DevicePlayRealActivity;
import huaiye.com.vvs.ui.device.holder.DeviceHolder;
import huaiye.com.vvs.ui.device.holder.GroupHolder;
import huaiye.com.vvs.ui.device.holder.PersonHolder;
import huaiye.com.vvs.ui.home.holder.CallRecordListViewHolder;
import huaiye.com.vvs.ui.home.view.IContactsView;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DeviceListPresent
 */

public class CallRecordPresent {
    private final int PERSON = 0;
    private final int GROUP = 1;
    private final int DEVICE = 2;
    IContactsView iView;

    ArrayList<MeetList.Data> callRecordList = new ArrayList<>();
    LiteBaseAdapter<MeetList.Data> personAdapter;
    int total_person;
    int rel_person;


    boolean isLoad;
    int size = 20;
    int pagePerson = 1;

    public CallRecordPresent(final IContactsView iView) {
        this.iView = iView;
        personAdapter = new LiteBaseAdapter<>(iView.getContext(),
                callRecordList,
                CallRecordListViewHolder.class,
                R.layout.item_call_record_list_view,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        VssMessageListBean bean = (VssMessageListBean) v.getTag();
//                        bean.isRead = 1;
//                        VssMessageListMessages.get().isRead(bean);
//
//                        Intent intent = new Intent(CallRecordActivity.this, ChatActivity.class);
//                        intent.putExtra("listBean", bean);
//                        startActivity(intent);
//
//                        adapter.notifyDataSetChanged();
//                        callRecordDialog.show();
                    }
                }, "");
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
    }

    public void loadPerson(final boolean isRef) {

        if (isRef)
            pagePerson = 1;
        else
            pagePerson++;

        isLoad = true;
//        CallRecordApi.get().getCallRecord(pagePerson, size, new ModelCallback<MeetList>() {
//                    @Override
//                    public void onSuccess(MeetList talk) {
//                        total_person = talk.nTotalSize;
//                        loadFinish();
//                        if (isRef) {
//                            rel_person = 0;
//                            callRecordList.clear();
//                        }
//                        if (talk.lstTalkbackInfo != null) {
//                            rel_person += talk.lstTalkbackInfo.size();
//
//                            for (MeetList.Data bean : talk.lstTalkbackInfo) {
//
//                                callRecordList.add(bean);
//                            }
//                        }
//                        changeViewShow(callRecordList.isEmpty());
//                        personAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onFailure(HTTPResponse httpResponse) {
//                        super.onFailure(httpResponse);
//                        loadFinish();
//                        iView.getContext().showToast(AppUtils.getString(R.string.get_person_false));
//                    }
//                });
    }


    private void loadFinish() {
        isLoad = false;
        iView.getRefView().setRefreshing(false);
    }


    public RecyclerView.Adapter getPersonAdapter() {
        isLoad = false;
        return personAdapter;
    }


    public ArrayList<MeetList.Data> getPersonList() {
        return callRecordList;
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

}
