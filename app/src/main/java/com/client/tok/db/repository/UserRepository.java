package com.client.tok.db.repository;

import android.arch.lifecycle.LiveData;
import com.client.tok.R;
import com.client.tok.TokApplication;
import com.client.tok.bean.ToxAddress;
import com.client.tok.bean.ToxMeName;
import com.client.tok.bean.UserInfo;
import com.client.tok.db.user.UserDB;
import com.client.tok.db.user.UserDao;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.PreferenceUtils;
import com.client.tok.utils.StringUtils;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.data.ToxStatusMessage;
import java.util.List;

import static com.client.tok.db.DBConstants.COLUMN_AVATAR;
import static com.client.tok.db.DBConstants.COLUMN_NICK_NAME;
import static com.client.tok.db.DBConstants.COLUMN_PASSWORD;
import static com.client.tok.db.DBConstants.COLUMN_STATUS_MESSAGE;

public class UserRepository {
    private String TAG = "UserRepository";
    private UserDB userDB;
    private UserDao userDao;

    public UserRepository() {
        userDB = UserDB.getInstance(TokApplication.getInstance());
        userDao = userDB.userDao();
    }

    public String getActiveUser() {
        return PreferenceUtils.getAccount();
    }

    public void login(String userName) {
        PreferenceUtils.saveAccount(userName);
        PreferenceUtils.hasShowGuide();
        updateLoginTime(userName);
    }

    public boolean loggedIn() {
        return !StringUtils.isEmpty(getActiveUser());
    }

    public void logout() {
        PreferenceUtils.clearDefaultShare();
    }

    public long addUser(ToxMeName toxMeName, ToxAddress toxId, String password) {
        UserInfo user = new UserInfo(toxMeName, password,
            ToxNickname.unsafeFromValue(toxMeName.getUserName().getBytes()), "online",
            ToxStatusMessage.unsafeFromValue(
                StringUtils.getTextFromResId(R.string.default_signature).getBytes()), "",
            System.currentTimeMillis());
        long dbId = userDao.insert(user);
        LogUtil.i(TAG, "addUser,dbId:" + dbId);
        return dbId;
    }

    private int updateLoginTime(String userName) {
        return userDao.updateLoginTime(System.currentTimeMillis(), userName);
    }

    public boolean doesUserExist(String userName) {
        return userDao.queryCountByName(userName) > 0;
    }

    public boolean doesUserExist(String userName, String pwd) {
        return userDao.queryCountByNameAndPwd(userName, pwd) > 0;
    }

    public int deleteActiveUser() {
        String userName = getActiveUser();
        int delCount = userDao.delByName(userName);
        LogUtil.i(TAG, "deleteActiveUser,delCount:" + delCount);
        return delCount;
    }

    public UserInfo getActiveUserDetails() {
        String userName = getActiveUser();
        return userDao.queryByName(userName);
    }

    public UserInfo getUserDetails(String userName) {
        return userDao.queryByName(userName);
    }

    public LiveData<UserInfo> activeUserDetailsObservable() {
        String userName = getActiveUser();
        return userDao.getUserObserver(userName);
    }

    public List<UserInfo> getAllUser() {
        return userDao.getAll();
    }

    public boolean hasPwd(String userName) {
        UserInfo userInfo = userDao.queryByName(userName);
        return userInfo != null && !StringUtils.isEmpty(userInfo.getPassword());
    }

    public int changePwd(String userName, String newPwd) {
        return updateUserDetail(userName, COLUMN_PASSWORD, newPwd);
    }

    public int updateActiveUserDetail(String key, String value) {
        return updateUserDetail(getActiveUser(), key, value);
    }

    public int updateUserDetail(String userName, String key, String value) {
        UserInfo userInfo = getUserDetails(userName);
        if (userInfo != null) {
            switch (key) {
                case COLUMN_AVATAR:
                    userInfo.setAvatarName(value);
                    break;
                case COLUMN_PASSWORD:
                    userInfo.setPassword(value);
                    break;
                case COLUMN_STATUS_MESSAGE:
                    userInfo.setStatusMessage(ToxStatusMessage.unsafeFromValue(value.getBytes()));
                    break;
                case COLUMN_NICK_NAME:
                    userInfo.setNickname(ToxNickname.unsafeFromValue(value.getBytes()));
                    break;
            }
            return userDao.update(userInfo);
        } else {
            return 0;
        }
    }

    public void destroy() {
        if (userDB != null) {
            userDB.destroy();
            userDB = null;
        }
    }
}
