package com.example.trapic_test;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import com.example.trapic_test.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainFragment extends AppCompatActivity {
    private TextView test, status_mode_txt;
    private Button logout;

    private LinearLayout postLink;
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference dbRefs;

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

        pagerAdapter.addFragment(new MapFragment(), "");
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
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }else if(FirebaseAuth.getInstance().getCurrentUser() != null || FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){

                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("user_isOnline").setValue(false);
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("user_lastOnline").setValue(date_time);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }else{

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }else{
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        });

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
                    String firstname = "Hi, "+ WordUtils.capitalize(user.getUser_firstname());
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

    }
    public void init(){

        viewPager =  findViewById(R.id.viewPager);
        tabLayout =  findViewById(R.id.tabLayout);
        test =  findViewById(R.id.test);
        postLink =  findViewById(R.id.post_link);
        logout =  findViewById(R.id.logout_btn);

        status_mode_txt = findViewById(R.id.status_mode_txt);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        dbRefs = db.getReference("User");
        FirebaseUser user = auth.getCurrentUser();

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
        tabLayout.getTabAt(0).setIcon(R.drawable.map_logo);
        tabLayout.getTabAt(1).setIcon(R.drawable.newsfeed_logo);
        tabLayout.getTabAt(2).setIcon(R.drawable.notif_logo);
        tabLayout.getTabAt(3).setIcon(R.drawable.profile_logo);
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
        Toast.makeText(getApplicationContext(), "Back btn is disabled", Toast.LENGTH_SHORT).show();
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
    protected void onPause() {
        super.onPause();

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user_isOnline").setValue(false);

        final String date_time = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("user_lastOnline").setValue(date_time);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user_isOnline").setValue(true);
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
