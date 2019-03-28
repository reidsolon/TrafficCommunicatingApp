package com.example.trapic_test.MainFragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.trapic_test.Model.Event;
import com.example.trapic_test.Model.User;
import com.example.trapic_test.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
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
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements PermissionsListener {

    private MapboxMap mMap;
    private MapView mapView;
    private LocationRequest locationRequest;
    private String[] list;
    private double lat, lng;
    private LatLng latLng1;
    private Event event;
    private Button myLocBtn, myLocBtn2;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private CameraPosition cameraPosition;
    private MarkerOptions markerOptions;
    private String event_user_id, event_user_fullname;

    public MapFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), "pk.eyJ1IjoicmVpZHNvbG9uIiwiYSI6ImNqcnZpZThzMTAyN2Ezemx4eHMzM2RoZGwifQ.j65VGpYO6g84DnR1koippQ");
        return inflater.inflate(R.layout.map_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);
        myLocBtn = view.findViewById(R.id.my_loc);
        myLocBtn2 = view.findViewById(R.id.my_loc2);

        myLocBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateLocation();
            }
        });
        myLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list = new String[]{"Night Mode Traffic", "Day Mode Traffic", "Normal Streets"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select Map Type");
                builder.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            mMap.setStyle(Style.TRAFFIC_NIGHT, new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    enableLocationComponent();
                                }
                            });
                        } else if (which == 1) {
                            mMap.setStyle(Style.TRAFFIC_DAY, new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    enableLocationComponent();
                                }
                            });
                        } else {
                            mMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    enableLocationComponent();
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mMap = mapboxMap;
                myLocation();

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {


                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Get an instance of the component
                        myLocation();
                        loadAllMarkers();
                        enableLocationComponent();
                        mMap.setMinZoomPreference(12);
                    }
                });

            }
        });
    }

    private void animateLocation(){

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent() {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

            // Get an instance of the component
            locationComponent = mMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(getContext(), mMap.getStyle());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);


            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            // setting up my location
            cameraPosition = new CameraPosition.Builder().target(latLng1).zoom(17).bearing(180).tilt(30).build();
        } else {

            permissionsManager = new PermissionsManager(this);

            permissionsManager.requestLocationPermissions(getActivity());

        }
    }

    public void loadAllMarkers() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    event = dataSnapshot1.getValue(Event.class);

                    event_user_id = event.getUser_id();
                    markerOptions = new MarkerOptions();
                    if(latLng1 != null){

                        double distance = latLng1.distanceTo(new LatLng(event.getEvent_lat(), event.getEvent_lng()));
                        final double roundedDistance = Math.round(distance * 100.0) / 100.0;
                        final double roundedKm;
                        if(roundedDistance > 1000.0){
                            roundedKm = Math.round((roundedDistance / 100.0) * 100.0)/100.0;
                            markerOptions.setTitle(event.getEvent_type() + ": " + event.getEvent_location()+" "+roundedKm+" km away from you");
                        }else{
                            markerOptions.setTitle(event.getEvent_type() + ": " + event.getEvent_location()+" "+roundedDistance+" m away from you");
                        }

                        IconFactory iconFactory = IconFactory.getInstance(getActivity());
                        Icon construction_marker = iconFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_construction_marker));
                        Icon roadcrash_marker = iconFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_road_crash));
                        Icon trafficjam_marker = iconFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_traffic_jam));


                        switch(event.getEvent_type()){
                            case "Congestion":{
                                markerOptions.icon(trafficjam_marker);
                                break;
                            }

                            case "Construction Area":{
                                markerOptions.icon(construction_marker);
                                break;
                            }

                            case "Traffic Jams":{
                                markerOptions.icon(trafficjam_marker);
                                break;
                            }

                            case "Road Crash":{
                                markerOptions.icon(roadcrash_marker);
                                break;
                            }
                        }
                        markerOptions.setSnippet("at "+event.getEvent_time()+" ");
                            markerOptions.position(new LatLng(event.getEvent_lat(), event.getEvent_lng()));

                            mMap.addMarker(markerOptions);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void myLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callPermission();
        }else {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(4000);
            locationRequest.setFastestInterval(2000);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    lat = locationResult.getLastLocation().getLatitude();
                    lng = locationResult.getLastLocation().getLongitude();

                    latLng1 = new LatLng(lat, lng);

                }
            }, Looper.getMainLooper());
        }

    }

    private void callPermission(){
        Permissions.check(getActivity()/*context*/, Manifest.permission.ACCESS_FINE_LOCATION, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                myLocation();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                callPermission();
            }
        });
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onStart() {
        super.onStart();
        mapView.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
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


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocationComponent();
        }
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(100,
                100, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



}
