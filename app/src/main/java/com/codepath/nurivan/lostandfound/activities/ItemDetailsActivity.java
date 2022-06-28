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
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

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

        findAndSetMatches();
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

    private void findAndSetMatches() {
        List<Item> otherItems = new ArrayList<>();

        final int QUERY_LIMIT = 20;

        if(item instanceof LostItem) {
            ParseQuery<FoundItem> query = ParseQuery.getQuery(FoundItem.class);
            query.whereNotEqualTo(FoundItem.KEY_FOUND_BY, ParseUser.getCurrentUser());
            query.setLimit(QUERY_LIMIT);
            query.findInBackground((objects, e) -> {
                if(e != null) {
                    Log.e(TAG, "Error getting lost items", e);
                    return;
                }
                otherItems.addAll(objects);
                setPossibleMatches(item, otherItems);
            });
        } else if(item instanceof FoundItem) {
            ParseQuery<LostItem> query = ParseQuery.getQuery(LostItem.class);
            query.whereNotEqualTo(LostItem.KEY_LOST_BY, ParseUser.getCurrentUser());
            query.setLimit(QUERY_LIMIT);
            query.findInBackground((objects, e) -> {
                if(e != null) {
                    Log.e(TAG, "Error getting lost items", e);
                    return;
                }
                otherItems.addAll(objects);
                setPossibleMatches(item, otherItems);
            });
        }


    }

    private void setPossibleMatches(Item item, List<Item> otherItems) {
        JSONArray matches = item.getPossibleMatches();
        for(Item other : otherItems) {
            double matchAmount = item.checkItemMatch(other);

            Log.i(TAG, "Match amount: " + matchAmount + ", Item: " + item.getItemName() + ", Other Item: " + other.getItemName());

            if(matchAmount >= 0.7) {
                matches.put(other.getObjectId());
            }
        }

        item.setPossibleMatches(matches);
        item.saveInBackground(e -> {
            if(e != null) {
                Log.e(TAG, "Failed to save matches.", e);
            }
        });
    }
}