package huaiye.com.vvs.ui.chat;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.recycle.SafeLinearLayoutManager;
import huaiye.com.vvs.dao.msgs.ChatMessages;
import huaiye.com.vvs.dao.msgs.VssMessageBean;
import huaiye.com.vvs.dao.msgs.VssMessageListBean;
import huaiye.com.vvs.dao.msgs.VssMessageListMessages;
import huaiye.com.vvs.ui.chat.holder.ChatSearchListViewHolder;

import static android.view.View.GONE;


/**
 * Created by 123 on 17/5/26.
 */
@BindLayout(R.layout.activity_chat_search_list)
public class ChatSearchListActivity extends AppBaseActivity implements View.OnClickListener {

    @BindView(R.id.message_list)
    RecyclerView message_list;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.tv_cancel)
    View tv_cancel;
    @BindView(R.id.ll_empty)
    View ll_empty;

    ArrayList<VssMessageBean> datas = new ArrayList<>();
    LiteBaseAdapter<VssMessageBean> adapter;

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(GONE);

        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    loadMessage(et_search.getText().toString());
                }
                return false;
            }
        });

        adapter = new LiteBaseAdapter<>(this,
                datas,
                ChatSearchListViewHolder.class,
                R.layout.item_chat_list_view,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VssMessageBean temp = (VssMessageBean) v.getTag();
                        VssMessageListBean bean = VssMessageListMessages.get().getMessages(temp.sessionID);
                        if(bean == null) {
                            showToast(AppUtils.getString(R.string.no_this_column));
                            return;
                        }
                        bean.isRead = 1;
                        VssMessageListMessages.get().isRead(bean);

                        Intent intent = new Intent(ChatSearchListActivity.this, ChatActivity.class);
                        intent.putExtra("listBean", bean);
                        startActivity(intent);

                        adapter.notifyDataSetChanged();
                    }
                }, "");
        message_list.setLayoutManager(new SafeLinearLayoutManager(this));
        message_list.setAdapter(adapter);

    }

    @Override
    public void doInitDelay() {
    }

    private void loadMessage(String string) {
        List<VssMessageBean> allBean = ChatMessages.get().searchMessages();

        datas.clear();
        for (VssMessageBean temp : allBean) {
            if(et_search.getHint().toString().equals(AppUtils.getString(R.string.search))){
                if (temp.contactSessionName(string) || temp.contactContent(string) || temp.contactUser(string)) {
                    datas.add(temp);
                }
            } else if(et_search.getHint().toString().equals(AppUtils.getString(R.string.person_msg))) {
                if (temp.contactUser(string)) {
                    datas.add(temp);
                }
            } else if(et_search.getHint().toString().equals(AppUtils.getString(R.string.group_msg))) {
                if (temp.contactSessionName(string)) {
                    datas.add(temp);
                }
            } else if(et_search.getHint().toString().equals(AppUtils.getString(R.string.content_msg))) {
                if (temp.contactContent(string)) {
                    datas.add(temp);
                }
            }

        }
        adapter.notifyDataSetChanged();
        if (datas.size() > 0) {
            message_list.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(GONE);
        } else {
            message_list.setVisibility(GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.tv_cancel, R.id.tv_person, R.id.tv_group, R.id.tv_content})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                onBackPressed();
                break;
            case R.id.tv_person:
                et_search.setHint(AppUtils.getString(R.string.person_msg));
                et_search.setText("");
                break;
            case R.id.tv_group:
                et_search.setHint(AppUtils.getString(R.string.group_msg));
                et_search.setText("");
                break;
            case R.id.tv_content:
                et_search.setHint(AppUtils.getString(R.string.content_msg));
                et_search.setText("");
                break;
        }
    }
}
