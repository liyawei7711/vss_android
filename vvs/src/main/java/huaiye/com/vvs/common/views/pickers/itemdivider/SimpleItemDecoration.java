package huaiye.com.vvs.common.views.pickers.itemdivider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import huaiye.com.vvs.R;

/**
 * Created by zl on 2017/3/31.
 */

public class SimpleItemDecoration extends RecyclerView.ItemDecoration {

    private int dividerHeight;
    private Paint dividerPaint;

    public SimpleItemDecoration(Context context) {
        this(context,context.getResources().getColor(R.color.back_gray),2);
    }

    public SimpleItemDecoration(Context context,int color) {
        this(context,color,2);
    }

    public SimpleItemDecoration(Context context,int color,int height) {
        dividerPaint = new Paint();
        dividerPaint.setColor(color);
        dividerHeight = height;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            c.drawRect(left, top, right, bottom, dividerPaint);
        }
    }

}
