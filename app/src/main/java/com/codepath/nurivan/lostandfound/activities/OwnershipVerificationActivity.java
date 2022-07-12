package com.codepath.nurivan.lostandfound.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.nurivan.lostandfound.R;
import com.codepath.nurivan.lostandfound.databinding.ActivityOwnershipVerificationBinding;
import com.codepath.nurivan.lostandfound.models.Match;
import com.parse.ParseCloud;

import java.util.HashMap;

public class OwnershipVerificationActivity extends AppCompatActivity {
    public static final String TAG = "OwnershipVerificationActivity";

    private HashMap<String, String> quizAnswers;
    private ActivityOwnershipVerificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnershipVerificationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Match match = getIntent().getParcelableExtra(Match.class.getSimpleName());

        quizAnswers = new HashMap<>();
        quizAnswers.put("matchId", match.getObjectId());

        setUpSpinner(R.array.categories_array, "category", binding.sCategories);
        setUpSpinner(R.array.colors_array, "color", binding.sColors);
        setUpSpinner(R.array.patterns_array, "pattern", binding.sPatterns);
        setUpSpinner(R.array.sizes_array, "size", binding.sSizes);

        binding.bSubmitQuiz.setOnClickListener(v -> {
            binding.bSubmitQuiz.setVisibility(View.GONE);
            binding.pbLoadingResults.setVisibility(View.VISIBLE);
            ParseCloud.callFunctionInBackground("submitQuiz", quizAnswers, (passed, e) -> {
                if(e != null) {
                    Toast.makeText(OwnershipVerificationActivity.this, "Failed to submit quiz.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error submitting quiz.", e);
                    finish();
                }
                if((Boolean) passed) {
                    Toast.makeText(getApplicationContext(), "Verification successful.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Verification failed.", Toast.LENGTH_SHORT).show();
                }
                binding.pbLoadingResults.setVisibility(View.GONE);
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

                quizAnswers.put(spinnerType, selection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}