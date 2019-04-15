package im.tox.utils;

import android.util.Log;

public class LogCoreUtil {
    private static boolean mIsShowLog = true;
    private static String TAG = "Tok";

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (mIsShowLog) {
            msg = formNull(msg);
            Log.i(tag, msg);
           // System.out.print(tag + "," + msg);
        }
    }

    public static void i(Object tag, String msg) {
        i(tag.getClass().getName(), msg);
    }

    private static String formNull(String value) {
        return value == null ? "null" : value;
    }
}
