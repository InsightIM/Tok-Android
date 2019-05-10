package com.client.tok.pagejump;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.MimeTypeUtil;
import com.client.tok.utils.StringUtils;
import java.io.File;

public class PageJumpOut extends BasePageJump {

    public static void openFile(Context context, String path) {
        String fileType = MimeTypeUtil.getFileType(path);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && MimeTypeUtil.APK_TYPE.equals(
            fileType)) {
            if (!context.getPackageManager().canRequestPackageInstalls()) {
                Uri packageURI = Uri.parse("package:" + context.getPackageName());
                jump(context, new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI));
                return;
            }
        }
        if (!StringUtils.isEmpty(fileType)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri uri = FileUtilsJ.getUriForFile(new File(path));
            intent.setDataAndType(uri, fileType);
            context.startActivity(intent);
        }
    }

    public static void saveFile(Activity activity, int requestCode) {
        create(activity, "*/*", requestCode, new String[] { "*/*" });
    }

    public static void getFile(Activity activity, int requestCode) {
        select(activity, "*/*", requestCode, new String[] { "*/*" });
    }

    /**
     * open system file manager
     *
     * @param activity activity
     * @param type file type
     * @param requestCode code
     * @param extraMimeType mimiType must be arrays
     */
    private static void select(Activity activity, String type, int requestCode,
        String... extraMimeType) {
        Intent intent = new Intent();
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeType);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            try {
                activity.startActivityForResult(intent, requestCode);
            } catch (ActivityNotFoundException e2) {
                e2.printStackTrace();
            }
        }
    }

    private static void create(Activity activity, String type, int requestCode,
        String... extraMimeType) {
        Intent intent = new Intent();
        // intent.setType(type);
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeType);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void jumpWebBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        jump(context, intent);
    }

    public static void jumpOpenDial(Context context, String phone) {
        if (!phone.startsWith("tel:")) {
            phone = "tel:" + phone;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(phone));
        jump(context, intent);
    }

    public static void jumpCalling(Context context, String phone) {
        if (!phone.startsWith("tel:")) {
            phone = "tel:" + phone;
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phone));
        jump(context, intent);
    }

    public static void jumpEmail(Context context, String email) {
        try {
            if (!email.startsWith("mailto:")) {
                email = "mailto:" + email;
            }
            Uri uri = Uri.parse(email);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            jump(context, intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
