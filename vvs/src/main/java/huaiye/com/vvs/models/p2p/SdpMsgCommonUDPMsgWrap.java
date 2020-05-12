package huaiye.com.vvs.models.p2p;

import com.huaiye.cmf.sdp.SdpMsgCommonUDPMsg;

import java.io.Serializable;

public class SdpMsgCommonUDPMsgWrap implements Serializable {
    public String m_strIP;
    public int m_nMsgType;
    public int m_nSeqNo;
    public String m_strContent;

    public SdpMsgCommonUDPMsgWrap(SdpMsgCommonUDPMsg msg){
        this.m_strIP = msg.m_strIP;
        this.m_nMsgType = msg.m_nMsgType;
        this.m_nSeqNo = msg.m_nSeqNo;
        this.m_strContent = msg.m_strContent;
    }

    public SdpMsgCommonUDPMsg convert(){
        SdpMsgCommonUDPMsg msg = new SdpMsgCommonUDPMsg();
        msg.m_strIP = this.m_strIP;
        msg.m_nMsgType = this.m_nMsgType;
        msg.m_nSeqNo = this.m_nSeqNo;
        msg.m_strContent = this.m_strContent;
        return msg;
    }
}
