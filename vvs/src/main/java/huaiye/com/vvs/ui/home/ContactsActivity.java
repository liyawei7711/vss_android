package huaiye.com.vvs.ui.home;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.CloseMeetActivity;
import huaiye.com.vvs.bus.CloseTalkVideoActivity;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.rx.RxUtils;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.ChangeUserBean;
import huaiye.com.vvs.ui.device.holder.PersonHolder;
import huaiye.com.vvs.ui.home.present.ContactsPresent;
import huaiye.com.vvs.ui.home.view.ContactMenuLayout;
import huaiye.com.vvs.ui.home.view.IContactsView;

/**
 * author: admin
 * date: 2018/05/11
 * version: 0
 * mail: secret
 * desc: ContactsActivity
 */
@BindLayout(R.layout.activity_contacts)
public class ContactsActivity extends AppBaseActivity implements IContactsView {

    @BindView(R.id.iv_back)
    View iv_back;
    @BindView(R.id.iv_right)
    View iv_right;
    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;
    @BindView(R.id.rct_view)
    RecyclerView rct_view;

    @BindView(R.id.view_float)
    View view_float;
    @BindView(R.id.rbtn_person)
    TextView rbtn_person;
    @BindView(R.id.rbtn_group)
    TextView rbtn_group;
    @BindView(R.id.rbtn_device)
    TextView rbtn_device;
    @BindView(R.id.iv_empty_view)
    View iv_empty_view;
//    @BindView(R.id.iv_right_choose)
//    public View iv_right_choose;
    @BindView(R.id.contact_menu)
    ContactMenuLayout contact_menu;

    int id;
    RelativeLayout.LayoutParams lp;

    ContactsPresent present;
    RxUtils rxUtils;

    @Override
    protected void initActionBar() {
        rxUtils = new RxUtils();

        PersonHolder.selected_mode = false;
        present = new ContactsPresent(this);

        getNavigate().setVisibility(View.GONE);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.showPopu(v);
            }
        });

        rct_view.setLayoutManager(new LinearLayoutManager(this));

        rct_view.setAdapter(present.getPersonAdapter());
        present.loadPerson(true);
        present.loadGroup(true);
        com.huaiye.sdk.logger.Logger.debug("loadDevice from initActionBar" );
        present.loadDevice(true);

        lp = (RelativeLayout.LayoutParams) view_float.getLayoutParams();
        id = R.id.rbtn_person;
        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (id) {
                    case R.id.rbtn_person:
                        present.loadPerson(true);
                        break;
                    case R.id.rbtn_group:
                        present.loadGroup(true);
                        break;
                    case R.id.rbtn_device:
                        com.huaiye.sdk.logger.Logger.debug("loadDevice from initActionBar refresh" );
                        present.loadDevice(true);
                        break;
                }
            }
        });
    }

    @OnClick({R.id.rbtn_person, R.id.rbtn_group, R.id.rbtn_device})
    public void onClick(View view) {
        present.changeMenu();
        id = view.getId();

        lp.addRule(RelativeLayout.ALIGN_RIGHT, id);
        lp.addRule(RelativeLayout.ALIGN_LEFT, id);
        view_float.setLayoutParams(lp);

        rbtn_person.setTextColor(Color.parseColor("#333333"));
        rbtn_group.setTextColor(Color.parseColor("#333333"));
        rbtn_device.setTextColor(Color.parseColor("#333333"));
        switch (view.getId()) {
            case R.id.rbtn_person:
//                iv_right_choose.setVisibility(View.VISIBLE);
                rbtn_person.setTextColor(Color.parseColor("#539bf0"));
                rct_view.setAdapter(present.getPersonAdapter());
                present.changeViewShow(present.getPersonList().isEmpty());
                break;
            case R.id.rbtn_group:
//                iv_right_choose.setVisibility(View.GONE);
                rbtn_group.setTextColor(Color.parseColor("#539bf0"));
                rct_view.setAdapter(present.getGroupAdapter());
                present.changeViewShow(present.getGroupList().isEmpty());
                break;
            case R.id.rbtn_device:
//                iv_right_choose.setVisibility(View.GONE);
                rbtn_device.setTextColor(Color.parseColor("#539bf0"));
                contact_menu.setVisibility(View.GONE);
                rct_view.setAdapter(present.getDeviceAdapter());
                present.changeViewShow(present.getDeviceList().isEmpty());
                break;
        }
    }

    @Override
    public void doInitDelay() {
        contact_menu.setListener(new ContactMenuLayout.OnMentClickListener() {
            @Override
            public void onPhoneClick() {

                if (AppUtils.isMeet || AppUtils.isTalk || AppUtils.isVideo) {
                    getLogicDialog()
                            .setTitleText(AppUtils.getString(R.string.notice))
                            .setMessageText(AppUtils.getString(R.string.other_diaodu))
                            .setConfirmClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (AppUtils.isMeet) {
                                        EventBus.getDefault().post(new CloseMeetActivity("ContactsActivity 175 "));
                                    } else if (AppUtils.isTalk) {
                                        ((MainActivity) getContext()).waitAcceptLayout.closeWaitViewAll();
                                    } else if (AppUtils.isVideo) {
                                        EventBus.getDefault().post(new CloseTalkVideoActivity("ContactsActivity 180 "));
                                    }
                                    rxUtils.doDelayOn(200, new RxUtils.IMainDelay() {
                                        @Override
                                        public void onMainDelay() {
                                            present.phoneClick();
                                            finish();
                                        }
                                    });

                                }
                            }).show();
                    return;
                }
                present.phoneClick();
                finish();
            }

            @Override
            public void onVideoClick() {

                if (AppUtils.isMeet || AppUtils.isTalk || AppUtils.isVideo) {
                    getLogicDialog()
                            .setTitleText(AppUtils.getString(R.string.notice))
                            .setMessageText(AppUtils.getString(R.string.other_diaodu))
                            .setConfirmClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (AppUtils.isMeet) {
                                        EventBus.getDefault().post(new CloseMeetActivity("ContactsActivity 209 "));
                                    } else if (AppUtils.isTalk) {
                                        ((MainActivity) getContext()).waitAcceptLayout.closeWaitViewAll();
                                    } else if (AppUtils.isVideo) {
                                        EventBus.getDefault().post(new CloseTalkVideoActivity("ContactsActivity 213 "));
                                    }
                                    rxUtils.doDelayOn(200, new RxUtils.IMainDelay() {
                                        @Override
                                        public void onMainDelay() {
                                            finish();
                                            //videoClick会调用EventBus发送CreateTalkAndVideo事件
                                            //CreateTalkAndVideo事件会让MainActivity操作Fragment
                                            //Activity在暂停的时候不能操作Fragment
                                            //所以需要等1秒钟,MainActivity启动后再推消息
                                            RxUtils rxUtils = new RxUtils();
                                            rxUtils.doDelayOn(1000, new RxUtils.IMainDelay() {
                                                @Override
                                                public void onMainDelay() {
                                                    present.videoClick();
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
                        present.videoClick();
                    }
                });
            }

            @Override
            public void onMeetClick() {
                if (AppUtils.isMeet || AppUtils.isTalk || AppUtils.isVideo) {
                    getLogicDialog()
                            .setTitleText(AppUtils.getString(R.string.notice))
                            .setMessageText(AppUtils.getString(R.string.other_diaodu))
                            .setConfirmClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (AppUtils.isMeet) {
                                        EventBus.getDefault().post(new CloseMeetActivity("ContactsActivity 259 "));
                                    } else if (AppUtils.isTalk) {
                                        ((MainActivity) getContext()).waitAcceptLayout.closeWaitViewAll();
                                    } else if (AppUtils.isVideo) {
                                        EventBus.getDefault().post(new CloseTalkVideoActivity("ContactsActivity 263 "));
                                    }
                                    rxUtils.doDelayOn(200, new RxUtils.IMainDelay() {
                                        @Override
                                        public void onMainDelay() {
                                            present.meetClick();
                                        }
                                    });

                                }
                            }).show();
                    return;
                }
                present.meetClick();
            }

            @Override
            public void onWatchClick() {
                if (AppUtils.isTalk || AppUtils.isMeet || AppUtils.isVideo) {
                    AppUtils.showMsg(false, false);
                    return;
                }
                present.watchClick();
            }

            @Override
            public void onZhiHuiClick() {
                present.zhiHuiClick();
            }

            @Override
            public void onChannelClick() {
                present.channelClick();
            }

        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangeUserBean bean) {
        if (bean.strModifyUserID.equals(AppDatas.Auth().getUserID() + "")) {
            AppDatas.Auth().put("nPriority", bean.nPriority);
            AppDatas.Auth().put("strUserName", bean.strModifyUserName);
        }
        if (present != null) {
            present.refBean(bean);
        }
    }

    @Override
    public AppBaseActivity getContext() {
        return this;
    }

    @Override
    public SwipeRefreshLayout getRefView() {
        return refresh_view;
    }

    @Override
    public View getEmptyView() {
        return iv_empty_view;
    }

    @Override
    public View getListView() {
        return rct_view;
    }

    @Override
    public void personSingle(int pro) {
        if (pro > AppDatas.Auth().getPriority()) {
            contact_menu.SingleUserCanWatch();
        } else {
            contact_menu.SingleUserNotWatch();
        }
    }

    @Override
    public void personMulite() {
        contact_menu.MuliteUser();
    }

    @Override
    public void personNull() {
        contact_menu.allNull();
    }

    @Override
    public void groupSingle() {
        contact_menu.SingleGroup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rxUtils != null) {
            rxUtils.clearAll();
            rxUtils = null;
        }
        if (present != null){
            present.destory();
        }
    }
}
