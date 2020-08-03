package ir.smartdevelopers.smartprogressimageview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

public class SmartProgressImageView extends AppCompatImageView {

    private AnimatedVectorDrawableCompat loadingAnimation;
    private boolean stopLoading=false;
    private EndAnimType mEndAnimType;
    private OnEndAnimationCompleteListener mOnEndAnimationCompleteListener;
    private int mCustomEndAnimationEndRes;
    public SmartProgressImageView(Context context) {
        super(context);
        init(context,null);
    }

    public SmartProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public SmartProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }
    private void init(Context context,AttributeSet attributeSet){
        loadingAnimation=AnimatedVectorDrawableCompat.create(context,R.drawable.avd_anim_circular_progress_bar);
    }
    public void startLoading(){
        stopLoading=false;
        setImageDrawable(loadingAnimation);
        loadingAnimation.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                super.onAnimationEnd(drawable);
                if (!stopLoading) {
                    loadingAnimation.start();
                }else {
                    switch (mEndAnimType){
                        case TYPE_DONE:
                            startDoneAnimation();
                            break;
                        case TYPE_FAILED:
                            startFailedAnimation();
                            break;
                        case TYPE_NO_WIFI:
                            startNoWifiAnimation();
                            break;
                        case TYPE_CUSTOM:
                            startCustomAnimation();

                    }
                }
            }
        });
        loadingAnimation.start();
    }

    private void startCustomAnimation() {
        AnimatedVectorDrawableCompat customAnimation=AnimatedVectorDrawableCompat.create(getContext(),mCustomEndAnimationEndRes);
        startEndAnimation(customAnimation);
    }

    private void startNoWifiAnimation() {
        AnimatedVectorDrawableCompat noWifiAnimation=AnimatedVectorDrawableCompat.create(getContext(),R.drawable.avd_anim_progress_no_wifi);
        startEndAnimation(noWifiAnimation);
    }

    private void startFailedAnimation() {
        AnimatedVectorDrawableCompat failedAnimation=AnimatedVectorDrawableCompat.create(getContext(),R.drawable.avd_anim_progress_failed);
        startEndAnimation(failedAnimation);
    }

    private void startDoneAnimation() {
        AnimatedVectorDrawableCompat doneAnimation=AnimatedVectorDrawableCompat.create(getContext(),R.drawable.avd_anim_progress_done);
        startEndAnimation(doneAnimation);
    }
    private void startEndAnimation(AnimatedVectorDrawableCompat animatedVectorDrawableCompat){

        if (animatedVectorDrawableCompat!=null) {
            setImageDrawable(animatedVectorDrawableCompat);
            animatedVectorDrawableCompat.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    super.onAnimationEnd(drawable);
                    if (mOnEndAnimationCompleteListener!=null){
                        mOnEndAnimationCompleteListener.onAnimationEnd(mEndAnimType);
                    }
                }
            });
            animatedVectorDrawableCompat.start();
        }else {
            throw new RuntimeException("done animation is null!");
        }
    }

    public void loadingDone(EndAnimType type){
        mEndAnimType=type;
        stopLoading=true;
    }

    public void setOnEndAnimationCompleteListener(OnEndAnimationCompleteListener onEndAnimationCompleteListener) {
        mOnEndAnimationCompleteListener = onEndAnimationCompleteListener;
    }

    public int getCustomEndAnimationEndRes() {
        return mCustomEndAnimationEndRes;
    }

    public void setCustomEndAnimationEndRes(@DrawableRes int customEndAnimationEndRes) {
        mCustomEndAnimationEndRes = customEndAnimationEndRes;
    }

    public enum EndAnimType{
        TYPE_DONE,TYPE_NO_WIFI,TYPE_FAILED,TYPE_CUSTOM
    }
    public interface OnEndAnimationCompleteListener{
        void onAnimationEnd(EndAnimType type);
    }
}
