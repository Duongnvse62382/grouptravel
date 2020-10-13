package com.fpt.gta.room.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.fpt.gta.room.daos.UserDao;
import com.fpt.gta.room.entities.User;

@Database(entities = {User.class}, exportSchema = false, version = RMaPDatabase.DATABASE_VERSION)
public abstract class RMaPDatabase extends RoomDatabase {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "rmap-database";
    private static RMaPDatabase INSTANCE;

    public abstract UserDao userDao();

    public static RMaPDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RMaPDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), RMaPDatabase.class, DATABASE_NAME)
                            .build();
                }
            }

        }
        return INSTANCE;
    }
}
