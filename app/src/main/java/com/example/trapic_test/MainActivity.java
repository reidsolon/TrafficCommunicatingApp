package com.example.trapic_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.trapic_test.UserFragments.GuestActivity;
import com.example.trapic_test.UserFragments.LoginFragment;
import com.example.trapic_test.UserFragments.RegisterFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button regBtn, logBtn, guestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, MainFragment.class));
        }

        regBtn = findViewById(R.id.register_link);
        logBtn = findViewById(R.id.login_link);
        guestBtn = findViewById(R.id.guest_btn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this, RegisterFragment.class));
            }
        });

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this, LoginFragment.class));
            }
        });

        guestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this, GuestActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Back btn is disabled", Toast.LENGTH_SHORT).show();
    }
}
