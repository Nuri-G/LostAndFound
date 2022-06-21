package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.codepath.nurivan.lostandfound.R;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseObject firstObject = new ParseObject("FirstClass");
        firstObject.put("message", "Hey ! First message from android. Parse is now connected");
        firstObject.saveInBackground(e -> {
            if (e != null) {
                Log.e("MainActivity", e.getLocalizedMessage());
            } else {
                Log.d("MainActivity", "Object saved.");
            }
        });

        Log.i(TAG, "Current User: " + ParseUser.getCurrentUser().getUsername());
    }
}