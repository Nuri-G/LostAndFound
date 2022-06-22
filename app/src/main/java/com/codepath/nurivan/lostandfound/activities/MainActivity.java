package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.databinding.ActivityMainBinding;
import com.codepath.nurivan.lostandfound.fragments.FoundFragment;
import com.codepath.nurivan.lostandfound.fragments.LostFragment;
import com.codepath.nurivan.lostandfound.fragments.ProfileFragment;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment lostFragment = new LostFragment();
        final Fragment foundFragment = new FoundFragment();
        final Fragment profileFragment = new ProfileFragment();



        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment;

            int itemId = item.getItemId();

            if(itemId == R.id.action_lost) {
                fragment = lostFragment;
            } else if(itemId == R.id.action_found) {
                fragment = foundFragment;
            } else if(itemId == R.id.action_profile) {
                fragment = profileFragment;
            } else {
                return true;
            }

            fragmentManager.beginTransaction().replace(R.id.fragment_holder, fragment).commit();

            return true;
        });

        Log.i(TAG, "Current User: " + ParseUser.getCurrentUser().getUsername());
    }
}