package com.client.tok.utils;

import android.os.Looper;

public class ThreadUtils {
    private static String TAG = "ThreadUtils";

    public static boolean isMainThread() {
        boolean isMain = Looper.getMainLooper().getThread() == Thread.currentThread();
        LogUtil.i(TAG, "isMainThread:" + isMain);
        return isMain;
    }
}
