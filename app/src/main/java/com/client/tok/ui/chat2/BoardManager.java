package com.client.tok.ui.chat2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.SystemUtils;
import com.client.tok.widget.KeyboardRecordSwitchView;
import com.client.tok.widget.RecorderBtn;

/**
 * board manager:layout on the chat bottom,such as:send msg,file selector,recorder; totally 7
 * 1.button, open layout: file selector(album,camera,file)
 * 2.button, toggle record and keyboard
 * 3.EditText,input message
 * 4.recorder button
 * 5.send button
 * 6.layout include(album,camera,file)
 * 7.keyboard
 */

public class BoardManager implements KeyboardRecordSwitchView.SwitchListener {
    private String TAG = "ChatKeyboard";
    private static final String SHARE_PREFERENCE_NAME = "EmotionKeyboard";
    private static final String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "SoftInputHeight";

    private Activity mActivity;
    private SharedPreferences mShareP;

    private View mContentView;
    private KeyboardRecordSwitchView mInputRecordBtSw;
    private View mExtendFileLayout;//file selector layout
    private View mEmotionLayout;
    private EditText mInputEt;
    private RecorderBtn mRecorderView;
    private ImageView mSendIv;

    private BoardManager() {

    }

    public static BoardManager with(Activity activity) {
        BoardManager richMsgKeyboard = new BoardManager();
        richMsgKeyboard.mActivity = activity;
        richMsgKeyboard.mShareP =
            activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return richMsgKeyboard;
    }

    /**
     * bind view，make the bar's height fixed
     */
    public BoardManager bindToContent(View contentView) {
        mContentView = contentView;
        return this;
    }

    public BoardManager bindToEditText(EditText editText) {
        mInputEt = editText;
        mInputEt.requestFocus();
        mInputEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    View curShowLayout = null;
                    if (mEmotionLayout != null && mEmotionLayout.isShown()) {
                        curShowLayout = mEmotionLayout;
                    } else if (mExtendFileLayout != null && mExtendFileLayout.isShown()) {
                        curShowLayout = mExtendFileLayout;
                    }
                    if (curShowLayout != null) {
                        BoardManager.this.lockContentHeight();
                        BoardManager.this.hideExtendLayout(curShowLayout, true);
                        mInputEt.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                unlockContentHeightDelayed();
                            }
                        }, 200L);
                    }
                }
                return false;
            }
        });
        return this;
    }

    public BoardManager bindToEmotionBtn(View emotionBtn) {
        emotionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout != null && mEmotionLayout.isShown()) {
                    BoardManager.this.lockContentHeight();
                    BoardManager.this.hideExtendLayout(mEmotionLayout, true);
                    BoardManager.this.unlockContentHeightDelayed();
                } else {
                    if (BoardManager.this.isSoftInputShown()) {
                        BoardManager.this.lockContentHeight();
                        BoardManager.this.showExtendLayout(mEmotionLayout);
                        BoardManager.this.unlockContentHeightDelayed();
                    } else {
                        if (mExtendFileLayout.isShown()) {
                            BoardManager.this.hideExtendLayout(mExtendFileLayout, false);
                        }
                        BoardManager.this.showExtendLayout(mEmotionLayout);
                    }
                }
            }
        });
        return this;
    }

    public BoardManager setEmotionLayout(View emotionLayout) {
        this.mEmotionLayout = emotionLayout;
        return this;
    }

    public BoardManager bindToExtendBtn(View addExtendFileBtn) {
        addExtendFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mExtendFileLayout != null && mExtendFileLayout.isShown()) {
                    BoardManager.this.lockContentHeight();
                    BoardManager.this.hideExtendLayout(mExtendFileLayout, true);
                    BoardManager.this.unlockContentHeightDelayed();
                } else {
                    if (BoardManager.this.isSoftInputShown()) {
                        BoardManager.this.lockContentHeight();
                        BoardManager.this.showExtendLayout(mExtendFileLayout);
                        BoardManager.this.unlockContentHeightDelayed();
                    } else {
                        if (mEmotionLayout != null && mEmotionLayout.isShown()) {
                            BoardManager.this.hideExtendLayout(mEmotionLayout, false);
                        }
                        BoardManager.this.showExtendLayout(mExtendFileLayout);
                    }
                }
            }
        });
        return this;
    }

    public BoardManager setExtendFileLayout(View extendLayout) {
        mExtendFileLayout = extendLayout;
        return this;
    }

    public BoardManager setRecordInputBtSw(KeyboardRecordSwitchView inputRecordBtSw) {
        mInputRecordBtSw = inputRecordBtSw;
        mInputRecordBtSw.setSwitchListener(this);
        return this;
    }

    public BoardManager setRecordLayout(RecorderBtn recordBtn) {
        mRecorderView = recordBtn;
        return this;
    }

    public BoardManager setSendBt(ImageView sendBt) {
        mSendIv = sendBt;
        return this;
    }

    private void lockContentHeight() {
        LinearLayout.LayoutParams params =
            (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    private void unlockContentHeightDelayed() {
        mInputEt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }

    private void showSoftInput() {
        SystemUtils.openSoftKeyBoard(mActivity);
    }

    private void hideSoftInput() {
        SystemUtils.hideSoftKeyBoard(mActivity);
    }

    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    @Override
    public void onKeyboard() {
        showInputLayout();
    }

    @Override
    public void onRecord() {
        showRecordLayout();
    }

    private void hideLayoutWithFixHeight(View layout, boolean isShowSoft) {
        lockContentHeight();
        hideExtendLayout(mExtendFileLayout, isShowSoft);
        unlockContentHeightDelayed();
    }

    private void showLayoutWithFixHeight(View layout) {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = mShareP.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 778);
        }
        LogUtil.i(TAG, "softInputHeight:" + softInputHeight);
        if (layout != null) {
            layout.getLayoutParams().height = softInputHeight;
            layout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * show file selector layout
     * No.2-->toggle button show keyboard
     * No.3-->EditText visible
     * No.4-->record button invisible
     * No.5-->send button visible
     * No.6-->file selector layout show
     * No.7-->keyboard hide
     *
     * @param layout the view should be visible
     */
    private void showExtendLayout(View layout) {
        //No.2
        mInputRecordBtSw.showKeyboard();
        //No.3
        mInputEt.setVisibility(View.VISIBLE);
        //No.4
        mRecorderView.setVisibility(View.GONE);
        //No.5
        mSendIv.setVisibility(View.VISIBLE);
        //No.6
        showLayoutWithFixHeight(layout);
        //No.7
        hideSoftInput();
    }

    /**
     * hide file selector layout
     * No.2-->toggle button show keyboard
     * No.3-->EditText visible
     * No.4-->record button invisible
     * No.5-->send button visible
     * No.6-->file selector layout show
     * No.7-->keyboard is visible by parameter
     *
     * @param layout the view need invisible
     * @param showSoftInput is show keyboard
     */
    private void hideExtendLayout(View layout, boolean showSoftInput) {
        if (layout != null && layout.isShown()) {
            layout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
                mInputRecordBtSw.showRecord();
            } else {
                mInputRecordBtSw.showKeyboard();
            }
        }
    }

    /**
     * No.2 -->toggle button show keyboard
     * No.3 -->EditText invisible
     * No.4 -->record button visible
     * No.5-->send button invisible
     * No.6-->file selector layout invisible
     * No.7-->keyboard is visible by parameter
     */
    private void showRecordLayout() {
        //No.2
        mInputRecordBtSw.showKeyboard();
        //No.3
        mInputEt.setVisibility(View.GONE);
        //No.4
        mRecorderView.setVisibility(View.VISIBLE);
        //No.5
        mSendIv.setVisibility(View.GONE);
        //No.6
        mExtendFileLayout.setVisibility(View.GONE);
        //No.7
        hideSoftInput();
    }

    /**
     * No.2 -->toggle button show record
     * No.3 -->EditText visible
     * No.4 -->record button invisible
     * No.5-->send button visible
     * No.6-->file selector layout invisible
     * No.7-->keyboard is visible
     */
    private void showInputLayout() {
        //No.2
        mInputRecordBtSw.showRecord();
        //No.3
        mInputEt.setVisibility(View.VISIBLE);
        mInputEt.requestFocus();
        mInputEt.setSelection(mInputEt.getText().length());
        //No.4
        mRecorderView.setVisibility(View.GONE);
        //No.5
        mSendIv.setVisibility(View.VISIBLE);
        //No.6 +No.7
        if (mExtendFileLayout.isShown()) {
            lockContentHeight();
            hideExtendLayout(mExtendFileLayout, true);
            unlockContentHeightDelayed();
        } else {
            showSoftInput();
        }
    }

    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView is the top view of the window，we can get decorView by window's getDecorView
         * decorView can get display area(include title bar,but status bar)
         */
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;

        LogUtil.i(TAG, "ScreenHeight:"
            + screenHeight
            + ",activityBottom:"
            + r.bottom
            + ",softInputHeight:"
            + softInputHeight);
        /**
         * some android ,when keyboard not show,the height always 144,
         * because it include the bottom virtual keystroke bar's height
         * when apk >20,we need minus the height
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            int softBtnBarHeight = getNavigationBarHeight();
            softInputHeight = softInputHeight - getNavigationBarHeight();
            LogUtil.i(TAG,
                "softInputHeight:" + softInputHeight + ",softBtnBarHeight:" + softBtnBarHeight);
        }

        if (softInputHeight < 0) {
            LogUtil.i(TAG, "EmotionKeyboard--Warning: value of softInputHeight is below zero!");
            softInputHeight = 0;
        }
        if (softInputHeight > 0) {
            mShareP.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply();
        }
        return softInputHeight;
    }

    /**
     * Height of the bottom virtual keystroke bar
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getNavigationBarHeight() {
        Resources resources = mActivity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public BoardManager build() {
        mActivity.getWindow()
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        hideSoftInput();
        return this;
    }

    public boolean isRichMsgLayoutShowing() {
        return (mExtendFileLayout != null && mExtendFileLayout.isShown()) || (mEmotionLayout != null
            && mEmotionLayout.isShown());
    }

    public void hideRichMsgLayout() {
        hideExtendLayout(mExtendFileLayout, false);
        hideExtendLayout(mEmotionLayout, false);
    }

    /**
     * when click back should be hide bottom layout first
     */
    public boolean interceptBackPress() {
        if (isRichMsgLayoutShowing()) {
            hideRichMsgLayout();
            return true;
        }
        return false;
    }
}
