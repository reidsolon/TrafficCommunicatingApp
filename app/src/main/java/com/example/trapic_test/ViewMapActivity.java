package com.example.trapic_test;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

public class ViewMapActivity extends Activity implements PermissionsListener {
    private MapView mapView;
    private MapboxMap mMap;
    private PermissionsManager permissionsManager;
    private double lat ,lng;
    private LocationComponent locationComponent;
    private Button myLoc, eventLoc;
    private String location, time;
    private LatLng latLng;
    private TextView eventLoc_txt,time_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(), "pk.eyJ1IjoicmVpZHNvbG9uIiwiYSI6ImNqcnZpZThzMTAyN2Ezemx4eHMzM2RoZGwifQ.j65VGpYO6g84DnR1koippQ");
        setContentView(R.layout.viewmap_layout);
        initViews();

        Intent intent = getIntent();

         lat = intent.getDoubleExtra("Lat", 0);
         lng = intent.getDoubleExtra("Lng", 0);
         time = intent.getStringExtra("Time");
         location = intent.getStringExtra("Location");

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mMap = mapboxMap;


                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {


                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Get an instance of the component
                        mMap.setMinZoomPreference(12);
                        initEventLocation();
                    }
                });

            }
        });

        myLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationComponent.setLocationComponentEnabled(true);
            }
        });

        eventLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition position = new CameraPosition.Builder().target(latLng).zoom(17).bearing(180).tilt(30).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
            }
        });

        eventLoc_txt.setText(location);
        time_txt.setText(" "+time);

    }

    public void initViews() {
        mapView = findViewById(R.id.viewMap);
        myLoc = findViewById(R.id.viewMap_myLoc);
        eventLoc = findViewById(R.id.viewMap_eventLoc);
        eventLoc_txt = findViewById(R.id.event_loc);
        time_txt = findViewById(R.id.timestamp_viewmap);
    }

    public void initEventLocation() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getApplicationContext())) {

            // Get an instance of the component
            locationComponent = mMap.getLocationComponent();

            // Activate with options
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationComponent.activateLocationComponent(getApplicationContext(), mMap.getStyle());

            // Enable to make component visible
//            locationComponent.setLocationComponentEnabled(true);


            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
            latLng = new LatLng(lat, lng);
            CameraPosition position = new CameraPosition.Builder().target(latLng).zoom(17).bearing(180).tilt(30).build();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.setPosition(latLng);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

            mMap.addMarker(markerOptions);

        } else {

            permissionsManager = new PermissionsManager(this);

            permissionsManager.requestLocationPermissions(this);

        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            initEventLocation();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
