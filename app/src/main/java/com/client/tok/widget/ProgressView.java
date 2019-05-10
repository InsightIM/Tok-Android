package com.client.tok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ViewUtil;

public class ProgressView extends RelativeLayout {
    // progress show in text:image or file progress
    public static final int TYPE_TXT = 1;
    //progress show in image:audio progress/sender
    public static final int TYPE_IMG_SENDER = 2;
    //progress show in image:audio progress/receiver
    public static final int TYPE_IMG_RECEIVER = 3;
    private View mTxtLayout;
    private TextView mTxtProTv;
    private ImageView mImgProTv;
    private ImageView mFinishIconIv;
    private int mProType = TYPE_TXT;
    //if file transfer finished,what icon show. this is useful to video, show the play icon
    private int finishShowIconId = 0;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_loading_progress);
        this.addView(rootView);
        mTxtLayout = rootView.findViewById(R.id.id_txt_pro_layout);
        mTxtProTv = rootView.findViewById(R.id.id_txt_pro_tv);
        mImgProTv = rootView.findViewById(R.id.id_img_pro_iv);
        mFinishIconIv = rootView.findViewById(R.id.id_finish_show_icon_iv);
    }

    public void setProType(int proType) {
        mProType = proType;
    }

    public void setFinishIcon(int iconId) {
        finishShowIconId = iconId;
    }

    public void setProgress(long total, long curPosition) {
        setViewShowByType(mProType);
        if (curPosition >= total) {
            setSuccess();
        } else {
            mFinishIconIv.setVisibility(View.GONE);
            if (mProType == TYPE_IMG_SENDER || mProType == TYPE_IMG_RECEIVER) {
                mImgProTv.setImageResource(R.drawable.msg_sending);
            } else {
                mTxtProTv.setText(StringUtils.formatHtmlTxFromResId(R.string.percent,
                    (int) curPosition * 100 / total));
            }
        }
    }

    public void setSuccess() {
        setViewShowByType(mProType);
        if (mProType == TYPE_IMG_SENDER) {
            mImgProTv.setImageResource(R.drawable.msg_success);
        } else {
            mImgProTv.setVisibility(GONE);
            mTxtLayout.setVisibility(GONE);
            mTxtProTv.setVisibility(GONE);
        }
        if (finishShowIconId > 0) {
            mFinishIconIv.setImageResource(finishShowIconId);
            mFinishIconIv.setVisibility(View.VISIBLE);
        } else {
            mFinishIconIv.setVisibility(View.GONE);
        }
    }

    private void setViewShowByType(int proType) {
        this.setVisibility(View.VISIBLE);
        switch (proType) {
            case TYPE_TXT:
                mTxtLayout.setVisibility(View.VISIBLE);
                mTxtProTv.setVisibility(View.VISIBLE);
                mImgProTv.setVisibility(View.GONE);
                break;
            case TYPE_IMG_SENDER:
            case TYPE_IMG_RECEIVER:
                mImgProTv.setVisibility(View.VISIBLE);
                mTxtLayout.setVisibility(View.GONE);
                break;
        }
    }
}
