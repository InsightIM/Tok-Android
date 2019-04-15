package com.client.tok.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.utils.ViewUtil;

public class GuideView extends FrameLayout {

    private TextView mTitleTv;
    private TextView mContentTv;
    private ImageView mImgTv;
    private int mTitleId;
    private int mContentId;
    private int mImgId;

    public GuideView(Context context) {
        super(context);
    }

    public GuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readData(context, attrs);
        initView(context);
    }

    public GuideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readData(context, attrs);
        initView(context);
    }

    public GuideView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readData(context, attrs);
        initView(context);
    }

    private void readData(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.guide);
        mTitleId = ta.getResourceId(R.styleable.guide_gTitleId, 0);
        mContentId = ta.getResourceId(R.styleable.guide_gContentId, 0);
        mImgId = ta.getResourceId(R.styleable.guide_gImgId, 0);
        ta.recycle();
    }

    private void initView(Context context) {
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_guide);
        this.addView(rootView);
        mTitleTv = this.findViewById(R.id.id_guide_title_tv);
        mContentTv = this.findViewById(R.id.id_guide_content_tv);
        mImgTv = this.findViewById(R.id.id_guide_iv);

        mTitleTv.setText(mTitleId);
        mContentTv.setText(mContentId);
        mImgTv.setImageResource(mImgId);
    }
}
