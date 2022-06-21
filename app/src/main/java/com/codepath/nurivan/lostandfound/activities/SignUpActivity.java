package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.nurivan.lostandfound.databinding.ActivitySignUpBinding;
import com.parse.ParseUser;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "SignUpActivity";

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.bSignUp.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();
            String confirmedPassword = binding.etConfirmPassword.getText().toString();

            signUpUser(username, password, confirmedPassword);
        });
    }

    private void signUpUser(String username, String password, String confirmedPassword) {

        if(!confirmedPassword.equals(password)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 8) {
            Toast.makeText(this, "Password must be more than 8 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser.logOut();
        ParseUser user = new ParseUser();
        user.setPassword(password);
        user.setUsername(username);

        user.signUpInBackground(e -> {
            if (e == null) {
                Toast.makeText(SignUpActivity.this, "Created account.", Toast.LENGTH_SHORT).show();
                goMainActivity();
            } else {
                Toast.makeText(SignUpActivity.this, "Failed to make account.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}