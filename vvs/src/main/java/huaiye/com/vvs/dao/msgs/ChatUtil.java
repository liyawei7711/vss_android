package huaiye.com.vvs.dao.msgs;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.social.CSendMsgToMuliteUserRsp;
import com.huaiye.sdk.sdpmsgs.social.CSendMsgToUserRsp;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.NewMessage;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ScreenNotify;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.auth.AppAuth;

import static huaiye.com.vvs.common.AppUtils.CAPTURE_TYPE;
import static huaiye.com.vvs.common.AppUtils.CAPTURE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_DEVICE_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_PERSON_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_audio_video;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_only_audio;
import static huaiye.com.vvs.common.AppUtils.STOP_CAPTURE_TYPE;
import static huaiye.com.vvs.common.AppUtils.STOP_CAPTURE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2018/05/30
 * version: 0
 * mail: secret
 * desc: ChatUtil
 */

public class ChatUtil {


    Gson gson;

    private ChatUtil() {
        if (gson == null) {
            gson = new Gson();
        }
    }

    static class Holder {
        static final ChatUtil SINGLETON = new ChatUtil();
    }

    public static ChatUtil get() {
        return ChatUtil.Holder.SINGLETON;
    }

    /**
     * 接收到的消息
     *
     * @param bean
     * @param needSend
     */
    public void saveChangeMsg(VssMessageBean bean, boolean needSend) {
        if (gson == null) {
            gson = new Gson();
        }
        //一对一聊天的标题要特殊处理
        //标题换为:对方的名字+"的指令调度"
        if (bean.sessionUserList != null && bean.sessionUserList.size() == 2 && bean.groupType == 0) {
            String myUserID = AppAuth.get().getUserID() + "";
            for (SendUserBean item : bean.sessionUserList) {
                if (!item.strUserID.equals(myUserID)) {
                    bean.sessionName = item.strUserName + AppUtils.getString(R.string.zhiling_diaodu);
                }
            }
        }

        if (bean.type == PLAYER_TYPE_PERSON_INT) {
            String str = String.format(AppUtils.getString(R.string.person_share_from), bean.fromUserName);
            bean.sessionName = str;
        }

        if (bean.type == PLAYER_TYPE_DEVICE_INT) {
            String str = String.format(AppUtils.getString(R.string.person_share_device_from), bean.fromUserName);
            bean.sessionName = str;
        }

        ChatMessages.get().add(bean);

        List<VssMessageListBean> allBean = VssMessageListMessages.get().getMessages();
        if (allBean != null && allBean.size() > 0) {
            boolean has = false;
            for (VssMessageListBean temp : allBean) {
                if (temp.sessionID.equals(bean.sessionID)) {
                    has = true;
                    break;
                }
            }
            if (has) {
                VssMessageListBean vssMessageListBean = getVssMessageListBean(bean);
                VssMessageListMessages.get().del(vssMessageListBean.sessionID);
                VssMessageListMessages.get().add(vssMessageListBean);
            } else {
                VssMessageListBean vssMessageListBean = getVssMessageListBean(bean);
                VssMessageListMessages.get().add(vssMessageListBean);
            }
        } else {
            VssMessageListBean vssMessageListBean = getVssMessageListBean(bean);
            VssMessageListMessages.get().add(vssMessageListBean);
        }

        if (needSend && bean.sessionID != null)
            EventBus.getDefault().post(bean);

        EventBus.getDefault().post(new NewMessage(VssMessageListMessages.get().getMessagesUnRead()));
        if (AppUtils.isHide) {
            ScreenNotify.get().wakeUpAndUnlock();
            ScreenNotify.get().openApplicationFromBackground();
            ScreenNotify.get().showScreenNotify(AppUtils.ctx, "0".equals(bean.sessionID) ? "新的广播消息" : "新的指令调度", bean.sessionName);
        }
    }

    /**
     * 自己发送的消息
     *
     * @param bean
     */
    public void saveMySendMsg(VssMessageBean bean) {
        ChatMessages.get().add(bean);

        List<VssMessageListBean> allBean = VssMessageListMessages.get().getMessages();
        if (allBean != null && allBean.size() > 0) {
            boolean has = false;
            for (VssMessageListBean temp : allBean) {
                if (temp.sessionID.equals(bean.sessionID)) {
                    has = true;
                    break;
                }
            }
            if (has) {
                VssMessageListBean vssMessageListBean = getVssMessageListBean(bean);
                VssMessageListMessages.get().del(vssMessageListBean.sessionID);
                VssMessageListMessages.get().add(vssMessageListBean);
            } else {
                VssMessageListBean vssMessageListBean = getVssMessageListBean(bean);
                VssMessageListMessages.get().add(vssMessageListBean);
            }
        } else {
            VssMessageListBean vssMessageListBean = getVssMessageListBean(bean);
            VssMessageListMessages.get().add(vssMessageListBean);
        }

    }

    @NonNull
    private VssMessageListBean getVssMessageListBean(VssMessageBean bean) {
        VssMessageListBean vssMessageListBean = new VssMessageListBean();
        vssMessageListBean.sessionID = bean.sessionID;
        vssMessageListBean.sessionName = bean.sessionName;
        vssMessageListBean.sessionUserList = bean.sessionUserList;
        vssMessageListBean.content = bean.content;
        vssMessageListBean.groupType = bean.groupType;
        vssMessageListBean.groupDomainCode = bean.groupDomainCode;
        vssMessageListBean.groupID = bean.groupID;
        vssMessageListBean.lastUserId = bean.fromUserId;
        vssMessageListBean.lastUserDomain = bean.fromUserDomain;
        vssMessageListBean.lastUserName = bean.fromUserName;
        vssMessageListBean.time = System.currentTimeMillis();
        vssMessageListBean.type = bean.type;
        if (TextUtils.isEmpty(bean.fromUserId)) {
            vssMessageListBean.isRead = 1;
        } else if (bean.fromUserId.equals(AppDatas.Auth().getUserID() + "")) {
            vssMessageListBean.isRead = 1;
        } else {
            vssMessageListBean.isRead = 0;
        }
        return vssMessageListBean;
    }

    /**
     * 观摩请求
     *
     * @param userId
     * @param domain
     * @param userName
     */
    public void reqGuanMo(String userId, String domain, String userName) {
        final VssMessageBean bean = new VssMessageBean();
        bean.content = CAPTURE_TYPE;
        bean.type = CAPTURE_TYPE_INT;
        bean.sessionID = "-1";
        bean.sessionName = AppUtils.getString(R.string.capture);
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.time = System.currentTimeMillis();
        bean.sessionUserList.add(new SendUserBean(userId, domain, userName));


        HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMessage()
                        .setIsImportant(false)
                        .setMessage(bean.toString())
                        .setReceiverUserDomainCode(domain)
                        .setReceiverUserId(userId),
                new SdkCallback<CSendMsgToUserRsp>() {
                    @Override
                    public void onSuccess(CSendMsgToUserRsp resp) {
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(AppUtils.getString(R.string.player_faile));
                    }
                });
    }

    public void rspGuanMo(String fromUserId, String fromUserDomain, String fromUserName,String sessionID) {
        com.huaiye.sdk.logger.Logger.debug("CaptureViewLayout sendPlayerMessage rspGuanMo" );
        final VssMessageBean bean = new VssMessageBean();
        if (AppUtils.isTalk) {
            bean.content = PLAYER_TYPE_only_audio;
        } else {
            bean.content = PLAYER_TYPE_audio_video;
        }
        bean.type = PLAYER_TYPE_INT;
        bean.sessionID = sessionID;
        bean.sessionName = AppUtils.getString(R.string.player);
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserTokenId = HYClient.getSdkOptions().User().getUserTokenId();
        bean.fromUserName = AppDatas.Auth().getUserName();

        bean.sessionUserList.add(new SendUserBean(fromUserId, fromUserDomain, fromUserName));

        bean.time = System.currentTimeMillis();

        HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                        .setIsImportant(false)
                        .setMessage(bean.toString())
                        .setUser(bean.sessionUserList),
                new SdkCallback<CSendMsgToMuliteUserRsp>() {
                    @Override
                    public void onSuccess(CSendMsgToMuliteUserRsp resp) {
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                    }
                });
    }

    public void closeGuanMoClose(String toUserId, String toUserDomain, String toUserName) {
        final VssMessageBean bean = new VssMessageBean();
        bean.content = STOP_CAPTURE_TYPE;
        bean.type = STOP_CAPTURE_TYPE_INT;
        bean.sessionID = "-1";
        bean.sessionName = AppUtils.getString(R.string.capture_stop);
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.time = System.currentTimeMillis();
        bean.sessionUserList.add(new SendUserBean(toUserId, toUserDomain, toUserName));


        HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMessage()
                        .setIsImportant(false)
                        .setMessage(bean.toString())
                        .setReceiverUserDomainCode(toUserDomain)
                        .setReceiverUserId(toUserId),
                new SdkCallback<CSendMsgToUserRsp>() {
                    @Override
                    public void onSuccess(CSendMsgToUserRsp resp) {
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast("");
                    }
                });
    }


}
