package huaiye.com.vvs.ui.home;

import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.SP;

import static android.view.KeyEvent.KEYCODE_STEM_2;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_camera_key;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_ptt_channel_left_key;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_ptt_channel_right_key;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_ptt_key;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_sos_key;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_video_key;

/**
 * author: admin
 * date: 2018/06/15
 * version: 0
 * mail: secret
 * desc: AudioSettingActivity
 */

@BindLayout(R.layout.activity_anjian_setting)
public class KeyCodeSettingActivity extends AppBaseActivity {

    @BindView(R.id.tv_ptt)
    TextView tv_ptt;
    @BindView(R.id.tv_sos)
    TextView tv_sos;
    @BindView(R.id.tv_camera)
    TextView tv_camera;
    @BindView(R.id.tv_video)
    TextView tv_video;

    @BindView(R.id.tv_ptt_channel_left)
    TextView tv_ptt_channel_left;
    @BindView(R.id.tv_ptt_channel_right)
    TextView tv_ptt_channel_right;

    boolean pttStart;
    boolean sosStart;
    boolean cameraStart;
    boolean videoStart;
    boolean pttLeftStart;
    boolean pttRightStart;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.anjian_setting))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        System.out.println("Build.MODEL " + Build.MODEL);

        tv_ptt.setText(SP.getInteger(STRING_KEY_ptt_key, KEYCODE_STEM_2) + "");
        tv_sos.setText(SP.getInteger(STRING_KEY_sos_key, -1) == -1 ? "" : SP.getInteger(STRING_KEY_sos_key, -1) + "");
        tv_camera.setText(SP.getInteger(STRING_KEY_camera_key, -1) == -1 ? "" : SP.getInteger(STRING_KEY_camera_key, -1) + "");
        tv_video.setText(SP.getInteger(STRING_KEY_video_key, -1) == -1 ? "" : SP.getInteger(STRING_KEY_video_key, -1) + "");
        tv_ptt_channel_left.setText(SP.getInteger(STRING_KEY_ptt_channel_left_key, -1) == -1 ? "" : SP.getInteger(STRING_KEY_ptt_channel_left_key, -1) + "");
        tv_ptt_channel_right.setText(SP.getInteger(STRING_KEY_ptt_channel_right_key, -1) == -1 ? "" : SP.getInteger(STRING_KEY_ptt_channel_right_key, -1) + "");
    }

    @OnClick({
            R.id.tv_clear,
            R.id.tv_save,
            R.id.tv_ptt,
            R.id.tv_sos,
            R.id.tv_ptt_channel_right,
            R.id.tv_ptt_channel_left,
            R.id.tv_camera,
            R.id.tv_video})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_clear:
                tv_ptt.setText("");
                tv_sos.setText("");
                tv_camera.setText("");
                tv_video.setText("");
                tv_ptt_channel_left.setText("");
                tv_ptt_channel_right.setText("");
                break;
            case R.id.tv_save:
                SP.putInt(STRING_KEY_ptt_key, TextUtils.isEmpty(tv_ptt.getText().toString()) ? KEYCODE_STEM_2 : Integer.parseInt(tv_ptt.getText().toString()));
                SP.putInt(STRING_KEY_sos_key, TextUtils.isEmpty(tv_sos.getText().toString()) ? -1 : Integer.parseInt(tv_sos.getText().toString()));
                SP.putInt(STRING_KEY_camera_key, TextUtils.isEmpty(tv_camera.getText().toString()) ? -1 : Integer.parseInt(tv_camera.getText().toString()));
                SP.putInt(STRING_KEY_video_key, TextUtils.isEmpty(tv_video.getText().toString()) ? -1 : Integer.parseInt(tv_video.getText().toString()));
                SP.putInt(STRING_KEY_ptt_channel_left_key, TextUtils.isEmpty(tv_ptt_channel_left.getText().toString()) ? -1 : Integer.parseInt(tv_ptt_channel_left.getText().toString()));
                SP.putInt(STRING_KEY_ptt_channel_right_key, TextUtils.isEmpty(tv_ptt_channel_right.getText().toString()) ? -1 : Integer.parseInt(tv_ptt_channel_right.getText().toString()));

                AppUtils.ptt_key = SP.getInteger(STRING_KEY_ptt_key, KEYCODE_STEM_2);
                AppUtils.sos_key = SP.getInteger(STRING_KEY_sos_key, -1);
                AppUtils.camera_key = SP.getInteger(STRING_KEY_camera_key, -1);
                AppUtils.video_key = SP.getInteger(STRING_KEY_video_key, -1);
                AppUtils.ptt_channel_left = SP.getInteger(STRING_KEY_ptt_channel_left_key, -1);
                AppUtils.ptt_channel_right= SP.getInteger(STRING_KEY_ptt_channel_right_key, -1);

                onBackPressed();
                break;
            case R.id.tv_ptt:
                pttStart = true;
                sosStart = false;
                cameraStart = false;
                videoStart = false;
                pttLeftStart = false;
                pttRightStart = false;

                tv_ptt.setSelected(true);
                tv_sos.setSelected(false);
                tv_camera.setSelected(false);
                tv_video.setSelected(false);
                tv_ptt_channel_left.setSelected(false);
                tv_ptt_channel_right.setSelected(false);
                break;
            case R.id.tv_sos:
                pttStart = false;
                sosStart = true;
                cameraStart = false;
                videoStart = false;
                pttLeftStart = false;
                pttRightStart = false;

                tv_ptt.setSelected(false);
                tv_sos.setSelected(true);
                tv_camera.setSelected(false);
                tv_video.setSelected(false);
                tv_ptt_channel_left.setSelected(false);
                tv_ptt_channel_right.setSelected(false);
                break;
            case R.id.tv_camera:
                pttStart = false;
                sosStart = false;
                cameraStart = true;
                videoStart = false;
                pttLeftStart = false;
                pttRightStart = false;

                tv_ptt.setSelected(false);
                tv_sos.setSelected(false);
                tv_camera.setSelected(true);
                tv_video.setSelected(false);
                tv_ptt_channel_left.setSelected(false);
                tv_ptt_channel_right.setSelected(false);
                break;
            case R.id.tv_video:
                pttStart = false;
                sosStart = false;
                cameraStart = false;
                videoStart = true;
                pttLeftStart = false;
                pttRightStart = false;

                tv_ptt.setSelected(false);
                tv_sos.setSelected(false);
                tv_camera.setSelected(false);
                tv_video.setSelected(true);
                tv_ptt_channel_left.setSelected(false);
                tv_ptt_channel_right.setSelected(false);
                break;
            case R.id.tv_ptt_channel_left:
                pttStart = false;
                sosStart = false;
                cameraStart = false;
                videoStart = false;
                pttLeftStart = true;
                pttRightStart = false;


                tv_ptt.setSelected(false);
                tv_sos.setSelected(false);
                tv_camera.setSelected(false);
                tv_video.setSelected(false);
                tv_ptt_channel_left.setSelected(true);
                tv_ptt_channel_right.setSelected(false);
                break;
            case R.id.tv_ptt_channel_right:
                pttStart = false;
                sosStart = false;
                cameraStart = false;
                videoStart = false;
                pttLeftStart = false;
                pttRightStart = true;



                tv_ptt.setSelected(false);
                tv_sos.setSelected(false);
                tv_camera.setSelected(false);
                tv_video.setSelected(false);
                tv_ptt_channel_left.setSelected(false);
                tv_ptt_channel_right.setSelected(true);
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (pttStart) {
            tv_ptt.setText(keyCode + "");
        } else if (sosStart) {
            tv_sos.setText(keyCode + "");
        } else if (cameraStart) {
            tv_camera.setText(keyCode + "");
        } else if (videoStart) {
            tv_video.setText(keyCode + "");
        } else if (pttLeftStart) {
            tv_ptt_channel_left.setText(keyCode + "");
        } else if (pttRightStart) {
            tv_ptt_channel_right.setText(keyCode + "");
        }
        return super.onKeyDown(keyCode, event);
    }
}
