package com.example.miroom;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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


    private CustomAdapter2           m_Adapter;


    String user_name;
    String user_id;
    String user_phone_num;

    private SharedPreferences appData;

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


        m_Adapter = new CustomAdapter2();



        listView = (ListView)findViewById(R.id.listView_check_result);
        textView_no_result = (TextView)findViewById(R.id.no_result);

        appData = getSharedPreferences("appData", MODE_PRIVATE);

        user_id = appData.getString("User_ID", "");

        Thread Thread = new Thread(){
            public void run(){
                contact_server_get_user_info();
            }
        };

        Thread.start();
        System.out.println("--- loadThread go!");

        try{
            Thread.join();
            System.out.println("--- loadThread done!");
        }catch(Exception e){
            e.getMessage();
        }


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

        listView.setAdapter(m_Adapter);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void contact_server_for_search(){

        HttpPost httppost;
        HttpResponse response;
        HttpClient httpClient;
        List<NameValuePair> nameValuePairs;
        HttpGet httpget;

        System.out.println("--- 스레드 시작");

        try{

            httpClient = new DefaultHttpClient();
            httpget = new HttpGet("http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/reservation_load.php?user_name="+user_name+"&user_phone="+user_phone_num);
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

            if(token[0].equals("")){

            }
            else {
                for (int i = 0; i < token.length; i = i+7) {
                    System.out.println("token " + i + " : " + token[i]);

                    ReservationDTO dto = new ReservationDTO();

                    dto.setId(token[i]);
                    dto.setDate(token[i+1]);
                    dto.setStart_time(token[i+2]);
                    dto.setEnd_time(token[i+4]);
                    dto.setLayer(token[i+5]);
                    dto.setRoom_name(token[i+6]);
                    m_Adapter.add(dto);
                }
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
                user_phone_num = token[1];
            }

        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }

}