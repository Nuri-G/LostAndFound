package com.codepath.nurivan.lostandfound.activities.lost_item;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.nurivan.lostandfound.databinding.ActivityLostItemLocationBinding;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.parse.ParseGeoPoint;

public class LostItemLocationActivity extends AppCompatActivity {
    public static final String TAG = "LostItemLocationActivity";

    ActivityLostItemLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLostItemLocationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        binding.bFind.setOnClickListener(v -> {
            double latitude = Double.parseDouble(binding.etLatitude.getText().toString());
            double longitude = Double.parseDouble(binding.etLongitude.getText().toString());

            LostItem lostItem = getIntent().getParcelableExtra(LostItem.class.getSimpleName());
            ParseGeoPoint parseGeoPoint = new ParseGeoPoint(latitude, longitude);
            lostItem.setLocation(parseGeoPoint);

            lostItem.saveInBackground(e -> {
                if(e != null) {
                    Log.e(TAG, "Failed to save item.", e);
                }
            });
            finish();
        });
    }
}