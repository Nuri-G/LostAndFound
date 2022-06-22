package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.nurivan.lostandfound.databinding.ActivityLogInBinding;
import com.parse.ParseUser;

public class LogInActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private ActivityLogInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ParseUser.logOut();

        if(ParseUser.getCurrentUser() != null) {
            showMainActivity();
        }

        binding.bLogIn.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();

            logInUser(username, password);
        });

        binding.bSignUp.setOnClickListener(v -> showSignUpActivity());
    }

    private void logInUser(String username, String password) {

        ParseUser.logOut();
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if(e != null) {
                Log.e(TAG, "Login issue", e);
                Toast.makeText(this, "Failed to log in.", Toast.LENGTH_SHORT).show();
                return;
            }


            showMainActivity();

            Toast.makeText(LogInActivity.this, "Logged in.", Toast.LENGTH_SHORT).show();
        });
    }

    private void showSignUpActivity() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }

    private void showMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}