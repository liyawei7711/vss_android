package huaiye.com.vvs.dao.msgs;

import android.text.TextUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import huaiye.com.vvs.dao.AppDatas;

/**
 * author: admin
 * date: 2018/01/09
 * version: 0
 * mail: secret
 * desc: AppMessages
 */
public class ChatMessages {
    private ChatMessages() {

    }

    static class Holder {
        static final ChatMessages SINGLETON = new ChatMessages();
    }

    public static ChatMessages get() {
        return Holder.SINGLETON;
    }

    public void clear() {
        AppDatas.DB().deleteQuery(VssMessageBean.class).delete();
    }

    public List<VssMessageBean> getMessages(String sessionId) {
        List<VssMessageBean> list = AppDatas.DB().findQuery(VssMessageBean.class)
                .addWhereColumn("ownerId", AppDatas.Auth().getUserID() + "")
                .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                .addWhereColumn("sessionID", sessionId)
                .orderBy("time", "asc")
                .selectAll();
        Collections.sort(list, new Comparator<VssMessageBean>() {
            @Override
            public int compare(VssMessageBean o1, VssMessageBean o2) {
                if (o1.time > o2.time) {
                    return 1;
                } else if (o1.time < o2.time) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return list;
    }

    public List<VssMessageBean> searchMessages() {
        List<VssMessageBean> list = AppDatas.DB().findQuery(VssMessageBean.class)
                .addWhereColumn("ownerId", AppDatas.Auth().getUserID() + "")
                .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                .orderBy("time", "asc")
                .selectAll();
        return list;
    }

    public void add(VssMessageBean data) {
        if (data == null) return;
        if (TextUtils.isEmpty(data.sessionID)) return;
        data.ownerId = AppDatas.Auth().getUserID() + "";
        data.ownerDomain = AppDatas.Auth().getDomainCode();
        data.time = System.currentTimeMillis();
        AppDatas.DB().insertQuery(VssMessageBean.class)
                .insert(data);
    }

    public void del(VssMessageBean data) {
        if (data == null) {
            clear();
        } else {
            del(data.sessionID);
        }
    }

    public void del(String key) {
        AppDatas.DB().deleteQuery(VssMessageBean.class)
                .addWhereColumn("sessionID", key)
                .delete();
    }

}
