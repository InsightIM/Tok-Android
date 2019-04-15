package com.client.tok.constant;

public enum ContactType {
    NONE(0),
    FRIEND(1),
    GROUP(2),
    PEER(3);
    private int type;

    public int getType() {
        return type;
    }

    public static ContactType valueOf(int type) {
        for (ContactType contactType : ContactType.values()) {
            if (contactType.getType() == type) {
                return contactType;
            }
        }
        return NONE;
    }

    ContactType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type + "";
    }
}
