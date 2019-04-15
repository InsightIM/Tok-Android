package com.client.tok.ui.login.login;

import com.client.tok.TokApplication;
import com.client.tok.bean.ToxAddress;
import com.client.tok.bean.ToxData;
import com.client.tok.bean.ToxMeName;
import com.client.tok.db.DBConstants;
import com.client.tok.tox.State;
import com.client.tok.tox.ToxDataFile;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.PreferenceUtils;
import com.client.tok.utils.StorageUtil;
import im.tox.tox4j.core.options.SaveDataOptions;
import im.tox.tox4j.core.options.ToxOptions;
import im.tox.tox4j.impl.jni.ToxCoreImpl;
import java.io.File;

public class UserModel {
    public boolean createUser(String rawAccountName, String userPwd, boolean shouldCreateDataFile,
        boolean shouldRegister) {
        ToxMeName toxMeName = ToxMeName.fromString(rawAccountName, shouldRegister);
        ToxData toxData;
        if (shouldCreateDataFile) {
            toxData = createToxData(toxMeName.getUserName());
        } else {
            toxData = loadToxData(toxMeName.getUserName());
        }

        if (toxData != null) {
            State.userRepo().addUser(toxMeName, toxData.getAddress(), userPwd);
            setLoginUserInfo(toxMeName.getUserName(), userPwd);
            return true;
        }
        return false;
    }

    public ToxData createToxData(String accountName) {
        ToxData toxData = new ToxData();
        ToxOptions toxOptions = new ToxOptions();
        ToxCoreImpl tox = new ToxCoreImpl(toxOptions);
        ToxDataFile toxDataFile = new ToxDataFile(TokApplication.getInstance(), accountName);
        toxDataFile.saveFile(tox.getSaveData());
        toxData.setAddress(new ToxAddress(tox.getSelfAddress()));
        toxData.setFileBytes(toxDataFile.loadFile());
        return toxData;
    }

    public ToxData loadToxData(String fileName) {
        try {
            ToxData toxData = new ToxData();
            ToxDataFile toxDataFile = new ToxDataFile(TokApplication.getInstance(), fileName);
            ToxOptions toxOptions = new ToxOptions(SaveDataOptions.ToxSave(toxDataFile.loadFile()));
            ToxCoreImpl tox = new ToxCoreImpl(toxOptions);

            toxData.setAddress(new ToxAddress(tox.getSelfAddress()));
            toxData.setFileBytes(toxDataFile.loadFile());
            return toxData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setLoginUserInfo(String accountName, String pwd) {
        State.login(accountName);
        State.userRepo().updateActiveUserDetail(DBConstants.COLUMN_PASSWORD, pwd);
    }

    /**
     * clear current user's chat history(include txt information and files)
     */
    public boolean clearChatHistory() {
        State.infoRepo().deleteAllMessage();
        FileUtilsJ.deleteDir(new File(StorageUtil.getFilesFolder()));
        StorageUtil.initFolders();
        return true;
    }

    public boolean destroyAccount() {
        State.infoRepo().deleteAll();
        FileUtilsJ.deleteDir(new File(StorageUtil.getAppRootFolder()));
        StorageUtil.initFolders();
        State.userRepo().deleteActiveUser();
        PreferenceUtils.clearUserShare();
        PreferenceUtils.clearDefaultShare();
        return true;
    }
}
