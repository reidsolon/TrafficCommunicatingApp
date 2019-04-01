package com.example.trapic_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivation extends AppCompatActivity {
    TextView email_txt;
    private Button cont_btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activation);

        email_txt = (TextView) findViewById(R.id.email_txt);
        cont_btn = findViewById(R.id.continue_btn);
        String email_txt_txt = getIntent().getStringExtra("Email");
        email_txt.setText("We've sent it here: "+email_txt_txt);

        initClicks();
    }

    private void initClicks(){
        cont_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(AccountActivation.this, MainFragment.class));
            }
        });
    }

    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Back is disabled.", Toast.LENGTH_SHORT).show();
    }
}
