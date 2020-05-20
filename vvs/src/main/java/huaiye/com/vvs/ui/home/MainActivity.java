package huaiye.com.vvs.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmInitRsp;
import com.huaiye.cmf.sdp.SdpMsgCommonUDPMsg;
import com.huaiye.cmf.sdp.SdpMsgFindLanCaptureDeviceRsp;
import com.huaiye.cmf.sdp.SdpMsgLanCaptureDeviceNotAliveNotify;
import com.huaiye.cmf.sdp.SdpMsgLanCaptureDeviceStopped;
import com.huaiye.cmf.sdp.SdpMsgLanViewerNotAliveNotify;
import com.huaiye.samples.p2p.P2PSample;
import com.huaiye.samples.p2p.P2PSampleHandler;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.player.HYPlayer;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi.abilities.meet.callback.CallbackQuitMeet;
import com.huaiye.sdk.sdkabi.common.SDKUtils;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyGPSStatus;
import com.huaiye.sdk.sdpmsgs.meet.CGetMbeConfigParaRsp;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingRsp;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.bus.AcceptDiaoDu;
import huaiye.com.vvs.bus.CloseEvent;
import huaiye.com.vvs.bus.CloseView;
import huaiye.com.vvs.bus.CreateMeet;
import huaiye.com.vvs.bus.CreateTalkAndVideo;
import huaiye.com.vvs.bus.FinishDiaoDu;
import huaiye.com.vvs.bus.GPSStatus;
import huaiye.com.vvs.bus.MeetInvistor;
import huaiye.com.vvs.bus.ShowChangeSizeView;
import huaiye.com.vvs.bus.StopCaptureBy;
import huaiye.com.vvs.bus.TalkInvistor;
import huaiye.com.vvs.bus.WaitViewAllFinish;
import huaiye.com.vvs.common.AlarmMediaPlayer;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.ErrorMsg;
import huaiye.com.vvs.common.IntentWrapper;
import huaiye.com.vvs.common.SP;
import huaiye.com.vvs.common.rx.RxUtils;
import huaiye.com.vvs.common.views.PermissionUtils;
import huaiye.com.vvs.common.views.WindowManagerUtils;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.AppMessages;
import huaiye.com.vvs.dao.msgs.BroadcastManage;
import huaiye.com.vvs.dao.msgs.BroadcastMessage;
import huaiye.com.vvs.dao.msgs.CallRecordManage;
import huaiye.com.vvs.dao.msgs.CallRecordMessage;
import huaiye.com.vvs.dao.msgs.CaptureMessage;
import huaiye.com.vvs.dao.msgs.CaptureZhiFaMessage;
import huaiye.com.vvs.dao.msgs.ChangeUserBean;
import huaiye.com.vvs.dao.msgs.ChatUtil;
import huaiye.com.vvs.dao.msgs.MapMarkBean;
import huaiye.com.vvs.dao.msgs.MessageData;
import huaiye.com.vvs.dao.msgs.PlayerMessage;
import huaiye.com.vvs.dao.msgs.StopCaptureMessage;
import huaiye.com.vvs.dao.msgs.VssMessageBean;
import huaiye.com.vvs.map.baidu.GPSLocation;
import huaiye.com.vvs.map.baidu.LocationService;
import huaiye.com.vvs.map.baidu.activity.OfflineMapListActivity;
import huaiye.com.vvs.map.baidu.appcluster.MyCluster;
import huaiye.com.vvs.map.baidu.clusterutil.clustering.Cluster;
import huaiye.com.vvs.map.baidu.clusterutil.clustering.ClusterManager;
import huaiye.com.vvs.models.ConfigResult;
import huaiye.com.vvs.models.ModelApis;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.auth.KickOutHandler;
import huaiye.com.vvs.models.auth.KickOutUIObserver;
import huaiye.com.vvs.models.contacts.bean.PersonModelBean;
import huaiye.com.vvs.models.download.DownloadService;
import huaiye.com.vvs.models.map.bean.BDMarkBean;
import huaiye.com.vvs.models.p2p.SdpMsgFindLanCaptureDeviceRspWrap;
import huaiye.com.vvs.ui.chat.ChatListActivity;
import huaiye.com.vvs.ui.chat.ChatPlayHelper;
import huaiye.com.vvs.ui.home.present.MainPresent;
import huaiye.com.vvs.ui.home.view.ActionBarLayout;
import huaiye.com.vvs.ui.home.view.CaptureViewLayout;
import huaiye.com.vvs.ui.home.view.HomeViewPagerAdapter;
import huaiye.com.vvs.ui.home.view.HomeViewPagerListener;
import huaiye.com.vvs.ui.home.view.IMainView;
import huaiye.com.vvs.ui.home.view.LeftMenuLayout;
import huaiye.com.vvs.ui.home.view.MarkPointLayout;
import huaiye.com.vvs.ui.home.view.MuliteMarkerLayout;
import huaiye.com.vvs.ui.home.view.NoticMsgLayout;
import huaiye.com.vvs.ui.home.view.PlayerViewLayout;
import huaiye.com.vvs.ui.home.view.PreviewLayout;
import huaiye.com.vvs.ui.home.view.RightMenuLayout;
import huaiye.com.vvs.ui.home.view.SosDialog;
import huaiye.com.vvs.ui.home.view.TalkViewLayout;
import huaiye.com.vvs.ui.home.view.VideoWaitAcceptDialog;
import huaiye.com.vvs.ui.home.view.VideoWaitAcceptPending;
import huaiye.com.vvs.ui.home.view.WaitAcceptLayout;
import huaiye.com.vvs.ui.local.MediaLocalIndexActivity;
import huaiye.com.vvs.ui.meet.MeetActivity;
import huaiye.com.vvs.ui.talk.TalkVideoActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static huaiye.com.vvs.common.AppUtils.P2P_COMMON_UDP_MSG;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_false;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_main_show_type;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_map_type;
import static huaiye.com.vvs.common.AppUtils.STRING_KEY_true;
import static huaiye.com.vvs.common.AppUtils.ctx;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: MainActivity
 */
@BindLayout(R.layout.activity_main)
public class MainActivity extends AppBaseActivity implements IMainView, BaiduMap.OnMapLoadedCallback, P2PSampleHandler {

    public static final int ACTION_LOGOUT = 111;
    public static final String ACTION = "action";

    final int INVALID_DIRECTION = 500;
    @BindView(R.id.waitAcceptLayout)
    public WaitAcceptLayout waitAcceptLayout;

    @BindView(R.id.ab_title)
    public ActionBarLayout ab_title;

    @BindView(R.id.speak_voice_view)
    SpeakVoiceView mSpeakVoiceView;
    @BindView(R.id.mpl_view)
    public MarkPointLayout mpl_view;
    @BindView(R.id.nms_view)
    NoticMsgLayout nms_view;
    @BindView(R.id.bms_view)
    ViewPager bms_view;
    @BindView(R.id.pl_view)
    PreviewLayout pl_view;
    @BindView(R.id.mmk_view)
    public MuliteMarkerLayout mmk_view;
    @BindView(R.id.menu_left)
    public LeftMenuLayout menu_left;
    @BindView(R.id.menu_right)
    public RightMenuLayout menu_right;
    @BindView(R.id.cvl_capture)
    public CaptureViewLayout cvl_capture;
    @BindView(R.id.pvl_player)
    public PlayerViewLayout pvl_player;
    @BindView(R.id.menu_top_right)
    TopRightMenuView menu_top_right;

    @BindView(R.id.fl_map_view)
    View fl_map_view;
    @BindView(R.id.map_view)
    MapView map_view;
    @BindView(R.id.ll_left_menu)
    View ll_left_menu;
    @BindView(R.id.tv_ptt)
    ImageView tv_ptt;
    @BindView(R.id.tv_name_menu)
    TextView tv_name_menu;
    @BindView(R.id.tv_user_id)
    TextView tv_user_id;
    @BindView(R.id.iv_header)
    ImageView iv_header;

    @BindView(R.id.iv_gps_1)
    ImageView iv_gps_1;
    @BindView(R.id.iv_gps_2)
    ImageView iv_gps_2;
    @BindView(R.id.iv_gps_3)
    ImageView iv_gps_3;
    @BindView(R.id.iv_gps_4)
    ImageView iv_gps_4;
    @BindView(R.id.ll_gps)
    LinearLayout ll_gps;
    @BindView(R.id.tv_gps_status)
    TextView tvGpsStatus;

    @BindView(R.id.tv_local)
    TextView tv_local;


    @BindView(R.id.fl_parent)
    FrameLayout fl_parent;

    @BindView(R.id.slm_parent)
    public DrawerLayout slm_parent;
    @BindView(R.id.iv_not_read)
    public ImageView ivNotRead;
    @BindView(R.id.tv_gps_close)
    public TextView tv_gps_close;
    @BindView(R.id.tv_local_address)
    TextView tv_local_address;
    @BindView(R.id.dots_layout)
    LinearLayout dots_layout;


    private LocationService locationService;
    private MapStatus ms;
    private BaiduMap mBaiduMap;
    public MyLocationData currentMapData;

    @BindExtra
    public boolean isNoCenter;

    MainPresent present;
    ClusterManager<MyCluster> clusterManager;
    RxUtils rxUtils;

    MyOrientationListener myOrientationListener;
    BDLocation currentlocation;
    HomeViewPagerAdapter homeViewPagerAdapter;
    ArrayList<VssMessageBean> bmsDatas = new ArrayList<>();
    private int currentPager;
    VideoWaitAcceptPending mVideoWaitAcceptPengding;
    KickOutUIObserver mKickoutObserver;

    /**
     * 无中心
     */
    public P2PSample mP2PSample;
    /**
     * 对应地图上的P2P用户点
     */
    ArrayList<MyCluster> p2pUsers = new ArrayList<>();
    /**
     * key是p2p用户ip
     */
    ConcurrentHashMap<String, MyCluster> p2pUsersMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Long> p2pUserTimestamp = new ConcurrentHashMap<>();
    /**
     * 定时检查p2p用户是否还活着
     */
    Disposable p2pUserCheckDisposable;

    AlertDialog alertDialog;
    EditText passwdEncrypt;
    boolean bFirstEncryptTip = true;
    SdpMsgFindLanCaptureDeviceRsp mySelf;

    @Override
    protected void initActionBar() {
        ((MCApp) ctx).startTimer();

        getNavigate().setVisibility(GONE);

        initMap();

        locationService = ((MCApp) getApplication()).locationService;
        locationService.start();

        GPSLocation.get().setGpsStatusInterface(new GPSLocation.GpsStatusInterface() {
            @Override
            public void gpsSwitchState(boolean gpsOpen) {
                if (gpsOpen) {
                    ll_gps.setVisibility(View.VISIBLE);
                    tv_gps_close.setVisibility(View.GONE);
                } else {
                    ll_gps.setVisibility(View.GONE);
                    tv_gps_close.setVisibility(View.VISIBLE);
                }
            }
        });
        GPSLocation.get().startGpsObserver();
        present = new MainPresent(this);

        nms_view.showThisView();

        rxUtils = new RxUtils();
//        int id = getUrlCallID("rtsp://192.168.1.66:554/2268/rtsp://192.168.2.192:5544/5268/rtsp://192.168.2.192:5544/6_3/69/3/14dda9d2e171/10000015_2?BitRate=512000;FrameRate=25.000000;IFrame=100;DmgType=2260;mbesec=60002625");
//        Logger.debug("id = " + id);

        mKickoutObserver = new KickOutUIObserver();
        mKickoutObserver.start(new KickOutHandler() {
            @Override
            public void onKickOut() {
                closeMediaViewWhitOutClick();
            }
        });
        initViewPager();

        mVideoWaitAcceptPengding = new VideoWaitAcceptPending();

        passwdEncrypt = new EditText(this);
        passwdEncrypt.setInputType(EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        passwdEncrypt.setTransformationMethod(PasswordTransformationMethod.getInstance());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入加密卡密码");
        builder.setIcon(R.drawable.anquanfanghu);
        builder.setView(passwdEncrypt);
        builder.setPositiveButton("加密", null);
        builder.setNegativeButton("不加密", null);
        alertDialog = builder.create();
        alertDialog.hide();
    }

    private void initViewPager() {
        homeViewPagerAdapter = new HomeViewPagerAdapter(bmsDatas, this);
        bms_view.setAdapter(homeViewPagerAdapter);
        bms_view.setCurrentItem(0);
        bms_view.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int itemPosition = position % bmsDatas.size();
                currentPager = itemPosition;
                monitorPoint(currentPager);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        homeViewPagerAdapter.setHomeViewPagerListener(new HomeViewPagerListener() {
            @Override
            public void itemPagerClick(int postion) {
                VssMessageBean tag = bmsDatas.get(postion);
                closeBroadcast(postion);
                if (AppUtils.isTalk) {
                    if (WindowManagerUtils.simpleView instanceof TalkViewLayout) {

                    } else {
                        if (PermissionUtils.XiaoMiMobilePermission(AppUtils.ctx)) {
                            return;
                        }
                        waitAcceptLayout.removeTalkView();
                        WindowManagerUtils.createSmall(AppUtils.getTvl_view(MainActivity.this));
                    }
                    nms_view.jumpToChat(tag, MainActivity.this);
                } else {
                    nms_view.jumpToChat(tag, MainActivity.this);
                }
            }

            @Override
            public void itemClose(int postion) {
                closeBroadcast(postion);
            }
        });
    }

    private void closeBroadcast(int postion) {
        bmsDatas.remove(postion);
        if (bmsDatas.size() == 0) {
            bms_view.setVisibility(GONE);
        }
        if (bmsDatas.size() == 0 || bmsDatas.size() == 1) {
            dots_layout.setVisibility(GONE);
            currentPager = 0;
        }
        dots_layout.removeAllViews();
        addPoint();
        homeViewPagerAdapter.notifyDataSetChanged();
    }

    private void initMap() {

        ms = new MapStatus.Builder().target(new LatLng(31.988401, 118.779742)).zoom(8).build();
        map_view.showZoomControls(false);
        mBaiduMap = map_view.getMap();
        map_view.clearAnimation();
        mBaiduMap.clear();

        mBaiduMap.setOnMapLoadedCallback(this);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
        clusterManager = new ClusterManager<>(this, mBaiduMap);
        // 设置可改变地图位置
        mBaiduMap.setMyLocationEnabled(true);

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mmk_view.closeView();
                mpl_view.closeThisView();
                ab_title.hideList();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(clusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(clusterManager);
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyCluster>() {
            @Override
            public boolean onClusterClick(Cluster<MyCluster> cluster) {
                ab_title.hideList();
                mpl_view.closeThisView();

                if (HYClient.getSdkOptions().P2P().isP2PRunning()) {
                    mmk_view.showDate(p2pUsers);
                } else {
                    mmk_view.showDate((ArrayList<MyCluster>) cluster.getItems());
                }

                return false;
            }
        });
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyCluster>() {
            @Override
            public boolean onClusterItemClick(MyCluster item) {
                ab_title.hideList();
                mmk_view.closeView();
                mpl_view.showInfo(item);
                return false;
            }
        });
        mBaiduMap.setOnMapDoubleClickListener(new BaiduMap.OnMapDoubleClickListener() {
            @Override
            public void onMapDoubleClick(LatLng latLng) {
                animateMapStatus(MapStatusUpdateFactory.zoomBy(5f));
            }
        });

    }

    @OnClick({
            R.id.ll_left_menu,
            R.id.tv_msg,
            R.id.tv_setting,
            R.id.tv_lixian_map,
            R.id.tv_local_capture,
            R.id.tv_start_p2p,
            R.id.tv_stop_p2p,
            R.id.tv_local_capture,
            R.id.tv_local,
            R.id.call_record})
    public void onClick(View view) {
        if (view.getId() == R.id.ll_left_menu) {
            //只是拦截下点击事件,不然事件会传递到下面的mapview
            return;
        }

        showSmallView();

        switch (view.getId()) {
            case R.id.tv_start_p2p:
                startP2P();
                break;
            case R.id.tv_stop_p2p:
                stopP2P();
                break;
            case R.id.tv_lixian_map:
                startActivity(new Intent(this, OfflineMapListActivity.class));
                break;
            case R.id.tv_local_capture:
                startActivity(new Intent(this, MediaLocalIndexActivity.class));
                break;
            case R.id.tv_setting:
                startActivity(new Intent(this, SettingActivity.class));
                if (slm_parent.isDrawerOpen(Gravity.LEFT)) {
                    slm_parent.closeDrawer(Gravity.LEFT, false);
                }
                break;
            case R.id.tv_msg:
                startActivity(new Intent(this, MessageActivity.class));
                break;
            case R.id.call_record:
                startActivity(new Intent(this, CallRecordActivity.class));
//            case R.id.tv_local:
//                if ((Integer)tv_local.getTag()==0){
//                    tv_local.setTag(1);
//                    tv_local.setText(currentlocation.getAddress().address==null?AppUtils.getString(R.string.no_adress_name):currentlocation.getAddress().address);
//                }else {
//                    tv_local.setTag(0);
//                    tv_local.setText("N" + currentlocation.getLatitude() + " E" + currentlocation.getLongitude());
//                }
//                break;
        }
        if (slm_parent.isDrawerOpen(Gravity.LEFT)) {
            slm_parent.closeDrawers();
        }
    }

    @Override
    public void doInitDelay() {


        ab_title.setOnMenuItemClickListener(new ActionBarLayout.OnMenuItemClickListener() {
            @Override
            public void onLeftMenuClick() {
                if (isNoCenter) {
                    if (mySelf != null) {
                        tv_name_menu.setText(mySelf.m_strName);
                        tv_user_id.setText(AppUtils.getString(R.string.person_title) + " IP:" + mySelf.m_strIP);
                    }
                } else {
                    tv_name_menu.setText(AppDatas.Auth().getUserName());
                    tv_user_id.setText(AppUtils.getString(R.string.person_title) + " ID:" + AppDatas.Auth().getUserID());
                }

                showSmallView();
                if (!slm_parent.isDrawerOpen(Gravity.LEFT)) {
                    slm_parent.openDrawer(Gravity.LEFT);
                    ensureMsgNotRead();
                }
            }

            @Override
            public void onContactMenuClick() {
                showSmallView();
                if (isNoCenter) {
                    Intent intent = new Intent(MainActivity.this, P2PContactsActivity.class);
                    ArrayList<SdpMsgFindLanCaptureDeviceRspWrap> devices = new ArrayList<>();
                    for (MyCluster cluster : p2pUsers) {
                        if (cluster.bean.p2pDeviceBean != null) {
                            devices.add(new SdpMsgFindLanCaptureDeviceRspWrap(cluster.bean.p2pDeviceBean));
                        }
                    }
                    intent.putExtra("devices", devices);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                }
            }

            @Override
            public void onChatMenuClick() {
                showSmallView();
                startActivity(new Intent(MainActivity.this, ChatListActivity.class));
            }

            @Override
            public void onEnterP2pClick() {
                startP2P();
            }
        });

        ab_title.setChangeChannelListener(new ActionBarLayout.OnChangeChannelListener() {
            @Override
            public void onChangeChannel(TrunkChannelBean bean) {

            }

            @Override
            public void onStopSpeak(String from) {
                System.out.println("ptt stop " + from);
                tv_ptt.setImageResource(R.drawable.btn_ptt);
                mSpeakVoiceView.speakEnd();
            }

            @Override
            public void start() {
                System.out.println("ptt start");
                tv_ptt.setImageResource(R.drawable.btn_ptt_pressed);
                mSpeakVoiceView.speakStart();
            }

            @Override
            public void onShowList() {
                mmk_view.closeView();
                mpl_view.closeThisView();
            }

            @Override
            public void onSpeakWillFinish() {
                mSpeakVoiceView.willCountDown();
            }

        });
        waitAcceptLayout.setListener(new WaitAcceptLayout.OnDisFromViewListener() {
            @Override
            public void onDisFromView() {
                changeViewWhenWait(true);
            }

            @Override
            public void onShowFromView() {
                changeViewWhenWait(false);
            }
        });


        cvl_capture.setiCaptureStateChangeListener(menu_right.getStateChangeListener());

        menu_left.setListener(new LeftMenuLayout.OnLeftClickListener() {
            @Override
            public void onGpsClick() {
            }

            @Override
            public void onDingWeiClick() {
                if (present.getLocation() != null) {
                    present.initLocal(present.getLocation(), true);
                } else if (locationService.getLastCity() != null) {
                    Location location = new Location("baidu");
                    location.setLatitude(locationService.getLastCity().getLatitude());
                    location.setLongitude(locationService.getLastCity().getLongitude());
                    present.initLocal(location, true);
                } else {
//                    present.initLocal(GPSLocation.get().getLastLocation(), true);
                }

            }

            @Override
            public void onSoSClick() {
                if (AppUtils.isTalk || AppUtils.isMeet || AppUtils.isVideo
                        || pvl_player.getVisibility() != GONE
                        || waitAcceptLayout.getVisibility() != GONE) {
                    AppUtils.showMsg(pvl_player.getVisibility() != GONE, false);
                } else {
                    ArrayList<MyCluster> personClusters = present.getPersonCluster();
                    ArrayList<PersonModelBean> persons = new ArrayList<>();
                    for (MyCluster onePerson : personClusters) {
                        if (onePerson.bean != null && onePerson.bean.personModelBean != null) {
                            persons.add(onePerson.bean.personModelBean);
                        }
                    }
                    SosDialog sosDialog = SosDialog.getInstance(persons);
                    sosDialog.show(getSupportFragmentManager(), "sosDialog");
                }

            }

//            @Override
//            public void clickMark(MarkModelBean bean) {
//                present.loadMapMark(bean);
//            }
        });


        menu_top_right.setMainPresent(present);

        menu_right.setListener(new RightMenuLayout.OnRightClickListener() {
            @Override
            public void onPreviewClick() {
                cvl_capture.toggleShowHide();
            }

            @Override
            public boolean onToggleClick() {
                if (AppUtils.isTalk || AppUtils.isMeet || AppUtils.isVideo
                        || pvl_player.getVisibility() != GONE
                        || waitAcceptLayout.getVisibility() != GONE) {
                    AppUtils.showMsg(pvl_player.getVisibility() != GONE, false);
                    return false;
                } else {
                    if (cvl_capture.getVisibility() == GONE) {
                        ab_title.hideList();
                    }
                    cvl_capture.toggleCapture(null);
                    return true;
                }

            }
        });

        tv_ptt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pttStart();
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        pttEnd();
                        break;
                }
                return false;
            }
        });

        ModelApis.Auth().getServiceConfig(new ModelCallback<ConfigResult>() {
            @Override
            public void onSuccess(ConfigResult changePwd) {
                ModelApis.Auth().requestVersion(MainActivity.this, null);
            }
        });

//        requestConfig();

        if (isNoCenter) {
            HYClient.getSdkOptions().P2P().setSupportP2P(true);
            startP2P();
        } else {
            HYClient.getSdkOptions().P2P().setSupportP2P(false);
            stopP2P();
        }

        showSystemSetting();
    }

    public void pttStart() {
        ab_title.startPTT();
        tv_ptt.setImageResource(R.drawable.btn_ptt_pressed);
    }

    public void pttEnd() {
        tv_ptt.setImageResource(R.drawable.btn_ptt);
        ab_title.stopPTT();
        mSpeakVoiceView.speakEnd();

    }


    private void requestConfig() {
        HYClient.getModule(ApiMeet.class).getMeetConfigureInfo(SdkParamsCenter.Meet.GetMeetConfig(),
                new SdkCallback<CGetMbeConfigParaRsp>() {
                    @Override
                    public void onSuccess(CGetMbeConfigParaRsp cGetMbeConfigParaRsp) {
                        for (CGetMbeConfigParaRsp.ConfigParamsBean temp : cGetMbeConfigParaRsp.lstMbeConfigParaInfo) {
                            if (temp.strMbeConfigParaName.equals("bIfSupportPTP")) {
                                if (temp.strMbeConfigParaValue.equals("1")) {
                                    HYClient.getSdkOptions().P2P().setSupportP2P(true);
                                } else {
                                    HYClient.getSdkOptions().P2P().setSupportP2P(false);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {

                    }
                });
    }


    long lastMillions = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (WindowManagerUtils.simpleView != null) {
                closeMediaView();
                return true;
            }
            if (waitAcceptLayout.getVisibility() != View.GONE) {
                waitAcceptLayout.onBackPressed();
                return true;
            }


            if (slm_parent.isDrawerOpen(Gravity.LEFT)) {
                slm_parent.closeDrawers();
                return true;
            }

            long currentMillions = System.currentTimeMillis();
            long delta = currentMillions - lastMillions;
            lastMillions = currentMillions;
            if (delta < 2000) {
                // 登出操作
//                AuthApi.get().logout();
                Logger.log("双击退出");
//                ((MCApp) ctx).gotoClose();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                return true;
            }

            showToast(AppUtils.getString(R.string.double_click_exit));
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VssMessageBean bean) {
        if (!"0".equals(bean.sessionID) ||
                bean.type == AppUtils.CAPTURE_TYPE_INT ||
                bean.type == AppUtils.PLAYER_TYPE_INT ||
                bean.type == AppUtils.PLAYER_TYPE_PERSON_INT ||
                bean.type == AppUtils.PLAYER_TYPE_DEVICE_INT ||
                bean.fromUserId.equals(AppDatas.Auth().getUserID() + "")) {
            return;
        }
        bms_view.setVisibility(VISIBLE);
        bmsDatas.add(0, bean);
        //大于一时显示进度条
        dots_layout.removeAllViews();
        if (bmsDatas.size() > 1)
            dots_layout.setVisibility(VISIBLE);
        addPoint();
        homeViewPagerAdapter.notifyDataSetChanged();
//        if (bean.type == AppUtils.BROADCAST_AUDIO_TYPE_INT){
//            startVoice(bean);
//        }else if (bean.type == AppUtils.BROADCAST_VIDEO_TYPE_INT){
        if (bean.type == AppUtils.BROADCAST_AUDIO_TYPE_INT || bean.type == AppUtils.BROADCAST_VIDEO_TYPE_INT
                || bean.type == AppUtils.BROADCAST_AUDIO_FILE_TYPE_INT) {
            BroadcastManage.get().add(BroadcastMessage.downFile(bean));
            Intent intent = new Intent(MainActivity.this, DownloadService.class);
            intent.putExtra("downloadURL", bean.content);
            intent.putExtra("isAudioVideo", true);
            intent.putExtra("type", bean.type);
            startService(intent);
        }

//            if (AppUtils.isTalk || AppUtils.isVideo || AppUtils.isMeet) {
//                AppUtils.showMsg(false, false);
//                return;
//            }
//            Intent intent = new Intent(MainActivity.this, MediaOnlineVideoPlayActivity.class);
//            intent.putExtra("path", bean.content);
//            startActivity(intent);
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BroadcastMessage bean) {
        if (bean.getType() == BroadcastMessage.TYPE_AUDIO) {
            startVoice(bean.getDown_path());
        } else if (bean.getType() == BroadcastMessage.TYPE_VIDEO) {

            if (AppUtils.isTalk || AppUtils.isVideo || AppUtils.isMeet) {
                AppUtils.showMsg(false, false);
                return;
            }
            Intent intent = new Intent(MainActivity.this, MediaOnlineVideoPlayActivity.class);
            intent.putExtra("path", bean.getDown_path());
            startActivity(intent);
        }
    }

    private void startVoice(final String path) {
        if (AlarmMediaPlayer.get().isPlaying() && AlarmMediaPlayer.get().getCurrentPlayBean() != null
                && AlarmMediaPlayer.get().getCurrentPlayBean().sourceType == AlarmMediaPlayer.SOURCE_CUSTOM) {
            AlarmMediaPlayer.get().stop();
        }
        String pathName = AppUtils.subPath(path);

        if (!TextUtils.isEmpty(pathName) && pathName.endsWith(".dat")) {
            if (ChatPlayHelper.get().getVideoParam() != null && ChatPlayHelper.get().getPlayMap().containsKey(pathName)
                    && ChatPlayHelper.get().getPlayMap().get(pathName)) {
                HYClient.getHYPlayer().stopPlay(null, ChatPlayHelper.get().getVideoParam());
            }
            playLocalAudio(path);
            return;
        }

        ChatPlayHelper.get().getPlayMap().put(AppUtils.subPath(path), true);
        AlarmMediaPlayer.get().play(true, AlarmMediaPlayer.SOURCE_CUSTOM, AppUtils.audiovideoPath + "/" + AppUtils.subPath(path), new AlarmMediaPlayer.PlayerListener() {
            @Override
            public void onComplete(AlarmMediaPlayer.PlayBean playBean) {
                ChatPlayHelper.get().getPlayMap().put(AppUtils.subPath(path), false);
            }

            @Override
            public void onError(AlarmMediaPlayer.PlayBean playBean) {
                ChatPlayHelper.get().getPlayMap().put(AppUtils.subPath(path), false);
            }
        });
    }

    private void playLocalAudio(final String path) {
        VideoParams videoParams = Player.Params.TypeVideoOfflineRecord().setResourcePath(AppUtils.audiovideoPath + "/" + AppUtils.subPath(path));
        ChatPlayHelper.get().setVideoParam(videoParams);
        HYClient.getHYPlayer().startPlay(videoParams
                .setMixCallback(new VideoCallbackWrapper() {
                    @Override
                    public void onSuccess(VideoParams param) {
                        super.onSuccess(param);
                        ChatPlayHelper.get().getPlayMap().put(AppUtils.subPath(path), true);
                    }

                    @Override
                    public void onGetVideoRange(VideoParams param, int start, int end) {
                        super.onGetVideoRange(param, start, end);
                    }

                    @Override
                    public void onVideoProgressChanged(VideoParams param, HYPlayer.ProgressType type, int current, int total) {
                        super.onVideoProgressChanged(param, type, current, total);
                    }

                    @Override
                    public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                        super.onVideoStatusChanged(param, msg);
                        SdkMsgNotifyPlayStatus status = (SdkMsgNotifyPlayStatus) msg;
                        if (status.isStopped()
                                && !isFinishing()) {
                            ChatPlayHelper.get().getPlayMap().put(AppUtils.subPath(path), false);
                        }

                    }

                    @Override
                    public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                        super.onError(param, errorInfo);
                        ChatPlayHelper.get().getPlayMap().put(AppUtils.subPath(path), false);
                        showToast(AppUtils.getString(R.string.play_audio_error));
                    }
                }));

    }


    @Override
    public void onEvent(CloseEvent data) {
        super.onEvent(data);
        waitAcceptLayout.reAddTalkView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StopCaptureBy bean) {
        cvl_capture.stopCapture();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BDLocation location) {
        if (present != null && location.getLatitude() != 0 && location.getLatitude() != 4.9e-324
                && location.getLongitude() != 0 && location.getLongitude() != 4.9e-324) {
            currentlocation = location;
            sendCommonUDPmsg();
            tv_local.setVisibility(VISIBLE);
//            if (tv_local.getTag()==null){
//                tv_local.setText("N" + location.getLatitude() + " E" + location.getLongitude());
//                tv_local.setTag(0);
//            }
            tv_local.setText("N" + location.getLatitude() + " E" + location.getLongitude());
            if (TextUtils.isEmpty(currentlocation.getAddress().address)) {
                tv_local_address.setVisibility(GONE);
            } else {
                tv_local_address.setVisibility(VISIBLE);
                int first = currentlocation.getAddress().address.indexOf("省");
                tv_local_address.setText(first == -1 ? currentlocation.getAddress().address : currentlocation.getAddress().address.substring(first + 1));
            }

            Location location1 = new Location("baidu");
            location1.setLatitude(location.getLatitude());
            location1.setLongitude(location.getLongitude());
            present.initLocal(location1, false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Location location) {
        if (present != null) {
            present.initLocal(location, false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CloseView bean) {
        stopAlarmMP3("MainActivity onEvent(CloseView bean) " + bean.from);
        waitAcceptLayout.closeWaitMenuView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GPSStatus bean) {
        if (bean.num == 0) {
            iv_gps_2.setImageResource(R.drawable.gpsbuliang);
            iv_gps_3.setImageResource(R.drawable.gpsbuliang);
            iv_gps_4.setImageResource(R.drawable.gpsbuliang);
            tvGpsStatus.setText("弱");
        } else if (bean.num == 1) {
            iv_gps_2.setImageResource(R.drawable.gpsliang);
            iv_gps_3.setImageResource(R.drawable.gpsbuliang);
            iv_gps_4.setImageResource(R.drawable.gpsbuliang);
            tvGpsStatus.setText("弱");
        } else if (bean.num == 2) {
            iv_gps_2.setImageResource(R.drawable.gpsliang);
            iv_gps_3.setImageResource(R.drawable.gpsliang);
            iv_gps_4.setImageResource(R.drawable.gpsbuliang);
            tvGpsStatus.setText("中");
        } else {
            iv_gps_2.setImageResource(R.drawable.gpsliang);
            iv_gps_3.setImageResource(R.drawable.gpsliang);
            iv_gps_4.setImageResource(R.drawable.gpsliang);
            tvGpsStatus.setText("强");
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StopCaptureMessage bean) {
        cvl_capture.stopCaptureFromUser(bean);
    }

    public void onEvent(CaptureMessage bean) {
        super.onEvent(bean);

        if (AppUtils.isMeet
                || AppUtils.isTalk
                || AppUtils.isVideo) {
            ChatUtil.get().rspGuanMo(bean.fromUserId, bean.fromUserDomain, bean.fromUserName, bean.sessionID);
            return;
        }

        ab_title.hideList();
        slm_parent.closeDrawers();
        cvl_capture.startCaptureFromUser(bean);
    }

    public void onEvent(CaptureZhiFaMessage bean) {
        super.onEvent(bean);

        menu_right.tv_toggle.performClick();
    }

    public void onEvent(PlayerMessage bean) {
        super.onEvent(bean);
        pvl_player.startPlayer(bean);
    }

    public void onEvent(SdpMsgFindLanCaptureDeviceRsp bean) {
        super.onEvent(bean);
        if (mP2PSample != null) {
            pvl_player.startPlayer(bean);
        }
    }

    /**
     * 到达这里的应该是本机发起的语音调度和视频调度
     * 需要播放声音
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final CreateTalkAndVideo bean) {
        if (waitAcceptLayout.getVisibility() != GONE) {
            getLogicDialog()
                    .setTitleText(AppUtils.getString(R.string.notice))
                    .setMessageText(AppUtils.getString(R.string.other_wait_accept))
                    .setConfirmClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            waitAcceptLayout.refuseBtn();
                            doTalkStart(bean);
                        }
                    })
                    .show();
        } else {
            doTalkStart(bean);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final CreateMeet bean) {
        if (waitAcceptLayout.getVisibility() != GONE) {
            getLogicDialog()
                    .setTitleText(AppUtils.getString(R.string.notice))
                    .setMessageText(AppUtils.getString(R.string.other_wait_accept))
                    .setConfirmClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            waitAcceptLayout.refuseBtn();
                            doMeetStart(bean);

                        }
                    }).show();
        } else {
            doMeetStart(bean);
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WaitViewAllFinish bean) {
        stopAlarmMP3("MainActivity onEvent(WaitViewAllFinish bean) " + bean.from);
        waitAcceptLayout.closeWaitViewAll();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ShowChangeSizeView bean) {

        if (bean.isShow) {
            if (AppUtils.isVideo) {
                if (AppUtils.isVideoViewNull()) {
                    return;
                }
                WindowManagerUtils.createSmall(AppUtils.getTvvl_view(this));
            } else if (AppUtils.isMeet) {
                if (AppUtils.isMeetViewNull()) {
                    return;
                }
                WindowManagerUtils.createSmall(AppUtils.getMeet_view(this));
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinshDiaodu(FinishDiaoDu finishDiaoDu) {
        closeThisView("onFinshDiaodu");
        if (AppUtils.isCaptureLayoutShowing) {
            cvl_capture.onResume();
        }
    }

    @Override
    public void onEvent(CGetMeetingInfoRsp bean) {
        //这里没找到发送的地方
        super.onEvent(bean);
        cvl_capture.stopCapture();
        MeetActivity.stratMeet(MainActivity.this, bean.strMainUserID.equals(AppDatas.Auth().getUserID() + ""),
                bean.strDomainCode, bean.nMeetingID, 0, bean.getRequiredMediaMode());
    }

    @Override
    public void onEvent(AcceptDiaoDu status) {
        super.onEvent(status);
        stopAlarmMP3("from acceptdiaodu");

        ab_title.acceptWaite();

        if (status.meetData != null) {
            CNotifyInviteUserJoinMeeting meetData = status.meetData;
            MeetActivity.stratMeet(getContext(), meetData.isSelfMeetCreator(), meetData.strMeetingDomainCode, meetData.nMeetingID, status.millis, meetData.getRequiredMediaMode());
            CallRecordManage.get().updateCall(status.meetData.nMsgSessionID);
        } else {
            videoOrTalkBegin(status.talkData, status.udpMsg, status.millis);
            if (status.talkData != null) {
                CallRecordManage.get().updateCall(status.talkData.nMsgSessionID);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MapMarkBean bean) {
        if (present != null) {
            present.dealMapMark(bean, bean.nMsgType);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyGPSStatus bean) {
        if (present != null) {
            present.onGPSStatus(bean);
        }
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
        pl_view.refBean(bean);
        mpl_view.refInfo(bean);
    }

    /**
     * 视频会话或者语音会话开始了
     *
     * @param talkData
     * @param millis
     */
    private void videoOrTalkBegin(CNotifyUserJoinTalkback talkData, SdpMsgCommonUDPMsg udpMsg, long millis) {
        AppMessages.get().del(millis);
        if (talkData != null) {
            if (talkData.getRequiredMediaMode() == SdkBaseParams.MediaMode.AudioAndVideo) {
                TalkVideoActivity.joinTalk(this, talkData.strTalkbackDomainCode, talkData.nTalkbackID, udpMsg);
            } else {
                waitAcceptLayout.acceptTalkInvite(talkData, millis);
                rxUtils.doDelayOn(2000, new RxUtils.IMainDelay() {
                    @Override
                    public void onMainDelay() {
                        //为了capture重新获取preview
                        cvl_capture.onResume();
                    }
                });
            }
        } else {
            TalkVideoActivity.joinTalk(this, "", -1, udpMsg);
        }
    }

    /**
     *
     */
    private void showSystemSetting() {
        int showToWhiteVersion = SP.getInteger("showToWhiteVersion", 1);
        if (showToWhiteVersion < AppUtils.versionCode()) {
            IntentWrapper.whiteListMatters(this, null);
            SP.putInt("showToWhiteVersion", AppUtils.versionCode());
        }
    }

    /**
     * 视频和会议开始前要通知对讲暂停,侧边栏菜单关闭,视频采集关闭
     */
    private void meetTalkStartPre() {
        slm_parent.closeDrawers();
        ab_title.acceptWaite();
        //会议和对讲的时候,采集不停止
//        cvl_capture.stopCapture();
    }

    private void doTalkStart(final CreateTalkAndVideo bean) {
        if (bean.device == null)
            CallRecordManage.get().add(CallRecordMessage.from(bean));
        meetTalkStartPre();
        String extParam = "";
        if (bean.from.equals("sos")) {
            playAlarmMP3(AlarmMediaPlayer.SOURCE_SOS_VOICE);
            extParam = "sos通话";
        } else {
            playAlarmMP3(AlarmMediaPlayer.SOURCE_CALL_VOICE);
        }
        final String finalExtParam = extParam;
        rxUtils.doDelayOn(200, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                if (bean.hasVideo) {
                    TalkVideoActivity.createTalk(MainActivity.this, bean.domain, bean.id, bean.name, finalExtParam, bean.device);
                } else {
                    waitAcceptLayout.createTalk(false, bean.domain, bean.id, bean.name);
                    //等两秒重新setPreview
                    rxUtils.doDelayOn(2000, new RxUtils.IMainDelay() {
                        @Override
                        public void onMainDelay() {
                            cvl_capture.onResume();
                        }
                    });
                }

            }
        });
    }

    private void doMeetStart(final CreateMeet bean) {
        CallRecordManage.get().add(CallRecordMessage.from(bean));
        //发起列表加上自己
        CStartMeetingReq.UserInfo userBean = new CStartMeetingReq.UserInfo();
        userBean.setDevTypeUser();
        userBean.strUserDomainCode = AppDatas.Auth().getDomainCode();
        userBean.strUserID = AppDatas.Auth().getUserID() + "";
        userBean.strUserName = AppDatas.Auth().getUserName();
        bean.sessionUserList.add(userBean);

        mZeusLoadView.loadingText(AppUtils.getString(R.string.is_start_meet)).setLoading();
        meetTalkStartPre();
        rxUtils.doDelayOn(200, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                sendCreateMeetMsg(bean.sessionUserList);
            }
        });
    }

    /**
     * 等待接听的时候,是否隐藏无关界面
     *
     * @param show
     */
    private void changeViewWhenWait(boolean show) {
        if (show) {
            menu_left.setVisibility(VISIBLE);
            menu_right.setVisibility(VISIBLE);
            tv_ptt.setVisibility(VISIBLE);
            menu_top_right.setVisibility(VISIBLE);
        } else {
            menu_left.setVisibility(GONE);
            menu_right.setVisibility(GONE);
            tv_ptt.setVisibility(GONE);
            menu_top_right.setVisibility(GONE);
        }
    }

    @Override
    public void onTalkInvite(final TalkInvistor data) {
        if (MeetActivity.activityShow || TalkVideoActivity.activityShow) {
            return;
        }

//        if (iv_change_size.getVisibility() == GONE) {
//            return;
//        }

        super.onTalkInvite(data);
        if (data == null) {
            return;
        }

        if (data.talk == null && data.p2p_talk == null) {
            //重连后检查聊天链接状态,断开的话就关闭当前的调度页面
            waitAcceptLayout.closeIfTalkDisconnect();
            return;
        }

        if (!AppUtils.isMeet
                && !AppUtils.isTalk
                && !AppUtils.isVideo) {
            //有强制接通的话,直接接通
            if (data.talk != null) {
                if (data.talk.isForceInvite()) {
                    rxUtils.doDelayOn(500, new RxUtils.IMainDelay() {
                        @Override
                        public void onMainDelay() {
                            EventBus.getDefault().post(new AcceptDiaoDu(null, data.talk, data.p2p_talk, data.millis));
                        }
                    });
                    return;
                }
            }
            if (data.talk != null && data.talk.getRequiredMediaMode() == SdkBaseParams.MediaMode.AudioAndVideo) {
                //本机正在响铃的时候说明正在准备接电话,直接拒绝另外一个
                if (getSupportFragmentManager().findFragmentByTag("videoWait") != null) {
                    HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                            .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                            .setTalkId(data.talk.nTalkbackID)
                            .setTalkDomainCode(data.talk.strTalkbackDomainCode), null);
                    return;
                }

                final VideoWaitAcceptDialog videoWaitAcceptDialog = VideoWaitAcceptDialog.getInstance(data.talk);
                videoWaitAcceptDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        changeViewWhenWait(true);
                    }
                });
                videoWaitAcceptDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        changeViewWhenWait(false);
                    }
                });
                if (isResumed) {
                    videoWaitAcceptDialog.show(getSupportFragmentManager(), VideoWaitAcceptDialog.TAG);
                } else {
                    mVideoWaitAcceptPengding.setPendingDialog(videoWaitAcceptDialog, data.talk);
                }


            } else {
                waitAcceptLayout.showWaitTalk(data.talk, data.p2p_talk, data.millis);

            }
            slm_parent.closeDrawers();
            playAlarmMP3(AlarmMediaPlayer.SOURCE_CALL_VOICE);
        } else {
            //有通话正在进行中,如果waitAcceptLayout显示中,说明正在进行语音通话,
            // 提示用户是否切换,如果切换,关闭当前语音,并且加入新的邀请
            if (waitAcceptLayout.getVisibility() != View.GONE) {
                if (((MCApp) ctx).getTopActivity().getLogicTimeDialog().isShowing()) {
                    // 会议中不接受对讲
                    HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                            .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                            .setTalkId(data.talk.nTalkbackID)
                            .setTalkDomainCode(data.talk.strTalkbackDomainCode), null);
                    return;
                }
                String str = "";
                if (data.talk.getRequiredMediaMode() == SdkBaseParams.MediaMode.AudioAndVideo) {
                    str = AppUtils.getString(R.string.video);
                } else {
                    str = AppUtils.getString(R.string.talk);
                }
                final long millis = data.millis;
                ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setTitleText(AppUtils.getString(R.string.invisitor_title))
                        .setMessageText(data.talk.strFromUserName + AppUtils.getString(R.string.invisitor_you) + str + "，" + AppUtils.getString(R.string.qiehuandao) + str + AppUtils.getString(R.string.diaodu_shifou))
                        .setCancelClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 语音中,拒绝邀请
                                AppMessages.get().del(millis);
                                HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                                        .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                                        .setTalkId(data.talk.nTalkbackID)
                                        .setTalkDomainCode(data.talk.strTalkbackDomainCode), null);

                                if (data.talk != null) {
                                    CallRecordManage.get().updateCall(data.talk.nMsgSessionID);
                                }
                            }
                        }).setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        waitAcceptLayout.closeAndJoinTalk(data.talk, data.millis);

                    }
                });
                ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setMeetMessage(false, "", "").show();
                return;
            }

            if (AppUtils.isVideo) {
                AppUtils.getTvvl_view(this).onTalkInvite(MainActivity.this, data.talk, null, data.millis);
                return;
            }

            if (AppUtils.isMeet) {
                if (WindowManagerUtils.simpleView != null) {
                    AppUtils.getMeet_view(this).onTalkInvite(MainActivity.this, data.talk, data.millis);
                }
            }

        }
    }

    @Override
    public void onMeetInvite(final MeetInvistor data) {
        Logger.debug("onMeetInvite  MeetActivity.activityShow " + MeetActivity.activityShow + " " + TalkVideoActivity.activityShow);
        if (MeetActivity.activityShow || TalkVideoActivity.activityShow) {
            return;
        }
//        if (iv_change_size.getVisibility() == GONE) {
//            return;
//        }

        super.onMeetInvite(data);

        if (data == null) {
            return;
        }

        if (data.meet == null) {
            //重连后关闭当前的调度页面
//            waitAcceptLayout.closeWaitViewAll();
            return;
        }
        Logger.debug("MeetViewLayout " + "onMeetInvite  strMeetDomainCode " + data.meet.strMeetingDomainCode + " nMeetID " + data.meet.nMeetingID + " mMediaMode " + data.meet.getRequiredMediaMode());

        if (data.meet.nMeetingStatus != 1) {
            return;
        }
        //waitAcceptLayout只有onDestory后才为空
        if (waitAcceptLayout != null) {
            // waitAcceptLayout出现的时候,不能进行其他操作,所以自己创建的时候,waitAcceptLayout一定不会再显示中的
            if (!AppUtils.isMeet
                    && !AppUtils.isTalk
                    && !AppUtils.isVideo) {
                // 自己创建的会议调度，直接进入
                // 其他情况显示等待框
                if (data.meet.isSelfMeetCreator()) {
                    mZeusLoadView.dismiss();
                    MeetActivity.stratMeet(MainActivity.this, data.meet.isSelfMeetCreator(), data.meet.strMeetingDomainCode,
                            data.meet.nMeetingID, data.millis, data.meet.getRequiredMediaMode());
                } else {
                    if (data.meet.isForceInvite()) {
                        rxUtils.doDelayOn(500, new RxUtils.IMainDelay() {
                            @Override
                            public void onMainDelay() {
                                EventBus.getDefault().post(new AcceptDiaoDu(data.meet, null, null, data.millis));
                            }
                        });
                        return;
                    }

                    slm_parent.closeDrawers();
                    waitAcceptLayout.showMeetTalk(data.meet, data.millis);
                    playAlarmMP3(AlarmMediaPlayer.SOURCE_CALL_VOICE);
                }
            } else {

                //当前正在视频对话
                if (AppUtils.isVideo) {
                    AppUtils.getTvvl_view(this).onMeetInvite(MainActivity.this, data.meet, data.millis);
                    return;
                }

                //当前正在语音对话
                if (waitAcceptLayout.getVisibility() != View.GONE) {

                    if (((MCApp) ctx).getTopActivity().getLogicTimeDialog().isShowing()) {
                        //直接拒绝
                        HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                                .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                                .setMeetID(data.meet.nMeetingID)
                                .setMeetDomainCode(data.meet.strMeetingDomainCode), null);
                        return;
                    }
                    final CNotifyInviteUserJoinMeeting temp = data.meet;
                    // 语音中来会议邀请，对话框提示
                    ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setMessageText(temp.strInviteUserName + AppUtils.getString(R.string.is_accept_meet_diaodu))
                            .setTitleText(AppUtils.getString(R.string.invisitor_title))
                            .setCancelClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AppMessages.get().del(data.millis);
                                    //拒绝
                                    HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                                            .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                                            .setMeetID(temp.nMeetingID)
                                            .setMeetDomainCode(temp.strMeetingDomainCode), null);
                                    CallRecordManage.get().updateCall(temp.nMsgSessionID);
                                }
                            })
                            .setConfirmClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    waitAcceptLayout.closeAndJoinMeet(data.meet, data.millis);

                                }
                            });
                    ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setMeetMessage(true, data.meet.nMeetingID + "", data.meet.strMeetingDomainCode).show();
                    return;
                }
                //当前正在会议
                if (AppUtils.isMeet) {
                    if (WindowManagerUtils.simpleView != null) {
                        if (data.meet == null) {
                            AppUtils.getMeet_view(this).closeMeet(null);
                            return;
                        }
                        if (data.meet.nMeetingStatus != 1) {
                            return;
                        }
                        if (((MCApp) ctx).getTopActivity().getLogicTimeDialog().isShowing()) {
                            HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                                    .setMeetID(data.meet.nMeetingID)
                                    .setMeetDomainCode(data.meet.strMeetingDomainCode), null);
                            return;
                        }
                        // 会议中来会议邀请，对话框提示
                        ((MCApp) ctx).getTopActivity().getLogicTimeDialog()
                                .setTitleText(AppUtils.getString(R.string.invisitor_title))
                                .setMessageText(data.meet.strInviteUserName + AppUtils.getString(R.string.is_accept_meet_diaodu))
                                .setCancelClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AppMessages.get().del(data.millis);
                                        HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                                                .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                                                .setMeetID(data.meet.nMeetingID)
                                                .setMeetDomainCode(data.meet.strMeetingDomainCode), null);
                                        CallRecordManage.get().updateCall(data.meet.nMsgSessionID);
                                    }
                                })
                                .setConfirmClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CallRecordManage.get().updateCall(data.meet.nMsgSessionID);
                                        //先关闭,然后再打开
                                        AppMessages.get().del(data.millis);
                                        AppUtils.getMeet_view(MainActivity.this).endMeet(new CallbackQuitMeet() {
                                            @Override
                                            public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                                                return false;
                                            }

                                            @Override
                                            public void onSuccess(Object o) {
                                                CNotifyInviteUserJoinMeeting meetData = data.meet;
                                                MeetActivity.stratMeet(getContext(), meetData.isSelfMeetCreator(), meetData.strMeetingDomainCode, meetData.nMeetingID, data.millis, meetData.getRequiredMediaMode());

                                            }

                                            @Override
                                            public void onError(ErrorInfo errorInfo) {

                                            }
                                        });
                                    }
                                });
                        ((MCApp) ctx).getTopActivity().getLogicTimeDialog().setMeetMessage(true, data.meet.nMeetingID + "", data.meet.strMeetingDomainCode).show();

                    }
                }
            }

        }

    }

    public void sendCreateMeetMsg(ArrayList<CStartMeetingReq.UserInfo> allUser) {
        HYClient.getModule(ApiMeet.class)
                .createMeeting(SdkParamsCenter.Meet.CreateMeet()
                        .setUsers(allUser)
                        .setOpenRecord(true)
                        .setMeetDesc("")
                        .setMeetTrunkMessage("")
                        .setEncrypt(HYClient.getSdkOptions().encrypt().isEncryptBind())
                        .setMeetName(AppDatas.Auth().getUserName() + " " + AppUtils.getString(R.string.meet_start) + " " + AppUtils.getString(R.string.linshi_meet)), new SdkCallback<CStartMeetingRsp>() {
                    @Override
                    public void onSuccess(CStartMeetingRsp cStartMeetingRsp) {

                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        EventBus.getDefault().post(new WaitViewAllFinish("waitacceptlayout createMeet onError"));
                        showToast(ErrorMsg.getMsg(ErrorMsg.create_meet_err_code));
                        mZeusLoadView.dismiss();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (waitAcceptLayout != null) {
            waitAcceptLayout.onResume();
        }
        if (mVideoWaitAcceptPengding != null) {
            mVideoWaitAcceptPengding.onResume(this);
        }
        if (ab_title != null) {
            ab_title.changeMenu(isNoCenter);
        }

        ab_title.onResume();
        pvl_player.onResume();
        cvl_capture.onResume();
        if (Boolean.parseBoolean(SP.getParam(STRING_KEY_main_show_type, STRING_KEY_true).toString())) {
            pl_view.setVisibility(GONE);
            map_view.setVisibility(View.VISIBLE);

            if (Boolean.parseBoolean(SP.getParam(STRING_KEY_map_type, STRING_KEY_false).toString())) {
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            } else {
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            }

            if (menu_left != null) {
                menu_left.showLocation(View.VISIBLE);
            }
        } else {
            pl_view.setVisibility(View.VISIBLE);
            map_view.setVisibility(GONE);
            mpl_view.closeThisView();

            if (menu_left != null) {
                menu_left.showLocation(GONE);
            }
        }

        map_view.onResume();

        changeMark();
        if (myOrientationListener != null) {
            myOrientationListener.start();
        }

        //弹出模态对话框输入密码
        alert_edit();
    }

    @Override
    public void setLocation(MyLocationData data) {
        setLocation(data, INVALID_DIRECTION);
    }

    public void setLocation(MyLocationData data, float direction) {
        if (currentlocation == null)
            return;
        MyLocationData.Builder builder = new MyLocationData.Builder()
                .accuracy(currentlocation.getRadius())
                .latitude(data.latitude)
                .longitude(data.longitude);
        if (direction != INVALID_DIRECTION) {
            builder.direction(direction);
        }
        currentMapData = builder.build();
        mBaiduMap.setMyLocationData(currentMapData);

//        int accuracyCircleFillColor = 0xAAFFFF88;
//        int accuracyCircleStrokeColor = 0xAA00FF00;
        MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL
                , true, null);
        mBaiduMap.setMyLocationConfiguration(configuration);
    }

    @Override
    public void drawCover(final MyCluster data) {
        clusterManager.addItem(data);
    }

    @Override
    public void refCluster() {
        clusterManager.cluster();
    }

    @Override
    public void deleteCluster(MyCluster data) {
        if (data == null) {
            return;
        }
        clusterManager.removeItem(data);
    }

    @Override
    public void notifyGPSInfoChange(String id, CNotifyGPSStatus cNotifyGPSStatus) {
        //preView只有用户信息变动的时候通知
        if (pl_view.getVisibility() == View.VISIBLE && cNotifyGPSStatus.isTypeUser()) {
            pl_view.notifyPersonInfoChange(cNotifyGPSStatus);
        }

        //这个是显示列表,数据来源于地图,地图收到通知的时候就更新了,现在只需要通知界面,重新绘制下就可以了
        if (mmk_view.getVisibility() == View.VISIBLE) {
            mmk_view.updateView();
        }

        //这个是显示单个的,如果id配对上就需要更新
        if (mpl_view.getVisibility() == View.VISIBLE && mpl_view.isShowing(id)) {
            mpl_view.updateView(cNotifyGPSStatus);
        }


    }


    @Override
    public Overlay addOverlay(OverlayOptions overlay) {
        return mBaiduMap.addOverlay(overlay);
    }

    @Override
    public void clearMarkMark() {
        clusterManager.clearItems();
    }

    @Override
    public void animateMapStatus(MapStatusUpdate update) {
        mBaiduMap.animateMapStatus(update);
    }

    @Override
    public AppBaseActivity getContext() {
        return this;
    }

    void changeMark() {
        clearMarkMark();
//        present.loadCover();
        present.loadPerson();
        present.loadDevice();
//        present.loadDomain();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (waitAcceptLayout != null) {
            waitAcceptLayout.onPause();
        }

        pvl_player.onPause();
        cvl_capture.onPause();
        map_view.onPause();
        myOrientationListener.stop();
        alertDialog.hide();
    }

    @Override
    protected void onDestroy() {

        GPSLocation.get().setGpsStatusInterface(null);
        present.destroy();
        mKickoutObserver.stop();
        ((MCApp) ctx).stopApp();

        if (waitAcceptLayout != null) {
            WindowManagerUtils.closeAll(true);
            waitAcceptLayout.onDestroy();
            waitAcceptLayout = null;
        }

        AlarmMediaPlayer.get().stop();


        cvl_capture.onDestroy();
        pvl_player.clickClose();
        nms_view.closeThisView();

        locationService.stop(); //停止定位服务
        ab_title.destroyEvent();

        map_view.onDestroy();
        map_view = null;
        GPSLocation.get().startGpsObserver();

        stopP2P();

        super.onDestroy();
    }

    @Override
    public void onMapLoaded() {
//        ms = new MapStatus.Builder().zoom(9).build();
//        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    /**
     * 关闭媒体相关界面
     */
    private void closeThisView(String from) {
        WindowManagerUtils.closeAll(true);
        if (waitAcceptLayout != null) {
            stopAlarmMP3("MainActivity closeThisView(String from) " + from);
            waitAcceptLayout.closeWaitViewAll();
        }
    }

    /**
     * 关闭媒体相关的view
     */
    private boolean closeMediaView() {
        if (AppUtils.isVideo) {
            AppUtils.getTvvl_view(this).onBackPressed();
            return false;
        }
        if (AppUtils.isTalk) {
            AppUtils.getTvl_view(this).onBackPressed();
            return false;
        }

        if (AppUtils.isMeet) {
            AppUtils.getMeet_view(this).onBackPressed();
            return false;
        }
        return true;
    }

    private boolean closeMediaViewWhitOutClick() {
        if (AppUtils.isVideo) {
            AppUtils.getTvvl_view(this).createError("closeMediaViewWhitOutClick isVideo");
            return false;
        }
        if (AppUtils.isTalk) {
            AppUtils.getTvl_view(this).closeRel();
            return false;
        }

        if (AppUtils.isMeet) {
            AppUtils.getMeet_view(this).quitMeet(false);
            return false;
        }
        if (cvl_capture != null && cvl_capture.getVisibility() == View.VISIBLE) {
            cvl_capture.stopCapture();
            return false;
        }
        return true;
    }


    String strName;

    void playAlarmMP3(int type) {

        rxUtils.clearTag(strName);
        strName = "time_out " + System.currentTimeMillis();
        rxUtils.doDelay(30 * 1000, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                stopAlarmMP3("");
                WindowManagerUtils.closeAll(true);
                if (waitAcceptLayout != null && waitAcceptLayout.getVisibility() == View.VISIBLE && !AppUtils.isTalk) {
                    waitAcceptLayout.closeWaitViewAll();
                }
                Fragment waitFragment = getSupportFragmentManager().findFragmentByTag(VideoWaitAcceptDialog.TAG);
                if (waitFragment != null) {
                    VideoWaitAcceptDialog videoWaitAcceptDialog = (VideoWaitAcceptDialog) waitFragment;
                    videoWaitAcceptDialog.refuseBtn();
                }
                mVideoWaitAcceptPengding.cancel();

            }
        }, strName);

        AlarmMediaPlayer.get().play(type);

    }

    public void stopAlarmMP3(String from) {
        AlarmMediaPlayer.get().stop();

        rxUtils.clearTag(strName);
        Logger.log("ddddddddddddddddddddddddddddddd stopAlarmMP3 " + from);
    }

    private void showSmallView() {
        if (AppUtils.isTalk) {
            if (WindowManagerUtils.simpleView instanceof TalkViewLayout) {

            } else {

                if (PermissionUtils.XiaoMiMobilePermission(AppUtils.ctx)) {
                    return;
                }

                waitAcceptLayout.removeTalkView();
                WindowManagerUtils.createSmall(AppUtils.getTvl_view(this));
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myOrientationListener = new MyOrientationListener(this);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                if (currentMapData == null) {
                    return;
                }
                //如果在采集中,不设置方向,节约下计算
                if (HYClient.getHYCapture().isCapturing() || HYClient.getHYPlayer().hasVideoRendering()) {
                    return;
                }
                setLocation(currentMapData, x);
            }
        });
    }

    public void alert_edit() {

        if (bFirstEncryptTip) {
            String enFile = SDKUtils.getStoragePath(HYClient.getContext(), true) + "/Android/data/" + HYClient.getContext().getPackageName() + "/files/" + "rt_sech2.bin";
            File encryptCard = new File(enFile);
            if (!encryptCard.exists()) {
                return;
            }
        } else {
            return;
        }

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strPsw = passwdEncrypt.getText().toString().trim();
                if (TextUtils.isEmpty(strPsw)) {
                    showToast(AppUtils.getString(R.string.encrypt_card_psw_hint));
                    //cb_encrypt.setChecked(false);
                    return;
                }

                bFirstEncryptTip = false;
                HYClient.getModule(ApiEncrypt.class).encryptInit(SdkParamsCenter.Encrypt.EncryptInit()
                        .setUserId(HYClient.getSdkOptions().User().getUserId())
                        .setTfCardRoot(SDKUtils.getStoragePath(HYClient.getContext(), true))
                        .setPackageName(HYClient.getContext().getPackageName())
                        .setPsw(strPsw)
                        .setFileName("rt_sech2.bin"), new SdkCallback<SdpMessageCmInitRsp>() {

                    @Override
                    public void onSuccess(SdpMessageCmInitRsp sdpMessageCmInitRsp) {
                        AppUtils.nEncryptPasswd = strPsw;
                        showToast("加密初始化成功 ");
                        alertDialog.dismiss();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast("加密初始化失败 " + errorInfo.getMessage());
                    }
                });
            }
        });
    }

    private void ensureMsgNotRead() {
        List<MessageData> msgList = AppDatas.Messages().getMessages();
        boolean haveNotRead = false;
        if (msgList != null && msgList.size() > 0) {
            for (MessageData messageData : msgList) {
                if (!messageData.getIsRead()) {
                    haveNotRead = true;
                    break;
                }
            }
        }

        if (haveNotRead) {
            ivNotRead.setVisibility(View.VISIBLE);
        } else {
            ivNotRead.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onGetScanDevices(SdpMsgFindLanCaptureDeviceRsp msg) {
        //过滤掉自己
        if (!TextUtils.isEmpty(HYClient.getSdkOptions().P2P().getP2PNickName()) && HYClient.getSdkOptions().P2P().getP2PNickName().equals(msg.m_strName)) {
            mySelf = msg;
            if (mySelf != null) {
                tv_user_id.setText(AppUtils.getString(R.string.person_title) + " IP:" + mySelf.m_strIP);
            }
            return;
        }

        boolean isContains = false;

        for (MyCluster tmp : p2pUsers) {
            if (tmp.bean.p2pDeviceBean != null) {
                if (tmp.bean.p2pDeviceBean.m_strName.equals(msg.m_strName)
                        || tmp.bean.p2pDeviceBean.m_strIP.equals(msg.m_strIP)) {
                    tmp.bean.p2pDeviceBean.m_nCaptureState = msg.m_nCaptureState;
                    tmp.bean.p2pDeviceBean.m_strIP = msg.m_strIP;
                    tmp.bean.p2pDeviceBean.m_strName = msg.m_strName;
                    try {
                        tmp.bean.latLng = new LatLng(Double.parseDouble(msg.m_strInfo.split(",")[0]),
                                Double.parseDouble(msg.m_strInfo.split(",")[1]));
                    } catch (Exception e) {
                        tmp.bean.latLng = new LatLng(31.988401, 118.779742);
                    }
                    isContains = true;
                    EventBus.getDefault().post(tmp);
                    break;
                }
            }
        }

        if (!isContains) {
            BDMarkBean bdMarkBean = new BDMarkBean();
            bdMarkBean.p2pDeviceBean = msg;
            if (!TextUtils.isEmpty(msg.m_strInfo)) {
                try {
                    bdMarkBean.latLng = new LatLng(Double.parseDouble(msg.m_strInfo.split(",")[0]),
                            Double.parseDouble(msg.m_strInfo.split(",")[1]));
                } catch (Exception e) {
                    bdMarkBean.latLng = new LatLng(31.988401, 118.779742);
                }
            }
            MyCluster cluster = new MyCluster(bdMarkBean);
            p2pUsers.add(cluster);
            p2pUsersMap.put(msg.m_strIP, cluster);
            drawCover(cluster);
            EventBus.getDefault().post(cluster);
            refCluster();

            Logger.debug("p2p device " + p2pUsers.size());
        }

        p2pUserTimestamp.put(msg.m_strIP, System.currentTimeMillis());


    }


    @Override
    public void onGetScanDevicesOffline(SdpMsgCommonUDPMsg msg) {
        MyCluster findItem = null;
        for (MyCluster tmp : p2pUsers) {
            if (tmp.bean.p2pDeviceBean != null) {
                if (tmp.bean.p2pDeviceBean.m_strIP.equals(msg.m_strIP)) {
                    findItem = tmp;
                    break;
                }
            }

        }
        if (findItem != null) {
            p2pUsers.remove(findItem);
            EventBus.getDefault().post(findItem.bean.p2pDeviceBean.m_strIP);
            p2pUsersMap.remove(findItem.bean.p2pDeviceBean.m_strIP);
            deleteCluster(findItem);
            refCluster();
        }

        p2pUserTimestamp.remove(msg.m_strIP);

    }

    @Override
    public void onWatchingDeviceOffline(SdpMsgLanCaptureDeviceNotAliveNotify sdpMsgLanCaptureDeviceNotAliveNotify) {
        showToast(AppUtils.getString(R.string.guanmo_capture_is_offline));
        pvl_player.stopPlayer();
    }

    @Override
    public void onWatchingDeviceCaptureStopped(SdpMsgLanCaptureDeviceStopped sdpMsgLanCaptureDeviceStopped) {
        showToast(AppUtils.getString(R.string.guanmo_capture_is_stop));
        pvl_player.stopPlayer();
    }

    @Override
    public void onWatchingDeviceStopWatch(SdpMsgCommonUDPMsg sdpMsgCommonUDPMsg) {
        // 观摩的设备停止了我对他的观摩
    }

    @Override
    public void onReceiveWatchRequest(SdpMsgCommonUDPMsg msg) {
        if (!cvl_capture.isCapture()) {
            cvl_capture.toggleCapture(null);
        }
        mP2PSample.respWatchRequest(SdkBaseParams.AgreeMode.Agree, msg.m_strIP);

    }

    @Override
    public void onReceiveCommonUDPMsg(SdpMsgCommonUDPMsg msg) {
        //更新用户坐标
        if (msg.m_nMsgType == P2P_COMMON_UDP_MSG) {
            MyCluster findItem = null;
            for (MyCluster temp : p2pUsers) {
                if (temp.bean.p2pDeviceBean != null && temp.bean.p2pDeviceBean.m_strIP.equals(msg.m_strIP)) {
                    findItem = temp;
                    break;
                }
            }
            if (findItem != null) {
                findItem.bean.latLng = new LatLng(Double.parseDouble(msg.m_strContent.split(",")[0]),
                        Double.parseDouble(msg.m_strContent.split(",")[1]));
                p2pUsers.remove(findItem);
                deleteCluster(p2pUsersMap.get(findItem.bean.p2pDeviceBean.m_strIP));
                MyCluster myCluster = new MyCluster(findItem.bean);
                p2pUsers.add(myCluster);
                p2pUsersMap.put(findItem.bean.p2pDeviceBean.m_strIP, myCluster);
                drawCover(myCluster);
                refCluster();
            }
        }
    }

    @Override
    public void onWatcherDeviceOffline(SdpMsgLanViewerNotAliveNotify sdpMsgLanViewerNotAliveNotify) {
        showToast(AppUtils.getString(R.string.guanmo_is_offline));
    }

    @Override
    public void onWatcherWatchStopped(SdpMsgCommonUDPMsg sdpMsgCommonUDPMsg) {
        showToast(AppUtils.getString(R.string.guanmo_is_stop));
        if (cvl_capture.isCapture()) {
            cvl_capture.stopCapture();
        }
    }

    @Override
    public void onReceiveTalkRequest(SdpMsgCommonUDPMsg sdpMsgCommonUDPMsg) {
        onTalkInvite(new TalkInvistor(null, sdpMsgCommonUDPMsg, System.currentTimeMillis()));
    }

    @Override
    public void onTalkStopped(SdpMsgCommonUDPMsg sdpMsgCommonUDPMsg) {
        waitAcceptLayout.isThisIp(sdpMsgCommonUDPMsg.m_strIP);
        AppUtils.getTvvl_view(this).stopTalk(this, sdpMsgCommonUDPMsg.m_strIP);
    }


    @Override
    public void startP2P() {
        if (HYClient.getSdkOptions().P2P().isSupportP2P() && !HYClient.getSdkOptions().P2P().isP2PRunning()) {
            closeMediaViewWhitOutClick();
            present.clearMyCluster();

            HYClient.getSdkOptions().P2P().setCaptureName(AppDatas.Auth().getNoCenterUser(), currentlocation == null ? "31.988401,118.779742" : currentlocation.getLatitude() + "," + currentlocation.getLongitude());
            mP2PSample = HYClient.getSdkSamples().P2P();
            mP2PSample.subscribe(this);
            mP2PSample.startP2P();
            mP2PSample.scanLanDevices();

            ab_title.changeToP2P();
            tv_ptt.setVisibility(View.GONE);
            p2pUserCheckDisposable = io.reactivex.Observable.interval(10, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            Iterator<Map.Entry<String, Long>> iterable = p2pUserTimestamp.entrySet().iterator();
                            while (iterable.hasNext()) {
                                Map.Entry<String, Long> item = iterable.next();
                                if (System.currentTimeMillis() - item.getValue() > 10 * 1000) {
                                    iterable.remove();
                                    SdpMsgCommonUDPMsg offlineMsg = new SdpMsgCommonUDPMsg();
                                    offlineMsg.m_strIP = item.getKey();
                                    onGetScanDevicesOffline(offlineMsg);
                                }
                            }
                        }
                    });
        } else {
            Logger.log("startP2P donothing " + HYClient.getSdkOptions().P2P().isSupportP2P() + " " + HYClient.getSdkOptions().P2P().isP2PRunning());
        }
    }

    @Override
    public void p2pConnectRetry(int remainTry) {
        ab_title.showConnectRetry(remainTry);
    }

    @Override
    public void stopP2P() {
        super.stopP2P();
        ab_title.hideConnectRetry();
        tv_ptt.setVisibility(View.VISIBLE);
        if (HYClient.getSdkOptions().P2P().isP2PRunning()) {
            if (p2pUserCheckDisposable != null) {
                p2pUserCheckDisposable.dispose();
                p2pUserCheckDisposable = null;
            }
            ab_title.changeToNormal();
            if (mP2PSample != null) {
                mP2PSample.stopScanLanDevices();
                mP2PSample.unSubscribe(this);
                mP2PSample.stopP2P();
                mP2PSample = null;
            }

            for (MyCluster temp : p2pUsers) {
                deleteCluster(temp);
            }
            p2pUsers.clear();
            p2pUsersMap.clear();
            refCluster();

            changeMark();
        } else {
            Logger.log("stopP2P donothing " + HYClient.getSdkOptions().P2P().isSupportP2P() + " " + HYClient.getSdkOptions().P2P().isP2PRunning());
        }
    }

    /**
     * 给每个p2p用户发送一个消息,告诉他们本机的ip
     */
    private void sendCommonUDPmsg() {
        if (mP2PSample != null) {
            for (MyCluster temp : p2pUsers) {
                if (temp.bean.p2pDeviceBean != null) {
                    mP2PSample.setCustomerMessage(temp.bean.p2pDeviceBean.m_strIP, P2P_COMMON_UDP_MSG, 0, currentlocation.getLatitude() + "," + currentlocation.getLongitude());
                }
            }

        }
    }


    public static int getUrlCallID(String url) {
        if (TextUtils.isEmpty(url)) {
            return 0;
        }
        if (!url.contains("mbesec")) {
            return 0;
        }
        String[] array = url.split(";");
        final String key = "mbesec=";
        for (int i = 0; i < array.length; i++) {
            if (array[i].contains(key)) {
                String str = array[i];
                int startPos = str.indexOf(key) + key.length();
                String strID = str.substring(startPos, str.length());
                try {
                    int id = Integer.parseInt(strID);
                    return id;
                } catch (NumberFormatException bfe) {
                    return 0;
                }

            }
        }
        return 0;
    }

    /**
     * 添加小圆点
     */
    private void addPoint() {
        if (bmsDatas.size() == 0 || bmsDatas.size() == 1)
            return;
        int height = AppUtils.getResourceDimenssion(3);
        int width = AppUtils.getResourceDimenssion(78) / bmsDatas.size();
        LinearLayout.LayoutParams pointParams = new LinearLayout.LayoutParams(width, height);
        for (int i = 0; i < bmsDatas.size(); i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(pointParams);
            if (currentPager == i) {
                iv.setBackgroundResource(R.drawable.shape_brocast_slide);
            } else {
                iv.setBackgroundResource(R.drawable.shape_brocast_slide_no);
            }
            dots_layout.addView(iv);
        }
    }

    /**
     * 判断小圆点
     */
    private void monitorPoint(int position) {
        for (int i = 0; i < bmsDatas.size(); i++) {
            if (i == position) {
                if (dots_layout.getChildAt(position) != null)
                    dots_layout.getChildAt(position).setBackgroundResource(R.drawable.shape_brocast_slide);
            } else {
                if (dots_layout.getChildAt(position) != null)
                    dots_layout.getChildAt(i).setBackgroundResource(R.drawable.shape_brocast_slide_no);
            }
        }
    }


}
