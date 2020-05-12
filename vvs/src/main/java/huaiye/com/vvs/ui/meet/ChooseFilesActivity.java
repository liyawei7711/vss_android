package huaiye.com.vvs.ui.meet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.whiteboard.CStopWhiteboardShareRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.models.ModelApis;
import huaiye.com.vvs.models.ModelCallback;
import huaiye.com.vvs.models.auth.bean.Upload;
import huaiye.com.vvs.models.meet.bean.FileBean;
import huaiye.com.vvs.ui.meet.viewholder.FileHolder;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vvs.common.AppUtils.rootPath;

/**
 * author: admin
 * date: 2018/04/23
 * version: 0
 * mail: secret
 * desc: ChoosePhotoActivity
 */

@BindLayout(R.layout.activity_choose_photo)
public class ChooseFilesActivity extends AppBaseActivity {

    @BindView(R.id.rv_data)
    RecyclerView rv_data;

    @BindExtra
    public int nMeetID = 0;

    LiteBaseAdapter<FileBean> adapter;

    ArrayList<FileBean> arrays = new ArrayList<>();
    ArrayList<String> types = new ArrayList<>();
    FileBean currentBean;
    String currentPath;

    LinkedHashMap<String, ArrayList<FileBean>> map = new LinkedHashMap<>();
    LinkedHashMap<String, Integer> mapIndex = new LinkedHashMap<>();

    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        types.add("docx");
        types.add("doc");
        types.add("pdf");
        types.add("xls");
        types.add("xlsx");
        types.add("ppt");
        types.add("pptx");
        types.add("log");
        types.add("txt");
        types.add("java");

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate()
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stop();
                        finish();
                    }
                })
                .setTitlText(AppUtils.getString(R.string.share_file))
                .setRightText(AppUtils.getString(R.string.makesure))
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentBean == null) {
                            showToast(AppUtils.getString(R.string.selected_share_file));
                            return;
                        }
                        File file = new File(currentBean.path);

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
                                                    ModelApis.Download().upload(new ModelCallback<Upload>() {
                                                        @Override
                                                        public void onSuccess(Upload upload) {
                                                            if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                                                mZeusLoadView.dismiss();

                                                            Intent intent = new Intent();
                                                            intent.putExtra("updata", upload);
                                                            setResult(Activity.RESULT_OK, intent);
                                                            ChooseFilesActivity.this.finish();
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
                            ModelApis.Download().upload(new ModelCallback<Upload>() {
                                @Override
                                public void onSuccess(Upload upload) {
                                    if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                        mZeusLoadView.dismiss();

                                    Intent intent = new Intent();
                                    intent.putExtra("updata", upload);
                                    setResult(Activity.RESULT_OK, intent);
                                    ChooseFilesActivity.this.finish();
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
                FileHolder.class,
                R.layout.item_file,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FileBean bean = (FileBean) v.getTag();
                        if (bean.isFile) {
                            bean.isChecked = !bean.isChecked;
                            if (currentBean != null && currentBean != bean) {
                                currentBean.isChecked = false;
                            }
                            currentBean = bean;
                            adapter.notifyDataSetChanged();
                        } else {
                            mapIndex.put(bean.mapKey, linearLayoutManager.findLastVisibleItemPosition());
                            getFiles(bean.parent, bean.name);
                        }
                    }
                }, null);
        rv_data.setLayoutManager(linearLayoutManager = new LinearLayoutManager(this));
        rv_data.setAdapter(adapter);

        getFiles(rootPath, "");
    }

    /**
     * 加载文档
     *
     * @param path
     * @param current
     */
    public void getFiles(String path, String current) {
        File file = new File(path, current);
        currentPath = file.toString();
        ArrayList<FileBean> arrayList = new ArrayList<>();
        if (!map.containsKey(file.toString())) {

            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile() && !types.contains(f.getName().substring(f.getName().lastIndexOf(".") + 1))) {
                    continue;
                }
                FileBean bean = new FileBean();
                bean.name = f.getName();
                bean.path = f.getPath();
//                bean.end = f.getName().substring(f.getName().lastIndexOf("."));
                bean.parent = f.getParent();
                bean.isFile = f.isFile();
                bean.mapKey = file.toString();
                arrayList.add(bean);
            }
            Collections.sort(arrayList, new Comparator<FileBean>() {
                @Override
                public int compare(FileBean o1, FileBean o2) {
                    if (o1.isFile && !o2.isFile) return 1;
                    if (!o1.isFile && o2.isFile) return -1;
                    if (o1.isFile && o2.isFile) return 0;
                    return 0;
                }
            });
            map.put(file.toString(), arrayList);
        } else {
            arrayList = map.get(file.toString());
        }
        arrays.clear();
        arrays.addAll(arrayList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (rootPath.equals(currentPath)) {
            stop();
            super.onBackPressed();
        } else {
            String lastPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
            getFiles(lastPath, "");

            if (mapIndex.containsKey(lastPath)) {
                int index = mapIndex.get(lastPath);
                linearLayoutManager.scrollToPosition(index);
            }
        }
    }

    private void stop() {
        if (nMeetID != 0) {
            HYClient.getModule(ApiMeet.class)
                    .stopWhiteBoard(SdkParamsCenter.Meet.StopWhiteBoard().setnMeetingID(nMeetID),
                            new SdkCallback<CStopWhiteboardShareRsp>() {
                                @Override
                                public void onSuccess(CStopWhiteboardShareRsp cStopWhiteboardShareRsp) {
                                }

                                @Override
                                public void onError(ErrorInfo errorInfo) {
                                }
                            });
        }
    }

}
