package com.example.trapic_test;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trapic_test.Adapters.ViewPagerAdapter;
import com.example.trapic_test.MainFragments.MapFragment;
import com.example.trapic_test.MainFragments.NewsfeedFragment;
import com.example.trapic_test.MainFragments.NotificationFragment;
import com.example.trapic_test.MainFragments.ProfileFragment;
import com.example.trapic_test.Model.Event;
import com.example.trapic_test.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainFragment extends AppCompatActivity {
    private TextView test, status_mode_txt, user_fullname, user_email, profile_btn, help_btn, feedback_btn, settings_btn, logout;

    private LinearLayout postLink,user_settings_btn, dialog_close_btn;
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference dbRefs;
    private BottomSheetDialog dialog1;
    private Button mapLink;

    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainfragment_layout);
        init();
        loadUserInfo();
        checkStatus();

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

//        pagerAdapter.addFragment(new MapFragment(), "");
        pagerAdapter.addFragment(new NewsfeedFragment(), "");
        pagerAdapter.addFragment(new NotificationFragment(), "");
        pagerAdapter.addFragment(new ProfileFragment(), "");

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){


            final String date_time = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());

            updateUserStatus(FirebaseAuth.getInstance().getCurrentUser().getUid());
            makeUserOnline(FirebaseAuth.getInstance().getCurrentUser().getUid());
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user_isOnline").onDisconnect().setValue(false);
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("user_lastOnline").onDisconnect().setValue(date_time);
        }

        setupOnClicks();
    }
    private void makeUserOnline(String id){
        FirebaseDatabase.getInstance().getReference("Users").child(id).child("user_isOnline").setValue(true);
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("user_lastOnline").setValue("0");
    }
    private void checkStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            if(user.isEmailVerified()){
                loadUserInfo();
            }else{
                loadUserInfo();
            }
        }

        Query query = FirebaseDatabase.getInstance().getReference("Posts");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                dataSnapshot.getChildren();
                Event event = dataSnapshot.getValue(Event.class);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                builder.setContentTitle("Someone posted an event!")
                        .setContentText("Click here to view")
                        .setSmallIcon(R.drawable.trapic_logo)
                        .setTicker("There is an event posted near you!")
                        .setAutoCancel(true);

                Notification notification = builder.build();
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                manager.notify(4, notification);
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
    private void setupOnClicks(){
        postLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified() && FirebaseAuth.getInstance().getCurrentUser() != null){
                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED){
                            requestStoragePermission();
                        }else{
                            startActivity(new Intent(getApplicationContext(), SelectEvent.class));
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Unverified account/Guest cannot use this feature.", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Unverified account/Guest cannot use this feature.", Toast.LENGTH_LONG).show();
                }

            }

        });

        mapLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainFragment.this, MapFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        user_settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog1.show();
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.getChildren();
                                    User user = dataSnapshot.getValue(User.class);

                                    user_fullname.setText(WordUtils.capitalize(user.getUser_firstname()+" " +user.getUser_lastname()));
                                    user_email.setText(user.getUser_eMail());

                                    final String date_time = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());
                                    logout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                                                if(FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){

                                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child("user_isOnline").setValue(false);
                                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child("user_lastOnline").setValue(date_time);
                                                    FirebaseAuth.getInstance().signOut();
                                                    finish();
                                                    startActivity(new Intent(MainFragment.this, MainActivity.class));
                                                }else if(FirebaseAuth.getInstance().getCurrentUser() != null || FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){

                                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child("user_isOnline").setValue(false);
                                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child("user_lastOnline").setValue(date_time);
                                                    FirebaseAuth.getInstance().signOut();
                                                    finish();
                                                    startActivity(new Intent(MainFragment.this, MainActivity.class));
                                                }else{
                                                    finish();
                                                    startActivity(new Intent(MainFragment.this, MainActivity.class));
                                                }
                                            }else{
                                                finish();
                                                startActivity(new Intent(MainFragment.this, MainActivity.class));
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }else{
                    Intent intent = getIntent();
                    String name = intent.getStringExtra("Guest_name");
                    user_fullname.setText(WordUtils.capitalize(name));
                    user_email.setText("Guest User");
                    profile_btn.setVisibility(View.INVISIBLE);
                    feedback_btn.setVisibility(View.INVISIBLE);
                    logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            startActivity(new Intent(MainFragment.this, MainActivity.class));
                        }
                    });
                }

            }
        });
        dialog_close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            profile_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(3);
                }
            });
            feedback_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainFragment.this, SendFeedbackActivity.class));
                }
            });
        }

    }

    private void loadUserInfo(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            if(FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                status_mode_txt.setText("Verified User");
            }else if(FirebaseAuth.getInstance().getCurrentUser() != null || FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                status_mode_txt.setText("Unverified User");
            }else{
                status_mode_txt.setText("Guest Mode");
            }

            String id = auth.getCurrentUser().getUid();
            db = FirebaseDatabase.getInstance();

            dbRefs = db.getReference("Users").child(id);
            if(auth.getCurrentUser() == null){
                finish();
                startActivity(new Intent(MainFragment.this, MainActivity.class));
            }
            dbRefs.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getChildren();
                    User user = dataSnapshot.getValue(User.class);
                    String firstname = "Hi, "+ WordUtils.capitalize(user.getUser_firstname()+" Lv. 1");
                    test.setText(firstname);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Intent intent = getIntent();
            String name = intent.getStringExtra("Guest_name");
            test.setText("Hi,"+ WordUtils.capitalize(name));
            status_mode_txt.setText("Guest mode");
        }

        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren();
                Event event = dataSnapshot.getValue(Event.class);
                final String date_time = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());
//                if(event.getEvent_closed_time() <= date_time){
//
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void init(){

        viewPager =  findViewById(R.id.viewPager);
        tabLayout =  findViewById(R.id.tabLayout);
        test =  findViewById(R.id.test);
        postLink =  findViewById(R.id.post_link);
        user_settings_btn = findViewById(R.id.user_settings_btn);
        status_mode_txt = findViewById(R.id.status_mode_txt);
        mapLink = findViewById(R.id.main_view_map);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        dbRefs = db.getReference("User");
        FirebaseUser user = auth.getCurrentUser();

        dialog1 = new BottomSheetDialog(this);
        dialog1.setContentView(R.layout.user_settings_layout);
        user_fullname = dialog1.findViewById(R.id.user_fullname);
        user_email = dialog1.findViewById(R.id.user_email);
        dialog_close_btn = dialog1.findViewById(R.id.dialog_close_btn);
        feedback_btn = dialog1.findViewById(R.id.feedback_btn);
        profile_btn = dialog1.findViewById(R.id.myprofile_btn);
        help_btn = dialog1.findViewById(R.id.help_btn);
        settings_btn = dialog1.findViewById(R.id.settings_btn);
        logout =  dialog1.findViewById(R.id.logout_btn);

    }

    public void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainFragment.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this).setTitle("Permission").setMessage("Allow this app to write external storage to be able to post")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainFragment.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    }).setNegativeButton("Don't Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public void setupTabIcons(){
//        tabLayout.getTabAt(0).setIcon(R.drawable.map_logo);
        tabLayout.getTabAt(0).setIcon(R.drawable.newsfeed_logo);
        tabLayout.getTabAt(1).setIcon(R.drawable.notif_logo);
        tabLayout.getTabAt(2).setIcon(R.drawable.profile_logo);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(getApplicationContext(), SelectEvent.class));
            }
        }
    }

    public void set(int i){
        viewPager.setCurrentItem(i, true);
    }

    @Override
    public void onBackPressed() {
//        Toast.makeText(getApplicationContext(), "Back btn is disabled", Toast.LENGTH_SHORT).show();
        viewPager.setCurrentItem(0);
    }

    private void updateUserStatus(String id){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(id).child("user_status");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified()){
            databaseReference.setValue("verified");
        }else{
            databaseReference.setValue("unverified");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user_isOnline").setValue(false);

            final String date_time = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("user_lastOnline").setValue(date_time);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user_isOnline").setValue(true);
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("user_lastOnline").setValue(null);

        }

    }

    private void removeLatLng(){
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("user_lat").removeValue();

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("user_lat").removeValue();
    }
}
