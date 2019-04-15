package com.client.tok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.client.tok.R;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.ViewUtil;

public class SafePromptView extends FrameLayout implements View.OnClickListener {
    public SafePromptView(Context context) {
        super(context);
        initView(context);
    }

    public SafePromptView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SafePromptView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public SafePromptView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_safe_prompt);
        rootView.setOnClickListener(this);
        addView(rootView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_safe_prompt_layout:
                PageJumpIn.jumpSafePage(getContext());
                break;
        }
    }
}
