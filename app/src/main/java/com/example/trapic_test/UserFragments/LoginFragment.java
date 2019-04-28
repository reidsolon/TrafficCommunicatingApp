package com.example.trapic_test.UserFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trapic_test.MainActivity;
import com.example.trapic_test.MainFragment;
import com.example.trapic_test.Model.Log;
import com.example.trapic_test.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

public class LoginFragment extends AppCompatActivity {
    Button logBtn, regBtn, guestBtn;
    EditText pw, email;
    FirebaseAuth auth;
    ProgressDialog dialog;
    DatabaseReference dbRef;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(getApplicationContext());

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainFragment.class));
        }
        setContentView(R.layout.loginfragment);

        logBtn = (Button) findViewById(R.id.log_btn);
        regBtn = (Button) findViewById(R.id.register_link);
        pw = (EditText) findViewById(R.id.log_pw);
        email = (EditText) findViewById(R.id.log_email);
        guestBtn = findViewById(R.id.guest_btn);

        dbRef = FirebaseDatabase.getInstance().getReference("User");
        dialog = new ProgressDialog(LoginFragment.this);
        dialog.setTitle("Logging in");
        dialog.setMessage("Checking your account...");
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                loginValidation();
                checkUser();

            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), RegisterFragment.class));
            }
        });

        guestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GuestActivity.class));
            }
        });

    }

    public boolean checkUser(){

        boolean loginValidation = loginValidation();
        String email_txt = email.getText().toString();
        String pw_txt = pw.getText().toString();

        if(loginValidation){
            auth.signInWithEmailAndPassword(email_txt, pw_txt).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        dialog.dismiss();
                        final String date_time = (String) DateFormat.format("MMMM dd, yyyy hh:mm:ss a", new Date());

                        com.example.trapic_test.Model.Log log = new Log(FirebaseAuth.getInstance().getCurrentUser().getUid(), " just logged in.", date_time);
                        DatabaseReference dbRefs = FirebaseDatabase.getInstance().getReference();


                        Task<Void> databaseReference = FirebaseDatabase.getInstance().getReference("Logs")
                                .child(dbRefs.push().getKey()).setValue(log).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainFragment.class));

                    }else{
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Login failed" ,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 1000);

            Toast.makeText(getApplicationContext(), "Login failed" ,Toast.LENGTH_LONG).show();
        }

        return false;
    }

    public boolean loginValidation(){

        String email_txt = email.getText().toString();
        String pw_txt = pw.getText().toString();

        boolean check_email;
        boolean check_pw;

        if(TextUtils.isEmpty(email_txt)){
            email.setError("Please enter email address");
            check_email = false;
        }else{
            check_email = true;
        }

        if(TextUtils.isEmpty(pw_txt)){
            pw.setError("Please enter your password");
            check_pw = false;
        }else{
            check_pw = true;
        }

        if(check_email && check_pw){
            return true;
        }

        return false;
    }
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Back is disabled.", Toast.LENGTH_SHORT).show();
    }
}
