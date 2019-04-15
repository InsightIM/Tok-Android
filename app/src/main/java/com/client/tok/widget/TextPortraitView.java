package com.client.tok.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.client.tok.R;
import com.client.tok.utils.ColorUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.ScreenUtils;
import com.client.tok.utils.StringUtils;


class TextPortraitView extends View {
    private Context mContext;
    private Paint mPaint;
    private Canvas mCanvas;
    private String mMsg;
    private float mWidth = 0f;
    private String mFriendKey;

    public TextPortraitView(Context context) {
        this(context, null);
    }

    public TextPortraitView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextPortraitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getMeasuredWidth() > getHeight()) {
            mWidth = getMeasuredHeight();
        } else {
            mWidth = getMeasuredWidth();
        }
    }

    private void initPaint() {
        if (mPaint == null) {
            mPaint = new Paint();
        }
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        if (StringUtils.isEmpty(mFriendKey)) {
            mPaint.setColor(ColorUtils.getOwnerColor());
        } else {
            mPaint.setColor(ColorUtils.getFriendColor(mFriendKey));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mCanvas = canvas;
        initPaint();
        mPaint.setStrokeWidth(2);
        mCanvas.drawRoundRect(0, 0, mWidth, mWidth,
            ScreenUtils.dimen2px(mContext, R.dimen.portrait_corner), ScreenUtils.dp2px(mContext, 4),
            mPaint);

        if (!TextUtils.isEmpty(mMsg)) {
            mPaint.setTextSize(mWidth / 2);
            mPaint.setColor(Color.WHITE);
            mPaint.setTextAlign(Paint.Align.CENTER);
            Rect bounds = new Rect();
            mPaint.getTextBounds(mMsg, 0, mMsg.length(), bounds);
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            float baseline = (mWidth - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            mCanvas.drawText(mMsg, mWidth / 2, baseline, mPaint);
        }
    }

    public void setFriendText(String friendKey, CharSequence text) {
        LogUtil.i("PortraitView", "friendKey:" + friendKey);
        if (!TextUtils.isEmpty(text)) {
            mFriendKey = friendKey;
            mMsg = String.valueOf(text.charAt(0)).toUpperCase();
            invalidate();
        }
    }
}

