package com.codepath.nurivan.lostandfound.activities.lost_item;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.codepath.nurivan.lostandfound.databinding.ActivityLostItemNameBinding;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.parse.ParseUser;

public class LostItemNameActivity extends AppCompatActivity {

    ActivityLostItemNameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLostItemNameBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        binding.bNext.setOnClickListener(v -> {
            String name = binding.etItemName.getText().toString();
            name = name.trim();
            if(name.length() == 0) {
                Toast.makeText(this, "Please enter a name.", Toast.LENGTH_SHORT).show();
                return;
            }
            LostItem lostItem = new LostItem();
            lostItem.setItemName(name);
            lostItem.setLostBy(ParseUser.getCurrentUser());

            showLostItemLocationActivity(lostItem);
        });
    }

    private void showLostItemLocationActivity(LostItem lostItem) {
        Intent i = new Intent(this, LostItemLocationActivity.class);
        i.putExtra(LostItem.class.getSimpleName(), lostItem);
        startActivity(i);
        finish();
    }
}