package com.example.trapic_test;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trapic_test.Adapters.CommentAdapter;
import com.example.trapic_test.Adapters.FeedAdapter;
import com.example.trapic_test.Adapters.NewsfeedAdapter;
import com.example.trapic_test.Adapters.UserCommentAdapter;
import com.example.trapic_test.Model.Comment;
import com.example.trapic_test.Model.Event;
import com.example.trapic_test.Model.Notification;
import com.example.trapic_test.Model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    ImageView cmtBtn;
    String postId, postBy, publisherID;
    TextView comment_txt;
    EditText comment_edittext;
    DatabaseReference dbRefs;
    List<Comment> list;
    FirebaseFirestore firestore;

    private RecyclerView recyclerView;
    private UserCommentAdapter userCommentAdapter;
    private CollectionReference collectionReference;
    FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.comment_layout);
        initViews();
        list = new ArrayList<>();
        dbRefs = FirebaseDatabase.getInstance().getReference("Comments");

        Intent intent = getIntent();
        postBy = intent.getStringExtra("PostBy");
        publisherID = intent.getStringExtra("PublisherId");
        postId = intent.getStringExtra("PostId");
        comment_txt.setText("Comments from "+postBy+"'s post.");

        auth = FirebaseAuth.getInstance();

        cmtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] fullname = new String[1];
                if(!comment_edittext.getText().toString().equals("")){
                    final String comment = comment_edittext.getText().toString();
                    com.google.firebase.database.Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("user_id").equalTo(auth.getCurrentUser().getUid());

                    final String d_date = (String) DateFormat.format("MMMM dd, yyyy", new Date());
                    final String d_time = (String) DateFormat.format("hh:mm:ss a", new Date());
                    final String date_time = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());


                    query.addValueEventListener(new ValueEventListener() {
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

                    Comment comment1 = new Comment(publisherID, postId, comment, auth.getUid(), fullname[0], d_date, d_time,date_time);
                    String id = dbRefs.push().getKey();
                    dbRefs.child(postId).child(id).setValue(comment1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Commented Successfully", Toast.LENGTH_SHORT).show();
                            comment_edittext.setText("");
                            setupRecycleView();
                            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            commentNotification("comment", user_id, postId, publisherID);
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter your comment message!", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(getApplicationContext(), "Ehm", Toast.LENGTH_SHORT).show();
            }
        });
        setupRecycleView();
    }

    public void initViews(){
        comment_edittext = findViewById(R.id.comment);
        cmtBtn = findViewById(R.id.sendBtn);
        firestore = FirebaseFirestore.getInstance();
        comment_txt = findViewById(R.id.comment_txt);
        recyclerView = findViewById(R.id.recyclerview);
    }
// FOR TESTING //

/////////////////
    public String addComment(String cmt_publisher, String cmt_msg){

        if(!cmt_publisher.equals("") && !cmt_msg.equals("")){
            return "Comment Submitted.";
        }else{
            return "Comment not submitted.";
        }

    }

    public void setupRecycleView(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Query query = FirebaseDatabase.getInstance().getReference("Comments").child(postId).orderByChild("comment_date_time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                for(DataSnapshot snapshot :  dataSnapshot.getChildren()) {

                    Comment event = snapshot.getValue(Comment.class);
                    if(event.getPost_id().equals(postId)){
                        list.add(event);
                    }
                }
                userCommentAdapter = new UserCommentAdapter(getApplicationContext(), list);
                recyclerView.setAdapter(userCommentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(){
        DatabaseReference dbRefs = FirebaseDatabase.getInstance().getReference("Notifications");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String, Object> map = new HashMap<>();
        map.put("notification_type", "comment");
        map.put("user_id", user.getUid());
        map.put("text", " commented on your post.");
        map.put("post_id", postId);
        String id = dbRefs.push().getKey();
        dbRefs.child(user.getUid()).child(id).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void commentNotification(String type, String user_id, String post_id, String pub_id){
        final String d_date = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());
        com.example.trapic_test.Model.Notification notif = new Notification(type, user_id, post_id, pub_id, d_date);
        String push_id = FirebaseDatabase.getInstance().getReference("Notifications").push().getKey();

        FirebaseDatabase.getInstance().getReference("Notifications").child(push_id).setValue(notif);
    }

}
