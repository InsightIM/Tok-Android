package com.client.tok.db.converter;

import android.arch.persistence.room.TypeConverter;
import com.client.tok.constant.MessageType;

public class MessageTypeConverter {
    @TypeConverter
    public static MessageType make(int msgType) {
        return MessageType.fromValue(msgType);
    }

    @TypeConverter
    public static int parse(MessageType msgType) {
        if (msgType == null) {
            return MessageType.MESSAGE.getType();
        }
        return msgType.getType();
    }
}
