package com.codepath.nurivan.lostandfound.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.databinding.ActivityItemDescriptionBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;

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


        binding.bFindOwner.setOnClickListener(v -> {
            binding.clItemDescription.setVisibility(View.GONE);
            binding.clFindingItem.setVisibility(View.VISIBLE);
            item.saveInBackground(e -> {
                if(e != null) {
                    Log.e(TAG, "Failed to save FoundItem", e);
                    Toast.makeText(this, "Failed to save item.", Toast.LENGTH_SHORT).show();
                    finish();
                }

                item.setPossibleMatches();

                Intent intent = new Intent(this, ItemDetailsActivity.class);
                intent.putExtra(Item.class.getSimpleName(), item);
                startActivity(intent);
                finish();
            });
        });
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