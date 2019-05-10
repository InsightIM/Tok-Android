package com.client.tok.utils;

import android.os.Looper;
import com.client.tok.TokApplication;

public class ThreadUtils {
    private static String TAG = "ThreadUtils";

    public static boolean isMainThread() {
        boolean isMain = Looper.getMainLooper().getThread() == Thread.currentThread();
        LogUtil.i(TAG, "isMainThread:" + isMain);
        return isMain;
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            TokApplication.getInstance().getHandler().post(runnable);
        } else {
            TokApplication.getInstance().getHandler().postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        TokApplication.getInstance().getHandler().removeCallbacks(runnable);
    }
}
