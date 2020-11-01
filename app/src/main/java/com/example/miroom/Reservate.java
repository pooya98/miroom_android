package com.example.miroom;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Reservate extends AppCompatActivity {

    ListView listView;
    ArrayList<String> aryList = new ArrayList<String>();

    private TextView textView_Date;
    private DatePickerDialog.OnDateSetListener callbackMethod;

    int select_year;
    int select_month;
    int select_day;
    String select_yoil;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservate);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("RESERVATION");
        ab.setDisplayHomeAsUpEnabled(true);

        this.InitializeView();
        this.InitializeListener();

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


        Spinner spinner = (Spinner) findViewById(R.id.building_spinner);
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(0, "건물명");
        arrayList.add("테크노빌딩");

        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(
                this, R.layout.item_spinner, R.id.name, arrayList);

        spinner.setAdapter(Adapter);

        Spinner spinner2 = (Spinner) findViewById(R.id.layer_spinner);
        ArrayList<String> arrayList2 = new ArrayList<String>();
        arrayList2.add(0, "층");
        arrayList2.add("지하 1층");

        ArrayAdapter<String> Adapter2 = new ArrayAdapter<String>(
                this, R.layout.item_spinner, R.id.name, arrayList2);

        spinner2.setAdapter(Adapter2);

        Spinner spinner3 = (Spinner) findViewById(R.id.start_time_spinner);
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

        Spinner spinner4 = (Spinner) findViewById(R.id.end_time_spinner);
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


        listView = (ListView) findViewById(R.id.listView);

        aryList.add("401호");
        aryList.add("402호");
        aryList.add("403호");
        aryList.add("404호");
        aryList.add("405호");
        aryList.add("406호");
        aryList.add("407호");
        aryList.add("408호");
        aryList.add("409호");
        aryList.add("410호");
        aryList.add("411호");
        aryList.add("412호");
        aryList.add("413호");
        aryList.add("414호");
        aryList.add("415호");
        aryList.add("416호");


        adapter = new ArrayAdapter<String>(
                this,
                R.layout.item,
                R.id.name,
                aryList
        );

    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Toast.makeText(Reservate.this, aryList.get(position), Toast.LENGTH_SHORT).show();
            Intent My_intent = new Intent(Reservate.this, com.example.miroom.Book_page.class);
            My_intent.putExtra("parameter_title", aryList.get(position));
            startActivity(My_intent);
        }
    };

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
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
    }
}