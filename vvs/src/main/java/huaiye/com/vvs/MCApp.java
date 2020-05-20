package huaiye.com.vvs;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import huaiye.com.vvs.bus.LogoutBean;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.BatteryHelper;
import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.common.ScreenNotify;
import huaiye.com.vvs.common.views.WindowManagerUtils;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.map.baidu.LocationService;
import huaiye.com.vvs.models.CommonResult;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.auth.AuthApi;
import huaiye.com.vvs.models.auth.KickOutUIObserver;
import huaiye.com.vvs.push.MessageReceiver;
import huaiye.com.vvs.push.VolumeObserver;
import huaiye.com.vvs.services.DeskService;
import huaiye.com.vvs.ui.auth.LoginActivity;
import huaiye.com.vvs.ui.home.MainActivity;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_needload;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: MCApp
 */

public class MCApp extends MultiDexApplication {

    public static MCApp instance;
    public LocationService locationService;
    ArrayList<AppBaseActivity> allActivity = new ArrayList<>();


    public void addActivity(AppBaseActivity activity) {
        if (!allActivity.contains(activity)) {
            allActivity.add(activity);
        }
    }

    public void removeActivity(AppBaseActivity activity) {
        if (allActivity.contains(activity)) {
            allActivity.remove(activity);
        }
        //强杀了后,通知后台我已经退出了
        if (allActivity.isEmpty() && AppUtils.isHide) {
            Logger.log("强杀了");
        }
    }

    public MainActivity getMainActivity() {
        for (AppBaseActivity temp : allActivity) {
            if (temp instanceof MainActivity) {
                return (MainActivity) temp;
            }
        }
        return null;
    }

    public void stopApp() {
        for (int i = allActivity.size() - 1; i >= 0; i--) {
            Activity activity = allActivity.get(i);
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
                Log.i("AppBaseClose MCApp", "close_activity " + activity.getClass().getSimpleName());
                activity = null;
            }
        }
        allActivity.clear();
        topActivity = null;
    }

    public static MCApp getInstance() {
        return instance;
    }

    public AppBaseActivity getTopActivity() {
//        return allActivity.get(allActivity.size() - 1);
        return topActivity;
    }

    public void gotoLogin() {
        gotoLogin(false);
    }

    public void gotoLogin(boolean showDialog) {
        EventBus.getDefault().post(new KickOutUIObserver.UIKickout());
        SP.putBoolean(STRING_KEY_needload, false);

        AuthApi.get().logout();
        Intent loginIntent = new Intent();
        if (showDialog) {
            loginIntent.putExtra("showKickOutDialog", 1);
        }
        if (topActivity != null) {
            Logger.debug("topActivity not null");
            loginIntent.setClass(topActivity, LoginActivity.class);
            topActivity.startActivity(loginIntent);
        } else {
            Logger.debug("topActivity is null");
            loginIntent.setClass(MCApp.this, LoginActivity.class);
            loginIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
        }
        EventBus.getDefault().post(new LogoutBean());

    }

    public void gotoClose() {
        EventBus.getDefault().post(new LogoutBean(true));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String str = getProcessName(getApplicationContext());
        if (!TextUtils.isEmpty(str)) {
            if (str.endsWith(":remote")) {
                return;
            }
        }

        SP.init(this);
        HYClient.initSdk(this);
        AppDatas.init(this);
        AppUtils.init(this);
        SDKInitializer.initialize(this);
        MessageReceiver.get();

        locationService = new LocationService(getApplicationContext());
        instance = this;
        Logger.setRuntimeExceptionCallback(new Logger.RuntimeExceptionCallback() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Logger.setRuntimeExceptionCallback(new Logger.RuntimeExceptionCallback() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable throwable) {
                        AuthApi.get().uploadLogOnCrash(false);
                    }
                });
            }
        });

        BatteryHelper.getInstance().init(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleListener());

        VolumeObserver volumnObserver = new VolumeObserver(null);
        getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, volumnObserver);
//        if (BuildConfig.DEBUG) {
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                return;
//            }
//            LeakCanary.install(this);
//        }
        ScreenNotify.createNotificationChannel(this);
        //打印commitid到日志里
        Logger.log("VSS init Commit id = " + BuildConfig.commit);

    }


    AppBaseActivity topActivity;

    class ActivityLifecycleListener implements ActivityLifecycleCallbacks {

        private int refCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            refCount++;
            AppUtils.isHide = false;

            WindowManagerUtils.justReShow();
            Intent stopIntent = new Intent(instance, DeskService.class);
            stopService(stopIntent);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (BuildConfig.DEBUG) {
                if (activity instanceof AppBaseActivity) {
                    topActivity = (AppBaseActivity) activity;
                }
                return;
            }
            topActivity = (AppBaseActivity) activity;
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            refCount--;
            AppUtils.isHide = false;
            if (refCount == 0) {
                AppUtils.isHide = true;

                WindowManagerUtils.justRemove();
                startService(new Intent(instance, DeskService.class));
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }

    public void keepAlive() {
        String URL = AppDatas.Constants().getAddressBaseURL() + "vss/httpjson/user_keep_alive";
        Https.post(URL)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addParam("strUserID", AppDatas.Auth().getUserID() + "")
                .setHttpCallback(new ModelCallback<CommonResult>() {

                    @Override
                    public void onPreStart(HTTPRequest request) {
                        super.onPreStart(request);
                    }

                    @Override
                    public void onSuccess(CommonResult response) {
                    }

                    @Override
                    public void onFailure(HTTPResponse response) {
                        super.onFailure(response);
                    }
                })
                .build()
                .requestNowAsync();

    }


    Timer mTimer;
    TimerTask mTimerTask;

    public void startTimer() {
        stopTimer();

        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    keepAlive();
                }
            };
        }
        if (mTimer != null)
            mTimer.schedule(mTimerTask, 1000, 60 * 1000);
    }

    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }


    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}
