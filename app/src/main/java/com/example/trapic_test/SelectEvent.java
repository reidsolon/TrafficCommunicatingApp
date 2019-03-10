package com.example.trapic_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;



public class SelectEvent extends AppCompatActivity{

    LinearLayout cat1, cat2, cat3, cat4;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.selectevent_layout);

        initViews();

        onClicks();
    }

    public void initViews(){
        cat1 = (LinearLayout) findViewById(R.id.cat_1);
        cat2 = (LinearLayout) findViewById(R.id.cat_2);
        cat3 = (LinearLayout) findViewById(R.id.cat_3);
        cat4 = (LinearLayout) findViewById(R.id.cat_4);
    }

    public void onClicks(){
        cat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("Category", "Category1_test");
                startActivity(intent);
            }
        });

        cat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("Category", "Category2_test");
                startActivity(intent);
            }
        });

        cat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("Category", "Category3_test");
            }
        });

        cat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("Category", "Category4_test");
                startActivity(intent);
            }
        });
    }

}
