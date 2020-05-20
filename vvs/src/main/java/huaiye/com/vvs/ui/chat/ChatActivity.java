package huaiye.com.vvs.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.UUID;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.SafeLinearLayoutManager;
import huaiye.com.vvs.dao.AppDatas;
import huaiye.com.vvs.dao.auth.AppAuth;
import huaiye.com.vvs.dao.msgs.VssMessageBean;
import huaiye.com.vvs.dao.msgs.VssMessageListBean;
import huaiye.com.vvs.models.auth.bean.Upload;
import huaiye.com.vvs.ui.chat.iview.IChatView;
import huaiye.com.vvs.ui.chat.present.ChatPresent;
import huaiye.com.vvs.ui.meet.ChooseFilesActivity;
import huaiye.com.vvs.ui.meet.ChoosePhotoAndScreenActivity;

import static huaiye.com.vvs.common.AppUtils.PLAYER_TYPE_PERSON_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_CODE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_FILE_TYPE_INT;
import static huaiye.com.vvs.common.AppUtils.ZHILING_IMG_TYPE_INT;


/**
 * Created by 123 on 17/5/26.
 */
@BindLayout(R.layout.activity_chat)
public class ChatActivity extends AppBaseActivity implements IChatView {

    @BindView(R.id.message_list)
    RecyclerView message_list;
    @BindView(R.id.chat_input_box)
    EditText chat_input_box;
    @BindView(R.id.btn_send)
    Button btn_send;
    @BindView(R.id.btn_send_img)
    View btn_send_img;
    @BindView(R.id.btn_send_file)
    View btn_send_file;
    @BindView(R.id.ll_input)
    View ll_input;
    @BindView(R.id.ll_expend)
    View ll_expend;

    @BindExtra
    ArrayList<SendUserBean> sessionUserList;
    @BindExtra
    VssMessageListBean listBean;
    @BindExtra
    String channelName;
    @BindExtra
    String channelId;
    @BindExtra
    String sessionDomainCode;
    @BindExtra
    boolean isGroup;

    private SafeLinearLayoutManager layoutManager;
    private InputMethodManager imm;
    private int mIndex;
    private boolean move;
    private ChatPresent present;

    private String sessionID;
    private String name;

    @Override
    protected void initActionBar() {
        sessionID = AppDatas.Auth().getUserID() + "_" + UUID.randomUUID();
        if (!TextUtils.isEmpty(channelId)) {
            sessionID = channelId;
        }


        //说明本地已经有该会话了,直接复用
        if (listBean != null) {
            isGroup = listBean.groupType == 2;

            sessionID = listBean.sessionID;
        }

        name = getChatName();
        getNavigate().setTitlText(name)
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

        present = new ChatPresent(this, sessionID);
        present.startListener();
        message_list.setLayoutManager(layoutManager = new SafeLinearLayoutManager(this));
        message_list.setAdapter(present.getAdapter());

        message_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //在这里进行第二次滚动（最后的100米！）
                if (move) {
                    move = false;
                    //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                    int n = mIndex - message_list.getChildAdapterPosition(message_list.getChildAt(0));
                    if (0 <= n && n < message_list.getChildCount()) {
                        //获取要置顶的项顶部离RecyclerView顶部的距离
                        int top = message_list.getChildAt(n).getTop();
                        //最后的移动
                        message_list.scrollBy(0, top);
                    }
                }
            }

        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat_input_box.getText().toString().length() == 0) {
                    return;
                }
                if (isGroup) {
                    present.onSendClickedGroup(sessionID, name, listBean != null ? listBean.groupDomainCode : sessionDomainCode, chat_input_box.getText().toString(), ZHILING_CODE_TYPE_INT);
                } else {
                    //虽然进来了聊天界面,但是一直没有通知服务器创建会话,如果listBean为null就是新的聊天会话,sessionUserList为null就是已经创建过的会话
                    //关注name,这个就是聊天的sessionName,是自己创建的
                    present.onSendClicked(sessionID, name, sessionDomainCode, chat_input_box.getText().toString(), sessionUserList, listBean, ZHILING_CODE_TYPE_INT);
                }
                chat_input_box.setText("");
            }
        });
        btn_send_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChoosePhotoAndScreenActivity.class);
                ((AppBaseActivity) getContext()).startActivityForResult(intent, 1001);
            }
        });

        btn_send_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChooseFilesActivity.class);
                ((AppBaseActivity) getContext()).startActivityForResult(intent, 1002);
            }
        });

        moveToPosition(present.getAdapter().getItemCount() - 1);
    }

    @Override
    public void doInitDelay() {
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        ImageView ivImgSafe = (ImageView)findViewById(R.id.image_safe);
        ViewGroup.LayoutParams para;
        para = ivImgSafe.getLayoutParams();
        if (HYClient.getSdkOptions().encrypt().isEncryptBind())
        {
            para.height = 40;
        }
        else
        {
            para.height = 0;
        }

        ivImgSafe.setLayoutParams(para);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001 || requestCode == 1002) {

                Upload upload = (Upload) data.getSerializableExtra("updata");

                if (isGroup) {
                    present.onSendClickedGroup(sessionID, name, listBean != null ? listBean.groupDomainCode : sessionDomainCode, upload.file1_name, requestCode == 1001 ? ZHILING_IMG_TYPE_INT : ZHILING_FILE_TYPE_INT);
                } else {
                    present.onSendClicked(sessionID, name, sessionDomainCode, upload.file1_name, sessionUserList, listBean, requestCode == 1001 ? ZHILING_IMG_TYPE_INT : ZHILING_FILE_TYPE_INT);
                }
            }
        }
    }

    @Override
    public void moveToPosition(int position) {
        mIndex = position;
        layoutManager = (SafeLinearLayoutManager) message_list.getLayoutManager();
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = layoutManager.findFirstVisibleItemPosition();
        int lastItem = layoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (position <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            message_list.scrollToPosition(position);
        } else if (position <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int top = message_list.getChildAt(position - firstItem).getTop();
            message_list.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            message_list.scrollToPosition(position);
            move = true;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(VssMessageBean obj) {
        //接到群组的人员变化后刷新界面
        if (obj.sessionID.equals(sessionID) && !obj.fromUserId.equals(AppDatas.Auth().getUserID() + "")) {
            present.onReceiverMsg(obj);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }


    private String getChatName() {
        String name = null;
        if (listBean != null && listBean.type == PLAYER_TYPE_PERSON_INT) {
            return listBean.sessionName;
        }
        //是组
        if (isGroup) {
            if (!TextUtils.isEmpty(channelName)) {
                //创建的时候channelName是有的
                name = channelName + AppUtils.getString(R.string.zhiling_diaodu);
            } else if (listBean != null) {
                //之后再进入会话的时候,直接取名字
                name = listBean.sessionName;
            }
            return name;
        }

        String myUserID = AppAuth.get().getUserID() + "";


        //本地有,复用老的
        if (listBean != null && listBean.sessionUserList != null) {
            if (listBean.sessionUserList.size() > 2) {
                //多人的聊天就是临时调度,因为之前已经有了,就用之前的
                return listBean.sessionName;
            } else {
                //一对一就显示对方
                for (SendUserBean bean : listBean.sessionUserList) {
                    if (!bean.strUserID.equals(myUserID)) {
                        return bean.strUserName + AppUtils.getString(R.string.zhiling_diaodu);
                    }
                }
                return listBean.sessionName;
            }
        }

        //该会话还没创建
        if (sessionUserList != null) {
            if (sessionUserList.size() > 2) {
                //多人的聊天就是临时调度,没创建的话 我就是发起人
                return AppAuth.get().getUserName() + AppUtils.getString(R.string.zhiling_diaodu_temporary_group);
            } else {
                //一对一就显示对方
                for (SendUserBean bean : sessionUserList) {
                    if (!bean.strUserID.equals(myUserID)) {
                        return bean.strUserName + AppUtils.getString(R.string.zhiling_diaodu);
                    }
                }
            }
        }

        if (listBean.type == AppUtils.PLAYER_TYPE_DEVICE_INT) {
            return listBean.sessionName;
        }

        return "";
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (present!=null)
            present.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (present!=null)
            present.onDestroy();
    }
}
