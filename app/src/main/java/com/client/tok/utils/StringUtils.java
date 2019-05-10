package com.client.tok.utils;

import android.support.annotation.StringRes;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;
import com.client.tok.BuildConfig;
import com.client.tok.TokApplication;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;

public class StringUtils {

    public static String getTextFromResId(int resId) {
        if (resId <= 0) {
            return "";
        } else {
            return TokApplication.getInstance().getString(resId);
        }
    }

    public static String formatTxFromResId(@StringRes int resId, Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        String str = TokApplication.getInstance().getString(resId);
        return String.format(str, args);
    }

    public static Spanned formatHtmlTxFromResId(@StringRes int resId, Object... args) {
        return Html.fromHtml(formatTxFromResId(resId, args));
    }

    public static Spanned formatHtmlTxFromResId(@StringRes int resId) {
        return Html.fromHtml(getTextFromResId(resId));
    }

    public static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }

    public static boolean isEqualsWithConstant(String constant, String variable) {
        return constant.equals(variable);
    }

    public static boolean isEquals(String srstr, String destr) {
        if (srstr != null) {
            return isEqualsWithConstant(srstr, destr);
        }
        if (destr != null) {
            return isEqualsWithConstant(destr, srstr);
        }
        return false;
    }

    public static boolean isEmpty(CharSequence content) {
        return TextUtils.isEmpty(content);
    }

    public static String getStringValue(TextView view) {
        if (view != null) {
            return view.getText().toString().trim();
        }
        return "";
    }

    public static String str2Ascii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    public static String byte2Str(ByteString byteString) {
        if (byteString != null) {
            return new String(byteString.toByteArray());
        }
        return "";
    }

    public static String byte2Str(byte[] bytes) {
        if (bytes != null) {
            return new String(bytes);
        }
        return "";
    }

    public static String removeNewLines(String str) {
        return str.replace("\n", "").replace("\r", "");
    }

    public static String md5(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        String md5code = str;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes("UTF-8"));
            byte[] secretBytes = md.digest();
            md5code = new BigInteger(1, secretBytes).toString(16);
            for (int i = 0; i < 32 - md5code.length(); i++) {
                md5code = "0" + md5code;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return md5code;
    }

    public static byte[] getBytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.getBytes();
    }

    public static int getDrawableIdByName(String resName) {
        try {
            return TokApplication.getInstance()
                .getResources()
                .getIdentifier(resName, "drawable", BuildConfig.APPLICATION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
