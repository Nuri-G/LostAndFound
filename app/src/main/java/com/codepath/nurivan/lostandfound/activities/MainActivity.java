package com.codepath.nurivan.lostandfound.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "MainActivity";

    private FragmentManager fragmentManager;

    private final Fragment lostFragment = new LostFragment();
    private final Fragment foundFragment = new FoundFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment mapFragment = SupportMapFragment.newInstance();
    private final HashMap<Marker, Item> markerItems = new HashMap<>();

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.codepath.nurivan.lostandfound.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fragmentManager = getSupportFragmentManager();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {


            int itemId = item.getItemId();

            if(itemId == R.id.action_lost) {
                currentFragment = lostFragment;
            } else if(itemId == R.id.action_found) {
                currentFragment = foundFragment;
            } else if(itemId == R.id.action_profile) {
                currentFragment = profileFragment;
            } else if(itemId == R.id.action_map) {
                currentFragment = mapFragment;
                ((SupportMapFragment) mapFragment).getMapAsync(this);
            } else {
                return true;
            }

            fragmentManager.beginTransaction().replace(R.id.fragment_holder, currentFragment).commit();

            return true;
        });

        if(currentFragment == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.action_lost);
        }

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

    private void setMapPins(GoogleMap googleMap) {
        googleMap.clear();
        List<Item> allItems = new ArrayList<>();
        allItems.addAll(((LostFragment) lostFragment).getItemList());
        allItems.addAll(((FoundFragment) foundFragment).getItemList());
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

            Marker m = googleMap.addMarker(options);
            markerItems.put(m, item);
        }

        LatLng avgLocation = calculateAverageLocation(allItems);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(avgLocation, 2));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(Item.class.getSimpleName())) {
            Item item = intent.getParcelableExtra(Item.class.getSimpleName());
            if(item instanceof LostItem) {
                ((LostFragment) lostFragment).addLostItem((LostItem) item);
            } else if(item instanceof FoundItem) {
                ((FoundFragment) foundFragment).addFoundItem((FoundItem) item);
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        setMapPins(googleMap);
        googleMap.setOnInfoWindowClickListener(marker -> {
            Intent i = new Intent(MainActivity.this, ItemDetailsActivity.class);
            i.putExtra(Item.class.getSimpleName(), markerItems.get(marker));
            startActivity(i);
        });
    }
}