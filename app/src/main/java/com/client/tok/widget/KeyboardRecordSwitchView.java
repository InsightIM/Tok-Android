package com.client.tok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewSwitcher;
import com.client.tok.R;
import com.client.tok.utils.ViewUtil;

public class KeyboardRecordSwitchView extends FrameLayout implements View.OnClickListener {
    private Context mContext;
    private ViewSwitcher mSwitcherVs;
    private ImageView mKeyboardIv;
    private ImageView mRecordIv;
    private SwitchListener mSwitchListener;

    public KeyboardRecordSwitchView(Context context) {
        this(context, null);
    }

    public KeyboardRecordSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardRecordSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public KeyboardRecordSwitchView(Context context, AttributeSet attrs, int defStyleAttr,
        int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView(mContext);
    }

    private void initView(Context context) {
        ViewUtil.inflateViewById(context, R.layout.view_keyboard_recoard_switch, this, true);
        mSwitcherVs = findViewById(R.id.id_switcher_vs);
        mSwitcherVs.showNext();
        mKeyboardIv = findViewById(R.id.id_keyboard_iv);
        mRecordIv = findViewById(R.id.id_record_iv);
        mKeyboardIv.setOnClickListener(this);
        mRecordIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_keyboard_iv:
                mSwitcherVs.showNext();
                if (mSwitchListener != null) {
                    mSwitchListener.onKeyboard();
                }
                break;
            case R.id.id_record_iv:
                mSwitcherVs.showNext();
                if (mSwitchListener != null) {
                    mSwitchListener.onRecord();
                }
                break;
        }
    }

    public void showKeyboard() {
        mSwitcherVs.setDisplayedChild(0);
    }

    public void showRecord() {
        mSwitcherVs.setDisplayedChild(1);
    }

    public void setSwitchListener(SwitchListener listener) {
        mSwitchListener = listener;
    }

    public interface SwitchListener {
        void onKeyboard();

        void onRecord();
    }
}
