package com.example.miroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void onclick_reservation(View view) {
        Intent My_intent = new Intent(MainActivity.this, Reservate.class);
        startActivity(My_intent);
    }

    public void onclick_Lookup(View view) {
        Intent My_intent = new Intent(MainActivity.this, Reservation_check_result.class);
        startActivity(My_intent);
    }
}