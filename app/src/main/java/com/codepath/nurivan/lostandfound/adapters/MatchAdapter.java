package com.codepath.nurivan.lostandfound.adapters;

import static com.codepath.nurivan.lostandfound.models.Item.formatItemName;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.nurivan.lostandfound.activities.ItemDetailsActivity;
import com.codepath.nurivan.lostandfound.databinding.MatchLayoutBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.codepath.nurivan.lostandfound.models.Match;
import com.codepath.nurivan.lostandfound.models.Meeting;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {
    public static final String TAG = "MatchAdapter";
    private final Context context;

    private final List<Match> matches;
    private Item item;

    public MatchAdapter(Context context, Item item) {
        this.context = context;
        this.item = item;
        matches = new ArrayList<>();
    }

    public void loadMatches(Item item) {
        this.item = item;
        JSONArray matchesArray = item.getPossibleMatches();
        List<String> matchIds = new ArrayList<>();
        for(int i = 0; i < matchesArray.length(); i++) {
            try {
                matchIds.add(matchesArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ParseQuery<Match> matchQuery = ParseQuery.getQuery(Match.class.getSimpleName());
        matchQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        matchQuery.whereContainedIn("objectId", matchIds);
        matchQuery.findInBackground((objects, e) -> {
            if(e != null) {
                if(e.getCode() != ParseException.CACHE_MISS) {
                    Log.e(TAG, "Error fetching matches", e);
                    Toast.makeText(context, "Error fetching matches.", Toast.LENGTH_SHORT).show();
                }
            } else {
                int size = matches.size();
                matches.clear();
                notifyItemRangeRemoved(0, size);
                matches.addAll(objects);
                notifyItemRangeInserted(0, matches.size());
            }
        });
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
        holder.bind(match);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final MatchLayoutBinding binding;

        private Match match;

        public ViewHolder(MatchLayoutBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(Match match) {
            this.match = match;

            FindCallback<Item> callback = (items, e) -> {
                if(e != null) {
                    if (e.getCode() != ParseException.CACHE_MISS) {
                        Log.e(TAG, "Error binding match.", e);
                        Toast.makeText(context, "Error binding match.", Toast.LENGTH_SHORT).show();
                    }

                } else if(items.size() > 0) {
                    Item otherItem = items.get(0);

                    binding.tvOtherItemName.setText(formatItemName(otherItem.getItemName()));
                    binding.tvCity.setText(otherItem.getItemAddress());
                }
            };

            if(item instanceof LostItem) {
                match.getFoundItem(callback);
            } else if(item instanceof FoundItem) {
                match.getLostItem(callback);
            }
            double distance = match.getDistanceMiles().doubleValue();
            double score = match.getMatchScore().doubleValue();

            String distanceString = String.format(Locale.US,"%.2f mi", distance);
            String scoreString = String.format(Locale.US, "%.2f%% match", score * 100);

            binding.tvMatchDistance.setText(distanceString);
            binding.tvMatchScore.setText(scoreString);

            if(match.isVerified()) {
                binding.bVerify.setVisibility(View.GONE);
                binding.tvVerification.setVisibility(View.GONE);
                binding.bEmail.setVisibility(View.VISIBLE);
                binding.bEmail.setOnClickListener(v -> {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("matchId", match.getObjectId());
                    ParseCloud.callFunctionInBackground("getEmail", params, (FunctionCallback<String>) (emailAddress, e) -> {
                        if(e != null) {
                            Toast.makeText(context, "Failed to get email address.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            Meeting meeting = new Meeting();
                            meeting.setEmailAddress(emailAddress);
                            meeting.setItem(item);
                            showChooseMeetingPlace(meeting);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    });
                });
            } else if(item instanceof FoundItem) {
                binding.bVerify.setVisibility(View.GONE);
                binding.tvVerification.setVisibility(View.VISIBLE);
            } else {
                binding.bVerify.setOnClickListener(v -> {
                    if(context instanceof ItemDetailsActivity) {
                        ((ItemDetailsActivity) context).showOwnershipVerificationActivity(match);
                    }
                });
            }
        }

        private void showChooseMeetingPlace(Meeting meeting) throws JSONException {
            JSONArray meetingPlaces = match.getMeetingPlaces();
            HashMap<String, String> nameAddressMap = new HashMap<>();
            for(int i = 0; i < meetingPlaces.length(); i++) {
                JSONObject meetingPlace = meetingPlaces.getJSONObject(i);
                nameAddressMap.put(meetingPlace.getString("locationName"), meetingPlace.getString("address"));
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Pick a meeting place");
            String[] items = nameAddressMap.keySet().toArray(new String[0]);
            builder.setItems(items, (dialog, which) -> {
                meeting.setLocationName(items[which]);
                meeting.setLocationAddress(nameAddressMap.get(items[which]));
                showChooseMeetingDate(meeting);
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void showChooseMeetingDate(Meeting meeting) {
            DatePickerDialog datePicker = new DatePickerDialog(context);
            datePicker.setTitle("Pick a meeting date");
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis());

            datePicker.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                Calendar meetingDate = Calendar.getInstance();
                meetingDate.set(year, month, dayOfMonth);
                meeting.setMeetingTime(meetingDate);

                showChooseMeetingTime(meeting);
            });

            datePicker.show();
        }

        private void showChooseMeetingTime(Meeting meeting) {
            TimePickerDialog.OnTimeSetListener listener = (view, hourOfDay, minute) -> {
                Calendar meetingTime = meeting.getMeetingTime();
                meetingTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                meetingTime.set(Calendar.MINUTE, minute);

                try {
                    showSendEmailActivity(meeting);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            };
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, listener, 0, 0, false);
            timePickerDialog.setTitle("Pick a meeting time");

            timePickerDialog.show();
        }

        private void showSendEmailActivity(Meeting meeting) throws JSONException, IOException {
            File f = new File(context.getCacheDir(), "meeting.ics");
            FileOutputStream fileOutputStream = new FileOutputStream(f);

            net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
            calendar.getProperties().add(Version.VERSION_2_0);
            calendar.getProperties().add(CalScale.GREGORIAN);

            VEvent meetingEvent = new VEvent(new DateTime(meeting.getMeetingTime().getTime()), "Item exchange");
            meetingEvent.getProperties().add(new Location(meeting.getLocationAddress()));
            calendar.getComponents().add(meetingEvent);

            CalendarOutputter calendarOutputter = new CalendarOutputter();
            calendarOutputter.output(calendar, fileOutputStream);
            fileOutputStream.close();
            Uri path = FileProvider.getUriForFile(context, "com.codepath.fileprovider", f);

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            String emailString = meeting.makeEmailText();
            emailIntent.putExtra(Intent.EXTRA_STREAM, path);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ meeting.getEmailAddress() });
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Lost and Found Item Exchange");
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailString);

            emailIntent.setType("message/rfc822");
            context.startActivity(Intent.createChooser(emailIntent, "Choose an Email client"));
        }
    }
}
