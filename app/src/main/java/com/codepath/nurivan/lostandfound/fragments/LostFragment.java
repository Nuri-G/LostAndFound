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
import com.codepath.nurivan.lostandfound.databinding.FragmentLostBinding;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LostFragment extends Fragment {
    public static final String TAG = "LostFragment";

    private final List<Item> items = new ArrayList<>();
    private static String lastUserId = "";

    private FragmentLostBinding binding;
    private ItemAdapter adapter;
    private boolean loading;

    public LostFragment() {
        loading = true;
        getLostItems(true, 0);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvLostItems.setLayoutManager(layoutManager);
        binding.rvLostItems.setAdapter(adapter);
        binding.rvLostItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if(!loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = true;
                            getLostItems(false, totalItemCount);
                        }
                    }
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemAdapter.SwipeHelper(binding.getRoot(), adapter));
        itemTouchHelper.attachToRecyclerView(binding.rvLostItems);

        binding.swipeRefreshLost.setOnRefreshListener(this::getLostItems);
        if(items.isEmpty()) {
            binding.tvEmptyMessageLost.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.swipeRefreshLost.setRefreshing(false);

        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        if(!lastUserId.equals(currentUserId)) {
            lastUserId = currentUserId;
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

    //Loads from cache if firstLoad is true.
    private void getLostItems(boolean firstLoad, int skip) {
        if(binding != null) {
            binding.swipeRefreshLost.setRefreshing(true);
        }
        ParseQuery<LostItem> query = ParseQuery.getQuery(LostItem.class);
        if(firstLoad) {
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
        } else {
            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        }
        query.whereEqualTo(LostItem.KEY_LOST_BY, ParseUser.getCurrentUser());
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
                    binding.tvEmptyMessageLost.setVisibility(View.VISIBLE);
                } else {
                    binding.tvEmptyMessageLost.setVisibility(View.GONE);
                }
                binding.swipeRefreshLost.setRefreshing(false);
            }

            if(firstLoad) {
                getLostItems(false, 0);
            } else {
                loading = false;
            }
        });
    }

    private void getLostItems() {
        getLostItems(false, 0);
    }

    public void addLostItem(LostItem item) {
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
            binding.tvEmptyMessageLost.setVisibility(View.GONE);
        }
    }

    public List<Item> getItemList() {
        return items;
    }
}