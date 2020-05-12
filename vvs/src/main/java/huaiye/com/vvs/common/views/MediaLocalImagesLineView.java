package huaiye.com.vvs.common.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.io.File;
import java.util.List;

import huaiye.com.vvs.R;
import huaiye.com.vvs.dao.MediaFileDao;
import ttyy.com.jinnetwork.Images;
import ttyy.com.jinnetwork.ext_image.cache.ImageCacheType;

/**
 * author: admin
 * date: 2017/08/15
 * version: 0
 * mail: secret
 * desc: ViewBDLXImagesLine
 */
@BindLayout(R.layout.view_local_images_line)
public class MediaLocalImagesLineView extends LinearLayout implements View.OnClickListener{

    @BindView(R.id.iv_pre_0)
    CheckableImageView iv_pre_0;

    @BindView(R.id.iv_pre_1)
    CheckableImageView iv_pre_1;

    @BindView(R.id.iv_pre_2)
    CheckableImageView iv_pre_2;

    @BindView(R.id.iv_pre_3)
    CheckableImageView iv_pre_3;

    CheckStateCallback mCallback;

    MediaFileDao.MediaFile[] datas;

    public MediaLocalImagesLineView(Context context) {
        this(context, null);
    }

    public MediaLocalImagesLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MediaLocalImagesLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Injectors.get().injectView(this);

        iv_pre_0.setOnClickListener(this);
        iv_pre_1.setOnClickListener(this);
        iv_pre_2.setOnClickListener(this);
        iv_pre_3.setOnClickListener(this);
    }

    public void setLocalImages(List<MediaFileDao.MediaFile> choosed, MediaFileDao.MediaFile... modules){
        if(modules == null
                || modules.length == 0){
            setVisibility(View.GONE);
            return;
        }else {
            setVisibility(View.VISIBLE);
        }

        datas = modules;
        iv_pre_0.setVisibility(View.INVISIBLE);
        iv_pre_1.setVisibility(View.INVISIBLE);
        iv_pre_2.setVisibility(View.INVISIBLE);
        iv_pre_3.setVisibility(View.INVISIBLE);
        for(int i = 0 ; i <modules.length ; i++){
            CheckableImageView curr_imageview = null;
            switch (i){
                case 0:
                    curr_imageview = iv_pre_0;
                    break;
                case 1:
                    curr_imageview = iv_pre_1;
                    break;
                case 2:
                    curr_imageview = iv_pre_2;
                    break;
                case 3:
                    curr_imageview = iv_pre_3;
                    break;
            }

            if(curr_imageview.isInCheckMode()){
                curr_imageview.setChecked(choosed.contains(modules[i]));
            }
            curr_imageview.setVisibility(View.VISIBLE);
            Images.get()
                    .source("file://"+modules[i].getRecordPath())
                    .useCache(ImageCacheType.AllCache)
                    .placeholder(R.drawable.shape_image_pre)
                    .error(R.drawable.shape_image_error)
                    .into(curr_imageview);
        }
    }

    public void setOpenCheck(boolean isOpen){
        if(isOpen){
            iv_pre_0.setModeCheck();
            iv_pre_1.setModeCheck();
            iv_pre_2.setModeCheck();
            iv_pre_3.setModeCheck();
        }else {
            iv_pre_0.setModeNone();
            iv_pre_1.setModeNone();
            iv_pre_2.setModeNone();
            iv_pre_3.setModeNone();
        }
    }

    public void setCallback(CheckStateCallback callback){
        this.mCallback = callback;
    }

    @Override
    public void onClick(View v) {
        if(v.getVisibility() != View.VISIBLE){
            return;
        }

        switch (v.getId()){
            case R.id.iv_pre_0:

                if(iv_pre_0.isInCheckMode()){
                    iv_pre_0.setChecked(!iv_pre_0.isChecked()).invalidate();
                    if(mCallback != null){
                        mCallback.onCheckedChanged(datas[0], iv_pre_0.isChecked());
                    }
                }else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(datas[0].getRecordPath())), "image/png");
                    getContext().startActivity(intent);
                }

                break;
            case R.id.iv_pre_1:

                if(iv_pre_1.isInCheckMode()){
                    iv_pre_1.setChecked(!iv_pre_1.isChecked()).invalidate();
                    if(mCallback != null){
                        mCallback.onCheckedChanged(datas[1], iv_pre_1.isChecked());
                    }
                }else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(datas[1].getRecordPath())), "image/png");
                    getContext().startActivity(intent);
                }

                break;
            case R.id.iv_pre_2:

                if(iv_pre_2.isInCheckMode()){
                    iv_pre_2.setChecked(!iv_pre_2.isChecked()).invalidate();
                    if(mCallback != null){
                        mCallback.onCheckedChanged(datas[2], iv_pre_2.isChecked());
                    }
                }else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(datas[2].getRecordPath())), "image/png");
                    getContext().startActivity(intent);
                }

                break;
            case R.id.iv_pre_3:

                if(iv_pre_3.isInCheckMode()){
                    iv_pre_3.setChecked(!iv_pre_3.isChecked()).invalidate();
                    if(mCallback != null){
                        mCallback.onCheckedChanged(datas[3], iv_pre_3.isChecked());
                    }
                }else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(datas[3].getRecordPath())), "image/png");
                    getContext().startActivity(intent);
                }

                break;
        }
    }

    public interface CheckStateCallback{

        void onCheckedChanged(MediaFileDao.MediaFile module, boolean isChecked);

    }
}
