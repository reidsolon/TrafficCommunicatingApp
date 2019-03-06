package com.example.trapic_test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AccountActivation extends AppCompatActivity {
    TextView email_txt;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activation);

        email_txt = (TextView) findViewById(R.id.email_txt);
        String email_txt_txt = getIntent().getStringExtra("Email");
        email_txt.setText("We've sent it here: "+email_txt_txt);
    }
}
