package com.client.tok.utils;

public class MimeTypeUtil {
    public static String IMG_TYPE = "image/*";
    public static String APK_TYPE = "application/vnd.android.package-archive";
    public static String PDF_TYPE = "application/pdf";
    public static String TXT_TYPE = "text/plain";
    public static String VIDEO_TYPE = "video/*";
    public static String AUDIO_TYPE = "audio/*";
    public static String CHM_TYPE = "application/x-chm";
    public static String PPT_TYPE = "application/vnd.ms-powerpoint";
    public static String WORD_TYPE = "application/msword";
    public static String EXCEL_TYPE = "application/vnd.ms-excel";
    public static String OTHER_TYPE = "*/*";

    private static String IMG = "jpg,jpeg,gif,png";
    private static String APK = "apk";
    private static String PDF = "pdf";
    private static String TXT = "txt";
    private static String VIDEO = "mp4,avi";
    private static String AUDIO = "mp3,acm,aif,au";
    private static String CHM = "chm";
    private static String PPT = "ppt,pptx";
    private static String WORD = "doc,docx";
    private static String EXCEL = "xls,xlsx";

    public static String getFileType(String path) {
        if (!StringUtils.isEmpty(path)) {
            String suffix = FileUtilsJ.getFileSuffix(path);
            if (IMG.contains(suffix)) {
                return IMG_TYPE;
            } else if (APK.contains(suffix)) {
                return APK_TYPE;
            } else if (PDF.contains(suffix)) {
                return PDF_TYPE;
            } else if (TXT.contains(suffix)) {
                return TXT_TYPE;
            } else if (VIDEO.contains(suffix)) {
                return VIDEO_TYPE;
            } else if (AUDIO.contains(suffix)) {
                return AUDIO_TYPE;
            } else if (CHM.contains(suffix)) {
                return CHM_TYPE;
            } else if (PPT.contains(suffix)) {
                return PPT_TYPE;
            } else if (WORD.contains(suffix)) {
                return WORD_TYPE;
            } else if (EXCEL.contains(suffix)) {
                return EXCEL_TYPE;
            } else {
                return OTHER_TYPE;
            }
        } else {
            return null;
        }
    }
}
