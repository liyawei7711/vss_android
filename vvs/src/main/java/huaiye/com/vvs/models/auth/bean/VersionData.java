package huaiye.com.vvs.models.auth.bean;

import java.io.Serializable;

import huaiye.com.vvs.BuildConfig;

/**
 * author: admin
 * date: 2018/02/01
 * version: 0
 * mail: secret
 * desc: VersionData
 */

public class VersionData implements Serializable {

    public int versionCode;
    public String versionName;
    public String message;
    public String path;

    public boolean isNeedToUpdate() {
        return BuildConfig.VERSION_CODE < versionCode;
    }
}
