package huaiye.com.vvs.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ttyy.commonanno.Finder;
import com.ttyy.commonanno.Injectors;

import huaiye.com.vvs.common.dialog.LogicDialog;
import huaiye.com.vvs.common.views.NavigateView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: AppBaseFragment
 */

public class AppBaseFragment extends Fragment {

    LinearLayout contentView;
    Handler mHandler = new Handler(Looper.getMainLooper());
    NavigateView navigate;

    Toast toast;
    LogicDialog mLogicDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("AppBaseOpen", "open_fragment " + this.getClass().getSimpleName());
        // Content 初始化
        contentView = new LinearLayout(getActivity());
        contentView.setOrientation(LinearLayout.VERTICAL);

        FrameLayout userContent = new FrameLayout(getActivity());
        Injectors.get().inject(Finder.View, userContent, this);

        navigate = new NavigateView(getActivity());

        contentView.addView(navigate, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        contentView.addView(userContent, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        mLogicDialog = new LogicDialog(getActivity());

        return contentView;
    }

    public void showToast(String msg) {
        toast.setText(msg);
        toast.show();
    }

    public LogicDialog getLogicDialog() {
        return mLogicDialog;
    }

    public View getContentView() {
        return contentView;
    }

    public NavigateView getNavigate() {
        return navigate;
    }

    public AppBaseFragment getSelf() {
        return this;
    }

    public void post(Runnable runnable) {
        postDelayed(runnable, 0);
    }

    public void postDelayed(Runnable runnable, long delayedMillions) {
        if (runnable == null) {
            return;
        }
        mHandler.postDelayed(runnable, delayedMillions);
    }

    protected Observable<String> createTextChangeObserver(final EditText et_key) {
        Observable<String> textChangeObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                final TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        emitter.onNext(s.toString().replaceAll(" ", ""));
                    }
                };
                et_key.addTextChangedListener(watcher);
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        et_key.removeTextChangedListener(watcher);
                    }
                });
            }
        });
        return textChangeObservable;
    }

}
