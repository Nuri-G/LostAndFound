package com.codepath.nurivan.lostandfound.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.databinding.ActivitySignUpBinding;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "SignUpActivity";

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ParseUser user;
        if(ParseUser.getCurrentUser() != null) {
            user = ParseUser.getCurrentUser();
            binding.bSignUp.setText(R.string.update_profile);
            binding.etUsername.setText(user.getUsername());
            binding.etEmailAddress.setText(user.getEmail());
            binding.etHomeAddress.setText((String) user.get("homeAddress"));
            binding.etPassword.setHint("New Password (Leave empty for old)");
            binding.etConfirmPassword.setHint("Confirm New Password");

        } else {
            user = new ParseUser();
        }

        binding.bSignUp.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString().trim();
            String password = binding.etPassword.getText().toString();
            String emailAddress = binding.etEmailAddress.getText().toString().trim();
            String homeAddress = binding.etHomeAddress.getText().toString().trim();
            String confirmedPassword = binding.etConfirmPassword.getText().toString();

            signUpUser(user, username, emailAddress, homeAddress, password, confirmedPassword);
        });
    }

    private boolean isValidEmail(String emailAddress) {
        return !TextUtils.isEmpty(emailAddress) && Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    private boolean isValidLocation(String location) {
        location = location.trim();
        Geocoder geocoder = new Geocoder(this);

        if(!location.isEmpty()) {
            try {
                List<Address> addressList = geocoder.getFromLocationName(location, 1);
                if(addressList.size() == 1) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private boolean isValidDetails(String username, String emailAddress, String homeAddress, String password, String confirmedPassword) {
        if(username.length() < 5) {
            Toast.makeText(this, "Username must be at least 5 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(ParseUser.getCurrentUser() == null || !password.isEmpty() && !confirmedPassword.isEmpty()) {
            if(!confirmedPassword.equals(password)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return false;
            }

            if(password.length() < 8) {
                Toast.makeText(this, "Password must be more than 8 characters.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if(!isValidEmail(emailAddress)) {
            Toast.makeText(this, "Email address is not valid.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!isValidLocation(homeAddress)) {
            Toast.makeText(this, "Home location is not valid.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void signUpUser(ParseUser user, String username, String emailAddress, String homeAddress, String password, String confirmedPassword) {

        if(isValidDetails(username, emailAddress, homeAddress, password, confirmedPassword)) {
            if(!password.isEmpty()) {
                user.setPassword(password);
            }
            user.setUsername(username);
            user.setEmail(emailAddress);
            user.put("homeAddress", homeAddress);


            if(ParseUser.getCurrentUser() == null) {
                user.signUpInBackground(e -> {
                    if (e == null) {
                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                        installation.put("userId", ParseUser.getCurrentUser().getObjectId());
                        installation.saveInBackground();

                        Toast.makeText(SignUpActivity.this, "Created account.", Toast.LENGTH_SHORT).show();
                        showMainActivity();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to create account.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to sign up user", e);
                    }
                });
            } else {
                user.saveInBackground(e -> {
                    if (e == null) {
                        Toast.makeText(SignUpActivity.this, "Updated account.", Toast.LENGTH_SHORT).show();
                        showMainActivity();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to update account.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to update user", e);
                    }
                });
            }
        }
    }

    private void showMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}