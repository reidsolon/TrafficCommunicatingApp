package com.example.trapic_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {

    ImageView cmtBtn;
    String postId, postBy, publisherID;
    TextView comment_txt;
    EditText comment_edittext;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.comment_layout);
        initViews();

        comment_txt = findViewById(R.id.comment_txt);
        Intent intent = getIntent();
        postBy = intent.getStringExtra("PostBy");
        publisherID = intent.getStringExtra("PublisherId");
        postId = intent.getStringExtra("PostId");
        comment_txt.setText("Comments from "+postBy+"'s post.");

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
    }

    public void initViews(){
        comment_edittext = findViewById(R.id.comment);
        cmtBtn = findViewById(R.id.sendBtn);
        firestore = FirebaseFirestore.getInstance();
    }

    public void addComment(){
        String comment = comment_edittext.getText().toString();
        HashMap<String, Object> map = new HashMap<>();
        map.put("UserId", publisherID);
        map.put("PostId", postId);
        map.put("Comment_msg", comment);
        firestore.collection("Comments").document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Commented Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
