package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.codepath.nurivan.lostandfound.databinding.ActivitySignUpBinding;
import com.parse.ParseInstallation;
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
            String emailAddress = binding.etEmailAddress.getText().toString();
            String homeAddress = binding.etHomeAddress.getText().toString();
            String confirmedPassword = binding.etConfirmPassword.getText().toString();

            signUpUser(username, emailAddress, homeAddress, password, confirmedPassword);
        });
    }

    private boolean isValidEmail(String emailAddress) {
        return !TextUtils.isEmpty(emailAddress) && Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    private void signUpUser(String username, String emailAddress, String homeAddress, String password, String confirmedPassword) {

        if(!confirmedPassword.equals(password)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 8) {
            Toast.makeText(this, "Password must be more than 8 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isValidEmail(emailAddress)) {
            Toast.makeText(this, "Email address is not valid.", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser.logOut();
        ParseUser user = new ParseUser();
        user.setPassword(password);
        user.setUsername(username);
        user.setEmail(emailAddress);


        // TODO - Need to verify that home address is a real place
        user.put("homeAddress", homeAddress);

        user.signUpInBackground(e -> {
            if (e == null) {
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("userId", ParseUser.getCurrentUser().getObjectId());
                installation.saveInBackground();

                Toast.makeText(SignUpActivity.this, "Created account.", Toast.LENGTH_SHORT).show();
                showMainActivity();
            } else {
                Toast.makeText(SignUpActivity.this, "Failed to make account.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to sign up user", e);
            }
        });
    }

    private void showMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}