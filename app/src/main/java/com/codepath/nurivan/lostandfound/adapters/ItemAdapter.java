package com.codepath.nurivan.lostandfound.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.nurivan.lostandfound.activities.ItemDetailsActivity;
import com.codepath.nurivan.lostandfound.databinding.ItemLayoutBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;

import java.util.Date;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    Context context;
    List<Item> items;

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

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemLayoutBinding binding;
        Item item;
        public ViewHolder(ItemLayoutBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(Item item) {
            this.item = item;
            binding.tvItemName.setText(item.getItemName());

            double latitude = item.getItemLocation().getLatitude();
            double longitude = item.getItemLocation().getLongitude();

            String coordinates = "(" + latitude + ", " + longitude + ")";

            binding.tvCoordinates.setText(coordinates);

            Date date = new Date();

            if(item instanceof LostItem) {
                date.setTime(((LostItem) item).getTimeLost().getTime());
            } else if(item instanceof FoundItem) {
                date.setTime(((FoundItem) item).getTimeFound().getTime());
            }
            binding.tvDate.setText(date.toString());
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, ItemDetailsActivity.class);
            i.putExtra(Item.class.getSimpleName(), item);

            context.startActivity(i);
        }
    }
}
