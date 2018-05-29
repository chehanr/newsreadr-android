package com.chehanr.newsreadr.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.chehanr.newsreadr.database.dao.SavedArticlesDao;
import com.chehanr.newsreadr.database.entity.SavedArticle;

/**
 * Created by chehanr on 9/22/2017.
 */

@Database(entities = {SavedArticle.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, AppDatabase.class, "saved_articles_db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract SavedArticlesDao savedArticlesDao();
}
