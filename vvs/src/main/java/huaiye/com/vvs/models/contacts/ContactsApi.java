package huaiye.com.vvs.models.contacts;

import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.contacts.bean.PersonBean;
import huaiye.com.vvs.models.contacts.bean.SosPersonBean;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: ContactsModel
 */

public class ContactsApi {

    String URL;

    private ContactsApi() {
    }

    public static ContactsApi get() {
        return new ContactsApi();
    }

    public void getPerson(int nPage, int size, int nOrderByID, int nAscOrDesc,
                          final ModelCallback<PersonBean> callback) {
        URL = AppDatas.Constants().getAddressBaseURL() + "vss/httpjson/get_user_list";
        Https.post(URL)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addParam("nPage", nPage)
                .addParam("nSize", size)
                .addParam("nOrderByID", nOrderByID)
                .addParam("nAscOrDesc", nAscOrDesc)
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .setHttpCallback(new ModelCallback<PersonBean>() {
                    @Override
                    public void onSuccess(PersonBean personBean) {
                        if (personBean != null && callback != null) {
                            callback.onSuccess(personBean);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                    }
                })
                .build()
                .requestNowAsync();
    }
    public void loadSos(final ModelCallback<SosPersonBean> callback) {
        URL = AppDatas.Constants().getAddressBaseURL() + "vss/httpjson/get_seek_help_info";
        Https.post(URL)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .setHttpCallback(new ModelCallback<SosPersonBean>() {
                    @Override
                    public void onSuccess(SosPersonBean personBean) {
                        if (personBean != null && callback != null) {
                            callback.onSuccess(personBean);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                    }
                })
                .build()
                .requestNowAsync();
    }


}
