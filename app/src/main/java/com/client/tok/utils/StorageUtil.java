package com.client.tok.utils;

import android.os.Environment;
import java.io.File;

public class StorageUtil {
    private static String TAG = "StorageUtil";
    private static String ROOT_NAME = "Tok";
    public static String ROOT_PROVIDER_NAME = "froot";

    private static String FOLDER_SDCARD = Environment.getExternalStorageDirectory().toString();
    private static String FOLDER_ROOT =
        FOLDER_SDCARD + File.separator + ROOT_NAME + File.separator + PreferenceUtils.getAccount();
    private static String[] SUB_FOLDERS =
        { ".files", ".avatars", ".qr", ".tmp", "profile", "download" };
    private static String QR_CODE_FILE = "qr_code.jpg";
    private static String QR_CODE_SHARE_FILE = "qr_code_share.jpg";
    private static String SHARE_IMG_FILE = "share_img.jpg";

    public static void initFolders() {
        try {
            FOLDER_ROOT = FOLDER_SDCARD
                + File.separator
                + ROOT_NAME
                + File.separator
                + PreferenceUtils.getAccount();
            for (String subFolder : SUB_FOLDERS) {
                File file = new File(FOLDER_ROOT + File.separator + subFolder);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAppRootFolder() {
        return FOLDER_ROOT + File.separator;
    }

    public static String getFilesFolder() {
        return FOLDER_ROOT + File.separator + SUB_FOLDERS[0] + File.separator;
    }

    public static String getAvatarsFolder() {
        return FOLDER_ROOT + File.separator + SUB_FOLDERS[1] + File.separator;
    }

    public static String getTmpFolder() {
        return FOLDER_ROOT + File.separator + SUB_FOLDERS[3] + File.separator;
    }

    public static String getQrCodeFolder() {
        return FOLDER_ROOT + File.separator + SUB_FOLDERS[2] + File.separator;
    }

    public static String getProfileFolder() {
        return FOLDER_ROOT + File.separator + SUB_FOLDERS[4] + File.separator;
    }

    public static String getDownloadFolder() {
        return FOLDER_ROOT + File.separator + SUB_FOLDERS[5] + File.separator;
    }

    public static String getQrCodeFile() {
        return getQrCodeFolder() + File.separator + QR_CODE_FILE;
    }

    public static String getShareQrCodeFile() {
        return getQrCodeFolder() + File.separator + QR_CODE_SHARE_FILE;
    }

    public static String getShareImgFile() {
        return getTmpFolder() + File.separator + SHARE_IMG_FILE;
    }
}
