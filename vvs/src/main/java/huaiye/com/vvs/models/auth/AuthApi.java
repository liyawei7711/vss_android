package huaiye.com.vvs.models.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiAuth;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._options.OptionsUser;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.auth.CUploadLogInfoRsp;
import com.huaiye.sdk.sdpmsgs.auth.CUserRegisterRsp;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListReq;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListRsp;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import huaiye.com.vvs.BuildConfig;
import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.UploadFile;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ErrorMsg;
import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.common.dialog.LogicDialog;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.models.ConfigResult;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.ModelSDKErrorResp;
import huaiye.com.vvs.models.auth.bean.AuthUser;
import huaiye.com.vvs.models.auth.bean.ChangePwd;
import huaiye.com.vvs.models.auth.bean.Upload;
import huaiye.com.vvs.models.auth.bean.VersionData;
import huaiye.com.vvs.models.contacts.bean.PersonBean;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.download.DownloadApi;
import huaiye.com.vvs.models.download.DownloadService;
import huaiye.com.vvs.models.download.ErrorDialogActivity;
import huaiye.com.vvs.models.map.MapApi;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.callback.HTTPUIThreadCallbackAdapter;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
import ttyy.com.jinnetwork.core.work.method_post.PostContentType;

import static huaiye.com.vvs.common.AppUtils.STRING_KEY_needload;
import static huaiye.com.vvs.common.ErrorMsg.login_empty_code;
import static huaiye.com.vvs.common.ErrorMsg.login_err_code;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: AuthModel
 */

public class AuthApi {

    String URL;
    File tag = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.APPLICATION_ID + "_" + AppDatas.Auth().getUserName() + "_" + System.currentTimeMillis() + ".zip");
    File tagZip = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/files");

    private AuthApi() {
    }

    public static AuthApi get() {
        return new AuthApi();
    }


    public void changpwd(String old, String news, final ModelCallback<ChangePwd> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "vss/httpjson/mod_user_pwd";
        Https.post(URL)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", AppDatas.Auth().getUserID() + "")
                .addParam("strOldPassword", AppUtils.getMd5(old))
                .addParam("strNewPassword", AppUtils.getMd5(news))
                .setHttpCallback(new ModelCallback<ChangePwd>() {

                    @Override
                    public void onPreStart(HTTPRequest request) {
                        super.onPreStart(request);
                    }

                    @Override
                    public void onSuccess(ChangePwd response) {
                        if (callback == null)
                            return;

                        callback.onSuccess(response);

                    }

                    @Override
                    public void onFailure(HTTPResponse response) {
                        super.onFailure(response);
                        if (callback != null) {
                            callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_err_code, "HTTP ERROR AuthApi "));
                        }
                    }
                })
                .build()
                .requestNowAsync();

    }

    public void getServiceConfig(final ModelCallback<ConfigResult> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "vss/httpjson/get_vss_config_para";
        ArrayList<String> params = new ArrayList<>();
        params.add("FILE_SERVICE_IP");
        params.add("FILE_SERVICE_PORT");
        Https.post(URL)
                .addParam("lstVssConfigParaName",params)
                .setHttpCallback(new ModelCallback<ConfigResult>() {

                    @Override
                    public void onSuccess(ConfigResult response) {
                        if (response.nResultCode == 0) {
                            for (int i = 0 ; i < response.getLstVssConfigParaInfo().size() ; i ++){
                                ConfigResult.Info info = response.getLstVssConfigParaInfo().get(i);
                                if (info.getStrVssConfigParaName().equals("FILE_SERVICE_IP")){
                                    AppDatas.Constants().setFileAddress(info.getStrVssConfigParaValue());
                                }
                                if (info.getStrVssConfigParaName().equals("FILE_SERVICE_PORT")){
                                    AppDatas.Constants().setFilePort(Integer.parseInt(info.getStrVssConfigParaValue()));
                                }
                            }
                            callback.onSuccess(response);
                        }else {
                            callback.onFinish(null);
                        }
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {
                        super.onFinish(httpResponse);
                        if (callback != null) {
                            callback.onFinish(httpResponse);
                        }
                    }
                })
                .build()
                .requestNowAsync();

    }

    public void logout() {
//        URL = AppDatas.Constants().getAddressBaseURL() + "vss/httpjson/user_login_out";
//        Https.post(URL)
//                .addParam("strUserID", AppDatas.Auth().getUserID())
//                .setHttpCallback(new ModelCallback<Object>() {
//                    @Override
//                    public void onSuccess(Object response) {
//                    }
//                })
//                .build()
//                .requestNowAsync();
        if (HYClient.getSdkOptions().User().isRegistered()) {
            HYClient.getModule(ApiAuth.class).logout(null);
        }

    }

    public void login(final Context context,
                      final boolean notice,
                      final String account,
                      final String password,
                      final ModelCallback<AuthUser> callback) {
        URL = AppDatas.Constants().getAddressBaseURL() + "vss/httpjson/user_login";
        Https.post(URL)
                .addParam("strLoginName", account)
                .addParam("strPassword", AppUtils.getMd5(password))
                .addParam("strMAC", AppUtils.getNewMac() == null ? "" : AppUtils.getNewMac())
                .addParam("nClientType", 2)
                .addParam("strIP", AppDatas.Constants().getAddressIP())
                .setHttpCallback(new HTTPUIThreadCallbackAdapter() {

                    @Override
                    public void onPreStart(HTTPRequest request) {
                        super.onPreStart(request);
                        Log.i("MCApp", "Auth Login PreStart");
                        if (callback != null) {
                            callback.onPreStart(request);
                        }
                    }

                    @Override
                    public void onSuccess(HTTPResponse response) {
                        super.onSuccess(response);
                        String str = response.getContentToString();
                        AuthUser authUser = null;
                        try {
                            authUser = new Gson().fromJson(str, AuthUser.class);
                        } catch (Exception e) {
                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_empty_code, "HTTP ERROR AuthApi"));
                            }
                            return;
                        }
                        if (authUser == null) {
                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_empty_code, "HTTP ERROR AuthApi"));
                            }
                            return;
                        }
                        if (authUser.nResultCode != 0) {

                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(authUser.nResultCode, "HTTP ERROR AuthApi " + authUser.nResultCode));
                            }

                            return;
                        }

                        authUser.password = password;
                        authUser.loginName = account;
                        AppDatas.Auth().setAuthUser(authUser);
                        MapApi.isNewBegin = true;
                        final AuthUser finalAuthUser = authUser;

                        HYClient.getModule(ApiAuth.class)
                                .login(SdkParamsCenter.Auth.Login()
                                        .setAddress(authUser.strSieIP, 9001)
                                        .setUserId(AppDatas.Auth().getUserID() + "")
                                        .setnPriority(authUser.nPriority)
                                        .setUserName(AppDatas.Auth().getUserName()), new SdkCallback<CUserRegisterRsp>() {
                                    @Override
                                    public void onSuccess(CUserRegisterRsp cUserRegisterRsp) {
                                        successDeal(callback, finalAuthUser, cUserRegisterRsp);
                                    }

                                    @Override
                                    public void onError(final ErrorInfo errorInfo) {
                                        if (errorInfo.getCode() == ErrorMsg.re_load_code) {
                                            if (notice) {

                                                LogicDialog dialog = new LogicDialog(context)
                                                        .setMessageText(AppUtils.getString(R.string.reload_msg))
                                                        .setCancelClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                errorDeal(errorInfo, callback);
                                                            }
                                                        })
                                                        .setConfirmClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                HYClient.getModule(ApiAuth.class)
                                                                        .login(SdkParamsCenter.Auth.Login()
                                                                                .setAddress(finalAuthUser.strSieIP, 9001)
                                                                                .setUserId(AppDatas.Auth().getUserID() + "")
                                                                                .setAutoKickout(true)
                                                                                .setnPriority(finalAuthUser.nPriority)
                                                                                .setUserName(AppDatas.Auth().getUserName()), new SdkCallback<CUserRegisterRsp>() {
                                                                            @Override
                                                                            public void onSuccess(CUserRegisterRsp cUserRegisterRsp) {
                                                                                successDeal(callback, finalAuthUser, cUserRegisterRsp);
                                                                            }

                                                                            @Override
                                                                            public void onError(ErrorInfo errorInfo) {
                                                                                errorDeal(errorInfo, callback);
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                dialog.setCancelable(false);
                                                dialog.setCanceledOnTouchOutside(false);
                                                dialog.show();
                                            } else {
                                                errorDeal(errorInfo, callback);
                                            }
                                        } else {
                                            errorDeal(errorInfo, callback);
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onFailure(HTTPResponse response) {
                        super.onFailure(response);
                        if (callback != null) {
                            callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_err_code, "HTTP ERROR AuthApi "));
                        }
                    }
                })
                .build()
                .requestNowAsync();
    }


    /**
     * 登陆业务服务器
     * @param account
     * @param password
     * @param callback
     */
    public void loginBizServerSilent(
                      final String account,
                      final String password,
                      final ModelCallback<AuthUser> callback) {
        URL = AppDatas.Constants().getAddressBaseURL() + "vss/httpjson/user_login";
        Https.post(URL)
                .addParam("strLoginName", account)
                .addParam("strPassword", AppUtils.getMd5(password))
                .addParam("strMAC", AppUtils.getNewMac() == null ? "" : AppUtils.getNewMac())
                .addParam("nClientType", 2)
                .addParam("strIP", AppDatas.Constants().getAddressIP())
                .setHttpCallback(new HTTPUIThreadCallbackAdapter() {

                    @Override
                    public void onPreStart(HTTPRequest request) {
                        super.onPreStart(request);
                        Log.i("MCApp", "Auth Login PreStart");
                        if (callback != null) {
                            callback.onPreStart(request);
                        }
                    }

                    @Override
                    public void onSuccess(HTTPResponse response) {
                        super.onSuccess(response);
                        String str = response.getContentToString();
                        AuthUser authUser = null;
                        try {
                            authUser = new Gson().fromJson(str, AuthUser.class);
                        } catch (Exception e) {
                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_empty_code, "HTTP ERROR AuthApi"));
                            }
                            return;
                        }
                        if (authUser == null) {
                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_empty_code, "HTTP ERROR AuthApi"));
                            }
                            return;
                        }
                        if (authUser.nResultCode != 0) {

                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(authUser.nResultCode, "HTTP ERROR AuthApi " + authUser.nResultCode));
                            }

                            return;
                        }

                        authUser.password = password;
                        authUser.loginName = account;
                        AppDatas.Auth().setAuthUser(authUser);
                        final AuthUser finalAuthUser = authUser;
                        if (callback != null) {
                            callback.onSuccess(finalAuthUser);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse response) {
                        super.onFailure(response);
                        if (callback != null) {
                            callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_err_code, "HTTP ERROR AuthApi "));
                        }
                    }
                })
                .build()
                .requestNowAsync();
    }

    public interface CommonLoginCallBack {
        void onLoginSuccess();
        void onLoginFail();
    }

    /**
     * 客户端掉线后,重新静默登录,登录业务平台和媒体平台
     * @param callBack
     */
    public void retryLoginSilent(final CommonLoginCallBack callBack){
        //1 请求业务平台,看是否被踢出
        //2 请求媒体平台,看是否被踢出
        final OptionsUser user = HYClient.getSdkOptions().User();
        Logger.debug("retryLoginSilent start");
        AuthApi.get().getMyUserInfoFromBizServer(user.getDomainCode(), user.getUserId(), new ModelCallback<PersonBean>() {
            @Override
            public void onSuccess(PersonBean personBean) {
                Logger.debug("retryLoginSilent getMyUserInfoFromBizServer success");
                if (personBean != null  && personBean.userList != null && personBean.userList.size() > 0){
                    if (personBean.userList.get(0).nStatus < PersonModelBean.STATUS_ONLINE_IDLE){
                        //业务服务器不在线
                        onUserOffline();
                    }else {
                        //业务服务器用户在线,接下来登录媒体服务器
                        AuthApi.get().loginMediaServer(new CommonLoginCallBack() {
                            @Override
                            public void onLoginSuccess() {
                                Logger.debug("retryLoginSilent loginMediaServer onLoginSuccess");
                                callBack.onLoginSuccess();
                            }

                            @Override
                            public void onLoginFail() {
                                Logger.debug("retryLoginSilent loginMediaServer onLoginFail");
                                callBack.onLoginFail();
                            }
                        });
                    }

                }else {
                    //业务服务器不在线
                    onUserOffline();
                }

            }

            @Override
            public void onUserOffline() {
                Logger.debug("retryLoginSilent getMyUserInfoFromBizServer onUserOffline");
                //被踢出了,重新登陆业务平台
                String account = AppDatas.Auth().getUserLoginName();
                String password = AppDatas.Auth().getPassword();

                AuthApi.get().loginBizServerSilent(account, password, new ModelCallback<AuthUser>() {
                    @Override
                    public void onSuccess(AuthUser authUser) {
                        Logger.debug("retryLoginSilent loginBizServerSilent success");
                        //登录媒体服务器
                        AuthApi.get().loginMediaServer(new CommonLoginCallBack() {
                            @Override
                            public void onLoginSuccess() {
                                callBack.onLoginSuccess();
                            }

                            @Override
                            public void onLoginFail() {
                                callBack.onLoginFail();
                            }
                        });
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        Logger.debug("retryLoginSilent loginBizServerSilent onFailure");
                        super.onFailure(httpResponse);
                        callBack.onLoginFail();
                    }
                });
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                Logger.debug("retryLoginSilent  onFailure");
                callBack.onLoginFail();
            }
        });
    }

    /**
     * 登录媒体服务器
     */
    public void loginMediaServer(final CommonLoginCallBack callback){
        //从媒体服务器请求自己的状态
        CQueryUserListReq.UserInfo usr = new CQueryUserListReq.UserInfo();
        usr.strUserID = HYClient.getSdkOptions().User().getUserId();
        HYClient.getModule(ApiSocial.class).getUsers(SdkParamsCenter.Social.GetUsers()
                .setDomainCode(HYClient.getSdkOptions().User().getDomainCode())
                .addUser(usr), new SdkCallback<ArrayList<CQueryUserListRsp.UserInfo>>() {
            @Override
            public void onSuccess(ArrayList<CQueryUserListRsp.UserInfo> resp) {
                if (resp != null && !resp.isEmpty()) {
                    if(resp.get(0).nState >= PersonModelBean.STATUS_ONLINE_IDLE){
                        //在登录状态
                        callback.onLoginSuccess();
                    }else {
                        //登录媒体服务器
                        HYClient.getModule(ApiAuth.class)
                                .login(SdkParamsCenter.Auth.Login()
                                        .setAddress(AppDatas.Auth().getSieIP(), 9001)
                                        .setUserId(AppDatas.Auth().getUserID() + "")
                                        .setnPriority(AppDatas.Auth().getPriority())
                                        .setUserName(AppDatas.Auth().getUserName()), new SdkCallback<CUserRegisterRsp>() {
                                    @Override
                                    public void onSuccess(CUserRegisterRsp cUserRegisterRsp) {
                                        callback.onLoginSuccess();
                                        Logger.debug("onNetworkStatusChanged stopP2P userinfo nState > 1  stopP2P");
                                    }

                                    @Override
                                    public void onError(final ErrorInfo errorInfo) {
                                        callback.onLoginFail();
                                        Logger.debug("onNetworkStatusChanged connected but service kick me out");
                                    }
                                });
                    }
                } else {
                    //已经掉线
                    //登录媒体服务器
                    HYClient.getModule(ApiAuth.class)
                            .login(SdkParamsCenter.Auth.Login()
                                    .setAddress(AppDatas.Auth().getSieIP(), 9001)
                                    .setUserId(AppDatas.Auth().getUserID() + "")
                                    .setnPriority(AppDatas.Auth().getPriority())
                                    .setUserName(AppDatas.Auth().getUserName()), new SdkCallback<CUserRegisterRsp>() {
                                @Override
                                public void onSuccess(CUserRegisterRsp cUserRegisterRsp) {
                                    callback.onLoginSuccess();
                                    Logger.debug("onNetworkStatusChanged stopP2P userinfo nState > 1  stopP2P");
                                }

                                @Override
                                public void onError(final ErrorInfo errorInfo) {
                                    Logger.debug("onNetworkStatusChanged connected but service kick me out");
                                    callback.onLoginFail();
                                }
                            });

                }
            }

            @Override
            public void onError(ErrorInfo error) {

            }
        });
    }




    /**
     * 从业务服务器获取自己的用户信息
     * @param strDomainCode
     * @param mySelfUserID
     * @param callback
     */
    public void getMyUserInfoFromBizServer(
                      String strDomainCode,
                      String mySelfUserID,
                      final ModelCallback<PersonBean> callback) {
        URL = AppDatas.Constants().getAddressBaseURL() + "vss/httpjson/get_user_info";
        ArrayList<String> ids = new ArrayList<>();
        ids.add(mySelfUserID);
        Https.post(URL)
                .addParam("strDomainCode", strDomainCode)
                .addParam("userList", ids)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addHeader("Connection", "close")
                .setHttpCallback(callback)
                .build()
                .requestNowAsync();
    }



    /**
     * 出错处理
     *
     * @param errorInfo
     * @param callback
     */
    private void errorDeal(SdkCallback.ErrorInfo errorInfo, ModelCallback<AuthUser> callback) {
        if (callback != null) {
            if (errorInfo.getCode() == ErrorMsg.re_load_code) {
                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_err_code, ""));
            } else {
                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_err_code, errorInfo.getMessage()));
            }
        }
    }

    /**
     * 成功处理
     *
     * @param callback
     * @param finalAuthUser
     * @param registerRsp
     */
    private void successDeal(ModelCallback<AuthUser> callback, AuthUser finalAuthUser, CUserRegisterRsp registerRsp) {
        SP.putBoolean(STRING_KEY_needload, true);
        if (callback != null) {
            callback.onSuccess(finalAuthUser);
        }

        String strOSDCommand = "drawtext=fontfile="
                + HYClient.getSdkOptions().Capture().getOSDFontFile()
                + ":fontcolor=white:x=0:y=0:fontsize=26:box=1:boxcolor=black:alpha=0.8:text=' "
                + AppDatas.Auth().getUserName()
                + "'";
        // OSD名称初始化
        HYClient.getSdkOptions().Capture().setOSDCustomCommand(strOSDCommand);
    }

    /**
     * 获取版本信息
     *
     * @param context
     * @param callback
     */
    public void requestVersion(final Context context, final ModelCallback<VersionData> callback) {
        String URL = AppDatas.Constants().getFileAddressURL() + "app/android.version";
        Https.get(URL)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .setHttpCallback(new ModelCallback<VersionData>() {

                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                    }

                    @Override
                    public void onSuccess(final VersionData versionData) {
                        if (callback != null) {
                            callback.onSuccess(versionData);
                        }
                        if (versionData.isNeedToUpdate()) {
                            final LogicDialog logicDialog = new LogicDialog(context);
                            logicDialog.setCancelable(false);
                            logicDialog.setCanceledOnTouchOutside(false);
                            logicDialog.setMessageText(versionData.message);
                            logicDialog.setConfirmClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    logicDialog.dismiss();
                                    if (DownloadApi.isLoad) {
                                        return;
                                    }
                                    int netStatus = AppUtils.getNetWorkStatus(context);
                                    if (netStatus == -1) {
                                        // 无网络
                                    } else if (netStatus == 0) {
                                        // wifi
                                        AppUtils.showToast(AppUtils.getString(R.string.download_apk_start));
                                        Intent intent = new Intent(context, DownloadService.class);
                                        intent.putExtra("downloadURL", AppDatas.Constants().getFileAddressURL() + versionData.path);
                                        context.startService(intent);
                                    } else if (netStatus == 1) {
                                        // 4G/3G
                                        Intent intent = new Intent(context, ErrorDialogActivity.class);
                                        intent.putExtra("downloadURL", AppDatas.Constants().getFileAddressURL() + versionData.path);
                                        context.startActivity(intent);
                                    }
//                                    logicDialog.setConfirmClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            logicDialog.dismiss();
//                                            ModelApis.Download().download(context, AppDatas.Constants().getFileAddressURL() + versionData.path);
//                                        }
//                                    }).setCancelClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            logicDialog.dismiss();
//                                        }
//                                    });
//                                    logicDialog.show();
                                }
                            }).setCancelClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    logicDialog.dismiss();
                                }
                            }).show();
                        }
                    }
                })
                .build()
                .requestNowAsync();
    }

    /**
     * 崩溃时处理
     */
    public void uploadLogOnCrash(boolean isShow) {
        AppUtils.zip(tagZip, tag);
        upload(tag, isShow);
    }

    /**
     * 每次启动检测上传
     */
    public void uploadLog(boolean isShow) {
        if (tag.exists()) {
            upload(tag, isShow);
        }
    }

    /**
     * 上传
     *
     * @param tag
     */
    private void upload(final File tag, final boolean isShow) {
        String URL = AppDatas.Constants().getFileAddressURL() + "upload_file_lua";
        if (tag.length() > 1028 * 1028 * 50) {
            EventBus.getDefault().post(new UploadFile(2));
            return;
        }

        Https.post(URL, PostContentType.MultipartFormdata)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addParam("file1", tag)
                .setHttpCallback(new ModelCallback<Upload>() {
                    @Override
                    public void onSuccess(final Upload versionData) {
                        tag.deleteOnExit();
                        HYClient.getModule(ApiAuth.class)
                                .uploadLogInfo(SdkParamsCenter.Auth.UploadLogInfo()
                                        .setStrLogPath(versionData.file1_name)
                                        .setStrLogTime(new Date().toString()), new SdkCallback<CUploadLogInfoRsp>() {
                                    @Override
                                    public void onSuccess(CUploadLogInfoRsp cUploadLogInfoRsp) {
                                        if (isShow) {
                                            EventBus.getDefault().post(new UploadFile(0));
                                        }
                                    }

                                    @Override
                                    public void onError(ErrorInfo errorInfo) {
                                        if (isShow) {
                                            EventBus.getDefault().post(new UploadFile(1));
                                        }
                                    }
                                });

                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {
                        super.onFinish(httpResponse);
                        if (isShow) {
                            EventBus.getDefault().post(new UploadFile(1));
                        }
                    }
                })
                .build()
                .requestNowAsync();
    }


}
