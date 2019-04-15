package com.client.tok.db.converter;

import android.arch.persistence.room.TypeConverter;
import com.client.tok.constant.FileKind;

public class FileKindConverter {
    @TypeConverter
    public static FileKind make(int kindId) {
        return FileKind.fromKindId(kindId);
    }

    @TypeConverter
    public static int parse(FileKind fileKind) {
        if (fileKind == null) {
            return FileKind.INVALID.getKindId();
        }
        return fileKind.getKindId();
    }
}
