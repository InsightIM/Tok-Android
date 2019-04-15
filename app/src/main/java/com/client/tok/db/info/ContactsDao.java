package com.client.tok.db.info;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import com.client.tok.bean.ContactsInfo;
import com.client.tok.db.BaseDao;
import java.util.List;

@Dao
public interface ContactsDao extends BaseDao<ContactsInfo> {

    @Query("select * from friend_contacts")
    LiveData<List<ContactsInfo>> getAllObserver();

    @Query("select * from friend_contacts")
    List<ContactsInfo> getAll();

    @Query("select * from friend_contacts where contact_type=:contactType")
    LiveData<List<ContactsInfo>> contactsListObserver(int contactType);

    @Query("select * from friend_contacts where tox_key=:key")
    LiveData<ContactsInfo> friendInfoObserver(String key);

    @Query("select * from friend_contacts where tox_key=:key")
    ContactsInfo contactsInfo(String key);

    @Query("select * from friend_contacts where contact_type=2")
    List<ContactsInfo> groupList();

    @Query("select count(*) from friend_contacts where tox_key=:key")
    int countByKey(String key);

    @Query("update friend_contacts set isonline=0")
    int setAllOffline();

    @Query("delete from friend_contacts where tox_key=:key")
    int delContactByKey(String key);

    @Query("update friend_contacts set received_avatar=:receivedAvatar")
    int setAllFriendReceivedAvatar(boolean receivedAvatar);

    @Query("select * from friend_contacts where contact_type=1 and isonline==1 and received_avatar==0")
    List<ContactsInfo> getUnsendAvatarFriendList();

    @Query("delete from friend_contacts")
    int delAll();

    @Query("update friend_contacts set alias=:alias where tox_key=:key")
    int updateAliasByKey(String key, String alias);

    @Query("update friend_contacts set mute=:mute where tox_key=:key")
    int setContactMute(String key, boolean mute);
}
