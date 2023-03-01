package com.example.triviagame;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameStartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameStartFragment extends Fragment implements View.OnClickListener {
    // global variable initialized
    private EditText username;
    private View v;
    // Text Watcher used to check for users input into EditText field so that play game button can only be clicked when
    // a non space character has been entered to check for valid username.
    // TextWatcher code influenced by "https://www.youtube.com/watch?v=Vy_4sZ6JVHM"
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String usernameInput = username.getText().toString().trim();
            v.findViewById(R.id.btnPlay).setEnabled(!usernameInput.isEmpty());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public GameStartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameStartFragment.
     */
    public static GameStartFragment newInstance(String param1, String param2) {
        GameStartFragment fragment = new GameStartFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_game_start, container, false);

        Button playButton = v.findViewById(R.id.btnPlay);
        Button highScoreButton = v.findViewById(R.id.btnHighScore);
        //On click listeners for navigation
        highScoreButton.setOnClickListener(this);
        playButton.setOnClickListener(this);

        username = (EditText) v.findViewById(R.id.etUserName);
        username.addTextChangedListener(textWatcher);


        return v;
    }
// Navigation to other pages via button clicks
    @Override
    public void onClick(View view) {
        NavController navController = Navigation.findNavController(view);
        switch (view.getId()){
            case R.id.btnPlay:
                String usernamePlay = username.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("usernamePlay", usernamePlay);

                navController.navigate(R.id.landing_to_game_start,bundle);

                break;
            case R.id.btnHighScore:
                navController.navigate(R.id.landing_to_highScore);
                break;
        }

    }
}