package com.codepath.nurivan.lostandfound.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Meeting {
    private Item item;
    private String locationName;
    private String locationAddress;
    private Calendar meetingTime;
    private String emailAddress;

    public Item getItem() {
        return item;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public Calendar getMeetingTime() {
        return meetingTime;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public void setMeetingTime(Calendar meetingTime) {
        this.meetingTime = meetingTime;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    private String formatMeetingTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy hh:mma", Locale.US);
        dateFormat.setTimeZone(meetingTime.getTimeZone());
        return dateFormat.format(meetingTime.getTime());
    }

    public String makeEmailText() {
        return "Howdy!\n\nIt looks like we've been matched on Lost and Found for the " + item.getItemName()
                + ". I would like to meet outside " + locationName + " on " + formatMeetingTime() + " to exchange the item.\n\n The address is "
                + locationAddress + "\n\nThank you!";
    }
}
