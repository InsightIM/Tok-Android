package com.client.tok.widget.audio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.utils.ScreenUtils;
import com.client.tok.utils.ViewUtil;

public class SlidePanelView extends RelativeLayout {
    private BlinkingDrawable blinkingDrawable;
    private LinearLayout slideLayout;
    private TextView timeTv;
    private TextView cancelTv;
    private int timeValue;
    private boolean toCanceled;
    private SlideCallback callback;
    private UpdateTimeRunnable updateTimeRunnable;

    public SlidePanelView(Context context) {
        this(context, null);
    }

    public SlidePanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidePanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_slide_panel);
        addView(rootView);
        setBackgroundColor(Color.WHITE);
        setClickable(true);
        slideLayout = findViewById(R.id.slide_ll);
        timeTv = findViewById(R.id.time_tv);
        cancelTv = findViewById(R.id.cancel_tv);
        int blinkSize = context.getResources().getDimensionPixelSize(R.dimen.blink_size);
        blinkingDrawable = new BlinkingDrawable(ScreenUtils.getColor(context, R.color.color_blink));
        blinkingDrawable.setBounds(0, 0, blinkSize, blinkSize);
        timeTv.setCompoundDrawables(blinkingDrawable, null, null, null);
        cancelTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View cancelTv) {
                if (callback != null) {
                    callback.onSlideCancel();
                }
            }
        });
        timeTv.setText(String.valueOf(0));
    }

    public void setCallback(SlideCallback callback) {
        this.callback = callback;
    }

    public void onStart() {
        updateTimeRunnable = new UpdateTimeRunnable();
        setVisibility(View.VISIBLE);
        setTranslationX(getMeasuredWidth());
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setDuration(200);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (blinkingDrawable != null) {
                    blinkingDrawable.blinking();
                }
                postDelayed(updateTimeRunnable, 200);
            }
        });
        animSet.playTogether(ObjectAnimator.ofFloat(this, "translationX", 0f),
            ObjectAnimator.ofFloat(this, "alpha", 1f));
        animSet.start();
    }

    public int getSlideWidth() {
        int[] location = new int[2];
        slideLayout.getLocationOnScreen(location);
        return location[0] - (int) (64 * getContext().getResources().getDisplayMetrics().density);
    }

    public void slideText(float x) {
        float preX = slideLayout.getTranslationX();
        if (preX - x > 0) {
            slideLayout.setTranslationX(0f);
        } else {
            slideLayout.setTranslationX(slideLayout.getTranslationX() - x * 1.5f);
        }
        float alpha = Math.abs(slideLayout.getTranslationX() * 1.5f / slideLayout.getWidth());
        slideLayout.setAlpha(1 - alpha);
    }

    public void toCancel() {
        if (!toCanceled) {
            AnimatorSet animSet = new AnimatorSet();
            animSet.setDuration(200);
            animSet.setInterpolator(new DecelerateInterpolator());
            animSet.playTogether(ObjectAnimator.ofFloat(slideLayout, "alpha", 0f),
                ObjectAnimator.ofFloat(slideLayout, "translationY", ScreenUtils.dip(20)),
                ObjectAnimator.ofFloat(cancelTv, "alpha", 1f),
                ObjectAnimator.ofFloat(cancelTv, "translationY", 0 - ScreenUtils.dip(20), 0f));
            animSet.start();
            toCanceled = true;
        }
    }

    public void onEnd() {
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(200);
        animSet.setInterpolator(new AccelerateInterpolator());
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                handEnd();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                handEnd();
            }
        });
        animSet.playTogether(ObjectAnimator.ofFloat(this, "translationX", getMeasuredWidth()),
            ObjectAnimator.ofFloat(this, "alpha", 0f));
        animSet.start();
    }

    public void handEnd() {
        if (toCanceled) {
            toCanceled = false;
            cancelTv.setAlpha(0f);
            cancelTv.setTranslationY(0f);
            slideLayout.setAlpha(0f);
            slideLayout.setTranslationX(0f);
        }
        slideLayout.setTranslationX(0f);
        if (blinkingDrawable != null) {
            blinkingDrawable.stopBlinking();
        }
        removeCallbacks(updateTimeRunnable);
        timeValue = 0;
        timeTv.setText(String.valueOf(0));
    }

    public interface SlideCallback {
        void onSlideTimeout();

        void onSlideCancel();
    }

    public class UpdateTimeRunnable implements Runnable {

        @Override
        public void run() {
            if (timeValue >= GlobalParams.MAX_AUDIO) {
                if (callback != null) {
                    callback.onSlideTimeout();
                    return;
                }
            }
            timeValue++;
            timeTv.setText(String.valueOf(timeValue));
            postDelayed(this, 1000);
        }
    }
}
