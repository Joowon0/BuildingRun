package com.example.samsung.team_a;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class newIdActivity extends AppCompatActivity {
    Button btnNewId, btnLogin, btnVerify;
    EditText edtId, edtPass, edtPass2, edtEmail, edtPhone;
    public static boolean checkIdFlag = false;
    public static boolean joinFlag = false;
    String checkId;
    AlertDialog alertdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_id);

        btnNewId = (Button) findViewById(R.id.btnNewID);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        edtId = (EditText) findViewById(R.id.edtId);
        edtPass = (EditText) findViewById(R.id.edtPass);
        edtPass2 = (EditText) findViewById(R.id.edtPass2);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);

        alertdialog= new AlertDialog.Builder(newIdActivity.this).create();
    }
    public void loginButtonClick(View v)
    {
        Intent i = new Intent(newIdActivity.this, loginActivity.class);
        newIdActivity.this.startActivity(i);
    }
    public void newIDButtonClick(View v)
    {

        Intent i = new Intent(newIdActivity.this, MainActivity.class);
        newIdActivity.this.startActivity(i);
        /*if(edtId.getText().toString().equals("") || edtName.getText().toString().equals("") || edtPass.getText().toString().equals("") || edtPass2.getText().toString().equals("") || edtPhone.getText().toString().equals("") || edtEmail.getText().toString().equals(""))
        {
            alertdialog.setTitle("Error");
            alertdialog.setMessage("Enter the information.");
            alertdialog.show();
            return;
        }

        else if(!edtId.getText().toString().equals(checkId) || !checkIdFlag) {
            alertdialog.setTitle("Error");
            alertdialog.setMessage("Check your ID. There is same ID");
            alertdialog.show();
            checkIdFlag = false;
            return;
        }

        else if(!edtPass.getText().toString().equals(edtPass2.getText().toString()))
        {
            alertdialog.setTitle("Error");
            alertdialog.setMessage("Check your password.");
            alertdialog.show();
            return;
        }

        String type ="newId";

        String id = edtId.getText().toString();
        String Pass = edtPass.getText().toString();
        String Name = edtName.getText().toString();
        String Email = edtEmail.getText().toString();
        String Phone = edtPhone.getText().toString();

        newIdBW newidBW = new newIdBW(this);
        newidBW.execute(type,id,Pass,Name,Email,Phone);

        if(joinFlag)
        {
            alertdialog.setTitle("Sign UP");
            alertdialog.setMessage("Sign up Successfully");
            sHandler.sendEmptyMessageDelayed(0,3000);
        }*/
    }
    Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent i = new Intent(getApplicationContext(), loginActivity.class);
            startActivity(i);
        }
    };
    public void checkIdButtonClick(View v)
    {
        if(edtId.getText().toString().equals(""))
        {
            alertdialog.setTitle("Error");
            alertdialog.setMessage("ID를 입력하세요.");
            alertdialog.show();
            return;
        }
        checkId = edtId.getText().toString();
        String type ="checkId";

        checkIdBW checkBW = new checkIdBW(this);
        checkBW.execute(type,checkId);

        checkIdFlag = true;
    }
}

class checkIdBW extends AsyncTask<String, Void, String> {
    String type = "";
    Context context;
    android.app.AlertDialog alertdialog;

    checkIdBW(Context etx){
        context =etx;
    }

    public checkIdBW() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        type=params[0];
        String checkId_url="http://"+FirstActivity.connectIP+"/checkId.php";
        if(type.equals("checkId")){
            try {
                String user_id = params[1];

                URL url=new URL(checkId_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputstream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(outputstream,"UTF-8"));
                String post_data = URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputstream.close();
                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String result="";
                String line="";

                while((line=bufferedReader.readLine())!=null){
                    result+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub

        alertdialog= new android.app.AlertDialog.Builder(context).create();
        alertdialog.setTitle("Check ID");
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        if(type.equals("checkId"))
        {
            alertdialog.setMessage(result);
            alertdialog.show();

            if(result.equals("사용 가능한 ID입니다.")){
                newIdActivity.checkIdFlag = true;
            }
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }
}

class newIdBW extends AsyncTask<String, Void, String> {
    String type = "";
    Context context;
    android.app.AlertDialog alertdialog;

    newIdBW(Context etx) {
        context = etx;
    }

    public newIdBW() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        type = params[0];
        String newId_url = "http://" + FirstActivity.connectIP + "/newId.php";
        if (type.equals("newId")) {
            try {
                String user_id = params[1];
                String user_pass = params[2];
                String user_name = params[3];
                String user_ssn = params[4];
                String user_phone = params[5];

                URL url = new URL(newId_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputstream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputstream, "UTF-8"));
                String post_data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")
                        + "&" + URLEncoder.encode("user_pass", "UTF-8") + "=" + URLEncoder.encode(user_pass, "UTF-8")
                        + "&" + URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8")
                        + "&" + URLEncoder.encode("user_ssn", "UTF-8") + "=" + URLEncoder.encode(user_ssn, "UTF-8")
                        + "&" + URLEncoder.encode("user_phone", "UTF-8") + "=" + URLEncoder.encode(user_phone, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputstream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String result = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub

        alertdialog = new android.app.AlertDialog.Builder(context).create();
        alertdialog.setTitle("Message");
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        if (type.equals("newId")) {
            newIdActivity.joinFlag = true;
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }
}
