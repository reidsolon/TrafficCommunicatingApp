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
    private String event_date_time;
    private double event_trust_rate;
    private long event_report_count;
    private String event_status;
    private long event_thank_count;
    private String event_closed_time = null;

    public Event(){

    }

    public Event(String status, String caption, String type, String location, String image, String id, String event_id, String time, String date, double lat, double lng, String date_time){
        this.event_caption = caption;
        this.event_type = type;
        this.event_location = location;
        this.event_image = image;
        this.user_id = id;
        this.event_id =event_id;
        this.event_time = time;
        this.event_date = date;
        this.event_lat = lat;
        this.event_lng = lng;
        this.event_date_time = date_time;
        this.event_status = status;
    }

    public String getEvent_closed_time() {
        return event_closed_time;
    }

    public String getEvent_status() {
        return event_status;
    }

    public long getEvent_report_count() {
        return event_report_count;
    }

    public long getEvent_thank_count() {
        return event_thank_count;
    }

    public double getEvent_trust_rate() {
        return event_trust_rate;
    }

    public String getEvent_caption() {
        return event_caption;
    }

    public void setEvent_caption(String event_caption) {
        this.event_caption = event_caption;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEvent_image() {
        return event_image;
    }

    public void setEvent_image(String event_image) {
        this.event_image = event_image;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public double getEvent_lat() {
        return event_lat;
    }

    public void setEvent_lat(double event_lat) {
        this.event_lat = event_lat;
    }

    public double getEvent_lng() {
        return event_lng;
    }

    public void setEvent_lng(double event_lng) {
        this.event_lng = event_lng;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_date_time() {
        return event_date_time;
    }
}
