package huaiye.com.vvs.ui.channel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.talk.trunkchannel.ParamsGetTrunkChannelInfo;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CGetTrunkChannelInfoRsp;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelBean;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelUserBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.LiteBaseAdapter;
import huaiye.com.vvs.common.recycle.LiteViewHolder;
import huaiye.com.vvs.common.recycle.SafeLinearLayoutManager;

@BindLayout(R.layout.activity_channel_detail)
public class ChannelDetailActivity extends AppBaseActivity {
    @BindExtra
    public TrunkChannelBean trunkChannelBean;

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SwipeRefreshLayout swipeRefreshLayout;

    View headView;
    LiteBaseAdapter adapter;

    ArrayList<TrunkChannelUserBean> userBeans = new ArrayList<>();

    @Override
    protected void initActionBar() {
        getNavigate()
            .setTitlText(AppUtils.getString(R.string.channel_detail))
            .setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
    }

    @Override
    public void doInitDelay() {


        adapter = new LiteBaseAdapter<>(getSelf(),
                userBeans,
                ChannelDetailItemHolder.class,
                R.layout.item_channel_detail,
                null, "");
        rv.setLayoutManager(new SafeLinearLayoutManager(this));
        rv.setAdapter(adapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateInfos();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setRefreshing(true);
        initHead();
        updateInfos();
    }

    private void updateInfos(){
        ParamsGetTrunkChannelInfo paramsGetTrunkChannelInfo = new ParamsGetTrunkChannelInfo();
        paramsGetTrunkChannelInfo.setnTrunkChannelID(trunkChannelBean.nTrunkChannelID);
        paramsGetTrunkChannelInfo.setStrTrunkChannelDomainCode(trunkChannelBean.strTrunkChannelDomainCode);
        HYClient.getModule(ApiTalk.class).getTrunkChannelInfo(paramsGetTrunkChannelInfo, new SdkCallback<CGetTrunkChannelInfoRsp>() {
            @Override
            public void onSuccess(CGetTrunkChannelInfoRsp cGetTrunkChannelInfoRsp) {
                swipeRefreshLayout.setRefreshing(false);
                userBeans =  cGetTrunkChannelInfoRsp.lstTrunkChannelUser;
                adapter.setDatas(userBeans);
                updateHead(cGetTrunkChannelInfoRsp);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                showToast(errorInfo.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    public void initHead(){
        headView = LayoutInflater.from(getSelf()).inflate(R.layout.head_channel_detail,rv,false);
        adapter.setHeaderView(headView, new LiteViewHolder(getSelf(),headView,null,null) {
            @Override
            public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {

            }
        });
    }

    public void updateHead(CGetTrunkChannelInfoRsp info){
        TextView tvName = headView.findViewById(R.id.tv_1);
        TextView tvID = headView.findViewById(R.id.tv_2);
        TextView tvTime = headView.findViewById(R.id.tv_3);
        tvName.setText(String.format(AppUtils.getString(R.string.channel_detail_channel_name),info.strTrunkChannelName));
        tvID.setText(String.format(AppUtils.getString(R.string.channel_detail_channel_id),info.nTrunkChannelID));
        tvTime.setText(String.format(AppUtils.getString(R.string.channel_detail_channel_max_duration),info.nSpeakTimeout));
    }


}
