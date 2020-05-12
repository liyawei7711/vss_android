package huaiye.com.vvs.models.download;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.io.File;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;

/**
 * Created by liyawei on 15-12-24.
 * phone 18952280597
 * QQ    751804582
 */
@BindLayout(R.layout.activity_installdialog)
public class InstallDialogActivity extends AppBaseActivity {

    public static String APK_PATH = "apk_path";
    public static String INSTALL_INFO = "install_info";
    private final int INSTALL_PACKAGES_REQUESTCODE = 11;
    private final int GET_UNKNOWN_APP_SOURCES = 12;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.tv_confirm)
    TextView tv_confirm;
    @BindView(R.id.tv_errinfo)
    TextView tv_errinfo;

    private String apkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setFinishOnTouchOutside(false);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) (AppUtils.getScreenWidth() * 0.8f);
        getWindow().setAttributes(params);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        initListener();

    }

    protected void initListener() {

        apkPath = getIntent().getStringExtra(APK_PATH);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apkPath == null) {
                    onBackPressed();
                    return;
                }

                doInstall();
//                Intent intent = new Intent();
//                File file = new File(apkPath);
//                intent.setAction(android.content.Intent.ACTION_VIEW);
//                //Android 7.0 系统共享文件需要通过 FileProvider 添加临时权限，否则系统会抛出 FileUriExposedException .
//                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
//                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
//                }else {
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//                }
//                startActivity(intent);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == INSTALL_PACKAGES_REQUESTCODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                InstallUtil.installNormal(InstallDialogActivity.this, new File(apkPath), true);
            }else {
                getLogicDialog()
                        .setTitleText("提示")
                        .setMessageText("请打开"+AppUtils.getString(R.string.app_name)+"安全未知来源权限")
                        .setConfirmClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                                startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
                            }
                        })
                        .show();


            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_UNKNOWN_APP_SOURCES){
            doInstall();
        }
    }


    private void doInstall(){
        if (Build.VERSION.SDK_INT >= 26){
            boolean canRequestPackageInstall = getPackageManager().canRequestPackageInstalls();
            if (!canRequestPackageInstall){
                ActivityCompat.requestPermissions(InstallDialogActivity.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUESTCODE);
                return;
            }
        }

        InstallUtil.installNormal(InstallDialogActivity.this, new File(apkPath), true);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
