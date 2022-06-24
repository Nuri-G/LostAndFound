package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.codepath.nurivan.lostandfound.databinding.ActivityItemDetailsBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;

public class ItemDetailsActivity extends AppCompatActivity {
    private static final String LOST_DATE_LABEL = "Lost On:";
    private static final String FOUND_DATE_LABEL = "Found On:";

    ActivityItemDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItemDetailsBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        Item item = getIntent().getParcelableExtra(Item.class.getSimpleName());

        if(item instanceof LostItem) {
            LostItem lostItem = (LostItem) item;

            binding.tvItemDate.setText(lostItem.getTimeLost().toString());
            binding.tvItemDateLabel.setText(LOST_DATE_LABEL);
        } else if(item instanceof FoundItem) {
            FoundItem foundItem = (FoundItem) item;

            binding.tvItemDate.setText(foundItem.getTimeFound().toString());
            binding.tvItemDateLabel.setText(FOUND_DATE_LABEL);
        }

        binding.tvItemNameDetails.setText(item.getItemName());
        binding.tvItemLocation.setText(item.getItemLocation().toString());

        binding.bEditItem.setOnClickListener(v -> {
            Intent i = new Intent(this, ItemNameActivity.class);
            i.putExtra(Item.class.getSimpleName(), item);

            startActivity(i);
        });
    }
}