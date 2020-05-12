package huaiye.com.vvs.ui.meet;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.rx.RxUtils;
import huaiye.com.vvs.dao.MediaFileDao;
import huaiye.com.vvs.models.ModelApis;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.auth.bean.Upload;
import huaiye.com.vvs.models.meet.bean.LocalPhotoBean;
import huaiye.com.vvs.ui.meet.viewholder.LocalPhotoHolder;
import ttyy.com.jinnetwork.core.work.HTTPResponse;


/**
 * author: admin
 * date: 2018/04/23
 * version: 0
 * mail: secret
 * desc: ChoosePhotoActivity
 */

@BindLayout(R.layout.activity_choose_photo)
public class ChoosePhotoAndScreenActivity extends AppBaseActivity {

    @BindView(R.id.rv_data)
    RecyclerView rv_data;

    LiteBaseAdapter<LocalPhotoBean> adapter;

    ArrayList<LocalPhotoBean> arrays = new ArrayList<>();
    SimpleDateFormat sdf;
    LocalPhotoBean currentBean;

    public ChoosePhotoAndScreenActivity() {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initActionBar() {
        getNavigate()
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                .setTitlText(AppUtils.getString(R.string.img))
                .setRightText(AppUtils.getString(R.string.makesure))
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentBean == null) {
                            showToast(AppUtils.getString(R.string.selected_share_img));
                            return;
                        }

                        File file = new File(currentBean.data.replaceFirst("file://", ""));

                        if (HYClient.getSdkOptions().encrypt().isEncryptBind())
                        {
                            HYClient.getModule(ApiEncrypt.class)
                                    .encryptFile(
                                            SdkParamsCenter.Encrypt.EncryptFile()
                                                    .setSrcFile(file.getPath())
                                                    .setDstFile(file.getPath() + ".encrypt")
                                                    .setDoEncrypt(true),
                                            new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                                @Override
                                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                    //tvTip.setText("文件加密成功，输出文件为:" + resp.m_strData);
                                                    File fileEncrypt = new File(resp.m_strData);
                                                    if (fileEncrypt.length() > 1028 * 1028 * 50) {
                                                        showToast(AppUtils.getString(R.string.file_is_bigger_than));
                                                        return;
                                                    }
                                                    mZeusLoadView.loadingText(AppUtils.getString(R.string.is_upload_ing)).setLoading();
                                                    ModelApis.Download().uploadNew(new ModelCallback<Upload>() {
                                                        @Override
                                                        public void onSuccess(Upload upload) {
                                                            if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                                                mZeusLoadView.dismiss();

                                                            if(upload.file1_name == null) {
                                                                showToast(AppUtils.getString(R.string.file_upload_false));
                                                                return;
                                                            }

                                                            Intent intent = new Intent();
                                                            intent.putExtra("updata", upload);
                                                            setResult(Activity.RESULT_OK, intent);
                                                            ChoosePhotoAndScreenActivity.this.finish();
                                                        }

                                                        @Override
                                                        public void onFailure(HTTPResponse httpResponse) {
                                                            super.onFailure(httpResponse);
                                                            showToast(AppUtils.getString(R.string.file_upload_false));
                                                        }

                                                        @Override
                                                        public void onFinish(HTTPResponse httpResponse) {
                                                            if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                                                mZeusLoadView.dismiss();
                                                        }
                                                    }, fileEncrypt);
                                                }

                                                @Override
                                                public void onError(ErrorInfo error) {
                                                    showToast("文件加密失败");
                                                }
                                            }
                                    );
                        }
                        else
                        {
                            if (file.length() > 1028 * 1028 * 50) {
                                showToast(AppUtils.getString(R.string.file_is_bigger_than));
                                return;
                            }
                            mZeusLoadView.loadingText(AppUtils.getString(R.string.is_upload_ing)).setLoading();
                            ModelApis.Download().uploadNew(new ModelCallback<Upload>() {
                                @Override
                                public void onSuccess(Upload upload) {
                                    if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                        mZeusLoadView.dismiss();

                                    if(upload.file1_name == null) {
                                        showToast(AppUtils.getString(R.string.file_upload_false));
                                        return;
                                    }

                                    Intent intent = new Intent();
                                    intent.putExtra("updata", upload);
                                    setResult(Activity.RESULT_OK, intent);
                                    ChoosePhotoAndScreenActivity.this.finish();
                                }

                                @Override
                                public void onFailure(HTTPResponse httpResponse) {
                                    super.onFailure(httpResponse);
                                    showToast(AppUtils.getString(R.string.file_upload_false));
                                }

                                @Override
                                public void onFinish(HTTPResponse httpResponse) {
                                    if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                        mZeusLoadView.dismiss();
                                }
                            }, file);
                        }
                    }
                });
    }

    @Override
    public void doInitDelay() {
        adapter = new LiteBaseAdapter<>(this,
                arrays,
                LocalPhotoHolder.class,
                R.layout.item_photo,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocalPhotoBean bean = (LocalPhotoBean) v.getTag();
                        bean.isChecked = !bean.isChecked;

                        if (currentBean != null && currentBean != bean) {
                            currentBean.isChecked = false;
                        }

                        currentBean = bean;

                        adapter.notifyDataSetChanged();
                    }
                }, null);
        rv_data.setLayoutManager(new GridLayoutManager(this, 4));
        rv_data.setAdapter(adapter);

        loadData();
    }

    /**
     * 加载图片
     */
    private void loadData() {
        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal<ArrayList<LocalPhotoBean>>() {
            @Override
            public ArrayList<LocalPhotoBean> doOnThread() {
                arrays.clear();

                List<MediaFileDao.MediaFile> datas = MediaFileDao.get().getAllImgs();
                for (int i = 0; i < datas.size(); i++) {
                    LocalPhotoBean temp = new LocalPhotoBean();
                    temp.name = datas.get(i).getRecordPath();
                    temp.data = "file://" + datas.get(i).getRecordPath();
                    arrays.add(temp);
                }

                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

                while (cursor.moveToNext()) {
                    LocalPhotoBean temp = new LocalPhotoBean();
                    temp.name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    temp.data = new String(data, 0, data.length - 1);

                    arrays.add(temp);
                }
                return arrays;
            }

            @Override
            public void doOnMain(ArrayList<LocalPhotoBean> data) {
                adapter.notifyDataSetChanged();
            }
        });
    }

}
