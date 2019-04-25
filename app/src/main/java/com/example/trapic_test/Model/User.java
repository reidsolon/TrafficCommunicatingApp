package com.example.trapic_test.Model;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.sql.Date;

public class User {
    public String user_id = null;
    public String user_firstname = null;
    public String user_lastname = null;
    public String user_passWord = null;
    public String user_eMail = null;
    private String user_created = null;
    private String user_status;
    private String user_address;
    private boolean user_isOnline;
    private String user_lastOnline;
    private double user_lat;
    private double user_lng;
    private LatLng user_latLng;
    private String user_auth = "basic_auth";
    private String user_profile_state = "default";

    public User(){

    }

    public User(String id, String firstname, String lastname, String passWord, String eMail, String created, String account_status){
       this.user_id = id;
       this.user_firstname = firstname;
       this.user_lastname = lastname;
       this.user_passWord = passWord;
       this.user_status = account_status;
       this.user_created = created;
       this.user_eMail = eMail;
    }

    public String getUser_auth() {
        return user_auth;
    }

    public double getUser_lat() {
        return user_lat;
    }

    public double getUser_lng() {
        return user_lng;
    }

    public LatLng getUser_latLng() {
        return user_latLng;
    }

    public String getUser_lastOnline() {
        return user_lastOnline;
    }

    public String getDate() {
        return user_lastOnline;
    }

    public String getUser_address() {
        return user_address;
    }

    public boolean isUser_isOnline() {
        return user_isOnline;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_firstname() {
        return user_firstname;
    }

    public void setUser_firstname(String user_firstname) {
        this.user_firstname = user_firstname;
    }

    public String getUser_lastname() {
        return user_lastname;
    }

    public void setUser_lastname(String user_lastname) {
        this.user_lastname = user_lastname;
    }

    public String getUser_passWord() {
        return user_passWord;
    }

    public void setUser_passWord(String user_passWord) {
        this.user_passWord = user_passWord;
    }

    public String getUser_eMail() {
        return user_eMail;
    }

    public void setUser_eMail(String user_eMail) {
        this.user_eMail = user_eMail;
    }

    public String getUser_created() {
        return user_created;
    }

    public void setUser_created(String user_created) {
        this.user_created = user_created;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }
}
