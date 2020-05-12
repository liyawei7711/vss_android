package huaiye.com.vvs.ui.home.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import huaiye.com.vvs.R;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: ActionBarLayout
 */

public class LeftMenuLayout extends FrameLayout implements View.OnClickListener {

//    View fl_data;
    View tv_dingwei;
    public View tv_sos_qiuzhu;
//    RecyclerView rv_data;

//    ArrayList<MarkModelBean> markModelBeans = new ArrayList<>();
//    Map<Integer, String> all = new HashMap<>();
//    Map<Integer, String> push_all = new HashMap<>();
//    LiteBaseAdapter<MarkModelBean> adapter;
    OnLeftClickListener listener;
//    MainPresent mainPresent;

    public LeftMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LeftMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);

        View view = LayoutInflater.from(context).inflate(R.layout.main_left_menu_layout, null);

//        fl_data = view.findViewById(R.id.fl_data);
//        rv_data = view.findViewById(R.id.rv_data);
        tv_dingwei = view.findViewById(R.id.tv_dingwei);
        tv_sos_qiuzhu = view.findViewById(R.id.tv_sos_qiuzhu);

//        adapter = new LiteBaseAdapter<>(getContext(),
//                markModelBeans,
//                MarkShowHolder.class,
//                R.layout.item_mark_show,
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        MarkModelBean bean = (MarkModelBean) v.getTag();
//                        if (!all.containsKey(bean.nMarkID)) {
//                            all.put(bean.nMarkID, bean.strMarkName);
//                            bean.isChoose = true;
//                        } else {
//                            all.remove(bean.nMarkID);
//                            bean.isChoose = false;
//                        }
//
//                        if (listener != null) {
//                            listener.clickMark(bean);
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                }, all);
//        rv_data.setLayoutManager(new LinearLayoutManager(getContext()));
//        rv_data.setAdapter(adapter);
        addView(view);

        tv_dingwei.setOnClickListener(this);
        tv_sos_qiuzhu.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
//            case R.id.tv_map_mark:
//                MyMapMarkDialog dialog = new MyMapMarkDialog();
//                dialog.setListener(listener);
//                if (mainPresent != null){
//                    dialog.setShowingCustomMarkIds(mainPresent.getCustomMarkIds());
//                }
//                dialog.show(MCApp.getInstance().getTopActivity().getSupportFragmentManager(),"myMap");
//                break;
            case R.id.tv_dingwei:
                listener.onDingWeiClick();
                break;
            case R.id.tv_sos_qiuzhu:
                listener.onSoSClick();
                break;
        }
    }



    public void setListener(OnLeftClickListener listener) {
        this.listener = listener;
    }

    public void showLocation(int visible) {
        tv_dingwei.setVisibility(visible);
    }


//    public void setMainPresent(MainPresent mainPresent) {
//        this.mainPresent = mainPresent;
//    }

    //    public void dealMapMark(MapMarkBean bean) {
//
//        if (bean.nMsgType == 1) {//add
//            if (!push_all.containsKey(bean.nMarkID)) {
//                push_all.put(bean.nMarkID, bean.strMarkName);
//            }
//
//            for (MarkModelBean temp : markModelBeans) {
//                if (temp.nMarkID == bean.nMarkID) {
//
//                    temp.isChoose = !temp.isChoose;
//                    if (temp.isChoose) {
//                        if (!all.containsKey(bean.nMarkID)) {
//                            all.put(bean.nMarkID, bean.strMarkName);
//                        }
//                    } else {
//                        if (all.containsKey(bean.nMarkID)) {
//                            all.remove(bean.nMarkID);
//                        }
//                    }
//                    if (listener != null) {
//                        listener.clickMark(temp);
//                    }
//                    adapter.notifyDataSetChanged();
//                    break;
//                }
//            }
//        } else if (bean.nMsgType == 2) {//delete
//            if (push_all.containsKey(bean.nMarkID)) {
//                push_all.remove(bean.nMarkID);
//            }
//
//            for (MarkModelBean temp : markModelBeans) {
//                if (temp.nMarkID == bean.nMarkID) {
//
//                    temp.isChoose = false;
//                    if (all.containsKey(bean.nMarkID)) {
//                        all.remove(bean.nMarkID);
//                    }
//                    if (listener != null) {
//                        listener.clickMark(temp);
//                    }
//                    markModelBeans.remove(temp);
//                    adapter.notifyDataSetChanged();
//                    break;
//                }
//            }
//
//        }
//
//    }

    public interface OnLeftClickListener {
        void onGpsClick();

        void onDingWeiClick();

        void onSoSClick();

//        void clickMark(MarkModelBean bean);
    }

}
