package com.example.miroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.miroom.R;
import com.example.miroom.Reservate;
import com.example.miroom.Reservation_check;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, com.example.miroom.Loading.class);
        startActivity(intent);

    }

    public void onclick_reservation(View view) {
        Intent My_intent = new Intent(MainActivity.this, Reservate.class);
        startActivity(My_intent);
    }

    public void onclick_Lookup(View view) {
        Intent My_intent = new Intent(MainActivity.this, Reservation_check.class);
        startActivity(My_intent);
    }
}