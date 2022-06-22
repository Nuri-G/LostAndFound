package com.codepath.nurivan.lostandfound.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("LostItem")
public class LostItem extends Item {
    public static final String KEY_LOST_BY = "lostBy";

    public ParseUser getLostBy() {
        return getParseUser(KEY_LOST_BY);
    }
    public void setLostBy(ParseUser user) {
        put(KEY_LOST_BY, user);
    }
}
