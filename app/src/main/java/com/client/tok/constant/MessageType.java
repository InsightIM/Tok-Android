package com.client.tok.constant;

import im.tox.tox4j.core.enums.ToxMessageType;
import java.util.HashSet;
import java.util.Set;

public enum MessageType {
    NONE(0),
    MESSAGE(1),
    FILE_TRANSFER(2),
    ACTION(3),
    GROUP_MESSAGE(4),
    GROUP_ACTION(5),
    CALL_EVENT(6),
    PROMPT_NORMAL(7),//normal prompt,just has text
    DRAFT(8),
    PROMPT_ADD_OFFLINE_BOT(9),//prompt about add offline bot,can click
    OFF_LINE(10);

    private int type;

    MessageType(int type) {
        this.type = type;
    }

    public static MessageType fromValue(int type) {
        for (MessageType msgType : MessageType.values()) {
            if (msgType.getType() == type) {
                return msgType;
            }
        }
        return NONE;
    }

    public static MessageType fromToxMessageType(ToxMessageType messageType) {
        switch (messageType) {
            case ACTION:
                return ACTION;
            case NORMAL:
                return MESSAGE;
            case OFF_LINE:
                return MESSAGE;
            default:
                return NONE;
        }
    }

    public static ToxMessageType toToxMessageType(MessageType messageType) throws Exception {
        switch (messageType) {
            case ACTION:
            case GROUP_ACTION:
                return ToxMessageType.ACTION;
            case MESSAGE:
            case GROUP_MESSAGE:
                return ToxMessageType.NORMAL;
            case OFF_LINE:
                return ToxMessageType.OFF_LINE;
            default:
                throw new Exception("Invalid message type for conversion to ToxMessageType.");
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static Set transferValues() {
        Set values = new HashSet();
        values.add(FILE_TRANSFER);
        return values;
    }

    public static boolean isFileTransfer(MessageType type) {
        return MessageType.transferValues().contains(type);
    }

    @Override
    public String toString() {
        return type + "";
    }
}
