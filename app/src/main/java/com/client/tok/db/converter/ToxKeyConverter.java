package com.client.tok.db.converter;

import android.arch.persistence.room.TypeConverter;
import com.client.tok.bean.ContactsKey;

public class ToxKeyConverter {
    @TypeConverter
    public static ContactsKey make(String key) {
        if (key == null) {
            return null;
        }
        return new ContactsKey(key);
    }

    @TypeConverter
    public static String parse(ContactsKey toxKey) {
        if (toxKey == null) {
            return "";
        }
        return toxKey.getKey();
    }
}
