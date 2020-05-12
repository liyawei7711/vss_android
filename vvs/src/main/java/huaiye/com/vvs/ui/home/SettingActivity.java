package huaiye.com.vvs.ui.home;

import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import java.io.File;

import huaiye.com.vvs.BuildConfig;
import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.ChatMessages;
import huaiye.com.vvs.dao.msgs.VssMessageListMessages;
import huaiye.com.vvs.models.ModelApis;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.auth.bean.VersionData;
import huaiye.com.vvs.ui.auth.ChangePwdActivity;
import huaiye.com.vvs.ui.encrypt.EncryptSettingActivity;

import static huaiye.com.vvs.common.AppUtils.CAPTURE_TYPE;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_false;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_language_show_type;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_main_show_type;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_true;
import static huaiye.com.vvs.common.AppUtils.ctx;

/**
 * author: admin
 * date: 2018/05/11
 * version: 0
 * mail: secret
 * desc: SettingActivity
 */
@BindLayout(R.layout.activity_settings)
public class SettingActivity extends AppBaseActivity {
    @BindView(R.id.cb_type)
    CheckBox cb_type;
    @BindView(R.id.cb_captur_type)
    CheckBox cb_captur_type;
//    @BindView(R.id.cb_map_type)
//    CheckBox cb_map_type;
    @BindView(R.id.cb_language_type)
    CheckBox cb_language_type;
    @BindView(R.id.new_version)
    TextView new_version;



    @Override
    protected void initActionBar() {
        getNavigate()
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                .setTitlText(AppUtils.getString(R.string.setting));

        new_version.setText("v " + BuildConfig.VERSION_NAME);

        cb_type.setChecked(Boolean.parseBoolean(SP.getParam(STRING_KEY_main_show_type, STRING_KEY_true).toString()));
        cb_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SP.setParam(STRING_KEY_main_show_type, STRING_KEY_true);
                } else {
                    SP.setParam(STRING_KEY_main_show_type, STRING_KEY_false);
                }
            }
        });

        cb_language_type.setChecked(Boolean.parseBoolean(SP.getParam(STRING_KEY_language_show_type, STRING_KEY_false).toString()));
        cb_language_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SP.setParam(STRING_KEY_language_show_type, STRING_KEY_true);
                } else {
                    SP.setParam(STRING_KEY_language_show_type, STRING_KEY_false);
                }
                showToast(AppUtils.getString(R.string.chang_language));
                AppUtils.shiftLanguage();
            }
        });

        cb_captur_type.setChecked(Boolean.parseBoolean(SP.getParam(CAPTURE_TYPE, STRING_KEY_false).toString()));
        cb_captur_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SP.setParam(CAPTURE_TYPE, STRING_KEY_true);
                } else {
                    SP.setParam(CAPTURE_TYPE, STRING_KEY_false);
                }
            }
        });

//        cb_map_type.setChecked(Boolean.parseBoolean(SP.getParam(STRING_KEY_map_type, STRING_KEY_false).toString()));
//        cb_map_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    SP.setParam(STRING_KEY_map_type, STRING_KEY_true);
//                } else {
//                    SP.setParam(STRING_KEY_map_type, STRING_KEY_false);
//                }
//            }
//        });




    }




    @Override
    public void doInitDelay() {
//        Images.get()
//                .useCache(ImageCacheType.NoneCache)
//                .source(AppDatas.Constants().getAddressBaseURL() + "busidataexchange/getQRCode.action?name=ANDROID_APP")
//                .into(iv_img);

    }

    @OnClick({R.id.tv_logout,
            R.id.view_clear,
            R.id.rl_peizhi,
            R.id.rl_anjian,
            R.id.rl_map_setting,
            R.id.check_update,
            R.id.view_more,
            R.id.rl_encrypt_setting,
            R.id.view_change_pwd})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_logout:
                ((MCApp) ctx).gotoLogin();
                break;
            case R.id.rl_peizhi:
                startActivity(new Intent(this, AudioSettingActivity.class));
                break;
            case R.id.view_clear:
                mLogicDialog.setMessageText(AppUtils.getString(R.string.delete_all_msg));
                mLogicDialog.setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppDatas.Messages().clear();
                        VssMessageListMessages.get().clear();
                        ChatMessages.get().clear();
                        //删除缓存文件夹
                        File fC = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/files/chat/");
                        if (fC.exists())
                        {
                            deleteDir(fC.getPath());
                        }

//                        MainActivity home = (MainActivity) getActivity();
//                        home.resetMessageNumbers();
                        showToast(AppUtils.getString(R.string.has_delete_all_msg));
                    }
                }).setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLogicDialog.dismiss();
                    }
                }).show();
                break;
            case R.id.check_update:
                requestVersion();
                break;
            case R.id.view_more:
                startActivity(new Intent(getSelf(),SettingMoreActivity.class));
                break;
            case R.id.view_change_pwd:
                startActivity(new Intent(this, ChangePwdActivity.class));
                break;
            case R.id.rl_anjian:
                startActivity(new Intent(this, KeyCodeSettingActivity.class));
                break;
            case R.id.rl_map_setting:
                startActivity(new Intent(this, MapSettingActivity.class));
                break;
            case R.id.rl_encrypt_setting:
                startActivity(new Intent(this, EncryptSettingActivity.class));
                break;
        }
    }



    void requestVersion() {
        ModelApis.Auth().requestVersion(this, new ModelCallback<VersionData>() {
            @Override
            public void onSuccess(VersionData versionData) {
                if (!versionData.isNeedToUpdate()) {
                    showToast(AppUtils.getString(R.string.all_is_new_version));
                } else {
//                    new_version.setText(AppUtils.getString(R.string.version_new_title) + versionData.versionName + "," + AppUtils.getString(R.string.update_at_once));
                }
            }
        });
    }

    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

}
