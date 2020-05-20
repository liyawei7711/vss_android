package huaiye.com.vvs.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.huaiye.cmf.sdp.SdpMessageCmCtrlRsp;
import com.huaiye.cmf.sdp.SdpMessageCmInitRsp;
import com.huaiye.cmf.sdp.SdpMessageCmRegisterUserRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi.common.SDKUtils;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import java.util.ArrayList;

import huaiye.com.vvs.BuildConfig;
import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ErrorMsg;
import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.common.dialog.LogicDialog;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.auth.AppAuth;
import huaiye.com.vvs.models.ModelApis;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.auth.bean.AuthUser;
import huaiye.com.vvs.ui.home.MainActivity;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vvs.common.AppUtils.STRING_KEY_encrypt;
import static huaiye.com.vvs.common.AppUtils.ctx;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: LoginActivity
 */
@BindLayout(R.layout.activity_auth)
public class LoginActivity extends AppBaseActivity {

    @BindView(R.id.edt_account)
    EditText edt_account;
    @BindView(R.id.edt_password)
    EditText edt_password;
    @BindView(R.id.view_load)
    View view_load;

    @BindExtra
    int showKickOutDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setVisibility(View.GONE);

        HYClient.getHYCapture().stopCapture(null);

        edt_account.setText(AppDatas.Auth().getUserLoginName());
        edt_password.setText(AppDatas.Auth().getPassword());
        edt_account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt_password.setText("");
            }
        });


        checkPermission();

    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {
        if (showKickOutDialog == 1) {
            final LogicDialog logicDialog = new LogicDialog(this);
            logicDialog.setCancelable(false);
            logicDialog.setCanceledOnTouchOutside(false);
            logicDialog.setMessageText(AppUtils.getString(R.string.has_connected_false_out));
            logicDialog.setCancelButtonVisibility(View.GONE);
            logicDialog.setConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
        }
    }

    @OnClick({R.id.ll_login, R.id.btn_ip_set, R.id.btn_nick_meet, R.id.btn_wuzhongxin})
    void onBtnClicked(View view) {
        if (view_load.getVisibility() == View.VISIBLE) {
            showToast(AppUtils.getString(R.string.is_loading));
            return;
        }

        switch (view.getId()) {
            case R.id.ll_login:
                // 登录
                login();
                break;
            case R.id.btn_ip_set:
                // 设置服务器
                startActivity(new Intent(getSelf(), SettingAddressActivity.class));
                break;
            case R.id.btn_nick_meet:
                // 匿名入会
                break;
            case R.id.btn_wuzhongxin:
                // 无中心
                if (BuildConfig.DEBUG) {
                    AppAuth.get().setNoCenterUser("lyw " + System.currentTimeMillis());
                } else {
                    if (TextUtils.isEmpty(edt_account.getText())) {
                        showToast(AppUtils.getString(R.string.count_empty));
                        return;
                    }
                    AppAuth.get().setNoCenterUser(edt_account.getText().toString());
                }

                jumpToMain(true);
                break;
        }
    }

    private void jumpToMain(boolean isNoCenter) {

        AppUtils.isMeet = false;
        AppUtils.isTalk = false;
        AppUtils.isVideo = false;
        Intent intent = new Intent(getSelf(), MainActivity.class);
        intent.putExtra("isNoCenter", isNoCenter);
        startActivity(intent);
        finish();
    }

    void login() {
        if (TextUtils.isEmpty(edt_account.getText())
                || TextUtils.isEmpty(edt_password.getText())) {
            showToast(AppUtils.getString(R.string.count_pwd_empty));
            return;
        }

        ModelApis.Auth().login(this, true, edt_account.getText().toString(),
                edt_password.getText().toString(),
                new ModelCallback<AuthUser>() {

                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                        view_load.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSuccess(AuthUser authUser) {
                        jumpToMain(false);
//                        encryptInit();
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        view_load.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(httpResponse.getErrorMessage())) {
                            showToast(ErrorMsg.getMsg(httpResponse.getStatusCode()));
                        }
                    }
                });
    }

    void checkPermission() {
        ArrayList<String> permissions = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }

        if (permissions.size() > 0) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[]{}), 1000);
        }

    }

    long lastMillions = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentMillions = System.currentTimeMillis();
            long delta = currentMillions - lastMillions;
            lastMillions = currentMillions;
            if (delta < 2000) {
                ((MCApp) ctx).gotoClose();
                return super.onKeyDown(keyCode, event);
            }

            showToast(AppUtils.getString(R.string.double_click_exit));
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


//    public static void encryptInit(){
//        encryptInit(AppDatas.Auth().getEncryptPsw(),new SdkCallback<SdpMessageCmRegisterUserRsp>() {
//            @Override
//            public void onSuccess(SdpMessageCmRegisterUserRsp sdpMessageCmRegisterUserRsp) {
//                HYClient.getSdkOptions().encrypt().setEncrypt(true);
//            }
//
//            @Override
//            public void onError(ErrorInfo errorInfo) {
//                Logger.debug("encrypt encryptRegister error" + errorInfo);
//                SP.setParam(STRING_KEY_encrypt, 1);
//                HYClient.getSdkOptions().encrypt().setEncrypt(false);
//            }
//        });
//
//    }


    public static void encryptInit(final String psw, final SdkCallback<SdpMessageCmRegisterUserRsp> resp) {
        if (SP.getInteger(STRING_KEY_encrypt, -1) == 0) {
            HYClient.getModule(ApiEncrypt.class).encryptRegister(SdkParamsCenter.Encrypt.EncryptRegister(), new SdkCallback<SdpMessageCmRegisterUserRsp>() {
                @Override
                public void onSuccess(SdpMessageCmRegisterUserRsp registerResp) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            HYClient.getModule(ApiEncrypt.class).encryptInit(SdkParamsCenter.Encrypt.EncryptInit()
                                    .setUserId(HYClient.getSdkOptions().User().getUserId())
                                    .setTfCardRoot(SDKUtils.getStoragePath(HYClient.getContext(), true))
                                    .setPackageName(HYClient.getContext().getPackageName())
                                    .setPsw(psw)
                                    .setFileName("rt_sech2.bin"), new SdkCallback<SdpMessageCmInitRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmInitRsp initResp) {
                                    AppUtils.nEncryptPasswd = psw;
                                    AppUtils.showToast("初始化成功");
                                    Handler handlerBind = new Handler(Looper.getMainLooper());
                                    handlerBind.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            HYClient.getModule(ApiEncrypt.class).encryptBind(new SdkCallback<SdpMessageCmCtrlRsp>() {
                                                @Override
                                                public void onSuccess(SdpMessageCmCtrlRsp bindResp) {
                                                    AppUtils.showToast("绑定成功");
                                                    resp.onSuccess(null);

                                                }

                                                @Override
                                                public void onError(ErrorInfo error) {
                                                    AppUtils.showToast("绑定失败" + error.getMessage());
                                                    resp.onError(error);
                                                }
                                            });
                                        }
                                    }, 2000);

                                }

                                @Override
                                public void onError(ErrorInfo error) {
                                    AppUtils.showToast("初始化失败" + error.getMessage());
                                    resp.onError(error);

                                }
                            });
                        }
                    }, 2000);

                }

                @Override
                public void onError(ErrorInfo error) {
                    AppUtils.showToast("加密注册失败" + error.getMessage());
                    resp.onError(error);
                }
            });

        }
    }
}
