package com.example.triviagame.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserScores")
public class UserScores {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int uid;
    // users username
    private String username;
    // Users score
    private int userScore;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String usernameEnd) {
        this.username = usernameEnd;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int scoreEnd) {
        this.userScore = scoreEnd;
    }

}
