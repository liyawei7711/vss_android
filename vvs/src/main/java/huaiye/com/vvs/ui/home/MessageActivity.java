package huaiye.com.vvs.ui.home;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.CreateTalkAndVideo;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.RecycleTouchUtils;
import huaiye.com.vvs.common.rx.RxUtils;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.MessageData;
import huaiye.com.vvs.ui.meet.MeetDetailActivity;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;

/**
 * author: admin
 * date: 2018/05/11
 * version: 0
 * mail: secret
 * desc: MessageActivity
 */
@BindLayout(R.layout.activity_messages)
public class MessageActivity extends AppBaseActivity {
    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;
    @BindView(R.id.rct_view)
    RecyclerView rct_view;
    @BindView(R.id.ll_empty)
    View ll_empty;

    EXTRecyclerAdapter<MessageData> adapter;

    @Override
    protected void initActionBar() {

        getNavigate()
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                .setTitlText(AppUtils.getString(R.string.notice_title));

        rct_view.setLayoutManager(new LinearLayoutManager(this));
        new RecycleTouchUtils().initTouch(new RecycleTouchUtils.ITouchEvent() {
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                mLogicDialog.setCancelable(false);
                mLogicDialog.setCanceledOnTouchOutside(false);
                mLogicDialog.setMessageText(AppUtils.getString(R.string.is_delete_this_msg));
                mLogicDialog.setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MessageData data = adapter.getDataForItemPosition(viewHolder.getAdapterPosition());
                        AppDatas.Messages().del(data);

                        adapter.removeDataForItemPosition(viewHolder.getAdapterPosition());

                        refNum();

                        showEmpty();

                    }
                }).setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.notifyDataSetChanged();
                        refNum();

                    }
                }).show();
            }
        }).attachToRecyclerView(rct_view);

        adapter = new EXTRecyclerAdapter<MessageData>(R.layout.item_messages) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, MessageData messageData) {

                switch (messageData.getMessageType()) {
                    case MessageData.MEET_INVITE_JISHI:
                    case MessageData.MEET_INVITE_QUXIAO:
                        CNotifyInviteUserJoinMeeting meetInvite =
                                new Gson().fromJson(messageData.getMessageJson(), CNotifyInviteUserJoinMeeting.class);
                        extViewHolder.setText(R.id.tv_title, messageData.getTitle() + "(" + meetInvite.nMeetingID + ")");
                        break;
                    case MessageData.TALK_INVITE:
                        CNotifyUserJoinTalkback talkInvite =
                                new Gson().fromJson(messageData.getMessageJson(), CNotifyUserJoinTalkback.class);
                        extViewHolder.setText(R.id.tv_title, talkInvite.strFromUserName);
                        break;
                    default:
                        extViewHolder.setText(R.id.tv_content, messageData.getContent());
                        break;
                }
                extViewHolder.setText(R.id.tv_content, messageData.getContent());
                extViewHolder.setText(R.id.tv_date, messageData.getDate());
                extViewHolder.setImageResouce(R.id.iv_message_type, messageData.getIconResource());
                extViewHolder.setVisibility(R.id.view_red, messageData.getIsRead() ? View.GONE : View.VISIBLE);

            }
        };

        adapter.setOnItemClickListener(new EXTRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int i) {
                onMessageItemClicked(adapter.getDataForItemPosition(i));
            }
        });

        rct_view.setAdapter(adapter);

        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refMessage();
            }
        });
    }

    @Override
    public void doInitDelay() {

    }

    /**
     * 展示空数据
     */
    private void showEmpty() {
        if (adapter.getDatasCount() > 0) {
            ll_empty.setVisibility(View.GONE);
        } else {
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 刷新本地数据
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refMessage() {
        adapter.setDatas(AppDatas.Messages().getMessages());
        adapter.notifyDataSetChanged();

        refNum();

        showEmpty();
        refresh_view.setRefreshing(false);

    }

    public void refNum() {
        int count = 0;
        for (MessageData temp : adapter.getDatas()) {
            if (!temp.getIsRead()) {
                count++;
            }
        }
//        ((MainActivity) getActivity()).changeRedCircle(count);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refMessage();
    }

    /**
     * 点击消息
     *
     * @param data
     */
    void onMessageItemClicked(final MessageData data) {

        AppDatas.Messages().read(data.nMillions);
        data.setIsRead(1);

        switch (data.getMessageType()) {
            case MessageData.MEET_INVITE_JISHI:
                Intent intent = new Intent();
                CNotifyInviteUserJoinMeeting meetInvite = new Gson().fromJson(data.getMessageJson(), CNotifyInviteUserJoinMeeting.class);
                intent.setClass(MessageActivity.this, MeetDetailActivity.class);
                intent.putExtra("strMeetDomainCode", meetInvite.strMeetingDomainCode);
                intent.putExtra("nMeetID", meetInvite.nMeetingID);
                startActivity(intent);
                break;
            case MessageData.TALK_INVITE:
                finish();
                //videoClick会调用EventBus发送CreateTalkAndVideo事件
                //CreateTalkAndVideo事件会让MainActivity操作Fragment
                //Activity在暂停的时候不能操作Fragment
                //所以需要等1秒钟,MainActivity启动后再推消息
                RxUtils rxUtils = new RxUtils();
                rxUtils.doDelayOn(1000, new RxUtils.IMainDelay() {
                    @Override
                    public void onMainDelay() {
                        CNotifyUserJoinTalkback talkInvite = new Gson().fromJson(data.getMessageJson(), CNotifyUserJoinTalkback.class);
                        EventBus.getDefault().post(new CreateTalkAndVideo(talkInvite.getRequiredMediaMode() == SdkBaseParams.MediaMode.AudioAndVideo, talkInvite.strFromUserDomainCode, talkInvite.strFromUserID, talkInvite.strFromUserName, null, "message 216"));

                    }
                });
                break;
            default:
                break;
        }

    }

}
