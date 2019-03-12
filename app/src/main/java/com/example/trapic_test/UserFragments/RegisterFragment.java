package com.example.trapic_test.UserFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.trapic_test.AccountActivation;
import com.example.trapic_test.Model.User;
import com.example.trapic_test.MainFragment;
import com.example.trapic_test.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

public class RegisterFragment extends AppCompatActivity {
    ProgressDialog progressBar;
    DatabaseReference dbRef;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    EditText uname, fname, lname, pw1, pw2, email;
    LinearLayout layout;
    Button reg_btn, log_btn;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FirebaseApp.initializeApp(RegisterFragment.this);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerfragment);
        initViews();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(RegisterFragment.this, MainFragment.class));
        }

        progressBar = new ProgressDialog(RegisterFragment.this);
        progressBar.setTitle("Creating your account");
        progressBar.setMessage("Registering User...");

        dbRef = FirebaseDatabase.getInstance().getReference("User");

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.show();
                addUser();

            }
        });

        log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                startActivity(new Intent(getApplicationContext(), LoginFragment.class));

            }
        });
    }

    public void initViews(){

        fname = (EditText) findViewById(R.id.reg_fname);
        lname = (EditText) findViewById(R.id.reg_lname);
        pw1 = (EditText) findViewById(R.id.reg_pw1);
        pw2 = (EditText) findViewById(R.id.reg_pw2);
        email = (EditText) findViewById(R.id.reg_email);
        log_btn = (Button) findViewById(R.id.login_link);
        reg_btn = (Button) findViewById(R.id.reg_btn);

    }

    public boolean addUser(){

        boolean validate = regValidation();
        final String fname_reg = fname.getText().toString();
        final String lname_reg = lname.getText().toString();
        final String email_reg = email.getText().toString();
        final String pw1_reg = pw1.getText().toString();

        if(validate) {

            firebaseAuth.createUserWithEmailAndPassword(email_reg, pw1_reg).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        try
                        {
                            throw task.getException();
                        }
                        // if user enters wrong email.
                        catch (FirebaseAuthWeakPasswordException weakPassword)
                        {
                            Log.d("RegisterFragment", "onComplete: weak_password");
                            Toast.makeText(getApplicationContext(), "Weak Password!" , Toast.LENGTH_LONG).show();

                            // TODO: take your actions!
                        }
                        // if user enters wrong password.
                        catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                        {
                            Log.d("RegisterFragment", "onComplete: malformed_email");
                            Toast.makeText(getApplicationContext(), "Please enter valid email address!" , Toast.LENGTH_LONG).show();

                            // TODO: Take your action
                        }
                        catch (FirebaseAuthUserCollisionException existEmail)
                        {
                            Log.d("RegisterFragment", "onComplete: exist_email");

                            // TODO: Take your action
                            Toast.makeText(getApplicationContext(), "Email already exist!" , Toast.LENGTH_LONG).show();
                        }
                        catch (Exception e)
                        {
                            Log.d("RegisterFragment", "onComplete: " + e.getMessage());
                        }
                        String id = firebaseAuth.getUid();
                        user = new User(id, fname_reg, lname_reg, pw1_reg, email_reg);
                        firestore.collection("Users").document(id).set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Account Registered Successfully", Toast.LENGTH_LONG).show();
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        user.sendEmailVerification();
                                        Intent activation = new Intent(RegisterFragment.this, AccountActivation.class);
                                        activation.putExtra("Email" , email.getText().toString());
                                        finish();
                                        startActivity(activation);
                                        progressBar.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


                    } else {
                        progressBar.dismiss();
                        Toast.makeText(RegisterFragment.this, "Not added!", Toast.LENGTH_LONG).show();
                    }
                }
            });

            return true;
        }else{

            progressBar.dismiss();
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();


            return false;
        }
    }

    public boolean regValidation(){

        String fname_reg = fname.getText().toString();
        String lname_reg = lname.getText().toString();
        String email_reg = email.getText().toString();
        String pw1_reg = pw1.getText().toString();
        String pw2_reg = pw2.getText().toString();

        boolean fname;
        boolean lname;
        boolean email;
        boolean pw;


        if(TextUtils.isEmpty(fname_reg)){
            this.fname.setError("Please enter your firstname");
            fname = false;
        }else{
            fname = true;
        }

        if(TextUtils.isEmpty(lname_reg)){
            this.lname.setError("Please enter yout lastname");
            lname = false;
        }else{
            lname = true;
        }

        if(TextUtils.isEmpty(email_reg)){
            this.email.setError("Please enter valid email address");
            email = false;
        }else{
            email = true;
        }

        if (!TextUtils.isEmpty(pw1_reg) && !TextUtils.isEmpty(pw2_reg)){
            if(pw1_reg.equals(pw2_reg)){

                if(!PASSWORD_PATTERN.matcher(pw1_reg).matches()){
                    pw1.setError("Password too weak");
                    pw = false;
                }else{
                    pw = true;
                }
            }else{
                pw1.setError("Password not matched");
                pw2.setError("Password not matched");

                pw = false;
            }
        }else{
            pw1.setError("Please enter password");
            pw2.setError("Please enter password");

            pw = false;
        }

        if(fname && lname && email && pw){
            return true;
        }else{
            return false;
        }
    }
}

