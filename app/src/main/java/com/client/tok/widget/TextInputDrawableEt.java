package com.client.tok.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TextInputDrawableEt extends android.support.v7.widget.AppCompatEditText {
    public TextInputDrawableEt(Context context) {
        super(context);
    }

    public TextInputDrawableEt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextInputDrawableEt(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (onDrawableLeftListener != null) {
                Drawable drawableLeft = getCompoundDrawables()[0];
                // click position < marginLeft+width+Padding
                if (drawableLeft != null && event.getRawX() <= (getLeft()
                    + getTotalPaddingLeft()
                    + drawableLeft.getBounds().width())) {
                    onDrawableLeftListener.onDrawableLeftClick();
                    return true;
                }
            }

            if (onDrawableRightListener != null) {
                Drawable drawableRight = getCompoundDrawables()[2];
                // click position> marginRight-width-Padding
                if (drawableRight != null && event.getRawX() >= (getRight()
                    - getTotalPaddingRight()
                    - drawableRight.getBounds().width())) {
                    onDrawableRightListener.onDrawableRightClick();
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public interface OnDrawableLeftListener {
        void onDrawableLeftClick();
    }

    private OnDrawableLeftListener onDrawableLeftListener;

    public void setOnDrawableLeftListener(OnDrawableLeftListener onDrawableLeftListener) {
        this.onDrawableLeftListener = onDrawableLeftListener;
    }

    public interface OnDrawableRightListener {
        void onDrawableRightClick();
    }

    private OnDrawableRightListener onDrawableRightListener;

    public void setOnDrawableRightListener(OnDrawableRightListener onDrawableRightListener) {
        this.onDrawableRightListener = onDrawableRightListener;
    }
}
