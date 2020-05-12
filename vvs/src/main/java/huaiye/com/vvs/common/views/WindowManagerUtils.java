package huaiye.com.vvs.common.views;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.huaiye.sdk.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.CloseEvent;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.ui.home.view.TalkVideoViewLayout;
import huaiye.com.vvs.ui.home.view.TalkViewLayout;
import huaiye.com.vvs.ui.meet.MeetActivity;
import huaiye.com.vvs.ui.meet.MeetViewLayoutNew;
import huaiye.com.vvs.ui.talk.TalkVideoActivity;

import static android.content.Context.WINDOW_SERVICE;
import static huaiye.com.vvs.common.AppUtils.ctx;
import static huaiye.com.vvs.common.AppUtils.showToast;

/**
 * Created by liyawei on 2017-01-19.
 * phone 18952280597
 * QQ    751804582
 * if(WindowManagerUtils.simpleView != null) {
 * WindowManagerUtils.closeAll();
 * } else {
 * SimpleDemoView simpleDemoView = new SimpleDemoView(MyApplication.getInstence());
 * WindowManagerUtils.createSmalls(simpleDemoView, true);
 * }
 */

public class WindowManagerUtils {
    // 创建LayoutParams
    private static WindowManager.LayoutParams layoutParams;
    // 添加到View到窗口
    private static WindowManager windowManager;// = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
    public static ViewGroup simpleView;
    public static SimpleView showView;
    public static DisplayMetrics displayMetrics;

    public WindowManagerUtils() {
        initWindowManager();
    }

    /**
     * 初始话manager
     */
    private static void initWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) AppUtils.ctx.getApplicationContext().getSystemService(WINDOW_SERVICE);
        }
    }

    public static WindowManager getWindownManager() {
        if (windowManager == null)
            initWindowManager();
        return windowManager;
    }

    public static WindowManager.LayoutParams getLayoutParams() {
        if (layoutParams == null)
            initLayoutParams();
        return layoutParams;
    }

    private static void initLayoutParams() {
        layoutParams = new WindowManager.LayoutParams();
        // 设置窗口的类型
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(AppUtils.ctx)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppUtils.ctx.startActivity(intent);
                showToast(AppUtils.getString(R.string.has_connected_false));
                return;
            } else {
                //绘ui代码, 这里说明6.0系统已经有权限了
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
        } else {
            //绘ui代码,这里android6.0以下的系统直接绘出即可
            //API level 18及以下使用TYPE_TOAST无法接收触摸事件
            if (Build.VERSION.SDK_INT <= 18) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }

        // 设置行为选项
        //        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        layoutParams.format = PixelFormat.RGBA_8888;
        // 设置透明度
        //        layoutParams.alpha = 0;
        displayMetrics = AppUtils.ctx.getResources().getDisplayMetrics();
        // 设置位置
        layoutParams.x = AppUtils.getSize(20);
        layoutParams.y = AppUtils.getSize(70);
        // 设置宽高
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;

    }

    public static void createSmall(ViewGroup viewGroup) {
        if (PermissionUtils.XiaoMiMobilePermission(AppUtils.ctx)) {
            return;
        }

        simpleView = viewGroup;

        if (showView == null) {
            showView = new SimpleView(ctx);
        }

        if (simpleView instanceof MeetViewLayoutNew) {
            showView.setTileName(AppUtils.getString(R.string.meet_diaodu_ing));
            showView.setLogo(R.drawable.huiyitonghua);
        } else if (simpleView instanceof TalkVideoViewLayout) {
            showView.setTileName(AppUtils.getString(R.string.video_diaodu_ing));
            showView.setLogo(R.drawable.shipingtonghua);
        } else if (simpleView instanceof TalkViewLayout) {
            showView.setTileName(AppUtils.getString(R.string.talk_diaodu_ing));
            showView.setLogo(R.drawable.shipingtonghua);
        }

        showView.setOnClickListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAll(false);
                if (simpleView instanceof MeetViewLayoutNew) {
                    MeetActivity.stratMeet(MCApp.getInstance().getTopActivity(), false, "", 1, 1, null);
                } else if (simpleView instanceof TalkVideoViewLayout) {
                    TalkVideoActivity.joinTalk(MCApp.getInstance().getTopActivity(), "", -1, null);
                } else if (simpleView instanceof TalkViewLayout) {
                    EventBus.getDefault().post(new CloseEvent());
                }
                simpleView = null;
            }
        });

        if (ViewCompat.isAttachedToWindow(showView)) {
            try {
                getWindownManager().removeView(showView);
            } catch (Exception e) {

            }
        }

        try {
            if (!ViewCompat.isAttachedToWindow(showView))
                getWindownManager().addView(showView, getLayoutParams());
        } catch (Exception e) {
            Logger.log("SIMPLEVIEW ERROR createSmall "+e.toString());
        }
    }

    /**
     * 关闭所有窗体
     */
    public static void closeAll(boolean needReSetSimpleView) {
        if (showView != null && ViewCompat.isAttachedToWindow(showView)) {
            try {
                getWindownManager().removeView(showView);
            } catch (Exception e) {
                Logger.log("SIMPLEVIEW ERROR closeAll "+e.toString());
            }
        }

        if(needReSetSimpleView) {
            simpleView = null;
        }

        showView = null;
        windowManager = null;
        layoutParams = null;
    }

    public static void justRemove() {
        if (showView != null && ViewCompat.isAttachedToWindow(showView)) {
            try {
                getWindownManager().removeView(showView);
            } catch (Exception e) {
                Logger.log("SIMPLEVIEW ERROR justRemove "+e.toString());
            }
        }
    }

    public static void justReShow() {
        if (showView != null && !ViewCompat.isAttachedToWindow(showView)) {
            try {
                getWindownManager().addView(showView, getLayoutParams());
            } catch (Exception e) {
                Logger.log("SIMPLEVIEW ERROR justReShow "+e.toString());
            }
        }
    }

    public static void showTime(String s) {
        if (showView != null && ViewCompat.isAttachedToWindow(showView)) {
            showView.setTimeText(s);
        }
    }
}
