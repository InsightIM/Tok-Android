package com.client.tok.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.content.ContextCompat;
import com.client.tok.R;
import com.client.tok.TokApplication;
import com.client.tok.utils.StringUtils;

public class PermissionModel {
    private static String TAG = "PermissionModel";
    private static int REQ_CODE_PERMISSION = 10000;
    private static int requestCode = REQ_CODE_PERMISSION;
    public static final String[] PERMISSION_CAMERA = {
        Manifest.permission.CAMERA
    };

    public static final String[] PERMISSION_RECORD_STORAGE = {
        Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final String[] PERMISSION_STORAGE = {
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String[] PERMISSION_CAMERA_STORAGE = {
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    };

    public static final String[] PERMISSION_CAMERA_AUDIO = {
        Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
    };
    public static final String[] PERMISSION_LOCATION = {
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static final String[] PERMISSION_CONTACT = {
        Manifest.permission.READ_CONTACTS
    };
    public static final String[] PERMISSION_RECORD_AUDIO = {
        Manifest.permission.RECORD_AUDIO
    };

    /**
     * get prompt by permissions
     */
    public static CharSequence getRationalByPer(String[] permissions) {
        if (permissions == null || permissions.length == 0) {
            return null;
        } else {
            StringBuilder perNames = new StringBuilder();
            for (int i = 0; i < permissions.length; i++) {
                String perName = null;
                switch (permissions[i]) {
                    case Manifest.permission.CAMERA:
                        perName = StringUtils.getTextFromResId(R.string.permission_camera);
                        break;
                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                        perName = StringUtils.getTextFromResId(R.string.permission_storage);
                        break;
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                        perName = StringUtils.getTextFromResId(R.string.permission_location);
                        break;
                    case Manifest.permission.READ_CONTACTS:
                        perName = StringUtils.getTextFromResId(R.string.permission_contacts);
                        break;
                }
                if (!StringUtils.isEmpty(perName)) {
                    if (StringUtils.isEmpty(perNames.toString())) {
                        perNames.append(perName);
                    } else if (!perNames.toString().contains(perName)) {
                        perNames.append(",").append(perName);
                    }
                }
            }
            return StringUtils.formatHtmlTxFromResId(R.string.permission_deny_prompt, perNames,
                perNames);
        }
    }

    private static BasePermissionActivity getTopPermissionActivity() {
        if (BasePermissionActivity.permissionActivityList != null
            && BasePermissionActivity.permissionActivityList.size() > 0) {
            return BasePermissionActivity.permissionActivityList.peek();
        }
        return null;
    }

    /**
     * is have those permission?
     *
     * @param permissions permission list
     */
    public static boolean hasPermissions(@Size(min = 1) @NonNull String[] permissions) {
        return hasPermissions(TokApplication.getInstance(), permissions);
    }

    /**
     * is have those permission?
     *
     * @param permissions permission list:at least 1
     * @return true:every permission granted; false:have one or more permission not granted
     */
    public static boolean hasPermissions(@NonNull Context context,
        @Size(min = 1) @NonNull String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
            || permissions == null
            || permissions.length == 0) {
            return true;
        }
        if (context == null) {
            throw new IllegalArgumentException("Can't check permissions for null context");
        }
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(context, perm)
                != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * permission request,not show dialog after refused
     * permission + callBack
     * requestCode default
     */
    public static void requestPermissions(String[] permissions, PermissionCallBack callBack) {
        requestPermissions(requestCode, permissions, callBack);
    }

    /**
     * permission request,not show dialog after refused
     * can set requestCode
     */
    public static void requestPermissions(int requestCode, String[] permissions,
        PermissionCallBack callBack) {
        requestPermissions(requestCode, permissions, null, callBack);
    }

    /**
     * request permission, requestCode use the default code,
     * if user refused，it will show dialog to guide the user open setting granted permission
     *
     * @param permissions permission list
     * @param rationale the message show on the guide dialog
     * @param callBack callback
     */
    public static void requestPermissions(String[] permissions, CharSequence rationale,
        PermissionCallBack callBack) {
        requestPermissions(requestCode, permissions, rationale, callBack);
    }

    /**
     * request permission, requestCode can be set,
     * if user refused，it will show dialog to guide the user open setting granted permission
     *
     * @param requestCode requestCode
     * @param permissions permission list
     * @param rationale the message show on the guide dialog
     * @param callBack callback
     */
    public static void requestPermissions(int requestCode,
        @Size(min = 1) @NonNull String[] permissions, CharSequence rationale,
        PermissionCallBack callBack) {
        BasePermissionActivity activity = getTopPermissionActivity();
        if (hasPermissions(activity, permissions)) {
            notifyAlreadyHasPermissions(activity, requestCode, permissions);
            return;
        }
        activity.setPermissionsInfo(requestCode, permissions, rationale, callBack);
        PermissionHelper.requestPermissions(activity, requestCode, permissions);
    }

    public static void jumpAppSetting() {
        BasePermissionActivity activity = getTopPermissionActivity();
        PermissionSetting.startSettingForResult(activity, PermissionSetting.REQ_CODE_SETTING);
    }

    /**
     * check some permission are refused
     */
    public static boolean somePermissionsDenied(Activity activity, String... permissions) {
        return !PermissionHelper.shouldShowRationale(activity, permissions);
    }

    /**
     * check some permission are refused + not prompt again
     */
    public static boolean somePermissionNeverPrompt(Activity activity, String... permissions) {
        for (String perm : permissions) {
            if (!PermissionHelper.shouldShowRequestPermissionRationale(activity, perm)) {
                return true;
            }
        }
        return false;
    }

    private static void notifyAlreadyHasPermissions(BasePermissionActivity activity,
        int requestCode, @NonNull String[] permissions) {
        int[] grantResults = new int[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            grantResults[i] = PackageManager.PERMISSION_GRANTED;
        }
        activity.permissionResultDeal(requestCode, permissions, grantResults);
    }
}
