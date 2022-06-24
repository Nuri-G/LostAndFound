package com.codepath.nurivan.lostandfound.models;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

public class Item extends ParseObject {
    public static final String KEY_ITEM_NAME = "itemName";
    public static final String KEY_ITEM_LOCATION = "itemLocation";
    public static final String KEY_MATCHES = "matches";

    public String getItemName() {
        return getString(KEY_ITEM_NAME);
    }
    public void setItemName(String name) {
        put(KEY_ITEM_NAME, name);
    }

    public ParseGeoPoint getItemLocation() {
        return getParseGeoPoint(KEY_ITEM_LOCATION);
    }

    public void setItemLocation(ParseGeoPoint itemLocation) {
        put(KEY_ITEM_LOCATION, itemLocation);
    }

    public JSONArray getMatches() {
        return getJSONArray(KEY_MATCHES);
    }

    public void addMatch(String itemId) {
        JSONArray matches = getMatches();
        matches.put(itemId);

        put(KEY_MATCHES, matches);
    }


}
