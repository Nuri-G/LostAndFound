package com.codepath.nurivan.lostandfound.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.boltsinternal.Task;

@ParseClassName("Match")
public class Match extends ParseObject {
    public static final String KEY_LOST_ITEM = "lostItem";
    public static final String KEY_FOUND_ITEM = "foundItem";
    public static final String KEY_MATCH_SCORE = "matchScore";
    public static final String KEY_DISTANCE_MILES = "distanceMiles";
    public static final String KEY_VERIFIED = "verified";

    public Task<LostItem> getLostItem() {
        ParseObject lostItem = getParseObject(KEY_LOST_ITEM);
        if(lostItem == null) {
            return null;
        }
        return lostItem.fetchInBackground();
    }

    public Task<FoundItem> getFoundItem() {
        ParseObject foundItem = getParseObject(KEY_FOUND_ITEM);
        if(foundItem == null) {
            return null;
        }
        return foundItem.fetchInBackground();
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
