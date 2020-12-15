package com.example.miroom;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miroom.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.zip.Deflater;

public class Reservate extends AppCompatActivity {

    ListView listView;
    Spinner layer_spinner;
    Spinner spinner3, spinner4;
    private CustomAdapter           m_Adapter;

    private TextView textView_Date;
    private DatePickerDialog.OnDateSetListener callbackMethod;

    int select_year;
    int select_month;
    int select_day;
    String select_yoil;
    String selected_layer;

    ArrayList<String> arrayList_for_layer_spinner = new ArrayList<String>();

    HttpPost httppost;
    HttpResponse response;
    HttpClient httpClient;
    List<NameValuePair> nameValuePairs;
    HttpGet httpget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservate);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("RESERVATION");
        ab.setDisplayHomeAsUpEnabled(true);

        this.InitializeView();
        this.InitializeListener();

        layer_spinner = (Spinner)findViewById(R.id.layer_spinner);

        m_Adapter = new CustomAdapter();


        /* 현재 날짜 정보 저장 */
        Calendar cal = Calendar.getInstance();
        select_year = cal.get(Calendar.YEAR);
        select_month = cal.get(Calendar.MONTH) + 1;
        select_day = cal.get(Calendar.DAY_OF_MONTH);

        try {
            select_yoil = getDateDay(select_year + "-" + select_month + "-" + select_day, "yyyy-MM-dd");
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*                      */

        textView_Date.setText(select_year + "년 " + select_month + "월 " + select_day + "일" + " (" + select_yoil + ")");


        /* Layer Spinner Load 시작 */
        Thread loadThread = new Thread(){
          public void run(){
              contact_server_for_load();
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

        ArrayAdapter<String> Adapter_for_layer = new ArrayAdapter<String>(
                this, R.layout.item_spinner, R.id.name, arrayList_for_layer_spinner);

        layer_spinner.setAdapter(Adapter_for_layer);


        /* Layer Spinner Load 완료 */




        spinner3 = (Spinner) findViewById(R.id.start_time_spinner);
        ArrayList<String> arrayList3 = new ArrayList<String>();
        arrayList3.add(0, "시작 시각");
        for (int i = 0; i <= 24; i++) {
            if (i < 10)
                arrayList3.add("0" + i + ":00");
            else
                arrayList3.add(i + ":00");
        }

        ArrayAdapter<String> Adapter3 = new ArrayAdapter<String>(
                this, R.layout.item_spinner, R.id.name, arrayList3);

        spinner3.setAdapter(Adapter3);


        /* 종료시각 spinner */
        spinner4 = (Spinner) findViewById(R.id.end_time_spinner);
        ArrayList<String> arrayList4 = new ArrayList<String>();
        arrayList4.add(0, "종료 시각");
        for (int i = 0; i <= 24; i++) {
            if (i < 10)
                arrayList4.add("0" + i + ":00");
            else
                arrayList4.add(i + ":00");
        }

        ArrayAdapter<String> Adapter4 = new ArrayAdapter<String>(
                this, R.layout.item_spinner, R.id.name, arrayList4);

        spinner4.setAdapter(Adapter4);

        /* 종료 시각 spinner 완료*/


        listView = (ListView) findViewById(R.id.listView);

    }
/*
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Toast.makeText(Reservate.this,listView.get, Toast.LENGTH_SHORT).show();
            Intent My_intent = new Intent(Reservate.this, com.example.miroom.Book_page.class);
            My_intent.putExtra("parameter_title", aryList.get(position).getRoom_name());
            startActivity(My_intent);
        }
    };*/

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ;

    public void InitializeView() {
        textView_Date = (TextView) findViewById(R.id.textView_date);
    }

    public void InitializeListener() {
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                select_year = year;
                select_month = monthOfYear + 1;
                select_day = dayOfMonth;

                try {
                    select_yoil = getDateDay(select_year + "-" + select_month + "-" + select_day, "yyyy-MM-dd");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textView_Date.setText(select_year + "년 " + select_month + "월 " + select_day + "일" + " (" + select_yoil + ")");
            }
        };
    }

    public void onclick_date_bt(View view) {
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, select_year, select_month - 1, select_day);

        dialog.show();
    }

    public static String getDateDay(String date, String dateType) throws Exception {

        String day = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
        Date nDate = dateFormat.parse(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

        }

        return day;
    }

    public void onclick_OK(View view) {

        if(layer_spinner.getSelectedItem().toString().equals("층")) {
            showAlert_layer();
        }else if(spinner3.getSelectedItem().toString().equals("시작 시각")){
            showAlert_start_time();
        }else if(spinner4.getSelectedItem().toString().equals("종료 시각")){
            showAlert_end_time();
        }else if(Integer.parseInt(spinner3.getSelectedItem().toString().substring(0,2))> Integer.parseInt(spinner4.getSelectedItem().toString().substring(0,2))){
            showAlert_time_reserve();
        }else if(Integer.parseInt(spinner3.getSelectedItem().toString().substring(0,2)) == Integer.parseInt(spinner4.getSelectedItem().toString().substring(0,2))){
            showAlert_time_same();
        }else{
            m_Adapter = new CustomAdapter();
            Thread searchThread = new Thread(){
                public void run(){
                    contact_server_for_search();
                }
            };

            searchThread.start();
            System.out.println("--- loadThread go!");

            try{
                searchThread.join();
                System.out.println("--- loadThread done!");
            }catch(Exception e){
                e.getMessage();
            }

            Thread searchThread2 = new Thread(){
                public void run(){
                    contact_server_for_search2();
                }
            };

            searchThread2.start();
            System.out.println("--- loadThread go!");

            try{
                searchThread2.join();
                System.out.println("--- loadThread done!");
            }catch(Exception e){
                e.getMessage();
            }


            listView.setAdapter(m_Adapter);
            //listView.setOnItemClickListener(listener);
        }
    }

    void contact_server_for_load(){
        System.out.println("--- 스레드 시작");

        try{

            httpClient = new DefaultHttpClient();
            httppost = new HttpPost("http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/layer_load.php");
            response = httpClient.execute(httppost);

            //
            Scanner scan = new Scanner(response.getEntity().getContent());

            String response_string = "";
            while(scan.hasNext()){
                response_string += scan.nextLine();
            }

            System.out.println("---Response : "+response_string);

            String[] token;
            token = response_string.split(" ");

            arrayList_for_layer_spinner.add(0,"층");
            for(int i=0;i< token.length;i++){
                System.out.println("token "+i+" : "+ token[i]);
                arrayList_for_layer_spinner.add(token[i]);
            }

        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }

    void contact_server_for_search(){
        System.out.println("--- 스레드 시작");

        String select_date = select_year+"-"+select_month+"-"+select_day;
        String start_time = spinner3.getSelectedItem().toString()+":00";
        String end_time = spinner4.getSelectedItem().toString()+":00";

        try{

            String selected_layer = layer_spinner.getSelectedItem().toString();

            httpClient = new DefaultHttpClient();
            httpget = new HttpGet("http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/search_room2.php?layer="+ selected_layer+"&date="+select_date+"&start_time="+start_time+"&end_time="+end_time);
            //nameValuePairs = new ArrayList<>(2);
            //nameValuePairs.add(new BasicNameValuePair("layer", selected_layer));

            //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
                for (int i = 0; i < token.length; i++) {
                    System.out.println("token " + i + " : " + token[i]);

                    RoomDTO dto = new RoomDTO();

                    dto.setRoom_name(token[i]);
                    dto.setLayer(selected_layer);
                    dto.setStart_time(start_time);
                    dto.setEnd_time(end_time);
                    dto.setDate(select_date);
                    dto.setCheck_flag("예약가능");
                    m_Adapter.add(dto);
                }
            }
        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }

    void contact_server_for_search2(){
        System.out.println("--- 스레드 시작");

        String select_date = select_year+"-"+select_month+"-"+select_day;
        String start_time = spinner3.getSelectedItem().toString()+":00";
        String end_time = spinner4.getSelectedItem().toString()+":00";

        try{

            String selected_layer = layer_spinner.getSelectedItem().toString();

            httpClient = new DefaultHttpClient();
            httpget = new HttpGet("http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/search_unavail_room.php?layer="+ selected_layer+"&date="+select_date+"&start_time="+start_time+"&end_time="+end_time);
            //nameValuePairs = new ArrayList<>(2);
            //nameValuePairs.add(new BasicNameValuePair("layer", selected_layer));

            //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
                for (int i = 0; i < token.length; i++) {
                    System.out.println("token " + i + " : " + token[i]);

                    RoomDTO dto = new RoomDTO();

                    dto.setRoom_name(token[i]);
                    dto.setLayer(selected_layer);
                    dto.setStart_time(start_time);
                    dto.setEnd_time(end_time);
                    dto.setDate(select_date);
                    dto.setCheck_flag("예약불가");
                    m_Adapter.add(dto);
                }
            }
        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }

    public void showAlert_layer() {
        Reservate.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Reservate.this);
                builder.setTitle(" ");
                builder.setMessage("층을 선택해주세요.")
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

    public void showAlert_start_time() {
        Reservate.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Reservate.this);
                builder.setTitle(" ");
                builder.setMessage("시작 시각을 선택해주세요.")
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

    public void showAlert_end_time() {
        Reservate.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Reservate.this);
                builder.setTitle(" ");
                builder.setMessage("종료 시각을 선택해주세요.")
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

    public void showAlert_time_reserve() {
        Reservate.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Reservate.this);
                builder.setTitle(" ");
                builder.setMessage("종료시각이 더 빠릅니다.\n 다시 선택해주세요.")
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

    public void showAlert_time_same() {
        Reservate.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Reservate.this);
                builder.setTitle(" ");
                builder.setMessage("시작 시각과 종료 시각이 같습니다.\n 다시 선택해주세요.")
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
}