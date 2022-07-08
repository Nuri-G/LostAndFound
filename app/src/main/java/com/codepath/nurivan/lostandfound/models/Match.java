package com.codepath.nurivan.lostandfound.models;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Match")
public class Match extends ParseObject {
    public static final String KEY_LOST_ITEM = "lostItem";
    public static final String KEY_FOUND_ITEM = "foundItem";
    public static final String KEY_MATCH_SCORE = "matchScore";
    public static final String KEY_DISTANCE_MILES = "distanceMiles";
    public static final String KEY_VERIFIED = "verified";

    public void getLostItem(GetCallback<Item> callback) {
        ParseObject lostItem = getParseObject(KEY_LOST_ITEM);

        assert lostItem != null;
        lostItem.fetchInBackground(callback);
    }

    public void getFoundItem(GetCallback<Item> callback) {
        ParseObject foundItem = getParseObject(KEY_FOUND_ITEM);

        assert foundItem != null;
        foundItem.fetchInBackground(callback);
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
}
