package com.example.trapic_test;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.trapic_test.Model.Comment;
import com.example.trapic_test.Model.Event;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {

    ImageView cmtBtn;
    String postId, postBy, publisherID;
    TextView comment_txt;
    EditText comment_edittext;
    FirebaseFirestore firestore;

    private RecyclerView recyclerView;
    private CommentAdapter feedAdapter;
    private CollectionReference collectionReference;
    FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.comment_layout);
        initViews();

        Intent intent = getIntent();
        postBy = intent.getStringExtra("PostBy");
        publisherID = intent.getStringExtra("PublisherId");
        postId = intent.getStringExtra("PostId");
        comment_txt.setText("Comments from "+postBy+"'s post.");

        auth = FirebaseAuth.getInstance();

        cmtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!comment_edittext.getText().toString().equals("")){
                    addComment();
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter your comment message!", Toast.LENGTH_LONG).show();
                }
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

    public void addComment(){
        final String comment = comment_edittext.getText().toString();
        final DocumentReference documentReference = firestore.collection("Users").document(auth.getUid());
        Date d = new Date();
        final String d_date = (String) DateFormat.format("MMMM d, yyyy", d.getDate());
        Date time = new Date();
        final String d_time = (String) DateFormat.format("hh:mm:ss a", time.getTime());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String fullname = documentSnapshot.getString("user_firstname")+" "+documentSnapshot.getString("user_lastname");
                Comment comment1 = new Comment(publisherID, postId, comment, auth.getUid(), fullname, d_date, d_time);
                String id = firestore.collection("Comments").document().getId();
                firestore.collection("Comments").document(id).set(comment1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Commented Successfully", Toast.LENGTH_SHORT).show();
                        comment_edittext.setText("");
                    }
                });
            }
        });

    }

    public void setupRecycleView(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        collectionReference = firestore.collection("Comments");

        Query query;
        query = collectionReference.whereEqualTo("post_id", postId);
        query.orderBy("comment_time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment.class).build();
        feedAdapter = new CommentAdapter(options, getApplicationContext());

        recyclerView.setAdapter(feedAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        feedAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedAdapter.stopListening();
    }
}
