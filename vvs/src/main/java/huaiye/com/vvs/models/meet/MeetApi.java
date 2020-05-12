package huaiye.com.vvs.models.meet;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.CallRecordMessage;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.meet.bean.MeetList;
import huaiye.com.vvs.models.meet.bean.RecordMeetList;
import ttyy.com.jinnetwork.Https;

/**
 * author: admin
 * date: 2018/01/09
 * version: 0
 * mail: secret
 * desc: MeetApi
 */

public class MeetApi {

    SimpleDateFormat sdf;

    private MeetApi() {

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    static class Holder {
        static final MeetApi SINGLETON = new MeetApi();
    }

    public static MeetApi get() {
        return Holder.SINGLETON;
    }

    /**
     * 获取当前会议调度
     */
    public void requestCurrentMeets(int index, ModelCallback<RecordMeetList> callback) {
        String URL = AppDatas.Constants().getSieAddress() + "get_meeting_list";

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -3);
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DATE, 7);

        Https.post(URL)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", AppDatas.Auth().getUserID()+"")
                .addParam("strQueryStartTime", sdf.format(c.getTime()))
                .addParam("strQueryEndTime", sdf.format(c2.getTime()))
                .addParam("nStatus", 1)
                .addParam("nPage", index)
                .addParam("nSize", 30)
                .addParam("nReverse", 2)
                .setHttpCallback(callback)
                .build()
                .requestNowAsync();

    }

    /**
     * 获取历史会议调度
     */
    public void requestHistoryMeets(int index, String key, String start, String end, ModelCallback<MeetList> callback) {

        String URL = AppDatas.Constants().getSieAddress() + "get_meeting_list";

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -2);

        String startTime = sdf.format(c.getTime());
        String endTime = sdf.format(new Date());

        if (!TextUtils.isEmpty(start)) {
            startTime = start + " 00:00:00";
        }
        if (!TextUtils.isEmpty(end)) {
            endTime = end + " 23:59:59";
        }


        Https.post(URL)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", AppDatas.Auth().getUserID()+"")
                .addParam("strQueryStartTime", startTime)
                .addParam("strQueryEndTime", endTime)
                .addParam("strMeetingKeywords", key)
                .addParam("nPage", index)
                .addParam("nSize", 20)
                .addParam("nReverse", 2)
                .addParam("nStatus", 2)
                .setHttpCallback(callback)
                .build()
                .requestNowAsync();

    }

}
