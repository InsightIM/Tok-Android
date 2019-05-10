package com.client.tok.db.info;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import com.client.tok.bean.FriendRequest;
import com.client.tok.db.BaseDao;
import java.util.List;

@Dao
public interface FriendReqDao extends BaseDao<FriendRequest> {
    @Query("select * from friend_requests")
    LiveData<List<FriendRequest>> getAllObserver();

    @Query("select * from friend_requests")
    List<FriendRequest> getAll();

    @Query("select count(*) from friend_requests where has_read=0")
    LiveData<Integer> getUnReadCountLive();

    @Query("select count(*) from friend_requests where has_read=0")
    int getUnReadCount();

    @Query("update friend_requests set has_read=1 where has_read=0 and tox_key=:key")
    int setHasRead(String key);

    @Query("select * from friend_requests where tox_key=:key")
    FriendRequest queryByKey(String key);

    @Query("delete from friend_requests where tox_key=:key")
    int delFriendReq(String key);

    @Query("delete from friend_requests")
    int delAll();
}
