package com.client.tok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ViewUtil;

public class FileIconView extends FrameLayout {
    private String TAG = "PortraitView";
    private TextView mFileTypeTv;

    public FileIconView(Context context) {
        this(context, null);
    }

    public FileIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FileIconView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_file_icon);
        this.addView(rootView);
        mFileTypeTv = findViewById(R.id.id_file_type_tv);
    }

    public void setText(String fileName) {
        if (!StringUtils.isEmpty(fileName)) {
            String[] splits = fileName.split("\\.");
            if (splits.length > 1) {
                mFileTypeTv.setText(splits[splits.length - 1]);
                return;
            }
        }
        mFileTypeTv.setText("");
    }
}
