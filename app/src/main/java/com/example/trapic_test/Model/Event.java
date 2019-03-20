package com.example.trapic_test.Model;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Event {
    private String event_caption;
    private String event_type;
    private String event_location;
    private String user_id;
    private String event_image;
    private double event_lat;
    private double event_lng;
    private String currentTime = Calendar.getInstance().getTime().toString();

    public Event(){

    }

    public Event(String caption, String type, String location, String image, String id){
        this.event_caption = caption;
        this.event_type = type;
        this.event_location = location;
        this.event_image = image;
        this.user_id = id;

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

    public String getUser_id() {
        return user_id;
    }
}
