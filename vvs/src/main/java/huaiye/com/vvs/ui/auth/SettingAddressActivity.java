package huaiye.com.vvs.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.map.baidu.activity.OfflineMapListActivity;
import huaiye.com.vvs.ui.iperf.IperfActivity;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: SettingAddressActivity
 */
@BindLayout(R.layout.activity_settings_address)
public class SettingAddressActivity extends AppBaseActivity {

    @BindView(R.id.edt_address_port)
    EditText edt_address_port;
    @BindView(R.id.edt_address_ip)
    EditText edt_address_ip;
    @BindView(R.id.cb_private)
    RadioButton cb_private;

    @BindView(R.id.edt_address_port_public)
    EditText edt_address_port_public;
    @BindView(R.id.edt_address_ip_public)
    EditText edt_address_ip_public;
    @BindView(R.id.cb_public)
    RadioButton cb_public;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setTitlText(AppUtils.getString(R.string.new_setting))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                .setRightText(AppUtils.getString(R.string.save_change))
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateAddress();
                    }
                });

        edt_address_ip_public.setText(AppDatas.Constants().getStrAddressPublic());
        edt_address_port_public.setText(AppDatas.Constants().getnPortPublic() + "");

        edt_address_ip.setText(AppDatas.Constants().getStrAddressPrivate());
        edt_address_port.setText(AppDatas.Constants().getnPortPrivate() + "");

        findViewById(R.id.tv_lixian_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getSelf(), OfflineMapListActivity.class));
            }
        });
        findViewById(R.id.tv_iperf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getSelf(), IperfActivity.class));
            }
        });


        boolean selectPublic =  AppDatas.Constants().isSelectPublic();
        if (selectPublic){
            cb_public.setChecked(true);
            cb_private.setChecked(false);
        }else {
            cb_public.setChecked(false);
            cb_private.setChecked(true);
        }
        cb_public.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_private.setChecked(false);
                }
            }
        });
        cb_private.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_public.setChecked(false);
                }
            }
        });
    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {

    }

    void updateAddress() {
        String ip;
        int port ;
        if (!cb_public.isChecked()){
            if (TextUtils.isEmpty(edt_address_ip.getText())
                    || TextUtils.isEmpty(edt_address_port.getText())) {
                showToast(AppUtils.getString(R.string.ip_address_empty));
                return;
            }
            if (!AppUtils.isIpAddress(edt_address_ip.getText().toString().replaceAll(" ",""))) {
                showToast(AppUtils.getString(R.string.input_right_txt));
                return;
            }
            if (edt_address_port.getText().toString().length() != 4) {
                showToast(AppUtils.getString(R.string.pore_right_txt));
                return;
            }
            ip = edt_address_ip.getText().toString().replaceAll(" ","");
            port = Integer.parseInt(edt_address_port.getText().toString());
        }else {
            if (TextUtils.isEmpty(edt_address_ip_public.getText())
                    || TextUtils.isEmpty(edt_address_port_public.getText())) {
                showToast(AppUtils.getString(R.string.ip_address_empty));
                return;
            }
            if (!AppUtils.isIpAddress(edt_address_ip_public.getText().toString().replaceAll(" ",""))) {
                showToast(AppUtils.getString(R.string.input_right_txt));
                return;
            }
            if (edt_address_port_public.getText().toString().length() != 4) {
                showToast(AppUtils.getString(R.string.pore_right_txt));
                return;
            }
            ip = edt_address_ip_public.getText().toString().replaceAll(" ","");
            port = Integer.parseInt(edt_address_port_public.getText().toString());
        }

        if (edt_address_ip.getText() != null && edt_address_port.getText() != null){
            AppDatas.Constants().setAddressPrivate(edt_address_ip.getText().toString().replaceAll(" ",""), Integer.parseInt(edt_address_port.getText().toString()));
        }


        if (edt_address_ip_public.getText() != null && edt_address_port_public.getText() != null){
            AppDatas.Constants().setAddressPublic(edt_address_ip_public.getText().toString().replaceAll(" ",""), Integer.parseInt(edt_address_port_public.getText().toString()));
        }

        if (cb_public.isChecked()){
            AppDatas.Constants().setCurrentSelect(true);
        }else {
            AppDatas.Constants().setCurrentSelect(false);
        }


        AppDatas.Constants().setAddress(ip, port);

        finish();
    }
}
