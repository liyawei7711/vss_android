package huaiye.com.vvs.common.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import huaiye.com.vvs.R;

/**
 * author: admin
 * date: 2017/08/15
 * version: 0
 * mail: secret
 * desc: CheckableImageView
 */

@SuppressLint("AppCompatCustomView")
public class CheckableImageView extends ImageView {

    private final int MODE_NONE = -1;
    private final int MODE_CHOICE = 1;

    Drawable stateCheckedDrawable;
    Drawable stateUnCheckedDrawable;
    int checkUISzie = 40;
    int checkUIMargin = 4;
    int checkMode = -1;// -1 非选则模式 1 选择模式
    boolean isChecked = false;

    public CheckableImageView(Context context) {
        this(context, null);
    }

    public CheckableImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CheckableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CheckableImageView);

            stateCheckedDrawable = ta.getDrawable(R.styleable.CheckableImageView_stateCheckedRes);
            stateUnCheckedDrawable = ta.getDrawable(R.styleable.CheckableImageView_stateUnCheckedRes);
            checkUISzie = ta.getDimensionPixelOffset(R.styleable.CheckableImageView_checkUiSize, checkUISzie);
            checkUIMargin = ta.getDimensionPixelOffset(R.styleable.CheckableImageView_checkUiMargin, checkUIMargin);
            checkMode = ta.getInteger(R.styleable.CheckableImageView_checkMode, checkMode);
            isChecked = ta.getBoolean(R.styleable.CheckableImageView_stateChecked, isChecked);

            if(stateCheckedDrawable != null){
                stateCheckedDrawable.setBounds(0, 0, checkUISzie, checkUISzie);
            }

            if(stateUnCheckedDrawable != null){
                stateUnCheckedDrawable.setBounds(0, 0, checkUISzie, checkUISzie);
            }

            ta.recycle();
        }
    }

    public CheckableImageView setModeCheck(){
        checkMode = MODE_CHOICE;

        return this;
    }

    public CheckableImageView setModeNone(){
        checkMode = MODE_NONE;
        return this;
    }

    public CheckableImageView setChecked(boolean isChecked){
        this.isChecked = isChecked;
        return this;
    }

    public boolean isInCheckMode(){
        return checkMode == MODE_CHOICE;
    }

    public boolean isChecked(){
        return checkMode == MODE_CHOICE && isChecked;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(checkMode > 0){
            canvas.save();
            canvas.translate(getWidth() - checkUISzie - checkUIMargin, checkUIMargin);

            if(isChecked
                    && stateCheckedDrawable != null){
                stateCheckedDrawable.draw(canvas);
            }else if(!isChecked && stateUnCheckedDrawable != null){
                stateUnCheckedDrawable.draw(canvas);
            }

            canvas.restore();
        }
    }
}
