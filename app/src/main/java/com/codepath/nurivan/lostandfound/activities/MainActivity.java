package com.codepath.nurivan.lostandfound.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.databinding.ActivityMainBinding;
import com.codepath.nurivan.lostandfound.fragments.FoundFragment;
import com.codepath.nurivan.lostandfound.fragments.ItemMapFragment;
import com.codepath.nurivan.lostandfound.fragments.LostFragment;
import com.codepath.nurivan.lostandfound.fragments.ProfileFragment;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private FragmentManager fragmentManager;

    private final Fragment lostFragment = new LostFragment();
    private final Fragment foundFragment = new FoundFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment mapFragment = new ItemMapFragment((LostFragment) lostFragment, (FoundFragment) foundFragment);

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.codepath.nurivan.lostandfound.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fragmentManager = getSupportFragmentManager();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {


            int itemId = item.getItemId();

            if(itemId == R.id.action_lost) {
                currentFragment = lostFragment;
            } else if(itemId == R.id.action_found) {
                currentFragment = foundFragment;
            } else if(itemId == R.id.action_profile) {
                currentFragment = profileFragment;
            } else if(itemId == R.id.action_map) {
                currentFragment = mapFragment;
            } else {
                return true;
            }

            fragmentManager.beginTransaction().replace(R.id.fragment_holder, currentFragment).commit();

            return true;
        });

        if(currentFragment == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.action_lost);
        }

        Log.i(TAG, "Current User: " + ParseUser.getCurrentUser().getUsername());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(Item.class.getSimpleName())) {
            Item item = intent.getParcelableExtra(Item.class.getSimpleName());
            if(item instanceof LostItem) {
                ((LostFragment) lostFragment).addLostItem((LostItem) item);
            } else if(item instanceof FoundItem) {
                ((FoundFragment) foundFragment).addFoundItem((FoundItem) item);
            }
        }
    }
}