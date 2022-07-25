package com.codepath.nurivan.lostandfound.adapters;

import static com.codepath.nurivan.lostandfound.models.Item.formatItemName;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.activities.ItemDetailsActivity;
import com.codepath.nurivan.lostandfound.databinding.ItemLayoutBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;

import java.util.Date;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

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

    private void beforeDelete(int index) {
        items.remove(index);
        notifyItemRemoved(index);
    }

    private void cancelDelete(Item item, int index) {
        items.add(index, item);
        notifyItemInserted(index);
    }

    private void deleteItem(Item item) {
        item.deleteInBackground(e -> {
            if(e != null) {
                Log.e(TAG, "Failed to delete item", e);
                return;
            }
            Toast.makeText(context, "Item deleted.", Toast.LENGTH_SHORT).show();
        });
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

        public SwipeHelper(ItemAdapter adapter) {
            super(0, ItemTouchHelper.LEFT);
            this.adapter = adapter;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getBindingAdapterPosition();

            Item item = adapter.items.get(position);
            adapter.beforeDelete(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.context);
            builder.setTitle("Delete item?");
            builder.setMessage("Once deleted, the item cannot be recovered.");
            builder.setNegativeButton("Cancel", (dialog, which) -> adapter.cancelDelete(item, position));
            builder.setPositiveButton("Confirm", (dialog, which) -> adapter.deleteItem(item));

            builder.create().show();

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX/4, dY, actionState, isCurrentlyActive);
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_forever_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(adapter.context, R.color.deleteColor))
                    .create()
                    .decorate();
        }
    }
}
