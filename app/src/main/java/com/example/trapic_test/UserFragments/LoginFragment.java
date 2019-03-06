package com.example.trapic_test.UserFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trapic_test.MainActivity;
import com.example.trapic_test.MainFragment;
import com.example.trapic_test.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginFragment extends AppCompatActivity {
    Button logBtn;
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

        pw = (EditText) findViewById(R.id.log_pw);
        email = (EditText) findViewById(R.id.log_email);

        dbRef = FirebaseDatabase.getInstance().getReference("User");
        dialog = new ProgressDialog(LoginFragment.this);
        dialog.setMessage("Logging in your account...");
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                dialog.show();
                loginValidation();
                checkUser();

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

                        finish();
                        startActivity(new Intent(getApplicationContext(), MainFragment.class));

                    }else{
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Login failed" ,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            dialog.dismiss();
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
}
