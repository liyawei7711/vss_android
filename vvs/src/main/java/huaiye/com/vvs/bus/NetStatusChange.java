package huaiye.com.vvs.bus;

import com.huaiye.sdk.sdkabi._params.SdkBaseParams;

/**
 * author: admin
 * date: 2018/05/29
 * version: 0
 * mail: secret
 * desc: ChannelInvistor
 */

public class NetStatusChange {
    public SdkBaseParams.ConnectionStatus data;
    /**
     * 是否已经尝试过重新登陆
     */
    public boolean haveTryLogin;


    public NetStatusChange(SdkBaseParams.ConnectionStatus data) {
        this.data = data;
        this.haveTryLogin = false;
    }

    public NetStatusChange(SdkBaseParams.ConnectionStatus data, boolean haveTryLogin) {
        this.data = data;
        this.haveTryLogin = haveTryLogin;
    }

    //    public NetStatusChange(SdkBaseParams.ConnectionStatus data, CQueryUserListRsp.UserInfo usrInfo) {
//        this.data = data;
//        this.usrInfo = usrInfo;
//    }
}
