package com.client.tok.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ViewUtil;

public class HeadInfoView extends RelativeLayout {
    private PortraitView mAvatarView;
    private TextView mTitleTv;
    private TextView mInfoTv;
    private ImageView mFunctionIv;
    private int mTitleId;
    private int mInfoId;
    private int mFunIconId;

    public HeadInfoView(Context context) {
        super(context);
        initView(context);
    }

    public HeadInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readData(context, attrs);
        initView(context);
    }

    public HeadInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readData(context, attrs);
        initView(context);
    }

    public HeadInfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readData(context, attrs);
        initView(context);
    }

    private void readData(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.headInfo);
        mTitleId = ta.getResourceId(R.styleable.headInfo_titleId, 0);
        mInfoId = ta.getResourceId(R.styleable.headInfo_infoId, 0);
        mFunIconId = ta.getResourceId(R.styleable.headInfo_hRightIconId, 0);
        ta.recycle();
    }

    private void initView(Context context) {
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_head_info_view);
        this.addView(rootView);
        mTitleTv = this.findViewById(R.id.id_head_title_tv);
        setTitle(mTitleId);

        mInfoTv = this.findViewById(R.id.id_head_info_tv);
        setContent(mInfoId);

        mAvatarView = this.findViewById(R.id.id_head_avatar_civ);

        mFunctionIv = this.findViewById(R.id.id_head_function_iv);
        setFunctionIcon(mFunIconId);
    }

    public void setTitle(int resId) {
        setTitle(StringUtils.getTextFromResId(resId));
    }

    public void setTitle(CharSequence content) {
        if (!StringUtils.isEmpty(content)) {
            mTitleTv.setVisibility(View.VISIBLE);
            mTitleTv.setText(content);
        } else {
            mTitleTv.setVisibility(View.GONE);
        }
    }

    public void setContent(int resId) {
        setContent(StringUtils.getTextFromResId(resId));
    }

    public void setContent(CharSequence content) {
        if (!StringUtils.isEmpty(content)) {
            mInfoTv.setVisibility(View.VISIBLE);
            mInfoTv.setText(content);
        } else {
            mInfoTv.setVisibility(View.GONE);
        }
    }

    public void setAvatar(String tokId, String name) {
        if (!StringUtils.isEmpty(name)) {
            mAvatarView.setFriendText(tokId, name);
            mAvatarView.setVisibility(View.VISIBLE);
        } else {
            mAvatarView.setVisibility(View.GONE);
        }
    }

    public void setAvatarId(int avatarId) {
        if (avatarId > 0) {
            mAvatarView.setAvatarId(avatarId);
            mAvatarView.setVisibility(View.VISIBLE);
        } else {
            mAvatarView.setVisibility(View.GONE);
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
        mAvatarView.setClickEnterDetail(isEnterDetail);
    }

    public void setFunctionListener(OnClickListener listener) {
        mFunctionIv.setOnClickListener(listener);
    }
}
