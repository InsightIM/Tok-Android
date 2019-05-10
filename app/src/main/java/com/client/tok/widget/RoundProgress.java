package com.client.tok.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.client.tok.R;
import com.client.tok.utils.LogUtil;

/**
 * progress of Round
 */
public class RoundProgress extends View {
    private String TAG = "RoundProgress";
    private Paint paint;
    private int roundColor;
    private float roundWidth;
    private int progressColor;
    private float progressWidth;
    private float max;
    private int style;
    private int startAngle;
    public static final int STROKE = 0;
    public static final int FILL = 1;
    private float curProgress;

    private boolean autoUpdate = false;
    private float autoUpdateTimeInterval = 200f;//ms
    private float autoUpdateValStep = 1f;
    private ProgressListener listener = null;

    public RoundProgress(Context context) {
        this(context, null);
    }

    public RoundProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.roundProgress);
        roundColor = mTypedArray.getColor(R.styleable.roundProgress_roundColor, Color.GRAY);
        roundWidth = mTypedArray.getDimension(R.styleable.roundProgress_roundWidth, 5);
        progressColor = mTypedArray.getColor(R.styleable.roundProgress_progressColor, Color.RED);
        progressWidth =
            mTypedArray.getDimension(R.styleable.roundProgress_progressWidth, roundWidth);
        max = mTypedArray.getInteger(R.styleable.roundProgress_max, 100);
        style = mTypedArray.getInt(R.styleable.roundProgress_style, 0);
        startAngle = mTypedArray.getInt(R.styleable.roundProgress_startAngle, 0);

        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int radius = (int) (centerX - roundWidth / 2);

        // step1
        paint.setStrokeWidth(roundWidth);
        paint.setColor(roundColor);
        paint.setAntiAlias(true);
        switch (style) {
            case STROKE:
                paint.setStyle(Paint.Style.STROKE);
                break;
            case FILL:
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                break;
        }
        canvas.drawCircle(centerX, centerX, radius, paint);

        // step2
        paint.setStrokeWidth(progressWidth);
        paint.setColor(progressColor);
        RectF oval =
            new RectF(centerX - radius, centerX - radius, centerX + radius, centerX + radius);

        float sweepAngle = 360 * curProgress / max;
        // 根据进度画圆弧
        switch (style) {
            case STROKE:
                canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
                break;
            case FILL:
                canvas.drawArc(oval, startAngle, sweepAngle, true, paint);
                break;
        }
    }

    public void clear() {
        curProgress = 0;
        autoUpdate = false;
        postInvalidate();
    }

    /**
     * @param max int
     */
    public synchronized void setMax(int max) {
        this.max = max;
    }

    public synchronized float getProgress() {
        return curProgress;
    }

    public synchronized void setProgress(float progress) {
        LogUtil.i(TAG, "setProgress:" + progress);
        if (progress > max) {
            progress = max;
        }
        this.curProgress = progress;
        if (listener != null) {
            if (curProgress <= max) {
                listener.onProgress(curProgress);
            }
            if (curProgress == max) {
                listener.onProgressFinish();
            }
        }
        postInvalidate();
    }

    /**
     * update by time
     *
     * @param time max time second
     */
    public synchronized void setAutoTimeProgress(int time) {
        max = time * (1000 / autoUpdateTimeInterval);
        autoUpdateValStep = time / max * (1000 / autoUpdateTimeInterval);
        LogUtil.i(TAG, "max:" + max + ",autoUpdateValStep:" + autoUpdateValStep);
    }

    public synchronized void startAutoUpdate() {
        autoUpdate = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (autoUpdate && curProgress < max) {
                    curProgress = curProgress + autoUpdateValStep;
                    RoundProgress.this.setProgress(curProgress);
                    try {
                        Thread.sleep((long) autoUpdateTimeInterval);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public synchronized void stopAutoUpdate() {
        autoUpdate = false;
    }

    public void setProgressListener(ProgressListener listener) {
        this.listener = listener;
    }

    public interface ProgressListener {
        void onProgress(float progress);

        void onProgressFinish();
    }
}
