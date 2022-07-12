package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.codepath.nurivan.lostandfound.databinding.ActivityItemLocationBinding;
import com.codepath.nurivan.lostandfound.databinding.ActivityItemTimeBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;

import java.util.Calendar;
import java.util.Date;

public class ItemTimeActivity extends AppCompatActivity {
    private static final String LOST_QUESTION = "When did you lose it?";
    private static final String FOUND_QUESTION = "When did you find it?";

    ActivityItemTimeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItemTimeBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        Item item = getIntent().getParcelableExtra(Item.class.getSimpleName());

        Date date = new Date();

        if(item instanceof LostItem) {
            LostItem lostItem = (LostItem) item;
            binding.tvTime.setText(LOST_QUESTION);
            if(lostItem.getTimeLost() != null) {
                date = lostItem.getTimeLost();
            }
        } else if(item instanceof FoundItem) {
            FoundItem foundItem = (FoundItem) item;
            binding.tvTime.setText(FOUND_QUESTION);
            if(foundItem.getTimeFound() != null) {
                date = foundItem.getTimeFound();
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        binding.dpItemTime.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        binding.dpItemTime.setMaxDate(System.currentTimeMillis());

        binding.bToLocation.setOnClickListener(v -> {
            setItemDate(item);

            showItemLocationActivity(item);
        });
    }

    public static Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    private void setItemDate(Item item) {
        Date date = getDateFromDatePicker(binding.dpItemTime);
        Date current = new Date();


        if(date.after(current)) {
            Toast.makeText(this, "Please enter a date on or before today.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(item instanceof LostItem) {
            ((LostItem) item).setTimeLost(date);
        } else if(item instanceof FoundItem) {
            ((FoundItem) item).setTimeFound(date);
        }
    }

    private void showItemLocationActivity(Item item) {
        Intent i = new Intent(this, ItemLocationActivity.class);
        i.putExtra(Item.class.getSimpleName(), item);
        startActivity(i);
        finish();
    }
}