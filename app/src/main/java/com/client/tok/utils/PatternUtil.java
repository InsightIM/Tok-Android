package com.client.tok.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtil {
    public static Matcher getOrderMatcher(CharSequence sequence) {
        int flags = Pattern.CASE_INSENSITIVE;
        Pattern p = Pattern.compile("/[a-zA-Z0-9]{2,}", flags);
        return p.matcher(sequence);
    }

    public static Matcher getPkMatcher(CharSequence sequence) {
        int flags = Pattern.CASE_INSENSITIVE;
        Pattern p = Pattern.compile(PkUtils.ADDRESS_PATTER, flags);
        return p.matcher(sequence);
    }
}
