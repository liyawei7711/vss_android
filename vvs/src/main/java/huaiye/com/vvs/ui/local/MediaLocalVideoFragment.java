package huaiye.com.vvs.ui.local;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMsgStopUploadRecordReq;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.sdkabi._api.ApiIO;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.io.FileTupple;
import com.huaiye.sdk.sdkabi.abilities.io.callback.CallbackUploadVideo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.views.CheckableLinearLayout;
import huaiye.com.vvs.dao.MediaFileDao;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.tags.TagsAdapter;


/**
 * author: admin
 * date: 2017/09/05
 * version: 0
 * mail: secret
 * desc: MediaLocalVideoFragment
 */

public class MediaLocalVideoFragment extends MediaLocalBaseFragment {
    RecyclerView rcv_list;
    View ll_empty;
    View fl_progress;
    ProgressBar pb_progress;
    TextView tv_progress;

    TagsAdapter<MediaFileDao.MediaFile> adapter;

    float currentUploadedPercent = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv_list = view.findViewById(R.id.rcv_list);
        ll_empty = view.findViewById(R.id.ll_empty);
        fl_progress = view.findViewById(R.id.fl_progress);
        pb_progress = view.findViewById(R.id.pb_progress);
        tv_progress = view.findViewById(R.id.tv_progress);

        rcv_list.setLayoutManager(new LinearLayoutManager(getContext()));

        rcv_list.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
                return false;
            }
        });

        adapter = new TagsAdapter<MediaFileDao.MediaFile>(R.layout.item_local_video) {
            @Override
            public EXTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final EXTViewHolder holder = super.onCreateViewHolder(parent, viewType);
                CheckableLinearLayout root = (CheckableLinearLayout) holder.getItemView();
                root.setOnCheckedListener(new CheckableLinearLayout.OnCheckedChangedListener() {
                    @Override
                    public void onCheckedChanged(View parent, boolean isChecked) {
                        ImageView iv = (ImageView) parent.findViewById(R.id.iv_status);
                        if (isChecked) {
                            iv.setImageResource(R.drawable.ic_status_choosed_yes);
                        } else {
                            iv.setImageResource(R.drawable.ic_status_choosed_no);
                        }
                    }
                });
                return holder;
            }

            @Override
            public void onBindTagViewHolder(EXTViewHolder extViewHolder, int i, MediaFileDao.MediaFile data) {
                if (getMode() == Mode.MultiChoice) {
                    extViewHolder.setVisibility(R.id.iv_status, View.VISIBLE);
                } else {
                    extViewHolder.setVisibility(R.id.iv_status, View.GONE);
                }

                extViewHolder.setText(R.id.tv_video_name, data.getRecordNameUI());

            }
        };

        adapter.setOnItemClickListener(new EXTRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int i) {
                if (adapter.getMode() == TagsAdapter.Mode.MultiChoice) {
                    if (adapter.getSelectedPositions().size() > 0
                            && adapter.getSelectedPositions().size() == adapter.getItemCount()) {
                        parent.setChooseAll(true);
                    } else {
                        parent.setChooseAll(false);
                    }
                } else {
                    MediaFileDao.MediaFile module = adapter.getDataForItemPosition(i);
                    Intent intent = new Intent(getActivity(), MediaLocalVideoPlayActivity.class);
                    intent.putExtra("path", module.getRecordPath());
                    startActivity(intent);
                }
            }
        });

        List<MediaFileDao.MediaFile> datas = MediaFileDao.get().getAllVideos();
        if (datas == null
                || datas.isEmpty()) {
            return;
        }
        Collections.sort(datas, new Comparator<MediaFileDao.MediaFile>() {
            @Override
            public int compare(MediaFileDao.MediaFile o1, MediaFileDao.MediaFile o2) {
                if (o1.nRecordStartTimeMillions > o2.nRecordStartTimeMillions)
                    return -1;
                if (o1.nRecordStartTimeMillions < o2.nRecordStartTimeMillions)
                    return 1;
                if (o1.nRecordStartTimeMillions < o2.nRecordStartTimeMillions)
                    return 0;
                return 0;
            }
        });
        adapter.setChoiceMode(TagsAdapter.Mode.None);
        adapter.setDatas(datas);
        rcv_list.setAdapter(adapter);
        showEmty();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        showEmty();
    }

    @Override
    public void setModeEdit() {
        adapter.setChoiceMode(TagsAdapter.Mode.MultiChoice);
        adapter.notifyDataSetChanged();
        showEmty();
    }

    private void showEmty() {
        if (adapter.getDatasCount() <= 0) {
            ll_empty.setVisibility(View.VISIBLE);
        } else {
            ll_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void clearStates() {
        adapter.clearChooseStatus();
        adapter.setChoiceMode(TagsAdapter.Mode.None);
    }

    @Override
    public void clearChoosed() {
        adapter.clearChooseStatus();
    }

    @Override
    public void chooseAll() {
        adapter.chooseAll();
    }

    @Override
    public boolean isAllChoosed() {
        return adapter.getSelectedPositions().size() > 0
                && adapter.getSelectedPositions().size() == adapter.getItemCount();
    }

    @Override
    public void deleteChoosed() {
        List<Integer> positions = adapter.getSelectedPositions();
        List<MediaFileDao.MediaFile> copy = adapter.getDatasCopy();

        List<MediaFileDao.MediaFile> dels = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            int position = positions.get(i);
            MediaFileDao.MediaFile module = adapter.getDataForItemPosition(position);

            copy.remove(module);
            dels.add(module);
        }
        adapter.clearChooseStatus();
        adapter.setDatas(copy);
        adapter.notifyDataSetChanged();

        MediaFileDao.get().del(dels.toArray(new MediaFileDao.MediaFile[]{}));

        parent.setChooseAll(isAllChoosed());

        showEmty();
    }

    List<Integer> uploadingIndexs = null;
    List<Integer> uploadedIndexs = null;

    @Override
    public void uploadChoosed() {
        uploadingIndexs = adapter.getSelectedPositions();
        uploadedIndexs = new ArrayList<>();
        if (uploadingIndexs == null
                || uploadingIndexs.isEmpty()) {
            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.selected_need_upload_file));
            return;
        }

        uploadVideo();
    }

    long taskSession;

    void uploadVideo() {

        ArrayList<FileTupple> paths = new ArrayList<>();
        for (int tmp : uploadingIndexs) {
            MediaFileDao.MediaFile module = adapter.getDataForItemPosition(tmp);
            FileTupple fileTupple = new FileTupple();
            fileTupple.setActionTime(module.getDateDetail());
            fileTupple.setFilePath(module.getRecordPath());
            paths.add(fileTupple);
        }

        fl_progress.setVisibility(View.VISIBLE);
        HYClient.getModule(ApiIO.class).uploadVideo(SdkParamsCenter.IO.UploadVideo()
                        .setFiles(paths),
                new CallbackUploadVideo() {
                    @Override
                    public void onTaskSession(long sessionId) {
                        Log.e("Test", "onTaskSession >>> " + sessionId);
                        taskSession = sessionId;
                    }

                    @Override
                    public boolean onUploadingFileError(int index, String path, int nCode) {
                        Log.e("Test", "onUploadingFileError >>> " + index + " >>> " + path);
                        ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.upload_num_is) + index + AppUtils.getString(R.string.is_error));
                        uploadingIndexs.remove(0);
                        return true;
                    }

                    @Override
                    public void onProgressChanged(int index, String path, int current, int total) {
                        currentUploadedPercent = (float) current / total;
                        Log.e("Test", "onProgressChanged >>> " + index + " >>> " + currentUploadedPercent);
                        pb_progress.setProgress((int) currentUploadedPercent);
                        tv_progress.setText(AppUtils.getString(R.string.current_progress) + (int) currentUploadedPercent + "%");
                        adapter.notifyDataSetChanged();

                        if (current == total) {
                            if (uploadingIndexs.size() > 0) {
                                uploadedIndexs.add(uploadingIndexs.remove(0));
                                currentUploadedPercent = 0;
                            }
                        }

                        showEmty();

                    }

                    @Override
                    public void onSuccess(Boolean resp) {
                        Log.e("Test", "onSuccess >>> " + resp);
                        fl_progress.setVisibility(View.GONE);
                        ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.upload_success));

                        rcv_list.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                List<MediaFileDao.MediaFile> copy = adapter.getDatasCopy();

                                List<MediaFileDao.MediaFile> dels = new ArrayList<>();
                                for (int i = 0; i < uploadedIndexs.size(); i++) {
                                    int position = uploadedIndexs.get(i);
                                    MediaFileDao.MediaFile module = adapter.getDataForItemPosition(position);

                                    copy.remove(module);
                                    dels.add(module);
                                }

                                uploadingIndexs.clear();
                                uploadedIndexs.clear();

                                adapter.clearChooseStatus();
                                adapter.setDatas(copy);
                                adapter.notifyDataSetChanged();

                                MediaFileDao.get().del(dels.toArray(new MediaFileDao.MediaFile[]{}));

                                parent.setChooseAll(isAllChoosed());

                                showEmty();

                            }
                        }, 1200);
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        Log.e("Test", "onError >>> " + errorInfo.toString());
                        ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.upload_false));
                    }

                });

    }

    @Override
    public void cancelCurrentAction() {
        if (isUploading()) {
            MediaFileDao.MediaFile module = adapter.getDataForItemPosition(uploadingIndexs.get(0));
            SdpMsgStopUploadRecordReq req = new SdpMsgStopUploadRecordReq();
            req.m_fileName = module.getRecordPath();

            HYClient.getModule(ApiIO.class).cancelUploadVideo(taskSession);

            List<MediaFileDao.MediaFile> copy = adapter.getDatasCopy();

            ArrayList<MediaFileDao.MediaFile> dels = new ArrayList<>();
            for (int i = 0; i < uploadedIndexs.size(); i++) {
                int position = uploadedIndexs.get(i);
                module = adapter.getDataForItemPosition(position);

                copy.remove(module);
                dels.add(module);
            }
            adapter.clearChooseStatus();
            adapter.setDatas(copy);
            adapter.notifyDataSetChanged();

            MediaFileDao.get().del(dels.toArray(new MediaFileDao.MediaFile[]{}));

            parent.setChooseAll(isAllChoosed());
        } else {
            clearStates();
        }

        showEmty();
    }

    @Override
    public boolean isUploading() {
        return uploadingIndexs != null && uploadingIndexs.size() > 0;
    }

    MediaLocalParent parent;

    @Override
    public void setParentIntf(MediaLocalParent parentIntf) {
        parent = parentIntf;
        showEmty();
    }
}
