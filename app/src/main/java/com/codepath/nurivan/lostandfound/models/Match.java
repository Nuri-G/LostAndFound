package com.codepath.nurivan.lostandfound.models;

import com.parse.ParseObject;


public class Match extends ParseObject {
    public static final String KEY_LOST_ITEM = "lostItem";
    public static final String KEY_FOUND_ITEM = "foundItem";
    public static final String KEY_MATCH_SCORE = "matchScore";

    public LostItem getLostItem() {
        return (LostItem) getParseObject(KEY_LOST_ITEM);
    }

    public FoundItem getFoundItem() {
        return (FoundItem) getParseObject(KEY_FOUND_ITEM);
    }

    public Number getMatchScore() {
        return getNumber(KEY_MATCH_SCORE);
    }
}
