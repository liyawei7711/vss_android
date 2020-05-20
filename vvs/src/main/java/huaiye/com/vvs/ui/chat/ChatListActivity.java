package huaiye.com.vvs.ui.chat;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.recycle.RecycleTouchUtils;
import huaiye.com.vvs.common.recycle.SafeLinearLayoutManager;
import huaiye.com.vvs.dao.msgs.BroadcastManage;
import huaiye.com.vvs.dao.msgs.BroadcastMessage;
import huaiye.com.vvs.dao.msgs.ChatMessages;
import huaiye.com.vvs.dao.msgs.VssMessageBean;
import huaiye.com.vvs.dao.msgs.VssMessageListBean;
import huaiye.com.vvs.dao.msgs.VssMessageListMessages;
import huaiye.com.vvs.ui.chat.holder.ChatListViewHolder;


/**
 * Created by 123 on 17/5/26.
 */
@BindLayout(R.layout.activity_chat_list)
public class ChatListActivity extends AppBaseActivity {

    @BindView(R.id.message_list)
    RecyclerView message_list;
    @BindView(R.id.ll_empty)
    View ll_empty;
    @BindView(R.id.fl_search)
    View fl_search;

    ArrayList<VssMessageListBean> datas = new ArrayList<>();
    LiteBaseAdapter<VssMessageListBean> adapter;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(AppUtils.getString(R.string.zhiling_diaodu_title))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

        new RecycleTouchUtils().initTouch(new RecycleTouchUtils.ITouchEvent() {
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                mLogicDialog.setCancelable(false);
                mLogicDialog.setCanceledOnTouchOutside(false);
                mLogicDialog.setMessageText(AppUtils.getString(R.string.is_delete_this_msg));
                mLogicDialog.setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VssMessageListBean data = datas.get(viewHolder.getAdapterPosition());
                        VssMessageListMessages.get().del(data.sessionID);
                        ChatMessages.get().del(data.sessionID);
                        if (data.sessionID.equals("0")){
                            BroadcastManage.get().delAll();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    File file=new File(AppUtils.audiovideoPath);
                                    if (file.exists()){
                                        File[] files = file.listFiles();
                                        for (File f : files) {
                                            AppUtils.delFile(f);
                                        }
                                        file.delete();
                                    }
                                }
                            }).start();
                        }
                        datas.remove(viewHolder.getAdapterPosition());
                        adapter.notifyDataSetChanged();

                        showEmpty();
                    }
                }).setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.notifyDataSetChanged();
                    }
                }).show();
            }
        }).attachToRecyclerView(message_list);

        adapter = new LiteBaseAdapter<>(this,
                datas,
                ChatListViewHolder.class,
                R.layout.item_chat_list_view,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VssMessageListBean bean = (VssMessageListBean) v.getTag();
                        bean.isRead = 1;
                        VssMessageListMessages.get().isRead(bean);

                        Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                        intent.putExtra("listBean", bean);
                        startActivity(intent);

                        adapter.notifyDataSetChanged();
                    }
                }, "");
        message_list.setLayoutManager(new SafeLinearLayoutManager(this));
        message_list.setAdapter(adapter);

        fl_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatListActivity.this, ChatSearchListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void doInitDelay() {
        loadMessage();
    }

    private void loadMessage() {
        List<VssMessageListBean> allBean = VssMessageListMessages.get().getMessages();
        datas.clear();
        datas.addAll(allBean);
        adapter.notifyDataSetChanged();
        showEmpty();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null){
            loadMessage();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(VssMessageBean obj) {
        //接到群组的人员变化后刷新界面
        loadMessage();
    }


    private void showEmpty() {
        if (datas.size() > 0) {
            message_list.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            message_list.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

}
