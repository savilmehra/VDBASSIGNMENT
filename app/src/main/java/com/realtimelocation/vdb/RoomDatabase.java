package com.realtimelocation.vdb;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.database.SQLException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


@Database(entities = {JakesEntity.class}, version = 1, exportSchema = false)

public abstract class RoomDatabase extends android.arch.persistence.room.RoomDatabase {

    private static RoomDatabase db;

    public abstract DaoForJakes daoForJakes();

    public static RoomDatabase getAppDatabase(Context context) {

        if (db == null) {
            synchronized (RoomDatabase.class) {
                if (db == null) {

                    db = Room.
                            databaseBuilder(context.getApplicationContext(), RoomDatabase.class, "jakes.db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                    }

            }
        }
        return db;
    }



    public static boolean updateDatabaseCreated(final Context context, String tableName) {
        if (context.getDatabasePath(tableName).exists()) {
            return true;
        }
        return false;
    }

    public static void destroyInstance() {
        db = null;
    }


}
