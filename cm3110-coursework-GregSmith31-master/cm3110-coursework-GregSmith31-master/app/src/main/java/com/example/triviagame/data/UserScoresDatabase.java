package com.example.triviagame.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserScores.class},version = 1)
public abstract class UserScoresDatabase extends RoomDatabase {

    public abstract UserScoresDao userScoresDao();
    private static UserScoresDatabase INSTANCE;
    public static UserScoresDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (UserScoresDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    UserScoresDatabase.class, "user_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
