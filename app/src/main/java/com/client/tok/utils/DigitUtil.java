package com.client.tok.utils;

public class DigitUtil {
    public static int str2Int(String str) {
        try {
            return Integer.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
