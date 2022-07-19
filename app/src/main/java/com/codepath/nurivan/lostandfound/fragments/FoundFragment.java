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

import com.codepath.nurivan.lostandfound.activities.ItemNameActivity;
import com.codepath.nurivan.lostandfound.adapters.ItemAdapter;
import com.codepath.nurivan.lostandfound.databinding.FragmentFoundBinding;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.FoundItem;
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
    private boolean loading;

    public FoundFragment() {
        loading = true;
        getFoundItems(true, 0);
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
                            getFoundItems(false, totalItemCount);
                        }
                    }
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemAdapter.SwipeHelper(binding.getRoot(), adapter));
        itemTouchHelper.attachToRecyclerView(binding.rvFoundItems);

        binding.swipeRefreshFound.setOnRefreshListener(this::getFoundItems);
        if(items.isEmpty()) {
            binding.tvEmptyMessageFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.swipeRefreshFound.setRefreshing(false);

        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        if(!lastUserId.equals(currentUserId)) {
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

    //Loads from cache if firstLoad is true.
    private void getFoundItems(boolean firstLoad, int skip) {
        if(binding != null) {
            binding.swipeRefreshFound.setRefreshing(true);
        }
        ParseQuery<FoundItem> query = ParseQuery.getQuery(FoundItem.class);
        if(firstLoad) {
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
        } else {
            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        }
        query.whereEqualTo(FoundItem.KEY_FOUND_BY, ParseUser.getCurrentUser());
        query.setSkip(skip);
        int QUERY_LIMIT = 20;
        query.setLimit(QUERY_LIMIT);
        query.findInBackground((objects, e) -> {
            if(e != null) {
                if(!firstLoad) {
                    Log.e(TAG, "Error getting lost items.", e);
                    Context context = getContext();
                    if(context != null) {
                        Toast.makeText(context, "Error getting lost items.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if(skip == 0){
                int size = adapter.getItemCount();
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
                    binding.tvEmptyMessageFound.setVisibility(View.VISIBLE);
                } else {
                    binding.tvEmptyMessageFound.setVisibility(View.GONE);
                }
                binding.swipeRefreshFound.setRefreshing(false);
            }

            if(firstLoad) {
                getFoundItems(false, 0);
            } else {
                loading = false;
            }
        });
    }

    private void getFoundItems() {
        getFoundItems(false, 0);
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
        if(binding != null) {
            binding.tvEmptyMessageFound.setVisibility(View.GONE);
        }
    }

    public List<Item> getItemList() {
        return items;
    }
}