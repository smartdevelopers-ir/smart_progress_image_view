package ir.smartdevelopers.smartprogressimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

public class SmartProgressImageView extends AppCompatImageView {

    private AnimatedVectorDrawableCompat loadingAnimation;
    private boolean stopLoading = false;
    private EndAnimType mEndAnimType;
    private OnEndAnimationCompleteListener mOnEndAnimationCompleteListener;
    private int mCustomEndAnimationEndRes;
    private int mNoConnectionColor;
    private int mProgressFailedColor;
    private int mProgressDoneColor;
    private int mProgressNormalColor;

    public SmartProgressImageView(Context context) {
        this(context, null);
//        init(context,null);
    }

    public SmartProgressImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.SPIV_DefualtStyle);
//        init(context,attrs);
    }

    public SmartProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        loadingAnimation = AnimatedVectorDrawableCompat.create(context, R.drawable.avd_anim_circular_progress_bar);

        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SmartProgressImageView);
            mNoConnectionColor = typedArray.getColor(R.styleable.SmartProgressImageView_SPIV_NoConnectionColor,
                    ContextCompat.getColor(context, R.color.SPIV_colorNoConnection));
            mProgressDoneColor = typedArray.getColor(R.styleable.SmartProgressImageView_SPIV_ProgressDoneColor,
                    ContextCompat.getColor(context, R.color.SPIV_colorDone));
            mProgressFailedColor = typedArray.getColor(R.styleable.SmartProgressImageView_SPIV_ProgressFailedColor,
                    ContextCompat.getColor(context, R.color.SPIV_colorFailed));
            mProgressNormalColor = typedArray.getColor(R.styleable.SmartProgressImageView_SPIV_ProgressNormalColor,
                    ContextCompat.getColor(context, R.color.SPIV_colorNormal));
            typedArray.recycle();
        }
    }

    public void startLoading() {
        stopLoading = false;
        loadingAnimation.setTint(mProgressNormalColor);
        if (loadingAnimation.isRunning()) {
            return;
        }
        setImageDrawable(loadingAnimation);
        loadingAnimation.clearAnimationCallbacks();
        loadingAnimation.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                super.onAnimationEnd(drawable);
                if (!stopLoading) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        loadingAnimation.start();
                    });

                } else {
                    if (mEndAnimType == null) {
                        return;
                    }
                    switch (mEndAnimType) {
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


    public void startCustomAnimation() {
        mEndAnimType = EndAnimType.TYPE_CUSTOM;
        stopAnimation(false);
        AnimatedVectorDrawableCompat customAnimation = AnimatedVectorDrawableCompat.create(getContext(), mCustomEndAnimationEndRes);
        startEndAnimation(customAnimation);
    }

    public void startNoWifiAnimation() {
        mEndAnimType = EndAnimType.TYPE_NO_WIFI;
        stopAnimation(false);
        AnimatedVectorDrawableCompat noWifiAnimation = AnimatedVectorDrawableCompat
                .create(getContext(), R.drawable.avd_anim_progress_no_wifi);
        noWifiAnimation.setTint(mNoConnectionColor);
        startEndAnimation(noWifiAnimation);
    }

    public void startFailedAnimation() {
        mEndAnimType = EndAnimType.TYPE_FAILED;
        stopAnimation(false);
        AnimatedVectorDrawableCompat failedAnimation = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.avd_anim_progress_failed);
        failedAnimation.setTint(mProgressFailedColor);
        startEndAnimation(failedAnimation);
    }

    public void startDoneAnimation() {
        mEndAnimType = EndAnimType.TYPE_DONE;
        stopAnimation(false);
        AnimatedVectorDrawableCompat doneAnimation = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.avd_anim_progress_done);
        doneAnimation.setTint(mProgressDoneColor);
        startEndAnimation(doneAnimation);
    }

    private void startEndAnimation(AnimatedVectorDrawableCompat animatedVectorDrawableCompat) {

        if (animatedVectorDrawableCompat != null) {
            setImageDrawable(animatedVectorDrawableCompat);
            animatedVectorDrawableCompat.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    super.onAnimationEnd(drawable);
                    if (mOnEndAnimationCompleteListener != null) {
                        post(()->{
                            mOnEndAnimationCompleteListener.onAnimationEnd(mEndAnimType);
                        });
                    }
                }
            });
            animatedVectorDrawableCompat.start();
        } else {
            throw new RuntimeException("done animation is null!");
        }
    }



    public void stopAnimation() {
        stopAnimation(true);
    }

    private void stopAnimation(boolean clearImage) {
        stopLoading = true;
        if (loadingAnimation != null) {
            loadingAnimation.clearAnimationCallbacks();
            loadingAnimation.stop();
        }
        if (clearImage) {
            setImageDrawable(null);
        }

    }

    public void loadingDone(EndAnimType type) {
        mEndAnimType = type;
        stopLoading = true;
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

    public void setNoConnectionColor(int noConnectionColor) {
        mNoConnectionColor = noConnectionColor;
    }

    public void setProgressFailedColor(int progressFailedColor) {
        mProgressFailedColor = progressFailedColor;
    }

    public void setProgressDoneColor(int progressDoneColor) {
        mProgressDoneColor = progressDoneColor;
    }

    public void setProgressNormalColor(int progressNormalColor) {
        mProgressNormalColor = progressNormalColor;
    }

    public enum EndAnimType {
        TYPE_DONE, TYPE_NO_WIFI, TYPE_FAILED, TYPE_CUSTOM
    }

    public interface OnEndAnimationCompleteListener {
        void onAnimationEnd(EndAnimType type);
    }
}
