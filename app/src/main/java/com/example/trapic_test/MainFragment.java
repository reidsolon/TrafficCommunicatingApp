package com.example.trapic_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trapic_test.Database.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainFragment extends AppCompatActivity {
    TextView test;
    Button logout;
    FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(MainFragment.this, MainActivity.class));
        }
        setContentView(R.layout.mainfragment_layout);

        test = (TextView) findViewById(R.id.user_Fullname);
        String fullname = user.getDisplayName();
        test.setText(fullname);

        logout = (Button) findViewById(R.id.logout_btn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}
