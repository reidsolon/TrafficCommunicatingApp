package com.example.trapic_test.Model;

import com.google.android.gms.maps.model.LatLng;

public class EventList {

    private String name;
    private LatLng position;

    public EventList(){

    }


    public EventList(String name, LatLng position) {
        this.name = name;
        this.position = position;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}
