package huaiye.com.vvs.models;

import huaiye.com.vvs.models.auth.AuthApi;
import huaiye.com.vvs.models.contacts.ContactsApi;
import huaiye.com.vvs.models.download.DownloadApi;
import huaiye.com.vvs.models.meet.MeetApi;

/**
 * author: admin
 * date: 2018/01/03
 * version: 0
 * mail: secret
 * desc: ModelApis
 */

public class ModelApis {

    private ModelApis() {

    }

    public static AuthApi Auth() {
        return AuthApi.get();
    }

    public static ContactsApi Contacts() {
        return ContactsApi.get();
    }

    public static MeetApi Meet() {
        return MeetApi.get();
    }

    public static DownloadApi Download() {
        return DownloadApi.get();
    }
}
