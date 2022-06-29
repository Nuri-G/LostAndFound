package com.codepath.nurivan.lostandfound.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.nurivan.lostandfound.databinding.ActivityItemDetailsBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;

import java.util.HashMap;

public class ItemDetailsActivity extends AppCompatActivity implements DefaultLifecycleObserver {
    public static final String TAG = "ItemDetailsActivity";

    private static final String LOST_DATE_LABEL = "Lost On:";
    private static final String FOUND_DATE_LABEL = "Found On:";

    ActivityItemDetailsBinding binding;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItemDetailsBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        item = getIntent().getParcelableExtra(Item.class.getSimpleName());

        getLifecycle().addObserver(this);

        binding.bEditItem.setOnClickListener(v -> {
            Intent i = new Intent(this, ItemNameActivity.class);
            i.putExtra(Item.class.getSimpleName(), item);

            startActivity(i);
        });

        setPossibleMatches(item);
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        updateDetails();
    }



    private void updateDetails() {
        if(item instanceof LostItem) {
            LostItem lostItem = (LostItem) item;

            binding.tvItemDate.setText(Item.formatItemDate(lostItem.getTimeLost()));
            binding.tvItemDateLabel.setText(LOST_DATE_LABEL);
        } else if(item instanceof FoundItem) {
            FoundItem foundItem = (FoundItem) item;

            binding.tvItemDate.setText(Item.formatItemDate(foundItem.getTimeFound()));
            binding.tvItemDateLabel.setText(FOUND_DATE_LABEL);
        }

        binding.tvItemNameDetails.setText(item.getItemName());
        binding.tvItemLocation.setText(Item.formatItemCoordinates(item.getItemLocation()));
    }

    private void setPossibleMatches(Item item) {
        if(item instanceof LostItem) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("lostItemId", item.getObjectId());
            ParseCloud.callFunctionInBackground("updateMatches", params, (FunctionCallback<Float>) (object, e) -> {
                if(e != null) {
                    Log.e(TAG, "Failed to set matches: ", e);
                }
            });
        }
    }
}