package huaiye.com.vvs.ui.home.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.dao.msgs.VssMessageBean;

import static huaiye.com.vvs.common.AppUtils.BROADCAST_AUDIO_FILE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.BROADCAST_AUDIO_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.BROADCAST_VIDEO_TYPE_INT;

/**
 * Created by Administrator on 2017/6/29 0029.
 * 显示图片的adapter
 */

public class HomeViewPagerAdapter extends PagerAdapter {
    ArrayList<VssMessageBean> datas;
    Context mActivity;
    HomeViewPagerListener homeViewPagerListener;

    public HomeViewPagerAdapter(ArrayList<VssMessageBean> imgData, Context activity) {
        datas = imgData;
        mActivity = activity;
    }

    public void setHomeViewPagerListener(HomeViewPagerListener homeViewPagerListener){
        this.homeViewPagerListener=homeViewPagerListener;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View) object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
//        int itemPosition = position % imageViews.size();
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_broadcast_msg_layout, null);
        ImageView iv_close = (ImageView) view.findViewById(R.id.iv_close);
        TextView tv_notice_msg_contant_show = (TextView) view.findViewById(R.id.tv_notice_msg_contant_show);
        TextView tv_notice_msg_show = (TextView) view.findViewById(R.id.tv_notice_msg_show);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (homeViewPagerListener!=null)
                    homeViewPagerListener.itemPagerClick(position);
            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (homeViewPagerListener!=null)
                    homeViewPagerListener.itemClose(position);
            }
        });
        VssMessageBean bean = datas.get(position);
        tv_notice_msg_show.setText(bean.fromUserName + "(" + bean.fromUserId + ")");
        if (bean.type == BROADCAST_AUDIO_TYPE_INT || bean.type == BROADCAST_AUDIO_FILE_TYPE_INT) {
            tv_notice_msg_contant_show.setText("[" + AppUtils.getString(R.string.audio) + "]:" + bean.content);
        } else if (bean.type == BROADCAST_VIDEO_TYPE_INT) {
            tv_notice_msg_contant_show.setText("[" + AppUtils.getString(R.string.video) + "]:" + bean.content);
        }else if (bean.type == AppUtils.ZHILING_IMG_TYPE_INT) {
            tv_notice_msg_contant_show.setText("[" + AppUtils.getString(R.string.img) + "]");
        } else if (bean.type == AppUtils.ZHILING_FILE_TYPE_INT) {
            tv_notice_msg_contant_show.setText("[" + AppUtils.getString(R.string.notice_file) + "]");
        } else if (bean.type == AppUtils.PLAYER_TYPE_PERSON_INT ||
                bean.type == AppUtils.PLAYER_TYPE_DEVICE_INT) {
            tv_notice_msg_contant_show.setText("[" + AppUtils.getString(R.string.notice_share) + "]");
        } else {
            tv_notice_msg_contant_show.setText(bean.content);
        }
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
