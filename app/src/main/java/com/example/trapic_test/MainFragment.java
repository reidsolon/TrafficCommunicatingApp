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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trapic_test.Adapters.ViewPagerAdapter;
import com.example.trapic_test.MainFragments.MapFragment;
import com.example.trapic_test.MainFragments.NewsfeedFragment;
import com.example.trapic_test.MainFragments.NotificationFragment;
import com.example.trapic_test.MainFragments.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.text.WordUtils;

public class MainFragment extends AppCompatActivity {
    TextView test;
    Button logout;

    LinearLayout postLink;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseDatabase db;
    DatabaseReference dbRefs;
    DocumentReference documentReference;

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
        String id = auth.getUid();

        firestore = FirebaseFirestore.getInstance();

        documentReference = firestore.collection("Users").document(id);

        if(auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(MainFragment.this, MainActivity.class));
        }

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String firstname = "Hi, "+ WordUtils.capitalize(documentSnapshot.getString("user_firstname"));
                test.setText(firstname);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });




        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new MapFragment(), "");
        pagerAdapter.addFragment(new NewsfeedFragment(), "");
        pagerAdapter.addFragment(new NotificationFragment(), "");
        pagerAdapter.addFragment(new ProfileFragment(), "");

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        postLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED){
                    requestStoragePermission();
                }else{
                    startActivity(new Intent(getApplicationContext(), SelectEvent.class));
                }
            }
        });
    }

    public void init(){

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        test = (TextView) findViewById(R.id.test);
        postLink = (LinearLayout) findViewById(R.id.post_link);
        logout = (Button) findViewById(R.id.logout_btn);

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
}
