package com.client.tok.db.user;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import com.client.tok.bean.UserInfo;
import com.client.tok.db.BaseDao;
import java.util.List;

@Dao
public interface UserDao extends BaseDao<UserInfo> {

    @Query("SELECT COUNT(*) FROM users WHERE username=:userName")
    int queryCountByName(String userName);

    @Query("SELECT COUNT(*) FROM users WHERE username=:userName AND password=:pwd")
    int queryCountByNameAndPwd(String userName, String pwd);

    @Query("SELECT * FROM users WHERE username=:userName")
    UserInfo queryByName(String userName);

    @Query("UPDATE users SET login_time=:loginTime WHERE username=:userName")
    int updateLoginTime(long loginTime, String userName);

    @Query("DELETE FROM users WHERE username=:userName")
    int delByName(String userName);

    @Query("SELECT * FROM users WHERE username=:userName")
    LiveData<UserInfo> getUserObserver(String userName);

    @Query("SELECT * FROM users ORDER BY login_time DESC")
    List<UserInfo> getAll();
}
