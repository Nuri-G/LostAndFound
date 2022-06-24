package com.codepath.nurivan.lostandfound.models;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.json.JSONArray;

import java.time.temporal.ChronoUnit;
import java.util.Date;

public abstract class Item extends ParseObject {
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

    public void setMatches(JSONArray matches) {
        put(KEY_MATCHES, matches);
    }

    public JSONArray getMatches() {
        return getJSONArray(KEY_MATCHES);
    }

    public void addMatch(String itemId) {
        JSONArray matches = getMatches();
        matches.put(itemId);

        put(KEY_MATCHES, matches);
    }

    //Returns value from 0 to 1 depending on how close to 0 the distance is.
    private double getLocationSimilarity(Item other) {
        final double MAX_DISTANCE = 50.0;
        double distanceMiles = getItemLocation().distanceInMilesTo(other.getItemLocation());

        if(distanceMiles > MAX_DISTANCE) {
            return 0;
        } else {
            return (MAX_DISTANCE - distanceMiles) / MAX_DISTANCE;
        }
    }

    private double getNameSimilarity(Item other) {
        String itemName = getItemName();
        String otherName = other.getItemName();
        int longerLength = Math.max(itemName.length(), otherName.length());
        LevenshteinDistance nameSimilarity = new LevenshteinDistance();

        int similarity = nameSimilarity.apply(itemName, otherName);

        return (longerLength - similarity) / (double) longerLength;
    }

    private double getTimeSimilarity(Item other) {
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

        double nameSimilarity = getNameSimilarity(other);
        double locationSimilarity = getLocationSimilarity(other);
        double timeSimilarity = getTimeSimilarity(other);


        return NAME_SIMILARITY_WEIGHT * nameSimilarity + LOCATION_SIMILARITY_WEIGHT * locationSimilarity + TIME_SIMILARITY_WEIGHT * timeSimilarity;
    }


}
