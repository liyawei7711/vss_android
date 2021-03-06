package huaiye.com.vvs.ui.meet.viewholder;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.common.recycle.SafeLinearLayoutManager;
import huaiye.com.vvs.models.meet.bean.Bean;
import huaiye.com.vvs.models.meet.bean.PhotoBean;

/**
 * Created by Administrator on 2018\2\27 0027.
 */

public class ChoosePhotoHolder extends LiteViewHolder {

    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.rv_images)
    RecyclerView rv_images;

    public ChoosePhotoHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        rv_images.setLayoutManager(new SafeLinearLayoutManager(context));
        rv_images.setLayoutManager(new GridLayoutManager(context, 4));
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        PhotoBean bean = (PhotoBean) data;

        tv_time.setText(bean.date);

        LiteBaseAdapter<Bean> adapter = new LiteBaseAdapter<>(context,
                bean.photos,
                PhotoHolder.class,
                R.layout.item_photo,
                ocl, null);
        rv_images.setAdapter(adapter);
    }
}
