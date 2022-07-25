package com.codepath.nurivan.lostandfound.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.activities.ItemNameActivity;
import com.codepath.nurivan.lostandfound.adapters.ItemAdapter;
import com.codepath.nurivan.lostandfound.databinding.FragmentItemsBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

public class ItemFragment extends Fragment {
    public static final String TAG = "ItemFragment";

    private static final String EXTRA_TYPE = "itemType";
    private static final String EXTRA_LOADING = "loading";

    private final ArrayList<Item> items = new ArrayList<>();
    private static String lastUserId = "";

    private FragmentItemsBinding binding;
    private ItemAdapter adapter;
    private boolean loading;

    //LostItem or FoundItem
    private Class<? extends Item> itemType;

    public ItemFragment() {
        // Required empty public constructor
    }

    public static ItemFragment newInstance(Class<? extends Item> itemType) {
        Bundle bundle = new Bundle(2);
        bundle.putSerializable(EXTRA_TYPE, itemType);
        bundle.putBoolean(EXTRA_LOADING, true);
        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(bundle);
        fragment.itemType = itemType;
        fragment.loading = true;
        fragment.getItems(true, 0);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        loading = getArguments().getBoolean(EXTRA_LOADING);
        @SuppressWarnings("unchecked")
        Class<? extends Item> typeClass = (Class<? extends Item>) getArguments().getSerializable(EXTRA_TYPE);
        itemType = typeClass;
        adapter = new ItemAdapter(getContext(), items);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentItemsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        if(itemType.equals(FoundItem.class)) {
            binding.tvEmptyMessage.setText(R.string.found_items_empty);
        } else if(itemType.equals(LostItem.class)) {
            binding.tvEmptyMessage.setText(R.string.lost_items_empty);
        }
        binding.bAdd.setOnClickListener(v -> showItemNameActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvFoundItems.setLayoutManager(layoutManager);
        binding.rvFoundItems.setAdapter(adapter);
        binding.rvFoundItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if(!loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = true;
                            getItems(false, totalItemCount);
                        }
                    }
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemAdapter.SwipeHelper(adapter));
        itemTouchHelper.attachToRecyclerView(binding.rvFoundItems);

        binding.swipeRefreshFound.setOnRefreshListener(this::getItems);
        if(items.isEmpty()) {
            binding.tvEmptyMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.swipeRefreshFound.setRefreshing(false);

        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        if(!lastUserId.equals(currentUserId)) {
            lastUserId = currentUserId;
            getItems();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showItemNameActivity() {
        Item item;
        if(itemType.equals(LostItem.class)) {
            item = new LostItem();
        } else {
            item = new FoundItem();
        }

        Intent i = new Intent(getActivity(), ItemNameActivity.class);
        i.putExtra(Item.class.getSimpleName(), item);

        startActivity(i);
    }

    //Loads from cache if firstLoad is true.
    private void getItems(boolean firstLoad, int skip) {
        if(binding != null) {
            binding.swipeRefreshFound.setRefreshing(true);
        }
        ParseQuery<? extends Item> query = ParseQuery.getQuery(itemType);
        if(firstLoad) {
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
        } else {
            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        }
        if(itemType.equals(FoundItem.class)) {
            query.whereEqualTo(FoundItem.KEY_FOUND_BY, ParseUser.getCurrentUser());
        } else if(itemType.equals(LostItem.class)) {
            query.whereEqualTo(LostItem.KEY_LOST_BY, ParseUser.getCurrentUser());
        }

        query.setSkip(skip);
        int QUERY_LIMIT = 20;
        query.setLimit(QUERY_LIMIT);
        query.findInBackground((objects, e) -> {
            if(e != null) {
                if(!firstLoad) {
                    Log.e(TAG, "Error getting found items.", e);
                    Context context = getContext();
                    if(context != null) {
                        Toast.makeText(context, "Error getting items.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if(skip == 0){
                int size = items.size();
                items.clear();
                items.addAll(objects);
                if(adapter != null) {
                    adapter.notifyItemRangeRemoved(0, size);
                    adapter.notifyItemRangeInserted(0, items.size());
                }
            } else {
                int start = items.size();
                items.addAll(objects);
                if(adapter != null) {
                    adapter.notifyItemRangeInserted(start, objects.size());
                }
            }

            if(binding != null) {
                if(objects != null && objects.isEmpty()) {
                    binding.tvEmptyMessage.setVisibility(View.VISIBLE);
                } else {
                    binding.tvEmptyMessage.setVisibility(View.GONE);
                }
                binding.swipeRefreshFound.setRefreshing(false);
            }

            if(firstLoad) {
                getItems(false, 0);
            } else {
                loading = false;
            }
        });
    }

    private void getItems() {
        getItems(false, 0);
    }

    public void addItem(Item item) {
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
        if(binding != null) {
            binding.tvEmptyMessage.setVisibility(View.GONE);
        }
    }

    public ArrayList<Item> getItemList() {
        return items;
    }
}