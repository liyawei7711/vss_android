package huaiye.com.vvs.models;

import java.io.Serializable;

/**
 * author: admin
 * date: 2018/05/29
 * version: 0
 * mail: secret
 * desc: InvistorBean
 */

public class InvistorBean implements Serializable {

    public static final int MEET = 1;
    public static final int TALK_VIDEO = 2;
    public static final int TALK_TALK = 3;

    public int type;
    public int id;
    public String domain;
    public String userid;
    public String userdomain;
    public String username;
    public long millis;

    public int nVoiceIntercom;

    public InvistorBean(int type, int id, String domain, String userid, String userdomain, String username, long millis, int nVoiceIntercom) {
        this.type = type;
        this.id = id;
        this.domain = domain;
        this.userid = userid;
        this.userdomain = userdomain;
        this.username = username;
        this.millis = millis;
        this.nVoiceIntercom = nVoiceIntercom;
    }

    public boolean isMeet() {
        return type == MEET;
    }

    public boolean isTalkVideo() {
        return type == TALK_VIDEO;
    }

    public boolean isTalkTalk() {
        return type == TALK_TALK;
    }
}
