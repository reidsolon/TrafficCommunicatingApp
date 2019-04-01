package com.example.trapic_test.UserFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trapic_test.MainFragment;
import com.example.trapic_test.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;

public class GuestActivity extends Activity {

    private Button regBtn ,logBtn, guestBtn;
    private TextView guestName;
    private ProgressDialog dialog;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.guest_layout);
        initViews();

        guestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGuest();
            }
        });
    }

    private void initViews(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Proceeding.");
        guestName = findViewById(R.id.guest_name);
        regBtn = findViewById(R.id.reg_btn_link);
        logBtn = findViewById(R.id.login_btn_link);
        guestBtn = findViewById(R.id.proceed_btn);
    }

    private void addGuest(){
        dialog.show();
        if(!guestName.getText().toString().equals("")){

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Guests");

            final String guest_name = guestName.getText().toString().trim();
            String id = databaseReference.push().getKey();
            final String d_date = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());
            HashMap map = new HashMap();
            map.put("guest_id", id);
            map.put("guest_name", "Guest#"+guest_name);
            map.put("guest_date_time", d_date);

            databaseReference.child(id).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 1500);

                    guestName.setText("");
                    Toast.makeText(getApplicationContext(), "Wait a minute...", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(GuestActivity.this, MainFragment.class);
                    intent.putExtra("Guest_name", guest_name);
                    startActivity(intent);
                }
            });

        }else{
            guestName.setError("Please enter/specify your nickname.");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 1000);
        }
    }
}
