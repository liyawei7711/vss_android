package huaiye.com.vvs.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.huaiye.sdk.logger.Logger;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.common.AppUtils;

public class PttButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.log("PttButtonReceiver action " +intent.getAction());
        if (!AppUtils.isHide){
            Logger.log("PttButtonReceiver app is hide " + AppUtils.isHide);
            return;
        }
        //适配科里讯设备
        if(intent.getAction().equals("com.dfl.s02.pttDown")){
            if (MCApp.getInstance().getMainActivity() != null){
                Logger.log("PttButtonReceiver pttStart");
                MCApp.getInstance().getMainActivity().pttStart();
            }
            return;
        }

        if(intent.getAction().equals("com.dfl.s02.pttup")){
            if (MCApp.getInstance().getMainActivity() != null){
                Logger.log("PttButtonReceiver pttEnd");
                MCApp.getInstance().getMainActivity().pttEnd();
            }
            return;
        }


        Bundle bundle = intent.getExtras();
        if (bundle == null){
            return;
        }

        if (intent.getAction().equals("android.intent.action.PTT_KEY_DOWN")  ){
            boolean haveDown = bundle.getBoolean("action");
            if (haveDown){
                if (MCApp.getInstance().getMainActivity() != null){
                    Logger.log("PttButtonReceiver pttStart");
                    MCApp.getInstance().getMainActivity().pttStart();
                }
            }else {
                if (MCApp.getInstance().getMainActivity() != null){
                    Logger.log("PttButtonReceiver pttEnd " );
                    MCApp.getInstance().getMainActivity().pttEnd();
                }

            }

        }
    }
}
