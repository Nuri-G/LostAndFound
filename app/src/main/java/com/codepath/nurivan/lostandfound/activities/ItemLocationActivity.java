package com.codepath.nurivan.lostandfound.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.databinding.ActivityItemLocationBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

public class ItemLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    public static final String TAG = "ItemLocationActivity";
    private static final String LOST_QUESTION = "Where did you lose it?";
    private static final String FOUND_QUESTION = "Where did you find it?";
    private static final String LOST_BUTTON = "Find my item!";
    private static final String FOUND_BUTTON = "Next";

    private GoogleMap map;
    private Marker mapMarker;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityItemLocationBinding binding = ActivityItemLocationBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        item = getIntent().getParcelableExtra(Item.class.getSimpleName());

        if(item instanceof LostItem) {
            binding.tvLocation.setText(LOST_QUESTION);
            binding.bFind.setText(LOST_BUTTON);
        } else if(item instanceof FoundItem) {
            binding.tvLocation.setText(FOUND_QUESTION);
            binding.bFind.setText(FOUND_BUTTON);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fcvMap);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        binding.bFind.setOnClickListener(v -> updateItem(item));
    }

    private void updateItem(Item item) {
        if(mapMarker == null) {
            Toast.makeText(this, "Tap to place a map marker.", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLng markerPosition = mapMarker.getPosition();

        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(markerPosition.latitude, markerPosition.longitude);

        item.setItemLocation(parseGeoPoint);
        if(item instanceof LostItem) {
            item.saveInBackground(e -> {
                if(e != null) {
                    Log.e(TAG, "Failed to save item.", e);
                    Toast.makeText(this, "Failed to save item.", Toast.LENGTH_SHORT).show();
                    finish();
                }

                item.setPossibleMatches();

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(LostItem.class.getSimpleName(), item);
                startActivity(intent);
                finish();
            });
        } else if(item instanceof FoundItem) {
            showItemDetailsActivity((FoundItem) item);
        }
    }

    private void showItemDetailsActivity(FoundItem item) {
        Intent i = new Intent(this, ItemDescriptionActivity.class);
        i.putExtra(FoundItem.class.getSimpleName(), item);
        startActivity(i);
        finish();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);
        ParseGeoPoint location = item.getItemLocation();
        if(location != null) {
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            onMapClick(point);
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        MarkerOptions options = new MarkerOptions().snippet("test");
        options.position(point);
        if(mapMarker != null) {
            mapMarker.remove();
        }
        mapMarker = map.addMarker(options);
    }
}