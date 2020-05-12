package huaiye.com.vvs.models.p2p;

import com.huaiye.cmf.sdp.SdpMsgFindLanCaptureDeviceRsp;

import java.io.Serializable;

public class SdpMsgFindLanCaptureDeviceRspWrap implements Serializable {
    public String m_strIP;
    public String m_strName;
    public String m_strInfo;
    public int m_nCaptureState;

    public SdpMsgFindLanCaptureDeviceRspWrap(SdpMsgFindLanCaptureDeviceRsp rsp){
        this.m_strIP = rsp.m_strIP;
        this.m_strName = rsp.m_strIP;
        this.m_strInfo = rsp.m_strInfo;
        this.m_nCaptureState = rsp.m_nCaptureState;
    }

    public SdpMsgFindLanCaptureDeviceRsp convert(){
        SdpMsgFindLanCaptureDeviceRsp rsp = new SdpMsgFindLanCaptureDeviceRsp();
        rsp.m_strIP = this.m_strIP;
        rsp.m_strName = this.m_strIP;
        rsp.m_strInfo = this.m_strInfo;
        rsp.m_nCaptureState = this.m_nCaptureState;
        return rsp;
    }
}
