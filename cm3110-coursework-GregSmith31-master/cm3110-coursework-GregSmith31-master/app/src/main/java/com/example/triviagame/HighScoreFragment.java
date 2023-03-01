package com.example.triviagame;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.example.triviagame.data.UserScores;
import com.example.triviagame.data.UserScoresRepository;
import com.example.triviagame.data.UserScoresViewModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HighScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HighScoreFragment extends Fragment implements AdapterView.OnClickListener{
    //View model for user tasks
    private UserScoresViewModel mUserScoresViewModel;
    // List of scores being displayed
    LiveData<List<UserScores>> mUserScores;
    // the RecyclerView adapter being used to display them
    RecyclerView.Adapter rvAdapter;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public HighScoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HighScoreFragment.
     */

    public static HighScoreFragment newInstance(String param1, String param2) {
        HighScoreFragment fragment = new HighScoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //this.mUserScores = new ArrayList<UserScores>();

        // get a ViewModelProvider for this fragment
        ViewModelProvider provider = new ViewModelProvider(this);

        this.mUserScoresViewModel = provider.get(UserScoresViewModel.class);

        // get all user scores
        this.mUserScores = this.mUserScoresViewModel.getAllUserScores();
        // now observe any changes
        this.mUserScores.observe(this, new Observer<List<UserScores>>() {
            @Override
            public void onChanged(List<UserScores> userScores) {
                if (rvAdapter != null){
                    rvAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_high_score, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Button btnDeleteScores = view.findViewById(R.id.btnDeleteScores);
        btnDeleteScores.setOnClickListener(this);
        // Recycler view from UI
        RecyclerView rv = view.findViewById(R.id.rv_userScoreRecyclerView);
        // new adapter for recycler view
        rvAdapter =new RecyclerViewAdapter(getContext(),this.mUserScores);

        rv.setAdapter(rvAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));


    }

    @Override
    public void onClick(View view) {
        // Deletes all entries in the database on user request, also empties recycler view by doing so
        if(view.getId()==R.id.btnDeleteScores){
            UserScoresRepository.getRepository(getContext()).deleteScores(this.mUserScores);
        }
    }
}