package com.codepath.nurivan.lostandfound.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import org.json.JSONObject;

@ParseClassName("FoundItem")
public class FoundItem extends Item {
    public static final String KEY_FOUND_BY = "foundBy";
    //This will be the details used to generate the quiz like any colors or patterns the item has.
    public static final String KEY_ITEM_DETAILS = "itemDetails";

    public ParseUser getFoundBy() {
        return getParseUser(KEY_FOUND_BY);
    }
    public void setFoundBy(ParseUser user) {
        put(KEY_FOUND_BY, user);
    }

    public JSONObject getItemDetails() {
        return getJSONObject(KEY_ITEM_DETAILS);
    }

    public void setItemDetail(JSONObject details) {
        put(KEY_ITEM_DETAILS, details);
    }
}
