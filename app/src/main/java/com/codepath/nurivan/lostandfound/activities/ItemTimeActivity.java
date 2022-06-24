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

        if(item instanceof LostItem) {
            binding.tvTime.setText(LOST_QUESTION);
        } else if(item instanceof FoundItem) {
            binding.tvTime.setText(FOUND_QUESTION);
        }

        binding.bToLocation.setOnClickListener(v -> {
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

    private void showItemLocationActivity(Item item) {
        Intent i = new Intent(this, ItemLocationActivity.class);
        i.putExtra(Item.class.getSimpleName(), item);
        startActivity(i);
        finish();
    }
}