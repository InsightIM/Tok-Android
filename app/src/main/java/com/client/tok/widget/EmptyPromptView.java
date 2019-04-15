package com.client.tok.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.utils.ViewUtil;

public class EmptyPromptView extends FrameLayout implements View.OnClickListener {
    private TextView mMainContentTv;
    private TextView mSubContentTv;
    private TextView mBtn1Tv;
    private TextView mBtn2Tv;
    private int mMainContentId;
    private int mSubContentId;
    private int mBtnTxtId1;
    private int mBtnTxtId2;
    private OnClickListener mMainContentListener;
    private OnClickListener mBtn1Listener;
    private OnClickListener mBtn2Listener;

    public EmptyPromptView(Context context) {
        super(context);
        initView(context);
    }

    public EmptyPromptView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readData(context, attrs);
        initView(context);
    }

    public EmptyPromptView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readData(context, attrs);
        initView(context);
    }

    public EmptyPromptView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readData(context, attrs);
        initView(context);
    }

    private void readData(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.empty);
        mMainContentId = ta.getResourceId(R.styleable.empty_mainContentId, 0);
        mSubContentId = ta.getResourceId(R.styleable.empty_subContentId, 0);
        mBtnTxtId1 = ta.getResourceId(R.styleable.empty_btnTxtId1, 0);
        mBtnTxtId2 = ta.getResourceId(R.styleable.empty_btnTxtId2, 0);
        ta.recycle();
    }

    private void initView(Context context) {
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_empty_prompt);
        addView(rootView);
        mMainContentTv = this.findViewById(R.id.id_empty_main_content_tv);
        mMainContentTv.setOnClickListener(this);
        mSubContentTv = this.findViewById(R.id.id_empty_sub_content_tv);
        mBtn1Tv = this.findViewById(R.id.id_empty_btn1_tv);
        mBtn1Tv.setOnClickListener(this);

        mBtn2Tv = this.findViewById(R.id.id_empty_btn2_tv);
        mBtn2Tv.setOnClickListener(this);
        if (mMainContentId > 0) {
            mMainContentTv.setText(mMainContentId);
            mMainContentTv.setVisibility(View.VISIBLE);
        } else {
            mMainContentTv.setVisibility(View.GONE);
        }

        if (mSubContentId > 0) {
            mSubContentTv.setText(mSubContentId);
            mSubContentTv.setVisibility(View.VISIBLE);
        } else {
            mSubContentTv.setVisibility(View.GONE);
        }

        if (mBtnTxtId1 > 0) {
            mBtn1Tv.setText(mBtnTxtId1);
            mBtn1Tv.setVisibility(View.VISIBLE);
        } else {
            mBtn1Tv.setVisibility(View.GONE);
        }

        if (mBtnTxtId2 > 0) {
            mBtn2Tv.setText(mBtnTxtId2);
            mBtn2Tv.setVisibility(View.VISIBLE);
        } else {
            mBtn2Tv.setVisibility(View.GONE);
        }
    }

    public void setMainContentListener(OnClickListener listener) {
        mMainContentListener = listener;
    }

    public void setBtn1Listener(OnClickListener listener) {
        mBtn1Listener = listener;
    }

    public void setBtn2Listener(OnClickListener listener) {
        mBtn2Listener = listener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_empty_main_content_tv:
                if (mMainContentListener != null) {
                    mMainContentListener.onClick(v);
                }
                break;
            case R.id.id_empty_btn1_tv:
                if (mBtn1Listener != null) {
                    mBtn1Listener.onClick(v);
                }
                break;
            case R.id.id_empty_btn2_tv:
                if (mBtn2Listener != null) {
                    mBtn2Listener.onClick(v);
                }
                break;
        }
    }
}
