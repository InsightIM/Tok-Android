package com.client.tok.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import com.airbnb.lottie.LottieAnimationView;
import com.client.tok.R;
import com.client.tok.widget.dialog.DialogView;

public class LoadingUtil {
    private String TAG = "LoadingUtil2";
    private static LoadingUtil sInstance;
    private View mLoadingView;
    private DialogView mDialogView;
    private Activity mActivity;
    private LottieAnimationView lottieAnimationView;

    private LoadingUtil() {
    }

    public void initView(Activity activity) {
        try {
            if (mLoadingView == null) {
                mLoadingView =
                    ViewUtil.inflateViewById(activity, R.layout.layout_dialog_common_loading);
                lottieAnimationView = mLoadingView.findViewById(R.id.animation_view);
            }
            lottieAnimationView.setProgress(0f);
            lottieAnimationView.playAnimation();

            mActivity = activity;
            if (mLoadingView.getParent() != null) {
                ((FrameLayout) mLoadingView.getParent()).removeAllViews();
            }
            if (mDialogView == null) {
                mDialogView = new DialogView(mActivity, mLoadingView, DialogView.DIALOG_LOADING);
            }
        } catch (Exception e) {
            LogUtil.i(TAG, e.getLocalizedMessage());
        }
    }

    public void disappear() {
        if (lottieAnimationView != null) {
            lottieAnimationView.cancelAnimation();
            lottieAnimationView = null;
            if (mDialogView != null) {
                if (mDialogView.isShowing() && !activityIsDestroy()) {
                    mDialogView.dismiss();
                    mDialogView.hide();
                }
                mDialogView = null;
            }
        }
        mActivity = null;
        sInstance = null;
    }

    private boolean activityIsDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed();
        } else {
            return mActivity == null || mActivity.isFinishing();
        }
    }

    public static void show(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (sInstance == null || sInstance.mActivity != activity) {
            dismiss();
            sInstance = new LoadingUtil();
            sInstance.initView(activity);
            sInstance.mDialogView.show();
        }
    }

    public static void dismiss() {
        if (sInstance != null) {
            sInstance.disappear();
        }
    }
}
