package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.databinding.ActivityItemDescriptionBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;

public class ItemDescriptionActivity extends AppCompatActivity {
    public static final String TAG = "ItemDescriptionActivity";

    ActivityItemDescriptionBinding binding;
    FoundItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItemDescriptionBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        item = getIntent().getParcelableExtra(FoundItem.class.getSimpleName());

        setUpSpinner(R.array.categories_array, "category", binding.sCategories);
        setUpSpinner(R.array.colors_array, "color", binding.sColors);
        setUpSpinner(R.array.patterns_array, "pattern", binding.sPatterns);
        setUpSpinner(R.array.sizes_array, "size", binding.sSizes);


        binding.bFindOwner.setOnClickListener(v -> item.saveInBackground(e -> {
            if(e != null) {
                Log.e(TAG, "Failed to save FoundItem", e);
            }
            finish();
        }));
    }

    private void setUpSpinner(int arrayId, String spinnerType, Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

                item.setItemDetail(spinnerType, selection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}