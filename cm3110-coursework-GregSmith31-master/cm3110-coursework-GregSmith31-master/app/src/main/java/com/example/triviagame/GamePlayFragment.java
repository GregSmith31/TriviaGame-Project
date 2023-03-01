package com.example.triviagame;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.triviagame.data.UserScores;
import com.example.triviagame.data.UserScoresRepository;

import java.util.ArrayList;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GamePlayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GamePlayFragment extends Fragment implements View.OnClickListener {
    //Initializing all global variables needed
    View v;
    int currentQuestion = 0;
    int score = 0;
    int incorrectIncrementer;
    int totalQuestion;
    int randomInt = -1;
    Boolean connectionFailure;
    String selectedAnswer;
    Button btnAnswer1;
    Button btnAnswer2;
    Button btnAnswer3;
    Button btnAnswer4;
    ProgressBar loadingSpinner;
    public  ArrayList<String> questionList = new ArrayList<>();
    public  ArrayList<String> correctAnswers = new ArrayList<>();
    public  ArrayList<String> incorrectAnswers = new ArrayList<>();


    // Parameter names that match
    public static final String ARG_PARAM_USERNAME = "usernamePlay";

    // parameters
    private String mUsername;


    public GamePlayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param username The users username for the game
     * @return A new instance of fragment GamePlayFragment.
     */
    public static GamePlayFragment newInstance(String username) {
        GamePlayFragment fragment = new GamePlayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionFailure = false; //Setting Connection flag for initial instance of view
        if (getArguments() != null) {
            this.mUsername = getArguments().getString(ARG_PARAM_USERNAME);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_play, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        //Setting values of each element needed to run the game, needed each fragment lifecycle
        v = view;
        TextView tvPlayerName = v.findViewById(R.id.tvPlayerName);
        tvPlayerName.setText(this.mUsername);
        btnAnswer1 = v.findViewById(R.id.btnAnswer1);
        btnAnswer2 = v.findViewById(R.id.btnAnswer2);
        btnAnswer3 = v.findViewById(R.id.btnAnswer3);
        btnAnswer4 = v.findViewById(R.id.btnAnswer4);
        loadingSpinner = v.findViewById(R.id.pbLoadingSpinner);
        btnAnswer1.setVisibility(v.INVISIBLE);
        btnAnswer2.setVisibility(v.INVISIBLE);
        btnAnswer3.setVisibility(v.INVISIBLE);
        btnAnswer4.setVisibility(v.INVISIBLE);
        btnAnswer1.setEnabled(false);
        btnAnswer2.setEnabled(false);
        btnAnswer3.setEnabled(false);
        btnAnswer4.setEnabled(false);
        btnAnswer1.setBackgroundColor(Color.rgb(187, 134, 252));
        btnAnswer2.setBackgroundColor(Color.rgb(187, 134, 252));
        btnAnswer3.setBackgroundColor(Color.rgb(187, 134, 252));
        btnAnswer4.setBackgroundColor(Color.rgb(187, 134, 252));
        btnAnswer1.setTextColor(Color.BLACK);
        btnAnswer2.setTextColor(Color.BLACK);
        btnAnswer3.setTextColor(Color.BLACK);
        btnAnswer4.setTextColor(Color.BLACK);
        //Checks if the user was in the middle of a game cycle when fragment view state updated and takes the data
        // from the saved instance if they were so that game progress is not lost
        if(savedInstanceState!=null){
            loadingSpinner.setVisibility(v.INVISIBLE);
            btnAnswer1.setVisibility(v.VISIBLE);
            btnAnswer2.setVisibility(v.VISIBLE);
            btnAnswer3.setVisibility(v.VISIBLE);
            btnAnswer4.setVisibility(v.VISIBLE);
            currentQuestion = savedInstanceState.getInt("CurrentQuestion");
            score = savedInstanceState.getInt("Score");
            questionList = savedInstanceState.getStringArrayList("QuestionList");
            correctAnswers = savedInstanceState.getStringArrayList("CorrectList");
            incorrectAnswers = savedInstanceState.getStringArrayList("IncorrectList");
            randomInt = savedInstanceState.getInt("RandomInt");
        // If the user wasn't already in a game data and elements reset so a new game can begin
        }else {
            questionList.clear();
            correctAnswers.clear();
            incorrectAnswers.clear();
            incorrectIncrementer = 0;
            currentQuestion = 0;
            score = 0;
        }
        // on click method call for Connection Error button
        v.findViewById(R.id.btn_ConnectionError).setOnClickListener(this);
        // If the user is not in progress of a game (nothing stored in saved instance) donwload data method is called so a new game can begin
        if(savedInstanceState==null){downloadData();}
        // New game data is not needed so game is reinstated using data previously downloaded and stored in save instance state.
        else{
            startGame();
        }

    }
    //Method to get data from the OpenTDB API, used for questions and answers for the game using Volley to make https request
    private void downloadData() {
        String url = "https://opentdb.com/api.php?amount=10&type=multiple";
        // Makes request to API to get JSON data
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    //When response is loaded into the app from API
                    public void onResponse(JSONObject response) {
                        loadingSpinner.setVisibility(v.INVISIBLE); //Loading spinner that is displayed when waiting for response from API
                        btnAnswer1.setVisibility(v.VISIBLE);
                        btnAnswer2.setVisibility(v.VISIBLE);
                        btnAnswer3.setVisibility(v.VISIBLE);
                        btnAnswer4.setVisibility(v.VISIBLE);
                        // Dealing with JSON response from API
                        try {
                            JSONArray resultsArray = response.getJSONArray("results");
                            // Each question and corresponding correct and incorrect answer in the response is stored
                            // within the JSON Arrray results so for each set of questions and answers the data is stored
                            // in global variables to be used within game cycle
                            for (int i = 0; i < resultsArray.length(); i++) {
                                JSONObject questionObject = resultsArray.getJSONObject(i);
                                String question = questionObject.getString("question");
                                questionList.add(question);
                                String correct = (questionObject.getString("correct_answer"));
                                correct = Html.fromHtml(correct).toString(); // OpenTdb uses HTML encoded string so needs to be decoded, solution influenced by "https://stackoverflow.com/questions/2918920/decode-html-entities-in-android"
                                correctAnswers.add(correct);
                                JSONArray test = questionObject.getJSONArray("incorrect_answers");
                                String incorrect1 = test.get(0).toString();
                                incorrectAnswers.add(incorrect1);
                                String incorrect2 = test.get(1).toString();
                                incorrectAnswers.add(incorrect2);
                                String incorrect3 = test.get(2).toString();
                                incorrectAnswers.add(incorrect3);
                            }

                            totalQuestion = questionList.size();
                            startGame();//Once data has been loaded game start method is called to begin game functionality
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    //Handles error if there is no connection to volley, waits 5 seconds sets the connectionFailure flag to true and tries to connect again if it still cant
                    //connect then an error message is displayed to the user to check their network connection and a button to try again which performs a callback on downloadData when clicked.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(!connectionFailure) {
                            Handler connectionHandler = new Handler();
                            connectionHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    connectionFailure = true;
                                    downloadData();
                                }
                            }, 5000);
                        }
                        else{
                            loadingSpinner.setVisibility(v.INVISIBLE);
                            v.findViewById(R.id.btn_ConnectionError).setVisibility(v.VISIBLE);
                            v.findViewById(R.id.btn_ConnectionError).setEnabled(true);
                            v.findViewById(R.id.tv_ConnectionError).setVisibility(v.VISIBLE);
                        }
                    }
                }
        );

        RequestQueue rq = Volley.newRequestQueue(getContext());
        rq.add(jsonObjectRequest);
    }

    // Used to store all users progress whilst in an active game
    @Override
    public void onSaveInstanceState(@NonNull Bundle outstate){
        super.onSaveInstanceState(outstate);
        outstate.putInt("CurrentQuestion",currentQuestion);
        outstate.putInt("Score",score);
        outstate.putStringArrayList("QuestionList",questionList);
        outstate.putStringArrayList("CorrectList",correctAnswers);
        outstate.putStringArrayList("IncorrectList",incorrectAnswers);
        outstate.putInt("RandomInt",randomInt);

    }
    //Method for game functionality and logic
    public void startGame() {
        //Checks if current Question counter exceeds question amount / game cycle has ended before loading new data to prevent crashes
        if (currentQuestion == 10) {
            //If the user has completed 10 questions they are navigated to the end game page
            // The users score and name is also passed to end game page for use there
            // The users score and name values are stored database
            NavController navController = Navigation.findNavController(v);
            TextView etUserNameEnd =(TextView) v.findViewById(R.id.tvPlayerName);
            String usernameEnd = etUserNameEnd.getText().toString();
            Integer scoreEnd = score;
            UserScores userScores = new UserScores();
            userScores.setUserScore(scoreEnd);
            userScores.setUsername(usernameEnd);
            UserScoresRepository repo = UserScoresRepository.getRepository(getContext());
            repo.storeUserScore(userScores);
            Bundle bundle = new Bundle();
            bundle.putString("usernameEnd", usernameEnd);
            bundle.putInt("scoreEnd", scoreEnd);
            navController.navigate(R.id.gamePlay_to_gameEnd,bundle);
            return; // So app does not crash by being stuck in the method
        }
        // Sets all question data and makes buttons clickable per question
        btnAnswer1 = v.findViewById(R.id.btnAnswer1);
        btnAnswer2 = v.findViewById(R.id.btnAnswer2);
        btnAnswer3 = v.findViewById(R.id.btnAnswer3);
        btnAnswer4 = v.findViewById(R.id.btnAnswer4);
        btnAnswer1.setEnabled(true);
        btnAnswer2.setEnabled(true);
        btnAnswer3.setEnabled(true);
        btnAnswer4.setEnabled(true);
        TextView tvQuestion = v.findViewById(R.id.tvQuestion);
        TextView tvQuestionNum = v.findViewById(R.id.tvQuestionNum);
        TextView tvScore = v.findViewById(R.id.tvScore);
        String question = Html.fromHtml(questionList.get(currentQuestion)).toString(); // OpenTdb uses HTML encoded string so needs to be decoded, solution influenced by "https://stackoverflow.com/questions/2918920/decode-html-entities-in-android"
        String questionNumber = "Question: " + (currentQuestion + 1) + "/10";
        String correct = (correctAnswers.get(currentQuestion));
        // OpenTdb uses HTML encoded string so needs to be decoded, solution influenced by "https://stackoverflow.com/questions/2918920/decode-html-entities-in-android"
        String incorrect1 = Html.fromHtml(incorrectAnswers.get(incorrectIncrementer)).toString();
        String incorrect2 = Html.fromHtml(incorrectAnswers.get(incorrectIncrementer + 1)).toString();
        String incorrect3 = Html.fromHtml(incorrectAnswers.get(incorrectIncrementer + 2)).toString();
        tvQuestionNum.setText(questionNumber);
        tvQuestion.setText(question);
        tvScore.setText( "Score: "+ (score));
        Random rand = new Random();
        //Randomly allocates order that correct and incorrect answers will go within the buttons
        randomInt = (rand.nextInt(4));
        switch (randomInt) {
            case 0:
                btnAnswer1.setText(correct);
                btnAnswer2.setText(incorrect1);
                btnAnswer3.setText(incorrect2);
                btnAnswer4.setText(incorrect3);
                break;
            case 1:
                btnAnswer1.setText(incorrect1);
                btnAnswer2.setText(correct);
                btnAnswer3.setText(incorrect2);
                btnAnswer4.setText(incorrect3);
                break;
            case 2:
                btnAnswer1.setText(incorrect1);
                btnAnswer2.setText(incorrect2);
                btnAnswer3.setText(correct);
                btnAnswer4.setText(incorrect3);
                break;
            case 3:
                btnAnswer1.setText(incorrect1);
                btnAnswer2.setText(incorrect2);
                btnAnswer3.setText(incorrect3);
                btnAnswer4.setText(correct);
                break;
        }
        // on click listeners for each of the buttons
        btnAnswer1.setOnClickListener(this);
        btnAnswer2.setOnClickListener(this);
        btnAnswer3.setOnClickListener(this);
        btnAnswer4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // If Connection error button is clicked an attempt to make https connection is made again
        if(view.getId()== R.id.btn_ConnectionError){
            v.findViewById(R.id.btn_ConnectionError).setVisibility(v.INVISIBLE);
            v.findViewById(R.id.btn_ConnectionError).setEnabled(false);
            v.findViewById(R.id.tv_ConnectionError).setVisibility(v.INVISIBLE);
            loadingSpinner.setVisibility(v.VISIBLE);
            downloadData();
            return;
        }
        // After a answer button is clicked all buts become unclickable until new data is loaded to prevent user causing out of bounds app crashes.
        btnAnswer1.setEnabled(false);
        btnAnswer2.setEnabled(false);
        btnAnswer3.setEnabled(false);
        btnAnswer4.setEnabled(false);
        // Handler used to create time delay so that the button which contains the correct answer for that question
        // briefly turns green to signify to the user what the correct answer was
        // time delay used to cause slight animation affect, code for delay influenced by "https://stackoverflow.com/questions/15874117/how-to-set-delay-in-android"
        Handler handler = new Handler();
        //Checks which button contained the correct answer for the question, if user clicked on the corresponding answer then their score is increased
        // and new set of question and answer set by startGame method otherwise score is not increased and new set of question and answer set by startGame method
        Button clickedButton = (Button) view;
            selectedAnswer = clickedButton.getText().toString();
            if (selectedAnswer.equals(correctAnswers.get(currentQuestion).toString())) {
                clickedButton.setBackgroundColor(Color.rgb(0, 255, 0));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clickedButton.setBackgroundColor(Color.rgb(187, 134, 252));
                        currentQuestion++;
                        incorrectIncrementer += 3;
                        score++;
                        startGame();
                    }
                }, 1000);
            }
            else if(btnAnswer1.getText().toString().equals(correctAnswers.get(currentQuestion).toString())) {
                btnAnswer1.setBackgroundColor(Color.rgb(0,255,0));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnAnswer1.setBackgroundColor(Color.rgb(187, 134, 252));
                        currentQuestion++;
                        incorrectIncrementer += 3;
                        startGame();
                    }
                }, 1000);
            }
            else if(btnAnswer2.getText().toString().equals(correctAnswers.get(currentQuestion).toString())) {
                btnAnswer2.setBackgroundColor(Color.rgb(0,255,0));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnAnswer2.setBackgroundColor(Color.rgb(187, 134, 252));
                        currentQuestion++;
                        incorrectIncrementer += 3;
                        startGame();
                    }
                }, 1000);
            }
            else if(btnAnswer3.getText().toString().equals(correctAnswers.get(currentQuestion).toString())) {
                btnAnswer3.setBackgroundColor(Color.rgb(0,255,0));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnAnswer3.setBackgroundColor(Color.rgb(187, 134, 252));
                        currentQuestion++;
                        incorrectIncrementer += 3;
                        startGame();
                    }
                }, 1000);
            }
            else if(btnAnswer4.getText().toString().equals(correctAnswers.get(currentQuestion).toString())) {
                btnAnswer4.setBackgroundColor(Color.rgb(0,255,0));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnAnswer4.setBackgroundColor(Color.rgb(187, 134, 252));
                        currentQuestion++;
                        incorrectIncrementer += 3;
                        startGame();
                    }
                }, 1000);
            }


    }}
