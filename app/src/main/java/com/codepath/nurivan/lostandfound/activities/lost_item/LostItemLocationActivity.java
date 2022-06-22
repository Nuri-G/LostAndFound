package com.codepath.nurivan.lostandfound.activities.lost_item;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.codepath.nurivan.lostandfound.databinding.ActivityLostItemLocationBinding;

public class LostItemLocationActivity extends AppCompatActivity {

    ActivityLostItemLocationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLostItemLocationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);
    }
}