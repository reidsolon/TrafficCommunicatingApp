package com.example.trapic_test;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.trapic_test.Model.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostActivity extends AppCompatActivity {
    DatabaseReference dbRefs;
    FirebaseAuth auth;
    LinearLayout layout;
    FirebaseUser firebaseUser;
    EditText caption, location, type;
    Button postBtn;
    ProgressDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postactivity_layout);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Posting event...");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        dbRefs = FirebaseDatabase.getInstance().getReference("PostedEvents");



        initView();


        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validatePost()){
                    dialog.show();
                    addEvent();
                }else{
                    Toast.makeText(getApplicationContext(), "Validation Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void initView(){
        layout = (LinearLayout) findViewById(R.id.postevent_layout);
        caption = (EditText) findViewById(R.id.event_caption);
        location = (EditText) findViewById(R.id.event_location);
        type = (EditText) findViewById(R.id.event_type);
        postBtn = (Button) findViewById(R.id.postBtn);
    }

    public void addEvent(){
        String caption_txt = caption.getText().toString();
        String type_txt = type.getText().toString();
        String location_txt = location.getText().toString();

        Event event = new Event(caption_txt, type_txt, location_txt);
        String id = auth.getUid();
        String post_id = dbRefs.push().getKey();

        dbRefs.child(id).child(post_id).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    dialog.dismiss();
                    caption.setText("");
                    type.setText("");
                    location.setText("");
                    Toast.makeText(getApplicationContext(), "Successfully posted!", Toast.LENGTH_LONG);
                }else{
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Posting unsuccessful!", Toast.LENGTH_LONG);
                }
            }
        });

    }
    public boolean validatePost(){

        String caption_txt = caption.getText().toString();
        String type_txt = type.getText().toString();
        String location_txt = location.getText().toString();

        boolean caption_bol;
        boolean type_bol;
        boolean location_bol;

        if(caption_txt.equals("")){
            caption.setError("Enter something about the event");
            caption_bol = false;
        }else{
            caption_bol = true;
        }

        if (type_txt.equals("")) {
            type.setError("Enter event type");
            type_bol =  false;
        }else{
            type_bol = true;
        }

        if(location_txt.equals("")){
            location.setError("Adasdasd");
            location_bol = false;
        }else{
            location_bol = true;
        }

        if(caption_bol && location_bol && type_bol){
            return true;
        }else{
            return false;
        }

    }
}
