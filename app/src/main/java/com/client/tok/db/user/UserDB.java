package com.client.tok.db.user;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import com.client.tok.bean.UserInfo;

/**
 * user infoRepo
 */
@Database(entities = { UserInfo.class }, version = 32, exportSchema = false)
public abstract class UserDB extends RoomDatabase {
    private static String USER_DB_NAME = "userdb";
    private static UserDB INSTANCE;
    private static final Object sLock = new Object();

    public static UserDB getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), UserDB.class,
                    USER_DB_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_30_31, MIGRATION_31_32)
                    .build();
            }
            return INSTANCE;
        }
    }

    static final Migration MIGRATION_30_31 = new Migration(30, 31) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //scheme change
            database.execSQL("ALTER TABLE `users` RENAME TO `users_old`");
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `users` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `username` TEXT, `password` TEXT, `nickname` TEXT, `status` TEXT, `status_message` TEXT, `avatar` TEXT)");
            database.execSQL(
                "INSERT INTO `users` (`username`, `password`, `nickname`,`status`,`status_message`,`avatar`) SELECT `username`, `password`, `nickname`,`status`,`status_message`,`avatar` FROM users_old");

            database.execSQL("DROP TABLE IF EXISTS `users_old`");
        }
    };

    static final Migration MIGRATION_31_32 = new Migration(31, 32) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `users` ADD `login_time` INTEGER NOT NULL default 0");
        }
    };

    public abstract UserDao userDao();

    public void destroy() {
        if (INSTANCE != null) {
            INSTANCE = null;
        }
    }
}
