package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.nurivan.lostandfound.databinding.ActivityItemLocationBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.parse.ParseGeoPoint;

public class ItemLocationActivity extends AppCompatActivity {
    public static final String TAG = "ItemLocationActivity";
    private static final String LOST_QUESTION = "Where did you lose it?";
    private static final String FOUND_QUESTION = "Where did you find it?";
    private static final String LOST_BUTTON = "Find my item!";
    private static final String FOUND_BUTTON = "Next";

    ActivityItemLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItemLocationBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        Item item = getIntent().getParcelableExtra(Item.class.getSimpleName());

        if(item instanceof LostItem) {
            binding.tvLocation.setText(LOST_QUESTION);
            binding.bFind.setText(LOST_BUTTON);
        } else if(item instanceof FoundItem) {
            binding.tvLocation.setText(FOUND_QUESTION);
            binding.bFind.setText(FOUND_BUTTON);
        }


        binding.bFind.setOnClickListener(v -> {
            String latitudeString = binding.etLatitude.getText().toString();
            String longitudeString = binding.etLongitude.getText().toString();
            updateItem(item, latitudeString, longitudeString);
        });
    }

    private void updateItem(Item item, String latitudeString, String longitudeString) {
        if(latitudeString.length() == 0 || longitudeString.length() == 0) {
            Toast.makeText(this, "Please enter a value for latitude and longitude.", Toast.LENGTH_SHORT).show();
            return;
        }

        double latitude = Double.parseDouble(latitudeString);
        double longitude = Double.parseDouble(longitudeString);

        if(latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            Toast.makeText(this, "Please enter valid coordinates.", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(latitude, longitude);
        item.setItemLocation(parseGeoPoint);

        if(item instanceof LostItem) {
            item.saveInBackground(e -> {
                if(e != null) {
                    Log.e(TAG, "Failed to save item.", e);
                }
            });
            finish();
        } else if(item instanceof FoundItem) {
            showItemDetailsActivity((FoundItem) item);
        }
    }

    private void showItemDetailsActivity(FoundItem item) {
        Intent i = new Intent(this, ItemDetailsActivity.class);
        i.putExtra(FoundItem.class.getSimpleName(), item);
        startActivity(i);
        finish();
    }
}