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
    private String event_id;
    private double event_lat;
    private double event_lng;
    private String event_time;
    private String event_date;

    public Event(){

    }

    public Event(String caption, String type, String location, String image, String id, String event_id, String time, String date){
        this.event_caption = caption;
        this.event_type = type;
        this.event_location = location;
        this.event_image = image;
        this.user_id = id;
        this.event_id =event_id;
        this.event_time = time;
        this.event_date = date;
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

    public String getEvent_image() {
        return event_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public String getEvent_time() {
        return event_time;
    }

    public String getEvent_date() {
        return event_date;
    }
}
