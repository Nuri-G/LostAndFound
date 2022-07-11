package com.codepath.nurivan.lostandfound.activities;

import android.content.Intent;
import android.content.res.Configuration;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
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
    private GoogleMap googleMap;

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

    private void setMapPins() {
        googleMap.clear();
        List<Item> allItems = new ArrayList<>();
        allItems.addAll(((LostFragment) lostFragment).getItemList());
        allItems.addAll(((FoundFragment) foundFragment).getItemList());
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Item item : allItems) {
            MarkerOptions options = new MarkerOptions();
            options.title(item.getItemName());
            options.snippet(Item.formatItemCoordinates(item.getItemLocation()));
            ParseGeoPoint location = item.getItemLocation();
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            builder.include(point);
            options.position(point);
            if(item instanceof LostItem) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            } else if(item instanceof FoundItem) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }

            Marker m = googleMap.addMarker(options);
            markerItems.put(m, item);
        }
        LatLngBounds bounds = builder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
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

        if(currentFragment == mapFragment && googleMap != null) {
            setMapPins();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMaxZoomPreference(18);

        //Making the map dark if it is night mode
        int nightModeFlags = getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_night));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                break;
        }

        setMapPins();

        googleMap.setOnMarkerClickListener(marker -> {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
            marker.showInfoWindow();
            return true;
        });

        googleMap.setOnInfoWindowClickListener(marker -> {
            Intent i = new Intent(MainActivity.this, ItemDetailsActivity.class);
            i.putExtra(Item.class.getSimpleName(), markerItems.get(marker));
            startActivity(i);
        });
    }
}