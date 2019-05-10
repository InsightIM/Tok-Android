package com.client.tok.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.TextViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.client.tok.R;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ViewUtil;

public class ItemInfoView extends RelativeLayout {
    private int SHOW = 1;
    private int HIDE = 0;
    private Context mContext;
    private ImageView mLeftIv;
    private ImageView mRightIv;
    private ImageView mFunctionIv;
    private TextView mPromptTv;
    private TextView mContentTv;
    private TextView mDetailTv;
    private PortraitView mPortraitView;
    private ToggleButton mTb;
    private View mLineView;
    private int mPromptId;
    private int mContentId;
    private int mDetailId;
    private int mLeftIconId;
    private int mFunIconId;
    private int mRightIconId;

    private int mContentMaxLine;
    private int mRightIconVisible;
    private int mTbVisible;
    private int mLineVisible;

    public ItemInfoView(Context context) {
        super(context);
        mContext = context;
    }

    public ItemInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        readData(context, attrs);
        initView(context);
    }

    public ItemInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        readData(context, attrs);
        initView(context);
    }

    public ItemInfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        readData(context, attrs);
        initView(context);
    }

    private void readData(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.fc);
        mLeftIconId = ta.getResourceId(R.styleable.fc_lefIconId, 0);
        mPromptId = ta.getResourceId(R.styleable.fc_promptId, 0);
        mContentId = ta.getResourceId(R.styleable.fc_contentId, 0);
        mDetailId = ta.getResourceId(R.styleable.fc_detail, 0);
        mContentMaxLine = ta.getInteger(R.styleable.fc_contentMaxLine, 1);
        mFunIconId = ta.getResourceId(R.styleable.fc_functionIconId, 0);
        mRightIconId = ta.getResourceId(R.styleable.fc_rightIconId, R.drawable.info_item_arrow);
        mRightIconVisible = ta.getInteger(R.styleable.fc_rightIconVisible, 1);
        mTbVisible = ta.getInteger(R.styleable.fc_tbVisible, 0);
        mLineVisible = ta.getInteger(R.styleable.fc_lineVisible, 0);
        ta.recycle();
    }

    private void initView(Context context) {
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_info_item);
        this.addView(rootView);
        mLeftIv = this.findViewById(R.id.id_info_item_left_icon_iv);
        if (mLeftIconId > 0) {
            mLeftIv.setImageResource(mLeftIconId);
            mLeftIv.setVisibility(View.VISIBLE);
        } else {
            mLeftIv.setVisibility(View.GONE);
            rootView.setPadding(0, rootView.getPaddingTop(), rootView.getPaddingRight(),
                rootView.getPaddingBottom());
        }
        mPromptTv = this.findViewById(R.id.id_info_item_prompt_tv);
        if (mPromptId > 0) {
            mPromptTv.setText(mPromptId);
            mPromptTv.setVisibility(View.VISIBLE);
        } else {
            mPromptTv.setVisibility(View.GONE);
        }

        mContentTv = this.findViewById(R.id.id_info_item_content_iv);
        setContent(mContentId);
        mContentTv.setMaxLines(mContentMaxLine);

        mDetailTv = this.findViewById(R.id.id_info_item_detail_tv);
        if (mDetailId > 0) {
            mDetailTv.setText(mDetailId);
            mDetailTv.setVisibility(View.VISIBLE);
        } else {
            mDetailTv.setVisibility(View.GONE);
        }

        mPortraitView = this.findViewById(R.id.id_info_item_portrait_view);

        mFunctionIv = this.findViewById(R.id.id_info_item_function_icon_iv);
        setFunctionIcon(mFunIconId);

        mRightIv = this.findViewById(R.id.id_info_item_right_icon_iv);
        if (mRightIconId > 0 && SHOW == mRightIconVisible) {
            mRightIv.setImageResource(mRightIconId);
            mRightIv.setVisibility(View.VISIBLE);
        } else {
            mRightIv.setVisibility(View.GONE);
            rootView.setPadding(rootView.getPaddingLeft(), rootView.getPaddingTop(), 0,
                rootView.getPaddingBottom());
        }

        mTb = this.findViewById(R.id.id_info_item_tb);
        if (SHOW == mTbVisible) {
            mTb.setVisibility(View.VISIBLE);
        } else {
            mTb.setVisibility(View.GONE);
        }

        mLineView = this.findViewById(R.id.id_info_item_line);
        if (SHOW == mLineVisible) {
            mLineView.setVisibility(View.VISIBLE);
        } else {
            mLineView.setVisibility(View.GONE);
        }
    }

    public void setPrompt(int resId) {
        setPrompt(StringUtils.getTextFromResId(resId));
    }

    public void setPrompt(CharSequence prompt) {
        if (!StringUtils.isEmpty(prompt)) {
            mPromptTv.setVisibility(View.VISIBLE);
            mPromptTv.setText(prompt);
        } else {
            mPromptTv.setVisibility(View.GONE);
        }
    }

    public void setContent(int resId) {
        setContent(StringUtils.getTextFromResId(resId));
    }

    public void setContent(CharSequence content) {
        setContent(content, -1, -1);
    }

    public void setContent(CharSequence content, int style, int bg) {
        if (!StringUtils.isEmpty(content)) {
            mContentTv.setVisibility(View.VISIBLE);
            mContentTv.setText(content);
            if (style > 0) {
                TextViewCompat.setTextAppearance(mContentTv, style);
            }
            if (bg > 0) {
                mContentTv.setBackgroundResource(bg);
            }
        } else {
            mContentTv.setVisibility(View.GONE);
        }
    }

    public void setPortrait(String tokId, String name) {
        if (!StringUtils.isEmpty(name)) {
            mPortraitView.setFriendText(tokId, name);
            mPortraitView.setVisibility(View.VISIBLE);
        } else {
            mPortraitView.setVisibility(View.GONE);
        }
    }

    public void setFunctionIcon(int imgId) {
        if (imgId > 0) {
            mFunctionIv.setImageResource(imgId);
            mFunctionIv.setVisibility(View.VISIBLE);
        } else {
            mFunctionIv.setVisibility(View.GONE);
        }
    }

    public void setClickEnterDetail(boolean isEnterDetail) {
        mPortraitView.setClickEnterDetail(isEnterDetail);
    }

    public void setToggleEnable(boolean enable) {
        mTb.setChecked(enable);
    }

    public void setToggleListener(OnClickListener listener) {
        if (listener != null) {
            mTb.setOnClickListener(listener);
        }
    }
}
