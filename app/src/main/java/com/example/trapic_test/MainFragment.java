package com.example.trapic_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainFragment extends AppCompatActivity {
    TextView test;
    Button logout;

    LinearLayout postLink;
    FirebaseAuth auth;

    FirebaseDatabase db;
    DatabaseReference dbRefs;
    DataSnapshot dataSnapshot;
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
        dbRefs = FirebaseDatabase.getInstance().getReference("User").child(id);

        if(auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(MainFragment.this, MainActivity.class));
        }

        dbRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String full_name = dataSnapshot.child("firstname").getValue().toString()+" "+dataSnapshot.child("lastname").getValue().toString();
                test.setText("Hi, "+full_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                startActivity(new Intent(getApplicationContext(), SelectEvent.class));
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

    public void setupTabIcons(){
        tabLayout.getTabAt(0).setIcon(R.drawable.map_logo);
        tabLayout.getTabAt(1).setIcon(R.drawable.newsfeed_logo);
        tabLayout.getTabAt(2).setIcon(R.drawable.notif_logo);
        tabLayout.getTabAt(3).setIcon(R.drawable.profile_logo);
    }
}
