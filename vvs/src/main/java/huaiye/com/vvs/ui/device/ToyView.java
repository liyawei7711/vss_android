package huaiye.com.vvs.ui.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * author: admin
 * date: 2017/05/24
 * version: 0
 * mail: secret
 * desc: SpecialView
 */

@SuppressLint("AppCompatCustomView")
public class ToyView extends ImageView {

    RectF mInnerRect;

    RectF mOuterRect;

    Path mTriangleArrow;

    Paint paint;

    int mTextColor = Color.parseColor("#333333");
    TextPaint mTextPaint;

    String mCenterText = "";

    PressListener mPressListener;

    public ToyView(Context context) {
        this(context, null);
    }

    public ToyView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ToyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Paint csp = new Paint();
        // Android5.0 解决裁剪完成之后其他区域为黑色的问题
        csp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, csp);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(mTextColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int r = getRadius();

        mOuterRect = new RectF(0, 0, r * 8 / 10, r * 8 / 10);

        mInnerRect = new RectF(0, 0, (float) (r * 1 / 2.5), (float) (r * 1 / 2.5));

        mTriangleArrow = new Path();

        mTextPaint.setTextSize(r * 0.14f);

        float density = getResources().getDisplayMetrics().density;

        float triangle1X = -3.5f * density;
        float triangle1Y = r / 10f + 2.5f * density;

        float triangle2X = 3.5f * density;
        float triangle2Y = r / 10f + 2.5f * density;

        float triangle3X = 0;
        float triangle3Y = r / 10f - 2.5f * density;

        mTriangleArrow.moveTo(triangle1X, triangle1Y);
        mTriangleArrow.quadTo(triangle1X, triangle1Y, triangle2X, triangle2Y);
        mTriangleArrow.quadTo(triangle2X, triangle2Y, triangle3X, triangle3Y);
        mTriangleArrow.quadTo(triangle3X, triangle3Y, triangle1X, triangle1Y);
        mTriangleArrow.close();
    }


    int getRadius() {
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeee getRadius " + getMeasuredWidth() + "    " + getMeasuredHeight()
                + "  " + getMaxHeight() + "   " + getMeasuredWidth());
        return getMeasuredWidth() > getMeasuredHeight() ? getMaxHeight() : getMeasuredWidth();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int dist = getDistanceToCircleCenter(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("eeeeeeeeeeeeeeeeeeeeeeee dist " + dist);
                if (dist < getRadius() * 3f / 10 - 10) {

                    mCurrentFocusIndex = CENTER_CIRCLE;
                    postInvalidate();

                } else if (dist > getRadius() / 2f + 10) {

                    mCurrentFocusIndex = -1;
                    postInvalidate();

                } else {

                    mCurrentFocusIndex = getPressedItem(event);
                    if (mPressListener != null) {
                        mPressListener.onArrowPressDown(mCurrentFocusIndex);
                    }
                    postInvalidate();
                }

                break;
            case MotionEvent.ACTION_MOVE:

                if (mCurrentFocusIndex == -1) {
                    return true;
                }

                if (dist < getRadius() * 3f / 10 - 10
                        || dist > getRadius() / 2f + 10) {

                    if (mPressListener != null
                            && mCurrentFocusIndex != CENTER_CIRCLE) {
                        mPressListener.onArrowPressUp(mCurrentFocusIndex);
                    }
                    mCurrentFocusIndex = -1;
                    postInvalidate();
                } else {

                    int index = getPressedItem(event);
                    if (index != mCurrentFocusIndex) {
                        if (mPressListener != null
                                && mCurrentFocusIndex != CENTER_CIRCLE) {
                            mPressListener.onArrowPressUp(mCurrentFocusIndex);
                        }
                        mCurrentFocusIndex = -1;
                        postInvalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                if (mCurrentFocusIndex == -1) {
                    return true;
                }

                if (dist < getRadius() * 3f / 10 - 10) {

                    if (mCurrentFocusIndex == CENTER_CIRCLE) {
                        if (mPressListener != null) {
                            // 点击中心圆
                            mPressListener.onCenterCircleSingleTap();
                        }
                    }

                } else if (dist > getRadius() / 2f + 10) {

                } else {

                    int item = getPressedItem(event);
                    if (item == mCurrentFocusIndex) {
                        if (mPressListener != null) {
                            mPressListener.onArrowPressUp(item);
                        }
                    }
                }

                mCurrentFocusIndex = -1;
                if (mPressListener != null) {
                    mPressListener.onCancel();
                }
                postInvalidate();
                break;
        }

        return true;
    }

    float getDegree(MotionEvent event) {
        int c = getDistanceToCircleCenter(event);
        double a = event.getY() - getMeasuredHeight() / 2;
        double sin = a / c;
        if (sin < -1) {
            sin = -1;
        }
        if (sin > 1) {
            sin = 1;
        }

        float degree = (int) (Math.asin(sin) / Math.PI * 180);

        int quadrant = getQuadrant(event);

        switch (quadrant) {
            case 1:
                degree = 112.5f + degree;
                break;
            case 2:
                if (degree < -67.5) {
                    degree = degree + 112.5f;
                } else if (degree< -22.5){
                    degree = 360 + degree + 22.5f;
                } else {
                    //点击的事0 ~ -22.5
                    //转换后的应该是292.5~315
                    degree = 360f-90f+22.5f- degree;
                }

                break;
            case 3:
                degree = 180 - degree + 112.5f;
                break;
            case 4:
                if (degree < 0) {
                    degree = 90 + degree + 112.5f;
                } else {
                    degree += 112.5f;
                }

                break;
        }

        return degree;
    }

    int getPressedItem(MotionEvent event) {
        float degree = getDegree(event);

        int index = (int) Math.floor(degree / 45f);

        return index;
    }

    /**
     * 获取象限
     *
     * @param event
     * @return
     */
    int getQuadrant(MotionEvent event) {

        float centerX = getMeasuredWidth() / 2f;
        float centerY = getMeasuredHeight() / 2f;

        float x = event.getX();
        float y = event.getY();

        if (x >= centerX
                && y <= centerY) {
            return 1;
        } else if (x < centerX && y < centerY) {

            return 2;
        } else if (x >= centerX
                && y >= centerY) {

            return 4;
        } else {

            return 3;
        }
    }

    int getDistanceToCircleCenter(MotionEvent event) {

        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;

        int dist = (int) Math.sqrt(Math.pow(event.getX() - centerX, 2) + Math.pow(event.getY() - centerY, 2));
        return dist;
    }

    public void setPressListener(PressListener listener) {
        this.mPressListener = listener;
    }

    public void setCenterText(String text) {
        mCenterText = text;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawOuterBtns(canvas);

        drawInnerBtns(canvas);
    }

    int mCurrentFocusIndex = -1;
    static final int CENTER_CIRCLE = 8;

    int mCircleBarColor = Color.WHITE;
    int mCenterCircleColor = Color.WHITE;
    int mDividerColor = Color.parseColor("#cccccc");
    int mArrowColor = Color.parseColor("#333333");

    void drawOuterBtns(Canvas canvas) {

        int degree = 45;
        float hafDegree = 45f / 2;
        int radius = getRadius() / 5;
        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;

        canvas.save();
        canvas.translate((getMeasuredWidth() - mOuterRect.width()) / 2, (getMeasuredHeight() - mOuterRect.width()) / 2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(radius);
        paint.setColor(mCircleBarColor);
        canvas.drawOval(mOuterRect, paint);
        canvas.restore();

        for (int i = 0; i < 8; i++) {
            // 画选中
            if (mCurrentFocusIndex == i) {

                paint.setColor(mDividerColor);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(radius);

                canvas.save();
                canvas.rotate(degree * i - hafDegree, centerX, centerY);
                canvas.translate((getMeasuredWidth() - mOuterRect.width()) / 2, (getMeasuredHeight() - mOuterRect.width()) / 2);
                canvas.drawArc(mOuterRect, -90, degree, false, paint);
                canvas.restore();
            }

            // 画箭头
            canvas.save();
            canvas.rotate(degree * i, centerX, centerY);
            canvas.translate(getMeasuredWidth() / 2, 0);
            paint.setColor(mArrowColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPath(mTriangleArrow, paint);
            canvas.restore();

            // 画分界线
            paint.setColor(mDividerColor);
            paint.setStrokeWidth(1.4f);
            paint.setStyle(Paint.Style.STROKE);

            canvas.save();
            canvas.rotate(degree * i - hafDegree, centerX, centerY);
            canvas.drawLine(getMeasuredWidth() / 2, 0, getMeasuredWidth() / 2, radius, paint);

            canvas.rotate(degree, centerX, centerY);
            canvas.drawLine(getMeasuredWidth() / 2, 0, getMeasuredWidth() / 2, radius, paint);
            canvas.restore();
        }

    }

    void drawInnerBtns(Canvas canvas) {

        canvas.save();
        canvas.translate((getMeasuredWidth() - mInnerRect.width()) / 2, (getMeasuredHeight() - mInnerRect.width()) / 2);
        paint.setStyle(Paint.Style.FILL);

        if (mCurrentFocusIndex == 8) {
            paint.setColor(mDividerColor);
        } else {
            paint.setColor(mCenterCircleColor);
        }

        canvas.drawOval(mInnerRect, paint);
        canvas.restore();

        float cy = getMeasuredHeight() / 2f;
        float cx = getMeasuredWidth() / 2f;
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        cy = cy - (fm.top + fm.bottom) / 2f;
        canvas.drawText(mCenterText, 0, mCenterText.length(), cx, cy, mTextPaint);

    }

    public interface PressListener {

        void onArrowPressDown(int item);

        void onArrowPressUp(int item);

        void onCenterCircleSingleTap();

        void onCancel();
    }

}
