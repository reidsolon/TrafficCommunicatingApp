package com.example.trapic_test;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SelectEvent extends AppCompatActivity {

    LinearLayout cat1, cat2, cat3, cat4;
    Geocoder geocoder;
    private double lat, lng;
    private LocationRequest locationRequest;
    private List<Address> addresses;
    private String my_address;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.selectevent_layout);

        initViews();
        onClicks();
        callPermission();
    }

    public void initViews() {
        cat1 = (LinearLayout) findViewById(R.id.cat_1);
        cat2 = (LinearLayout) findViewById(R.id.cat_2);
        cat3 = (LinearLayout) findViewById(R.id.cat_3);
        cat4 = (LinearLayout) findViewById(R.id.cat_4);
    }

    public void onClicks() {
        cat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("Category", "Construction Area");
                intent.putExtra("Lat", lat);
                intent.putExtra("Lng", lng);
                intent.putExtra("Address", my_address);
                startActivity(intent);
            }
        });

        cat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("Category", "Congestion");
                intent.putExtra("Lat", lat);
                intent.putExtra("Lng", lng);
                intent.putExtra("Address", my_address);
                startActivity(intent);
            }
        });

        cat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("Category", "Traffic Jams");
                intent.putExtra("Lat", lat);
                intent.putExtra("Lng", lng);
                intent.putExtra("Address", my_address);
            }
        });

        cat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("Category", "Road Crash");
                intent.putExtra("Lat", lat);
                intent.putExtra("Lng", lng);
                intent.putExtra("Address", my_address);
                startActivity(intent);
            }
        });
    }
    public void callPermission(){
        Permissions.check(this/*context*/, Manifest.permission.ACCESS_FINE_LOCATION, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                requestLocation();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                callPermission();
            }
        });
    }
    public void requestLocation(){

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callPermission();
        } else {FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(4000);
            locationRequest.setFastestInterval(2000);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    lat = locationResult.getLastLocation().getLatitude();
                    lng = locationResult.getLastLocation().getLongitude();
                    Log.e("Lat", "Lat: "+lat+" Lng:"+lng);
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), 1);
                        my_address = addresses.get(0).getThoroughfare()+" "+addresses.get(0).getLocality()+" "+addresses.get(0).getAdminArea();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, getMainLooper());
        }


    }



}
