package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.databinding.ActivityMainBinding;
import com.codepath.nurivan.lostandfound.fragments.FoundFragment;
import com.codepath.nurivan.lostandfound.fragments.LostFragment;
import com.codepath.nurivan.lostandfound.fragments.ProfileFragment;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;

    private final Fragment lostFragment = new LostFragment();
    private final Fragment foundFragment = new FoundFragment();
    private final Fragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fragmentManager = getSupportFragmentManager();

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

        binding.bottomNavigation.setSelectedItemId(R.id.action_lost);

        Log.i(TAG, "Current User: " + ParseUser.getCurrentUser().getUsername());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(LostItem.class.getSimpleName())) {
            LostItem item = intent.getParcelableExtra(LostItem.class.getSimpleName());
            ((LostFragment) lostFragment).addLostItem(item);
            binding.bottomNavigation.setSelectedItemId(R.id.action_lost);
        } else if(intent.hasExtra(FoundItem.class.getSimpleName())) {
            FoundItem item = intent.getParcelableExtra(FoundItem.class.getSimpleName());
            ((FoundFragment) foundFragment).addFoundItem(item);
            binding.bottomNavigation.setSelectedItemId(R.id.action_found);
        }
    }
}