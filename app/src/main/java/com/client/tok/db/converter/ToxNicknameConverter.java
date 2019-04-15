package com.client.tok.db.converter;

import android.arch.persistence.room.TypeConverter;
import im.tox.tox4j.core.data.ToxNickname;

public class ToxNicknameConverter {
    @TypeConverter
    public static ToxNickname make(String nickname) {
        if (nickname == null) {
            return null;
        }
        return ToxNickname.unsafeFromValue(nickname.getBytes());
    }

    @TypeConverter
    public static String parse(ToxNickname tokNickname) {
        if (tokNickname == null) {
            return "";
        }
        return tokNickname.toString();
    }
}
