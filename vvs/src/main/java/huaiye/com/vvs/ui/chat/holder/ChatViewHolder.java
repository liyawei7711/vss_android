package huaiye.com.vvs.ui.chat.holder;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import huaiye.com.vvs.BuildConfig;
import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.msgs.VssMessageBean;
import huaiye.com.vvs.ui.chat.ChatPlayHelper;

import static huaiye.com.vvs.common.AppUtils.BROADCAST_AUDIO_FILE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.BROADCAST_AUDIO_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.BROADCAST_VIDEO_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_DEVICE_INT;
import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_PERSON_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_CODE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_FILE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_IMG_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: ChatViewHolder
 */

public class ChatViewHolder extends LiteViewHolder {


    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.rl_right)
    View rl_right;
    @BindView(R.id.right_name)
    TextView right_name;
    @BindView(R.id.my_msg_content)
    TextView my_msg_content;
    @BindView(R.id.right_img)
    ImageView right_img;

    @BindView(R.id.rl_left)
    View rl_left;
    @BindView(R.id.left_name)
    TextView left_name;
    @BindView(R.id.text_left)
    TextView text_left;
    @BindView(R.id.left_img)
    ImageView left_img;
    @BindView(R.id.void_img)
    ImageView void_img;

    File fCache = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/files/chat");

    public ImageView getVoid_img() {
        return void_img;
    }

    AnimationDrawable mImageAnim;

    public AnimationDrawable getmImageAnim() {
        return mImageAnim;
    }

    public void setmImageAnim(AnimationDrawable mImageAnim) {
        this.mImageAnim = mImageAnim;
    }

    public ChatViewHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        text_left.setOnClickListener(ocl);
    }


    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        final VssMessageBean bean = (VssMessageBean) data;
        HashMap<String, Boolean> playerMap = ChatPlayHelper.get().getPlayMap();
        text_left.setTag(R.id.tag_bean, bean);
        text_left.setTag(R.id.tag_holder, holder);
        text_left.setTag(R.id.tag_position, position);

        if (position == 0) {
            time.setVisibility(View.VISIBLE);
        } else if (((VssMessageBean) datas.get(position - 1)).getTime().equals(bean.getTime())) {
            time.setVisibility(View.GONE);
        } else {
            time.setVisibility(View.VISIBLE);
        }
        time.setText(bean.getTime());
        void_img.setVisibility(View.GONE);
        if (bean.fromUserId.equals(AppDatas.Auth().getUserID() + "")) {
            rl_right.setVisibility(View.VISIBLE);
            rl_left.setVisibility(View.GONE);
            right_name.setText(AppDatas.Auth().getUserName());

            if (bean.type == ZHILING_IMG_TYPE_INT) {
                my_msg_content.setVisibility(View.GONE);
                right_img.setVisibility(View.VISIBLE);
                Log.i("MCApp_tt", "receive msg"+bean.content);
                final File fC = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/files/chat/");
                fC.mkdir();
                //判断是否需要解密
                if (bean.nEncrypt == 1 && HYClient.getSdkOptions().encrypt().isEncryptBind())
                {
                    new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final String fileLocal = fC + bean.content.substring(bean.content.lastIndexOf("/"));
                                if (downloadFileByUrl(AppDatas.Constants().getAddressWithoutPort() + bean.content,fileLocal))
                                {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            File fde = new File(fileLocal.substring(0,fileLocal.lastIndexOf(".")));
                                            if (fde.exists())
                                            {
                                                Glide.with(context)
                                                        .asBitmap()
                                                        .load(fde)
                                                        .apply(new RequestOptions().fitCenter())
                                                        .into(right_img);
                                            }
                                            else
                                            {
                                                HYClient.getModule(ApiEncrypt.class)
                                                        .encryptFile(
                                                                SdkParamsCenter.Encrypt.EncryptFile()
                                                                        .setSrcFile(fileLocal)
                                                                        .setDstFile(fde.getPath())
                                                                        .setDoEncrypt(false),
                                                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                                                    @Override
                                                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                                        Log.i("MCApp_tt", "resp: " + resp.m_strData);
                                                                        Glide.with(context)
                                                                                .asBitmap()
                                                                                .load(resp.m_strData)
                                                                                .apply(new RequestOptions().fitCenter())
                                                                                .into(right_img);
                                                                    }

                                                                    @Override
                                                                    public void onError(ErrorInfo error) {
                                                                        showToast(AppUtils.getString(R.string.chat_decrypt_fail));
                                                                    }
                                                                }
                                                        );
                                            }
                                        }
                                    });

                                }
                                else
                                {
                                    //下载失败
                                }
                            }
                    }).start();
                }
                else
                {
                    Glide.with(context)
                            .asBitmap()
                            .load(AppDatas.Constants().getAddressWithoutPort() + bean.content)
                            .apply(new RequestOptions().fitCenter())
                            .into(right_img);
                }
            } else if (bean.type == ZHILING_FILE_TYPE_INT) {
                my_msg_content.setVisibility(View.VISIBLE);
                right_img.setVisibility(View.GONE);
                my_msg_content.setText(AppUtils.getString(R.string.notice_file) + ":" + bean.content.substring(bean.content.lastIndexOf("/") + 1));
            } else {
                my_msg_content.setVisibility(View.VISIBLE);
                right_img.setVisibility(View.GONE);
                if (TextUtils.isEmpty(bean.contentSrc))
                {
                    //判断是否需要解密
                    if (bean.nEncrypt == 1 && HYClient.getSdkOptions().encrypt().isEncryptBind())
                    {
                            HYClient.getModule(ApiEncrypt.class)
                                    .encryptTextMsg(
                                            SdkParamsCenter.Encrypt.EncryptTextMsg()
                                                    .setM_strData(bean.content)
                                                    .setDoEncrypt(false),
                                            new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                                @Override
                                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                    my_msg_content.setText(resp.m_strData);
                                                }

                                                @Override
                                                public void onError(ErrorInfo error) {
                                                    my_msg_content.setText(bean.content);
                                                }
                                            }
                                    );
                     }
                     else
                    {
                        my_msg_content.setText(bean.content);
                    }
                }
                else
                {
                    my_msg_content.setText(bean.contentSrc);
                }
            }

        } else {
            rl_right.setVisibility(View.GONE);
            rl_left.setVisibility(View.VISIBLE);
            left_name.setText(bean.fromUserName);

            if (bean.type == BROADCAST_AUDIO_TYPE_INT || bean.type == BROADCAST_AUDIO_FILE_TYPE_INT) {
                text_left.setVisibility(View.VISIBLE);
                left_img.setVisibility(View.GONE);
                text_left.setText("[" + AppUtils.getString(R.string.audio) + "]:" + bean.content);
                void_img.setVisibility(View.VISIBLE);
            } else if (bean.type == BROADCAST_VIDEO_TYPE_INT) {
                text_left.setVisibility(View.VISIBLE);
                left_img.setVisibility(View.GONE);
                text_left.setText("[" + AppUtils.getString(R.string.video) + "]:" + bean.content);
            } else if (bean.type == ZHILING_IMG_TYPE_INT) {
                text_left.setVisibility(View.GONE);
                left_img.setVisibility(View.VISIBLE);
                final File fC = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/files/chat/");
                fC.mkdir();
                //判断是否需要解密
                if (bean.nEncrypt == 1 && HYClient.getSdkOptions().encrypt().isEncryptBind())
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String fileLocal = fC + bean.content.substring(bean.content.lastIndexOf("/"));
                            if (downloadFileByUrl(AppDatas.Constants().getAddressWithoutPort() + bean.content,fileLocal))
                            {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        File fde = new File(fileLocal.substring(0,fileLocal.lastIndexOf(".")));
                                        if (fde.exists())
                                        {
                                            Glide.with(context)
                                                    .asBitmap()
                                                    .load(fde)
                                                    .apply(new RequestOptions().fitCenter())
                                                    .into(left_img);
                                        }
                                        else
                                        {
                                            HYClient.getModule(ApiEncrypt.class)
                                                    .encryptFile(
                                                            SdkParamsCenter.Encrypt.EncryptFile()
                                                                    .setSrcFile(fileLocal)
                                                                    .setDstFile(fde.getPath())
                                                                    .setDoEncrypt(false),
                                                            new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                                                @Override
                                                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                                    Glide.with(context)
                                                                            .asBitmap()
                                                                            .load(resp.m_strData)
                                                                            .apply(new RequestOptions().fitCenter())
                                                                            .into(left_img);
                                                                }

                                                                @Override
                                                                public void onError(ErrorInfo error) {
                                                                    showToast(AppUtils.getString(R.string.chat_decrypt_fail));
                                                                }
                                                            }
                                                    );
                                        }
                                    }
                                });

                            }
                            else
                            {
                                //下载失败
                                showToast(AppUtils.getString(R.string.chat_download_fail));
                            }
                        }
                    }).start();
                }
                else
                {
                    Glide.with(context)
                            .asBitmap()
                            .load(AppDatas.Constants().getAddressWithoutPort() + bean.content)
                            .apply(new RequestOptions().fitCenter())
                            .into(left_img);
                }
            } else if (bean.type == ZHILING_FILE_TYPE_INT) {
                text_left.setVisibility(View.VISIBLE);
                left_img.setVisibility(View.GONE);
                if (0 == bean.nEncrypt)
                {
                    text_left.setText(AppUtils.getString(R.string.notice_file) + ":" + bean.content.substring(bean.content.lastIndexOf("/") + 1));
                }
                else
                {
                    text_left.setText( AppUtils.getString(R.string.chat_already_encrypt) +":" + bean.content.substring(bean.content.lastIndexOf("/") + 1, bean.content.lastIndexOf(".")));
                }
            } else if (bean.type == PLAYER_TYPE_PERSON_INT || bean.type == PLAYER_TYPE_DEVICE_INT) {
                text_left.setVisibility(View.VISIBLE);
                left_img.setVisibility(View.GONE);
//                text_left.setHint(bean.content);
                if (bean.type == PLAYER_TYPE_PERSON_INT) {
                    text_left.setText("[" + AppUtils.getString(R.string.person_share) + "]");
                } else {
                    text_left.setText("[" + AppUtils.getString(R.string.device_share) + "]");
                }
            } else {
                text_left.setVisibility(View.VISIBLE);
                left_img.setVisibility(View.GONE);

                //判断是否需要解密
                if (bean.nEncrypt == 1 && HYClient.getSdkOptions().encrypt().isEncryptBind())
                {
                    HYClient.getModule(ApiEncrypt.class)
                            .encryptTextMsg(
                                    SdkParamsCenter.Encrypt.EncryptTextMsg()
                                            .setM_strData(bean.content)
                                            .setDoEncrypt(false),
                                    new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                        @Override
                                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                            text_left.setText(resp.m_strData);
                                        }

                                        @Override
                                        public void onError(ErrorInfo error) {
                                            text_left.setText(bean.content);
                                        }
                                    }
                            );
                }
                else
                {
                    text_left.setText(bean.content);
                }
            }

        }

        if (!TextUtils.isEmpty(AppUtils.subPath(bean.content)) && playerMap.containsKey(AppUtils.subPath(bean.content)) && playerMap.get(AppUtils.subPath(bean.content))) {
            void_img.setImageResource(R.drawable.record_voice_anim);
            AnimationDrawable mImageAnim = (AnimationDrawable) void_img.getDrawable();
            mImageAnim.start();
            setmImageAnim(mImageAnim);
        } else {
            if (void_img.getDrawable() instanceof AnimationDrawable) {
                ((AnimationDrawable) void_img.getDrawable()).stop();
                void_img.setImageResource(R.drawable.ic_chat_voice);
            }
        }


    }

    private void startAnimation(final ChatViewHolder viewHolder) {
        viewHolder.getVoid_img().setImageResource(R.drawable.record_voice_anim);
        AnimationDrawable mImageAnim = (AnimationDrawable) viewHolder.getVoid_img().getDrawable();
        mImageAnim.start();
        viewHolder.setmImageAnim(mImageAnim);
    }

    private boolean downloadFileByUrl(final String urlLoadPath,final String fileName) {
        Log.i("MCApp_tt", "urlLoadPath: " + urlLoadPath + "  fileName:" + fileName);
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        HttpURLConnection httpURLConnection = null;

        //创建 这个文件名 命名的 file 对象
        File file = new File(fileName);
        // Log.i(TAG,"file: " + file);
        if (!file.exists()) {     //倘若没有这个文件
            // Log.i(TAG,"创建文件");
            //file.createNewFile();  //创建这个文件
        } else {
            //文件已存在，不重新下载
            return true;
        }
        try {
            URL url = new URL(urlLoadPath);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();
            if (code == 200) {
                //网络连接成功
                //根据响应获取文件大小
                int fileSize = httpURLConnection.getContentLength();
                // Log.i(TAG,"文件大小： " + fileSize);
                inputStream = httpURLConnection.getInputStream();
                fileOutputStream = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int tem = 0;
                while ((tem = inputStream.read(b)) != -1) {
                    fileOutputStream.write(b, 0, tem);
                }
                
            } else {
                return false;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
            if (inputStream != null) {
                inputStream.close();
            }

            if (fileOutputStream != null) {
                fileOutputStream.close();
            }

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
        //Log.i(TAG,"文件下载 成功");
        return true;
    }


}
