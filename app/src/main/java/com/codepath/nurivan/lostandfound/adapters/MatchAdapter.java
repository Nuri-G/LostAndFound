package com.codepath.nurivan.lostandfound.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.nurivan.lostandfound.databinding.MatchLayoutBinding;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.Match;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {
    public static final String TAG = "MatchAdapter";
    private final Context context;

    private List<Match> matches;
    private Item item;

    public MatchAdapter(Context context, Item item) {
        this.context = context;
        this.item = item;
        matches = new ArrayList<>();
    }

    public void loadMatches(Item item) {
        this.item = item;
        notifyItemRangeRemoved(0, matches.size());
        matches.clear();
        JSONArray matchesArray = item.getPossibleMatches();

        for(int i = 0; i < matchesArray.length(); i++) {
            try {
                String matchId = (String) matchesArray.get(i);
                Match match = new Match();
                match.setObjectId(matchId);
                match.fetchInBackground((object, e) -> {
                    if(e != null) {
                        Log.e(TAG, "Failed to fetch match", e);
                        return;
                    }
                    matches.add((Match) object);
                    notifyItemInserted(matches.size() - 1);
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public MatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MatchLayoutBinding binding = MatchLayoutBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MatchAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchAdapter.ViewHolder holder, int position) {
        Match match = matches.get(position);
        holder.bind(item, match);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final MatchLayoutBinding binding;

        public ViewHolder(MatchLayoutBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(Item item, Match match) {
            double distance = match.getDistanceMiles().doubleValue();
            double score = match.getMatchScore().doubleValue();

            String distanceString = String.format(Locale.US,"%.2f mi", distance);
            String scoreString = String.format(Locale.US, "%.2f%% match.", score * 100);

            binding.tvMatchDistance.setText(distanceString);
            binding.tvMatchScore.setText(scoreString);
        }
    }
}
