package com.codepath.nurivan.lostandfound.models;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.json.JSONArray;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public abstract class Item extends ParseObject {
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

    //Returns value from 0 to 1 depending on how close to 0 the distance is.
    private double locationSimilarity(Item other) {
        final double MAX_DISTANCE = 50.0;
        double distanceMiles = getItemLocation().distanceInMilesTo(other.getItemLocation());

        if(distanceMiles > MAX_DISTANCE) {
            return 0;
        } else {
            return (MAX_DISTANCE - distanceMiles) / MAX_DISTANCE;
        }
    }

    private double nameSimilarity(Item other) {
        String itemName = getItemName().toLowerCase();
        String otherName = other.getItemName().toLowerCase();
        int longerLength = Math.max(itemName.length(), otherName.length());
        LevenshteinDistance nameSimilarity = new LevenshteinDistance();

        int similarity = nameSimilarity.apply(itemName, otherName);

        return (longerLength - similarity) / (double) longerLength;
    }

    private double timeSimilarity(Item other) {
        LostItem lostItem;
        FoundItem foundItem;
        if(this instanceof LostItem) {
            lostItem = (LostItem) this;
            foundItem = (FoundItem) other;
        } else {
            lostItem = (LostItem) other;
            foundItem = (FoundItem) this;
        }

        Date lostDate = lostItem.getTimeLost();
        Date foundDate = foundItem.getTimeFound();

        int daysBetween = (int) ChronoUnit.DAYS.between(lostDate.toInstant(), foundDate.toInstant());

        final int MAX_DAYS_AFTER = 14;
        final int MAX_DAYS_BEFORE = 1;

        if(daysBetween > -MAX_DAYS_BEFORE && daysBetween < MAX_DAYS_AFTER) {
            return (MAX_DAYS_AFTER - daysBetween) / (double) MAX_DAYS_AFTER;
        }
        return 0;
    }

    public double checkItemMatch(Item other) {
        final double NAME_SIMILARITY_WEIGHT = 0.4;
        final double LOCATION_SIMILARITY_WEIGHT = 0.3;
        final double TIME_SIMILARITY_WEIGHT = 0.3;

        double nameSimilarity = nameSimilarity(other);
        double locationSimilarity = locationSimilarity(other);
        double timeSimilarity = timeSimilarity(other);


        return NAME_SIMILARITY_WEIGHT * nameSimilarity + LOCATION_SIMILARITY_WEIGHT * locationSimilarity + TIME_SIMILARITY_WEIGHT * timeSimilarity;
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
}
