package com.codepath.nurivan.lostandfound.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FoundFragment extends Fragment {
    public static final String TAG = "FoundFragment";

    private final List<Item> items = new ArrayList<>();
    private static String lastUserId = "";

    private FragmentFoundBinding binding;
    private ItemAdapter adapter;

    public FoundFragment() {
        getFoundItems();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ItemAdapter(getContext(), items);
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
        binding.rvFoundItems.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemAdapter.SwipeHelper(binding.getRoot(), adapter));
        itemTouchHelper.attachToRecyclerView(binding.rvFoundItems);

        binding.swipeRefreshFound.setOnRefreshListener(this::getFoundItems);
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.swipeRefreshFound.setRefreshing(false);

        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        if(items.isEmpty() || !lastUserId.equals(currentUserId)) {
            lastUserId = currentUserId;
            getFoundItems();
        }
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
        if(binding != null) {
            binding.swipeRefreshFound.setRefreshing(true);
        }
        ParseQuery<FoundItem> query = ParseQuery.getQuery(FoundItem.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.whereEqualTo(FoundItem.KEY_FOUND_BY, ParseUser.getCurrentUser());
        query.findInBackground((objects, e) -> {
            if(e != null) {
                Log.e(TAG, "Error getting found items", e);
            } else {
                items.clear();
                items.addAll(objects);
            }
            if(binding != null) {
                binding.swipeRefreshFound.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void addFoundItem(FoundItem item) {
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getObjectId().equals(item.getObjectId())) {
                items.set(i, item);
                if(adapter != null) {
                    adapter.notifyItemChanged(i);
                }
                return;
            }
        }
        items.add(item);
        if(adapter != null) {
            adapter.notifyItemInserted(items.size() - 1);
        }
    }

    public List<Item> getItemList() {
        return items;
    }
}