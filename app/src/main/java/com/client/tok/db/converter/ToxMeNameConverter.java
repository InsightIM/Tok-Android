package com.client.tok.db.converter;

import android.arch.persistence.room.TypeConverter;
import com.client.tok.bean.ToxMeName;

public class ToxMeNameConverter {
    @TypeConverter
    public static ToxMeName make(String name) {
        if (name == null) {
            return null;
        }
        return new ToxMeName(name, "");
    }

    @TypeConverter
    public static String parse(ToxMeName toxMeName) {
        if (toxMeName == null) {
            return "";
        }
        return toxMeName.getUserName();
    }
}
