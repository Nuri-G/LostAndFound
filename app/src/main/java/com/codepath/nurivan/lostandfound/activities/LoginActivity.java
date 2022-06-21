package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.nurivan.lostandfound.databinding.ActivityLoginBinding;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.bLogIn.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();

            loginUser(username, password);
        });
    }

    private void loginUser(String username, String password) {

        ParseUser.logOut();
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if(e != null) {
                Log.e(TAG, "Login issue", e);
                Toast.makeText(this, "Failed to log in.", Toast.LENGTH_SHORT).show();
                return;
            }


            goMainActivity();

            Toast.makeText(LoginActivity.this, "Logged in.", Toast.LENGTH_SHORT).show();
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}