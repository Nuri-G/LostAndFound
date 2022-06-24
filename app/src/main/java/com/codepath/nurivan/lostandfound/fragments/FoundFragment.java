package com.codepath.nurivan.lostandfound.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.nurivan.lostandfound.activities.ItemNameActivity;
import com.codepath.nurivan.lostandfound.adapters.ItemAdapter;
import com.codepath.nurivan.lostandfound.databinding.FragmentFoundBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FoundFragment extends Fragment {
    public static final String TAG = "FoundFragment";

    private FragmentFoundBinding binding;
    private ItemAdapter adapter;
    private List<Item> items;

    public FoundFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFoundBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        binding.bAdd.setOnClickListener(v -> showItemNameActivity());
        binding.rvFoundItems.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ItemAdapter(getContext(), items);
        binding.rvFoundItems.setAdapter(adapter);

        getFoundItems();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showItemNameActivity() {
        Item item = new FoundItem();
        Intent i = new Intent(getActivity(), ItemNameActivity.class);
        i.putExtra(Item.class.getSimpleName(), item);

        startActivity(i);
    }

    private void getFoundItems() {
        items.clear();
        ParseQuery<FoundItem> query = ParseQuery.getQuery(FoundItem.class);
        query.whereEqualTo(FoundItem.KEY_FOUND_BY, ParseUser.getCurrentUser());
        query.setLimit(20);
        binding.pbFoundItems.setVisibility(View.VISIBLE);
        query.findInBackground((objects, e) -> {
            if(e != null) {
                Log.e(TAG, "Error getting found items", e);
                return;
            }
            items.addAll(objects);
            adapter.notifyDataSetChanged();
            binding.pbFoundItems.setVisibility(View.GONE);
        });
    }
}