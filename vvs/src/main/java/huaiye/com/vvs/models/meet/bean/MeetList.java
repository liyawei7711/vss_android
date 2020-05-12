package huaiye.com.vvs.models.meet.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/01/19
 * version: 0
 * mail: secret
 * desc: MeetList
 */

public class MeetList implements Serializable {

    public int nResultCode;
    public String strResultDescribe;
    public int nTotalSize;
    public ArrayList<Data> lstMeetingInfo;

    public static class Data implements Serializable {
        public int nMeetingID;//": 1,
        public String strMeetingName;//": "testMeeting",
        public String strMeetingDesc;//": "testDesc",
        public int nMode;//":1,
        public String strStartTime;//": "2018-01-01 10:10:00",
        public int nTimeDuration;//": 10,
        public int nStatus;//":1,
        public String strMainUserDomainCode;//": "testDomain",
        public String strMainUserID;//": "testUser",
        public String strMainUserName;//": "testUserName"
        public int nInitiaterObserve;//": "nForceInvite" 1 watch
        public int nRecordID;//": "nRecordId"
        public int nMeetingType;//1预约  0及时

        public boolean isWatch() {
            return nInitiaterObserve == 1;
        }
    }
}
