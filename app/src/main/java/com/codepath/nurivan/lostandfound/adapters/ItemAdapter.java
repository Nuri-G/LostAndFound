package com.codepath.nurivan.lostandfound.adapters;

import static com.codepath.nurivan.lostandfound.models.Item.formatItemName;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.nurivan.lostandfound.activities.ItemDetailsActivity;
import com.codepath.nurivan.lostandfound.databinding.ItemLayoutBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    public static final String TAG = "ItemAdapter";
    private final Context context;
    private final List<Item> items;

    public ItemAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLayoutBinding binding = ItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item);
    }

    private void deleteItem(int index) {
        Item item = items.get(index);
        item.deleteInBackground(e -> {
            if(e != null) {
                Log.e(TAG, "Failed to delete item", e);
            }
        });
        items.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemLayoutBinding binding;
        private Item item;

        public ViewHolder(ItemLayoutBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(Item item) {
            this.item = item;
            binding.tvItemName.setText(formatItemName(item.getItemName()));

            binding.tvLocation.setText(item.getItemAddress());

            Date date = new Date();

            if (item instanceof LostItem) {
                date.setTime(((LostItem) item).getTimeLost().getTime());
            } else if (item instanceof FoundItem) {
                date.setTime(((FoundItem) item).getTimeFound().getTime());
            }
            binding.tvDate.setText(Item.formatItemDate(date));
            binding.getRoot().setOnClickListener(this);

            if(item.getPossibleMatches().length() > 0) {
                binding.ivMatched.setImageResource(android.R.drawable.presence_online);
            } else {
                binding.ivMatched.setImageResource(android.R.drawable.presence_offline);
            }
        }

        public Item getItem() {
            return item;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, ItemDetailsActivity.class);
            i.putExtra(Item.class.getSimpleName(), item);

            context.startActivity(i);
        }
    }

    public static class SwipeHelper extends ItemTouchHelper.SimpleCallback {
        private final ItemAdapter adapter;
        private final CoordinatorLayout layout;
        private boolean cancelDelete = false;

        public SwipeHelper(CoordinatorLayout layout, ItemAdapter adapter) {
            super(0, ItemTouchHelper.LEFT);
            this.adapter = adapter;
            this.layout = layout;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            Snackbar snackbar = Snackbar
                    .make(layout, "Item was deleted.", Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", view -> cancelDelete = true);

            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if(cancelDelete) {
                        adapter.notifyItemChanged(position);
                    } else {
                        adapter.deleteItem(position);
                    }

                    cancelDelete = false;
                }
            });
            snackbar.show();

        }
    }
}
