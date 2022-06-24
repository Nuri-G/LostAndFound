package com.codepath.nurivan.lostandfound.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("LostItem")
public class LostItem extends Item {

    public static final String KEY_LOST_BY = "lostBy";
    public static final String KEY_TIME_LOST = "timeLost";

    public ParseUser getLostBy() {
        return getParseUser(KEY_LOST_BY);
    }
    public void setLostBy(ParseUser user) {
        put(KEY_LOST_BY, user);
    }

    public void setTimeLost(Date timeLost) {
        put(KEY_TIME_LOST, timeLost);
    }

    public Date getTimeLost() {
        return getDate(KEY_TIME_LOST);
    }
    
}
