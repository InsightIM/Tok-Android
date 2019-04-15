package com.client.tok.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.utils.ViewUtil;

public class BottomTabView extends FrameLayout {
    private ImageView mImgTv;
    private TextView mTxtTv;
    private UnreadMsgView mNewMsgTv;

    private int mIconId;
    private int mTxtId;
    private int mTxtColorId;
    private int mTxtSizeId;
    private boolean mIsSelected;

    public BottomTabView(Context context) {
        super(context);
    }

    public BottomTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readData(context, attrs);
        initView(context);
    }

    public BottomTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readData(context, attrs);
        initView(context);
    }

    public BottomTabView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readData(context, attrs);
        initView(context);
    }

    private void readData(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.tab);
        mIconId = ta.getResourceId(R.styleable.tab_iconId, 0);
        mTxtId = ta.getResourceId(R.styleable.tab_txtId, 0);
        mTxtColorId = ta.getColor(R.styleable.tab_txtColorId, Color.BLUE);
        mTxtSizeId = (int) ta.getDimension(R.styleable.tab_txtSizeId, 12);
        mIsSelected = ta.getBoolean(R.styleable.tab_isSelected, false);
        ta.recycle();
    }

    private void initView(Context context) {
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_bottom_tab);
        this.addView(rootView);
        mImgTv = this.findViewById(R.id.id_img_iv);
        mTxtTv = this.findViewById(R.id.id_txt_tv);

        mImgTv.setImageResource(mIconId);

        mTxtTv.setText(mTxtId);

        mNewMsgTv = findViewById(R.id.id_bottom_new_msg_tv);

        setSelected(mIsSelected);
    }

    public void setNewFeature(String newContent) {
        mNewMsgTv.setNewFeature(newContent);
    }

    public void setNewMsg(int msgNum) {
        mNewMsgTv.setUnreadNum(msgNum);
    }

    public void setSelected(boolean selected) {
        mImgTv.setSelected(selected);
        mTxtTv.setSelected(selected);
    }
}
