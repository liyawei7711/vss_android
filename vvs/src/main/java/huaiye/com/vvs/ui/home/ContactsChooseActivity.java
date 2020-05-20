package huaiye.com.vvs.ui.home;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import java.util.ArrayList;

import huaiye.com.vvs.R;
import huaiye.com.vvs.common.AppBaseActivity;
import huaiye.com.vvs.common.AppUtils;
import huaiye.com.vvs.common.recycle.SafeLinearLayoutManager;
import huaiye.com.vvs.ui.home.present.ContactsChoosePresent;
import huaiye.com.vvs.ui.home.view.IContactsView;

/**
 * author: admin
 * date: 2018/05/11
 * version: 0
 * mail: secret
 * desc: ContactsActivity
 */
@BindLayout(R.layout.activity_contacts_choose)
public class ContactsChooseActivity extends AppBaseActivity implements IContactsView {

    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;
    @BindView(R.id.rct_view)
    RecyclerView rct_view;

    @BindView(R.id.view_float)
    View view_float;
    @BindView(R.id.rbtn_person)
    TextView rbtn_person;
    @BindView(R.id.rbtn_device)
    TextView rbtn_device;
    @BindView(R.id.iv_empty_view)
    View iv_empty_view;


    int id;
    RelativeLayout.LayoutParams lp;

    @BindExtra
    ArrayList<CGetMeetingInfoRsp.UserInfo> users;

    ContactsChoosePresent present;

    @Override
    protected void initActionBar() {
        present = new ContactsChoosePresent(this, users);

        getNavigate().setTitlText(AppUtils.getString(R.string.invisitor_title))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }).setRightText(AppUtils.getString(R.string.makesure))
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        present.meetClick();
                    }
                });

        lp = (RelativeLayout.LayoutParams) view_float.getLayoutParams();
        id = R.id.rbtn_person;

        rct_view.setLayoutManager(new SafeLinearLayoutManager(this));
        rct_view.setAdapter(present.getPersonAdapter());
        present.loadPerson(true);
        present.loadDevice(true);

        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (id) {
                    case R.id.rbtn_person:
                        present.loadPerson(true);
                        break;
                    case R.id.rbtn_device:
                        present.loadDevice(true);
                        break;
                }
            }
        });
    }

    @OnClick({R.id.rbtn_person, R.id.rbtn_group, R.id.rbtn_device})
    public void onClick(View view) {
        id = view.getId();

        lp.addRule(RelativeLayout.ALIGN_RIGHT, id);
        lp.addRule(RelativeLayout.ALIGN_LEFT, id);
        view_float.setLayoutParams(lp);

        rbtn_person.setTextColor(Color.parseColor("#333333"));
        rbtn_device.setTextColor(Color.parseColor("#333333"));
        switch (view.getId()) {
            case R.id.rbtn_person:
                rbtn_person.setTextColor(Color.parseColor("#539bf0"));
                rct_view.setAdapter(present.getPersonAdapter());
                present.changeViewShow(present.getPersonList().isEmpty());
                break;
            case R.id.rbtn_device:
                rbtn_device.setTextColor(Color.parseColor("#539bf0"));
                rct_view.setAdapter(present.getDeviceAdapter());
                present.changeViewShow(present.getDeviceList().isEmpty());
                break;
        }
    }

    @Override
    public void doInitDelay() {
    }

    @Override
    public AppBaseActivity getContext() {
        return this;
    }

    @Override
    public SwipeRefreshLayout getRefView() {
        return refresh_view;
    }

    @Override
    public View getEmptyView() {
        return iv_empty_view;
    }

    @Override
    public View getListView() {
        return rct_view;
    }

    @Override
    public void personSingle(int a) {
    }

    @Override
    public void personMulite() {
    }

    @Override
    public void personNull() {
    }

    @Override
    public void groupSingle() {
    }
}
