package com.client.tok.permission;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

class PermissionHelper {
    public static boolean shouldShowRequestPermissionRationale(Activity activity,
        String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public static void directRequestPermissions(Activity activity, int requestCode,
        @NonNull String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public static boolean shouldShowRationale(Activity activity, @NonNull String... permissions) {
        if (permissions != null) {
            for (String perm : permissions) {
                if (shouldShowRequestPermissionRationale(activity, perm)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void requestPermissions(Activity activity, int requestCode,
        @NonNull String... permissions) {
        //if (shouldShowRationale(activity, permissions)) {
        //    //showRequestPermissionRationale(rationale, requestCode, permissions);
        //} else {
        directRequestPermissions(activity, requestCode, permissions);
        //}
    }
}
