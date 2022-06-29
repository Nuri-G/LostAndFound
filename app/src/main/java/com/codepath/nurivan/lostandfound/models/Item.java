package com.codepath.nurivan.lostandfound.models;

import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public abstract class Item extends ParseObject {
    public static final String TAG = "Item";

    public static final String KEY_ITEM_NAME = "itemName";
    public static final String KEY_ITEM_LOCATION = "itemLocation";
    public static final String KEY_POSSIBLE_MATCHES = "possibleMatches";
    public static final String KEY_CONFIRMED_MATCH = "confirmedMatches";

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

    public void setPossibleMatches(JSONArray matches) {
        put(KEY_POSSIBLE_MATCHES, matches);
    }

    public JSONArray getPossibleMatches() {
        return getJSONArray(KEY_POSSIBLE_MATCHES);
    }

    public Item getConfirmedMatch() {
        return (Item) getParseObject(KEY_CONFIRMED_MATCH);
    }

    public static String formatItemDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        return month + "/" + day + "/" + year;
    }

    public static String formatItemCoordinates(ParseGeoPoint point) {
        return "(" + point.getLatitude() + ", " + point.getLongitude() + ")";
    }

    public void setPossibleMatches() {
        HashMap<String, Object> params = new HashMap<>();
        if(this instanceof LostItem) {
            params.put("lostItemId", getObjectId());
        } else if(this instanceof FoundItem) {
            params.put("foundItemId", getObjectId());
        }
        ParseCloud.callFunctionInBackground("updateMatches", params, (FunctionCallback<Float>) (object, e) -> {
            if(e != null) {
                Log.e(TAG, "Failed to set matches: ", e);
            }
        });
    }
}
