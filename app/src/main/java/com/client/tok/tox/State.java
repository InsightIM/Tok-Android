package com.client.tok.tox;

import com.client.tok.db.repository.InfoRepository;
import com.client.tok.db.repository.UserRepository;
import com.client.tok.notification.NotifyManager;
import com.client.tok.transfer.TransferManager;

public class State {
    /**
     * chat page is active、pause
     */
    private static boolean chatPageActive = false;
    /**
     * current active chat friend key
     */
    private static String activeKey;

    public static TransferManager transfers = new TransferManager();

    private static InfoRepository infoRepository;

    private static UserRepository userRepository = new UserRepository();

    public static void setDb(InfoRepository db) {
        infoRepository = db;
    }

    public static InfoRepository infoRepo() {
        return infoRepository;
    }

    public static UserRepository userRepo() {
        return userRepository;
    }

    public static TransferManager transferManager() {
        return transfers;
    }

    public static boolean isChatActive(String key) {
        return chatPageActive && key != null && key.equals(activeKey);
    }

    public static void setChatPageActive(boolean active) {
        chatPageActive = active;
    }

    public static void setChatKey(String key) {
        activeKey = key;
    }

    public static boolean isLoggedIn() {
        return userRepository.loggedIn();
    }

    public static void login(String name) {
        userRepository.login(name);
    }

    public static void logout() {
        NotifyManager.getInstance().cleanAllNotify();
        infoRepository.synchroniseWithTox(ToxManager.getManager().toxBase.getFriendList());
        ToxManager.getManager().saveAndClose();
        userRepository.logout();
        infoRepository.destroy();
        userRepository.destroy();
    }
}
