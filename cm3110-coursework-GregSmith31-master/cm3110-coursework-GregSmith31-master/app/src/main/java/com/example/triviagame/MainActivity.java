package com.example.triviagame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//      Sets a listener to the navigation bar used app wide to take the user back to the home page on their command
        BottomNavigationView bnv = findViewById(R.id.bottomNavigationView);
        bnv.setOnItemSelectedListener(this);
    }

//Method for taking user back to Home page of the app via bottom navigation if they are on any other page than the home page
// if they are on the Home page already nothing occurs.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(findViewById(R.id.fragmentContainerView));
        int currentFragmentId = navController.getCurrentDestination().getId();
        switch (item.getItemId()){
            case R.id.mi_BotttomNavHome:
                if (currentFragmentId != R.id.gameStartFragment){
                    navController.navigate(R.id.gameStartFragment);
                    return true;
                }
                break;
        }
        return false;
    }

}
