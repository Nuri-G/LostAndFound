package com.codepath.nurivan.lostandfound.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

@ParseClassName("FoundItem")
public class FoundItem extends Item {
    public static final String KEY_FOUND_BY = "foundBy";
    //This will be the details used to generate the quiz like any colors or patterns the item has.
    public static final String KEY_ITEM_DETAILS = "itemDetails";
    public static final String KEY_TIME_FOUND = "timeFound";

    public ParseUser getFoundBy() {
        return getParseUser(KEY_FOUND_BY);
    }
    public void setFoundBy(ParseUser user) {
        put(KEY_FOUND_BY, user);
    }

    public JSONObject getItemDetails() {
        return getJSONObject(KEY_ITEM_DETAILS);
    }

    public void setItemDetails(JSONObject details) {
        put(KEY_ITEM_DETAILS, details);
    }

    public void setItemDetail(String key, String value) {
        JSONObject itemDetails = getItemDetails();
        if(itemDetails == null) {
            itemDetails = new JSONObject();
        }
        try {
            itemDetails.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        put(KEY_ITEM_DETAILS, itemDetails);
    }

    public void setTimeFound(Date timeFound) {
        put(KEY_TIME_FOUND, timeFound);
    }

    public Date getTimeFound() {
        return getDate(KEY_TIME_FOUND);
    }
}
