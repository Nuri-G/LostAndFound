package com.codepath.nurivan.lostandfound.models;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public abstract class Item extends ParseObject {
    public static final String TAG = "Item";

    public static final String KEY_ITEM_NAME = "itemName";
    public static final String KEY_ITEM_LOCATION = "itemLocation";
    public static final String KEY_POSSIBLE_MATCHES = "possibleMatches";
    public static final String KEY_ITEM_ADDRESS = "itemAddress";

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

    public JSONArray getPossibleMatches() {
        return getJSONArray(KEY_POSSIBLE_MATCHES);
    }

    public void setItemAddress(String address) {
        put(KEY_ITEM_ADDRESS, address);
    }

    public String getItemAddress() {
        return getString(KEY_ITEM_ADDRESS);
    }

    public static String formatItemDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        return month + "/" + day + "/" + year;
    }

    public static String formatItemCoordinates(ParseGeoPoint point) {
        return "(" + String.format(Locale.US, "%.3f", point.getLatitude()) + ", " + String.format(Locale.US, "%.3f", point.getLongitude()) + ")";
    }

    public static String formatItemName(String name) {
        String shortName = String.format("%1.12s", name);
        if(name.length() > 12) {
            return shortName + "...";
        }
        return shortName;
    }

    public void setPossibleMatches(FunctionCallback<Object> callback) {
        HashMap<String, String> params = new HashMap<>();
        if(this instanceof LostItem) {
            params.put("lostItemId", getObjectId());
        } else if(this instanceof FoundItem) {
            params.put("foundItemId", getObjectId());
        }
        ParseCloud.callFunctionInBackground("updateMatches", params, callback);
    }
}
