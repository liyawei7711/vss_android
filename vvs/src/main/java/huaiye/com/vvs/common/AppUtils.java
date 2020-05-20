package huaiye.com.vvs.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.huaiye.cmf.JniIntf;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.capture.HYCapture;
import com.huaiye.sdk.sdkabi._options.symbols.SDKAccelerateMethod;
import com.huaiye.sdk.sdkabi._options.symbols.SDKAudioAEC;
import com.huaiye.sdk.sdkabi._options.symbols.SDKAudioAGC;
import com.huaiye.sdk.sdkabi._options.symbols.SDKAudioNS;
import com.huaiye.sdk.sdkabi._options.symbols.SDKCaptureQuality;
import com.huaiye.sdk.sdkabi._options.symbols.SDKTransformMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import huaiye.com.vvs.BuildConfig;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.views.WindowManagerUtils;
import huaiye.com.vvs.ui.home.view.TalkVideoViewLayout;
import huaiye.com.vvs.ui.home.view.TalkViewLayout;
import huaiye.com.vvs.ui.meet.MeetViewLayoutNew;

import static android.content.Context.TELEPHONY_SERVICE;
import static android.view.KeyEvent.KEYCODE_STEM_2;
import static java.lang.String.format;

/**
 * author: admin
 * date: 2017/09/05
 * version: 0
 * mail: secret
 * desc: AppUtils
 */

public final class AppUtils {
//    public static boolean isp2p = false;
//    public static boolean enableP2p = false;//是否可以开启配p2p

    public static boolean isVideo = false;
    public static boolean isTalk = false;
    public static boolean isMeet = false;

    public static boolean isCaptureLayoutShowing = false;
    public static boolean isChannel = false;

    public static int DevType_ANDROID = 1;
    public static int DevType_IOS = 2;
    public static int DevType_PC = 3;
    public static int DevType_WEB = 4;

    /**
     * 此名单不展示1080P选项 华为P8
     */
    public static final ArrayList<String> blackPhone = new ArrayList(Arrays.asList("ALE-UL00", "ALE-TL00"));
    /**
     * 此名单屏蔽三星某机型，此机型用AppAudioManager类库，会产生崩溃问题
     */
    public static final ArrayList<String> audioDevice = new ArrayList(Arrays.asList("HM NOTE 1LTETD"));
    /**
     * 此名单屏蔽三星某机型，此机型用AppAudioManager类库，会产生崩溃问题
     * 屏蔽姆比亚某机型，此机型用AppAudioManager类库，造成退出严重卡顿问题
     * 报activity local service 问题
     */
    public static final ArrayList<String> meetAudioDevice = new ArrayList(Arrays.asList("HM NOTE 1LTETD", "NX506J"));
    /**
     * 此名单屏蔽三星4.4手机，此手机采用硬件加速，低配置手机造成资源不足，无法展示问题
     */
    public static final ArrayList<String> jiaSuDevice = new ArrayList(Arrays.asList("HM NOTE 1LTETD", "SM-N7508V"));

    /**
     * 悬浮框手机
     */
    public static final ArrayList<String> xuanfuDevice = new ArrayList(Arrays.asList("Xiaomi", "Hisense Z1", "T996S", "ALE-UL00"));

    public static final int P2P_COMMON_UDP_MSG = 10005;

    public static String CAPTURE_TYPE = "capture_type";
    public static final int CAPTURE_TYPE_INT = 1000;
    public static String STOP_CAPTURE_TYPE = "capture_stop_type";
    public static final int STOP_CAPTURE_TYPE_INT = 1001;
    public static String PLAYER_TYPE = "player_type";
    public static String PLAYER_TYPE_only_audio = "only_audio";
    public static String PLAYER_TYPE_audio_video = "audio_video";
    public static final int PLAYER_TYPE_INT = 1002;
    public static final int PLAYER_TYPE_PERSON_INT = 1003;
    public static final int PLAYER_TYPE_DEVICE_INT = 1004;
    public static final int ZHILING_CODE_TYPE_INT = 9999;
    public static final int ZHILING_IMG_TYPE_INT = 9998;
    public static final int ZHILING_FILE_TYPE_INT = 9997;
    public static final int BROADCAST_AUDIO_TYPE_INT = 9994;
    public static final int BROADCAST_VIDEO_TYPE_INT = 9995;
    public static final int BROADCAST_AUDIO_FILE_TYPE_INT = 9996;

    public static final int MSG_TYPE_MARK = 2000;//标记推送
    public static final int MSG_TYPE_USER = 2001;//人员信息推送
    public static final int MSG_TYPE_USER_GPS = 2002;//人员信息推送
    public static final int MSG_TYPE_DIANZI_BOADER = 2003;//人员信息推送
    public static final int MSG_TYPE_DIANZI_BOADER_CHANGE = 2004;//人员信息推送

    public static int UPLOAD_GPS_INFO = 3;

    //每次开启APP的时候,是否需要展示登录界面
    public static String STRING_KEY_needload = "needload";

    public static String STRING_KEY_main_show_type = "main_show_type";
    public static String STRING_KEY_language_show_type = "language_show_type";
    public static String STRING_KEY_map_type = "map_type";
    public static String STRING_KEY_true = "true";
    public static String STRING_KEY_false = "false";
    public static final String STRING_KEY_LOCATION_FREQUENCY = "location_frequency";
    public static final String STRING_KEY_LOCATION_FREQUENCY_LOW = "location_frequency";
    public static final String STRING_KEY_LOCATION_FREQUENCY_MIDDLE = "location_frequency_MIDDLE";
    public static final String STRING_KEY_LOCATION_FREQUENCY_HIGH = "location_frequency_HIGH";

    public static String STRING_KEY_isAppFirstStarted = "isAppFirstStarted";

    public static String STRING_KEY_ptt_key = "ptt_key";
    public static String STRING_KEY_sos_key = "sos_key";
    public static String STRING_KEY_camera_key = "camera_key";
    public static String STRING_KEY_video_key = "video_key";
    public static String STRING_KEY_ptt_channel_left_key = "ptt_channel_left";
    public static String STRING_KEY_ptt_channel_right_key = "ptt_channel_right";

    public static String STRING_KEY_agc = "agc";
    public static String STRING_KEY_ns = "ns";
    public static String STRING_KEY_aec = "aec";
    public static String STRING_KEY_qos = "qos";
    public static String STRING_KEY_player = "player";
    public static String STRING_KEY_capture = "capture";
    public static String STRING_KEY_bitrate = "bitrate";
    public static String STRING_KEY_recapture = "recapture";
    public static String STRING_KEY_capturebianma = "capturebianma";
    public static String STRING_KEY_playerjiema = "playerjiema";
    public static String STRING_KEY_mPublishPresetoption = "mPublishPresetoption";
    public static final String STRING_KEY_camera = "camera";
    public static final String STRING_KEY_trans = "trans";
    public static final String STRING_KEY_tcp = "tcp";
    public static final String STRING_KEY_udp = "udp";
    public static final String STRING_KEY_VGA = "VGA";
    public static final String STRING_KEY_HD = "HDVGA";
    public static final String STRING_KEY_HD720P = "HD720P";
    public static final String STRING_KEY_HD1080P = "HD1080P";
    public static final String STRING_KEY_ying = "ying";
    public static final String STRING_KEY_soft = "soft";
    public static final String STRING_KEY_kbps = "kbps";
    public static final String STRING_KEY_encrypt = "encrypt";

    public static int ptt_key;
    public static int sos_key;
    public static int camera_key;
    public static int video_key;
    public static int ptt_channel_left;
    public static int ptt_channel_right;

    public static String nEncryptPasswd = null;

    public static Context ctx;
    static Toast mToast;
    public static boolean isHide;

    public static String rootPath = Environment.getExternalStorageDirectory().toString();
    static String Temp = "yyyy-MM-dd HH:mm:ss";
    static SimpleDateFormat sdf = new SimpleDateFormat(Temp, Locale.CHINA);
    static String Temp2 = "MM-dd HH:mm";
    static SimpleDateFormat sdf2 = new SimpleDateFormat(Temp2, Locale.CHINA);

    private static MeetViewLayoutNew meet_view;
    private static TalkVideoViewLayout tvvl_view;
    private static TalkViewLayout tvl_view;
    public static String audiovideoPath = Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/audiovideo";

    public static MeetViewLayoutNew getMeet_view(Context context) {
        if (meet_view == null) {
            Logger.log("getMeet_view CREATE");
            meet_view = new MeetViewLayoutNew(context.getApplicationContext());
        }
        return meet_view;
    }

    public static boolean isMeetViewNull() {
        return meet_view == null;
    }

    public static boolean isVideoViewNull() {
        return tvvl_view == null;
    }

    public static boolean isTalkViewNull() {
        return tvl_view == null;
    }

    public static TalkVideoViewLayout getTvvl_view(Context context) {
        if (tvvl_view == null) {
            Logger.log("getTvvl_view CREATE");
            tvvl_view = new TalkVideoViewLayout(context.getApplicationContext());
        }
        return tvvl_view;
    }

    public static TalkViewLayout getTvl_view(Context context) {
        if (tvl_view == null) {
            Logger.log("getTvvl_view CREATE");
            tvl_view = new TalkViewLayout(context.getApplicationContext());
        }
        return tvl_view;
    }

    public static void reSetVideoView() {
        WindowManagerUtils.closeAll(true);
        tvvl_view = null;
    }

    public static void reSetTalkView() {
        WindowManagerUtils.closeAll(true);
        tvl_view = null;
    }

    public static void reSetMeetView() {
        WindowManagerUtils.closeAll(true);
        meet_view = null;
    }

    private AppUtils() {

    }

    public static void init(Context context) {
        ctx = context.getApplicationContext();
        mToast = Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);

        if (TextUtils.isEmpty(SP.getString(STRING_KEY_trans))) {
            SP.setParam(STRING_KEY_trans, STRING_KEY_udp);
        }
        if (SP.getString(STRING_KEY_trans).equals(STRING_KEY_tcp)) {
            HYClient.getSdkOptions().Capture().setTransformMethod(SDKTransformMethod.TCP);
            HYClient.getSdkOptions().Player().setTransformMethod(SDKTransformMethod.TCP);
        } else {
            HYClient.getSdkOptions().Capture().setTransformMethod(SDKTransformMethod.UDP);
            HYClient.getSdkOptions().Player().setTransformMethod(SDKTransformMethod.UDP);
        }

        //默认采集码率为高
        if (TextUtils.isEmpty(SP.getString(STRING_KEY_capture))) {
            SP.setParam(STRING_KEY_capture, HYClient.getSdkOptions().Capture().getCaptureQuality().name());
            SP.putInteger(STRING_KEY_mPublishPresetoption, 2);
        }

        changePublishPresetoption();

        if (SP.getInteger(STRING_KEY_qos, -1) == -1) {
            SP.putInteger(STRING_KEY_qos, HYClient.getSdkOptions().Capture().isQOSOpened() ? 1 : 0);
        } else {
            HYClient.getSdkOptions().Capture().setOpenQOS(SP.getInteger(STRING_KEY_qos, -1) == 1);
        }

        if (TextUtils.isEmpty(SP.getString(STRING_KEY_player))) {
            SP.setParam(STRING_KEY_player, STRING_KEY_VGA);
        }

        if (!TextUtils.isEmpty(SP.getString(STRING_KEY_capturebianma))) {
            if (SP.getString(STRING_KEY_capturebianma).equals(STRING_KEY_ying)) {
                HYClient.getSdkOptions().Capture().setAccelerateMethod(SDKAccelerateMethod.Hardware);
            } else {
                HYClient.getSdkOptions().Capture().setAccelerateMethod(SDKAccelerateMethod.Software);
            }
        } else {
            SP.putString(STRING_KEY_capturebianma, STRING_KEY_ying);
            HYClient.getSdkOptions().Capture().setAccelerateMethod(SDKAccelerateMethod.Hardware);
        }

        //播放编码默认为soft解
        if (!TextUtils.isEmpty(SP.getString(STRING_KEY_playerjiema))) {
            if (SP.getString(STRING_KEY_playerjiema).equals(STRING_KEY_ying)) {
                HYClient.getSdkOptions().Player().setAccelerateMethod(SDKAccelerateMethod.Hardware);
            } else {
                HYClient.getSdkOptions().Player().setAccelerateMethod(SDKAccelerateMethod.Software);
            }
        } else {
            SP.putString(STRING_KEY_playerjiema, STRING_KEY_soft);
            HYClient.getSdkOptions().Player().setAccelerateMethod(SDKAccelerateMethod.Software);
        }

        if (SP.getLong(STRING_KEY_recapture, (long) -1) == -1) {
            SP.putLong(STRING_KEY_recapture, 0);
        }
        if (SP.getLong(STRING_KEY_recapture, (long) -1) == 1) {
            JniIntf.SetSystemProperty(JniIntf.SYSTEM_PROPERTY_ENABLE_RESAMPLE, 1);
        } else {
            JniIntf.SetSystemProperty(JniIntf.SYSTEM_PROPERTY_ENABLE_RESAMPLE, 0);
        }


        if (SP.getParamsInt(STRING_KEY_aec, -1) == -1) {
            SP.putInt(STRING_KEY_aec, HYClient.getSdkOptions().Capture().getAudioEnableAEC().value());
        }
        if (SP.getParamsInt(STRING_KEY_aec, -1) == 0) {
            HYClient.getSdkOptions().Capture().setAudioEnableAEC(SDKAudioAEC.CLOSE);
        } else {
            HYClient.getSdkOptions().Capture().setAudioEnableAEC(SDKAudioAEC.OPEN);
        }

        if (SP.getParamsInt(STRING_KEY_agc, -1) == -1) {
            SP.putInt(STRING_KEY_agc, 0);
        }
        if (SP.getParamsInt(STRING_KEY_agc, -1) == 0) {
            HYClient.getSdkOptions().Capture().setAudioEnableAGC(SDKAudioAGC.CLOSE);
        } else {
            HYClient.getSdkOptions().Capture().setAudioEnableAGC(SDKAudioAGC.OPEN);
        }

        if (SP.getParamsInt(STRING_KEY_ns, -1) == -1) {
            SP.putInt(STRING_KEY_ns, HYClient.getSdkOptions().Capture().getAudioNS().value());
        }
        if (SP.getParamsInt(STRING_KEY_ns, -1) == 0) {
            HYClient.getSdkOptions().Capture().setAudioNS(SDKAudioNS.CLOSE);
        } else {
            HYClient.getSdkOptions().Capture().setAudioNS(SDKAudioNS.OPEN);
        }

        if (SP.getInteger(STRING_KEY_camera, -1) == -1) {
            SP.putInteger(STRING_KEY_camera, 0);
        }

        if (SP.getInteger(STRING_KEY_encrypt, -1) == -1) {
            SP.putInteger(STRING_KEY_encrypt, 0);
        }

        AppUtils.ptt_key = SP.getInteger(STRING_KEY_ptt_key, KEYCODE_STEM_2);
        AppUtils.sos_key = SP.getInteger(STRING_KEY_sos_key, -1);
        AppUtils.camera_key = SP.getInteger(STRING_KEY_camera_key, -1);
        AppUtils.video_key = SP.getInteger(STRING_KEY_video_key, -1);
        AppUtils.ptt_channel_left = SP.getInteger(STRING_KEY_ptt_channel_left_key, -1);
        AppUtils.ptt_channel_right= SP.getInteger(STRING_KEY_ptt_channel_right_key, -1);

    }

    /**
     * 改变mPublishPresetoption
     */
    private static void changePublishPresetoption() {
        int current = SP.getInteger(STRING_KEY_mPublishPresetoption, 0);

        switch (SP.getString(STRING_KEY_capture)) {
            case STRING_KEY_VGA:

                if (current == -1) {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.VGA)
                                    .setmPublishPresetoption(current)
                                    .setBitrate(SP.getInteger(STRING_KEY_bitrate, 0) * 8 * 1000)
                    );
                } else {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.VGA)
                                    .setmPublishPresetoption(current)
                    );
                }
                break;
            case STRING_KEY_HD:

                if (current == -1) {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HDVGA)
                                    .setmPublishPresetoption(current)
                                    .setBitrate(SP.getInteger(STRING_KEY_bitrate, 0) * 8 * 1000)
                    );
                } else {

                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HDVGA)
                                    .setmPublishPresetoption(current)
                    );
                }
                break;
            case STRING_KEY_HD720P:

                if (current == -1) {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HD720P)
                                    .setmPublishPresetoption(current)
                                    .setBitrate(SP.getInteger(STRING_KEY_bitrate, 0) * 8 * 1000)
                    );
                } else {

                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HD720P)
                                    .setmPublishPresetoption(current)
                    );
                }
                break;
            case STRING_KEY_HD1080P:

                if (current == -1) {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HD1080P)
                                    .setmPublishPresetoption(current)
                                    .setBitrate(SP.getInteger(STRING_KEY_bitrate, 0) * 8 * 1000)
                    );
                } else {

                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HD1080P)
                                    .setmPublishPresetoption(current)
                    );
                }
                break;
        }
    }

    // 获取版本号
    public static int versionCode() {
        int version = -1;
        try {
            String packageName = ctx.getPackageName();
            PackageManager packageManager = ctx.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            version = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    // 获取版本名称
    public static String versionName() {
        String versionCode = null;
        try {
            String packageName = ctx.getPackageName();
            PackageManager packageManager = ctx.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getResourceString(int id) {
        return HYClient.getContext().getString(id);
    }

    public static Drawable getResourceDrawable(int id) {
        return HYClient.getContext().getResources().getDrawable(id);
    }

    public static ColorStateList getResourceColor(int id) {
        return HYClient.getContext().getResources().getColorStateList(id);
    }

    public static String getMd5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return str;
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    @SuppressLint("DefaultLocale")
    public static String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = format("%dK", size / 1024);
        } else {
            ret = format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    public static int getResourceDimenssion(int id) {
        return (int) (id * ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int getScreenWidth() {
        return ctx.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return ctx.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 关闭软键盘
     */
    public static void closeKeyboard(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public static void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    /**
     * 通过网络接口取
     *
     * @return
     */
    public static String getNewMac() {
        TelephonyManager TelephonyMgr = (TelephonyManager) ctx.getSystemService(TELEPHONY_SERVICE);
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return TelephonyMgr.getDeviceId() + "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return TelephonyMgr.getDeviceId() + "";
        }
        return TelephonyMgr.getDeviceId() + "";
    }

    public static boolean isNetworkConnected() {
        if (ctx != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 网络状态是否是wifi
     *
     * @return
     */
    public static int getNetWorkStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null
                && info.isConnected()) {
            // 有网状态

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                // wifi 状态
                return 0;
            } else {
                // 3G/4G
                return 1;
            }

        } else {
            // 无网状态
            return -1;
        }
    }

    public static void showToast(String str) {
        mToast.setText(str);
        mToast.show();
    }

    public static void startInstall(Context context, Uri uri) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    public static int getSize(int i) {
        return (int) (ctx.getResources().getDisplayMetrics().density * i);
    }


    public static String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "byte" : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F))
                + "K" : (var0 < 1073741824L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F))
                + "M" : (var0 < 0L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F))
                + "G" : "error")));
    }

    public static void copyAndPass(Context context, String string) {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(string);
    }

    public static String getCaptureStr() {
        switch (SP.getParam(STRING_KEY_capture, "").toString()) {
            case STRING_KEY_VGA:
                return AppUtils.getString(R.string.video_biaoqing);
            case STRING_KEY_HD720P:
                return AppUtils.getString(R.string.video_gaoqing);
            case STRING_KEY_HD1080P:
                return AppUtils.getString(R.string.video_chaoqing);
        }
        return AppUtils.getString(R.string.video_biaoqing);
    }

    public static int getCapturePresetOptionMax() {
        if (TextUtils.isEmpty(SP.getParam(STRING_KEY_capture, "").toString())) {
            SP.setParam(STRING_KEY_capture, HYClient.getSdkOptions().Capture().getCaptureQuality().name());
        }
        int max = 1;
        switch (SP.getParam(STRING_KEY_capture, "").toString()) {
            case STRING_KEY_VGA:
                max = HYCapture.Config.getVGA().getPublicPresetOption();
            case STRING_KEY_HD720P:
                max = HYCapture.Config.getHD720P().getPublicPresetOption();
            case STRING_KEY_HD1080P:
                max = HYCapture.Config.getHD1080P().getPublicPresetOption();
        }
        if (max < 0) {
            max = 0;
        }
        return max;
    }

    public static String getCaptureZhiLiangTxt(float pre) {
        if (pre == 0) {
            return AppUtils.getString(R.string.capture_cha) + AppUtils.getCaptureStr();
        } else if (pre <= 0.25) {
            return AppUtils.getString(R.string.capture_cha) + AppUtils.getCaptureStr();
        } else if (pre <= 0.5) {
            return AppUtils.getString(R.string.capture_yiban) + AppUtils.getCaptureStr();
        } else if (pre <= 0.75) {
            return AppUtils.getString(R.string.capture_lianghao) + AppUtils.getCaptureStr();
        } else {
            return AppUtils.getString(R.string.capture_lianghao) + AppUtils.getCaptureStr();
        }

    }

    public static String getBiLi() {
        switch (SP.getParam(STRING_KEY_capture, "").toString()) {
            case STRING_KEY_VGA:
                return "43";
            case STRING_KEY_HD720P:
            case STRING_KEY_HD1080P:
                return "169";
        }
        return "43";
    }

    public static int getCaptureZhiLiangImg(float pre) {
        if (pre == 0) {
            return R.drawable.xinhao0;
        } else if (pre <= 0.25) {
            return R.drawable.xinhao1;
        } else if (pre <= 0.5) {
            return R.drawable.xinhao2;
        } else if (pre <= 0.75) {
            return R.drawable.xinhao3;
        } else {
            return R.drawable.xinhao4;
        }
    }

    public static boolean isIpAddress(String ip) {
        boolean isIp = false;
        String str = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))";
        Pattern pattern = Pattern.compile(str);
        Matcher m = pattern.matcher(ip);

        isIp = m.matches();
        return isIp;
    }

    public static String getTime(long... time) {
        if (time != null && time.length > 0) {
            return sdf.format(new Date(time[0]));
        }
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    public static String getTimeHour(long... time) {
        if (time != null && time.length > 0) {
            return sdf2.format(new Date(time[0]));
        }
        return sdf2.format(new Date(System.currentTimeMillis()));
    }

    public static void zip(File src, File dest) {
        if (dest.exists())
            dest.delete();
        try {
            dest.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //提供了一个数据项压缩成一个ZIP归档输出流
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(dest));
            //如果此文件是一个文件，否则为false。
            if (src.isFile()) {
                zipFileOrDirectory(out, src, "");
            } else {
                //返回一个文件或空阵列。
                File[] entries = src.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    // 递归压缩，更新curPaths
                    File file = entries[i];
                    if (file.getName().equals("cmf")||file.getName().equals("mbe")){
                        zipFileOrDirectory(out, entries[i], "");
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //关闭输出流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void zipFileOrDirectory(ZipOutputStream out,
                                           File fileOrDirectory, String curPath) throws IOException {
        //从文件中读取字节的输入流
        FileInputStream in = null;
        try {
            //如果此文件是一个目录，否则返回false。
            if (!fileOrDirectory.isDirectory()) {
                if (fileOrDirectory.getName().equals("log-1831-1-run.log")
                        || fileOrDirectory.getName().equals("mbe.log")) {
                    // 压缩文件
                    byte[] buffer = new byte[4096];
                    int bytes_read;
                    in = new FileInputStream(fileOrDirectory);
                    //实例代表一个条目内的ZIP归档
                    ZipEntry entry = new ZipEntry(curPath
                            + fileOrDirectory.getName());
                    //条目的信息写入底层流
                    out.putNextEntry(entry);
                    while ((bytes_read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytes_read);
                    }
                    out.closeEntry();
                }
            } else {
                // 压缩目录
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    // 递归压缩，更新curPaths
                    zipFileOrDirectory(out, entries[i], curPath
                            + fileOrDirectory.getName() + "/");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // throw ex;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    /**
     * 解压zip到指定的路径
     * @param zipFileString  ZIP的名称
     * @param outPathString   要解压缩路径
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String  szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
//                Log.e(TAG,outPathString + File.separator + szName);
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()){
//                    Log.e(TAG, "Create the file:" + outPathString + File.separator + szName);
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public boolean isTalk() {
        return isTalk;
    }

    public void setTalk(boolean talk) {
        isTalk = talk;
    }

    public boolean isMeet() {
        return isMeet;
    }

    public void setMeet(boolean meet) {
        isMeet = meet;
    }

    public boolean isBusy() {
        return isVideo() || isChannel() || isTalk() || isMeet();
    }

    public boolean isChannel() {
        return isChannel;
    }

    public void setChannel(boolean channel) {
        isChannel = channel;
    }

    public static String getString(int id) {
        return ctx.getString(id);
    }

    /**
     * 切换中英文
     */
    public static void shiftLanguage() {
        if (Boolean.parseBoolean(SP.getParam(STRING_KEY_language_show_type, STRING_KEY_false).toString())) {
            Locale.setDefault(Locale.ENGLISH);
            Configuration config = ctx.getResources().getConfiguration();
            config.locale = Locale.ENGLISH;
            ctx.getResources().updateConfiguration(config, ctx.getResources().getDisplayMetrics());
        } else {
            Locale.setDefault(Locale.CHINESE);
            Configuration config = ctx.getResources().getConfiguration();
            config.locale = Locale.CHINESE;
            ctx.getResources().updateConfiguration(config, ctx.getResources().getDisplayMetrics());
        }
    }

    public static void showMsg(boolean player, boolean capture) {
        if (isTalk) {
            showToast(getString(R.string.now_is_talk));
        } else if (isVideo) {
            showToast(getString(R.string.now_is_video));
        } else if (isMeet) {
            showToast(getString(R.string.now_is_meet));
        } else if (player) {
            showToast(getString(R.string.now_is_play));
        } else if (capture) {
            showToast(getString(R.string.now_is_capture));
        } else {
            showToast(getString(R.string.now_is_capture));
        }
    }

    public static Float double2Float(Double d) {
        BigDecimal bigDecimal = new BigDecimal(d);
        return bigDecimal.floatValue();
    }


    /**
     *
     * @param mContext
     * @param is_remove 是否可以移动
     * @return
     */
    public static String getStoragePath(Context mContext, boolean is_remove) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_remove == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String subPath(String path){
       return path.substring(path.lastIndexOf("/") + 1);
    }

   public static void delFile(File file) {
        if (!file.exists()) {
            return ;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        file.delete();
    }
}
