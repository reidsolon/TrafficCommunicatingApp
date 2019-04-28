package com.example.trapic_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trapic_test.Model.Event;
import com.example.trapic_test.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

public class SingleFeedActivity extends Activity {
    TextView event_type, user_name, user_email, caption, timestamp, location;
    private ImageView post_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_feed_item);
        initViews();
        loadData();
    }
    private void initViews(){
        user_name = findViewById(R.id.user_name);
        user_email = findViewById(R.id.posted_by);
        caption = findViewById(R.id.caption);
        event_type = findViewById(R.id.event_type);
        timestamp = findViewById(R.id.timestamp);
        post_img = findViewById(R.id.post_img);
        location = findViewById(R.id.location);
    }
    private void loadData(){
        Intent intent = getIntent();

        final String post_id = intent.getStringExtra("PostID");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(post_id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getChildren();
                    Event event = dataSnapshot.getValue(Event.class);
//                    user_name.setText(event.getUser_id());
                    setUserInfo(event.getUser_id());
                    event_type.setText(event.getEvent_type());
                    timestamp.setText(event.getEvent_time());
                    location.setText(event.getEvent_location());
                    Picasso.with(getApplicationContext()).load(event.getEvent_image()).fit().into(post_img);
                    caption.setText(event.getEvent_caption());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setUserInfo(String id){

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(id);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren();
                    User user = dataSnapshot.getValue(User.class);
                    user_name.setText(WordUtils.capitalize(user.getUser_firstname()+" "+user.getUser_lastname()));
                    user_email.setText(WordUtils.capitalize(user.getUser_eMail()));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
