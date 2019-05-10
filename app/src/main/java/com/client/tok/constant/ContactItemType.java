package com.client.tok.constant;

public enum ContactItemType {
    FRIEND_REQUEST(1),
    FRIEND(2),
    GROUP_INVITE(3),
    GROUP(4);

    private int type;

    ContactItemType(int type) {
        this.type = type;
    }

}
