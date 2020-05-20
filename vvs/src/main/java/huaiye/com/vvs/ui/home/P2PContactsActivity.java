package huaiye.com.vvs.ui.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huaiye.sdk.HYClient;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.CreateTalkAndVideo;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.recycle.SafeLinearLayoutManager;
import huaiye.com.vvs.common.rx.RxUtils;
import huaiye.com.vvs.map.baidu.appcluster.MyCluster;
import huaiye.com.vvs.models.p2p.SdpMsgFindLanCaptureDeviceRspWrap;
import huaiye.com.vvs.ui.device.holder.P2PPersonHolder;
import huaiye.com.vvs.ui.home.view.ContactMenuLayout;

/**
 * author: admin
 * date: 2018/05/11
 * version: 0
 * mail: secret
 * desc: ContactsActivity
 */
@BindLayout(R.layout.activity_contacts_p2p)
public class P2PContactsActivity extends AppBaseActivity {

    @BindView(R.id.iv_back)
    View iv_back;
    @BindView(R.id.rct_view)
    RecyclerView rct_view;

    @BindView(R.id.iv_empty_view)
    View iv_empty_view;
    @BindView(R.id.contact_menu)
    ContactMenuLayout contact_menu;
    @BindExtra
    ArrayList<SdpMsgFindLanCaptureDeviceRspWrap> devices;

    LiteBaseAdapter<SdpMsgFindLanCaptureDeviceRspWrap> adapter;
    SdpMsgFindLanCaptureDeviceRspWrap bean;

    RxUtils rxUtils;

    @Override
    protected void initActionBar() {
        rxUtils = new RxUtils();

        getNavigate().setVisibility(View.GONE);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rct_view.setLayoutManager(new SafeLinearLayoutManager(this));
        adapter = new LiteBaseAdapter<>(this,
                devices,
                P2PPersonHolder.class,
                R.layout.item_person_holder,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bean = (SdpMsgFindLanCaptureDeviceRspWrap) v.getTag();
                        contact_menu.P2PUserController();
                    }
                }, "");
        rct_view.setAdapter(adapter);
        showEmptyView();
    }

    @Override
    public void doInitDelay() {
        contact_menu.setListener(new ContactMenuLayout.OnMentClickListener() {
            @Override
            public void onPhoneClick() {
            }

            @Override
            public void onVideoClick() {

                if (HYClient.getSdkSamples().P2P().isCapturing() ||
                        HYClient.getSdkSamples().P2P().isTalking() ||
                        HYClient.getSdkSamples().P2P().isWatching() ||
                        HYClient.getSdkSamples().P2P().isBeingWatched()) {
                    getLogicDialog()
                            .setTitleText(AppUtils.getString(R.string.notice))
                            .setMessageText(AppUtils.getString(R.string.other_diaodu))
                            .setConfirmClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    rxUtils.doDelayOn(200, new RxUtils.IMainDelay() {
                                        @Override
                                        public void onMainDelay() {
                                            HYClient.getSdkSamples().P2P().stopAll();
                                            finish();
                                            rxUtils.doDelayOn(1000, new RxUtils.IMainDelay() {
                                                @Override
                                                public void onMainDelay() {
                                                    videoClick(bean);
                                                }
                                            });
                                        }
                                    });

                                }
                            }).show();
                    return;
                }

                finish();
                rxUtils.doDelayOn(1000, new RxUtils.IMainDelay() {
                    @Override
                    public void onMainDelay() {
                        videoClick(bean);
                    }
                });
            }

            @Override
            public void onMeetClick() {
            }

            @Override
            public void onWatchClick() {
                if (HYClient.getSdkSamples().P2P().isCapturing() ||
                        HYClient.getSdkSamples().P2P().isTalking() ||
                        HYClient.getSdkSamples().P2P().isWatching() ||
                        HYClient.getSdkSamples().P2P().isBeingWatched()) {
                    AppUtils.showMsg(false, false);
                    return;
                }
                if (bean != null) {
                    EventBus.getDefault().post(bean.convert());
                }
                finish();
            }

            @Override
            public void onZhiHuiClick() {
            }

            @Override
            public void onChannelClick() {
            }

        });
    }

    /**
     * 视频
     */
    public void videoClick(SdpMsgFindLanCaptureDeviceRspWrap cluster) {
        EventBus.getDefault().post(new CreateTalkAndVideo(true,
                "",
                "",
                "",
                cluster.convert(),
                "P2P 208"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MyCluster bean) {
        if (bean.bean.p2pDeviceBean != null) {
            SdpMsgFindLanCaptureDeviceRspWrap hasBean = null;
            for (SdpMsgFindLanCaptureDeviceRspWrap temp : devices) {
                if (bean.bean.p2pDeviceBean.m_strIP.equals(temp.m_strIP)) {
                    hasBean = temp;
                    break;
                }
            }
            if (hasBean != null) {
                hasBean.m_nCaptureState = bean.bean.p2pDeviceBean.m_nCaptureState;
                hasBean.m_strIP = bean.bean.p2pDeviceBean.m_strIP;
                hasBean.m_strInfo = bean.bean.p2pDeviceBean.m_strInfo;
                hasBean.m_strName = bean.bean.p2pDeviceBean.m_strName;
            } else {
                devices.add(new SdpMsgFindLanCaptureDeviceRspWrap(bean.bean.p2pDeviceBean));
            }
            adapter.notifyDataSetChanged();
            showEmptyView();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String m_strIP) {
        SdpMsgFindLanCaptureDeviceRspWrap hasBean = null;
        for (SdpMsgFindLanCaptureDeviceRspWrap temp : devices) {
            if (m_strIP.equals(temp.m_strIP)) {
                hasBean = temp;
                break;
            }
        }
        if (hasBean != null) {
            devices.remove(hasBean);
        }
        adapter.notifyDataSetChanged();

        showEmptyView();
    }

    private void showEmptyView() {
        if (devices.isEmpty()) {
            iv_empty_view.setVisibility(View.VISIBLE);
        } else {
            iv_empty_view.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rxUtils != null) {
            rxUtils.clearAll();
            rxUtils = null;
        }
    }
}
