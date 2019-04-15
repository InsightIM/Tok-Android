package com.client.tok.db.info;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import com.client.tok.bean.Conversation;
import com.client.tok.bean.ConversationItem;
import com.client.tok.db.BaseDao;
import java.util.List;

@Dao
public interface ConversationDao extends BaseDao<Conversation> {

    @Query(
        "select c.tox_key as cKey,conts.name as name,conts.alias as alias,conts.contact_type as contactType,"
            + "conts.isonline as isOnline,conts.status as status,conts.mute as isMute,"
            + "c.last_msg_db_id as lastMsgDbId,msgs.type as msgType,msgs.message as lastMsg,"
            + "msgs.timestamp as lastMsgTime,c.unread_count as unreadCount,c.update_time as updateTime "
            + "from friend_conversation c "
            + "left join friend_contacts conts on c.tox_key==conts.tox_key "
            + "left join friend_messages msgs on c.last_msg_db_id=msgs._id "
            + "order by c.update_time desc")
    LiveData<List<ConversationItem>> getConversationLive();

    @Query("delete from friend_conversation")
    int delAll();

    @Query("update friend_conversation set unread_count=0 where tox_key=:key")
    int clearUnreadTag(String key);

    @Query("delete from friend_conversation where tox_key=:key")
    int delByKey(String key);

    @Query("select * from friend_conversation where tox_key=:key")
    Conversation getConversationByKey(String key);
}
