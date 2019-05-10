package com.client.tok.ui.setting;

import com.client.tok.bean.UserInfo;
import com.client.tok.tox.ToxManager;
import com.client.tok.tox.State;
import com.client.tok.utils.StorageUtil;

public class SettingModel {
    public boolean exportAccountInfo() {
        try {
            String profileFolder = StorageUtil.getProfileFolder();
            UserInfo userInfo = State.userRepo().getActiveUserDetails();
            return ToxManager.getManager().exportDataFile(profileFolder, userInfo.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
