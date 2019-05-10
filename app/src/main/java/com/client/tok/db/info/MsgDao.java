package com.client.tok.db.info;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import com.client.tok.bean.Message;
import com.client.tok.db.BaseDao;
import com.client.tok.pagejump.GlobalParams;
import java.util.List;

@Dao
public interface MsgDao extends BaseDao<Message> {
    @Query("select * from friend_messages where tox_key=:key and type!=2 and file_kind!=1")
    List<Message> getAll(String key);

    @Query("select * from friend_messages where tox_key=:key and file_kind!=1 order by timestamp asc")
    LiveData<List<Message>> getAllMsgLive(String key);

    @Query("delete from friend_messages where tox_key=:key and type==2 and file_kind=1")
    int delAvatarMessage(String key);

    @Query("update friend_messages set sent_status=:sentStatus where tox_key=:key and message_id=:fileNumber ")
    int updateSentStatus(String key, int fileNumber, int sentStatus);

    @Query("select count(*) from friend_messages where has_been_read=0 and file_kind!=1")
    LiveData<Integer> totalUnread();

    @Query("select count(*) from friend_messages where tox_key=:key and has_been_read=0 and file_kind!=1")
    int totalUnreadCount(String key);

    @Query("select count(*) from friend_messages where has_been_read=0 and file_kind!=1")
    int totalUnreadCount();

    @Query("select _id from friend_messages where tox_key=:key and message_id=:fileNumber and type==2")
    int getFileId(String key, int fileNumber);

    @Query("select count(*) from friend_messages where tox_key=:key and message_id=:messageId")
    int getMsgCount(String key, long messageId);

    @Query("update friend_messages set message_id=-1 where type=2")
    int clearFileNumbers();

    @Query("update friend_messages set message_id=-1 where type=2 and message_id=:fileNumber and tox_key=:key")
    int clearFileNumbers(String key, int fileNumber);

    @Query("update friend_messages set sent_status=:sentStatus,receive_status=:receiveStatus where type=2 and message_id=:fileNumber and tox_key=:key")
    int setFileStatus(String key, int fileNumber, int sentStatus, int receiveStatus);

    @Query("select * from  friend_messages where sent_status="
        + GlobalParams.SEND_FAIL
        + " and tox_key=:key and type!=2")
    List<Message> getUnsentMsgList(String key);

    @Query("select * from  friend_messages where tox_key=:key and file_kind!=1 order by timestamp desc limit 0,1")
    Message getLastMessage(String key);

    @Query("update friend_messages set sent_status="
        + GlobalParams.SEND_FAIL
        + " where message_id=:receiptId and sent_status="
        + GlobalParams.SEND_ING)
    int setMsgFailByMsgId(long receiptId);

    @Query("update friend_messages set sent_status="
        + GlobalParams.SEND_FAIL
        + " where _id=:dbId and sent_status="
        + GlobalParams.SEND_ING)
    int setMsgFailByDbId(long dbId);

    @Query("update friend_messages set sent_status="
        + GlobalParams.SEND_ING
        + ",message_id=:messageId where _id=:dbId")
    int setMsgSending(long messageId, long dbId);

    @Query("update friend_messages set sent_status="
        + GlobalParams.SEND_SUCCESS
        + " where message_id=:messageId and type=1")
    int setMessageReceived(long messageId);

    @Query("update friend_messages set sent_status="
        + GlobalParams.SEND_SUCCESS
        + " where _id in(select _id from friend_messages where message_id=:messageId and type=1 and tox_key=:key order by timestamp desc limit 0,1)")
    int setMessageReceived(long messageId, String key);

    @Query("update friend_messages set has_been_read=1 where tox_key=:key and has_been_read=0")
    int markReaded(String key);

    @Query("delete from friend_messages where _id=:dbId")
    int delMsgByDbId(long dbId);

    @Query("delete from friend_messages where tox_key=:key")
    int delMsgByKey(String key);

    @Query("select * from friend_messages where tox_key=:key and type=:type")
    Message queryMsgByKeyAndType(String key, int type);

    @Query("delete from friend_messages")
    int delAll();

    @Query("update friend_messages set has_played=:hasPlayed where _id=:dbId")
    int setHasPlayed(long dbId, boolean hasPlayed);

    @Query("select * from  friend_messages where tox_key=:key and type=2 and file_kind=0")
    List<Message> getFileMessage(String key);

    @Query("select * from  friend_messages where _id=:dbId")
    Message getMessageByDbId(long dbId);
}
