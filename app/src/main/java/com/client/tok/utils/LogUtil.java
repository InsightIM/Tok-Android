package com.client.tok.utils;

import android.util.Log;
import com.client.tok.BuildConfig;

public class LogUtil {
    private static boolean mIsShowLog = BuildConfig.LOG_DEBUG;
    private static String TAG = "Tok";

    public static String makeLogTag(Class cls) {
        return cls.getSimpleName();
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (mIsShowLog) {
            msg = formNull(msg);
            Log.v(tag, msg);
        }
    }

    public static void v(Object tag, String msg) {
        v(tag.getClass().getName(), msg);
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (mIsShowLog) {
            msg = formNull(msg);
            Log.d(tag, msg);
        }
    }

    public static void d(Object tag, String msg) {
        d(tag.getClass().getName(), msg);
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (mIsShowLog) {
            msg = formNull(msg);
            Log.i(tag, msg);
        }
    }

    public static void i(Object tag, String msg) {
        i(tag.getClass().getName(), msg);
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (mIsShowLog) {
            msg = formNull(msg);
            Log.w(tag, msg);
        }
    }

    public static void w(Object tag, String msg) {
        w(tag.getClass().getName(), msg);
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (mIsShowLog) {
            msg = formNull(msg);
            Log.e(tag, msg);
        }
    }

    public static void e(Object tag, String msg) {
        e(tag.getClass().getName(), msg);
    }

    private static String formNull(String value) {
        return value == null ? "null" : value;
    }
}
