package com.example.miroom;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Reservation_check_result extends AppCompatActivity {

    ListView listView;
    TextView textView_no_result;
    boolean no_result_flag=false;
    //ArrayList<String> aryList_reserv_num = new ArrayList<String>();
    //ArrayList<String> aryList_date = new ArrayList<String>();
    //ArrayList<String> aryList_time = new ArrayList<String>();
    //ArrayList<String> aryList_room_name = new ArrayList<String>();

    //ArrayAdapter<String> adapter_reserv_num;
   // ArrayAdapter<String> adapter_date;
    //ArrayAdapter<String> adapter_time;
    //ArrayAdapter<String> adapter_room_name;

    ArrayList<reservationData> reservationDataList;


    HttpResponse response;
    HttpClient httpClient;
    HttpGet httpget;

    String user_name;
    String user_phone_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_check_result);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if(getSupportActionBar() != null) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle("RESERVATION CHECK");
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        user_name = intent.getStringExtra("parameter_user_name");
        user_phone_num = intent.getStringExtra("parameter_user_phone_num");


        listView = (ListView)findViewById(R.id.listView_check_result);
        textView_no_result = (TextView)findViewById(R.id.no_result);




        /* 스레드 시작 */
        Thread loadThread = new Thread(){
            public void run(){
                contact_server_for_search();
            }
        };

        loadThread.start();
        System.out.println("--- loadThread go!");

        try{
            loadThread.join();
            System.out.println("--- loadThread done!");
        }catch(Exception e){
            e.getMessage();
        }

        if(no_result_flag == false){
            textView_no_result.setVisibility(View.INVISIBLE);
        }

        final reservationAdapter myAdapter = new reservationAdapter(this,reservationDataList);

        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Toast.makeText(getApplicationContext(),
                        myAdapter.getItem(position).getReserve_id(),
                        Toast.LENGTH_LONG).show();
            }
        });



        //listView.setOnItemClickListener(listener);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
/*
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Toast.makeText(Reservation_check_result.this, aryList_reserv_num.get(position), Toast.LENGTH_SHORT).show();
            Intent My_intent = new Intent(Reservation_check_result.this, com.example.miroom.Book_page.class);
            My_intent.putExtra("parameter_reserve_id", aryList_reserv_num.get(position));
            startActivity(My_intent);
        }
    };*/

    void contact_server_for_search(){
        System.out.println("--- 스레드 시작");

        try{

            httpClient = new DefaultHttpClient();
            httpget = new HttpGet("http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/search_my_reserv.php?user_name=%27"+ user_name+"%27&user_phone=%27"+user_phone_num+"%27");

            response = httpClient.execute(httpget);

            //
            Scanner scan = new Scanner(response.getEntity().getContent());

            String response_string = "";
            while(scan.hasNext()){
                response_string += scan.nextLine();
            }

            System.out.println("---Response : "+response_string);

            String[] token;
            token = response_string.split(" ");

            reservationDataList = new ArrayList<reservationData>();

            if(token[0].equals("")){
                no_result_flag = true;
            }
            else {
                for (int i = 0; i < (token.length)/7; i++) {

                    String temp = "";
                    System.out.println("token " + i*7 + " : " + token[i*7]);
                    System.out.println("token " + i*7+1 + " : " + token[i*7+1]);
                    System.out.println("token " + i*7+2 + " : " + token[i*7+2]);
                    System.out.println("token " + i*7+3 + " : " + token[i*7+3]);
                    System.out.println("token " + i*7+4 + " : " + token[i*7+4]);
                    System.out.println("token " + i*7+5 + " : " + token[i*7+5]);
                    System.out.println("token " + i*7+6 + " : " + token[i*7+6]);

                    token[i*7+1] = token[i*7+1].substring(5);
                    token[i*7+2] = token[i*7+2].substring(0,5);
                    token[i*7+4] = token[i*7+4].substring(0,5);

                    temp = token[i*7+2]+"~"+token[i*7+4];


                    //aryList_reserv_num.add(token[i*6]);
                    //aryList_date.add(token[i*6+1]);
                    //aryList_time.add(temp);
                    //aryList_room_name.add(token[i*6+5]);

                    reservationDataList.add(new reservationData(token[i*7], token[i*7+1], temp, token[i*7+5], token[i*7+6]));
                }
            }
        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }
}