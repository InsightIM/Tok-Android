package com.client.tok.db.info;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import com.client.tok.bean.ContactInfo;
import com.client.tok.bean.Conversation;
import com.client.tok.bean.FriendRequest;
import com.client.tok.bean.Message;
import com.client.tok.utils.PreferenceUtils;

/**
 * message infoRepo
 */
@Database(entities = {
    Conversation.class, ContactInfo.class, Message.class, FriendRequest.class
}, version = 35, exportSchema = false)
public abstract class InfoDB extends RoomDatabase {

    private static InfoDB INSTANCE;
    private static final Object sLock = new Object();

    public static InfoDB getInstance(Context context) {
        synchronized (sLock) {
            String dbName = PreferenceUtils.getAccount();
            if (INSTANCE == null) {
                INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), InfoDB.class, dbName)
                        .allowMainThreadQueries()
                        .addMigrations(MIGRATION_32_33, MIGRATION_33_34, MIGRATION_34_35)
                        .build();
            }
            return INSTANCE;
        }
    }

    static final Migration MIGRATION_32_33 = new Migration(32, 33) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `friend_conversation` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tox_key` TEXT, `last_msg_db_id` INTEGER NOT NULL, `update_time` INTEGER NOT NULL, `unread_count` INTEGER NOT NULL)");

            //db table update friend_requests
            database.execSQL("ALTER TABLE `friend_requests` RENAME TO `friend_requests_old`");
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `friend_requests` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tox_key` TEXT, `message` TEXT, `has_read` INTEGER NOT NULL default 0)");
            database.execSQL(
                "INSERT INTO `friend_requests` (`tox_key`, `message`) SELECT `tox_key`, `message` FROM friend_requests_old");
            database.execSQL("DROP TABLE IF EXISTS `friend_requests_old`");

            //db table update friend_contacts
            database.execSQL("ALTER TABLE `friend_contacts` RENAME TO `friend_contacts_old`");
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `friend_contacts` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tox_key` TEXT, `name` TEXT, `avatar` TEXT, `isonline` INTEGER NOT NULL, `status` TEXT, `note` TEXT, `received_avatar` INTEGER NOT NULL, `isblocked` INTEGER NOT NULL, `mute` INTEGER NOT NULL, `alias` TEXT, `contact_type` INTEGER NOT NULL, `is_bot` INTEGER NOT NULL default 0, `bot_type` INTEGER NOT NULL default 0)");
            database.execSQL(
                "INSERT INTO `friend_contacts` (`tox_key`, `name`,`avatar`,`isonline`, `status`, `note`, `received_avatar`, `isblocked`,`mute`,`alias`, `contact_type`) SELECT `tox_key`, `name`,`avatar`,`isonline`, `status`, `note`, `received_avatar`, `isblocked`,`mute`,`alias`, `contact_type` FROM friend_contacts_old");
            database.execSQL("DROP TABLE IF EXISTS `friend_contacts_old`");

            //db table update friend_messages
            database.execSQL("ALTER TABLE `friend_messages` RENAME TO `friend_messages_old`");
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `friend_messages` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `message_id` INTEGER NOT NULL, `tox_key` TEXT, `sender_key` TEXT, `sender_name` TEXT, `message` TEXT, `sent_status` INTEGER NOT NULL, `receive_status` INTEGER NOT NULL, `has_been_read` INTEGER NOT NULL, `has_played` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `size` INTEGER NOT NULL, `type` INTEGER, `file_kind` INTEGER)");
            database.execSQL(
                "INSERT INTO `friend_messages` (`message_id`, `tox_key`,`sender_key`,`sender_name`, `message`, `sent_status`, `receive_status`, `has_been_read`,`has_played`,`timestamp`, `size`, `type`, `file_kind`) SELECT `message_id`, `tox_key`,`sender_key`,`sender_name`, `message`, `sent_status`, `receive_status`, `has_been_read`,`has_played`,`timestamp`, `size`, `type`, `file_kind` FROM friend_messages_old");
            database.execSQL("DROP TABLE IF EXISTS `friend_messages_old`");

            //db table update group_peers
            database.execSQL("ALTER TABLE `group_peers` RENAME TO `group_peers_old`");
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `group_peers` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `group_number` INTEGER NOT NULL, `peer_pk` TEXT, `peer_name` TEXT, `peer_signature` TEXT)");
            database.execSQL(
                "INSERT INTO `group_peers` (`group_number`, `peer_pk`,`peer_name`,`peer_signature`) SELECT `group_number`, `peer_pk`,`peer_name`,`peer_signature` FROM group_peers_old");
            database.execSQL("DROP TABLE IF EXISTS `group_peers_old`");
        }
    };

    static final Migration MIGRATION_33_34 = new Migration(33, 34) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //TODO
            database.execSQL("DROP TABLE IF EXISTS `group_peers`");
        }
    };

    static final Migration MIGRATION_34_35 = new Migration(34, 35) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //add column(friend_contacts) on TABLE 'friend_contacts'
            database.execSQL("ALTER TABLE `friend_contacts` ADD `has_offline_bot` INTEGER NOT NULL default 0");
        }
    };

    public abstract ConversationDao conversationDao();

    public abstract MsgDao messageDao();

    public abstract ContactsDao contactsDao();

    public abstract FriendReqDao friendReqDao();

    public void destroy() {
        if (INSTANCE != null) {
            INSTANCE = null;
        }
    }
}
