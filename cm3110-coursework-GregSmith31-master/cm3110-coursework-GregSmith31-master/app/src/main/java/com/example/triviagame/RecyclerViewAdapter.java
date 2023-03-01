package com.example.triviagame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviagame.data.UserScores;

import java.util.List;



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.UserScoresViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private Context context;
    // The data to be displayed
    LiveData<List<UserScores>> userScores;

    /**
     *
     * @param context
     * @param userScores
     *
     */
    public RecyclerViewAdapter(Context context,LiveData<List<UserScores>> userScores){
        super();
        //initialization
        this.context = context;
        this.userScores = userScores;
    }

    @NonNull
    @Override
    public UserScoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout file for row
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_listitem, parent, false);
        //Store in view holder
        UserScoresViewHolder viewHolder = new UserScoresViewHolder(itemView, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserScoresViewHolder holder, int position) {
        // get userScore at position
        UserScores userScores = this.userScores.getValue().get(position);
        // update user score name (username)
        TextView tv_userScoreName  = holder.userItemView.findViewById(R.id.tv_userScoreListItemName);
        tv_userScoreName.setText(userScores.getUsername());
        // update user score score
        TextView tv_userScoreNumber  = holder.userItemView.findViewById(R.id.tv_userScoreListItemScore);
        tv_userScoreNumber.setText(String.valueOf(userScores.getUserScore()));

    }

    @Override
    public int getItemCount() {
        // if tasks is null, return 0, otherwise return the size of tasks
        return (this.userScores.getValue() == null)? 0 :this.userScores.getValue().size();
    }


    public class UserScoresViewHolder extends RecyclerView.ViewHolder{
        private View userItemView;
        private RecyclerViewAdapter adapter;
        public UserScoresViewHolder(View userItemView, RecyclerViewAdapter adapter) {
            super(userItemView);
            this.userItemView = userItemView;
            this.adapter = adapter;
        }
    }
}
