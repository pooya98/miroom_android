package com.example.miroom;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Reservation_check extends AppCompatActivity {

    EditText EditText_name;
    EditText EditText_phone_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_check);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        EditText_name = (EditText)findViewById(R.id.name);
        EditText_phone_num = (EditText)findViewById(R.id.phone_num);

        if(getSupportActionBar() != null) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle("RESERVATION CHECK");
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onclick_search_reservation(View view) {
        Intent myintent = new Intent(Reservation_check.this, Reservation_check_result.class);

        String user_name = EditText_name.getText().toString();
        String user_phone_num = EditText_phone_num.getText().toString();

        myintent.putExtra("parameter_user_name", user_name);
        myintent.putExtra("parameter_user_phone_num", user_phone_num);
        startActivity(myintent);
    }
}