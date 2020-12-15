package com.example.miroom;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

import com.example.miroom.R;

public class Book_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if(getSupportActionBar() != null) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle("RESERVATION");
            ab.setDisplayHomeAsUpEnabled(true);
        }

        //Intent intent = getIntent();
        //String Title = intent.getStringExtra("parameter_title");


        //TextView title = (TextView)findViewById(R.id.title);
        //title.setText(Title+" 예약 페이지"+"\n(구현예정)");
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}