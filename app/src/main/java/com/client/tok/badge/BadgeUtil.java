package com.client.tok.badge;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import com.client.tok.utils.LogUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class BadgeUtil {
    private static String TAG = "badge";

    private BadgeUtil() {
    }

    /**
     * Retrieve launcher activity name of the application from the context
     *
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     * "android:name" attribute.
     */
    private static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        // To limit the components this Intent will resolve to, by setting an
        // explicit package name.
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // All Application must have 1 Activity at least.
        // Launcher activity must be found!
        ResolveInfo info =
            packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        // get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
        // if there is no Activity which has filtered by CATEGORY_DEFAULT
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }
        // ComponentName componentName = context.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName()).getComponent();
        // return componentName.getClassName();
        return info.activityInfo.name;
    }

    /**
     * <p/>
     * MIUI
     * Sony
     * Samsung
     * LG
     * HTC
     * Nova
     *
     * @param context context
     * @param count count
     */
    public static void setBadgeCount(Context context, int count, int iconResId) {
        try {
            if (count <= 0) {
                count = 0;
            } else {
                count = Math.max(0, Math.min(count, 99));
            }
            String manufacturer = Build.MANUFACTURER;
            switch (manufacturer.toLowerCase()) {
                case "samsung":
                case "lg":
                    setBadgeOfSumsung(context, count);
                    break;
                case "huawei":
                    setBadgeOfHuaWei(context, count);
                    break;
                case "xiaomi":
                    //xiao mi has problem
                    //setBadgeOfMIUI(context, count, iconResId);
                    break;
                case "vivo":
                    setBadgeOfVivo(context, count);
                    break;
                case "htc":
                    setBadgeOfHTC(context, count);
                    break;
                case "sony":
                    setBadgeOfSony(context, count);
                    break;
                case "nova":
                    setBadgeOfNova(context, count);
                    break;
                default:
                    setBadgeOfDefault(context, count);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    /**
     * @param context context
     * @param count count
     */
    private static void setBadgeOfSumsung(Context context, int count) {
        try {
            String launcherClassName = getLauncherClassName(context);
            if (launcherClassName == null) {
                return;
            }
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", count);
            intent.putExtra("badge_count_package_name", context.getPackageName());
            intent.putExtra("badge_count_class_name", launcherClassName);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }

    private static void setBadgeOfHuaWei(Context context, int count) {
        try {
            ComponentName componentName = getComponent(context);
            Bundle localBundle = new Bundle();
            localBundle.putString("package", context.getPackageName());
            localBundle.putString("class", componentName.getClassName());
            localBundle.putInt("badgenumber", count);
            context.getContentResolver()
                .call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
                    "change_badge", null, localBundle);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }

    /**
     * @param context context
     * @param count count
     */
    private static void setBadgeOfMIUI(Context context, int count, int iconResId) {
        try {
            NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            // NOTE TODO 删除 builder.setContentTitle("title").setContentText("text").setSmallIcon(iconResId);
            Notification notification = builder.build();
            try {
                Field field = notification.getClass().getDeclaredField("extraNotification");
                Object extraNotification = field.get(notification);
                Method method =
                    extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
                method.invoke(extraNotification, count);
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
            mNotificationManager.notify(0, notification);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }

    private static void setBadgeOfVivo(Context context, int count) {
        try {
            ComponentName componentName = getComponent(context);
            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra("packageName", context.getPackageName());
            intent.putExtra("className", componentName.getClassName());
            intent.putExtra("notificationNum", count);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    /**
     * @param context context
     * @param count count
     */
    private static void setBadgeOfHTC(Context context, int count) {
        try {
            Intent intentNotification = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
            ComponentName localComponentName =
                new ComponentName(context.getPackageName(), getLauncherClassName(context));
            intentNotification.putExtra("com.htc.launcher.extra.COMPONENT",
                localComponentName.flattenToShortString());
            intentNotification.putExtra("com.htc.launcher.extra.COUNT", count);
            context.sendBroadcast(intentNotification);

            Intent intentShortcut = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
            intentShortcut.putExtra("packagename", context.getPackageName());
            intentShortcut.putExtra("count", count);
            context.sendBroadcast(intentShortcut);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }

    /**
     * @param context context
     * @param count count
     */
    private static void setBadgeOfSony(Context context, int count) {
        try {
            String launcherClassName = getLauncherClassName(context);
            if (launcherClassName == null) {
                return;
            }
            boolean isShow = true;
            if (count == 0) {
                isShow = false;
            }
            Intent localIntent = new Intent();
            localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",
                launcherClassName);
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE",
                String.valueOf(count));
            localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME",
                context.getPackageName());
            context.sendBroadcast(localIntent);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    /**
     * @param context context
     * @param count count
     */
    private static void setBadgeOfNova(Context context, int count) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("tag",
                context.getPackageName() + "/" + getLauncherClassName(context));
            contentValues.put("count", count);
            context.getContentResolver()
                .insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"),
                    contentValues);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }

    private static ComponentName getComponent(Context context) {
        Intent launchIntent =
            context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        return launchIntent.getComponent();
    }

    /**
     */
    public static void setBadgeOfDefault(Context context, int count) {
        try {
            ComponentName componentName = getComponent(context);
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", count);
            intent.putExtra("badge_count_package_name", componentName.getPackageName());
            intent.putExtra("badge_count_class_name", componentName.getClassName());
            context.sendBroadcast(intent);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }
}
