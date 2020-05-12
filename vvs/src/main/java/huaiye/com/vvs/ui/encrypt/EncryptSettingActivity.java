package huaiye.com.vvs.ui.encrypt;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMessageCmCtrlRsp;
import com.huaiye.cmf.sdp.SdpMessageCmExitRsp;
import com.huaiye.cmf.sdp.SdpMessageCmInitRsp;
import com.huaiye.cmf.sdp.SdpMessageCmRegisterUserRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.encrypt.ParamsEncryptSecretKeyDestroy;
import com.huaiye.sdk.sdkabi._params.encrypt.ParamsEncryptSecretKeyUpdate;
import com.huaiye.sdk.sdkabi.common.SDKUtils;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.ui.auth.LoginActivity;

import static huaiye.com.vvs.common.AppUtils.STRING_KEY_encrypt;

@BindLayout(R.layout.activity_encrypt_setting)
public class EncryptSettingActivity extends AppBaseActivity {

    @BindView(R.id.cb_encrypt)
    CheckBox cb_encrypt;

    @BindView(R.id.tv_hint1)
    TextView tv_hint1;

    @BindView(R.id.edt_pwd)
    EditText edt_pwd;

    @Override
    protected void initActionBar() {
        getNavigate()
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                .setTitlText(AppUtils.getString(R.string.encrypt_setting));



        String strPsw =  AppDatas.Auth().getEncryptPsw();
        edt_pwd.setText(strPsw);
    }

    @Override
    public void doInitDelay() {
        initEncryptSetting();
    }


    @OnClick({R.id.rl_change_psw,R.id.rl_destory_local_secret_key,
                R.id.btn_register,R.id.btn_init,R.id.btn_unbind,R.id.btn_bind})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rl_change_psw:
                changePsw();
                break;
            case R.id.rl_destory_local_secret_key:
                destroyLocalSecretKey();
                break;
            case R.id.btn_register:
                HYClient.getModule(ApiEncrypt.class).encryptRegister(SdkParamsCenter.Encrypt.EncryptRegister(), new SdkCallback<SdpMessageCmRegisterUserRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmRegisterUserRsp registerResp) {
                        showToast("加密注册成功 ");
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        showToast("加密注册失败 " + error.getMessage());
                    }
                });
                break;
            case R.id.btn_init:
                final String strPsw = edt_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(strPsw)){
                    showToast(AppUtils.getString(R.string.encrypt_card_psw_hint));
                    cb_encrypt.setChecked(false);
                    return;
                }

                HYClient.getModule(ApiEncrypt.class).encryptInit(SdkParamsCenter.Encrypt.EncryptInit()
                        .setUserId(HYClient.getSdkOptions().User().getUserId())
                        .setTfCardRoot(SDKUtils.getStoragePath(HYClient.getContext(), true))
                        .setPackageName(HYClient.getContext().getPackageName())
                        .setPsw(strPsw)
                        .setFileName("rt_sech2.bin"), new SdkCallback<SdpMessageCmInitRsp>(){

                    @Override
                    public void onSuccess(SdpMessageCmInitRsp sdpMessageCmInitRsp) {
                        AppUtils.nEncryptPasswd = strPsw;
                        showToast("加密初始化成功 ");

                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast("加密初始化失败 " + errorInfo.getMessage());
                    }
                });
                break;
            case R.id.btn_bind:
                HYClient.getModule(ApiEncrypt.class).encryptBind(new SdkCallback<SdpMessageCmCtrlRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmCtrlRsp resp) {
                        showToast("绑定成功");
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        showToast("绑定失败");
                    }
                });
                break;
            case R.id.btn_unbind:
                HYClient.getModule(ApiEncrypt.class).encryptUnbind(new SdkCallback<SdpMessageCmCtrlRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmCtrlRsp resp) {
                        showToast("解绑定成功");

                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        showToast("解绑定失败");

                    }
                });
                break;
        }
    }


    private void changePsw(){
        startActivity(new Intent(this,EncryptChangePwdActivity.class));
    }


    private void updateSecretKey(){
        Logger.debug("updateSecretKey start");
        HYClient.getModule(ApiEncrypt.class).secretKeyUpdate(new ParamsEncryptSecretKeyUpdate(),new SdkCallback<SdpMessageCmCtrlRsp>(){

            @Override
            public void onSuccess(SdpMessageCmCtrlRsp sdpMessageCmCtrlRsp) {
                Logger.debug("updateSecretKey onSuccess " + sdpMessageCmCtrlRsp.m_nResultCode);
                showToast(AppUtils.getString(R.string.encrypt_update_secret_key_success));


            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Logger.debug("updateSecretKey errorInfo " + errorInfo.getMessage());
                showToast(AppUtils.getString(R.string.encrypt_update_secret_key_error));

            }
        });
    }


    private void destroyLocalSecretKey(){
        Logger.debug("destroyLocalSecretKey start");
        HYClient.getModule(ApiEncrypt.class).localSecretKeyDestroy(new ParamsEncryptSecretKeyDestroy(),new SdkCallback<SdpMessageCmCtrlRsp>(){

            @Override
            public void onSuccess(SdpMessageCmCtrlRsp sdpMessageCmCtrlRsp) {
                Logger.debug("destroyLocalSecretKey onSuccess " + sdpMessageCmCtrlRsp.m_nResultCode);

            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Logger.debug("destroyLocalSecretKey errorInfo " + errorInfo.getMessage());

            }
        });
    }


    private void initEncryptSetting(){
        if (SP.getInteger(STRING_KEY_encrypt, -1) == 0){
            cb_encrypt.setChecked(true);
        }else {
            cb_encrypt.setChecked(false);
        }

        cb_encrypt.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            final String strPsw = edt_pwd.getText().toString().trim();
                            if (TextUtils.isEmpty(strPsw)){
                                showToast(AppUtils.getString(R.string.encrypt_card_psw_hint));
                                cb_encrypt.setChecked(false);
                                return;
                            }

                            mZeusLoadView.loadingText(AppUtils.getString(R.string.use_encrypt_init)).setLoading();
                            mZeusLoadView.setCancelable(true);
                            oneKey(strPsw);
//                            LoginActivity.encryptInit(strPsw,new SdkCallback<SdpMessageCmRegisterUserRsp>() {
//                                @Override
//                                public void onSuccess(SdpMessageCmRegisterUserRsp sdpMessageCmRegisterUserRsp) {
//                                    HYClient.getSdkOptions().encrypt().setEncrypt(true);
//                                    cb_encrypt.setChecked(true);
//                                    //update psw
//                                    AppDatas.Auth().setEncryptPsw(strPsw);
//                                    mZeusLoadView.dismiss();
//                                }
//
//                                @Override
//                                public void onError(ErrorInfo errorInfo) {
//                                    SP.setParam(STRING_KEY_encrypt, 1);
//                                    HYClient.getSdkOptions().encrypt().setEncrypt(false);
//                                    cb_encrypt.setChecked(false);
//                                    mZeusLoadView.dismiss();
//                                    showToast(AppUtils.getString(R.string.encrypt_init_error));
//                                }
//                            });
                        }else {
                            SP.setParam(STRING_KEY_encrypt, 1);
                            if (HYClient.getSdkOptions().encrypt().isEncryptBind()){
                                HYClient.getModule(ApiEncrypt.class).encryptUnbind(new SdkCallback<SdpMessageCmCtrlRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmCtrlRsp sdpMessageCmCtrlRsp) {
                                        HYClient.getModule(ApiEncrypt.class).encryptUnInit(SdkParamsCenter.Encrypt.EncryptUnInit(), new SdkCallback<SdpMessageCmExitRsp>() {
                                            @Override
                                            public void onSuccess(SdpMessageCmExitRsp sdpMessageCmExitRsp) {
                                                showToast(AppUtils.getString(R.string.encrypt_card_uninit));

                                            }

                                            @Override
                                            public void onError(ErrorInfo errorInfo) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(ErrorInfo errorInfo) {

                                    }
                                });
                            }

                        }
                    }
                });
    }



    private void oneKey(final String strPsw){
        HYClient.getModule(ApiEncrypt.class).encryptRegister(SdkParamsCenter.Encrypt.EncryptRegister(), new SdkCallback<SdpMessageCmRegisterUserRsp>() {
            @Override
            public void onSuccess(SdpMessageCmRegisterUserRsp registerResp) {
                HYClient.getModule(ApiEncrypt.class).encryptInit(SdkParamsCenter.Encrypt.EncryptInit()
                        .setUserId(HYClient.getSdkOptions().User().getUserId())
                        .setTfCardRoot(SDKUtils.getStoragePath(HYClient.getContext(), true))
                        .setPackageName(HYClient.getContext().getPackageName())
                        .setPsw(strPsw)
                        .setFileName("rt_sech2.bin"), new SdkCallback<SdpMessageCmInitRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmInitRsp resp) {
                        AppUtils.nEncryptPasswd = strPsw;
                        showToast("初始化成功");
                        HYClient.getModule(ApiEncrypt.class).encryptBind(new SdkCallback<SdpMessageCmCtrlRsp>() {
                            @Override
                            public void onSuccess(SdpMessageCmCtrlRsp resp) {
                                showToast("绑定成功");
                                onFinalSuccess(strPsw);
                                Log.d("Encrypt","bind status bind= " + HYClient.getSdkOptions().encrypt().isEncryptBind());
                                Log.d("Encrypt","bind status init = " + HYClient.getSdkOptions().encrypt().isEncryptInitSuccess());
                            }

                            @Override
                            public void onError(ErrorInfo error) {
                                showToast("绑定失败");
                                onFinalError();
                            }
                        });
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        showToast("初始化失败");
                        //初始化失败很可能是报重复初始化错误,反初始化一下
                        doUnInit();
                        onFinalError();
                    }
                });
            }

            @Override
            public void onError(ErrorInfo error) {
                showToast("加密注册失败 " + error.getMessage());
                onFinalError();
            }
        });
    }


    private void onFinalSuccess(String strPsw){
        cb_encrypt.setChecked(true);
        //update psw
        AppDatas.Auth().setEncryptPsw(strPsw);
        mZeusLoadView.dismiss();
        SP.setParam(STRING_KEY_encrypt, 0);
    }

    private void onFinalError(){
        SP.setParam(STRING_KEY_encrypt, 1);
        cb_encrypt.setChecked(false);
        mZeusLoadView.dismiss();

//        showToast(AppUtils.getString(R.string.encrypt_init_error));
    }


    private void doUnInit(){
        HYClient.getModule(ApiEncrypt.class).encryptUnInit(SdkParamsCenter.Encrypt.EncryptUnInit(), new SdkCallback<SdpMessageCmExitRsp>() {
            @Override
            public void onSuccess(SdpMessageCmExitRsp resp) {
            }

            @Override
            public void onError(ErrorInfo error) {

            }
        });
    }
    /**
     * 不管解绑定成功失败,都需要反初始化
     */
//    private void doUninit(){
//        HYClient.getModule(ApiEncrypt.class).encryptUnInit(SdkParamsCenter.Encrypt.EncryptUnInit(), new SdkCallback<SdpMessageCmExitRsp>() {
//            @Override
//            public void onSuccess(SdpMessageCmExitRsp sdpMessageCmExitRsp) {
//
//            }
//
//            @Override
//            public void onError(ErrorInfo errorInfo) {
//
//            }
//        });
//    }
}
