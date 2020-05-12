package huaiye.com.vvs.dao.msgs;

import java.util.List;

import huaiye.com.vvs.dao.AppDatas;

import static huaiye.com.vvs.dao.msgs.CallRecordMessage.CALL_IN;

/**
 * author: admin
 * date: 2018/01/09
 * version: 0
 * mail: secret
 * desc: AppMessages
 */

public class CallRecordManage {

    private CallRecordManage() {

    }

    static class Holder {
        static final CallRecordManage SINGLETON = new CallRecordManage();
    }

    public static CallRecordManage get() {
        return Holder.SINGLETON;
    }

    public void clear() {
        AppDatas.DB().deleteQuery(CallRecordMessage.class).delete();
    }

    public List<CallRecordMessage> getMessages() {
        List<CallRecordMessage> list = AppDatas.DB().findQuery(CallRecordMessage.class)
                .addWhereColumn("userId", AppDatas.Auth().getUserID())
                .addWhereColumn("domainCode", AppDatas.Auth().getDomainCode())
                .orderBy("nMillions", "desc")
                .selectAll();
//        Collections.reverse(list);

        return list;
    }



    public void add(CallRecordMessage data) {
        AppDatas.DB().insertQuery(CallRecordMessage.class)
                .insert(data);
    }

    public void del(CallRecordMessage data) {
        if (data == null) {
            clear();
        } else {
            del(data.nMillions);
        }
    }

    public void del(long key) {
        AppDatas.DB().deleteQuery(CallRecordMessage.class)
                .addWhereColumn("nMillions", key)
                .delete();
    }

    public void del(int nMsgSessionID) {
        AppDatas.DB().deleteQuery(CallRecordMessage.class)
                .addWhereColumn("nMsgSessionID", nMsgSessionID)
                .delete();
    }
    public void delAll() {
        AppDatas.DB().deleteQuery(CallRecordMessage.class)
                .addWhereColumn("userId", AppDatas.Auth().getUserID())
                .addWhereColumn("domainCode", AppDatas.Auth().getDomainCode())
                .delete();
    }

    public void updateCall(int nMsgSessionID) {
        AppDatas.DB().updateQuery(CallRecordMessage.class)
                .addWhereColumn("userId", AppDatas.Auth().getUserID())
                .addWhereColumn("domainCode", AppDatas.Auth().getDomainCode())
                .addWhereColumn("nMsgSessionID", nMsgSessionID)
                .addUpdateColumn("nAcceptType", CALL_IN)
                .update();
    }

    public void del(String key) {
        AppDatas.DB().deleteQuery(CallRecordMessage.class)
                .addWhereColumn("key", key)
                .delete();
    }

}
