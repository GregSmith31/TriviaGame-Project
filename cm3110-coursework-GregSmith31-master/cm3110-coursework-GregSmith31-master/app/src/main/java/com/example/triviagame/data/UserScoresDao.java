package com.example.triviagame.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;


@Dao
public interface UserScoresDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(UserScores us);

    @Query("Select * from UserScores")
    public LiveData<List<UserScores>> getAllUserScores();

    @Delete
    public void deleteScores(List<UserScores> userScores);

}
