package huaiye.com.vvs.ui.home.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import huaiye.com.vvs.common.AppBaseActivity;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: IDeviceListView
 */

public interface IContactsView {
    AppBaseActivity getContext();

    SwipeRefreshLayout getRefView();

    View getEmptyView();

    View getListView();

    void personSingle(int pro);

    void personMulite();

    void personNull();

    void groupSingle();
}
