package com.codepath.nurivan.lostandfound.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.databinding.ActivityMainBinding;
import com.codepath.nurivan.lostandfound.fragments.FoundFragment;
import com.codepath.nurivan.lostandfound.fragments.LostFragment;
import com.codepath.nurivan.lostandfound.fragments.ProfileFragment;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;

    private final Fragment lostFragment = new LostFragment();
    private final Fragment foundFragment = new FoundFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment mapFragment = SupportMapFragment.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fragmentManager = getSupportFragmentManager();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment;

            int itemId = item.getItemId();

            if(itemId == R.id.action_lost) {
                fragment = lostFragment;
            } else if(itemId == R.id.action_found) {
                fragment = foundFragment;
            } else if(itemId == R.id.action_profile) {
                fragment = profileFragment;
            } else if(itemId == R.id.action_map) {
                fragment = mapFragment;
                setMapPins();
            } else {
                return true;
            }

            fragmentManager.beginTransaction().replace(R.id.fragment_holder, fragment).commit();

            return true;
        });

        binding.bottomNavigation.setSelectedItemId(R.id.action_lost);

        Log.i(TAG, "Current User: " + ParseUser.getCurrentUser().getUsername());
    }

    private LatLng calculateAverageLocation(List<Item> items) {
        double totalX = 0;
        double totalY = 0;
        double totalZ = 0;

        for(Item item : items) {
            double latitude = Math.toRadians(item.getItemLocation().getLatitude());
            double longitude = Math.toRadians(item.getItemLocation().getLongitude());
            totalX += Math.cos(latitude) * Math.cos(longitude);
            totalY += Math.cos(latitude) * Math.sin(longitude);
            totalZ += Math.sin(latitude);
        }
        double avgX = totalX / items.size();
        double avgY = totalY / items.size();
        double avgZ = totalZ / items.size();

        double longitude = Math.toDegrees(Math.atan2(avgY, avgX));
        double hypotenuse = Math.sqrt(avgX * avgX + avgY * avgY);
        double latitude = Math.toDegrees(Math.atan2(avgZ, hypotenuse));

        return new LatLng(latitude, longitude);
    }

    private void setMapPins() {
        ((SupportMapFragment) mapFragment).getMapAsync(googleMap -> {
            googleMap.clear();
            List<Item> allItems = new ArrayList<>();
            allItems.addAll(LostFragment.getItemList());
            allItems.addAll(FoundFragment.getItemList());
            for(Item item : allItems) {
                MarkerOptions options = new MarkerOptions();
                options.title(item.getItemName());
                options.snippet(Item.formatItemCoordinates(item.getItemLocation()));
                ParseGeoPoint location = item.getItemLocation();
                LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                options.position(point);
                if(item instanceof LostItem) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                } else if(item instanceof FoundItem) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                }


                googleMap.addMarker(options);
            }

            LatLng avgLocation = calculateAverageLocation(allItems);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(avgLocation, 2));
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(Item.class.getSimpleName())) {
            Item item = intent.getParcelableExtra(Item.class.getSimpleName());
            if(item instanceof LostItem) {
                ((LostFragment) lostFragment).addLostItem((LostItem) item);
                binding.bottomNavigation.setSelectedItemId(R.id.action_lost);
            } else if(item instanceof FoundItem) {
                ((FoundFragment) foundFragment).addFoundItem((FoundItem) item);
                binding.bottomNavigation.setSelectedItemId(R.id.action_found);
            }
        }
    }
}