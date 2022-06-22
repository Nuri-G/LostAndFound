package com.codepath.nurivan.lostandfound.models;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class Item extends ParseObject {
    public static final String KEY_ITEM_NAME = "name";
    public static final String KEY_ITEM_LOCATION = "itemLocation";

    public String getItemName() {
        return getString(KEY_ITEM_NAME);
    }
    public void setItemName(String user) {
        put(KEY_ITEM_NAME, user);
    }

    public ParseGeoPoint getItemLocation() {
        return getParseGeoPoint(KEY_ITEM_LOCATION);
    }

    public void setLocation(ParseGeoPoint itemLocation) {
        put(KEY_ITEM_LOCATION, itemLocation);
    }


}
