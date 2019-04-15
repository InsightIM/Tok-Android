package com.client.tok.msg;

import com.client.tok.R;
import im.tox.tox4j.core.enums.ToxUserStatus;

public class UserStatus {

    public static int statusDrawable(boolean isOnline, ToxUserStatus status) {
        if (!isOnline) {
            return R.drawable.circle_offline;
        } else if (status == ToxUserStatus.NONE) {
            return R.drawable.circle_online;
        } else if (status == ToxUserStatus.AWAY) {
            return R.drawable.circle_away;
        } else if (status == ToxUserStatus.BUSY) {
            return R.drawable.circle_busy;
        } else {
            return R.drawable.circle_offline;
        }
    }

    //TODO has problem
    public static ToxUserStatus getUserStatusFromString(String status) {
        switch (status) {
            case "online":
                return ToxUserStatus.NONE;
            case "away":
                return ToxUserStatus.AWAY;
            case "busy":
                return ToxUserStatus.BUSY;
            default:
                return ToxUserStatus.NONE;
        }
    }

    public static String getStringFromUserStatus(ToxUserStatus status) {
        if (status == ToxUserStatus.NONE) {
            return "online";
        } else if (status == ToxUserStatus.AWAY) {
            return "away";
        } else if (status == ToxUserStatus.BUSY) {
            return "busy";
        } else {
            return "invalid";
        }
    }
}
