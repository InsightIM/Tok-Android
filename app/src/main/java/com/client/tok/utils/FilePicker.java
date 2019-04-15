package com.client.tok.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import com.client.tok.R;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.permission.PermissionCallBack;
import com.client.tok.permission.PermissionModel;
import com.leon.lfilepickerlibrary.LFilePicker;
import java.io.File;
import java.util.List;

public class FilePicker {
    private static final String TAG = "FilePicker";
    public static final int REQ_IMG_CAMERA = 2000;
    public static final int REQ_IMG_GALLERY = 2001;
    public static final int REQ_FILE_SEL = 2002;
    public static final int REQ_FOLDER_SEL = 2003;
    private static Uri imgCameraUri;

    public static void openCamera(final Activity activity) {
        if (PermissionModel.hasPermissions(PermissionModel.PERMISSION_CAMERA_STORAGE)) {
            jumpCamera(activity);
        } else {
            PermissionModel.requestPermissions(PermissionModel.PERMISSION_CAMERA_STORAGE,
                new PermissionCallBack() {
                    @Override
                    public void onPermissionsAllGranted(int requestCode,
                        @NonNull List<String> grantedPers) {
                        jumpCamera(activity);
                    }
                });
        }
    }

    public static void openGallery(final Activity activity) {
        if (PermissionModel.hasPermissions(PermissionModel.PERMISSION_STORAGE)) {
            jumpGallery(activity);
        } else {
            PermissionModel.requestPermissions(PermissionModel.PERMISSION_STORAGE,
                new PermissionCallBack() {
                    @Override
                    public void onPermissionsAllGranted(int requestCode,
                        @NonNull List<String> grantedPers) {
                        jumpGallery(activity);
                    }
                });
        }
    }

    public static void openFileSel(final Activity activity) {
        if (PermissionModel.hasPermissions(PermissionModel.PERMISSION_STORAGE)) {
            jumpFileOrFolderSel(activity, true);
        } else {
            PermissionModel.requestPermissions(PermissionModel.PERMISSION_STORAGE,
                new PermissionCallBack() {
                    @Override
                    public void onPermissionsAllGranted(int requestCode,
                        @NonNull List<String> grantedPers) {
                        jumpFileOrFolderSel(activity, true);
                    }
                });
        }
    }

    public static void openFolderSel(final Activity activity) {
        if (PermissionModel.hasPermissions(PermissionModel.PERMISSION_STORAGE)) {
            jumpFileOrFolderSel(activity, false);
        } else {
            PermissionModel.requestPermissions(PermissionModel.PERMISSION_STORAGE,
                new PermissionCallBack() {
                    @Override
                    public void onPermissionsAllGranted(int requestCode,
                        @NonNull List<String> grantedPers) {
                        jumpFileOrFolderSel(activity, false);
                    }
                });
        }
    }

    public static void openFolder(final Activity activity, String folder) {
        if (PermissionModel.hasPermissions(PermissionModel.PERMISSION_STORAGE)) {
            jumpFolderSee(activity, folder);
        } else {
            PermissionModel.requestPermissions(PermissionModel.PERMISSION_STORAGE,
                new PermissionCallBack() {
                    @Override
                    public void onPermissionsAllGranted(int requestCode,
                        @NonNull List<String> grantedPers) {
                        openFolder(activity, folder);
                    }
                });
        }
    }

    private static void jumpCamera(Activity activity) {
        try {
            File saveFile = FileUtilsJ.createTempImgFile();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imgCameraUri =
                    FileProvider.getUriForFile(activity, GlobalParams.PROVIDER_AUTH, saveFile);
                takePictureIntent.setFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                imgCameraUri = Uri.fromFile(saveFile);
            }
            LogUtil.i(TAG, "imgUri:" + imgCameraUri.toString());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgCameraUri);
            activity.startActivityForResult(takePictureIntent, REQ_IMG_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void jumpGallery(Activity activity) {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, null), REQ_IMG_GALLERY);
    }

    private static void jumpFileOrFolderSel(Activity activity, boolean isFileModel) {
        new LFilePicker().withActivity(activity)
            .withChooseMode(isFileModel)
            .withRequestCode(REQ_FILE_SEL)
            .withBackIcon(R.drawable.arrow_back_white)
            .withTitle(isFileModel ? StringUtils.getTextFromResId(R.string.select_file)
                : StringUtils.getTextFromResId(R.string.select_folder))
            .withMutilyMode(false)
            .start();
    }

    private static void jumpFolderSee(Activity activity, String startFolder) {
        new LFilePicker().withActivity(activity)
            .withChooseMode(false)
            .withStartPath(startFolder)
            .withBackIcon(R.drawable.arrow_back_white)
            .withTitle(StringUtils.getTextFromResId(R.string.file_check))
            .withMutilyMode(false)
            .start();
    }

    public static Uri getImgCameraUri() {
        return imgCameraUri;
    }
}
