package huaiye.com.vvs.models.map.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/05/12
 * version: 0
 * mail: secret
 * desc: OfflineMapBean
 */

public class OfflineMapBean implements Serializable {

    public int cityID;
    /**
     * @deprecated
     */
    public int size;
    public long dataSize;
    public String cityName;
    public int cityType;

    public boolean openStatus;
    public boolean isDownload;
    public boolean isWaite;
    public boolean isPause;

    public ArrayList<OfflineMapBean> childCities = new ArrayList<>();

}
