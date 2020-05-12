package huaiye.com.vvs.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huaiye.sdk.logger.Logger;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ErrorMsg;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.auth.AuthApi;
import huaiye.com.vvs.models.auth.bean.ChangePwd;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vvs.common.AppUtils.ctx;
import static huaiye.com.vvs.dao.auth.AppAuth.PWD;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: SettingAddressActivity
 */
@BindLayout(R.layout.activity_change_pwd)
public class ChangePwdActivity extends AppBaseActivity {

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
        AuthApi.get().changpwd(edt_old_pwd.getText().toString(),
                edt_new_pwd.getText().toString(),
                new ModelCallback<ChangePwd>() {
                    @Override
                    public void onSuccess(ChangePwd authUser) {
                        if (authUser.nResultCode == 0) {
                            AppDatas.Auth().put(PWD, "");
                            showToast(AppUtils.getString(R.string.pwd_has_changed));

                            Logger.log("修改密码退出");

                            ((MCApp) ctx).gotoLogin();

                        } else {
                            if(authUser.nResultCode == 1010100003) {
                                showToast(AppUtils.getString(R.string.no_this_user));
                            } else if(authUser.nResultCode == 1010100005) {
                                showToast(AppUtils.getString(R.string.old_pwd_error));
                            } else if(authUser.nResultCode == 1010100016) {
                                showToast(AppUtils.getString(R.string.change_error));
                            } else {
                                showToast(AppUtils.getString(R.string.change_error));
                            }

                            tv_sure.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        tv_sure.setEnabled(true);
                        showToast(AppUtils.getString(R.string.req_false));
                    }
                });

    }
}
