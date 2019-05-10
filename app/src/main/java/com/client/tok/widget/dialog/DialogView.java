package com.client.tok.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.utils.ScreenUtils;
import com.client.tok.utils.StringUtils;

public class DialogView extends Dialog {
    private LinearLayout rootLayout;
    private LinearLayout contentLayout;
    private FrameLayout customLayout;
    private View btnLayout;
    private TextView titleTv, contentTv;
    private Button leftBt, rightBt;
    private Activity activity;
    private View customView;
    private View.OnClickListener leftListener;
    private View.OnClickListener rightListener;

    private boolean autoDismiss = true;

    private int type;

    public static final int DIALOG_LOADING = 1;

    public DialogView(Activity activity) {
        this(activity, true);
    }

    public DialogView(Activity activity, boolean isFromBottom) {
        super(activity, R.style.CommonDialog);
        this.activity = activity;
        init();
        float percent = isFromBottom ? 1 : 0.8f;
        initWindow(percent, isFromBottom);
    }

    public DialogView(Activity activity, View customView, int type) {
        super(activity, R.style.CommonDialog);
        this.activity = activity;
        this.customView = customView;
        this.type = type;
        init();
    }

    public DialogView(Activity activity, View customView, boolean isFromBottom) {
        super(activity, R.style.CommonDialog);
        this.activity = activity;
        this.customView = customView;
        init();
        if (isFromBottom) {
            initWindow(1, isFromBottom);
        } else {
            initWindow(0.8, isFromBottom);
        }
    }

    private void init() {
        setContentView(R.layout.dialog_common);
        rootLayout = findViewById(R.id.id_dialog_root_layout);
        contentLayout = findViewById(R.id.id_dialog_content_layout);
        titleTv = findViewById(R.id.id_dialog_title_tv);
        contentTv = findViewById(R.id.id_dialog_content_tv);
        btnLayout = findViewById(R.id.id_dialog_btn_layout);
        leftBt = findViewById(R.id.id_dialog_left_bt);
        leftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leftListener != null) {
                    leftListener.onClick(v);
                }
                DialogView.this.dismiss();
            }
        });
        rightBt = findViewById(R.id.id_dialog_right_bt);
        rightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightListener != null) {
                    rightListener.onClick(v);
                }
                if (autoDismiss) {
                    DialogView.this.dismiss();
                }
            }
        });
        customLayout = findViewById(R.id.id_dialog_custom_layout);
        if (customView != null) {
            titleTv.setVisibility(View.GONE);
            contentLayout.setVisibility(View.GONE);
            customLayout.setVisibility(View.VISIBLE);
            customLayout.removeAllViews();
            customLayout.addView(customView);
        }
        if (type == DIALOG_LOADING) {
            rootLayout.setBackgroundResource(R.color.transparent);
            setCanceledOnTouchOutside(false);
            setCancelable(false);
            leftBt.setVisibility(View.GONE);
            rightBt.setVisibility(View.GONE);
        } else {
            setCanceledOnTouchOutside(true);
        }
    }

    private void initWindow(double percent, boolean isFromBottom) {
        if (!isFromBottom) {
            rootLayout.setBackgroundResource(R.drawable.dialog_common_center_bg);
        } else {
            rootLayout.setBackgroundResource(R.drawable.dialog_common_bottom_bg);
        }

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (ScreenUtils.getScreenWidth(activity) * percent);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (isFromBottom) {
            window.setGravity(Gravity.BOTTOM);
        }
        window.setAttributes(lp);
    }

    public DialogView showTitle(boolean isShowTitle) {
        if (isShowTitle) {
            titleTv.setVisibility(View.VISIBLE);
        } else {
            titleTv.setVisibility(View.GONE);
        }
        return this;
    }

    public DialogView setTitle_(int titleId) {
        return setTitle_(StringUtils.getTextFromResId(titleId));
    }

    public DialogView setTitle_(CharSequence title) {
        if (StringUtils.isEmpty(title)) {
            titleTv.setVisibility(View.GONE);
        } else {
            titleTv.setVisibility(View.VISIBLE);
            titleTv.setText(title);
        }
        return this;
    }

    public DialogView setTitleCenter() {
        titleTv.setGravity(Gravity.CENTER_HORIZONTAL);
        return this;
    }

    public DialogView setContent(CharSequence content) {
        if (StringUtils.isEmpty(content)) {
            contentLayout.setVisibility(View.GONE);
        } else {
            contentLayout.setVisibility(View.VISIBLE);
            contentTv.setText(content);
        }
        return this;
    }

    public DialogView setCanCancel(boolean cancelable) {
        setCancelable(cancelable);
        setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public DialogView setLeftButtonTxt(int leftTxt) {
        leftBt.setText(leftTxt);
        return this;
    }

    public DialogView setLeftButtonTxt(String leftTxt) {
        leftBt.setText(leftTxt);
        return this;
    }

    public DialogView setRightButtonTxt(int rightTxt) {
        rightBt.setText(rightTxt);
        return this;
    }

    public DialogView setRightButtonTxt(String rightTxt) {
        rightBt.setText(rightTxt);
        return this;
    }

    public DialogView setLeftOnClickListener(View.OnClickListener onClickListener) {
        leftListener = onClickListener;
        return this;
    }

    public DialogView setRightOnClickListener(View.OnClickListener onClickListener) {
        rightListener = onClickListener;
        return this;
    }

    public DialogView setBtnLayout(boolean visible) {
        btnLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public DialogView setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
        return this;
    }
}
