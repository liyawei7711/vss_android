package huaiye.com.vvs.bus;

/**
 * author: admin
 * date: 2018/09/26
 * version: 0
 * mail: secret
 * desc: LogoutBean
 */

public class LogoutBean {
    private boolean clearAll;


    public LogoutBean() {
        this.clearAll = false;
    }

    public LogoutBean(boolean clearAll) {
        this.clearAll = clearAll;
    }

    public boolean isClearAll() {
        return clearAll;
    }

    public void setClearAll(boolean clearAll) {
        this.clearAll = clearAll;
    }
}
