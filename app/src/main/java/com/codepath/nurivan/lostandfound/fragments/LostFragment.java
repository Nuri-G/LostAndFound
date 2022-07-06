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
import com.codepath.nurivan.lostandfound.databinding.FragmentLostBinding;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LostFragment extends Fragment {
    public static final String TAG = "LostFragment";

    private static final List<Item> items = new ArrayList<>();
    private static ParseUser lastUser = new ParseUser();

    private FragmentLostBinding binding;
    private ItemAdapter adapter;

    public LostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ItemAdapter(getContext(), items);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        binding.bAdd.setOnClickListener(v -> showItemNameActivity());
        binding.rvLostItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvLostItems.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemAdapter.SwipeHelper(adapter));
        itemTouchHelper.attachToRecyclerView(binding.rvLostItems);

        binding.swipeRefreshLost.setOnRefreshListener(this::getLostItems);
        if(items.isEmpty() || !lastUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            lastUser = ParseUser.getCurrentUser();
            getLostItems();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showItemNameActivity() {
        Item item = new LostItem();
        Intent i = new Intent(getActivity(), ItemNameActivity.class);
        i.putExtra(Item.class.getSimpleName(), item);

        startActivity(i);
    }

    private void getLostItems() {
        binding.swipeRefreshLost.setRefreshing(true);
        items.clear();
        ParseQuery<LostItem> query = ParseQuery.getQuery(LostItem.class);
        query.whereEqualTo(LostItem.KEY_LOST_BY, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.findInBackground((objects, e) -> {
            if(e != null) {
                Log.e(TAG, "Error getting lost items", e);
                return;
            }
            items.addAll(objects);
            adapter.notifyDataSetChanged();
            if(binding != null) {
                binding.swipeRefreshLost.setRefreshing(false);
            }
        });
    }

    public void addLostItem(LostItem item) {
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getObjectId().equals(item.getObjectId())) {
                items.set(i, item);
                adapter.notifyItemChanged(i);
                return;
            }
        }
        items.add(item);
        adapter.notifyItemInserted(items.size() - 1);
    }
}