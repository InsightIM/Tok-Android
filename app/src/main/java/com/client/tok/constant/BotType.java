package com.client.tok.constant;

public enum BotType {
    FIND_FRIEND_BOT(10001),
    OFFLINE_MSG_BOT(10002);
    private int type;

    public int getType() {
        return type;
    }

    public static BotType fromVal(int type) {
        for (BotType botType : BotType.values()) {
            if (botType.getType() == type) {
                return botType;
            }
        }
        return OFFLINE_MSG_BOT;
    }

    BotType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type + "";
    }
}
