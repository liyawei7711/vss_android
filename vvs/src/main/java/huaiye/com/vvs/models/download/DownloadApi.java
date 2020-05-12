package huaiye.com.vvs.models.download;

import android.content.Context;

import java.io.File;

import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.auth.bean.Upload;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.callback.HTTPCallback;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
import ttyy.com.jinnetwork.core.work.method_post.PostContentType;

/**
 * author: admin
 * date: 2018/02/22
 * version: 0
 * mail: secret
 * desc: DownloadApi
 */

public class DownloadApi {
    public static boolean isLoad = false;
    String URL;

    public static DownloadApi get() {
        return new DownloadApi();
    }

    private DownloadApi() {
        URL = AppDatas.Constants().getAddressBaseURL() + "download/load.action";
    }

    public void download(final Context context, String downLoadUrl) {
        Https.get(downLoadUrl)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .setDownloadMode(new File(context.getExternalCacheDir().getPath(), "newapk.apk"))
                .setHttpCallback(new HTTPCallback() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onProgress(HTTPResponse httpResponse, long l, long l1) {

                    }

                    @Override
                    public void onSuccess(HTTPResponse httpResponse) {

                    }

                    @Override
                    public void onCancel(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {

                    }
                });
    }

    public void upload(final ModelCallback<Upload> callback, File file) {
        String URL = AppDatas.Constants().getFileAddressURL() + "upload_file_lua";
        Https.post(URL, PostContentType.MultipartFormdata)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addParam("file1", file)
                .setHttpCallback(new ModelCallback<Upload>() {
                    @Override
                    public void onSuccess(final Upload versionData) {
                        callback.onSuccess(versionData);
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        callback.onFailure(httpResponse);
                    }

                })
                .build()
                .requestNowAsync();
    }

    public void uploadNew(final ModelCallback<Upload> callback, File file) {
        String URL = AppDatas.Constants().getFileAddressURL() + "upload_file_lua";
        Https.post(URL, PostContentType.MultipartFormdata)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addParam("file1", file)
                .setHttpCallback(new ModelCallback<Upload>() {
                    @Override
                    public void onSuccess(final Upload versionData) {
                        callback.onSuccess(versionData);
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        callback.onFailure(httpResponse);
                    }

                })
                .build()
                .requestNowAsync();
    }

}
