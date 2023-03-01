package com.example.triviagame.data;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class UserScoresViewModel extends AndroidViewModel {

    private UserScoresRepository mRepository;
    private LiveData<List<UserScores>> mUserScores;

    public UserScoresViewModel(Application application){
        super(application);
        mRepository = UserScoresRepository.getRepository(application.getApplicationContext());
        mUserScores = mRepository.getAllUserScores();
    }

    public LiveData<List<UserScores>> getAllUserScores(){
        return mUserScores;
    }
    public void deleteTasks(LiveData<List<UserScores>> userScores){
        mRepository.deleteScores(userScores);
    }
}
