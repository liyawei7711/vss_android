package huaiye.com.vvs.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * author: admin
 * date: 2018/05/02
 * version: 0
 * mail: secret
 * desc: CustomerWebView
 */

public class CustomerWebView extends WebView {
    public ScrollInterface mScrollInterface;

    public CustomerWebView(Context context) {
        super(context);
    }

    public CustomerWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomerWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);

        mScrollInterface.onSChanged(l, t, oldl, oldt);

    }

    public void setOnCustomScroolChangeListener(ScrollInterface scrollInterface) {

        this.mScrollInterface = scrollInterface;

    }

    public interface ScrollInterface {

        public void onSChanged(int l, int t, int oldl, int oldt);

    }

}
