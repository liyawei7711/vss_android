package huaiye.com.vvs.ui.home;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import huaiye.com.vvs.MCApp;
import huaiye.com.vvs.R;
import huaiye.com.vvs.models.map.bean.MarkModelBean;
import huaiye.com.vvs.ui.home.present.MainPresent;
import huaiye.com.vvs.ui.home.view.MyMapMarkDialog;

public class TopRightMenuView extends FrameLayout implements View.OnClickListener{
    ImageView ivOfflineMap;
    ImageView ivPrivateDevice;
    ImageView ivMyMap;

    FrameLayout flOfflineMap;
    FrameLayout flPrivateDevice;
    FrameLayout flMyMap;

    MainPresent mainPresent;
    TopRightClickListener listener;


    public TopRightMenuView(Context context) {
        super(context);
        init();
    }

    public TopRightMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopRightMenuView(Context context,  @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.menu_top_right,this,true);
        ivOfflineMap = findViewById(R.id.iv_offline_map);
        flOfflineMap = findViewById(R.id.fl_offline_map);
        ivPrivateDevice = findViewById(R.id.iv_private_device);
        flPrivateDevice = findViewById(R.id.fl_private_device);
        ivMyMap = findViewById(R.id.iv_my_map);
        flMyMap = findViewById(R.id.fl_my_map);



//        ivOfflineMap.setOnClickListener(this);
//        ivPrivateDevice.setOnClickListener(this);
//        ivMyMap.setOnClickListener(this);
        flOfflineMap.setOnClickListener(this);
        flPrivateDevice.setOnClickListener(this);
        flMyMap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_offline_map:
                if (mainPresent != null){
                    mainPresent.downOffLine();
                }
                break;
            case R.id.fl_private_device:
                if (mainPresent != null){
                    mainPresent. showTopo();
                }
                break;
            case R.id.fl_my_map:
                showMyMapMark();
                break;
        }
    }



    private void showMyMapMark(){
        MyMapMarkDialog dialog = new MyMapMarkDialog();
        dialog.setListener(new TopRightClickListener() {
            @Override
            public void clickMark(MarkModelBean bean) {
                if (mainPresent != null){
                    mainPresent.loadMapMark(bean);
                }
            }
        });
        if (mainPresent != null){
            dialog.setShowingCustomMarkIds(mainPresent.getCustomMarkIds());
        }
        dialog.setOnDeleteListener(new MyMapMarkDialog.OnDeleteListener() {
            @Override
            public void onDeleteSuccess() {
                mainPresent.clearAllUserMark();
            }
        });
        dialog.show(MCApp.getInstance().getTopActivity().getSupportFragmentManager(),"myMap");
    }


    public void setMainPresent(MainPresent mainPresent) {
        this.mainPresent = mainPresent;
    }

    public void setListener(TopRightClickListener listener) {
        this.listener = listener;
    }

    public interface TopRightClickListener{
        void clickMark(MarkModelBean bean);
    }
}
