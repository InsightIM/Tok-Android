package com.client.tok.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.view.inputmethod.InputMethodManager;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class SystemUtils {

    public static void hideSoftKeyBoard(Activity activity) {
        try {
            InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive() && activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openSoftKeyBoard(Activity activity) {
        try {
            InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive() && activity.getCurrentFocus() != null) {
                imm.showSoftInput(activity.getCurrentFocus(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyTxt2Clipboard(Context context, String content) {
        ClipboardManager cm =
            (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("content", content);
        cm.setPrimaryClip(data);
    }

    public static CharSequence getLastClipContent(Context context) {
        ClipboardManager clipboard =
            (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null) {
            ClipData.Item item = clipData.getItemAt(0);
            if (item != null) {
                return item.coerceToText(context);
            }
        }

        return null;
    }

    public static void clearLastClip(Context context) {
        ClipboardManager clipboard =
            (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(null, ""));
    }

    public static boolean isSoftShowing(Activity activity) {
        int screenHeight = activity.getWindow().getDecorView().getHeight();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return screenHeight * 2 / 3 > rect.bottom;
    }

    /**
     * get App versionCode
     */
    public static String getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String versionCode = "";
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static boolean isActivityExist(Context context, String className) {
        Intent intent = new Intent();
        intent.setClassName(context, className);
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
        return list != null && list.size() > 0;
    }

    public static boolean isActivityRunning(Context context, String className) {
        Intent intent = new Intent();
        intent.setClassName(context, className);
        ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
        boolean flag = false;
        if (cmpName != null) {
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList =
                am.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
}
