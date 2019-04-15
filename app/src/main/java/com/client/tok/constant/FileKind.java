package com.client.tok.constant;

import com.client.tok.utils.StorageUtil;
import com.client.tok.utils.StringUtils;
import java.io.File;

public enum FileKind {
    INVALID(-1, true, "", true, false),
    DATA(0, true, StorageUtil.getFilesFolder(), true, true),
    AVATAR(1, false, StorageUtil.getAvatarsFolder(), true, true);

    int kindId;
    boolean visible;
    String storageDir;
    boolean autoAccept;
    boolean replaceExisted;

    FileKind(int kindId, boolean visible, String storageDir, boolean autoAccept,
        boolean replaceExisted) {
        this.kindId = kindId;
        this.visible = visible;
        this.storageDir = storageDir;
        this.autoAccept = autoAccept;
        this.replaceExisted = replaceExisted;
    }

    public int getKindId() {
        return kindId;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getStorageDir() {
        return storageDir;
    }

    public boolean isAutoAccept() {
        return autoAccept;
    }

    public boolean isReplaceExisted() {
        return replaceExisted;
    }

    public File getFile(String fileName) {
        if (!StringUtils.isEmpty(fileName)) {
            File file = new File(storageDir, fileName);
            if (file.exists() && !file.isDirectory()) {
                return file;
            }
        }
        return null;
    }

    public static FileKind fromKindId(int kindId) {
        for (FileKind kind : FileKind.values()) {
            if (kind.kindId == kindId) {
                return kind;
            }
        }
        return INVALID;
    }

    @Override
    public String toString() {
        return kindId + "";
    }
}
