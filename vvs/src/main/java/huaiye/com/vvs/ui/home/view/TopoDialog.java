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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vvs.BuildConfig;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.recycle.SafeLinearLayoutManager;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.device.TopoDeviceGPS;
import huaiye.com.vvs.models.device.TopoDeviceListResp;
import huaiye.com.vvs.models.device.TopoInfoResp;
import huaiye.com.vvs.ui.home.holder.TopoHolder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * 自组网拓扑图
 */
public class TopoDialog extends BottomSheetDialogFragment {

    private String testStringList = "{\n" +
            "\"nResultCode\": 0,\n" +
            "\"strResultDescribe\": \"操作成功\",\n" +
            "\"lstAdDeviceInfo\": [{\n" +
            "    \"byname\": \"test1\",\n" +
            "\"ip\": \"192.168.1.66\",\n" +
            "\"devtype\": 1,\n" +
            "    \"noise\": -56\n" +
            "  }]\n" +
            "}";
    private String testString = " {\n" +
            "\"nResultCode\": 0,\n" +
            "\"strResultDescribe\": \"操作成功\",\n" +
            "\"lstMeshDeviceTop\": [{\n" +
            "\t\t                 \"srcdev\":\t{\n" +
            "\t\t\t\t\t\"strIP\":\t\"192.168.2.11\",\n" +
            "\t\t\t\t\t\"strByname\":\"1#\",\n" +
            "\t\t\t\t\t\"nNoise\":-105,\n" +
            "\t\t\t\t\t\"strMac\":\"1dsad\",\n" +
            "\"nDevType\":0\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t\"desdev\":\t{\n" +
            "\t\t\t\t\t\"strIP\":\t\"192.168.2.11\",\n" +
            "\t\t\t\t\t\"strByname\":\"3#\",\n" +
            "\t\t\t\t\t\"nNoise\":-99,\n" +
            "\t\t\t\t\t\"strMac\":\"1dsad\",\n" +
            "\"nDevType\":0\n" +
            "\t\t\t\t},\n" +
            "\"nSnr\":42,\n" +
            "\"nQuality\":42,\n" +
            "\"nNoise\":42,\n" +
            "\"nSignal\":42,\n" +
            "\"nRecvMode\":42,\n" +
            "\"nSendMode\":42\n" +
            "\t\t\t}, {                                  \n" +
            "                                \"srcdev\":       {              \n" +
            "                                        \"strIP\":\"192.168.2.31\",\n" +
            "                                        \"strByname\":\"3#\", \n" +
            "                                        \"nNoise\":-99,\n" +
            "\t\t\t\t     \"strMac\":\"1dsad\"\n" +
            "   \n" +
            "                                },                            \n" +
            "                                \"desdev\":       {           \n" +
            "                                        \"strIP\":   \"192.168.2.11\",\n" +
            "                                        \"strByname\": \"1#\", \n" +
            "                                        \"nNoise\": -105,\n" +
            "\t\t\t\t     \"strMac\":\"1dsad\"\n" +
            "},                                 \"nSnr\":42,\n" +
            "\"nQuality\":42,\n" +
            "\"nNoise\":42,\n" +
            "\"nSignal\":42,\n" +
            "\"nRecvMode\":42,\n" +
            "\"nSendMode\":42\n" +
            "                   \n" +
            "                        }, {                                  \n" +
            "                                \"srcdev\":       {              \n" +
            "                                        \"strIP\":\"192.168.2.31\",\n" +
            "                                        \"strByname\":\"3#\", \n" +
            "                                        \"nNoise\":-99,\n" +
            "\t\t\t\t     \"strMac\":\"1dsad\"\n" +
            "   \n" +
            "                                },                            \n" +
            "                                \"desdev\":       {           \n" +
            "                                        \"strIP\":   \"192.168.2.41\",\n" +
            "                                        \"strByname\": \"1#\", \n" +
            "                                        \"nNoise\": -105,\n" +
            "\t\t\t\t     \"strMac\":\"1dsad\"\n" +
            "},                                 \"nSnr\":42,\n" +
            "\"nQuality\":42,\n" +
            "\"nNoise\":42,\n" +
            "\"nSignal\":42,\n" +
            "\"nRecvMode\":42,\n" +
            "\"nSendMode\":42\n" +
            "                   \n" +
            "                        }, {                                  \n" +
            "                                \"srcdev\":       {              \n" +
            "                                        \"strIP\":\"192.168.2.51\",\n" +
            "                                        \"strByname\":\"3#\", \n" +
            "                                        \"nNoise\":-99,\n" +
            "\t\t\t\t     \"strMac\":\"1dsad\"\n" +
            "   \n" +
            "                                },                            \n" +
            "                                \"desdev\":       {           \n" +
            "                                        \"strIP\":   \"192.168.2.41\",\n" +
            "                                        \"strByname\": \"1#\", \n" +
            "                                        \"nNoise\": -105,\n" +
            "\t\t\t\t     \"strMac\":\"1dsad\"\n" +
            "},                                 \"nSnr\":42,\n" +
            "\"nQuality\":42,\n" +
            "\"nNoise\":42,\n" +
            "\"nSignal\":42,\n" +
            "\"nRecvMode\":42,\n" +
            "\"nSendMode\":42\n" +
            "                   \n" +
            "                        }]\n" +
            "}";

    private static final String PARAMS = "isMarkShowing";
    RecyclerView rv;
    WebView webview;
    TextView tvEmpty;
    CheckBox showInMap;
    View   llInfo;
    LiteBaseAdapter<TopoDeviceListResp.DeviceInfo> adapter;
    ArrayList<TopoDeviceListResp.DeviceInfo> lstMeshDeviceTop;
    ShowInMapListener listener ;
    List<TopoDeviceGPS>  topoDeviceGPSList ;
    public static TopoDialog getInstance(boolean isMarkShowing) {
        TopoDialog dialog = new TopoDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(PARAMS,isMarkShowing ? 1 : 0);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_topo, container, false);
        webview = view.findViewById(R.id.webview);
        rv = view.findViewById(R.id.rv);
        tvEmpty = view.findViewById(R.id.tv_empty);
        llInfo = view.findViewById(R.id.ll_info);
        showInMap = view.findViewById(R.id.cb_show_in_map);
        Bundle bundle = getArguments();
        int show = bundle.getInt(PARAMS);
        if (show == 1){
            showInMap.setChecked(true);
        }else {
            showInMap.setChecked(false);
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

        lstMeshDeviceTop = new ArrayList<>();
        adapter = new LiteBaseAdapter<>(getContext(),
                lstMeshDeviceTop,
                TopoHolder.class,
                R.layout.item_topo,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        PersonModelBean bean = (PersonModelBean) v.getTag();
//                        toCall(bean);
                    }
                }, null);
        rv.setLayoutManager(new SafeLinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        getTopoInfo();
        getTopoDeviceList();
        showInMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && (topoDeviceGPSList == null || topoDeviceGPSList.size() == 0)){
                    AppUtils.showToast(AppUtils.getString(R.string.topo_empty));
                    showInMap.setChecked(false);
                    return;
                }
                if (listener != null){
                    if (isChecked){
                        listener.onShowInMap(true,topoDeviceGPSList);
                    }else {
                        listener.onShowInMap(false,topoDeviceGPSList);
                    }
                }
            }
        });
    }


    public void showWebView(final TopoInfoResp resp) {
        webview.loadUrl("file:///android_asset/qunee.html");
        webview.getSettings().setJavaScriptEnabled(true);
        //自适应屏幕
//        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//适应屏幕，内容将自动缩放
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS );//适应内容大小
        webview.getSettings().setLoadWithOverviewMode(true);
        // 设置可以支持缩放
        webview.getSettings().setSupportZoom(true);
        // 扩大比例的缩放
        webview.getSettings().setUseWideViewPort(true);
        //设置是否出现缩放工具
        webview.getSettings().setBuiltInZoomControls(false);

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Gson gosn = new Gson();
                String strParams = gosn.toJson(resp);
//                webview.loadUrl("javascript:tptFromAndroid(" + strParams +  ")");
                webview.loadUrl("javascript:tptEvent(" + strParams +  ")");
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), R.style.translucentDialog);
    }

    public void getTopoInfo(){
        if (BuildConfig.DEBUG){
            //todo
            Gson gson = new Gson();
            TopoInfoResp testResp = gson.fromJson(testString,TopoInfoResp.class);
            showWebView(testResp);
            return;
        }

        String URL = AppDatas.Constants().getAddressBaseURL9200() + "sie/httpjson/get_mesh_device_top";
        Https.post(URL)
                .addHeader("Connection", "close")
                .setHttpCallback(new ModelCallback<TopoInfoResp>() {

                    @Override
                    public void onPreStart(HTTPRequest request) {
                        super.onPreStart(request);
                    }

                    @Override
                    public void onSuccess(TopoInfoResp response) {
                        if (response.nResultCode == 0){
                            if (response.lstMeshDeviceTop == null||response.lstMeshDeviceTop.size() == 0){

                            }else{
                                showWebView(response);

                            }


                        }

                    }

                    @Override
                    public void onFailure(HTTPResponse response) {
                        super.onFailure(response);
                        AppUtils.showToast(response.getErrorMessage());
                    }
                })
                .build()
                .requestNowAsync();

    }

    public void getTopoDeviceList(){
        if (BuildConfig.DEBUG){
            testList();
            return;
        }
        String URL = AppDatas.Constants().getAddressBaseURL9200() + "sie/httpjson/get_ad_all_addr";
        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strUserTokenID", AppDatas.Auth().getToken())
                .addParam("strMeshIP","")
                .setHttpCallback(new ModelCallback<TopoDeviceListResp>() {

                    @Override
                    public void onPreStart(HTTPRequest request) {
                        super.onPreStart(request);
                    }

                    @Override
                    public void onSuccess(TopoDeviceListResp response) {
                        if (response.nResultCode == 0){
                            lstMeshDeviceTop.clear();
                            if (response.lstAdDeviceInfo == null||response.lstAdDeviceInfo.size() == 0){
                                showEmpty();
                            }else{
                                lstMeshDeviceTop.clear();
                                lstMeshDeviceTop.addAll(response.lstAdDeviceInfo);
                                adapter.notifyDataSetChanged();
                                getDeviceGps(lstMeshDeviceTop);
                            }
                        }

                    }

                    @Override
                    public void onFailure(HTTPResponse response) {
                        super.onFailure(response);

                            showEmpty();
                            AppUtils.showToast(response.getErrorMessage());

                    }
                })
                .build()
                .requestNowAsync();

    }


    private void showEmpty(){
        llInfo.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
    }


    private void testList(){
        Gson gson = new Gson();
        TopoDeviceListResp testResp = gson.fromJson(testStringList,TopoDeviceListResp.class);
        lstMeshDeviceTop.clear();
        lstMeshDeviceTop.addAll(testResp.lstAdDeviceInfo);
        adapter.notifyDataSetChanged();
    }


    public void setListener(ShowInMapListener listener) {
        this.listener = listener;
    }

    public interface ShowInMapListener{
        void onShowInMap(boolean show, List<TopoDeviceGPS> list);
    }


    private void getDeviceGps(ArrayList<TopoDeviceListResp.DeviceInfo> devices){
        Observable.fromIterable(devices)
                .flatMap(new Function<TopoDeviceListResp.DeviceInfo, ObservableSource<TopoDeviceGPS>>() {
                    @Override
                    public ObservableSource<TopoDeviceGPS> apply(final TopoDeviceListResp.DeviceInfo deviceInfo) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<TopoDeviceGPS>() {
                            @Override
                            public void subscribe(final ObservableEmitter<TopoDeviceGPS> emitter) throws Exception {
                                String URL = AppDatas.Constants().getAddressBaseURL9200() + "sie/httpjson/get_mesh_gps_info";
                                Https.post(URL)
                                        .addHeader("Connection", "close")
                                        .addParam("strMeshIP",deviceInfo.ip)
                                        .setHttpCallback(new ModelCallback<TopoDeviceGPS>() {

                                            @Override
                                            public void onPreStart(HTTPRequest request) {
                                                super.onPreStart(request);
                                            }

                                            @Override
                                            public void onSuccess(TopoDeviceGPS response) {
                                                if (response.nResultCode == 0){
                                                    response.ip = deviceInfo.ip;
                                                    emitter.onNext(response);
                                                }else {
                                                    emitter.onError(new Throwable("resp code " + response.nResultCode));
                                                }
                                                emitter.onComplete();
                                            }

                                            @Override
                                            public void onFailure(HTTPResponse response) {
                                                super.onFailure(response);
                                                emitter.onError(new Throwable(response.getErrorMessage()));
                                                emitter.onComplete();

                                            }
                                        })
                                        .build()
                                        .requestAsync();
                            }
                        });
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<TopoDeviceGPS>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<TopoDeviceGPS> objects) {
                        topoDeviceGPSList = objects;
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private List<TopoDeviceGPS> mockList(){
        ArrayList<TopoDeviceGPS> list = new ArrayList<>();
        TopoDeviceGPS topoDeviceGPS = new TopoDeviceGPS();
        topoDeviceGPS.gpslon= 118.790818 ;
        topoDeviceGPS.gpslat  = 31.992246;
        topoDeviceGPS.ip  = "192.168.1.11";
        list.add(topoDeviceGPS);
        TopoDeviceGPS topoDeviceGPS2 = new TopoDeviceGPS();
        topoDeviceGPS2.gpslon= 118.770818 ;
        topoDeviceGPS2.gpslat  = 31.982246;
        topoDeviceGPS2.ip  = "192.168.1.12";
        list.add(topoDeviceGPS2);
        TopoDeviceGPS topoDeviceGPS3 = new TopoDeviceGPS();
        topoDeviceGPS3.gpslon= 118.750818 ;
        topoDeviceGPS3.gpslat  = 31.932246;
        topoDeviceGPS3.ip  = "192.168.1.13";
        list.add(topoDeviceGPS3);
        TopoDeviceGPS topoDeviceGPS4 = new TopoDeviceGPS();
        topoDeviceGPS4.gpslon= 118.710818 ;
        topoDeviceGPS4.gpslat  = 31.912246;
        topoDeviceGPS4.ip  = "192.168.1.14";
        list.add(topoDeviceGPS4);
        TopoDeviceGPS topoDeviceGPS5 = new TopoDeviceGPS();
        topoDeviceGPS5.gpslon= 118.730818 ;
        topoDeviceGPS5.gpslat  = 31.992246;
        topoDeviceGPS5.ip  = "192.168.1.15";
        list.add(topoDeviceGPS5);
//        double extra = -1;
//        for (int i = 0 ; i < 5 ; i++){
//            extra = -2*0.001*i * extra;
//            TopoDeviceGPS topoDeviceGPS = new TopoDeviceGPS();
//            topoDeviceGPS.gpslon= 118.790818 + 0.001*i*2;
//            topoDeviceGPS.gpslat  = 31.992246+ 0.001*i*2;
//        }
        return list;
    }
}
