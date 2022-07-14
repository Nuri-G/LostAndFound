package com.codepath.nurivan.lostandfound.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.activities.ItemDetailsActivity;
import com.codepath.nurivan.lostandfound.databinding.FragmentItemMapBinding;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemMapFragment extends Fragment implements GoogleMap.OnMapClickListener, OnMapReadyCallback {
    private final HashMap<Marker, Item> markerItems = new HashMap<>();

    private Context context;
    private FragmentItemMapBinding binding;

    private final LostFragment lostFragment;
    private final FoundFragment foundFragment;
    private GoogleMap googleMap;
    private Marker lastClickMarker;

    public ItemMapFragment(LostFragment lostFragment, FoundFragment foundFragment) {
        this.lostFragment = lostFragment;
        this.foundFragment = foundFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentItemMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fcvMap);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setOnMapClickListener(this);
        googleMap.setMaxZoomPreference(18);

        //Making the map dark if it is night mode
        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_night));
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
            Intent i = new Intent(getContext(), ItemDetailsActivity.class);
            i.putExtra(Item.class.getSimpleName(), markerItems.get(marker));
            startActivity(i);
        });

        binding.svMap.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Optional<Map.Entry<Marker, Item>> itemSearch = markerItems.entrySet().stream().filter(markerItemEntry -> markerItemEntry.getValue().getItemName().equalsIgnoreCase(query)).findFirst();

                if(itemSearch.isPresent()) {
                    LatLng position = itemSearch.get().getKey().getPosition();

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 18));
                    return false;
                }


                String location = binding.svMap.getQuery().toString();
                location = location.trim();

                if(!location.isEmpty()) {
                    try {
                        Geocoder geocoder = new Geocoder(context);
                        List<Address> addressList = geocoder.getFromLocationName(location, 1);
                        if(addressList.isEmpty()) {
                            Toast.makeText(context, "Please enter a valid location.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        Address address = addressList.get(0);

                        LatLng point = new LatLng(address.getLatitude(), address.getLongitude());

                        onMapClick(point);

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 10));
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

    public void setMapPins() {
        googleMap.clear();
        List<Item> allItems = new ArrayList<>();
        allItems.addAll(lostFragment.getItemList());
        allItems.addAll(foundFragment.getItemList());
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

        if(!allItems.isEmpty()) {
            LatLngBounds bounds = builder.build();

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
        }
    }
}
