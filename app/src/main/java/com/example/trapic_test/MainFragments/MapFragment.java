package com.example.trapic_test.MainFragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trapic_test.Model.Comment;
import com.example.trapic_test.Model.Event;
import com.example.trapic_test.Model.User;
import com.example.trapic_test.R;
import com.example.trapic_test.SingleFeedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
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
import com.mapbox.services.android.navigation.ui.v5.location.LocationEngineConductorListener;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.apache.commons.lang3.text.WordUtils;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements LocationEngineConductorListener,PermissionsListener, MapboxMap.OnMapClickListener {

    private MapboxMap mMap;

    private double distance;
    double roundedDistance;
    double roundedKm;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MapView mapView;
    private LocationRequest locationRequest;
    private TextView event_type_txt, event_caption_txt, event_time_txt, close_txt,status_mode, user_count, marker_comment_txt;
    private ImageView cat_img;
    private String[] list;
    private double lat, lng;
    private LatLng latLng1, myLatLng;
    private Event[] event = new Event[200];
    private User[] user = new User[200];
    private BottomSheetDialog dialog;
    private Button myLocBtn, myLocBtn2, viewNewsfeedBtn, yesBtn, noBtn, marker_comment_btn;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private CameraPosition cameraPosition;
    private int i=0, user_count_num, user_i=0;
    private MarkerOptions markerOptions;
    private String event_user_id, event_user_fullname;
    private Marker[] event_marker = new Marker[200];
    private Marker[] user_marker = new Marker[200];
    private HashMap<Marker, Event> map = new HashMap<Marker, Event>();


    // Get instance of Vibrator from current Context
    Vibrator v;

//    Location Change
    private Location originLocation;
    private Point originPoint;
    private Point destinationPoint;
    private Marker destinationLocation;
    private NavigationMapRoute navigationMapRoute;

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
        dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(R.layout.marker_dialog);

        v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        initViews(view);

//        refreshAll();

        myLocBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateLocation();
            }
        });
        marker_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!marker_comment_txt.equals("")){

                    final String[] fullname = new String[1];
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.getChildren();
                                    User user = dataSnapshot.getValue(User.class);
                                    fullname[0] = user.getUser_firstname()+" "+user.getUser_lastname();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                    final String[] publisherId = new String[1];
                    FirebaseDatabase.getInstance().getReference("Posts")
                            .child(event_caption_txt.getText().toString())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.getChildren();
                                    Event event = dataSnapshot.getValue(Event.class);
                                    publisherId[0] = event.getUser_id();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                    final String d_date = (String) DateFormat.format("MMMM dd, yyyy", new Date());
                    final String d_time = (String) DateFormat.format("hh:mm:ss a", new Date());
                    final String date_time = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());
                    String postId = event_caption_txt.getText().toString();
                    Comment comment1 = new Comment(publisherId[0], postId, marker_comment_txt.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid(), fullname[0], d_date, d_time,date_time);
                    String id = FirebaseDatabase.getInstance().getReference("Comments").push().getKey();
                    FirebaseDatabase.getInstance().getReference("Comments").child(postId).child(id).setValue(comment1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Commented Successfully", Toast.LENGTH_SHORT).show();
                            marker_comment_txt.setText("");
                        }
                    });
                }
            }
        });
        myLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectType();
            }
        });
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mMap = mapboxMap;

                mapboxMap.setStyle(Style.TRAFFIC_DAY, new Style.OnStyleLoaded() {


                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        myLocation();
                        loadAllMarkers();
                        callPermission();
                        enableLocationComponent();
                        displayUsers();
                        // Get an instance of the component

                    }
                });
            }
        });

//        navigationMapRoute = new NavigationMapRoute(null, mapView, mMap);


    }

    private void selectType(){
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
    private void initViews(View view){
        mapView = view.findViewById(R.id.mapView);
        myLocBtn = view.findViewById(R.id.my_loc);
        myLocBtn2 = view.findViewById(R.id.my_loc2);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
         status_mode = view.findViewById(R.id.status_mode_txt);
         user_count = view.findViewById(R.id.user_count);

         marker_comment_btn = dialog.findViewById(R.id.comment_btn);
         marker_comment_txt = dialog.findViewById(R.id.comment_txt);
        close_txt = dialog.findViewById(R.id.close_txt);
        viewNewsfeedBtn = dialog.findViewById(R.id.view_to_newsfeed);
//        cat_img = dialog.findViewById(R.id.cat_img);
        event_type_txt = dialog.findViewById(R.id.event_cat_txt);
        event_caption_txt = dialog.findViewById(R.id.event_cap);
        yesBtn = dialog.findViewById(R.id.yes_btn);
        noBtn = dialog.findViewById(R.id.no_btn);



    }
    private void refreshAll(){
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadAllMarkers();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                loadAllMarkers();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadAllMarkers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Approval");
        databaseReference2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadAllMarkers();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadAllMarkers();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                loadAllMarkers();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadAllMarkers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadAllMarkers();
            }
        });
    }
    private void animateLocation(){

        if (latLng1 != null) {
            myLocation();
            mMap.setMinZoomPreference(12);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3000);
        }

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

            originLocation = locationComponent.getLastKnownLocation();
            myLocation();
            loadAllMarkers();

            mMap.addOnMapClickListener(this);

        } else {

            permissionsManager = new PermissionsManager(this);

            permissionsManager.requestLocationPermissions(getActivity());

        }
    }
    private void isVoted(String post_id, final Button view){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Approval").child(post_id);
        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user_id).exists()){
                    yesBtn.setVisibility(View.INVISIBLE);
                    noBtn.setVisibility(View.INVISIBLE);
                    close_txt.setText("You just said that this event is closed. Thank you for giving us information.");
                    view.setTag("Voted");
                }else{
                    view.setTag("Not Voted");
                    yesBtn.setVisibility(View.VISIBLE);
                    noBtn.setVisibility(View.VISIBLE);
                    close_txt.setText("Is this event closed?");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void isClosed(final String post_id, final Button view){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(post_id);
        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren();
                Event event = dataSnapshot.getValue(Event.class);
                if(event.getEvent_status().equals("closed")){
                    yesBtn.setVisibility(View.INVISIBLE);
                    noBtn.setVisibility(View.INVISIBLE);

                    PrettyTime prettyTime = new PrettyTime();
                    String ago = prettyTime.format(new Date(event.getEvent_closed_time()));
                    close_txt.setText("This event is closed since - "+ago+"!");
                }else{
                    isVoted(post_id, view);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void loadOnClicks(final String post_id, final Button view){

        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Approval");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadAllMarkers();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadAllMarkers();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    loadAllMarkers();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadAllMarkers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser() != null && user.isEmailVerified()){
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(view.getTag().equals("Not Voted")){
                        databaseReference.child(post_id).child(user_id).setValue(true);
                    }else{
                        yesBtn.setVisibility(View.INVISIBLE);
                        noBtn.setVisibility(View.INVISIBLE);
                    }
                }
            });

            viewNewsfeedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SingleFeedActivity.class);
                    intent.putExtra("PostID", post_id);
                    startActivity(intent);
                }
            });
        }else{
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "You are not a verified user.", Toast.LENGTH_LONG).show();
                }
            });

            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "You are not a verified user.", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void loadAllMarkers() {


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    event[i] = dataSnapshot1.getValue(Event.class);
                    loadMarkerDatas();

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadMarkerDatas(){

        event_user_id = event[i].getUser_id();
        markerOptions = new MarkerOptions();

        if(latLng1 != null){

            //check if the event approval is 10 and
            final DatabaseReference databaseReference1 = FirebaseDatabase
                    .getInstance()
                    .getReference("Posts")
                    .child(event[i].getEvent_id());
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Approval").child(event[i].getEvent_id());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount() == 10){
                        dataSnapshot.getChildren();


                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.getChildren();
                                    Event event2 = dataSnapshot.getValue(Event.class);
                                    FirebaseDatabase.getInstance().getReference("Posts").child(event2.getEvent_id())
                                            .child("event_status").setValue("closed");

                                final String date_time = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());
                                    FirebaseDatabase.getInstance().getReference("Posts")
                                            .child(event2.getEvent_id())
                                            .child("event_closed_time").setValue(date_time);
                                    FirebaseDatabase.getInstance().getReference("Approval")
                                            .child(event2.getEvent_id()).removeValue();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getChildren();
                            User user = dataSnapshot.getValue(User.class);

                            myLatLng = new LatLng(user.getUser_lat(), user.getUser_lng());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            if(latLng1 != null){
                 distance = latLng1.distanceTo(new LatLng(event[i].getEvent_lat(), event[i].getEvent_lng()));
                 roundedDistance = Math.round(distance * 100.0) / 100.0;
                if(roundedDistance > 1000.0){
                    roundedKm = Math.round((roundedDistance / 100.0) * 100.0)/100.0;
                    markerOptions.setTitle("[ "+event[i].getEvent_type() + " ]: " + event[i].getEvent_location()+" "+roundedKm+" km away from you");
                }else{
                    markerOptions.setTitle("[ "+event[i].getEvent_type() + " ]: " + event[i].getEvent_location()+" "+roundedDistance+" m away from you");
                }
                if(distance <= 50 && event[i].getEvent_status().equals("open")){


                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());

                    if(event[i].getEvent_type().equals("Construction Area")){
                        builder.setContentTitle(event[i].getEvent_type())
                                .setContentText("There is a event within" + roundedDistance +"m near you.")
                                .setSmallIcon(R.drawable.ic_construction_marker)
                                .setTicker("There is an event posted near you!")
                                .setAutoCancel(true);

                        Notification notification = builder.build();
                        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                        manager.notify(1, notification);
                        v.vibrate(200);
                    }else if(event[i].getEvent_type().equals("Traffic Jams")){
                        builder.setContentTitle(event[i].getEvent_type())
                                .setContentText("There is a event within" + roundedDistance +"m near you.")
                                .setSmallIcon(R.drawable.ic_traffic_jam)
                                .setTicker("There is an event posted near you!")
                                .setAutoCancel(true);

                        Notification notification = builder.build();
                        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                        manager.notify(2, notification);
                        v.vibrate(200);
                    }else{
                        builder.setContentTitle(event[i].getEvent_type())
                                .setContentText("There is a event within" + roundedDistance +"m near you.")
                                .setSmallIcon(R.drawable.ic_road_crash)
                                .setTicker("There is an event posted near you!")
                                .setAutoCancel(true);

                        Notification notification = builder.build();
                        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                        manager.notify(3, notification);
                        v.vibrate(200);
                    }
            }

            }

            markerOptions.setSnippet(event[i].getEvent_id());
            IconFactory iconFactory = IconFactory.getInstance(getActivity());
            Icon construction_marker = iconFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_construction_marker));
            final Icon roadcrash_marker = iconFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_road_crash));
            final Icon trafficjam_marker = iconFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_traffic_jam));


            switch(event[i].getEvent_type()){
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


            markerOptions.position(new LatLng(event[i].getEvent_lat(), event[i].getEvent_lng()));
            event_marker[i] = mMap.addMarker(markerOptions);

            mMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {

//                    if(marker.getIcon().equals(roadcrash_marker)){
//                        cat_img.setImageResource(R.drawable.ic_road_crash);
//                    }else if(marker.getIcon().equals(trafficjam_marker)){
//                        cat_img.setImageResource(R.drawable.ic_traffic_jam);
//                    }else{
//                        cat_img.setImageResource(R.drawable.ic_construction_marker);
//                    }
                        if(marker.getSnippet() != null){
                            isClosed(marker.getSnippet(), yesBtn);
                            loadOnClicks(marker.getSnippet(), yesBtn);
                            event_type_txt.setText(marker.getTitle());
                            event_caption_txt.setText(marker.getSnippet());
                            dialog.show();
                        }else if(marker.getTitle() == "Destination"){
                            Toast.makeText(getContext(), "Your destination!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(), "TraPic Online User ", Toast.LENGTH_LONG).show();
                        }



                    return true;
                }
            });
            map.put(event_marker[i], event[i]);
            i++;
        }

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
                    originLocation = locationResult.getLastLocation();

                    latLng1 = new LatLng(lat, lng);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("user_latLng").setValue(latLng1);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("user_lat").setValue(lat);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("user_lng").setValue(lng);
                }
            }, Looper.getMainLooper());
            // setting up my location
            if (latLng1 != null) {

                cameraPosition = new CameraPosition.Builder().target(latLng1).zoom(17).bearing(180).tilt(40).build();
                final Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(latLng1.getLatitude(), latLng1.getLongitude(), 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    String my_address = addresses.get(0).getThoroughfare()+" "+addresses.get(0).getLocality()+" "+addresses.get(0).getAdminArea();
                                    status_mode.setText("Current Location: "+my_address);

                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("user_address").setValue(my_address);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
            }

            FirebaseDatabase.getInstance().getReference("Users").orderByChild("user_isOnline").equalTo(true).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user_count.setText(""+dataSnapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void callPermission(){
        Permissions.check(getActivity()/*context*/, Manifest.permission.ACCESS_FINE_LOCATION, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                myLocation();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialog dialog = new ProgressDialog(getActivity());
                        dialog.setMessage("Retrieving current location");
                        enableLocationComponent();
                        loadAllMarkers();
                    }
                },1000);

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

    private void showMarkerInfo(){

    }


    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        if(destinationLocation != null){
            mMap.removeMarker(destinationLocation);
        }
        destinationLocation = mMap.addMarker(new MarkerOptions().position(point).setTitle("Destination"));

        destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        originPoint = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());

        getRoute(originPoint, destinationPoint);

        return false;
    }

    public void getRoute(Point origin, Point destination){
        NavigationRoute.builder()
                .accessToken("pk.eyJ1IjoicmVpZHNvbG9uIiwiYSI6ImNqcnZpZThzMTAyN2Ezemx4eHMzM2RoZGwifQ.j65VGpYO6g84DnR1koippQ")
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                    }
                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onLocationUpdate(Location location) {
        originLocation = location;

    }

    private void displayUsers(){
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                IconFactory iconFactory = IconFactory.getInstance(getActivity());
                Icon user_icon = iconFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_user_marker));
                for(DataSnapshot snapshot :dataSnapshot.getChildren() ) {

                    User user = snapshot.getValue(User.class);
                    if(!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && user.isUser_isOnline()){

                        if(user_marker[i] != null){
                            mMap.removeMarker(user_marker[i]);
                        }

                        if(user.isUser_isOnline() == false){
                            mMap.removeMarker(user_marker[i]);
                            displayUsers();
                        }
                        user_marker[i] = mMap.addMarker(new MarkerOptions().setIcon(user_icon).position(new LatLng(user.getUser_lat(), user.getUser_lng())));
                        i++;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}