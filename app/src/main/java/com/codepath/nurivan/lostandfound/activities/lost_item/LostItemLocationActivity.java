package com.codepath.nurivan.lostandfound.activities.lost_item;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

        LostItem lostItem = getIntent().getParcelableExtra(LostItem.class.getSimpleName());

        binding.bFind.setOnClickListener(v -> {
            String latitudeString = binding.etLatitude.getText().toString();
            String longitudeString = binding.etLongitude.getText().toString();
            uploadLostItem(lostItem, latitudeString, longitudeString);
        });
    }

    private void uploadLostItem(LostItem lostItem, String latitudeString, String longitudeString) {
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
        lostItem.setLocation(parseGeoPoint);

        lostItem.saveInBackground(e -> {
            if(e != null) {
                Log.e(TAG, "Failed to save item.", e);
            }
        });
        finish();
    }
}