package huaiye.com.vvs.dao.msgs;

import android.text.TextUtils;

import java.util.List;

import huaiye.com.vvs.dao.AppDatas;

/**
 * author: admin
 * date: 2018/01/09
 * version: 0
 * mail: secret
 * desc: AppMessages
 */
public class VssMessageListMessages {
    private VssMessageListMessages() {

    }

    static class Holder {
        static final VssMessageListMessages SINGLETON = new VssMessageListMessages();
    }

    public static VssMessageListMessages get() {
        return Holder.SINGLETON;
    }

    public void clear() {
        AppDatas.DB().deleteQuery(VssMessageListBean.class).delete();
    }

    public List<VssMessageListBean> getMessages() {
        List<VssMessageListBean> list = AppDatas.DB().findQuery(VssMessageListBean.class)
                .addWhereColumn("ownerId", AppDatas.Auth().getUserID() + "")
                .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                .orderBy("time", "desc")
                .selectAll();
        return list;
    }

    public VssMessageListBean getMessages(String sessionID) {
        VssMessageListBean bean = AppDatas.DB().findQuery(VssMessageListBean.class)
                .addWhereColumn("ownerId", AppDatas.Auth().getUserID() + "")
                .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                .addWhereColumn("sessionID", sessionID)
                .orderBy("time", "desc")
                .selectFirst();
        return bean;
    }

    public boolean getMessagesUnRead() {
        try {
            List<VssMessageListBean> list = AppDatas.DB().findQuery(VssMessageListBean.class)
                    .addWhereColumn("ownerId", AppDatas.Auth().getUserID() + "")
                    .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                    .addWhereColumn("isRead", 0)
                    .selectAll();
            if (list == null) {
                return false;
            }
            if (list.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    public void isRead(VssMessageListBean bean) {
        AppDatas.DB().updateQuery(VssMessageListBean.class)
                .addWhereColumn("sessionID", bean.sessionID)
                .addWhereColumn("ownerId", AppDatas.Auth().getUserID())
                .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                .addUpdateColumn("isRead", 1)
                .update();
    }

    public void add(VssMessageListBean data) {
        if (data == null) return;
        if (TextUtils.isEmpty(data.sessionID)) return;
        data.ownerId = AppDatas.Auth().getUserID() + "";
        data.ownerDomain = AppDatas.Auth().getDomainCode();

        AppDatas.DB().insertQuery(VssMessageListBean.class)
                .insert(data);
    }

    public void del(VssMessageListBean data) {
        if (data == null) {
            clear();
        } else {
            del(data.sessionID);
        }
    }

    public void del(String key) {
        AppDatas.DB().deleteQuery(VssMessageListBean.class)
                .addWhereColumn("sessionID", key)
                .delete();
    }

}
