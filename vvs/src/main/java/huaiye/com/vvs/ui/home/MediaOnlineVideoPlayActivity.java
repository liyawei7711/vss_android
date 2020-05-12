package huaiye.com.vvs.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.media.player.HYPlayer;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.views.MediaRecordProgress;
import huaiye.com.vvs.dao.AppDatas;

/**
 * author: admin
 * date: 2017/09/15
 * version: 0
 * mail: secret
 * desc: MediaLocalVideoPlayActivity
 */
@BindLayout(R.layout.activity_local_play)
public class MediaOnlineVideoPlayActivity extends AppBaseActivity {

    @BindView(R.id.video_texture)
    TextureView video_texture;
    @BindView(R.id.view_progress)
    MediaRecordProgress view_progress;
    @BindView(R.id.iv_play_status)
    ImageView iv_play_status;

    @BindExtra
    String path;

    private boolean isVideoPaused = false;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("MediaOnlineVideoPlay", "onCreate" + path);
        if (getLogicDialog().isShowing()) {
            return;
        }
        onPause();
        getLogicDialog().setMessageText(AppUtils.getString(R.string.broadcast_video))
                .setConfirmText(AppUtils.getString(R.string.makesure))
                .setCancelText(AppUtils.getString(R.string.cancel))
                .setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HYClient.getHYPlayer().stopPlayEx(new SdkCallback<VideoParams>() {
                            @Override
                            public void onSuccess(VideoParams videoParams) {
                                playVideo();
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {

                            }
                        }, video_texture);
                    }
                }).setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
            }
        }).show();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setVisibility(View.GONE);
        Log.e("MediaOnlineVideoPlay", "onCreate" + path);
    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {
        playVideo();

    }

    private void playVideo() {

        view_progress.attachVideoTexture(video_texture);
        HYClient.getHYPlayer().startPlay(Player.Params.TypeVideoOfflineRecord()
                .setResourcePath(AppUtils.audiovideoPath + "/" + AppUtils.subPath(path))
                .setPreview(video_texture)
                .setMixCallback(new VideoCallbackWrapper() {
                    @Override
                    public void onSuccess(VideoParams param) {
                        super.onSuccess(param);
                        showToast(AppUtils.getString(R.string.player_success));
                    }

                    @Override
                    public void onGetVideoRange(VideoParams param, int start, int end) {
                        super.onGetVideoRange(param, start, end);
                        view_progress.setMaxTime(end);
                    }

                    @Override
                    public void onVideoProgressChanged(VideoParams param, HYPlayer.ProgressType type, int current, int total) {
                        super.onVideoProgressChanged(param, type, current, total);
                        view_progress.setCurrentTime(current);
                    }

                    @Override
                    public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                        super.onVideoStatusChanged(param, msg);
                        SdkMsgNotifyPlayStatus status = (SdkMsgNotifyPlayStatus) msg;
                        if (status.isStopped()
                                && !isFinishing()) {

                            if (!status.isOperationFromUser()) {
                                showToast(AppUtils.getString(R.string.player_complete));
                                finish();
                            }

                        }

                    }

                    @Override
                    public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                        super.onError(param, errorInfo);
                        showToast(errorInfo.getMessage());
                        onBackPressed();
                    }
                }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isVideoPaused) {
            HYClient.getHYPlayer().pausePlayEx(false, video_texture);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        HYClient.getHYPlayer().pausePlayEx(true, video_texture);
    }


    @OnClick(R.id.iv_play_status)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play_status:
                isVideoPaused = HYClient.getHYPlayer().togglePausePlayEx(video_texture)
                        .isPlayPausedEx(video_texture);
                if (isVideoPaused) {
                    iv_play_status.setImageResource(R.drawable.ic_play_start);
                } else {
                    iv_play_status.setImageResource(R.drawable.ic_play_pause);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        HYClient.getHYPlayer().stopPlayEx(null, video_texture);
        super.onBackPressed();
    }
}
