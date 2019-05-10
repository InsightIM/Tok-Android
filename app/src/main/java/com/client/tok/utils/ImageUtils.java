package com.client.tok.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import com.client.tok.TokApplication;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class ImageUtils {
    private static String TAG = "ImageUtils";
    private static List<String> IMG_TAG_LIST = Arrays.asList("jpg", "jpeg", "gif", "png", "webp");
    private static int IMG_LIMIT_SIZE = 500;

    public static boolean isImgFile(String path) {
        LogUtil.i(TAG, "isImgFile:" + path);
        if (!StringUtils.isEmpty(path)) {
            return IMG_TAG_LIST.contains(FileUtilsJ.getFileSuffix(path));
        } else {
            return false;
        }
    }

    public static boolean isHasExif(String suffix) {
        return IMG_TAG_LIST.contains(suffix.toLowerCase());
    }

    public static String getPath(Context context, Uri uri) {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(),
            uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                    Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[] { split[1] };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (uri.toString().contains(StorageUtil.ROOT_PROVIDER_NAME)) {
                //如果是自己目录下的，直接拼接路径
                return StorageUtil.getFilesFolder() + uri.getLastPathSegment();
            }
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                    .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String photoPath = "";
        if (context == null || uri == null) {
            return photoPath;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                if (isExternalStorageDocument(uri)) {
                    String[] split = docId.split(":");
                    if (split.length >= 2) {
                        String type = split[0];
                        if ("primary".equalsIgnoreCase(type)) {
                            photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                        }
                    }
                } else if (isDownloadsDocument(uri)) {
                    Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    // getDataColumn is not useful
                    //photoPath = getDataColumn(context, contentUri, null, null);
                    photoPath = getFilePathFromURI(context, contentUri);
                } else if (isMediaDocument(uri)) {
                    String[] split = docId.split(":");
                    if (split.length >= 2) {
                        String type = split[0];
                        Uri contentUris = null;
                        if ("image".equals(type)) {
                            contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        String selection = MediaStore.Images.Media._ID + "=?";
                        String[] selectionArgs = new String[] { split[1] };
                        photoPath = getDataColumn(context, contentUris, selection, selectionArgs);
                    }
                }
            } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
                photoPath = uri.getPath();
            } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
                // Return the remote address
                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }
                return getFilePathFromURI(context, uri);
            } else {
                photoPath = getDataColumn(context, uri, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoPath;
    }

    private static String getFilePathFromURI(Context context, Uri uri) {
        String data = null;

        if (uri.toString().contains(StorageUtil.ROOT_PROVIDER_NAME)) {
            //如果是自己目录下的，直接拼接路径
            return StorageUtil.getFilesFolder() + uri.getLastPathSegment();
        }
        //this is not useful when the uri is this app
        Cursor cursor = context.getContentResolver()
            .query(uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null);
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                if (index > -1) {
                    data = cursor.getString(index);
                }
            }
            cursor.close();
        }
        LogUtil.i(TAG, "getFilePathFromURI:" + data);
        return data;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
        String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor =
                context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return null;
    }

    public static boolean isCompressImg(String path) {
        if (!StringUtils.isEmpty(path)) {
            return !path.toLowerCase().contains(".gif");
        } else {
            return false;
        }
    }

    private Bitmap.CompressFormat getCompressFormat(String path) {
        if (!StringUtils.isEmpty(path)) {
            String suffix = FileUtilsJ.getFileSuffix(path);
            switch (suffix) {
                case "jpg":
                case "jpeg":
                    return Bitmap.CompressFormat.JPEG;
                case "png":
                    return Bitmap.CompressFormat.PNG;
                case "webp":
                    return Bitmap.CompressFormat.WEBP;
                default:
                    return Bitmap.CompressFormat.JPEG;
            }
        } else {
            return Bitmap.CompressFormat.JPEG;
        }
    }

    private static Bitmap compressBitmap(Bitmap bitmap, long sizeLimit) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 90;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        LogUtil.i(TAG, "origin size:" + baos.size());
        //TODO has problem
        while (baos.size() / 1024 > sizeLimit) {
            LogUtil.i(TAG, "after size:" + baos.size());
            // 清空baos
            baos.reset();
            quality -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }

        return BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null);
    }

    public static void compressBitmap(Bitmap bitmap, String outPath) {
        try {
            OutputStream out = new FileOutputStream(outPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String compressFile(String inPath, String outPath) {
        try {
            if (isCompressImg(inPath)) {
                File newFile = new File(outPath);
                FileOutputStream fout = new FileOutputStream(newFile);
                Bitmap bitmap = compressBitmap(BitmapFactory.decodeFile(inPath), IMG_LIMIT_SIZE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                fout.flush();
                fout.close();
                return newFile.getAbsolutePath();
            }
        } catch (Exception e) {
            return null;
        }
        return outPath;
    }

    public static String compressFile(String inPath) {
        File newFile = null;
        try {
            newFile = FileUtilsJ.createTempImgFile();
            return compressFile(inPath, newFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inPath;
    }

    public static void compressImg(String sourcePath, OnCompressListener compressListener) {
        compressImg(sourcePath, StorageUtil.getFilesFolder(), compressListener);
    }

    public static void compressImg(String sourcePath, String targetPath,
        OnCompressListener compressListener) {
        Luban.with(TokApplication.getInstance())
            .load(sourcePath)
            .ignoreBy(IMG_LIMIT_SIZE)
            .setTargetDir(targetPath)
            .filter(new CompressionPredicate() {
                @Override
                public boolean apply(String path) {
                    return isCompressImg(path);
                }
            })
            .setCompressListener(compressListener)
            .launch();
    }

    public static String getImgPath(String path) {
        LogUtil.i(TAG, "getImgPath:" + path);
        if (path.contains(File.separator)) {
            return path;
        } else {
            return StorageUtil.getFilesFolder() + path;
        }
    }
}
