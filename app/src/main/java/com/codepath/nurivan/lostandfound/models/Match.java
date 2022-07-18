package com.codepath.nurivan.lostandfound.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.Objects;

@ParseClassName("Match")
public class Match extends ParseObject {
    public static final String KEY_LOST_ITEM = "lostItem";
    public static final String KEY_FOUND_ITEM = "foundItem";
    public static final String KEY_MATCH_SCORE = "matchScore";
    public static final String KEY_DISTANCE_MILES = "distanceMiles";
    public static final String KEY_VERIFIED = "verified";
    private static final String KEY_MEETING_PLACES = "meetingPlaces";

    public void getLostItem(FindCallback<Item> callback, boolean cached) {
        ParseQuery<Item> lostItemQuery = ParseQuery.getQuery(LostItem.class.getSimpleName());
        lostItemQuery.whereEqualTo("objectId", Objects.requireNonNull(getParseObject(KEY_LOST_ITEM)).getObjectId());
        if(cached) {
            lostItemQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
        } else {
            lostItemQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        }

        lostItemQuery.findInBackground(callback);
    }

    public void getFoundItem(FindCallback<Item> callback, boolean cached) {
        ParseQuery<Item> foundItemQuery = ParseQuery.getQuery(FoundItem.class.getSimpleName());
        foundItemQuery.whereEqualTo("objectId", Objects.requireNonNull(getParseObject(KEY_FOUND_ITEM)).getObjectId());
        if(cached) {
            foundItemQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
        } else {
            foundItemQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        }

        foundItemQuery.findInBackground(callback);
    }

    public Number getMatchScore() {
        return getNumber(KEY_MATCH_SCORE);
    }

    public Number getDistanceMiles() {
        return getNumber(KEY_DISTANCE_MILES);
    }

    public Boolean isVerified() {
        return getBoolean(KEY_VERIFIED);
    }

    public JSONArray getMeetingPlaces() {
        return getJSONArray(KEY_MEETING_PLACES);
    }
}
