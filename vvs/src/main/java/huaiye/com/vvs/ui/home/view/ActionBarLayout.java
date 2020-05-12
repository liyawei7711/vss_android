package huaiye.com.vvs.ui.home.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huaiye.cmf.JniIntf;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.core.SdkCaller;
import com.huaiye.sdk.core.SdkNotifyCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.VideoStartCallback;
import com.huaiye.sdk.media.player.sdk.VideoStatusCallback;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.media.player.sdk.params.talk.trunkchannel.TrunkChannelReal;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi.abilities.talk.callback.CallbackStartGetRight;
import com.huaiye.sdk.sdkabi.abilities.talk.callback.CallbackTrunkChannelNotify;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListReq;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListRsp;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CGetSpeakRightRsp;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CGetTrunkChannelInfoRsp;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CJoinTrunkChannelRsp;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CNotifyTrunkChannelSpeakerStatus;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CNotifyTrunkChannelStatus;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CNotifyTrunkChannelUserPlaySpeakSet;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CNotifyUserKickTrunkChannel;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CQueryTrunkChannelListRsp;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CStartTrunkChannelSpeakRsp;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CStopTrunkChannelSpeakRsp;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelBean;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelUserBean;
import com.huaiye.sdk.sdpmsgs.video.CStartMobileCaptureRsp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.ChannelInvistor;
import huaiye.com.vvs.bus.FinishDiaoDu;
import huaiye.com.vvs.bus.NetStatusChange;
import huaiye.com.vvs.bus.NewMessage;
import huaiye.com.vvs.common.AlarmMediaPlayer;
import huaiye.com.vvs.common.AppAudioManagerWrapper;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.common.rx.RxUtils;
import huaiye.com.vvs.common.views.DropDownAnimator;
import huaiye.com.vvs.common.views.pickers.itemdivider.SimpleItemDecoration;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.auth.AppAuth;
import huaiye.com.vvs.dao.msgs.VssMessageListMessages;
import huaiye.com.vvs.ui.channel.ChannelDetailActivity;
import huaiye.com.vvs.ui.home.MainActivity;
import huaiye.com.vvs.ui.home.holder.TrunkChannelHolder;

import static huaiye.com.vvs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: 首页的title布局
 */

public class ActionBarLayout extends FrameLayout implements View.OnClickListener {
    private final String TAG = ActionBarLayout.class.getSimpleName();
    RecyclerView rv_list;
    View ll_middle;
    TextView tv_title;
    TextView tv_msg;
    View tv_left;
    View tv_right;
    View view_point;
    View tv_contact;
    TextView tv_channel_size;
    View rl_container;
    View tv_middle_p2p;
    View ll_disconnect;
    TextView tv_disconnect_info;
    TextView tv_enter_p2p;

    LiteBaseAdapter<TrunkChannelBean> adapter;
    /**
     * 拥有的频道列表
     */
    ArrayList<TrunkChannelBean> datas = new ArrayList<>();

    Map<Integer, TrunkChannelReal> mapVideo = new HashMap<>();

    ArrayList<SdkCaller> sdkCallers = new ArrayList<>();
    SdkCaller speakSdkCallers;
    OnChangeChannelListener listener;
    /**
     * 当前频道
     */
    TrunkChannelBean currentBean;

    /**
     * 上一个对讲频道
     */
    TrunkChannelBean lastChannelBean;

    /**
     * 默认对讲频道
     */
    TrunkChannelBean defaultChannelBean;

    /**
     * 当前说话的人,但是设备在采集中,没有真的播放,等设备停止采集(视频,会议)后再播放
     */
    CNotifyTrunkChannelUserPlaySpeakSet currentSpeak;

    AppAudioManagerWrapper audio;

    /**
     * 是否在抢麦
     */
    public boolean isMyselfSpeaking = false;

    Drawable drawableUp;
    Drawable drawableDown;
    long lastPlay;

    DropDownAnimator dropDownAnimator;
    RxUtils rxUtils;


    OnMenuItemClickListener onMenuItemClickListener;

    public ActionBarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ActionBarLayout(@NonNull final Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
        rxUtils = new RxUtils();
        drawableUp = getResources().getDrawable(R.drawable.tp_shouqi);
        drawableUp.setBounds(0, 0, drawableUp.getMinimumWidth(), drawableUp.getMinimumHeight());//对图片进行压缩
        drawableDown = getResources().getDrawable(R.drawable.tp_zhankai);
        drawableDown.setBounds(0, 0, drawableUp.getMinimumWidth(), drawableUp.getMinimumHeight());//对图片进行压缩


        View view = LayoutInflater.from(context).inflate(R.layout.main_action_bar_layout, null);
        tv_middle_p2p = view.findViewById(R.id.tv_middle_p2p);
        rl_container = view.findViewById(R.id.rl_container);
        rv_list = view.findViewById(R.id.rv_list);
        ll_middle = view.findViewById(R.id.ll_middle);
        tv_title = view.findViewById(R.id.tv_title);
        tv_msg = view.findViewById(R.id.tv_msg);
        tv_left = view.findViewById(R.id.tv_left);
        tv_right = view.findViewById(R.id.tv_right);
        view_point = view.findViewById(R.id.view_point);
        tv_contact = view.findViewById(R.id.tv_contact);
        ll_disconnect = view.findViewById(R.id.ll_disconnect);
        tv_disconnect_info = view.findViewById(R.id.tv_disconnect_info);
        tv_enter_p2p = view.findViewById(R.id.tv_enter_p2p);
        tv_enter_p2p.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuItemClickListener != null){
                    onMenuItemClickListener.onEnterP2pClick();
                }
            }
        });
        tv_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onMenuItemClickListener != null){
                    onMenuItemClickListener.onLeftMenuClick();
                }
            }
        });
        tv_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HYClient.getSdkOptions().P2P().isP2PRunning()){
                    showToast(AppUtils.getString(R.string.p2p_is_running_and_this_function_not_support));
                    return;
                }
                if (onMenuItemClickListener != null){
                    onMenuItemClickListener.onChatMenuClick();
                }
            }
        });
        tv_contact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HYClient.getSdkOptions().P2P().isP2PRunning()){
                    showToast(AppUtils.getString(R.string.p2p_is_running_and_this_function_not_support));
                    return;
                }
                if (onMenuItemClickListener != null){
                    onMenuItemClickListener.onContactMenuClick();
                }
            }
        });

        addView(view);
        dropDownAnimator = new DropDownAnimator(rv_list, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getContext().getResources().getDisplayMetrics()));
        adapter = new LiteBaseAdapter<>(context,
                datas,
                TrunkChannelHolder.class,
                R.layout.item_trunk_channel,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TrunkChannelBean bean = (TrunkChannelBean) v.getTag();
                        if (v.getId() == R.id.iv_arrow) {
                            Intent intent = new Intent(context, ChannelDetailActivity.class);
                            intent.putExtra("trunkChannelBean", bean);
                            context.startActivity(intent);
                        } else {
                            acceptNewChannel(bean);
                            hideList();
                        }

                    }
                }, "");

        rv_list.setLayoutManager(new LinearLayoutManager(context));
        rv_list.setAdapter(adapter);
        rv_list.addItemDecoration(new SimpleItemDecoration(getContext(), ActivityCompat.getColor(getContext(), R.color.gray4e535b), 1));
        ll_middle.setOnClickListener(this);
        rv_list.setOnClickListener(this);
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.item_channel_head, rv_list, false);
        tv_channel_size = headView.findViewById(R.id.tv_channel_size);
        adapter.setHeaderView(headView, new LiteViewHolder(getContext(), headView, null, null) {
            @Override
            public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {

            }
        });
        //监听频道说话状态
        SdkCaller callerChannelChange = HYClient.getModule(ApiTalk.class).observerTrunkChannelChange(new CallbackTrunkChannelNotify() {
            @Override
            public void onSuccess(CNotifyTrunkChannelSpeakerStatus resp) {
                Logger.debug(TAG+ " " + "ApiTalk onSuccess currentBean" + currentBean == null ? " null" : " not null");
            }

            @Override
            public void onError(ErrorInfo error) {
                Logger.debug(TAG+ " " + "ApiTalk onError " + error.toString());

            }

            @Override
            public void notifyTrunkChannelMyselfSpeakStatus(CNotifyTrunkChannelSpeakerStatus info) {
                Logger.debug(TAG+ " " + "ApiTalk notifyTrunkChannelUserPlaySpeak " + JniIntf.GetSystemProperty(JniIntf.SYSTEM_PROPERTY_ENABLE_RESAMPLE));
                if (info.isSpeakFinish()) {
                    setTrunkChannelIdle();
                    stopMyselfSpeak();
                } else {
                    if (currentBean != null && info.nTrunkChannelID == currentBean.nTrunkChannelID) {
                        if (System.currentTimeMillis() - lastPlay > 1000) {
                            lastPlay = System.currentTimeMillis();
                            doVibrate();
                            if (listener != null) {
                                listener.onSpeakWillFinish();
                            }
                        }
                    }
                }
            }

            @Override
            public void notifyTrunkChannelUserPlaySpeakSet(CNotifyTrunkChannelUserPlaySpeakSet status) {
                Logger.debug(TAG + " ApiTalk notifyTrunkChannelUserPlaySpeakSet " + status);
                if (status == null || currentBean == null ){
                    return;
                }
                if (status.needStart()){
                    startPlayVoice(status,currentBean);
                }else {

                    setTrunkChannelIdle();
                    if (!TextUtils.isEmpty(status.strSpeakUserTokenID)){
                        String userID = status.strSpeakUserTokenID.substring(0, status.strSpeakUserTokenID.lastIndexOf("_"));
                        if (status.strSpeakUserDomainCode.equals(AppDatas.Auth().getDomainCode())
                                && (AppDatas.Auth().getUserID() + "").equals(userID))
                            stopPTTAlarm();
                    }
                }
            }

            @Override
            public void notifyUserKickTrunkChannel(CNotifyUserKickTrunkChannel user) {
                Logger.debug(TAG+ " " + " ApiTalk notifyUserKickTrunkChannel");
                if (currentBean != null && currentBean.nTrunkChannelID == user.nTrunkChannelID) {
                    currentSpeak = null;
                    showToast(AppUtils.getString(R.string.kitout_group));
                }
                deleteOneGroup(user.nTrunkChannelID);


            }

        });
        sdkCallers.add(callerChannelChange);
        //监听频道变化
        SdkCaller callerStatus = HYClient.getModule(ApiTalk.class).observerTrunkChannelStatus(new SdkNotifyCallback<CNotifyTrunkChannelStatus>() {
            @Override
            public void onEvent(CNotifyTrunkChannelStatus status) {
                if (currentBean != null
                        && currentBean.nTrunkChannelID == status.nTrunkChannelID) {
                    if (status.isDelete()) {
                        if (currentBean != null && currentBean.nTrunkChannelID == status.nTrunkChannelID) {
                            showToast(AppUtils.getString(R.string.group_delete));
                        }
                        deleteOneGroup(status.nTrunkChannelID);
                    } else if (status.isUpdate()) {
                        for (TrunkChannelBean temp : datas) {
                            if (temp.nTrunkChannelID == status.nTrunkChannelID) {
                                temp.strTrunkChannelName = status.strTrunkChannelName;
                                break;
                            }
                        }
                        currentBean.strTrunkChannelName = status.strTrunkChannelName;
                        changeTitle();
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    if (status.isDelete()) {
                        for (TrunkChannelBean temp : datas) {
                            if (temp.nTrunkChannelID == status.nTrunkChannelID) {
                                datas.remove(temp);
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        sdkCallers.add(callerStatus);

        queryChannelData();

        EventBus.getDefault().register(this);
    }




    private void startPlayVoice(CNotifyTrunkChannelUserPlaySpeakSet status, TrunkChannelBean currentChannelBean) {

        tv_msg.setText(HYClient.getSdkOptions().User().getUserTokenId().equals(status.strSpeakUserTokenID + "") ?
                AppUtils.getString(R.string.me_talk_ing) : status.strSpeakUserName + AppUtils.getString(R.string.person_talk_ing));

        //新来的说话者和正在说的重复了,就不需要做什么了
        if (currentSpeak != null && currentSpeak.strSpeakUserTokenID.equals(status.strSpeakUserTokenID)){
            Logger.debug(TAG+ " " + " ApiTalk new speak same  with currentSpeak ");
            return;
        }

        //把当前播放的都记下来,停止播放的时候要置为空
        currentSpeak = status;
        if (AppUtils.isVideo || AppUtils.isMeet || AppUtils.isTalk) {
            Logger.debug(TAG+ " " + " ApiTalk notifyTrunkChannelUserPlaySpeakSet isPlay video  ");
            return;
        }

        if (status.strSpeakUserTokenID.equals(HYClient.getSdkOptions().User().getUserTokenId())) {
            Logger.debug(TAG+ " "  + " ApiTalk strSpeakUserTokenID is mySelft");
            return;
        }

        if (HYClient.getSdkOptions().User().getUserTokenId().equals(status.strSpeakUserTokenID + "")){
            //自己说话 不需要播放自己了
            return;
        }

        TrunkChannelReal videoParams;
        if (mapVideo.containsKey(currentChannelBean.nTrunkChannelID)) {
            videoParams = mapVideo.get(currentChannelBean.nTrunkChannelID);
        } else {
            videoParams = Player.Params.TypeTrunkChannelReal();
            mapVideo.put(currentChannelBean.nTrunkChannelID, videoParams);
        }

        videoParams.setStartPlayStatus(false);
        videoParams.setUserDomainCode(status.strSpeakUserDomainCode)
                .setUserID(status.strSpeakUserTokenID)
                .setStrMyID(HYClient.getSdkOptions().User().getUserTokenId())
                .setStatusCallback(new VideoStatusCallback() {
                    @Override
                    public void onVideoStatusChanged(VideoParams videoParams, SdpMessageBase sdpMessageBase) {
                        Logger.debug(TAG+ " " +  " ApiTalk notifyTrunkChannelUserPlaySpeakSet setStatusCallback");
                        //收到对方停止说话的消息 就停止本地播放
                        if (sdpMessageBase instanceof SdkMsgNotifyPlayStatus && ((SdkMsgNotifyPlayStatus) sdpMessageBase).isStopped()) {
                            closeReqStatus("329");
                            stopMyselfSpeak();
                        }
                    }
                })
                .setStartCallback(new VideoStartCallback() {
                    @Override
                    public void onSuccess(VideoParams param) {
                        showToast(AppUtils.getString(R.string.player_success));
                        Logger.debug(TAG+ " " + " ApiTalk notifyTrunkChannelUserPlaySpeakSet setStartCallback  success");
                    }

                    @Override
                    public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                        Logger.debug(TAG+ " " + " ApiTalk notifyTrunkChannelUserPlaySpeakSet setStartCallback  onError " + errorInfo.toString());
                    }
                });

        Logger.debug( TAG +" ApiTalk notifyTrunkChannelUserPlaySpeakSet will start " + mapVideo.get(currentChannelBean.nTrunkChannelID).getSessionID());

        initSound();
        HYClient.getHYAudioMgr().from(getContext()).setSpeakerphoneOn(true);
        HYClient.getHYPlayer().startPlay(mapVideo.get(currentChannelBean.nTrunkChannelID));
    }


    private void closePlayer(int id) {
        Logger.debug(TAG+ "closePlayer start mapVideoSize = " + mapVideo.size());
        closeReqStatus("375");
        if (id == -1) {
            closeAllPlayer();
        } else {
            TrunkChannelReal videoParams = mapVideo.get(id);
            mapVideo.remove(id);
            doClosePlayer(videoParams);
            closeSpeakLoud();
        }

        Logger.debug(TAG+ " " + "closePlayer end mapVideoSize = " + mapVideo.size());

    }

    /**
     * 清除当前播放对象,暂停所有播放
     */
    private void closeAllPlayer() {
        currentSpeak = null;
        pauseAllPlayer();
    }


    /**
     * 暂停所有的播放
     */
    private void pauseAllPlayer() {
        Logger.debug(TAG+ " " +  "closeAllPlayer  start mapVideoSize = " + mapVideo.size());
        for (Map.Entry<Integer, TrunkChannelReal> entry : mapVideo.entrySet()) {
            TrunkChannelReal videoParams = entry.getValue();
            doClosePlayer(videoParams);
        }
        mapVideo.clear();
        closeSpeakLoud();
        Logger.debug(TAG+ " "  + "closeAllPlayer  end mapVideoSize = " + mapVideo.size());
    }

    private void doClosePlayer(TrunkChannelReal videoParams){
        if (videoParams != null) {
            videoParams.setStartPlayStatus(true);
            HYClient.getHYPlayer().stopPlay(new SdkCallback<VideoParams>() {
                @Override
                public void onSuccess(VideoParams resp) {
                    showToast(AppUtils.getString(R.string.player_stop_success));
                }

                @Override
                public void onError(ErrorInfo error) {

                }
            }, videoParams);
        }
    }

    /**
     * 接受邀请加入新的频道
     * @param bean
     */
    private void acceptNewChannel(TrunkChannelBean bean) {
        //1 停止自己发言
        //2 停止当前播放
        //3 加入新的频道
        //4 更改ui
        Logger.debug(TAG+ " acceptNewChannel " + bean.nTrunkChannelID + " " + bean.strTrunkChannelName);
        closeReqStatus("888");
        stopMyselfSpeak();
        TrunkChannelBean willJoinChannel = null;
        for (TrunkChannelBean temp : datas) {
            if (temp.nTrunkChannelID == bean.nTrunkChannelID) {
                temp.extr = true;
                willJoinChannel = temp;
            } else {
                temp.extr = false;
            }
        }
        //找的到老频道就用老频道信息,找不到就用新的频道信息
        if (willJoinChannel == null) {
            willJoinChannel = bean;
            datas.add(0, bean);
            updateChannelSize();
            adapter.notifyDataSetChanged();
        }

        closeAllPlayer();

        final TrunkChannelBean finalWillJoinChannel = willJoinChannel;
        doJoinChannel(willJoinChannel, new SdkCallback<CJoinTrunkChannelRsp>() {
            @Override
            public void onSuccess(CJoinTrunkChannelRsp cJoinTrunkChannelRsp) {
                Logger.debug(TAG+ " acceptNewChannel success " + cJoinTrunkChannelRsp.toString());
                lastChannelBean = currentBean;
                currentBean = finalWillJoinChannel;
                showToast(AppUtils.getString(R.string.join_deal) + currentBean.strTrunkChannelName + AppUtils.getString(R.string.join_success));
                changeTitle();
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                showToast(AppUtils.getString(R.string.join_false));
                queryChannelData();

            }
        });

    }


    /**
     * 别的语音对讲,视频对讲结束后,继续当前的播放
     * @param data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResumePlay(final FinishDiaoDu data) {
        if (currentSpeak != null && currentBean != null ) {
            if(currentSpeak.needStart()){
                startPlayVoice(currentSpeak,currentBean);
            }else {
                setTrunkChannelIdle();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetStatusChange(NetStatusChange statusChange) {
        Logger.debug(TAG+  "onNetStatusChange ");
        if (statusChange.data == SdkBaseParams.ConnectionStatus.Connected && currentBean != null) {
            ensureChannelInfo(currentBean);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChannelInvite(final ChannelInvistor data) {
        if (data == null) {
            return;
        }
        Logger.debug(TAG+ " ChannelInvistor " + data.channel);

        final TrunkChannelBean willJoinChannel = new TrunkChannelBean();
        willJoinChannel.nTrunkChannelID = data.channel.nTrunkChannelID;
        willJoinChannel.strTrunkChannelDomainCode = data.channel.strTrunkChannelDomainCode;
        willJoinChannel.strTrunkChannelName = data.channel.strTrunkChannelName;
        willJoinChannel.extr = true;


        if (data.channel.nEnforce == 1) {
            AlarmMediaPlayer.get().play(AlarmMediaPlayer.SOURCE_PTT_VOICE);
            acceptNewChannel(willJoinChannel);
        } else {
            if (((AppBaseActivity) getContext()).getLogicDialog().isShowing()) {
                return;
            }
            ((AppBaseActivity) getContext()).getLogicDialog().setTitleText(AppUtils.getString(R.string.invisitor_title))
                    .setMessageText(data.channel.strTrunkChannelName + AppUtils.getString(R.string.group_invisitor_notice))
                    .setConfirmClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acceptNewChannel(willJoinChannel);
                        }
                    }).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NewMessage bean) {
        showPoint(bean.has);
    }

    public void onResume(){
        showPoint(VssMessageListMessages.get().getMessagesUnRead());

    }



    private void showPoint(boolean value) {
        view_point.setVisibility(value ? VISIBLE : GONE);
    }




    private void doJoinChannel(final TrunkChannelBean newChannelBean,SdkCallback<CJoinTrunkChannelRsp> callback){
        if (newChannelBean  == null){
            return;
        }
        HYClient.getModule(ApiTalk.class).joinTrunkChannel(SdkParamsCenter.Talk.JoinTrunkChannel()
                        .setnTrunkChannelID(newChannelBean.nTrunkChannelID)
                        .setnPriority(AppDatas.Auth().getPriority())
                        .setStrTrunkChannelDomainCode(newChannelBean.strTrunkChannelDomainCode),callback);
    }




    /**
     * 停止自己的发言
     * 修改: 去掉参数 notStop ,因为代码已经判断了有其他的采集就不关闭了,不需要外部控制了 by: lxf
     */
    private void stopMyselfSpeak() {
        if (!isMyselfSpeaking) {
            return;
        }
        if (currentBean != null) {
            isMyselfSpeaking = false;
            if (listener != null) {
                listener.onStopSpeak("593");
            }

            /**
             * 以启用的情况可以不关闭采集
             */
            boolean needStopCapture = true;
            if (AppUtils.isVideo || AppUtils.isTalk || AppUtils.isMeet || ((MainActivity) getContext()).cvl_capture.isCapture()) {
                needStopCapture = false;
            }

            HYClient.getModule(ApiTalk.class).stopTrunkChannelSpeak(SdkParamsCenter.Talk.StopTrunkChannelSpeak()
                            .setnTrunkChannelID(currentBean.nTrunkChannelID)
                            .setneedStopCapture(needStopCapture)
                            .setNeedStopDelay(true)
                            .setStrTrunkChannelDomainCode(currentBean.strTrunkChannelDomainCode),
                    new SdkCallback<CStopTrunkChannelSpeakRsp>() {
                        @Override
                        public void onSuccess(CStopTrunkChannelSpeakRsp resp) {
                        }

                        @Override
                        public void onError(ErrorInfo error) {
                        }
                    });
        }
    }


    private void stopPTTAlarm(){
        new RxUtils<>().doDelayOn(500, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                AlarmMediaPlayer.get().play(AlarmMediaPlayer.SOURCE_PTT_VOICE);
            }
        });
    }
    /**
     * 本机发言
     */
    private void qiangChannel() {
        if (currentBean != null) {
            if (isMyselfSpeaking) {
                return;
            }
            isMyselfSpeaking = true;

            speakSdkCallers = HYClient.getModule(ApiTalk.class).getSpeakRight(SdkParamsCenter.Talk.GetSpeakRight()
                            .setnTrunkChannelID(currentBean.nTrunkChannelID)
                            .setEnableAudioAmplitude(true)
                            .setStrTrunkChannelDomainCode(currentBean.strTrunkChannelDomainCode),
                    new CallbackStartGetRight() {
                        @Override
                        public void getRightSuccess(CGetSpeakRightRsp info) {
                            doVibrate();

                        }

                        @Override
                        public void getRightError(ErrorInfo err) {
                            showToast(AppUtils.getString(R.string.qiangmai_false));
                            isMyselfSpeaking = false;
                        }

                        @Override
                        public void startTrunkChannelSpeakSuccess(CStartTrunkChannelSpeakRsp status) {
                            if (listener != null) {
                                listener.start();
                            }
                        }

                        @Override
                        public void startTrunkChannelSpeakError(ErrorInfo err) {
                            showToast(AppUtils.getString(R.string.open_power_false));
                            isMyselfSpeaking = false;
                        }

                        @Override
                        public void startCaptureError(ErrorInfo error) {
                            showToast(AppUtils.getString(R.string.start_capture_false));
                            isMyselfSpeaking = false;
                        }

                        @Override
                        public void onSuccess(CStartMobileCaptureRsp resp) {
                        }


                        @Override
                        public void onError(ErrorInfo error) {
                            showToast(AppUtils.getString(R.string.time_out));
                            isMyselfSpeaking = false;
                        }
                    });

        }
    }

    /**
     * 请求对讲频道信息
     */
    private void queryChannelData() {
        HYClient.getModule(ApiTalk.class).queryTrunkChannel(SdkParamsCenter.Talk.QueryTrunkChannel()
                        .setStrTcUserID(AppAuth.get().getUserID() + "")
                        .setnTrunkChannelType(TrunkChannelBean.CHANNLE_TYPE_DEFAULT|TrunkChannelBean.CHANNLE_TYPE_STATIC|TrunkChannelBean.CHANNLE_TYPE_TMP_ING)
                        .setStrTcUserDomainCode(AppAuth.get().getDomainCode())
                        .setnSize(9999),
                new SdkCallback<CQueryTrunkChannelListRsp>() {
                    @Override
                    public void onSuccess(CQueryTrunkChannelListRsp resp) {
                        datas.clear();
                        for (TrunkChannelBean trunkChannelBean : resp.lstTrunkChannelInfo) {
                            if (trunkChannelBean.nTrunkChannelType != TrunkChannelBean.CHANNLE_TYPE_TMP_FINISH) {
                                datas.add(trunkChannelBean);
                            }
                            if (trunkChannelBean.nTrunkChannelType == TrunkChannelBean.CHANNLE_TYPE_DEFAULT){
                                defaultChannelBean = trunkChannelBean;
                            }
                        }
                        //如果当前的频道不为空,就找到,更新下
                        if (currentBean != null) {
                            boolean hasCurrent = false;
                            for (TrunkChannelBean temp : datas) {
                                if (temp.nTrunkChannelID == currentBean.nTrunkChannelID) {
                                    hasCurrent = true;
                                    temp.extr = currentBean.extr;
                                    currentBean = temp;
                                    break;
                                }
                            }
                            if (!hasCurrent) {
                                datas.add(currentBean);
                            }
                        }

                        updateByUserInfo();
                        if (datas.size() <= 0) {
                            tv_title.setText(AppUtils.getString(R.string.group_empty));
                        }
                        updateChannelSize();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ErrorInfo error) {

                    }
                });
    }

    /**
     * 确认频道状态,如断网重连后,请求频道信息,如果没人说话,就设置空闲状态
     * 1 B手机在说话,本机显示B正在说话
     * 2 本手机断网
     * 3 断网重连成功后,请求频道状态,设为空闲
     * 4 切换到新的频道后,这个不需要,切换到新频道会有说话通知
     *
     * @param currentBean
     */
    private void ensureChannelInfo(final TrunkChannelBean currentBean) {
        Logger.debug(TAG+ " ensureChannelInfo " + currentBean.nTrunkChannelID + " " +currentBean.strTrunkChannelName);
        HYClient.getModule(ApiTalk.class)
                .getTrunkChannelInfo(SdkParamsCenter.Talk.GetTrunkChannelInfo()
                                .setnTrunkChannelID(currentBean.nTrunkChannelID)
                                .setStrTrunkChannelDomainCode(currentBean.strTrunkChannelDomainCode),
                        new SdkCallback<CGetTrunkChannelInfoRsp>() {
                            @Override
                            public void onSuccess(CGetTrunkChannelInfoRsp cGetTrunkChannelInfoRsp) {
                                Logger.debug(TAG+" ensureChannelInfo success " + cGetTrunkChannelInfoRsp.toString());
                                //频道状态
                                //0：空闲
                                //1：抢占发言中
                                if (cGetTrunkChannelInfoRsp.nTrunkChannelStatus == 0) {
                                    setTrunkChannelIdle();
                                } else if (cGetTrunkChannelInfoRsp.nTrunkChannelStatus == 1) {
                                    //频道的状态是有人在说话,但是本地没有当前的说话者,所以找到当前说话者并播放
                                    if (currentSpeak == null) {
                                        for (TrunkChannelUserBean channelUser : cGetTrunkChannelInfoRsp.lstTrunkChannelUser) {
                                            if (channelUser.nUserStatus == 2) {
                                                CNotifyTrunkChannelUserPlaySpeakSet tmpUser = new CNotifyTrunkChannelUserPlaySpeakSet();
                                                tmpUser.strTrunkChannelDomainCode = cGetTrunkChannelInfoRsp.strTrunkChannelDomainCode;
                                                tmpUser.nPlaySet = 0;
                                                tmpUser.nTrunkChannelID = cGetTrunkChannelInfoRsp.nTrunkChannelID;
                                                tmpUser.strSpeakUserDomainCode = channelUser.strTcUserDomainCode;
                                                tmpUser.strSpeakUserTokenID = channelUser.strTcUserTokenID;
                                                tmpUser.strSpeakUserName = channelUser.strTcUserName;
                                                startPlayVoice(tmpUser,currentBean);
                                            }
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                Logger.debug(TAG + " ensureChannelInfo onError " + errorInfo.toString());

                            }
                        });
    }


    /**
     * 确认当前的对讲组,当被踢掉或者频道被删除的时候,要进入上一次或者默认频道
     */
    private void ensureCurrentChannel(){
        if (currentBean == null){
            if (lastChannelBean != null){
                acceptNewChannel(lastChannelBean);
            }else {
                if (defaultChannelBean != null){
                    acceptNewChannel(defaultChannelBean);
                }
            }
        }
    }

    /**
     * 请求用户信息,用户信息里面会返回当前的对讲频道,然后设置对讲频道
     * 当被踢掉或者频道被删除的时候,要进入默认频道
     */
    private void updateByUserInfo() {
        isMyselfSpeaking = false;
        if (listener != null) {
            listener.onStopSpeak("779");
        }

        final CQueryUserListReq.UserInfo userInfo = new CQueryUserListReq.UserInfo();
        userInfo.strUserID = AppDatas.Auth().getUserID() + "";

        HYClient.getModule(ApiSocial.class).getUsers(SdkParamsCenter.Social.GetUsers()
                        .setDomainCode(HYClient.getSdkOptions().User().getDomainCode())
                        .addUser(userInfo),
                new SdkCallback<ArrayList<CQueryUserListRsp.UserInfo>>() {
                    @Override
                    public void onSuccess(ArrayList<CQueryUserListRsp.UserInfo> userInfos) {
                        if (userInfos != null && userInfos.size() > 0) {
                            for (TrunkChannelBean temp : datas) {
                                if (temp.nTrunkChannelID == userInfos.get(0).nTrunkChannelID) {
                                    temp.extr = true;
                                    currentBean = temp;
                                    ensureChannelInfo(currentBean);
                                } else {
                                    temp.extr = false;
                                }
                            }
                            changeTitle();
                            ensureCurrentChannel();
                            updateChannelSize();
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                    }
                });
    }

    /**
     * 两种情况进入
     * 1 用户被从频道踢出
     * 2 频道被删除
     */
    private void dealKickOut() {

        AlarmMediaPlayer.get().play(AlarmMediaPlayer.SOURCE_PTT_VOICE);
        isMyselfSpeaking = false;
        if (listener != null) {
            listener.onStopSpeak("825");
        }
        updateByUserInfo();
    }



    /**
     * 用户被频道踢出,频道被删除,要去掉对应的去掉
     *
     * @param deleteTrunkChannelID
     */
    private void deleteOneGroup(int deleteTrunkChannelID) {
        //删除列表里的
        for (TrunkChannelBean temp : datas) {
            //最后一次进入的频道被删除了
            if (lastChannelBean != null && temp.nTrunkChannelID == lastChannelBean.nTrunkChannelID){
                lastChannelBean = null;
            }

            //默认频道被删除了
            if (defaultChannelBean != null && temp.nTrunkChannelID == defaultChannelBean.nTrunkChannelID){
                defaultChannelBean = null;
            }

            if (temp.nTrunkChannelID == deleteTrunkChannelID) {
                datas.remove(temp);
                break;
            }
        }

        //如果删除的是当前的,更新当前的
        if (currentBean.nTrunkChannelID == deleteTrunkChannelID) {
            currentBean = null;
            //用户被从频道中踢出
            dealKickOut();
        }


    }

    private void changeTitle() {
        if (currentBean != null) {
            tv_title.setText(currentBean.strTrunkChannelName);

            if (listener != null) {
                listener.onChangeChannel(currentBean);
            }
        } else {
            tv_title.setText(AppUtils.getString(R.string.group_empty));
            closeReqStatus("890");
        }
        if (datas.size() <= 0) {
            tv_title.setText(AppUtils.getString(R.string.group_empty));
            closeReqStatus("894");
        }
    }

    private void updateChannelSize() {
        String strFormat = AppUtils.getString(R.string.channel_change_size);
        String str = String.format(strFormat, adapter.getLitmit());
        tv_channel_size.setText(String.format(str));

    }

    /**
     * 把当前频道设置为空闲
     */
    private void setTrunkChannelIdle(){
        closeReqStatus("742");
        if (currentBean != null){
            closePlayer(currentBean.nTrunkChannelID);
        }
    }
    private void closeReqStatus(String from) {
        currentSpeak = null;
        tv_msg.setText(AppUtils.getString(R.string.now_is_kongxian));
    }



    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    public void changeChannelLeft(){
        if (datas != null && datas.size() > 0){
            if (currentBean != null){
                int findPos = 0;
                for (int i = 0 ; i < datas.size() ; i++){
                    if (datas.get(i).nTrunkChannelID == currentBean.nTrunkChannelID){
                        findPos = i;
                        break;
                    }
                }
                int newPos = findPos == 0 ? 0: findPos-1;
                acceptNewChannel(datas.get(newPos));
            }
        }
    }

    public void changeChannelRight(){
        if (datas != null && datas.size() > 0){
            if (currentBean != null){
                int findPos = 0;
                for (int i = 0 ; i < datas.size() ; i++){
                    if (datas.get(i).nTrunkChannelID == currentBean.nTrunkChannelID){
                        findPos = i;
                        break;
                    }
                }
                int newPos = findPos == datas.size()-1 ? datas.size()-1: findPos+1;
                acceptNewChannel(datas.get(newPos));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_middle:
            case R.id.rv_list:
                if (rv_list.getVisibility() == VISIBLE) {
                    hideList();
                } else {
                    showList();
                    //showList会调用动画,如果同时再进行网络请求会卡顿,所以等待300毫秒后再请求
                    rxUtils.doDelayOn(300, new RxUtils.IMainDelay() {
                        @Override
                        public void onMainDelay() {
                            queryChannelData();
                        }
                    });
                    if (listener != null) {
                        listener.onShowList();
                    }
                }
                break;
        }
    }

    public void startPTT() {
        if (AppUtils.isVideo || AppUtils.isTalk || AppUtils.isMeet) {
            AppUtils.showMsg(false, false);
            return;
        }
        qiangChannel();
    }

    public void stopPTT() {
        if (isMyselfSpeaking) {
            if (speakSdkCallers != null) {
                speakSdkCallers.cancel();
                speakSdkCallers = null;
            }
            stopMyselfSpeak();
        }
    }

    public void destroyEvent() {
        stopMyselfSpeak();
        closeAllPlayer();
        for (SdkCaller oneCaller : sdkCallers) {
            oneCaller.cancel();
        }
        EventBus.getDefault().unregister(this);
    }

    public void setChangeChannelListener(OnChangeChannelListener listener) {
        this.listener = listener;
    }


    public void hideList() {
        dropDownAnimator.hide();
        tv_title.setCompoundDrawables(null, null, drawableDown, null);
    }

    public void showList() {
        dropDownAnimator.show();
        tv_title.setCompoundDrawables(null, null, drawableUp, null);
    }

    private void initSound() {
        audio = new AppAudioManagerWrapper();
        audio.start();
    }

    private void closeSpeakLoud() {
        if (audio != null) {
            audio.stop();
            audio = null;
        }
    }

    private void doVibrate() {
        if (getContext() == null) {
            return;
        }
        Vibrator vibrator =
                (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        vibrator.vibrate(100);
    }

    /**
     * 暂停播放声音,停止自己说话
     */
    public void acceptWaite() {
//        closeReqStatus("1014");
        stopMyselfSpeak();
        pauseAllPlayer();
    }

    /**
     * 进入p2p模式后,改变下样式
     */
    public void changeToP2P(){
        ll_disconnect.setVisibility(View.GONE);
        rl_container.setBackgroundColor(ActivityCompat.getColor(getContext(),R.color.red));
        tv_middle_p2p.setVisibility(View.VISIBLE);
        ll_middle.setVisibility(View.GONE);
    }

    /**
     * 回到正常样式
     */
    public void changeToNormal(){
        ll_disconnect.setVisibility(View.GONE);
        rl_container.setBackgroundColor(ActivityCompat.getColor(getContext(),R.color.colorPrimary));
        tv_middle_p2p.setVisibility(View.GONE);
        ll_middle.setVisibility(View.VISIBLE);
    }


    public void showConnectRetry(int remainTry){
        ll_disconnect.setVisibility(View.VISIBLE);
        String strFormat = AppUtils.getString(R.string.p2p_disconnect_info);
        String info = String.format(strFormat,remainTry+"");
        tv_disconnect_info.setText(info);

    }

    public void hideConnectRetry(){
        ll_disconnect.setVisibility(View.GONE);
    }




    public interface OnMenuItemClickListener{
        void onLeftMenuClick();
        void onContactMenuClick();
        void onChatMenuClick();
        void onEnterP2pClick();
    }


    public interface OnChangeChannelListener {
        void onChangeChannel(TrunkChannelBean bean);

        void onStopSpeak(String from);

        void start();

        void onShowList();

        void onSpeakWillFinish();

    }

}
