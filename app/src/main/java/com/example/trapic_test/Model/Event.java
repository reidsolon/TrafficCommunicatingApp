package com.example.trapic_test.Model;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Event {
    private String event_caption;
    private String event_type;
    private String event_location;
    private String event_image;
    private String currentTime = Calendar.getInstance().getTime().toString();

    public Event(){

    }

    public Event(String caption, String type, String location, String image){
        this.event_caption = caption;
        this.event_type = type;
        this.event_location = location;
        this.event_image = image;

    }

    public String getEvent_caption() {
        return event_caption;
    }

    public String getEvent_location() {
        return event_location;
    }

    public String getEvent_type() {
        return event_type;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public String getEvent_image() {
        return event_image;
    }
}
