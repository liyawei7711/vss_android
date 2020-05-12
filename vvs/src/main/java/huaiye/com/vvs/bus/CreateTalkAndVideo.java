package huaiye.com.vvs.bus;

import com.huaiye.cmf.sdp.SdpMsgFindLanCaptureDeviceRsp;

/**
 * author: admin
 * date: 2018/05/29
 * version: 0
 * mail: secret
 * desc: CloseView
 */

public class CreateTalkAndVideo {
    public boolean hasVideo;
    public String domain;
    public String id;
    public String name;
    public String from;

    public SdpMsgFindLanCaptureDeviceRsp device;

    public CreateTalkAndVideo(boolean hasVideo, String domain, String id, String name, SdpMsgFindLanCaptureDeviceRsp device, String from) {
        this.hasVideo = hasVideo;
        this.domain = domain;
        this.id = id;
        this.name = name;
        this.device = device;
        this.from = from;
    }
}
