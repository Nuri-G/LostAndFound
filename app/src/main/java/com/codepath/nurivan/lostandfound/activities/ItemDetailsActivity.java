package com.codepath.nurivan.lostandfound.activities;

import static com.codepath.nurivan.lostandfound.models.Item.formatItemName;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codepath.nurivan.lostandfound.adapters.MatchAdapter;
import com.codepath.nurivan.lostandfound.databinding.ActivityItemDetailsBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.parse.ParseQuery;

public class ItemDetailsActivity extends AppCompatActivity implements DefaultLifecycleObserver {
    public static final String TAG = "ItemDetailsActivity";

    private static final String LOST_DATE_LABEL = "Lost On:";
    private static final String FOUND_DATE_LABEL = "Found On:";

    ActivityItemDetailsBinding binding;
    private MatchAdapter adapter;
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

        adapter = new MatchAdapter(this, item);
        binding.rvMatches.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMatches.setAdapter(adapter);
        binding.swipeRefreshMatches.setRefreshing(true);
        binding.swipeRefreshMatches.setOnRefreshListener(this::updateItemDetails);
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        updateItemDetails();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Item.class.getSimpleName(), item);
        startActivity(intent);
        finish();
    }

    private void updateItemDetails() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class.getSimpleName());
        query.whereEqualTo("objectId", item.getObjectId());
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground((objects, e) -> {
            if(e != null) {
                Log.e(TAG, "Error fetching item.", e);
            } else {
                if(objects.size() > 0) {
                    item = objects.get(0);
                }
                displayItemDetails();
            }
            if(binding != null) {
                binding.swipeRefreshMatches.setRefreshing(false);
            }
        });
    }

    private void displayItemDetails() {
        if(item instanceof LostItem) {
            LostItem lostItem = (LostItem) item;

            binding.tvItemDate.setText(Item.formatItemDate(lostItem.getTimeLost()));
            binding.tvItemDateLabel.setText(LOST_DATE_LABEL);
        } else if(item instanceof FoundItem) {
            FoundItem foundItem = (FoundItem) item;

            binding.tvItemDate.setText(Item.formatItemDate(foundItem.getTimeFound()));
            binding.tvItemDateLabel.setText(FOUND_DATE_LABEL);
        }

        binding.tvItemNameDetails.setText(formatItemName(item.getItemName()));
        binding.tvItemLocation.setText(item.getItemAddress());
        adapter.loadMatches(item);
    }
}