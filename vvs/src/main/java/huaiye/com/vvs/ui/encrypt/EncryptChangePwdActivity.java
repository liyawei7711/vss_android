package huaiye.com.vvs.ui.encrypt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._params.encrypt.ParamsEncryptChangePsw;
import com.huaiye.sdk.sdpmsgs.encrypt.CCmChangePwdRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.dao.AppDatas;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: SettingAddressActivity
 */
@BindLayout(R.layout.activity_encrypt_change_pwd)
public class EncryptChangePwdActivity extends AppBaseActivity {

    @BindView(R.id.tv_hint1)
    TextView tv_hint1;
    @BindView(R.id.tv_hint2)
    TextView tv_hint2;
    @BindView(R.id.tv_hint3)
    TextView tv_hint3;

    @BindView(R.id.edt_old_pwd)
    EditText edt_old_pwd;
    @BindView(R.id.edt_new_pwd)
    EditText edt_new_pwd;
    @BindView(R.id.edt_re_new_pwd)
    EditText edt_re_new_pwd;

    @BindView(R.id.tv_sure)
    View tv_sure;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(AppUtils.getString(R.string.chang_pwd))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        tv_hint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_old_pwd.requestFocus();
                AppUtils.showKeyboard(edt_old_pwd);
                edt_old_pwd.setSelection(edt_old_pwd.getText().toString().length());
            }
        });
        tv_hint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_new_pwd.requestFocus();
                AppUtils.showKeyboard(edt_new_pwd);
                edt_new_pwd.setSelection(edt_new_pwd.getText().toString().length());
            }
        });
        tv_hint3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_re_new_pwd.requestFocus();
                AppUtils.showKeyboard(edt_re_new_pwd);
                edt_re_new_pwd.setSelection(edt_re_new_pwd.getText().toString().length());
            }
        });

        edt_old_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    tv_hint1.setHint(AppUtils.getString(R.string.old_pwd));
                } else {
                    tv_hint1.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edt_new_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    tv_hint2.setHint(AppUtils.getString(R.string.new_pwd));
                } else {
                    tv_hint2.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edt_re_new_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    tv_hint3.setHint(AppUtils.getString(R.string.new_pwd_re));
                } else {
                    tv_hint3.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePwd();
            }
        });
    }

    void updatePwd() {

        if (TextUtils.isEmpty(edt_old_pwd.getText())) {
            showToast(AppUtils.getString(R.string.old_pwd_empty));
            return;
        }
        if (TextUtils.isEmpty(edt_new_pwd.getText())) {
            showToast(AppUtils.getString(R.string.new_pwd_empty));
            return;
        }
        if (TextUtils.isEmpty(edt_re_new_pwd.getText())) {
            showToast(AppUtils.getString(R.string.new_pwd_re_empty));
            return;
        }
        if (!edt_re_new_pwd.getText().toString().equals(edt_new_pwd.getText().toString())) {
            showToast(AppUtils.getString(R.string.pwd_diff));
            return;
        }
        tv_sure.setEnabled(false);

        final ParamsEncryptChangePsw paramsEncryptChangePsw = new ParamsEncryptChangePsw();
        paramsEncryptChangePsw.m_strNewPwd = edt_new_pwd.getText().toString().trim();
        paramsEncryptChangePsw.m_strOldPwd = edt_old_pwd.getText().toString().trim();
        HYClient.getModule(ApiEncrypt.class).changeEncryptPsw(paramsEncryptChangePsw, new SdkCallback<CCmChangePwdRsp>() {
            @Override
            public void onSuccess(CCmChangePwdRsp cCmChangePwdRsp) {
                showToast(AppUtils.getString(R.string.encrypt_change_psw_success));
                AppDatas.Auth().setEncryptPsw(paramsEncryptChangePsw.m_strNewPwd);
                finish();
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                showToast(AppUtils.getString(R.string.encrypt_change_psw_error));
            }
        });

    }
}
