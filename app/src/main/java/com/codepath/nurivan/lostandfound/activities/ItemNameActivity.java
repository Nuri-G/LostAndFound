package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.codepath.nurivan.lostandfound.databinding.ActivityItemNameBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.parse.ParseUser;

public class ItemNameActivity extends AppCompatActivity {
    private static final String LOST_QUESTION = "What did you lose?";
    private static final String FOUND_QUESTION = "What did you find?";

    ActivityItemNameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItemNameBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        Item item = getIntent().getParcelableExtra(Item.class.getSimpleName());

        if(item instanceof LostItem) {
            ((LostItem) item).setLostBy(ParseUser.getCurrentUser());
            binding.tvName.setText(LOST_QUESTION);
        } else if(item instanceof FoundItem) {
            ((FoundItem) item).setFoundBy(ParseUser.getCurrentUser());
            binding.tvName.setText(FOUND_QUESTION);
        }

        if(item.getItemName() != null && item.getItemName().length() > 0) {
            binding.etItemName.setText(item.getItemName());
        }

        binding.bToTime.setOnClickListener(v -> {
            String name = binding.etItemName.getText().toString();
            name = name.trim();

            if(name.length() == 0) {
                Toast.makeText(this, "Please enter a name.", Toast.LENGTH_SHORT).show();
                return;
            }

            item.setItemName(name);

            showItemTimeActivity(item);
        });
    }

    private void showItemTimeActivity(Item item) {
        Intent i = new Intent(this, ItemTimeActivity.class);
        i.putExtra(Item.class.getSimpleName(), item);
        startActivity(i);
        finish();
    }
}