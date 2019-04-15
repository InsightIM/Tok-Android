package com.client.tok.db.converter;

import android.arch.persistence.room.TypeConverter;
import im.tox.tox4j.core.data.ToxStatusMessage;

public class ToxStatusMessageConverter {
    @TypeConverter
    public static ToxStatusMessage make(String statusMsg) {
        if (statusMsg == null) {
            return null;
        }
        return ToxStatusMessage.unsafeFromValue(statusMsg.getBytes());
    }

    @TypeConverter
    public static String parse(ToxStatusMessage statusMsg) {
        if (statusMsg == null) {
            return "";
        }
        return new String(statusMsg.value);
    }
}
