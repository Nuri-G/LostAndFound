package com.codepath.nurivan.lostandfound.activities;

import static com.codepath.nurivan.lostandfound.models.Item.formatItemName;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codepath.nurivan.lostandfound.adapters.MatchAdapter;
import com.codepath.nurivan.lostandfound.databinding.ActivityItemDetailsBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.codepath.nurivan.lostandfound.models.Match;
import com.parse.GetCallback;

import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Size;

public class ItemDetailsActivity extends AppCompatActivity implements DefaultLifecycleObserver {
    public static final String TAG = "ItemDetailsActivity";

    private static final String LOST_DATE_LABEL = "Lost On:";
    private static final String FOUND_DATE_LABEL = "Found On:";

    private ActivityResultLauncher<Intent> quizLauncher;

    private ActivityItemDetailsBinding binding;
    private MatchAdapter adapter;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItemDetailsBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        item = getIntent().getParcelableExtra(Item.class.getSimpleName());

        getLifecycle().addObserver(this);

        binding.bEditItem.setOnClickListener(v -> {
            Intent i = new Intent(this, ItemNameActivity.class);
            i.putExtra(Item.class.getSimpleName(), item);

            startActivity(i);
        });

        adapter = new MatchAdapter(this, item);
        binding.rvMatches.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMatches.setAdapter(adapter);
        binding.swipeRefreshMatches.setRefreshing(true);
        binding.swipeRefreshMatches.setOnRefreshListener(this::updateItemDetails);
        quizLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if(data != null && data.getBooleanExtra("passed", false)) {
                            showConfetti();
                        }
                    }
                });
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        displayItemDetails();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Item.class.getSimpleName(), item);
        startActivity(intent);
        finish();
    }

    private void showConfetti() {
        EmitterConfig emitterConfig = new Emitter(3, TimeUnit.SECONDS).perSecond(50);
        Party party = new PartyFactory(emitterConfig)
                .spread(90)
                .setSpeedBetween(1f, 5f)
                .timeToLive(2000L)
                .sizes(new Size(12, 5f, 0.2f))
                .position(0.0, 0.0, 1.0, 0.0)
                .build();
        binding.kvConfetti.start(party);
    }

    public void showOwnershipVerificationActivity(Match match) {
        Intent i = new Intent(this, OwnershipVerificationActivity.class);
        i.putExtra(Match.class.getSimpleName(), match);

        quizLauncher.launch(i);
    }

    private void updateItemDetails() {
        item.fetchInBackground((GetCallback<Item>) (item, e) -> {
            if(e != null) {
                Log.e(TAG, "Error fetching item.", e);
                Toast.makeText(ItemDetailsActivity.this, "Error fetching item.", Toast.LENGTH_SHORT).show();
            }
            if(binding != null) {
                displayItemDetails();
                binding.swipeRefreshMatches.setRefreshing(false);
            }
        });
    }

    private void displayItemDetails() {
        if(item instanceof LostItem) {
            LostItem lostItem = (LostItem) item;

            binding.tvItemDate.setText(Item.formatItemDate(lostItem.getTimeLost()));
            binding.tvItemDateLabel.setText(LOST_DATE_LABEL);
        } else if(item instanceof FoundItem) {
            FoundItem foundItem = (FoundItem) item;

            binding.tvItemDate.setText(Item.formatItemDate(foundItem.getTimeFound()));
            binding.tvItemDateLabel.setText(FOUND_DATE_LABEL);
        }

        binding.tvItemNameDetails.setText(formatItemName(item.getItemName()));
        binding.tvItemLocation.setText(item.getItemAddress());
        adapter.loadMatches(item);
        binding.swipeRefreshMatches.setRefreshing(false);
    }
}