package com.client.tok.utils;

import java.io.File;

public class VerifyUtils {

    public static boolean isUserNameValid(String userName) {
        if (!StringUtils.isEmpty(userName)) {
            userName = userName.trim();
            return !userName.contains(File.separator);
        }
        return false;
    }
}
