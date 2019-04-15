package com.client.tok.utils;

import android.content.Context;
import com.client.tok.pagejump.GlobalParams;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PkUtils {
    private static String TAG = "PkUtils";
    public static String ADDRESS_PATTER = "[A-Za-z0-9]{76}";
    private static String CLIP_ADDRESS_PATTER = "\\${1}[A-Za-z0-9]{76}\\${1}";

    public static String getAddressFromContent(String content) {
        return pattern(content, ADDRESS_PATTER);
    }

    public static boolean isAddressValid(String address) {
        if (isPattern(address, ADDRESS_PATTER)) {
            int base = 0;
            for (char c : address.toCharArray()) {
                int i = Integer.parseInt(String.valueOf(c), 16);
                base = base ^ i;
            }
            return base == 0;
        }
        return false;
    }

    public static String getPkFromAddress(String address) {
        if (isAddressValid(address)) {
            return address.substring(0, GlobalParams.PK_LENGTH);
        }
        return "";
    }

    public static String getAddressFromClip(Context context) {
        CharSequence clipContent = SystemUtils.getLastClipContent(context);
        String result = null;
        if (!StringUtils.isEmpty(clipContent)) {
            String content = String.valueOf(clipContent);
            result = pattern(content, CLIP_ADDRESS_PATTER, true);
            if (!StringUtils.isEmpty(result)) {
                //去掉两边$
                result = pattern(content, ADDRESS_PATTER, true);
                SystemUtils.clearLastClip(context);
            }
        }
        return result;
    }

    private static String pattern(String content, String pattern) {
        return pattern(content, pattern, false);
    }

    private static String pattern(String content, String pattern, boolean isStrict) {
        if (!StringUtils.isEmpty(content)) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(content);
            boolean find = m.find();
            if (find) {
                return content.substring(m.start(), m.end());
            } else {
                return isStrict ? null : content;
            }
        }
        return isStrict ? null : content;
    }

    private static boolean isPattern(String content, String pattern) {
        if (!StringUtils.isEmpty(content)) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(content);
            return m.find();
        }
        return false;
    }

    public static String simplePk(String pk) {
        int simplePkLength = 8;
        return pk.substring(0, simplePkLength - 1);
    }
}
