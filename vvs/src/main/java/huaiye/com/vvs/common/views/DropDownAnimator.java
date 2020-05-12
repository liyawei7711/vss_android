package huaiye.com.vvs.common.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;


public class DropDownAnimator {
    private View mView;
    private int  mHeight;
    private final int ANIMATOR_DURATION = 300;
    private ValueAnimator mCurrentAnimator;

    public DropDownAnimator(View view,int height){
        this.mView = view;
        this.mHeight = height;
    }




    public void show(){
        if (mCurrentAnimator != null && mCurrentAnimator.isRunning()
                && mCurrentAnimator.isStarted()){
            return;
        }

        if (mView.getVisibility() == View.VISIBLE){
            return;
        }


        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentHeight = (int) (mHeight * animation.getAnimatedFraction());
                setViewHeight(currentHeight);

            }
        });
        valueAnimator.setDuration(ANIMATOR_DURATION);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setViewHeight(mHeight);

            }
        });
        mCurrentAnimator = valueAnimator;
        valueAnimator.start();
    }


    public void hide(){
        if (mCurrentAnimator != null && mCurrentAnimator.isRunning()
                && mCurrentAnimator.isStarted()){
            return;
        }

        if (mView.getVisibility() != View.VISIBLE){
            return;
        }

        final int actualHeight = mView.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentHeight = actualHeight - (int) (actualHeight * animation.getAnimatedFraction());
                setViewHeight(currentHeight);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mView.setVisibility(View.INVISIBLE);
                setViewHeight(0);
            }
        });
        valueAnimator.setDuration(ANIMATOR_DURATION);
        mCurrentAnimator = valueAnimator;
        valueAnimator.start();
    }


    private void setViewHeight(int height){
        ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
        layoutParams.height = height;
        mView.setLayoutParams(layoutParams);
    }
}
