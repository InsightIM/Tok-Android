package com.client.tok.utils;

public class AvatarUtil {
    public static boolean avatarExist(String pk) {
        return FileUtilsJ.exist(StorageUtil.getAvatarsFolder() + pk + ".png");
    }
}
