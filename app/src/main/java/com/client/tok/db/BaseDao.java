package com.client.tok.db;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;
import java.util.List;

/**
 * https://blog.iamsuleiman.com/android-architecture-components-tutorial-room-livedata-viewmodel/
 * @param <T>
 */
public interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(T obj);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(List<T> objList);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(T obj);

    @Delete
    int delete(T obj);

    @Delete
    int deleteList(List<T> objList);
}
