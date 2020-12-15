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

public class Book_page extends AppCompatActivity {
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

    String user_id;
    String user_name;
    String user_phone;

    TextView TV_room_name, TV_room_size, TV_room_util, TV_reserv_date, TV_reserv_time, TV_reserver;
    EditText TV_reserv_number;


    String other_user_id;
    String other_user_name;
    String other_start_time;
    String other_end_time;
    String other_reserv_number;

    private SharedPreferences appData;

    boolean insert_success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        appData = getSharedPreferences("appData", MODE_PRIVATE);

        Intent intent = getIntent();
        room_name = intent.getStringExtra("room_name");
        room_check = intent.getStringExtra("room_check");
        room_layer = intent.getStringExtra("room_layer");
        room_start_time = intent.getStringExtra("room_start_time");
        room_end_time = intent.getStringExtra("room_end_time");
        room_date = intent.getStringExtra("room_date");

        if(room_check.equals("예약가능")){
            setContentView(R.layout.activity_book_page);
            TV_room_name = (TextView)findViewById(R.id.TV_room_name);
            TV_room_size = (TextView)findViewById(R.id.TV_room_size);
            TV_room_util = (TextView)findViewById(R.id.TV_room_util);

            TV_reserv_date = (TextView)findViewById(R.id.TV_reserv_date);
            TV_reserv_time = (TextView)findViewById(R.id.TV_reserv_time);
            TV_reserver = (TextView)findViewById(R.id.TV_reserver);
            TV_reserv_number = (EditText)findViewById(R.id.TV_reserve_num);

            TV_room_name.setText("["+room_layer+"] "+room_name);
            TV_reserv_date.setText(room_date);
            TV_reserv_time.setText(room_start_time.substring(0,5)+" - "+room_end_time.substring(0,5));


            user_id = appData.getString("User_ID", "");

            Thread thread = new Thread() {
                public void run() {
                    contact_server_get_room_info();
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

            TV_room_size.setText(room_size+" ("+room_capacity+"인실)");
            TV_room_util.setText(room_util);


            Thread thread2 = new Thread() {
                public void run() {
                    contact_server_get_user_info();
                }
            };

            thread2.start();
            System.out.println("--- loadThread go!");

            try {
                thread2.join();
                System.out.println("--- loadThread done!");
            } catch (Exception e) {
                e.getMessage();
            }

            TV_reserver.setText(user_name+" ("+user_id+")");

        }else{
            setContentView(R.layout.activity_book_page_unavail);

            TV_room_name = (TextView)findViewById(R.id.TV_room_name);
            TV_room_size = (TextView)findViewById(R.id.TV_room_size);
            TV_room_util = (TextView)findViewById(R.id.TV_room_util);

            TV_reserv_date = (TextView)findViewById(R.id.TV_reserv_date);
            TV_reserv_time = (TextView)findViewById(R.id.TV_reserv_time);
            TV_reserver = (TextView)findViewById(R.id.TV_reserver);
            TV_reserv_number = (EditText)findViewById(R.id.TV_reserve_num);

            TV_room_name.setText("["+room_layer+"] "+room_name);
            TV_reserv_date.setText(room_date);

            Thread thread = new Thread() {
                public void run() {
                    contact_server_get_room_info();
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

            TV_room_size.setText(room_size+" ("+room_capacity+"인실)");
            TV_room_util.setText(room_util);


            Thread thread4 = new Thread() {
                public void run() {
                    contact_server_get_your_reserv();
                }
            };

            thread4.start();
            System.out.println("--- loadThread go!");

            try {
                thread4.join();
                System.out.println("--- loadThread done!");
            } catch (Exception e) {
                e.getMessage();
            }

            if(other_user_name.length() >2) {
                TV_reserver.setText(other_user_name.substring(0, 1) + "O" + other_user_name.substring(2));
            }else{
                TV_reserver.setText(other_user_name.substring(0, 1) + "O");
            }

            TV_reserv_number.setText(other_reserv_number);
            TV_reserv_time.setText(other_start_time.substring(0,5)+" - "+other_end_time.substring(0,5));
        }




        if(getSupportActionBar() != null) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle("RESERVATION");
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

    void contact_server_get_your_reserv(){

        HttpPost httppost;
        HttpResponse response;
        HttpClient httpClient;
        List<NameValuePair> nameValuePairs;
        HttpGet httpget;

        System.out.println("--- 스레드 시작");

        try{
            httpClient = new DefaultHttpClient();
            httpget = new HttpGet("http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/get_your_reserv.php"
                    +"?reserv_start_time="+room_start_time+"&reserv_end_time="+room_end_time+"&room_id="+room_id+"&date="+room_date);

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
                other_start_time = token[1];
                other_end_time = token[3];
                other_user_name = token[4];
                other_reserv_number = token[5];
            }

        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }

    void contact_server_get_room_info(){

        HttpPost httppost;
        HttpResponse response;
        HttpClient httpClient;
        List<NameValuePair> nameValuePairs;
        HttpGet httpget;

        System.out.println("--- 스레드 시작");

        try{
            httpClient = new DefaultHttpClient();
            httpget = new HttpGet("http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/get_room_info.php?room_name="+room_name+"&room_layer="+room_layer);

            response = httpClient.execute(httpget);

            //
            Scanner scan = new Scanner(response.getEntity().getContent());

            String response_string = "";
            while(scan.hasNext()){
                response_string += scan.nextLine();
            }

            String[] token;
            token = response_string.split(" ");

            if(token[0].equals("")){

            }
            else {
                room_size = token[0];
                room_capacity = token[1];
                room_util = token[2];
                room_id = token[3];
            }

        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }

    void contact_server_get_user_info(){

        HttpPost httppost;
        HttpResponse response;
        HttpClient httpClient;
        List<NameValuePair> nameValuePairs;
        HttpGet httpget;

        System.out.println("--- 스레드 시작");

        try{
            httpClient = new DefaultHttpClient();
            httpget = new HttpGet("http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/get_user_info.php?account="+user_id);

            response = httpClient.execute(httpget);

            //
            Scanner scan = new Scanner(response.getEntity().getContent());

            String response_string = "";
            while(scan.hasNext()){
                response_string += scan.nextLine();
            }

            String[] token;
            token = response_string.split(" ");

            if(token[0].equals("")){

            }
            else {
                user_name = token[0];
                user_phone = token[1];
            }

        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }

    void contact_server_Insert_reservation(){

        HttpPost httppost;
        HttpResponse response;
        HttpClient httpClient;
        List<NameValuePair> nameValuePairs;
        HttpGet httpget;

        System.out.println("--- 스레드 시작");

        try{
            httpClient = new DefaultHttpClient();
            httpget = new HttpGet("http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/insert_reservation.php"
                    +"?user_name=" +user_name+"&user_phone="+user_phone+"&reserv_date="+room_date+"&reserv_start_time="
                    +room_start_time+"&reserv_end_time="+room_end_time+"&reserv_number="+TV_reserv_number.getText().toString()
                    +"&room_id="+room_id);

            response = httpClient.execute(httpget);

            //
            Scanner scan = new Scanner(response.getEntity().getContent());

            String response_string = "";
            while(scan.hasNext()){
                response_string += scan.nextLine();
            }

            if(response_string.equals("T")){
                insert_success = true;
            }else{
                insert_success = false;
            }



        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }

    public void onclick_Insert_reservation(View view) {
        if(TV_reserv_number.getText().toString().replaceAll(" ","").equals("")){
            showAlert_insert_number();
        }
        else{


            Thread thread3 = new Thread() {
                public void run() {
                    contact_server_Insert_reservation();
                }
            };

            thread3.start();
            System.out.println("--- loadThread go!");

            try {
                thread3.join();
                System.out.println("--- loadThread done!");
            } catch (Exception e) {
                e.getMessage();
            }

            if(insert_success == true){
                showAlert_insert_success();
            }else{
                showAlert_insert_fail();
            }

        }
    }

    public void showAlert_insert_number() {
        Book_page.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Book_page.this);
                builder.setTitle(" ");
                builder.setMessage("예약 인원을 입력해주세요.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void showAlert_insert_success() {
        Book_page.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Book_page.this);
                builder.setTitle(" ");
                builder.setMessage("예약이 성공적으로 완료되었습니다.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Book_page.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void showAlert_insert_fail() {
        Book_page.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Book_page.this);
                builder.setTitle(" ");
                builder.setMessage("다른 사람에 의해 예약 되었습니다.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Book_page.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

}