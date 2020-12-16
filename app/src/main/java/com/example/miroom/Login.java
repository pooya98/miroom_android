package com.example.miroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class Login extends Activity {
    private EditText et_id, et_password;
    private Button btn_login;

    String usr_id, usr_password;
    String Response;
    private SharedPreferences appData;

    private long lastTimeBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Loading화면 띠우고 다시 돌아오기.
        Intent intent = new Intent(this, com.example.miroom.Loading.class);

        startActivity(intent);

        et_id = findViewById(R.id.et_id);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);

        appData = getSharedPreferences("appData", MODE_PRIVATE);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usr_id = et_id.getText().toString();
                usr_password = et_password.getText().toString();

                if(usr_id.replaceAll(" ", "").equals("")){
                    showAlert_InsertIdError();
                }else if(usr_password.replaceAll(" ", "").equals("")){
                    showAlert_InsertPwError();
                }else{
                    Thread thread = new Thread() {
                        public void run() {
                            contact_server_for_search();
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

                    if(Response.equals("T")){

                        SharedPreferences.Editor editor = appData.edit();
                        editor.putString("User_ID",usr_id);
                        editor.putString("User_PW",usr_password);

                        editor.apply();
                        Toast.makeText(Login.this,"로그인 성공",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else if(Response.equals("F")){
                        showAlert_LoginFail();
                    }else{
                        showAlert_LoginError();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 2000){
            finish();
            return;
        }
        lastTimeBackPressed = System.currentTimeMillis();
        Toast.makeText(this,"'뒤로가기' 버튼을 한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
    }

    public void onclick_GoSignUp(View view) {
        Intent intent = new Intent(this, com.example.miroom.SignUp.class);
        startActivity(intent);
    }

    public void showAlert_InsertIdError() {
        Login.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle(" ");
                builder.setMessage("아이디를 입력해주세요.")
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

    public void showAlert_LoginSuccess() {
        Login.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle(" ");
                builder.setMessage("로그인 성공.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("usr_id", usr_id);
                                intent.putExtra("usr_password", usr_password);
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void showAlert_LoginFail() {
        Login.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle(" ");
                builder.setMessage("일치하는 계정정보가 없습니다.")
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

    public void showAlert_LoginError() {
        Login.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle(" ");
                builder.setMessage("에러가 발생했습니다.")
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

    public void showAlert_InsertPwError() {
        Login.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle(" ");
                builder.setMessage("비밀번호를 입력해주세요.")
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

    void contact_server_for_search(){

        HttpPost httppost;
        HttpResponse response;
        HttpClient httpClient;
        List<NameValuePair> nameValuePairs;
        HttpGet httpget;

        System.out.println("--- 스레드 시작");

        try{
            httpClient = new DefaultHttpClient();
            httppost = new HttpPost("http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/account_check.php");
            nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("username", usr_id));
            nameValuePairs.add(new BasicNameValuePair("password", usr_password));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpClient.execute(httppost);

            //
            Scanner scan = new Scanner(response.getEntity().getContent());

            String response_string = "";
            while(scan.hasNext()){
                response_string += scan.nextLine();
            }

            if(response_string.equals("T") || response_string.equals("F")){
                Response = response_string;
            }else{
                Response = "E";
            }

        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }
}