package huaiye.com.vvs.ui.meet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ErrorMsg;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.ui.meet.views.MeetCreateHeaderView;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;

import static android.view.View.GONE;

/**
 * author: admin
 * date: 2018/01/15
 * version: 0
 * mail: secret
 * desc: MeetCreateActivity
 */
@BindLayout(R.layout.activity_meet_detail)
public class MeetDetailActivity extends AppBaseActivity {

    @BindView(R.id.rct_view)
    RecyclerView rct_view;

    @BindView(R.id.tv_player)
    View tv_player;
    @BindView(R.id.tv_enter)
    View tv_enter;

    @BindExtra
    String strMeetDomainCode;
    @BindExtra
    int nMeetID;

    boolean isMaster;
    CGetMeetingInfoRsp info;

    MeetCreateHeaderView header;
    EXTRecyclerAdapter<CGetMeetingInfoRsp.UserInfo> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(AppUtils.getString(R.string.meet_diaodu_detail))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        mZeusLoadView.loadingText(AppUtils.getString(R.string.is_loading_file)).setLoading();
        rct_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EXTRecyclerAdapter<CGetMeetingInfoRsp.UserInfo>(R.layout.item_meetcreate_member) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, CGetMeetingInfoRsp.UserInfo contactData) {
                if (i < getHeaderViewsCount()) {
                    return;
                }

                if (contactData.strUserID.equals(AppDatas.Auth().getUserID() + "")) {
                    extViewHolder.setVisibility(R.id.iv_mainer, View.VISIBLE);
                } else {
                    extViewHolder.setVisibility(R.id.iv_mainer, GONE);
                }
                extViewHolder.setText(R.id.tv_user_name, contactData.strUserName);
                extViewHolder.setVisibility(R.id.tv_user_status, GONE);
            }
        };

        header = new MeetCreateHeaderView(this, true, false);
        adapter.addHeaderView(header);

        rct_view.setAdapter(adapter);

        requestInfo();
    }

    @OnClick({R.id.tv_enter, R.id.tv_player})
    public void clickView(View view) {
        switch (view.getId()) {
            case R.id.tv_enter:
                entMeeting();
                break;
            case R.id.tv_player:
                startPlayer();
                break;
        }
    }

    /**
     * 播放录像
     */
    private void startPlayer() {
        if (info.nRecordID == 0) {
            showToast(AppUtils.getString(R.string.meet_no_record));
            return;
        }
        Intent intent = new Intent(this, MeetPlaybackActivity.class);
        intent.putExtra("nMeetID", nMeetID);
        intent.putExtra("strMeetDomainCode", strMeetDomainCode);
        startActivity(intent);
    }

    /**
     * 进入会议
     */
    private void entMeeting() {
        EventBus.getDefault().post(info);
    }

    private void requestInfo() {
        HYClient.getModule(ApiMeet.class)
                .requestMeetDetail(SdkParamsCenter.Meet.RequestMeetDetail()
                                .setMeetID(nMeetID)
                                .setMeetDomainCode(strMeetDomainCode)
                                .setnListMode(1),
                        new SdkCallback<CGetMeetingInfoRsp>() {
                            @Override
                            public void onSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                                mZeusLoadView.dismiss();

                                isMaster = cGetMeetingInfoRsp.strMainUserID.equals(AppDatas.Auth().getUserID() + "");
                                info = cGetMeetingInfoRsp;

                                if (cGetMeetingInfoRsp.nStatus == 1) {
                                    tv_enter.setVisibility(View.VISIBLE);
                                } else if (cGetMeetingInfoRsp.nStatus == 2) {
                                    tv_player.setVisibility(View.VISIBLE);
                                }

                                header.setMaster(false);
                                header.showInfo(cGetMeetingInfoRsp);
                                adapter.setDatas(cGetMeetingInfoRsp.listUser);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                mZeusLoadView.dismiss();

                                showToast(ErrorMsg.getMsg(errorInfo.getCode()));
                                header.setMaster(false);
                            }
                        });
    }


}
