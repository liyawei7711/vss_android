package huaiye.com.vvs.models.download;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ScreenNotify;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.BroadcastManage;
import huaiye.com.vvs.dao.msgs.BroadcastMessage;
import huaiye.com.vvs.ui.home.MainActivity;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.callback.HTTPCallback;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * ******************************
 *
 * @文件名称:DownloadService.java
 * @文件作者:Administrator
 * @创建时间:2015年10月22日
 * @文件描述: *****************************
 */
public class DownloadService extends IntentService {
    private static final String TAG = "DownloadService";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    int downloadCount = 0;

    private String apkUrl = "";

    public DownloadService() {
        super("DownloadService");
    }

    private File outputFile;
    private File audioVideoFile;
    private Intent intent;
    private boolean isAudioVideo;
    private int type = -1;

    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
        DownloadApi.isLoad = true;

        apkUrl = intent.getStringExtra("downloadURL");
        isAudioVideo = intent.getBooleanExtra("isAudioVideo", false);
        type = intent.getIntExtra("type",-1);
        if (!isAudioVideo) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notificationBuilder = new NotificationCompat.Builder(this, "my_channel_01");
            } else {
                notificationBuilder = new NotificationCompat.Builder(this);
            }
            notificationBuilder.setSmallIcon(R.drawable.logo)
                    .setContentTitle(AppUtils.getString(R.string.download_title))
                    .setContentText(AppUtils.getString(R.string.pre_load))
                    .setAutoCancel(true);

            notificationManager.notify(ScreenNotify.DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());
            download();
        } else {
            downloadAudioORVideo(apkUrl);
        }
    }

    long startTime = 0;

    int i = 0;

    private void download() {

        outputFile = new File(Environment.getExternalStorageDirectory(), "hy_vss_update.apk");

        if (outputFile.exists()) {
            outputFile.delete();
        }
        Https.get(apkUrl)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .setDownloadMode(outputFile)
                .setHttpCallback(new HTTPCallback() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onProgress(HTTPResponse httpResponse, long l, long l1) {
                        if (System.currentTimeMillis() - startTime > 1000) {
                            startTime = System.currentTimeMillis();
                            //不频繁发送通知，防止通知栏下拉卡顿
                            int progress = (int) ((l * 100) / l1);
                            if ((downloadCount == 0) || progress > downloadCount) {
                                sendNotification(l1, l, progress);
                            }
                        } else {
                            return;
                        }
                    }

                    @Override
                    public void onSuccess(HTTPResponse httpResponse) {
                        downloadCompleted(AppUtils.getString(R.string.download_success));
                    }

                    @Override
                    public void onCancel(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        downloadCompleted(AppUtils.getString(R.string.download_false));
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {

                    }
                }).build().requestNowAsync();

    }

    private void downloadAudioORVideo(final String path) {
        String downUrl = AppDatas.Constants().getFileAddressURL() + path;
        String fileName = apkUrl.substring(apkUrl.lastIndexOf("/") + 1);
        audioVideoFile = new File(AppUtils.audiovideoPath);

        if (!audioVideoFile.exists()) {
            audioVideoFile.mkdir();
        }
        Https.get(downUrl)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .setDownloadMode(new File(audioVideoFile, AppUtils.subPath(apkUrl)))
                .setHttpCallback(new HTTPCallback() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        BroadcastManage.get().updateSuccess(path, BroadcastMessage.DOWNING);
                    }

                    @Override
                    public void onProgress(HTTPResponse httpResponse, long l, long l1) {
                    }

                    @Override
                    public void onSuccess(HTTPResponse httpResponse) {
                        BroadcastManage.get().updateSuccess(path, BroadcastMessage.SUCCESS);
//                        EventBus.getDefault().post(new BroadcastMessage(path.endsWith(".dat") ? BroadcastMessage.TYPE_VIDEO : BroadcastMessage.TYPE_AUDIO, path));
                        if (type!=-1)
                            EventBus.getDefault().post(new BroadcastMessage(type == AppUtils.BROADCAST_VIDEO_TYPE_INT ? BroadcastMessage.TYPE_VIDEO : BroadcastMessage.TYPE_AUDIO, path));

                    }

                    @Override
                    public void onCancel(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        BroadcastManage.get().updateSuccess(path, BroadcastMessage.ERROR);
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {

                    }
                }).build().requestNowAsync();

    }


    private void downloadCompleted(String str) {
        notificationManager.cancel(ScreenNotify.DOWNLOAD_NOTIFICATION_ID);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText(str);
        notificationManager.notify(ScreenNotify.DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());

        //安装apk
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(Uri.fromFile(outputFile), "application/vnd.android.package-archive");
//        startActivity(intent);
        if (!str.contains("Error")) {
            notificationManager.cancel(ScreenNotify.DOWNLOAD_NOTIFICATION_ID);
            installAPKFile(outputFile);
        }
    }

    /**
     * 调起安装
     *
     * @param file
     */
    protected void installAPKFile(final File file) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);
        if (!infos.isEmpty()
                && infos.size() > 0) {

            ActivityManager.RunningTaskInfo info = infos.get(0);
            String infoCLassName = info.topActivity.getClassName();
            if (infoCLassName.equals(MainActivity.class.getCanonicalName())) {

                Intent intent = new Intent(DownloadService.this, InstallDialogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(InstallDialogActivity.APK_PATH, file.getPath());
                startActivity(intent);

                DownloadApi.isLoad = false;
                return;
            }
        }

        Intent intent = new Intent(DownloadService.this, InstallDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(InstallDialogActivity.APK_PATH, file.getPath());

        startActivity(intent);

        DownloadApi.isLoad = false;
    }

    private void sendNotification(long contentLength, long bytesRead, int progress) {
        notificationBuilder.setProgress(100, progress, false);
        notificationBuilder.setContentText(
                AppUtils.getDataSize(bytesRead) + "/" +
                        AppUtils.getDataSize(contentLength));
        notificationManager.notify(ScreenNotify.DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(ScreenNotify.DOWNLOAD_NOTIFICATION_ID);
    }
}
