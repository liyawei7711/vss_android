package huaiye.com.vvs.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.huaiye.sdk.logger.Logger;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.common.ScreenNotify;

import static huaiye.com.vvs.common.AppUtils.STRING_KEY_needload;

/**
 * author: admin
 * date: 2018/11/15
 * version: 0
 * mail: secret
 * desc: DeskService
 */

public class DeskService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.log("onCreate被调用，启动前台service");
        ScreenNotify.startForegroundService(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 如果Service被终止
        // 当资源允许情况下，重启service
        startLocation();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.log("DeskService onDestroy");
        ScreenNotify.cancel(this,ScreenNotify.FORGROUND_NOTICE_ID);
    }


    private void startLocation(){
        Logger.log("DeskService startLocation start");
        MCApp app = MCApp.getInstance();
        if (app != null){
            if(!SP.getBoolean(STRING_KEY_needload, false)){
                return;
            }
            Logger.log("DeskService startLocation start success");
            app.locationService.start();
        }
    }

}