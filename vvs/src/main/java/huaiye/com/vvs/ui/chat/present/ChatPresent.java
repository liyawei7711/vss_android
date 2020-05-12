package huaiye.com.vvs.ui.chat.present;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.media.player.HYPlayer;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.social.CSendMsgToMuliteUserRsp;
import com.huaiye.sdk.sdpmsgs.social.CSendMsgToUserRsp;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import huaiye.com.vvs.BuildConfig;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AlarmMediaPlayer;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.BroadcastManage;
import huaiye.com.vvs.dao.msgs.BroadcastMessage;
import huaiye.com.vvs.dao.msgs.ChatMessages;
import huaiye.com.vvs.dao.msgs.DeviceBean;
import huaiye.com.vvs.dao.msgs.PlayerMessage;
import huaiye.com.vvs.dao.msgs.VssMessageBean;
import huaiye.com.vvs.dao.msgs.VssMessageListBean;
import huaiye.com.vvs.dao.msgs.VssMessageListMessages;
import huaiye.com.vvs.models.device.bean.DevicePlayerBean;
import huaiye.com.vvs.ui.chat.ChatPlayHelper;
import huaiye.com.vvs.ui.chat.holder.ChatViewHolder;
import huaiye.com.vvs.ui.chat.iview.IChatView;
import huaiye.com.vvs.ui.device.DevicePlayRealActivity;
import huaiye.com.vvs.ui.home.MediaOnlineVideoPlayActivity;

import static huaiye.com.vvs.common.AppUtils.BROADCAST_AUDIO_FILE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.BROADCAST_AUDIO_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.BROADCAST_VIDEO_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_DEVICE_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_PERSON_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_CODE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_FILE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_IMG_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: ChatPresent
 */

public class ChatPresent implements AlarmMediaPlayer.PlayerListener {
    IChatView iChatView;
    ArrayList<VssMessageBean> datas = new ArrayList<>();
    LiteBaseAdapter<VssMessageBean> adapter;
//        types.add("docx");
//        types.add("doc");
//        types.add("pdf");
//        types.add("xls");
//        types.add("xlsx");
//        types.add("ppt");
//        types.add("pptx");
//        types.add("log");
//        types.add("txt");
//        types.add("java");

    Gson gson = new Gson();
    private HashMap<String, Boolean> playMap = ChatPlayHelper.get().getPlayMap();

    private static final String[][] MIME_MapTable={
            //{后缀名，    MIME类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",      "image/bmp"},
            {".c",        "text/plain"},
            {".class",    "application/octet-stream"},
            {".conf",    "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".docx",    "application/msword"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",    "application/x-gtar"},
            {".gz",        "application/x-gzip"},
            {".h",        "text/plain"},
            {".htm",    "text/html"},
            {".html",    "text/html"},
            {".jar",    "application/java-archive"},
            {".java",    "text/plain"},
            {".jpeg",    "image/jpeg"},
            {".JPEG",    "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js",        "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",    "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",    "video/mp4"},
            {".mpga",    "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".pptx",    "application/vnd.ms-powerpoint"},
            {".prop",    "text/plain"},
            {".rar",    "application/x-rar-compressed"},
            {".rc",        "text/plain"},
            {".rmvb",    "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh",        "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml",    "text/plain"},
            {".z",        "application/x-compress"},
            {".zip",    "application/zip"},
            {"",        "*/*"}
    };

    private String getMIMEType(File file) {

        String type="*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0)
            return type;
        /* 获取文件的后缀名 */
        String fileType = fName.substring(dotIndex,fName.length()).toLowerCase();
        if(fileType == null || "".equals(fileType))
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){
            if(fileType.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    public ChatPresent(final IChatView iChatView, String sessionID) {
        this.iChatView = iChatView;
        adapter = new LiteBaseAdapter<>(iChatView.getContext(),
                datas,
                ChatViewHolder.class,
                R.layout.item_chat_view,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.text_left) {
                            VssMessageBean bean = (VssMessageBean) v.getTag(R.id.tag_bean);

                            if (bean.type == PLAYER_TYPE_PERSON_INT) {

                                if (System.currentTimeMillis() - bean.time > 1000 * 60 * 60 * 24) {
                                    showToast(AppUtils.getString(R.string.time_out_share));
                                    return;
                                }

                                if (AppUtils.isTalk || AppUtils.isVideo || AppUtils.isMeet) {
                                    AppUtils.showMsg(false, false);
                                    return;
                                }

                                EventBus.getDefault().post(new PlayerMessage(bean.type, bean.fromUserId, bean.fromUserDomain, bean.fromUserName, bean.fromUserTokenId, bean.content));
                            } else if (bean.type == ZHILING_FILE_TYPE_INT) {
                                if (!bean.fromUserId.equals(AppDatas.Auth().getUserID() + "")) {
                                    //Uri uri = Uri.parse(AppDatas.Constants().getAddressWithoutPort() + bean.content);
                                    //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    //iChatView.getContext().startActivity(intent);
                                    final File fC = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/files/chat/");
                                    fC.mkdir();
                                    final  String content = bean.content;
                                    final int nEncrypt  = bean.nEncrypt;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final String fileLocal = fC + content.substring(content.lastIndexOf("/"));
                                            final File ffLocal = new File(fileLocal);
                                            if (downloadFileByUrl(AppDatas.Constants().getAddressWithoutPort() + content,fileLocal))
                                            {
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final File fde = new File(fileLocal.substring(0,fileLocal.lastIndexOf(".")));
                                                        if (1 == nEncrypt)
                                                        {
                                                            if (fde.exists())
                                                            {
                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                intent.setDataAndType(Uri.fromFile(fde),getMIMEType(fde));
                                                                iChatView.getContext().startActivity(intent);
                                                            }
                                                            else
                                                            {
                                                                 if (HYClient.getSdkOptions().encrypt().isEncryptBind())
                                                                 {
                                                                     HYClient.getModule(ApiEncrypt.class)
                                                                             .encryptFile(
                                                                                     SdkParamsCenter.Encrypt.EncryptFile()
                                                                                             .setSrcFile(fileLocal)
                                                                                             .setDstFile(fde.getPath())
                                                                                             .setDoEncrypt(false),
                                                                                     new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                                                                         @Override
                                                                                         public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                                                             Log.i("MCApp_tt", "resp: " + resp.m_strData);
                                                                                             //showExplorer
                                                                                             Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                                             intent.addCategory(Intent.CATEGORY_DEFAULT);
                                                                                             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                             intent.setDataAndType(Uri.fromFile(fde),getMIMEType(fde));
                                                                                             iChatView.getContext().startActivity(intent);
                                                                                         }

                                                                                         @Override
                                                                                         public void onError(ErrorInfo error) {
                                                                                             showToast(AppUtils.getString(R.string.chat_decrypt_fail));
                                                                                         }
                                                                                     }
                                                                             );
                                                                 }
                                                                 else
                                                                 {
                                                                     showToast(AppUtils.getString(R.string.chat_decrypt_fail));
                                                                 }
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            intent.setDataAndType(Uri.fromFile(ffLocal),getMIMEType(ffLocal));
                                                            iChatView.getContext().startActivity(intent);
                                                        }
                                                    }
                                                });

                                            }
                                            else
                                            {
                                                //下载失败
                                                showToast(AppUtils.getString(R.string.chat_download_fail));
                                            }
                                        }
                                    }).start();
                                }
                            } else if (bean.type == PLAYER_TYPE_DEVICE_INT) {

                                if (AppUtils.isTalk || AppUtils.isVideo || AppUtils.isMeet) {
                                    AppUtils.showMsg(false, false);
                                    return;
                                }

                                DevicePlayerBean dpb = new DevicePlayerBean();
                                DeviceBean deviceBean = gson.fromJson(bean.content, DeviceBean.class);
                                if (deviceBean.strMainUrl.isEmpty()) {
                                    return;
                                }
                                if (deviceBean.strMainUrl.split("-").length != 4) {
                                    return;
                                }

                                dpb.strDomainCode = deviceBean.strMainUrl.split("-")[0];
                                dpb.strDeviceCode = deviceBean.strMainUrl.split("-")[1];
                                dpb.strChannelCode = deviceBean.strMainUrl.split("-")[2];
                                dpb.strStreamCode = deviceBean.strMainUrl.split("-")[3];
                                dpb.strSubStreamCode = deviceBean.strSubUrl.split("-")[3];
                                Intent intent = new Intent(iChatView.getContext(), DevicePlayRealActivity.class);
                                intent.putExtra("data", dpb);
                                iChatView.getContext().startActivity(intent);
                            } else {
                                BroadcastMessage message = BroadcastManage.get().getMessages(bean.content);
                                switch (message.getState()) {
                                    case BroadcastMessage.DOWNING:
                                        showToast(AppUtils.getString(R.string.downloading));
                                        break;
                                    case BroadcastMessage.ERROR:
                                        Intent intent = new Intent(iChatView.getContext(), MediaOnlineVideoPlayActivity.class);
                                        intent.putExtra("path", bean.content);
                                        iChatView.getContext().startActivity(intent);
                                        break;
                                    case BroadcastMessage.SUCCESS:
                                        if (bean.type == BROADCAST_AUDIO_TYPE_INT || bean.type == BROADCAST_AUDIO_FILE_TYPE_INT) {

                                            if (AppUtils.isTalk || AppUtils.isVideo || AppUtils.isMeet) {
                                                AppUtils.showMsg(false, false);
                                                return;
                                            }

                                            ChatViewHolder holder = (ChatViewHolder) v.getTag(R.id.tag_holder);
                                            int itemPosition = (int) v.getTag(R.id.tag_position);
                                            startVoice(bean, itemPosition);
                                        } else if (bean.type == BROADCAST_VIDEO_TYPE_INT) {
                                            if (AppUtils.isTalk || AppUtils.isVideo || AppUtils.isMeet) {
                                                AppUtils.showMsg(false, false);
                                                return;
                                            }
                                            Intent intentPlay = new Intent(iChatView.getContext(), MediaOnlineVideoPlayActivity.class);
                                            intentPlay.putExtra("path", bean.content);
                                            iChatView.getContext().startActivity(intentPlay);
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }, playMap);
        datas.addAll(ChatMessages.get().getMessages(sessionID));
    }

    private void startVoice(final VssMessageBean voiceData, final int itemPosition) {
        //音频类型区分播放.dat或.wav
        AlarmMediaPlayer.PlayBean currentPlay = AlarmMediaPlayer.get().getCurrentPlayBean();


        //当前正在播放,且播放的是.wav
        if (currentPlay != null && currentPlay.sourceType == AlarmMediaPlayer.SOURCE_CUSTOM){
            AlarmMediaPlayer.get().stop();

            int lastItemPosition = ChatPlayHelper.get().getLastItemPosition();

            stopLastItemAnimator();

            //准备播放的是当前播放的,停止播放后就什么都不需要做了
            if (lastItemPosition == itemPosition){
                return;
            }
            //开始新的播放
            playLocalAudio(voiceData,itemPosition);
        }else if (ChatPlayHelper.get().getVideoParam() != null ){
            final int lastItemPosition = ChatPlayHelper.get().getLastItemPosition();

            stopLastItemAnimator();
            //这里是正在播放,且播放的.dat
            HYClient.getHYPlayer().stopPlay(new SdkCallback<VideoParams>() {
                @Override
                public void onSuccess(VideoParams videoParams) {
                    ChatPlayHelper.get().setVideoParam(null);
                    //准备播放的是当前播放的,停止播放后就什么都不需要做了
                    if (lastItemPosition == itemPosition){
                        return;
                    }
                    playLocalAudio(voiceData,itemPosition);
                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    ChatPlayHelper.get().setVideoParam(null);
                    //准备播放的是当前播放的,停止播放后就什么都不需要做了
                    if (lastItemPosition == itemPosition){
                        return;
                    }
                    playLocalAudio(voiceData,itemPosition);
                }
            }, ChatPlayHelper.get().getVideoParam());
        }else {
            //当前没有播放中的内容,直接播放
            playLocalAudio(voiceData,itemPosition);
        }






    }

    private void stopLastItemAnimator(){
        //停止播放
        int lastItemPosition = ChatPlayHelper.get().getLastItemPosition();
        ChatPlayHelper.get().setLastItemPosition(ChatPlayHelper.INVALID_POSITION);
        ChatPlayHelper.get().getPlayMap().clear();
        if (lastItemPosition != ChatPlayHelper.INVALID_POSITION) {
            adapter.notifyItemChanged(lastItemPosition);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    private void playLocalAudio(VssMessageBean voiceData, int itemPosition){
        ChatPlayHelper.get().setLastItemPosition(itemPosition);
        if (voiceData.type == BROADCAST_AUDIO_FILE_TYPE_INT){
            playLocalAudioDat(itemPosition,voiceData.content);
        }else {
            playLocalAudioWav(voiceData,itemPosition);
        }
    }

    //播放dat形式音频
    private void playLocalAudioDat(final int itemPosition, final String path) {
        VideoParams videoParams = Player.Params.TypeVideoOfflineRecord().setResourcePath(AppUtils.audiovideoPath + "/" + AppUtils.subPath(path));
        ChatPlayHelper.get().setVideoParam(videoParams);
        HYClient.getHYPlayer().startPlay(videoParams
                .setMixCallback(new VideoCallbackWrapper() {
                    @Override
                    public void onSuccess(VideoParams param) {
                        super.onSuccess(param);
                        playMap.put(AppUtils.subPath(path), true);
                        adapter.notifyItemChanged(itemPosition);
                    }

                    @Override
                    public void onGetVideoRange(VideoParams param, int start, int end) {
                        super.onGetVideoRange(param, start, end);
                    }

                    @Override
                    public void onVideoProgressChanged(VideoParams param, HYPlayer.ProgressType type, int current, int total) {
                        super.onVideoProgressChanged(param, type, current, total);
                    }

                    @Override
                    public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                        super.onVideoStatusChanged(param, msg);
                        SdkMsgNotifyPlayStatus status = (SdkMsgNotifyPlayStatus) msg;
                        ChatPlayHelper.get().setVideoParam(null);
                        if (status.isStopped()
                                && !((AppBaseActivity) iChatView).isFinishing()) {
                            playMap.put(AppUtils.subPath(path), false);
                            adapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                        super.onError(param, errorInfo);
                        ChatPlayHelper.get().setVideoParam(null);
                        playMap.put(AppUtils.subPath(path), false);
                        adapter.notifyDataSetChanged();
                        showToast(AppUtils.getString(R.string.play_audio_error));
                    }
                }));

    }


    public void playLocalAudioWav(VssMessageBean bean, int itemPosition) {
        playMap.put(AppUtils.subPath(bean.content), true);
        String path = AppUtils.audiovideoPath + "/" + AppUtils.subPath(bean.content);
        AlarmMediaPlayer.get().play(true, AlarmMediaPlayer.SOURCE_CUSTOM, path);
        adapter.notifyItemChanged(itemPosition);
    }


    public void startListener() {
        AlarmMediaPlayer.get().addPlayerListener(this);
    }


    public void stopListener() {
        AlarmMediaPlayer.get().removePlayerListener(this);
    }

//    private void startAnimation(final ChatViewHolder viewHolder, int type) {
//        viewHolder.getVoid_img().setImageResource(R.drawable.record_voice_anim);
//        AnimationDrawable mImageAnim = (AnimationDrawable) viewHolder.getVoid_img().getDrawable();
//        mImageAnim.start();
//        viewHolder.setmImageAnim(mImageAnim);
//    }


    public void onSendClicked(String sessionID, String sessionName, String
            sessionDomain, String str, ArrayList<SendUserBean> sessionUserList, VssMessageListBean
                                      listBean, int type) {
        final VssMessageBean bean = new VssMessageBean();
        bean.content = str;
        bean.contentSrc = str;
        bean.type = type;
        bean.sessionID = sessionID;
        bean.sessionName = sessionName;
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserName = AppDatas.Auth().getUserName();
        if (listBean != null) {
            bean.sessionUserList.addAll(listBean.sessionUserList);
            bean.groupType = listBean.groupType;
            bean.groupDomainCode = listBean.groupDomainCode;
            bean.groupID = listBean.groupID;
        } else {
            bean.sessionUserList.addAll(sessionUserList);
            if (TextUtils.isEmpty(sessionDomain)) {
                bean.groupType = 0;
                bean.groupDomainCode = AppDatas.Auth().getDomainCode();
                bean.groupID = AppDatas.Auth().getUserID() + "";
            }
        }
        bean.time = System.currentTimeMillis();

        bean.nEncrypt = 0;
        if (HYClient.getSdkOptions().encrypt().isEncryptBind())
        {
            if(type == ZHILING_CODE_TYPE_INT)
            {
                HYClient.getModule(ApiEncrypt.class)
                        .encryptTextMsg(
                                SdkParamsCenter.Encrypt.EncryptTextMsg()
                                        .setM_strData(str)
                                        .setDoEncrypt(true),
                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                        bean.content = resp.m_strData;
                                        bean.nEncrypt = 1;
                                        sendClicked(bean);
                                    }

                                    @Override
                                    public void onError(ErrorInfo error) {
                                        bean.nEncrypt = 0;
                                        sendClicked(bean);
                                    }
                                }
                        );
            }
            else
            {
                bean.nEncrypt = 1;
                sendClicked(bean);
            }
        }
        else
        {
            sendClicked(bean);
        }
    }

    public void sendClicked(final VssMessageBean bean)
    {
        HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                        .setIsImportant(true)
                        .setMessage(bean.toString())
                        .setnGroupType(bean.groupType)
                        .setUser(bean.sessionUserList),
                new SdkCallback<CSendMsgToMuliteUserRsp>() {
                    @Override
                    public void onSuccess(CSendMsgToMuliteUserRsp resp) {
//                        showInfo("发送成功...");
//                        ChatUtil.get().saveMySendMsg(bean);
                        datas.add(bean);
                        adapter.notifyDataSetChanged();
                        iChatView.moveToPosition(adapter.getItemCount() - 1);
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                    }
                });

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && (bean.type == ZHILING_FILE_TYPE_INT || bean.type == ZHILING_IMG_TYPE_INT))
        {
            deleteSingleFile(bean.content);
        }
    }

    public void onSendClickedGroup(String sessionID, String sessionName, String
            sessionDomain, String str, int type) {
        final VssMessageBean bean = new VssMessageBean();
        bean.content = str;
        bean.contentSrc = str;
        bean.type = type;
        bean.sessionID = sessionID;
        bean.sessionName = sessionName;
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.groupType = 2;
        bean.groupDomainCode = sessionDomain;
        bean.groupID = sessionID;
        bean.time = System.currentTimeMillis();

        bean.nEncrypt = 0;
        if (HYClient.getSdkOptions().encrypt().isEncryptBind())
        {
            if(type == ZHILING_CODE_TYPE_INT)
            {
                HYClient.getModule(ApiEncrypt.class)
                        .encryptTextMsg(
                                SdkParamsCenter.Encrypt.EncryptTextMsg()
                                        .setM_strData(str)
                                        .setDoEncrypt(true),
                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                        bean.content = resp.m_strData;
                                        bean.nEncrypt = 1;
                                        sendClickedGroup(bean);
                                    }

                                    @Override
                                    public void onError(ErrorInfo error) {
                                        bean.nEncrypt = 0;
                                        showToast(AppUtils.getString(R.string.chat_encrypt_fail));
                                        //sendClickedGroup(bean);
                                    }
                                }
                        );
            }
            else
            {
                HYClient.getModule(ApiEncrypt.class)
                        .encryptFile(
                                SdkParamsCenter.Encrypt.EncryptFile()
                                        .setSrcFile(str)
                                        .setDstFile(str + ".encrypt")
                                        .setDoEncrypt(true),
                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                        //tvTip.setText("文件加密成功，输出文件为:" + resp.m_strData);
                                        bean.content = resp.m_strData;
                                        bean.nEncrypt = 1;
                                        sendClickedGroup(bean);
                                    }

                                    @Override
                                    public void onError(ErrorInfo error) {
                                        //tvTip.setText("文件加密失败");
                                        showToast(AppUtils.getString(R.string.chat_encrypt_fail));
                                        bean.nEncrypt = 0;
                                        //sendClickedGroup(bean);
                                    }
                                }
                        );
            }
        }
        else
        {
            sendClickedGroup(bean);
        }
    }

    public void sendClickedGroup(final VssMessageBean bean)
    {
        HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMessage()
                        .setIsImportant(true)
                        .setMessage(bean.toString())
                        .setReceiverUserId(bean.sessionID)
                        .setReceiverUserDomainCode(bean.groupDomainCode)
                        .isGroup(),
                new SdkCallback<CSendMsgToUserRsp>() {
                    @Override
                    public void onSuccess(CSendMsgToUserRsp resp) {
//                        showInfo("发送成功...");
//                        ChatUtil.get().saveMySendMsg(bean);
                        datas.add(bean);
                        adapter.notifyDataSetChanged();
                        iChatView.moveToPosition(adapter.getItemCount() - 1);
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                    }
                });
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && (bean.type == ZHILING_FILE_TYPE_INT || bean.type == ZHILING_IMG_TYPE_INT))
        {
            deleteSingleFile(bean.content);
        }
    }

    public void onReceiverMsg(VssMessageBean obj) {
        datas.add(obj);
        adapter.notifyDataSetChanged();
        iChatView.moveToPosition(adapter.getItemCount() - 1);
        //找到VssMessageListMessages中对应的item,设置为read
        //因为现在在详情页,所以有消息过来就算读过了
        List<VssMessageListBean> msgListBean = VssMessageListMessages.get().getMessages();
        if (msgListBean != null) {
            for (VssMessageListBean item : msgListBean) {
                if (item.sessionID.equals(obj.sessionID)) {
                    VssMessageListMessages.get().del(item);
                    item.isRead = 1;
                    VssMessageListMessages.get().add(item);
                }
            }
        }
    }

    public LiteBaseAdapter getAdapter() {
        return adapter;
    }

    public void onDestroy() {
        stopListener();
        AlarmMediaPlayer.get().stop();
        if (ChatPlayHelper.get().getVideoParam() != null && !TextUtils.isEmpty(ChatPlayHelper.get().getVideoParam().getResourcePath())) {
            String path = AppUtils.subPath(ChatPlayHelper.get().getVideoParam().getResourcePath());
            if (playMap.containsKey(path) && playMap.get(path)) {
                HYClient.getHYPlayer().stopPlay(null, ChatPlayHelper.get().getVideoParam());
                playMap.put(path, false);
            }
        }

    }

    public void onPause() {
        AlarmMediaPlayer.get().stop();
        if (ChatPlayHelper.get().getVideoParam() != null && !TextUtils.isEmpty(ChatPlayHelper.get().getVideoParam().getResourcePath())) {
            String path = AppUtils.subPath(ChatPlayHelper.get().getVideoParam().getResourcePath());
            if (playMap.containsKey(path) && playMap.get(path)) {
                HYClient.getHYPlayer().stopPlay(null, ChatPlayHelper.get().getVideoParam());
                playMap.put(path, false);
            }
        }
    }

    @Override
    public void onComplete(AlarmMediaPlayer.PlayBean playBean) {
        if (playBean == null) {
            return;
        }
        stopLastItemAnimator();
    }

    @Override
    public void onError(AlarmMediaPlayer.PlayBean playBean) {
        if (playBean == null) {
            return;
        }
        stopLastItemAnimator();
        showToast(AppUtils.getString(R.string.play_audio_error));
    }

    private boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean downloadFileByUrl(final String urlLoadPath,final String fileName) {
        Log.i("MCApp_tt", "urlLoadPath: " + urlLoadPath + "  fileName:" + fileName);
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        HttpURLConnection httpURLConnection = null;

        //创建 这个文件名 命名的 file 对象
        File file = new File(fileName);
        // Log.i(TAG,"file: " + file);
        if (!file.exists()) {     //倘若没有这个文件
            // Log.i(TAG,"创建文件");
            //file.createNewFile();  //创建这个文件
        } else {
            //文件已存在，不重新下载
            return true;
        }
        try {
            URL url = new URL(urlLoadPath);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();
            if (code == 200) {
                //网络连接成功
                //根据响应获取文件大小
                int fileSize = httpURLConnection.getContentLength();
                // Log.i(TAG,"文件大小： " + fileSize);
                inputStream = httpURLConnection.getInputStream();
                fileOutputStream = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int tem = 0;
                while ((tem = inputStream.read(b)) != -1) {
                    fileOutputStream.write(b, 0, tem);
                }

            } else {
                return false;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        showToast(AppUtils.getString(R.string.chat_download_success));
        return true;
    }
}
