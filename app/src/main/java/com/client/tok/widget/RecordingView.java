package com.client.tok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.client.tok.R;
import com.client.tok.utils.ViewUtil;

public class RecordingView extends FrameLayout {
    private Context mContext;
    private TextView mPromptTv;
    private LottieAnimationView mLoadingView;//loading
    private ImageView mCancelIv;

    // slide up cancel
    private int MODE_SLIDE_UP_CANCEL = 1;
    //release to cancel
    private int MODE_RELEASE_TO_CANCEL = 2;

    private int mCurMode = 0;

    public RecordingView(Context context) {
        this(context, null);
    }

    public RecordingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordingView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RecordingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView(mContext);
    }

    private void initView(Context context) {
        ViewUtil.inflateViewById(context, R.layout.view_recording, this, true);
        mPromptTv = findViewById(R.id.id_record_prompt_tv);
        mLoadingView = findViewById(R.id.id_loading_view);
        mCancelIv = findViewById(R.id.id_record_cancel_view);
    }

    public void showSlideCancel() {
        if (mCurMode != MODE_SLIDE_UP_CANCEL) {
            mPromptTv.setText(R.string.slide_up_to_cancel);
            mLoadingView.setVisibility(View.VISIBLE);
            mCancelIv.setVisibility(View.GONE);
            mCurMode = MODE_SLIDE_UP_CANCEL;
        }
    }

    public void showReleaseCancel() {
        if (mCurMode != MODE_RELEASE_TO_CANCEL) {
            mPromptTv.setText(R.string.release_to_cancel);
            mLoadingView.setVisibility(View.GONE);
            mCancelIv.setVisibility(View.VISIBLE);
            mCurMode = MODE_RELEASE_TO_CANCEL;
        }
    }

    public void show() {
        if (!this.isShown()) {
            this.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        if (!this.isShown()) {
            this.setVisibility(View.GONE);
        }
    }
}
