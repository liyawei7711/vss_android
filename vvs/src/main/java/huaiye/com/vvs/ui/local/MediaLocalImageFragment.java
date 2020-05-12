package huaiye.com.vvs.ui.local;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.views.MediaLocalImagesLineView;
import huaiye.com.vvs.dao.MediaFileDao;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.idcs.FloatingTitleDecoration;


/**
 * author: admin
 * date: 2017/09/05
 * version: 0
 * mail: secret
 * desc: MediaLocalPictureFragment
 */
public class MediaLocalImageFragment extends MediaLocalBaseFragment {
    RecyclerView rcv_list;

    EXTRecyclerAdapter<String> adapter;

    FloatingTitleDecoration titleDecoration;

    List<MediaFileDao.MediaFile> choosedModules = new LinkedList<>();
    TreeMap<String, List<MediaFileDao.MediaFile>> dictModules = new TreeMap<>();
    MediaLocalImagesLineView.CheckStateCallback mCallback = new MediaLocalImagesLineView.CheckStateCallback() {
        @Override
        public void onCheckedChanged(MediaFileDao.MediaFile module, boolean isChecked) {
            if (isChecked) {
                choosedModules.add(module);
            } else {
                choosedModules.remove(module);
            }

            parent.setChooseAll(isAllChoosed());
        }
    };
    boolean isInCheckMode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv_list = (RecyclerView) view.findViewById(R.id.rcv_list);
        rcv_list.setLayoutManager(new LinearLayoutManager(getContext()));

        titleDecoration = new FloatingTitleDecoration();
        titleDecoration.setTitleBackgroundColor(Color.parseColor("#efefef"));
        titleDecoration.setNormalDividerBackgroundColor(Color.parseColor("#efefef"));
        titleDecoration.setTitleHeight(AppUtils.getSize(50));
        titleDecoration.setTitleTextSize(AppUtils.getSize(15));
        titleDecoration.setTitleTextLeftOffset(AppUtils.getSize(13));
        titleDecoration.setCallback(new FloatingTitleDecoration.TitleCallback() {
            @Override
            public boolean isPositionTitle(int i) {
                return true;
            }

            @Override
            public String titleForPosition(int i) {
                return adapter.getDataForItemPosition(i);
            }
        });
        rcv_list.addItemDecoration(titleDecoration);
        adapter = new EXTRecyclerAdapter<String>(R.layout.item_local_image) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int position, String key) {
                LinearLayout line_extends = extViewHolder.findViewById(R.id.line_extends);
                line_extends.removeAllViews();

                MediaLocalImagesLineView line_0 = extViewHolder.findViewById(R.id.image_lines_0);
                MediaLocalImagesLineView line_1 = extViewHolder.findViewById(R.id.image_lines_1);
                MediaLocalImagesLineView line_2 = extViewHolder.findViewById(R.id.image_lines_2);

                line_0.setVisibility(View.GONE);
                line_1.setVisibility(View.GONE);
                line_2.setVisibility(View.GONE);

                List<MediaFileDao.MediaFile> modules = dictModules.get(key);
                for (int i = 0; i < modules.size(); i += 4) {
                    MediaLocalImagesLineView line = null;
                    if (i == 0) {
                        line = line_0;
                    } else if (i == 4) {
                        line = line_1;
                    } else if (i == 8) {
                        line = line_2;
                    } else {
                        line = new MediaLocalImagesLineView(getActivity());
                        line_extends.addView(line);
                    }
                    line.setOpenCheck(isInCheckMode);
                    line.setCallback(mCallback);
                    line.setVisibility(View.VISIBLE);
                    int len = modules.size() - i > 4 ? 4 : modules.size() - i;
                    MediaFileDao.MediaFile[] datas = new MediaFileDao.MediaFile[len];
                    for (int l = 0; l < datas.length; l++) {
                        datas[l] = modules.get(i + l);
                    }
                    line.setLocalImages(choosedModules, datas);
                }
            }
        };

        List<MediaFileDao.MediaFile> datas = MediaFileDao.get().getAllImgs();
        if (datas == null
                || datas.isEmpty()) {
            return;
        }
        dictModules = filter(datas);
        adapter.setDatas(new ArrayList<String>(dictModules.keySet()));
        rcv_list.setAdapter(adapter);
    }

    @Override
    public void setModeEdit() {
        isInCheckMode = true;
        choosedModules.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void clearStates() {
        choosedModules.clear();
        isInCheckMode = false;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearChoosed() {
        choosedModules.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void chooseAll() {
        for (List<MediaFileDao.MediaFile> tmp : dictModules.values()) {
            choosedModules.addAll(tmp);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean isAllChoosed() {
        int totalSize = 0;
        for (List<MediaFileDao.MediaFile> tmp : dictModules.values()) {
            totalSize += tmp.size();
        }
        return choosedModules.size() == totalSize;
    }

    @Override
    public void deleteChoosed() {
        for (MediaFileDao.MediaFile module : choosedModules) {
            MediaFileDao.get().del(module);
            dictModules.get(module.getDateSimple()).remove(module);
            if (dictModules.get(module.getDateSimple()).isEmpty()) {
                dictModules.remove(module.getDateSimple());
            }
        }
        choosedModules.clear();
        adapter.setDatas(new ArrayList<String>(dictModules.keySet()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void uploadChoosed() {
        Toast.makeText(getContext(), AppUtils.getString(R.string.not_support_upload_img), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelCurrentAction() {

    }

    @Override
    public boolean isUploading() {
        return false;
    }

    MediaLocalParent parent;

    @Override
    public void setParentIntf(MediaLocalParent parentIntf) {
        parent = parentIntf;
    }

    TreeMap<String, List<MediaFileDao.MediaFile>> filter(List<MediaFileDao.MediaFile> modules) {
        TreeMap<String, List<MediaFileDao.MediaFile>> dict = new TreeMap<>();

        String lastDate = null;
        for (int i = 0; i < modules.size(); i++) {
            String currDate = modules.get(i).getDateSimple();
            if (!currDate.equals(lastDate)) {

                lastDate = currDate;
                dict.put(lastDate, new LinkedList<MediaFileDao.MediaFile>());
            }

            dict.get(lastDate).add(modules.get(i));

        }

        return dict;
    }
}
