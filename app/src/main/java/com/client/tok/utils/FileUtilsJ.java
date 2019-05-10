package com.client.tok.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.view.View;
import com.client.tok.TokApplication;
import com.client.tok.pagejump.GlobalParams;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

public class FileUtilsJ {

    public static boolean exist(String path) {
        return new File(path).exists();
    }

    /**
     * read String from raw
     */
    public static String readFromRaw(Context context, int rawId) {
        try {
            InputStream is = context.getResources().openRawResource(rawId);
            return readTextFromSDcard(is);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * read String from asset
     */
    public static String readFromAsset(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        AssetManager assetManager = context.getAssets();
        try {
            BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(assetManager.open(fileName), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * read String from asset
     */
    private String readFromAssets(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            return readTextFromSDcard(is);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * read String from inputStream
     *
     * @throws Exception
     */
    private static String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public static String getAppPath() {
        return TokApplication.getInstance().getFilesDir().getPath();
    }

    public static String saveBitmap(Bitmap bitmap) {
        String saveFile = StorageUtil.getFilesFolder() + UUID.randomUUID() + ".jpg";
        compress(bitmap, saveFile);
        return saveFile;
    }

    public static String saveFile(String resPath) {
        String saveFile = StorageUtil.getFilesFolder() + UUID.randomUUID() + ".jpg";
        compress(resPath, saveFile);
        return saveFile;
    }

    public static boolean compress(String inFilePath, String outFilePath) {
        boolean ret = false;
        Bitmap bitmap = convertToBitmap(inFilePath);
        return compress(bitmap, outFilePath);
    }

    public static boolean compress(Bitmap bitmap, String outFilePath) {
        boolean result = false;
        File saveFile = new File(outFilePath);
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outStream);
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result = false;
        } finally {
            close(outStream);
        }

        return result;
    }

    /**
     * get degree from exif
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            LogUtil.e(e.getMessage());
        }
        return degree;
    }

    public static Bitmap convertToBitmap(String path) {
        int pictureDegree = readPictureDegree(path);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeFile(path, opts);
        opts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        if (pictureDegree != 0) {
            bitmap = toTurn(bitmap, pictureDegree);
        }
        return bitmap;
    }

    public static Bitmap toTurn(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree); /*翻转度数*/
        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }

    public static File createTempImgFile() throws IOException {
        // Create an image file name
        final String tempName = generateTempName();
        File file = new File(StorageUtil.getFilesFolder());
        return File.createTempFile(tempName, ".jpg", file);
    }

    public static File createTempAudioFile() throws IOException {
        // Create an image file name
        final String tempName = generateTempName();
        File file = new File(StorageUtil.getFilesFolder());
        return File.createTempFile(tempName, ".ogg", file);
    }

    public static String generateTempName() {
        return UUID.randomUUID().toString();
    }

    public static String getFileSuffix(String path) {
        if (!StringUtils.isEmpty(path)) {
            String[] split = path.split("\\.");
            return split[split.length - 1].toLowerCase();
        } else {
            return "";
        }
    }

    public static void copy(byte[] sourceBytes, File dstFile) {
        try {
            FileOutputStream outputStream = new FileOutputStream(dstFile);
            outputStream.write(sourceBytes);
            close(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(File source, File destination) {
        try {
            FileInputStream inStream = new FileInputStream(source);
            FileOutputStream outStream = new FileOutputStream(destination);
            copy(inStream, outStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param srcPath src file
     * @param destFolder must include '/' at the end
     * @return copy to name;
     */
    public static String copy(String srcPath, String destFolder) {
        try {
            File srcFile = new File(srcPath);
            String srcName = srcFile.getName();
            File destFile = new File(destFolder + srcName);
            FileInputStream inStream = new FileInputStream(srcFile);
            FileOutputStream outStream = new FileOutputStream(destFile);
            copy(inStream, outStream);
            return destFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copy(InputStream inputStream, OutputStream outputStream) {
        try {
            byte[] buffer = new byte[inputStream.available()];
            while (inputStream.read(buffer) != -1) {
                outputStream.write(buffer);
            }
            close(inputStream);
            close(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] readToBytes(String source) {
        //byte[] data = null;
        //try {
        //    File file = new File(source);
        //NOTE on some device ,Files.readAllBytes make a crash:NoSuchMethodException
        //    data = Files.readAllBytes(file.toPath());
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        //return data;
        return readToBytes(new File(source));
    }

    public static byte[] readToBytes(File source) {
        RandomAccessFile f = null;
        byte[] data = new byte[] {};
        try {
            f = new RandomAccessFile(source, "r");
            if (f.length() <= Integer.MAX_VALUE) {
                data = new byte[(int) f.length()];
                f.readFully(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(f);
        }
        return data;
    }

    public static void writePrivateFile(Context context, String fileName, String write) {
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(write.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(outputStream);
        }
    }

    public static String rename(String filePath, String newName) {
        if (StringUtils.isEmpty(filePath) || StringUtils.isEmpty(newName)) {
            return null;
        }
        File file = new File(filePath);
        String parent = file.getParent();
        File newFile = new File(parent, newName);
        if (newFile.exists()) {
            return null;
        }
        return file.renameTo(newFile) ? newFile.getAbsolutePath() : null;
    }

    public static Uri getUriForFile(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(TokApplication.getInstance(),
                GlobalParams.PROVIDER_AUTH, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public static boolean writeFile(File file, byte[] bytes) {
        OutputStream ops = null;
        try {
            file.createNewFile();
            ops = new FileOutputStream(file);
            ops.write(bytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(ops);
        }
        return false;
    }

    public static boolean writeFile(String dest, byte[] bytes) {
        return writeFile(new File(dest), bytes);
    }

    public static void close(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
                stream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static boolean delFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile() && file.delete();
    }

    public static long fileSize(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.length();
        }
        return 0L;
    }

    public static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);
            while (md5.length() < 32) {
                md5 = "0" + md5;
            }

            return md5;
        } catch (Exception e) {
            return null;
        }
    }

    public static String saveViewToImg(View view, String path) {
        try {
            view.setDrawingCacheEnabled(true);
            view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            view.setDrawingCacheBackgroundColor(Color.WHITE);
            Bitmap bitmap = ViewUtil.loadBitmapFromView(view);
            FileOutputStream fos = null;
            if (StringUtils.isEmpty(path)) {
                File tmpFile = createTempImgFile();
                fos = new FileOutputStream(tmpFile);
                path = tmpFile.getAbsolutePath();
            } else {
                fos = new FileOutputStream(new File(path));
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            view.destroyDrawingCache();
        }
        return path;
    }

    /**
     * create dirs for path
     *
     * @param path full path
     * @param includeFileName is path include fileName
     */
    public static void createFolders(String path, boolean includeFileName) {
        try {
            if (includeFileName) {
                int lastIndex = path.lastIndexOf("/");
                path = path.substring(0, lastIndex);
            }
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileName(String path) {
        if (!StringUtils.isEmpty(path)) {
            String[] split = path.split("/");
            return split[split.length - 1];
        }
        return "";
    }

    public static boolean save2Download(Context context, String srcFile, String destFolder) {
        if (exist(srcFile)) {
            //some file from download folders,so no need save,return true
            if (!new File(destFolder).getPath().equals(new File(srcFile).getParent())) {
                //insert file to gallery
                String result = copy(srcFile, destFolder);
                try {
                    if (ImageUtils.isImgFile(result)) {
                        File file = new File(result);
                        // this will copy a new file to sdcard/pictures,no need
                        //MediaStore.Images.Media.insertImage(context.getContentResolver(), srcFile,
                        //    file.getName(), null);
                        //send broadcast,update gallery
                        Uri uri = Uri.fromFile(file);
                        context.sendBroadcast(
                            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }
}
