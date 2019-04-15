package com.client.tok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ViewUtil;

public class UnreadMsgView extends FrameLayout {
    private TextView mUnreadMsgTv;

    public UnreadMsgView(Context context) {
        super(context);
    }

    public UnreadMsgView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnreadMsgView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public UnreadMsgView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readData(context, attrs);
        initView(context);
    }

    private void readData(Context context, AttributeSet attrs) {
        //TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.fc);
        //ta.recycle();
    }

    private void initView(Context context) {
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_unread_msg);
        this.addView(rootView);
        mUnreadMsgTv = this.findViewById(R.id.id_unread_msg_tv);
    }

    public void setUnreadNum(int num) {
        if (num > 0) {
            setNewFeature(num > GlobalParams.MOST_UNREAD_MSG ? GlobalParams.MOST_UNREAD_MSG + "+"
                : String.valueOf(num));
        } else {
            setNewFeature(null);
        }
    }

    public void setNewFeature(String content) {
        if (StringUtils.isEmpty(content)) {
            this.setVisibility(View.GONE);
        } else {
            this.setVisibility(View.VISIBLE);
            mUnreadMsgTv.setText(content);
        }
    }
}
