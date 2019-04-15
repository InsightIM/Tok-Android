package com.client.tok.ui.login;

import com.client.tok.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountUtil {
    private static List<String> SUPPORT_FILE =
        new ArrayList<>(Arrays.asList(".tok", ".tox", ".fchat"));

    public static boolean isSupportAccountFile(String filePath) {
        if (!StringUtils.isEmpty(filePath)) {
            for (String suffix : SUPPORT_FILE) {
                if (filePath.contains(suffix)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String replaceSuffix(String fileName) {
        if (!StringUtils.isEmpty(fileName)) {
            for (String suffix : SUPPORT_FILE) {
                fileName = fileName.replace(suffix, "");
            }
        }
        return fileName;
    }
}
