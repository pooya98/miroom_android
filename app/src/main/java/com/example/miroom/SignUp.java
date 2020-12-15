package com.example.miroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SignUp extends Activity {

    EditText ET_username, ET_password, ET_password_check, ET_company, ET_name, ET_phone;
    String username, password, password_check, company, name, phone;
    String Response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ET_username = (EditText)findViewById(R.id.name);
        ET_password = (EditText)findViewById(R.id.password);
        ET_password_check = (EditText)findViewById(R.id.password_check);
        ET_company = (EditText)findViewById(R.id.company);
        ET_name = (EditText)findViewById(R.id.Signup_name);
        ET_phone = (EditText)findViewById(R.id.Signup_phone);
    }

    public void onclick_SignUp(View view) {
        username = ET_username.getText().toString();
        password = ET_password.getText().toString();
        password_check = ET_password_check.getText().toString();
        company = ET_company.getText().toString();
        name = ET_name.getText().toString();
        phone = ET_phone.getText().toString();

        if(username.replaceAll(" ","").equals("") || password.replaceAll(" ","").equals("") || password_check.replaceAll(" ","").equals("") || name.replaceAll(" ", "").equals("")){
            showAlert_InputError();
        }else {
            if (password.equals(password_check)) {
                if(phone.replaceAll(" ","").length() == 11) {
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

                    if (Response.equals("T")) {
                        showAlert_InsertSuccess();
                    } else if (Response.equals("F")) {
                        showAlert_InsertFail();
                    } else if (Response.equals("D")) {
                        showAlert_Duplicate();
                    } else {
                        showAlert_Error();
                    }
                }else{
                    showAlert_PhoneCheck();
                }
            }
            else{
                showAlert_PasswordCheckError();
            }
        }
    }


    public void onclick_Cancel_SignUp(View view) {
        finish();
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
            String uri = "http://ec2-3-35-3-235.ap-northeast-2.compute.amazonaws.com/insert_account.php"
                    +"?"
                    +"username="+username+""
                    +"&"
                    +"password="+password+""
                    +"&"
                    +"company="+company+""
                    +"&"
                    +"name="+name+""
                    +"&"
                    +"phone="+phone+"";
            httpget = new HttpGet(uri);
            response = httpClient.execute(httpget);

            //
            Scanner scan = new Scanner(response.getEntity().getContent());

            String response_string = "";
            while(scan.hasNext()){
                response_string += scan.nextLine();
            }

            System.out.println("dddd"+response_string);

            if(response_string.equals("T") || response_string.equals("F") || response_string.equals("D")){
                Response = response_string;
            }else{
                Response = "E";
            }

        }catch (Exception e){
            System.out.println("Exception : "+e.getMessage());
        }
    }

    public void showAlert_InsertSuccess() {
        SignUp.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                builder.setTitle(" ");
                builder.setMessage("회원가입에 성공하였습니다.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void showAlert_Duplicate() {
        SignUp.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                builder.setTitle(" ");
                builder.setMessage("이미 등록된 ID와 중복입니다.")
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

    public void showAlert_InsertFail() {
        SignUp.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                builder.setTitle(" ");
                builder.setMessage("회원가입에 실패했습니다.")
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

    public void showAlert_Error() {
        SignUp.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
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

    public void showAlert_InputError() {
        SignUp.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                builder.setTitle(" ");
                builder.setMessage("필수 정보가 입력되지 않았습니다.")
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

    public void showAlert_PasswordCheckError() {
        SignUp.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                builder.setTitle(" ");
                builder.setMessage("비밀번호 확인이 비밀번호와 다릅니다.")
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

    public void showAlert_PhoneCheck() {
        SignUp.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                builder.setTitle(" ");
                builder.setMessage("전화번호 입력이 올바르지 않습니다.")
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