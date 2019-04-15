package com.client.tok.widget.audio;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class BlinkingDrawable extends Drawable {
    private int color;
    private RectF bounds = new RectF();
    private float w = 0f;
    private float h = 0f;
    private Paint paint;

    private ObjectAnimator alphaAnimator;

    public BlinkingDrawable(int color) {
        this.color = color;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(this.color);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.bounds.set(bounds);
        w = this.bounds.width();
        h = this.bounds.height();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawOval(0f, 0f, w, h, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void blinking() {
        if (alphaAnimator != null) {
            alphaAnimator.cancel();
        }

        alphaAnimator = ObjectAnimator.ofInt(this, "alpha", 255, 0);
        alphaAnimator.setDuration(1000);
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.start();
    }

    public void stopBlinking() {
        if (alphaAnimator != null) {
            alphaAnimator.cancel();
        }
    }
}