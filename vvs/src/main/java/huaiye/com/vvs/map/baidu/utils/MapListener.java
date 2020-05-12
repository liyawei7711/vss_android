package huaiye.com.vvs.map.baidu.utils;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.huaiye.sdk.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;

import static huaiye.com.vvs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2018/01/16
 * version: 0
 * mail: secret
 * desc: ChoosedContacts
 */

public class MapListener implements MKOfflineMapListener {

    private MKOfflineMap mOffline;
    private ArrayList<String> loadDing = new ArrayList<>();

    private MapListener() {

    }

    @Override
    public void onGetOfflineMapState(int type, int state) {
        postProgress(state);
    }

    private void postProgress(int state) {
        MKOLUpdateElement update = mOffline.getUpdateInfo(state);
        if (update == null) {

        } else {
            Logger.log("MapListener Load Map " + update.cityID + "   " + update.cityName + "    " + update.ratio + "   " + update.status);
            if (update.ratio == 100) {
                loadDing.remove(update.cityID + "");
            } else {
                if (!loadDing.contains(state + "")) {
                    loadDing.add(state + "");
                }
            }
            EventBus.getDefault().post(update);
        }
    }

    public void start() {
        if (mOffline == null) {
            mOffline = new MKOfflineMap();
            mOffline.init(this);
        }
    }

    public void destroy() {
        if (mOffline != null) {
            mOffline.destroy();
            mOffline = null;
        }
    }

    public void startLoad(int cityID) {
        if (loadDing.size() >= 3) {
            showToast(AppUtils.getString(R.string.max_download));
            return;
        }
        if (mOffline != null) {
            if (mOffline.start(cityID)) {
                if (!loadDing.contains(cityID + "")) {
                    loadDing.add(cityID + "");
                }
            } else {
                showToast(AppUtils.getString(R.string.start_false));
            }
            postProgress(cityID);
        }
    }

    public void pauseLoad(int cityID) {
        if (!loadDing.contains(cityID + "")) {
            loadDing.remove(cityID + "");
        }
        if (mOffline != null) {
            if (mOffline.pause(cityID)) {

            } else {
                showToast(AppUtils.getString(R.string.pause_false));
            }
            postProgress(cityID);
        }
    }

    public void removeMap(int cityID) {
        if (loadDing.contains(cityID + "")) {
            loadDing.remove(cityID + "");
        }
        if (mOffline != null) {
            if (mOffline.remove(cityID)) {

            } else {
                showToast(AppUtils.getString(R.string.map_delete_false));
            }
            postProgress(cityID);
        }
    }

    /**
     * 更新
     *
     * @param cityID
     */
    public void updateMap(int cityID) {
        if (loadDing.size() >= 3) {
            showToast(AppUtils.getString(R.string.max_download));
            return;
        }
        if (mOffline != null) {
            if (mOffline.update(cityID)) {
                if (!loadDing.contains(cityID + "")) {
                    loadDing.add(cityID + "");
                }
            } else {
                showToast(AppUtils.getString(R.string.map_update_no));
            }
            postProgress(cityID);
        }
    }

    static class Holder {
        static final MapListener SINGLETON = new MapListener();
    }

    public static MapListener get() {
        return Holder.SINGLETON;
    }

    public MKOfflineMap getMkOfflineMap() {
        if (mOffline == null) {
            start();
        }
        return mOffline;
    }

}
