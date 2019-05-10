package com.client.tok.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.client.tok.TokApplication;

public class PreferenceUtils {
    public static final String SHARE_P_DEFAULT = "Tok";
    private static final String SHARE_P_CURRENT_USER = "Tok_";
    public static final String ACTIVE_ACCOUNT = "active_account";
    public static final String HAS_SHOW_GUIDE = "has_show_guide";
    public static final String HAS_SHOW_FIND_FRIEND_BOT_FEATURE = "has_show_find_friend_bot_fea";
    public static final String HAS_SHOW_OFFLINE_BOT_FEATURE = "show_offline_bot";
    public static final String CLEAR_MSG_LOGOUT = "clear_msg_logout";
    public static final String AUTO_RECEIVE_FILE = "auto_receive_file";

    public static final String GLOBAL_MSG_NOTIFY = "global_msg_notify";
    public static final String NEW_FRIEND_REQ_NOTIFY = "new_friend_req_notify";
    public static final String NOTIFY_CENTER = "notify_center";

    private static SharedPreferences getUserShareP() {
        return TokApplication.getInstance()
            .getSharedPreferences(SHARE_P_CURRENT_USER + getAccount(), Context.MODE_PRIVATE);
    }

    public static String getUserSharePFile() {
        return SHARE_P_CURRENT_USER + getAccount();
    }

    private static SharedPreferences.Editor getDefaultEditor() {
        return getUserShareP().edit();
    }

    public static void remove(String key) {
        SharedPreferences.Editor editor = getUserShareP().edit();
        editor.remove(key);
        editor.apply();
    }

    public static void saveString(String key, String value) {
        getDefaultEditor().putString(key, value).commit();
    }

    public static String getString(String key, String defValue) {
        return getUserShareP().getString(key, defValue);
    }

    public static void saveBoolean(String key, Boolean value) {
        getDefaultEditor().putBoolean(key, value).commit();
    }

    public static Boolean getBoolean(String key, Boolean defValue) {
        return getUserShareP().getBoolean(key, defValue);
    }

    public static void saveLong(String key, long value) {
        getDefaultEditor().putLong(key, value).commit();
    }

    public static long getLong(String key, long defValue) {
        return getUserShareP().getLong(key, defValue);
    }

    public static void saveInt(String key, int value) {
        getDefaultEditor().putInt(key, value).commit();
    }

    public static long getInt(String key, int defValue) {
        return getUserShareP().getInt(key, defValue);
    }

    public static boolean hasShowGuide() {
        boolean hasShow = getUserShareP().getBoolean(HAS_SHOW_GUIDE, false);
        saveBoolean(HAS_SHOW_GUIDE, true);
        return hasShow;
    }

    public static boolean hasShowFindFriendBotFeat() {
        return getUserShareP().getBoolean(HAS_SHOW_FIND_FRIEND_BOT_FEATURE, false);
    }

    public static void setHasShowFindFriendBotFeat() {
        saveBoolean(HAS_SHOW_FIND_FRIEND_BOT_FEATURE, true);
    }

    public static boolean hasShowOfflineBotFeat() {
        return getUserShareP().getBoolean(HAS_SHOW_OFFLINE_BOT_FEATURE, false);
    }

    public static void setHasShowOfflineBotFeat() {
        saveBoolean(HAS_SHOW_OFFLINE_BOT_FEATURE, true);
    }

    private static SharedPreferences getDefaultShareP() {
        return TokApplication.getInstance()
            .getSharedPreferences(SHARE_P_DEFAULT, Context.MODE_PRIVATE);
    }

    public static void saveAccount(String activeAccount) {
        SharedPreferences.Editor editor = getDefaultShareP().edit();
        editor.putString(ACTIVE_ACCOUNT, activeAccount);
        editor.apply();
    }

    public static String getAccount() {
        SharedPreferences preferences = getDefaultShareP();
        return preferences.getString(ACTIVE_ACCOUNT, "");
    }

    public static void clearUserShare() {
        SharedPreferences.Editor editor = getUserShareP().edit();
        editor.clear();
        editor.apply();
    }

    public static void clearDefaultShare() {
        SharedPreferences preferences = getDefaultShareP();
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
