package com.example.triviagame.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class UserScoresRepository {
    //member field
    private UserScoresDao mUserScoresDao;
    //member field
    private LiveData<List<UserScores>> mUserScores;
    //Singleton instance for repository
    private static UserScoresRepository INSTANCE;
    //context app is operating within
    private Context context;
    private static final String TAG = "TaskRepository";
    private UserScoresRepository(Context context){
        super();
        this.context = context;
        //setup for userScoresDao
        mUserScoresDao = UserScoresDatabase.getDatabase(context).userScoresDao();
        // get all user scores
        mUserScores = mUserScoresDao.getAllUserScores();
    }
    public static UserScoresRepository getRepository(Context context){
        if (INSTANCE == null){
            synchronized (UserScoresRepository.class){
                INSTANCE = new UserScoresRepository(context);
            }
        }
        return INSTANCE;
    }
//Storing user score in local database
    public void storeUserScore(UserScores userScores) {
        Executor exe = Executors.newSingleThreadExecutor();
        exe.execute(new Runnable() {
            @Override
            public void run() {
                // store in the local database
                mUserScoresDao.insert(userScores);
            }
        });

    }
// Delete tasks from local database
    public void deleteScores(LiveData<List<UserScores>>userScores){
        Executor exe = Executors.newSingleThreadExecutor();
        exe.execute(new Runnable() {
            @Override
            public void run() {
                mUserScoresDao.deleteScores(userScores.getValue());
            }
        });
    }


    public LiveData<List<UserScores>> getAllUserScores(){
        return mUserScoresDao.getAllUserScores();
    }

}
