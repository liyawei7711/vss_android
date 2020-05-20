package huaiye.com.vvs.ui.home.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCaller;
import com.huaiye.sdk.core.SdkNotifyCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdpmsgs.social.CNotifyUserStatus;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.CreateTalkAndVideo;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.recycle.SafeLinearLayoutManager;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.ui.home.holder.SosHolder;

public class SosDialog extends BottomSheetDialogFragment {
    ArrayList<PersonModelBean> listBeans = new ArrayList<>();
    LiteBaseAdapter<PersonModelBean> adapter;
    RecyclerView rv;
    SdkCaller userCaller;
    final static String PARAMS_PERSON = "person";

    public static SosDialog getInstance(ArrayList<PersonModelBean> personModelBeans) {
        SosDialog dialog = new SosDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAMS_PERSON, personModelBeans);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sos, container, false);
        adapter = new LiteBaseAdapter<>(getContext(),
                listBeans,
                SosHolder.class,
                R.layout.item_sos,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PersonModelBean bean = (PersonModelBean) v.getTag();
                        toCall(bean);
                    }
                }, null);

        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new SafeLinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
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
        getList();
        startUserObserver();
    }


    @Override
    public void onStop() {
        super.onStop();
        stopUserObserver();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), R.style.translucentDialog);
    }


    public void getList() {
        listBeans.clear();
        ArrayList<PersonModelBean> personModelBeans = (ArrayList<PersonModelBean>) getArguments().getSerializable(PARAMS_PERSON);
        if (personModelBeans == null) {
            MCApp.getInstance().getTopActivity().showToast("没有发现调度台用户");
            dismissAllowingStateLoss();
            return;
        }
        for (int i = 0; i < personModelBeans.size(); i++) {
            PersonModelBean personModelBean = personModelBeans.get(i);
            if (personModelBean.nDevType == AppUtils.DevType_ANDROID ||
                    personModelBean.nDevType == AppUtils.DevType_IOS) {
                continue;
            }
            if (personModelBean.nStatus == PersonModelBean.STATUS_OFFLINE) {
                //离线
                continue;
            }
            listBeans.add(personModelBean);
        }
        adapter.notifyDataSetChanged();
        if (listBeans.size() == 0){
            MCApp.getInstance().getTopActivity().showToast("没有发现调度台用户");
            dismissAllowingStateLoss();
            return;
        }
        if (listBeans.size() == 1){
            toCall(listBeans.get(0));
            return;
        }
    }

    private  void toCall(PersonModelBean bean){
        CreateTalkAndVideo createTalkAndVideo = new CreateTalkAndVideo(true, AppDatas.Auth().getDomainCode(), bean.strUserID, bean.strUserName, null, "sos");
        EventBus.getDefault().post(createTalkAndVideo);
        dismissAllowingStateLoss();
    }

    private void startUserObserver() {
        userCaller = HYClient.getModule(ApiSocial.class).observeUserStatus(new SdkNotifyCallback<CNotifyUserStatus>() {
            @Override
            public void onEvent(final CNotifyUserStatus data) {
                if (HYClient.getSdkOptions().P2P().isP2PRunning()) {
                    Logger.log("SOS dialog startUserObserver 当前是p2p模式");
                    return;
                }
                PersonModelBean findPerson = null;
                for (PersonModelBean temp : listBeans) {
                    if (temp.strUserID.equals(data.strUserID)) {
                        findPerson = temp;
                        break;
                    }
                }


                if (findPerson != null) {
                    //找到了,如果在线就更新,如果离线就删除
                   if (data.isOnline()){
                       if (data.isCapturing() || data.isTalking() || data.isMeeting()) {
                           findPerson.nStatus = PersonModelBean.STATUS_ONLINE_CAPTURING;
                       } else {
                           findPerson.nStatus = PersonModelBean.STATUS_ONLINE_IDLE;
                       }
                   }else {
                       listBeans.remove(findPerson);
                   }
                    adapter.notifyDataSetChanged();
                }else {
                    //没有找到对应的person
                    //如果上线就添加
                    //如果下线就不管
                    if (data.isOnline()){
                        PersonModelBean newPerson = new PersonModelBean();
                        newPerson.strDomainCode = data.strUserDomainCode;
                        newPerson.strUserID     = data.strUserID;
                        newPerson.strUserName   = data.strUserName;
                        if (data.isCapturing() || data.isTalking() || data.isMeeting()) {
                            newPerson.nStatus = PersonModelBean.STATUS_ONLINE_CAPTURING;
                        } else {
                            newPerson.nStatus = PersonModelBean.STATUS_ONLINE_IDLE;
                        }
                        listBeans.add(newPerson);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }


    private void stopUserObserver() {
        if (userCaller != null) {
            userCaller.cancel();
        }
    }

}
