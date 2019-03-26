package com.example.trapic_test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.*;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SendFeedbackActivity extends Activity {
    private TextView msg;
    private String msg_txt;
    private Button sendBtn;
    private ProgressDialog dialog;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_feedback_layout);

        initViews();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg_txt = msg.getText().toString();

                dialog.show();
                sendFeedback();


            }
        });

    }

    private void initViews(){
        msg = findViewById(R.id.feedback_msg);
        sendBtn = findViewById(R.id.send_btn);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Sending");
        dialog.setMessage("Sending your feedback..");

    }


    private void sendFeedback(){
        DatabaseReference dbRefs = FirebaseDatabase.getInstance().getReference("Feedbacks");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = dbRefs.push().getKey();
        java.util.Date d = new java.util.Date();
        final String d_date = (String) DateFormat.format("MMMM d, yyyy", d.getDate());
        java.util.Date time = new Date();
        final String d_time = (String) DateFormat.format("hh:mm:ss a", time.getTime());
        HashMap<String, Object> map = new HashMap<>();
        map.put("feedback_sent_by", user.getUid());
        map.put("feedback_msg", msg_txt);
        map.put("sent_date", d_date);
        map.put("sent_time", d_time);
        dbRefs.child(user.getUid()).child(id).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        msg.setText("");
                    }
                }, 2500);

            }
        });
    }
}
