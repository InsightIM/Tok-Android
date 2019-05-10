package com.client.tok.ui.recorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.ToastUtils;

public class CaptureButton extends View {
    public final String TAG = "CaptureButtom";

    private Paint mPaint;
    private Context mContext;
    private RecordVideoActivity activity;

    private float btn_center_Y;
    private float btn_center_X;

    private float btn_inside_radius;
    private float btn_outside_radius;
    //before radius
    private float btn_before_inside_radius;
    private float btn_before_outside_radius;
    //after radius
    private float btn_after_inside_radius;
    private float btn_after_outside_radius;

    private float btn_return_length;
    private float btn_return_X;
    private float btn_return_Y;

    private float btn_left_X, btn_right_X, btn_result_radius;

    //state
    private int STATE_SELECTED;
    private final int STATE_LESSNESS = 0;     //空闲状态
    private final int STATE_KEY_DOWN = 1;
    private final int STATE_CAPTURED = 2;
    private final int STATE_RECORD = 3;
    private final int STATE_PICTURE_BROWSE = 4;     //拍照完成后的预览状态
    private final int STATE_RECORD_BROWSE = 5;     //录像完成后的预览状态
    private final int STATE_READYQUIT = 6;     //退出
    private final int STATE_RECORDED = 7;

    private float key_down_Y;

    private RectF rectF;
    private float progress = 0;
    private LongPressRunnable longPressRunnable = new LongPressRunnable();
    private RecordRunnable recordRunnable = new RecordRunnable();
    private ValueAnimator record_anim = ValueAnimator.ofFloat(0, 360);
    private CaptureListener mCaptureListener;

    public CaptureButton(Context context) {
        this(context, null);
    }

    public CaptureButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        STATE_SELECTED = STATE_LESSNESS;
    }

    public void setActivity(RecordVideoActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        //        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = widthSize;
        int height = (width / 9) * 4;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        btn_center_X = getWidth() / 2f;
        btn_center_Y = getHeight() / 2f;

        btn_outside_radius = (float) (getWidth() / 9);
        btn_inside_radius = (float) (btn_outside_radius * 0.75);

        btn_before_outside_radius = (float) (getWidth() / 9);
        btn_before_inside_radius = (float) (btn_outside_radius * 0.75);
        btn_after_outside_radius = (float) (getWidth() / 6);
        btn_after_inside_radius = (float) (btn_outside_radius * 0.6);

        btn_return_length = (float) (btn_outside_radius * 0.35);
        //        btn_result_radius = 80;
        btn_result_radius = (float) (getWidth() / 9);
        btn_left_X = getWidth() / 2;
        btn_right_X = getWidth() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (STATE_SELECTED == STATE_LESSNESS || STATE_SELECTED == STATE_RECORD) {
            //draw capture button
            mPaint.setColor(0xFFEEEEEE);
            canvas.drawCircle(btn_center_X, btn_center_Y, btn_outside_radius, mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(btn_center_X, btn_center_Y, btn_inside_radius, mPaint);

            //draw Progress bar
            Paint paintArc = new Paint();
            paintArc.setAntiAlias(true);
            paintArc.setColor(0xFF00CC00);
            paintArc.setStyle(Paint.Style.STROKE);
            paintArc.setStrokeWidth(10);

            rectF = new RectF(btn_center_X - (btn_after_outside_radius - 5),
                btn_center_Y - (btn_after_outside_radius - 5),
                btn_center_X + (btn_after_outside_radius - 5),
                btn_center_Y + (btn_after_outside_radius - 5));
            canvas.drawArc(rectF, -90, progress, false, paintArc);

            //draw return button
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4);
            Path path = new Path();

            btn_return_X = ((getWidth() / 2) - btn_outside_radius) / 2;
            btn_return_Y = (getHeight() / 2 + 10);

            path.moveTo(btn_return_X - btn_return_length, btn_return_Y - btn_return_length);
            path.lineTo(btn_return_X, btn_return_Y);
            path.lineTo(btn_return_X + btn_return_length, btn_return_Y - btn_return_length);
            canvas.drawPath(path, paint);
        } else if (STATE_SELECTED == STATE_RECORD_BROWSE
            || STATE_SELECTED == STATE_PICTURE_BROWSE) {

            mPaint.setColor(0xFFEEEEEE);
            canvas.drawCircle(btn_left_X, btn_center_Y, btn_result_radius, mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(btn_right_X, btn_center_Y, btn_result_radius, mPaint);

            //left button
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            Path path = new Path();

            path.moveTo(btn_left_X - 2, btn_center_Y + 14);
            path.lineTo(btn_left_X + 14, btn_center_Y + 14);
            path.arcTo(new RectF(btn_left_X, btn_center_Y - 14, btn_left_X + 28, btn_center_Y + 14),
                90, -180);
            path.lineTo(btn_left_X - 14, btn_center_Y - 14);
            canvas.drawPath(path, paint);

            paint.setStyle(Paint.Style.FILL);
            path.reset();
            path.moveTo(btn_left_X - 14, btn_center_Y - 22);
            path.lineTo(btn_left_X - 14, btn_center_Y - 6);
            path.lineTo(btn_left_X - 23, btn_center_Y - 14);
            path.close();
            canvas.drawPath(path, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0xFF00CC00);
            paint.setStrokeWidth(4);
            path.reset();
            path.moveTo(btn_right_X - 28, btn_center_Y);
            path.lineTo(btn_right_X - 8, btn_center_Y + 22);
            path.lineTo(btn_right_X + 30, btn_center_Y - 20);
            path.lineTo(btn_right_X - 8, btn_center_Y + 18);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //        try {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (STATE_SELECTED == STATE_LESSNESS) {
                    if (event.getY() > btn_return_Y - 37
                        && event.getY() < btn_return_Y + 10
                        && event.getX() > btn_return_X - 37
                        && event.getX() < btn_return_X + 37) {
                        STATE_SELECTED = STATE_READYQUIT;
                    } else if (event.getY() > btn_center_Y - btn_outside_radius
                        && event.getY() < btn_center_Y + btn_outside_radius
                        && event.getX() > btn_center_X - btn_outside_radius
                        && event.getX() < btn_center_X + btn_outside_radius
                        && event.getPointerCount() == 1) {
                        key_down_Y = event.getY();
                        STATE_SELECTED = STATE_KEY_DOWN;
                        postCheckForLongTouch();
                    }
                } else if (STATE_SELECTED == STATE_RECORD_BROWSE
                    || STATE_SELECTED == STATE_PICTURE_BROWSE) {
                    if (event.getY() > btn_center_Y - btn_result_radius
                        && event.getY() < btn_center_Y + btn_result_radius
                        && event.getX() > btn_left_X - btn_result_radius
                        && event.getX() < btn_left_X + btn_result_radius
                        && event.getPointerCount() == 1) {
                        if (mCaptureListener != null) {
                            if (STATE_SELECTED == STATE_RECORD_BROWSE) {
                                mCaptureListener.deleteRecordResult();
                            } else if (STATE_SELECTED == STATE_PICTURE_BROWSE) {
                                mCaptureListener.cancel();
                            }
                        }
                        STATE_SELECTED = STATE_LESSNESS;
                        btn_left_X = btn_center_X;
                        btn_right_X = btn_center_X;
                        invalidate();
                    } else if (event.getY() > btn_center_Y - btn_result_radius
                        && event.getY() < btn_center_Y + btn_result_radius
                        && event.getX() > btn_right_X - btn_result_radius
                        && event.getX() < btn_right_X + btn_result_radius
                        && event.getPointerCount() == 1) {
                        if (mCaptureListener != null) {
                            if (STATE_SELECTED == STATE_RECORD_BROWSE) {
                                mCaptureListener.getRecordResult();
                            } else if (STATE_SELECTED == STATE_PICTURE_BROWSE) {
                                mCaptureListener.determine();
                            }
                        }
                        STATE_SELECTED = STATE_LESSNESS;
                        btn_left_X = btn_center_X;
                        btn_right_X = btn_center_X;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //                    if (event.getY() > btn_center_Y - btn_outside_radius &&
                //                            event.getY() < btn_center_Y + btn_outside_radius &&
                //                            event.getX() > btn_center_X - btn_outside_radius &&
                //                            event.getX() < btn_center_X + btn_outside_radius) {
                //                    }
                //                    if (mCaptureListener != null) {
                //                        mCaptureListener.scale(key_down_Y - event.getY());
                //                    }
                break;
            case MotionEvent.ACTION_UP:
                removeCallbacks(longPressRunnable);
                if (STATE_SELECTED == STATE_READYQUIT) {
                    if (event.getY() > btn_return_Y - 37
                        && event.getY() < btn_return_Y + 10
                        && event.getX() > btn_return_X - 37
                        && event.getX() < btn_return_X + 37) {
                        STATE_SELECTED = STATE_LESSNESS;
                        if (mCaptureListener != null) {    // 录制完成不使用直接返回键时，这个监听器null了，导致再次点击下箭头无法返回
                            mCaptureListener.quit();
                        }
                    }
                } else if (STATE_SELECTED == STATE_KEY_DOWN) {
                    if (event.getY() > btn_center_Y - btn_outside_radius
                        && event.getY() < btn_center_Y + btn_outside_radius
                        && event.getX() > btn_center_X - btn_outside_radius
                        && event.getX() < btn_center_X + btn_outside_radius) {
                        if (mCaptureListener != null) {
                            mCaptureListener.capture();
                        }
                        STATE_SELECTED = STATE_PICTURE_BROWSE;
                    }
                } else if (STATE_SELECTED == STATE_RECORD) {
                    LogUtil.e("CaptureButton",
                        "record_anim.getCurrentPlayTime==" + record_anim.getCurrentPlayTime());
                    if (record_anim.getCurrentPlayTime() < 800) {
                        ToastUtils.show("视频过短，请重新录制");
                        STATE_SELECTED = STATE_LESSNESS;
                        //                        Toast.makeText(mContext, "Under time", Toast.LENGTH_SHORT).show();
                        progress = 0;
                        invalidate();
                        record_anim.cancel();
                        if (mCaptureListener != null) {
                            mCaptureListener.cancel();
                        }
                    } else {
                        STATE_SELECTED = STATE_RECORD_BROWSE;
                        removeCallbacks(recordRunnable);
                        //                        Toast.makeText(mContext, "Time length " + record_anim
                        // .getCurrentPlayTime(), Toast.LENGTH_SHORT).show();
                        captureAnimation(getWidth() / 5, (getWidth() / 5) * 4);
                        record_anim.cancel();
                        progress = 0;
                        invalidate();
                        if (mCaptureListener != null) {
                            mCaptureListener.recordEnd();
                        }
                    }
                    if (btn_outside_radius == btn_after_outside_radius
                        && btn_inside_radius == btn_after_inside_radius) {
                        //                            startAnimation(btn_outside_radius, btn_outside_radius - 40,
                        // btn_inside_radius, btn_inside_radius + 20);
                        startAnimation(btn_after_outside_radius, btn_before_outside_radius,
                            btn_after_inside_radius, btn_before_inside_radius);
                    } else {
                        startAnimation(btn_after_outside_radius, btn_before_outside_radius,
                            btn_after_inside_radius, btn_before_inside_radius);
                    }
                }
                break;
            default:
                break;
        }
        //        } catch (Exception e) {
        //            return false;
        //        }
        return true;
    }

    public void initButton() {
        STATE_SELECTED = STATE_LESSNESS;
        invalidate();
    }

    public void captureSuccess() {
        captureAnimation(getWidth() / 5, (getWidth() / 5) * 4);
    }

    private void postCheckForLongTouch() {
        postDelayed(longPressRunnable, 200);
    }

    private class LongPressRunnable implements Runnable {
        @Override
        public void run() {
            startAnimation(btn_before_outside_radius, btn_after_outside_radius,
                btn_before_inside_radius, btn_after_inside_radius);
            STATE_SELECTED = STATE_RECORD;
        }
    }

    private class RecordRunnable implements Runnable {
        @Override
        public void run() {
            if (mCaptureListener != null) {
                mCaptureListener.record();
            }
            record_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (STATE_SELECTED == STATE_RECORD) {
                        progress = (float) animation.getAnimatedValue();
                    }
                    invalidate();
                }
            });
            record_anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (STATE_SELECTED == STATE_RECORD) {
                        STATE_SELECTED = STATE_RECORD_BROWSE;
                        progress = 0;
                        invalidate();
                        captureAnimation(getWidth() / 5, (getWidth() / 5) * 4);
                        if (btn_outside_radius == btn_after_outside_radius
                            && btn_inside_radius == btn_after_inside_radius) {
                            startAnimation(btn_after_outside_radius, btn_before_outside_radius,
                                btn_after_inside_radius, btn_before_inside_radius);
                        } else {
                            startAnimation(btn_after_outside_radius, btn_before_outside_radius,
                                btn_after_inside_radius, btn_before_inside_radius);
                        }
                        if (mCaptureListener != null) {
                            mCaptureListener.recordEnd();
                        }
                    }
                }
            });
            record_anim.setInterpolator(new LinearInterpolator());
            record_anim.setDuration(10000);
            record_anim.start();
        }
    }

    private void startAnimation(float outside_start, float outside_end, float inside_start,
        float inside_end) {

        ValueAnimator outside_anim = ValueAnimator.ofFloat(outside_start, outside_end);
        ValueAnimator inside_anim = ValueAnimator.ofFloat(inside_start, inside_end);
        outside_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                btn_outside_radius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        outside_anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (STATE_SELECTED == STATE_RECORD) {
                    postDelayed(recordRunnable, 100);
                }
            }
        });
        inside_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                btn_inside_radius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        outside_anim.setDuration(100);
        inside_anim.setDuration(100);
        outside_anim.start();
        inside_anim.start();
    }

    private void captureAnimation(float left, float right) {
        //        Toast.makeText(mContext,left+ " = "+right,Toast.LENGTH_SHORT).show();
        ValueAnimator left_anim = ValueAnimator.ofFloat(btn_left_X, left);
        ValueAnimator right_anim = ValueAnimator.ofFloat(btn_right_X, right);
        left_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                btn_left_X = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        right_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                btn_right_X = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        left_anim.setDuration(200);
        right_anim.setDuration(200);
        left_anim.start();
        right_anim.start();
    }

    public void setCaptureListener(CaptureListener mCaptureListener) {
        this.mCaptureListener = mCaptureListener;
    }

    public interface CaptureListener {
        void capture();

        void cancel();//拍照点击返回

        void determine();//拍照的确定

        void quit();//关闭界面

        void record();

        void recordEnd();

        void getRecordResult();//录像点击确定

        void deleteRecordResult();//录像点击返回

        void scale(float scaleValue);
    }
}
