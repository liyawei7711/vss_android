package huaiye.com.vvs.ui.home;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCaller;
import com.huaiye.sdk.core.SdkNotifyCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.meet.ParamsMeetObserver;
import com.huaiye.sdk.sdkabi.abilities.talk.callback.CallbackQuitTalk;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.talk.CQuitTalkbackRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.CreateMeet;
import huaiye.com.vvs.bus.CreateTalkAndVideo;
import huaiye.com.vvs.bus.MeetInvistor;
import huaiye.com.vvs.bus.TalkInvistor;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ErrorMsg;
import huaiye.com.vvs.common.dialog.CallRecordDialog;
import huaiye.com.vvs.common.dialog.LogicDialog;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.views.WindowManagerUtils;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.CallRecordManage;
import huaiye.com.vvs.dao.msgs.CallRecordMessage;
import huaiye.com.vvs.models.ModelApis;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.meet.bean.RecordMeetList;
import huaiye.com.vvs.ui.home.holder.CallRecordListViewHolder;
import huaiye.com.vvs.ui.meet.MeetActivity;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;


/**
 * Created by 123 on 17/5/26.
 */
@BindLayout(R.layout.activity_call_recordt_list)
public class CallRecordActivity extends AppBaseActivity {
    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;
    Gson gson = new Gson();
    @BindView(R.id.message_list)
    RecyclerView message_list;
    @BindView(R.id.ll_empty)
    View ll_empty;

    ArrayList<CallRecordMessage> datas = new ArrayList<>();
    LiteBaseAdapter<CallRecordMessage> adapter;
    private CallRecordDialog callRecordDialog;
    private ArrayList<CallRecordMessage> meetingDatas = new ArrayList<>();
    List<SdkCaller> callers = new ArrayList<>();

    @Override
    protected void initActionBar() {
        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request();
            }
        });

        getNavigate().setVisibility(View.GONE);

        adapter = new LiteBaseAdapter<>(this,
                datas,
                CallRecordListViewHolder.class,
                R.layout.item_call_record_list_view,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CallRecordMessage bean = (CallRecordMessage) v.getTag();
//                        adapter.notifyDataSetChanged();
                        if (bean.getIsRecord() == 1) {
                            callRecordDialog.setCallRecordMessage(bean);
                            //自己呼出的会议只能会议
                            if (bean.getnAcceptType() == CallRecordMessage.CALL_OUT && bean.getnTalkType() == CallRecordMessage.MEET) {
                                CreateMeet meet = gson.fromJson(bean.getStrCreateMeetJson(), CreateMeet.class);
                                EventBus.getDefault().post(meet);
                                return;
                            }
                            callRecordDialog.show();
                        } else {
                            if (AppUtils.isTalk || AppUtils.isVideo) {
                                LogicDialog dialog = getLogicDialog()
                                        .setMessageText(AppUtils.getString(R.string.close_talk));

                                dialog.setConfirmText(AppUtils.getString(R.string.makesure));
                                dialog.setCancelText(AppUtils.getString(R.string.cancel));

                                dialog.setConfirmClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (AppUtils.isTalk) {
                                            AppUtils.getTvl_view(CallRecordActivity.this).endTalk(new CallbackQuitTalk() {
                                                @Override
                                                public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                                                    return false;
                                                }

                                                @Override
                                                public void onSuccess(CQuitTalkbackRsp cQuitTalkbackRsp) {

                                                    MeetActivity.stratMeet(CallRecordActivity.this, bean.strMainUserID.equals(AppDatas.Auth().getUserID() + ""),
                                                            bean.strMainUserDomainCode, bean.nMeetingID, System.currentTimeMillis(), SdkBaseParams.MediaMode.AudioAndVideo);

                                                }

                                                @Override
                                                public void onError(ErrorInfo errorInfo) {
                                                    showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                                                }
                                            });
                                        } else {
                                            AppUtils.getTvvl_view(CallRecordActivity.this).endTalkView(new CallbackQuitTalk() {
                                                @Override
                                                public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                                                    return true;
                                                }

                                                @Override
                                                public void onSuccess(CQuitTalkbackRsp cQuitTalkbackRsp) {
                                                    AppUtils.getTvvl_view(CallRecordActivity.this).realClose();

                                                    MeetActivity.stratMeet(CallRecordActivity.this, bean.strMainUserID.equals(AppDatas.Auth().getUserID() + ""),
                                                            bean.strMainUserDomainCode, bean.nMeetingID, System.currentTimeMillis(), SdkBaseParams.MediaMode.AudioAndVideo);

                                                }

                                                @Override
                                                public void onError(ErrorInfo errorInfo) {
                                                    showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                                                }
                                            });
                                        }
                                    }
                                }).setCancelClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }).show();
                                return;
                            }
                            if (AppUtils.isMeet) {
                                if (bean.nMeetingID == AppUtils.getMeet_view(CallRecordActivity.this).nMeetID) {
                                    WindowManagerUtils.closeAll(false);
                                    MeetActivity.stratMeet(MCApp.getInstance().getTopActivity(), false, "", 1, 1, null);
                                    WindowManagerUtils.simpleView = null;
                                    return;
                                }
                            }
                            MeetActivity.stratMeet(CallRecordActivity.this, bean.strMainUserID.equals(AppDatas.Auth().getUserID() + ""),
                                    bean.strMainUserDomainCode, bean.nMeetingID, System.currentTimeMillis(), SdkBaseParams.MediaMode.AudioAndVideo);
                        }
                    }
                }, "");
        message_list.setLayoutManager(new

                LinearLayoutManager(this));
        message_list.setAdapter(adapter);

        callRecordDialog = new

                CallRecordDialog(this);
        callRecordDialog.setCompleteListener(new CallRecordDialog.CompleteListener()

        {
            @Override
            public void talk(CallRecordMessage callRecordMessage) {
                CreateTalkAndVideo createTalkAndVideo = null;
                switch (callRecordMessage.getnTalkType()) {
                    case CallRecordMessage.TALK:
                    case CallRecordMessage.TALK_VIDEO:

                        if (callRecordMessage.getnAcceptType() == CallRecordMessage.CALL_OUT) {
                            createTalkAndVideo = gson.fromJson(callRecordMessage.getStrCreateTalkJson(), CreateTalkAndVideo.class);
                            createTalkAndVideo.hasVideo = false;
                        } else {
                            TalkInvistor invistor = gson.fromJson(callRecordMessage.getStrJoinTalkJson(), TalkInvistor.class);
                            createTalkAndVideo = new CreateTalkAndVideo(false, invistor.talk.strFromUserDomainCode,
                                    invistor.talk.strFromUserID, invistor.talk.strFromUserName, null, "CallRecordActivity talk or video to talk");
                        }
                        break;
                    case CallRecordMessage.MEET:
                        MeetInvistor meet = gson.fromJson(callRecordMessage.getStrJoinMeetingJson(), MeetInvistor.class);
                        createTalkAndVideo = new CreateTalkAndVideo(false, meet.meet.strInviteUserDomainCode,
                                meet.meet.strInviteUserTokenID, meet.meet.strInviteUserName, null, "CallRecordActivity meet to talk");
                        break;
                }
                EventBus.getDefault().post(createTalkAndVideo);
                callRecordDialog.dismiss();
                finish();
            }

            @Override
            public void talkVideo(CallRecordMessage callRecordMessage) {

                CreateTalkAndVideo createTalkAndVideo = null;
                switch (callRecordMessage.getnTalkType()) {
                    case CallRecordMessage.TALK:
                    case CallRecordMessage.TALK_VIDEO:

                        if (callRecordMessage.getnAcceptType() == CallRecordMessage.CALL_OUT) {
                            createTalkAndVideo = gson.fromJson(callRecordMessage.getStrCreateTalkJson(), CreateTalkAndVideo.class);
                            createTalkAndVideo.hasVideo = true;
                        } else {
                            TalkInvistor invistor = gson.fromJson(callRecordMessage.getStrJoinTalkJson(), TalkInvistor.class);
                            createTalkAndVideo = new CreateTalkAndVideo(true, invistor.talk.strFromUserDomainCode,
                                    invistor.talk.strFromUserID, invistor.talk.strFromUserName, null, "CallRecordActivity talk or vidieo to video");

                        }
                        break;
                    case CallRecordMessage.MEET:
                        MeetInvistor meet = gson.fromJson(callRecordMessage.getStrJoinMeetingJson(), MeetInvistor.class);
                        createTalkAndVideo = new CreateTalkAndVideo(true, meet.meet.strInviteUserDomainCode,
                                meet.meet.strInviteUserTokenID, meet.meet.strInviteUserName, null, "CallRecordActivity meet to video");
                        break;
                }
                EventBus.getDefault().post(createTalkAndVideo);
                callRecordDialog.dismiss();
            }

            @Override
            public void meet(CallRecordMessage callRecordMessage) {
                CreateMeet meet = null;
                final ArrayList<CStartMeetingReq.UserInfo> allUser = new ArrayList<>();
                CStartMeetingReq.UserInfo bean = new CStartMeetingReq.UserInfo();
                switch (callRecordMessage.getnTalkType()) {
                    case CallRecordMessage.TALK:
                    case CallRecordMessage.TALK_VIDEO:
                        if (callRecordMessage.getnAcceptType() == CallRecordMessage.CALL_OUT) {
                            CreateTalkAndVideo createTalkAndVideo = gson.fromJson(callRecordMessage.getStrCreateTalkJson(), CreateTalkAndVideo.class);
                            bean.setDevTypeUser();
                            bean.strUserDomainCode = createTalkAndVideo.domain;
                            bean.strUserID = createTalkAndVideo.id;
                            bean.strUserName = createTalkAndVideo.name;
                            allUser.add(bean);
                        } else {
                            TalkInvistor invistor = gson.fromJson(callRecordMessage.getStrJoinTalkJson(), TalkInvistor.class);
                            bean.setDevTypeUser();
                            bean.strUserDomainCode = invistor.talk.strFromUserDomainCode;
                            bean.strUserID = invistor.talk.strFromUserID;
                            bean.strUserName = invistor.talk.strFromUserName;
                            ;
                            allUser.add(bean);
                        }
                        break;
                    case CallRecordMessage.MEET:
                        MeetInvistor meetInvistor = gson.fromJson(callRecordMessage.getStrJoinMeetingJson(), MeetInvistor.class);
                        bean.setDevTypeUser();
                        bean.strUserDomainCode = meetInvistor.meet.strInviteUserDomainCode;
                        bean.strUserID = meetInvistor.meet.strInviteUserTokenID;
                        bean.strUserName = meetInvistor.meet.strInviteUserName;
                        allUser.add(bean);
                        break;
                }
                EventBus.getDefault().post(new CreateMeet(allUser));
                callRecordDialog.dismiss();

            }
        });
    }

    @Override
    public void doInitDelay() {
        request();
    }

    private void request() {
        meetingDatas.clear();
        ModelApis.Meet().requestCurrentMeets(1, new ModelCallback<RecordMeetList>() {
            @Override
            public void onPreStart(HTTPRequest httpRequest) {
                super.onPreStart(httpRequest);
            }

            @Override
            public void onSuccess(RecordMeetList meetList) {
                for (SdkCaller caller : callers) {
                    caller.cancel();
                }
                callers.clear();
                datas.clear();
                if (meetList.nResultCode == 0) {
                    meetingDatas = meetList.lstMeetingInfo;
                    datas.addAll(meetList.lstMeetingInfo);
                    for (CallRecordMessage meet : meetList.lstMeetingInfo) {
                        SdkCaller sdkCaller = HYClient.getModule(ApiMeet.class).observeMeetingStatus(new ParamsMeetObserver()
                                .setMeetID(meet.nMeetingID)
                                .setMeetDomainCode(meet.strMainUserDomainCode), new SdkNotifyCallback<CNotifyMeetingStatusInfo>() {
                            @Override
                            public void onEvent(CNotifyMeetingStatusInfo cNotifyMeetingStatusInfo) {

                            }
                        });
                        callers.add(sdkCaller);
                    }
                }
                loadMessage();
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFinish(httpResponse);
                datas.clear();
                loadMessage();
            }
        });
    }

    private void loadMessage() {
        List<CallRecordMessage> allBean = CallRecordManage.get().getMessages();
        datas.addAll(allBean);
        adapter.notifyDataSetChanged();
        showEmpty();
        refresh_view.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (SdkCaller caller : callers) {
            caller.cancel();
        }
        callers.clear();

    }

    @OnClick({R.id.iv_back, R.id.iv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_right:
                LogicDialog dialog = getLogicDialog()
                        .setMessageText(AppUtils.getString(R.string.delete_record));

                dialog.setConfirmText(AppUtils.getString(R.string.makesure));
                dialog.setCancelText(AppUtils.getString(R.string.cancel));

                dialog.setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CallRecordManage.get().delAll();
                        request();
                    }
                }).setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
                break;
        }
    }


    private void showEmpty() {
        if (datas.size() > 0) {
            message_list.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            message_list.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyMeetingStatusInfo info) {
        if (info.isMeetFinished()) {
            for (CallRecordMessage callRecordMessage : meetingDatas) {
                if (info.nMeetingID == callRecordMessage.nMeetingID) {
                    request();
                }
            }
        }
    }

}
