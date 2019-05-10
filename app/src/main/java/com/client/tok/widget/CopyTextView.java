package com.client.tok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.client.tok.R;
import com.client.tok.utils.SystemUtils;
import com.client.tok.utils.ToastUtils;

public class CopyTextView extends MsgTextView {
    public CopyTextView(Context context) {
        super(context);
        addLongClickListener();
    }

    public CopyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addLongClickListener();
    }

    public CopyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addLongClickListener();
    }

    private void addLongClickListener() {
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SystemUtils.copyTxt2Clipboard(CopyTextView.this.getContext(),
                    CopyTextView.this.getText().toString());
                ToastUtils.show(R.string.copy_success);
                return true;
            }
        });
    }
}
