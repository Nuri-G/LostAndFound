package com.codepath.nurivan.lostandfound.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.databinding.ActivityItemLocationBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

import java.io.IOException;
import java.util.List;

public class ItemLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    public static final String TAG = "ItemLocationActivity";
    private static final String LOST_QUESTION = "Where did you lose it?";
    private static final String FOUND_QUESTION = "Where did you find it?";
    private static final String LOST_BUTTON = "Find my item!";
    private static final String FOUND_BUTTON = "Next";

    private ActivityItemLocationBinding binding;

    private GoogleMap map;
    private Marker mapMarker;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItemLocationBinding.inflate(getLayoutInflater());

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
            CameraUpdate newPosition = CameraUpdateFactory.newCameraPosition(new CameraPosition(point, 4, 0, 0));
            map.moveCamera(newPosition);
            onMapClick(point);
        }

        binding.svMap.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = binding.svMap.getQuery().toString();
                location = location.trim();

                System.out.println(location);

                if(!location.isEmpty()) {
                    Geocoder geocoder = new Geocoder(ItemLocationActivity.this);

                    try {
                        List<Address> addressList = geocoder.getFromLocationName(location, 1);
                        if(addressList.isEmpty()) {
                            Toast.makeText(ItemLocationActivity.this, "Please enter a valid location.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        Address address = addressList.get(0);

                        LatLng point = new LatLng(address.getLatitude(), address.getLongitude());

                        onMapClick(point);

                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 10));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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