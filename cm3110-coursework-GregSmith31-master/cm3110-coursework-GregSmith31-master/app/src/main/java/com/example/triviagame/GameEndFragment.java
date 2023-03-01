package com.example.triviagame;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;
//TODO ADD SHARE
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameEndFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameEndFragment extends Fragment implements View.OnClickListener {
    //Global variable for the fragment view
    View v;

    // the fragment initialization parameters
    private static final String ARG_PARAM_USERNAMEEND = "usernameEnd";
    private static final String ARG_PARAM_SCOREEND = "scoreEnd";

    // Parameters
    private String mUsernameEnd;
    private int mScoreEnd;

    public GameEndFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param usernameEnd The users username for the game
     * @param scoreEnd The users score for the game
     * @return A new instance of fragment GameEndFragment.
     */
    public static GameEndFragment newInstance(String usernameEnd, int scoreEnd) {
        GameEndFragment fragment = new GameEndFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_USERNAMEEND, usernameEnd);
        args.putInt(ARG_PARAM_SCOREEND, scoreEnd);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mUsernameEnd = getArguments().getString(ARG_PARAM_USERNAMEEND);
            this.mScoreEnd = getArguments().getInt(ARG_PARAM_SCOREEND);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_game_end, container, false);
        TextView tvUserNameEnd = v.findViewById(R.id.tvUsernameEnd);
        tvUserNameEnd.setText(this.mUsernameEnd);
        TextView tvScoreEnd =v.findViewById(R.id.tvEndScore);
        tvScoreEnd.setText(String.valueOf(this.mScoreEnd));
        Button playAgainButton = v.findViewById(R.id.btnPlayAgain);
        Button homeEndButton = v.findViewById(R.id.btnHomeEnd);
        Button shareButton = v.findViewById(R.id.btnShare);
        homeEndButton.setOnClickListener(this);
        playAgainButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        return v;
    }
//On click method for all buttons within fragment
    @Override
    public void onClick(View view) {
        NavController navController = Navigation.findNavController(view);
        switch (view.getId()){
            //Navigates user back to play game page passing the same username that was passed into this fragment from last game play
            // so that the users username is set as the same as previous and they can get on with playing the game.
            case R.id.btnPlayAgain:
                TextView username = v.findViewById(R.id.tvUsernameEnd);
                String usernamePlay = username.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("usernamePlay", usernamePlay);

                navController.navigate(R.id.gameEnd_to_gamePlay, bundle);

                break;
            // Navigates user back to home page
            case R.id.btnHomeEnd:
                navController.navigate(R.id.gameEnd_to_landing);
                break;

            case R.id.btnShare:
                //Allows user to share their score via an SMS messaging app of their choice using explicit intents, mobile specific feature.
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String message = "";
                message += String.format("I just got %1$d can you believe it! Me, %2$s",mScoreEnd,mUsernameEnd);
                Log.d("Test", message);
                intent.putExtra("sms_body",message);
                startActivity(intent);
                break;
        }

    }
}