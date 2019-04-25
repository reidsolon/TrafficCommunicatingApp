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
import android.graphics.Color;
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
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegAnnotation;
import com.mapbox.api.directions.v5.models.RouteLeg;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
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
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.location.LocationEngineConductorListener;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.apache.commons.lang3.text.WordUtils;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapFragment extends Fragment implements LocationEngineConductorListener,PermissionsListener, MapboxMap.OnMapClickListener {

    private MapboxMap mMap;

    private double distance;
    double roundedDistance;
    double roundedKm;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MapView mapView;
    private LocationRequest locationRequest;
    private TextView cmt_user_name, cmt_msg, dest_dur, dest_dis, event_type_txt, event_caption_txt, event_time_txt, close_txt,status_mode, user_count, marker_comment_txt, cons_count, road_count, traffic_count;
    private ImageView cat_img;
    private String[] list;
    private double lat, lng;
    private LatLng latLng1, myLatLng;
    private Event[] event = new Event[200];
    private User[] user = new User[200];
    private BottomSheetDialog dialog, destination_dialog;
    private Button myLocBtn, myLocBtn2, viewNewsfeedBtn, yesBtn, noBtn, marker_comment_btn, remove_route_btn;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private CameraPosition cameraPosition;
    private boolean event_status;
    private int i=0, user_count_num, user_i=0;
    private MarkerOptions markerOptions;
    private String event_user_id, event_user_fullname;
    private Marker[] event_marker = new Marker[200];
    private Marker[] user_marker = new Marker[200];
    private HashMap<Marker, Event> map = new HashMap<Marker, Event>();
    private DirectionsRoute currentRoute;
    private Style mapStyle;
    private Polyline polyline;

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
        destination_dialog = new BottomSheetDialog(getContext());

        dialog.setContentView(R.layout.marker_dialog);
        destination_dialog.setContentView(R.layout.destination_dialog);
        v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        initViews(view);
//        refreshAll();

        myLocBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    animateLocation();
                }else{
                    Toast.makeText(getContext(), "Not available on guest mode.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        marker_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                        if(!marker_comment_txt.getText().toString().equals("") && marker_comment_txt.getText().length() > 7){

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

                                    marker_comment_txt.setText("");
                                    Toast.makeText(getContext(), "Commented Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            marker_comment_txt.setError("Please provide comment and make sure it is atleast 10 characters");
                        }
                    }else{
                        Toast.makeText(getContext(), "Not available in unverfied user.", Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(getContext(), "Not available in guest mode.", Toast.LENGTH_SHORT).show();
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

                        if(FirebaseAuth.getInstance().getCurrentUser() != null){
                            myLocation();
                        }else{
                            guestLocation();
                        }

                        loadAllMarkers();
                        callPermission();
                        enableLocationComponent();
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            displayUsers();
                        }

                        mapStyle = style;
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
         cons_count = view.findViewById(R.id.cons_count);
         road_count = view.findViewById(R.id.road_count);
         traffic_count = view.findViewById(R.id.traffic_count);
         remove_route_btn = view.findViewById(R.id.remove_route_btn);

         remove_route_btn.setVisibility(View.INVISIBLE);

         remove_route_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(destinationLocation != null && polyline != null){
                     mMap.removeMarker(destinationLocation);
                     polyline.remove();
                     remove_route_btn.setVisibility(View.INVISIBLE);
                 }
             }
         });

         marker_comment_btn = dialog.findViewById(R.id.comment_btn);
         marker_comment_txt = dialog.findViewById(R.id.comment_txt);
        close_txt = dialog.findViewById(R.id.close_txt);
        viewNewsfeedBtn = dialog.findViewById(R.id.view_to_newsfeed);
//        cat_img = dialog.findViewById(R.id.cat_img);
        event_type_txt = dialog.findViewById(R.id.event_cat_txt);
        event_caption_txt = dialog.findViewById(R.id.event_cap);
        yesBtn = dialog.findViewById(R.id.yes_btn);
        noBtn = dialog.findViewById(R.id.no_btn);

        dest_dur = view.findViewById(R.id.dest_dur);
        dest_dis = view.findViewById(R.id.dest_dis);

        cmt_user_name = dialog.findViewById(R.id.cmt_user_id);
        cmt_msg = dialog.findViewById(R.id.cmt_msg);
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
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                myLocation();
            }

            mMap.setMinZoomPreference(12);

            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3000);
            }

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
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                myLocation();
            }

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
                if(dataSnapshot.child("yes").child(user_id).exists()){
                    yesBtn.setVisibility(View.INVISIBLE);
                    noBtn.setVisibility(View.INVISIBLE);
                    close_txt.setText("You just said that this event is closed. Thank you for giving us information.");
                    view.setTag("Voted");
                }else if(dataSnapshot.child("no").child(user_id).exists()){
                    yesBtn.setVisibility(View.INVISIBLE);
                    noBtn.setVisibility(View.INVISIBLE);
                    close_txt.setText("You just said that this event is still ongoing. Thank you for giving us information.");
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
                    close_txt.setText("This event is closed "+ago+" and approved by the users.");
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
                        databaseReference.child(post_id).child("yes").child(user_id).setValue(true);
                    }else{
                        yesBtn.setVisibility(View.INVISIBLE);
                        noBtn.setVisibility(View.INVISIBLE);
                    }
                }
            });

            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(view.getTag().equals("Not Voted")){
                        databaseReference.child(post_id).child("no").child(user_id).setValue(true);
                        final String date_time = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());

//                Add 1 hour for deletion
                        java.text.DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a");
                        Calendar calendar = null;
                        Date date;
                        try {
                            date = dateFormat.parse(date_time);
                            calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.HOUR, 2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        FirebaseDatabase.getInstance().getReference("Posts").child(post_id).child("event_closed_time").setValue(date_time);
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
                    if(event[i].getEvent_deleted() == false){
                        loadMarkerDatas();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadMarkerDatas(){

        event_user_id = event[i].getUser_id();

        if(event[i].getEvent_status().equals("closed")){
            PrettyTime p = new PrettyTime();
            String p2 = p.format(new Date(event[i].getEvent_closed_time()));

            if(p2.equals("20 minutes ago")){
                FirebaseDatabase.getInstance().getReference("Posts")
                        .child(event[i].getEvent_id()).child("event_deleted").setValue(true);
            }
        }

        markerOptions = new MarkerOptions();

        if(latLng1 != null){

            //check if the event approval is 10 and
            final DatabaseReference databaseReference1 = FirebaseDatabase
                    .getInstance()
                    .getReference("Posts")
                    .child(event[i].getEvent_id());
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Approval").child(event[i].getEvent_id()).child("yes");
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

//                Add 1 hour for deletion
                                java.text.DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a");
                                Calendar calendar = null;
                                Date date;
                                try {
                                    date = dateFormat.parse(date_time);
                                    calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    calendar.add(Calendar.MINUTE, 20);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                    FirebaseDatabase.getInstance().getReference("Posts")
                                            .child(event2.getEvent_id())
                                            .child("event_closed_time").setValue(calendar.getTime().toString());
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

            if(FirebaseAuth.getInstance().getCurrentUser() != null){
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
            }


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
            if(event[i].getEvent_deleted() == false){
                event_marker[i] = mMap.addMarker(markerOptions);
            }


            mMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {

                        if(marker.getSnippet() != null && marker.getTitle() != "Destination"){
                            String post_id = marker.getSnippet();
                            FirebaseDatabase.getInstance().getReference("Posts").child(post_id)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            dataSnapshot.getChildren();
                                            Event event = dataSnapshot.getValue(Event.class);
                                            event_status = event.getEvent_deleted();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                            if(event_status == false){
                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                    isClosed(marker.getSnippet(), yesBtn);
                                    loadOnClicks(marker.getSnippet(), yesBtn);
                                }

                                event_type_txt.setText(marker.getTitle());
                                event_caption_txt.setText(marker.getSnippet());
                                dialog.show();
                                FirebaseDatabase.getInstance().getReference("Comments").child(post_id).limitToLast(1)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.getChildrenCount() > 0){
                                                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                                        final Comment comment = dataSnapshot1.getValue(Comment.class);
                                                        FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(comment.getComment_user_id()).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                User user = dataSnapshot.getValue(User.class);
                                                                cmt_user_name.setText(user.getUser_firstname()+" said,");
                                                                cmt_msg.setText(comment.getCmt_msg());
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                }else{
                                                    cmt_user_name.setText("There is no comment on this post yet.");
                                                    cmt_msg.setText("There is no comment on this post yet.");
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }

                        }else if(marker.getSnippet() == "Destination"){
                            destinationLocation.showInfoWindow(mMap, mapView);
                        }else{

                        }
//                        end if

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
                                    status_mode.setText("I am here at "+my_address);

                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("user_address").setValue(my_address);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
            }
        }
    }

    private void guestLocation(){
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
                                status_mode.setText("I am here at "+my_address);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
            }
        }
    }

    private void callPermission(){
        Permissions.check(getActivity()/*context*/, Manifest.permission.ACCESS_FINE_LOCATION, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    myLocation();
                }

                Handler handler = new Handler();
                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Retrieving current location");
                dialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        enableLocationComponent();
                        loadAllMarkers();
                        loadSideAreaCount();
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
        if(FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            if(destinationLocation != null){
                mMap.removeMarker(destinationLocation);
            }
            destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
            originPoint = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());

            getRoute(mapStyle, originPoint, destinationPoint);


        }else{
            Toast.makeText(getContext(), "You cannot use this feature yet.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    public void initSource(Style style, Point point1, Point point2){
        style.addSource(new GeoJsonSource("route-source-id",
                FeatureCollection.fromFeatures(new Feature[] {})));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource("route-source-id", FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(point1.longitude(), point2.latitude())),
                Feature.fromGeometry(Point.fromLngLat(point1.longitude(), point2.latitude()))}));
        style.addSource(iconGeoJsonSource);
    }
    public void initLayers(Style style, Point point1, Point point2){
        LineLayer routeLayer = new LineLayer("route_layer_id", "route_source_id");

// Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        style.addLayer(routeLayer);

// Add the red marker icon image to the map
        style.addImage("red-pin-icon-id", BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.ic_user_marker)));

// Add the red marker icon SymbolLayer to the map
        style.addLayer(new SymbolLayer("icon-layer-id", "icon-source-id").withProperties(
                iconImage("red-pin-icon-id"),
                iconIgnorePlacement(true),
                iconIgnorePlacement(true),
                iconOffset(new Float[] {0f, -4f})));
    }
    public void getRoute(final Style style, Point origin, final Point destination){
//        initSource(style, origin, destination);
//        initLayers(style, origin, destination);
        MapboxDirections client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)

                .accessToken("pk.eyJ1IjoicmVpZHNvbG9uIiwiYSI6ImNqcnZpZThzMTAyN2Ezemx4eHMzM2RoZGwifQ.j65VGpYO6g84DnR1koippQ")
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                if (response.body() == null) {
                    Log.e("Map","No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e("Map","No routes found");
                    return;
                }

// Retrieve the directions route from the API response
                currentRoute = response.body().routes().get(0);

//                Converts mills to hh mm ss and the distance

                drawRoute(currentRoute);
                LatLng routeLatLng = new LatLng(destination.latitude(), destination.longitude());
                if(destinationLocation != null){
                    mMap.removeMarker(destinationLocation);
                }

                final Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(destination.latitude(), destination.longitude(), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String destination_address = addresses.get(0).getFeatureName()+" "+addresses.get(0).getThoroughfare()+" "+addresses.get(0).getLocality()+" "+addresses.get(0).getAdminArea();
                    destinationLocation = mMap.addMarker(new MarkerOptions().position(routeLatLng)
                        .setTitle("Destination"));

                    double distance_rounded = Math.round(currentRoute.distance() * 100.0) / 100.0;
                    double rounded_km;
                    if(distance_rounded > 1000.0){
                         rounded_km = Math.round((currentRoute.distance() / 100.0) * 100.0)/100.0;
                         destinationLocation.setSnippet(destination_address+"\n"+"Travel time: "+currentRoute.duration()+"\n"+"Distance: "+rounded_km+" km");
                    }else{
                        destinationLocation.setSnippet(destination_address+"\n"+"Travel time: "+currentRoute.duration()+"\n"+"Distance: "+distance_rounded+" m");
                    }




                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Finding best route...");
                progressDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Route displayed!", Toast.LENGTH_SHORT).show();
                        remove_route_btn.setVisibility(View.VISIBLE);

                    }
                }, 1500);


            }

            @Override public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {

                Timber.e("Error: " + throwable.getMessage());

            }
        });

    }
    public void drawRoute(DirectionsRoute route1){
        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route1.geometry(), PRECISION_6);
        List<Point> coordinates = lineString.coordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).latitude(),
                    coordinates.get(i).longitude());
        }
        if(polyline != null){
            polyline.remove();
        }
        // Draw Points on MapView
        polyline = mMap.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#009688"))
                .width(5));

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

    private void loadSideAreaCount(){
        int i_traffic;
        int i_con;
        int i_road;
        FirebaseDatabase.getInstance().getReference("Users").orderByChild("user_isOnline").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_count.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Posts").orderByChild("event_type").equalTo("Traffic Jams")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        traffic_count.setText(""+dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("Posts").orderByChild("event_type").equalTo("Construction Area")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cons_count.setText(""+dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("Posts").orderByChild("event_type").equalTo("Road Crash")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        road_count.setText(""+dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}