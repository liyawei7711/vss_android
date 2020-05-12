package huaiye.com.vvs.ui.local;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * author: admin
 * date: 2017/08/14
 * version: 0
 * mail: secret
 * desc: BDLXBaseFragment
 */

public abstract class MediaLocalBaseFragment extends Fragment {

    @Nullable
    @Override
    public abstract View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    public abstract void setModeEdit();

    public abstract void clearStates();

    public abstract void clearChoosed();

    public abstract void chooseAll();

    public abstract boolean isAllChoosed();

    public abstract void deleteChoosed();

    public abstract void uploadChoosed();

    public abstract void cancelCurrentAction();

    public abstract boolean isUploading();

    public abstract void setParentIntf(MediaLocalParent parentIntf);

}
