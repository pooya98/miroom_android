package com.example.miroom;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.miroom.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Reservation_cancel extends AppCompatActivity {
    String room_name;
    String room_check;
    String room_layer;
    String room_start_time;
    String room_end_time;
    String room_date;
    String room_util;
    String room_size;
    String room_capacity;
    String room_id;
    String room_person;
    String room_reserv_number;

    String user_id;
    String user_name;
    String user_phone;

    TextView TV_room_name, TV_room_size, TV_room_util, TV_reserv_date, TV_reserv_time, TV_reserver;
    TextView TV_reserv_number;


    String reserv_id;


    boolean delete_success = false;

    private SharedPreferences appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_cancel);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TV_room_name = (TextView)findViewById(R.id.TV_room_name);
        TV_room_size = (TextView)findViewById(R.id.TV_room_size);
        TV_room_util = (TextView)findViewById(R.id.TV_room_util);

        TV_reserv_date = (TextView)findViewById(R.id.TV_reserv_date);
        TV_reserv_time = (TextView)findViewById(R.id.TV_reserv_time);
        TV_reserver = (TextView)findViewById(R.id.TV_reserver);
        TV_reserv_number = (TextView)findViewById(R.id.TV_reserve_num);

        Intent intent = getIntent();
        reserv_id = intent.getStringExtra("reserv_id");

        appData = getSharedPreferences("appData", MODE_PRIVATE);


        Thread thread = new Thread() {
            public void run() {
                contact_server_get_reservation_info();
            }
        };

        thread.start();
        System.out.println("--- loadThread go!");

        try {
            thread.join();
            System.out.println("--- loadThread done!");
        } catch (Exception e) {
            e.getMessage();
        }

        TV_room_name.setText("["+room_layer+"] "+room_name);
        TV_reserv_date.setText(room_date);
        TV_reserv_time.setText(room_start_time.substring(0,5)+" - "+room_end_time.substring(0,5));

        user_id = appData.getString("User_ID", "");

        TV_room_size.setText(room_size+" ("+room_capacity+"인실)");
        TV_room_util.setText(room_util);

        TV_reserver.setText(user_name+" ("+user_id+")");

        TV_reserv_number.setText(room_reserv_number);

        if(getSupportActionBar() != null) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle("RESERVATION_CANCEL");
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




    void contact_server_get_reservation_info(){

        HttpPost httppost;
        HttpResponse response;
        HttpClient httpClient;
        List<NameValuePair> nameValuePairs;
        HttpGet httpget;

        System.out.println("--- 스레드 시작");

        try{
            httpClient = new DefaultHttpClient();
            httpget = new HttpGet("http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/get_reservation_info.php?reserv_id="+reserv_id);

            response = httpClient.execute(httpget);

            //
            Scanner scan = new Scanner(response.getEntity().getContent());

            String response_string = "";
            while(scan.hasNext()){
                response_string += scan.nextLine();
            }

            System.out.println("ddddd"+ response_string);

            String[] token;
            token = response_string.split(" ");

            if(token[0].equals("")){

            }
            else {
                room_name = token[0];
                room_capacity = token[1];
                room_size = token[2];
                room_util = token[3];
                room_date = token[4];
                room_start_time = token[5];
                room_end_time = token[7];
                user_name = token[8];
                room_person = token[9];
                room_layer = token[10];
                room_reserv_number = token[11];
            }

        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }

    void contact_server_delete_reservation(){

        HttpPost httppost;
        HttpResponse response;
        HttpClient httpClient;
        List<NameValuePair> nameValuePairs;
        HttpGet httpget;

        System.out.println("--- 스레드 시작");

        try{
            httpClient = new DefaultHttpClient();
            httpget = new HttpGet("http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/delete_reservation.php"
                    +"?reserv_id=" +reserv_id);

            response = httpClient.execute(httpget);

            //
            Scanner scan = new Scanner(response.getEntity().getContent());

            String response_string = "";
            while(scan.hasNext()){
                response_string += scan.nextLine();
            }

            if(response_string.equals("T")){
                delete_success = true;
            }else{
                delete_success = false;
            }



        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }


    public void showAlert_delete_success() {
        Reservation_cancel.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Reservation_cancel.this);
                builder.setTitle(" ");
                builder.setMessage("예약이 성공적으로 취소되었습니다.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Reservation_cancel.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void showAlert_delete_fail() {
        Reservation_cancel.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Reservation_cancel.this);
                builder.setTitle(" ");
                builder.setMessage("예약 취소를 실패했습니다.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Reservation_cancel.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void onclick_delete_reservation(View view) {
        Thread thread5 = new Thread() {
            public void run() {
                contact_server_delete_reservation();
            }
        };

        thread5.start();
        System.out.println("--- loadThread go!");

        try {
            thread5.join();
            System.out.println("--- loadThread done!");
        } catch (Exception e) {
            e.getMessage();
        }

        if(delete_success == true){
            showAlert_delete_success();
        }else{
            showAlert_delete_fail();
        }
    }


}