package huaiye.com.vvs.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;

import com.huaiye.sdk.HYClient;

import org.greenrobot.eventbus.EventBus;

import huaiye.com.vvs.bus.LowPowerMsg;

public class BatteryHelper {
    private final int LOW_POWER = 20;
    private final String  LOW_POWER_MSG_HAVE_SEND = "low_power_msg";
    private static BatteryHelper mInstance;
    public static BatteryHelper getInstance(){
        if (mInstance == null){
            synchronized (BatteryHelper.class){
                if (mInstance == null){
                    mInstance = new BatteryHelper();
                }
            }
        }
        return mInstance;
    }


    public void init(Context context){
        //每次初始化都重新来
        SP.putBoolean(LOW_POWER_MSG_HAVE_SEND,false);
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(new BatteryReceiver(), filter);
    }


    public class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //如果是正常状态,且到达了低电量的状态.就发送通知,并在本地写入这次已经通知
            //等到了充电状态就删除本地状态
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            int percent = getPercent(intent);

            if (status != BatteryManager.BATTERY_STATUS_CHARGING && status != BatteryManager.BATTERY_STATUS_FULL){

                //在上一次充电后推送过没有
                boolean haveSendInThisPeriod = SP.getBoolean(LOW_POWER_MSG_HAVE_SEND,false);
                //只有低电量且正在采集中才推送
                if ( !haveSendInThisPeriod && percent <= LOW_POWER && HYClient.getHYCapture().isCapturing()){
                    EventBus.getDefault().post(new LowPowerMsg());
                    SP.putBoolean(LOW_POWER_MSG_HAVE_SEND,true);
                }
            }

            //充电后就重置低电量提示
            boolean haveSendInThisPeriod = SP.getBoolean(LOW_POWER_MSG_HAVE_SEND,false);
            if ( haveSendInThisPeriod && status == BatteryManager.BATTERY_STATUS_CHARGING ){
                SP.putBoolean(LOW_POWER_MSG_HAVE_SEND,false);
            }


        }

    }

    private int getPercent(Intent intent){
        Bundle bundle = intent.getExtras();
        // 获取当前电量
        int current = bundle.getInt("level");
        // 获取总电量
        int total = bundle.getInt("scale");
        int percent = current * 100 / total ;
        return percent;
    }
}
