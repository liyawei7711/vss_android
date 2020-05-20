package huaiye.com.vvs.ui.home.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huaiye.sdk.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.dialog.ConfirmDialogFragment;
import huaiye.com.vvs.common.recycle.SafeLinearLayoutManager;
import huaiye.com.vvs.dao.msgs.MapMarkBean;
import huaiye.com.vvs.models.CommonResult;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.map.MapApi;
import huaiye.com.vvs.models.map.bean.MarkBean;
import huaiye.com.vvs.models.map.bean.MarkModelBean;
import huaiye.com.vvs.ui.home.MyMapMarkAdapter;
import huaiye.com.vvs.ui.home.TopRightMenuView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

public class MyMapMarkDialog extends BottomSheetDialogFragment {
    MyMapMarkAdapter adapter;
    ArrayList<MarkModelBean> markModelBeans = new ArrayList<>();
    RecyclerView rv;
    TopRightMenuView.TopRightClickListener listener;
    ArrayList<Integer> showingCustomMarkIds;
    TextView tvEmpty;

    TextView tvDelete;
    TextView tvSelectAll;
    TextView tvEdit;
    TextView tvCancel;
    OnDeleteListener onDeleteListener;
    boolean inEditMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_my_map_mark, container, false);
        inEditMode = false;
        adapter = new MyMapMarkAdapter(R.layout.item_mark_show, markModelBeans);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new SafeLinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        rv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                MarkModelBean bean = (MarkModelBean) adapter.getData().get(position);
                if (inEditMode){
                    bean.isEditChoose = !bean.isEditChoose;
                }else {
                    bean.isChoose = !bean.isChoose;
                    if (listener != null) {
                        listener.clickMark(bean);
                    }
                }
                tvSelectAll.setSelected(isAllSelected());
                adapter.notifyDataSetChanged();
            }
        });
        tvEmpty = view.findViewById(R.id.tv_empty);
        tvDelete = view.findViewById(R.id.tv_delete);
        tvSelectAll = view.findViewById(R.id.tv_select_all);
        tvEdit = view.findViewById(R.id.tv_edit);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AppBaseActivity appBaseActivity = (AppBaseActivity) getActivity();
                final ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.getInstance(AppUtils.getString(R.string.delete_all_marker));
                dialogFragment.show(appBaseActivity.getSupportFragmentManager(), "confirmDialog");
                dialogFragment.setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doDelete();
                        dialogFragment.dismiss();
                    }
                });
            }
        });
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectAll.setVisibility(View.VISIBLE);
                tvDelete.setVisibility(View.VISIBLE);
                tvCancel.setVisibility(View.VISIBLE);
                tvEdit.setVisibility(View.GONE);
                inEditMode = true;
                adapter.setInEditMode(inEditMode);
                adapter.notifyDataSetChanged();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectAll.setVisibility(View.GONE);
                tvDelete.setVisibility(View.GONE);
                tvCancel.setVisibility(View.GONE);
                tvEdit.setVisibility(View.VISIBLE);
                inEditMode = false;
                adapter.setInEditMode(inEditMode);
                adapter.notifyDataSetChanged();
            }
        });

        tvSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllSelect = isAllSelected();

                for (int i = 0; i < markModelBeans.size(); i++) {
                    //当前是全选,就全不选,否则就全选
                    if (isAllSelect) {
                        markModelBeans.get(i).isEditChoose = false;
                    } else {
                        markModelBeans.get(i).isEditChoose = true;
                    }
                }

                if (isAllSelect){
                    tvSelectAll.setSelected(false);
                }else {
                    tvSelectAll.setSelected(true);
                }
                adapter.notifyDataSetChanged();
            }
        });


        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        WindowManager windowManager = getActivity().getWindowManager();

        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);
        //关键
        lp.width = (int) (size.x * 0.95);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setAttributes(lp);

        getMarkList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new CustomHeightBottomSheetDialog(getContext(), R.style.translucentDialog, AppUtils.getSize(200), 0);
    }


    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }


    public boolean isAllSelected(){
        boolean isAllSelect = true;
        for (int i = 0; i < markModelBeans.size(); i++) {
            if (!markModelBeans.get(i).isEditChoose) {
                isAllSelect = false;
                break;
            }
        }
        return isAllSelect;
    }


    public void getMarkList() {

        MapApi.get().getCover(new ModelCallback<MarkBean>() {
            @Override
            public void onSuccess(MarkBean markBean) {
                for (MarkModelBean temp : markBean.markInfoList) {
                    if (showingCustomMarkIds.contains(temp.nMarkID)) {
                        temp.isChoose = true;
                    } else {
                        temp.isChoose = false;
                    }
                }
                markModelBeans.clear();
                markModelBeans.addAll(markBean.markInfoList);
                adapter.notifyDataSetChanged();
                ensureView();
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                Logger.log("MainPresent   loadDevice   失败");
            }
        });
    }

    public void setListener(TopRightMenuView.TopRightClickListener listener) {
        this.listener = listener;
    }

    public void setShowingCustomMarkIds(ArrayList<Integer> showingCustomMarkIds) {
        this.showingCustomMarkIds = showingCustomMarkIds;
    }

    public interface OnDeleteListener {
        void onDeleteSuccess();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MapMarkBean bean) {
        if (bean.nMsgType == 1) {
            MarkModelBean modelBean = new MarkModelBean();
            modelBean.nMarkID = bean.nMarkID;
            modelBean.strMarkName = bean.strMarkName;
            modelBean.strRemark = bean.strRemark;
            modelBean.strLastModTime = bean.strLastModTime;
            modelBean.strDomainCode = bean.strDomainCode;
            modelBean.isChoose = true;
            markModelBeans.add(modelBean);
        } else {
            int findDeletePos = -1;
            for (int i = 0; i < markModelBeans.size(); i++) {
                if (markModelBeans.get(i).nMarkID == bean.nMarkID) {
                    findDeletePos = i;
                    break;
                }
            }
            if (findDeletePos != -1) {
                markModelBeans.remove(findDeletePos);
            }
        }
        ensureView();
        adapter.notifyDataSetChanged();
    }

    private void ensureView() {
        if (markModelBeans.size() == 0) {
            rv.setVisibility(View.GONE);
            tvDelete.setVisibility(View.GONE);
            tvSelectAll.setVisibility(View.GONE);
            tvCancel.setVisibility(View.GONE);
            tvEdit.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            if (inEditMode){
                tvSelectAll.setVisibility(View.VISIBLE);
                tvCancel.setVisibility(View.VISIBLE);
                tvEdit.setVisibility(View.GONE);
                tvDelete.setVisibility(View.VISIBLE);
                if (isAllSelected()){
                    tvSelectAll.setSelected(true);
                }else {
                    tvSelectAll.setSelected(false);
                }
            }else {
                tvSelectAll.setVisibility(View.GONE);
                tvCancel.setVisibility(View.GONE);
                tvEdit.setVisibility(View.VISIBLE);
                tvDelete.setVisibility(View.GONE);
            }
            rv.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);

        }
    }

    private void doDelete() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (MarkModelBean oneMark : markModelBeans) {
            if (oneMark.isEditChoose){
                ids.add(oneMark.nMarkID);
            }
        }
        if (ids.size() == 0) {
            return;
        }
        MapApi.get().deleteMark(new Observer<CommonResult>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(CommonResult commonResult) {
                if (onDeleteListener != null) {
                    onDeleteListener.onDeleteSuccess();
                }
                ArrayList<MarkModelBean> nowList = new ArrayList<>();
                for (MarkModelBean oneMark : markModelBeans){
                    if (!oneMark.isEditChoose){
                        nowList.add(oneMark);
                    }
                }
                markModelBeans.clear();
                if (nowList.size() > 0){
                    markModelBeans.addAll(nowList);
                }else {
                    ensureView();
                }
                adapter.notifyDataSetChanged();
                AppUtils.showToast(AppUtils.getString(R.string.my_map_delete_success));
            }

            @Override
            public void onError(Throwable e) {
                Logger.debug("deleteMark error " + e.getMessage());
                AppUtils.showToast(AppUtils.getString(R.string.my_map_delete_error));
            }

            @Override
            public void onComplete() {

            }
        }, ids);
    }
}
