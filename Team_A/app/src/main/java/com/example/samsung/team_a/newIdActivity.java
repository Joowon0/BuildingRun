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
import android.widget.Toast;

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
    public static EditText edtEmail;
    EditText edtPass, edtPass2, edtPhone, edtFirstName, edtLastName;
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
        edtPass = (EditText) findViewById(R.id.edtPass);
        edtPass2 = (EditText) findViewById(R.id.edtPass2);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);

        alertdialog = new AlertDialog.Builder(newIdActivity.this).create();
    }

    public void loginButtonClick(View v) {
        Intent i = new Intent(newIdActivity.this, loginActivity.class);
        newIdActivity.this.startActivity(i);
    }

    public void newIDButtonClick(View v) {


        if (edtEmail.getText().toString().equals("") || edtPass.getText().toString().equals("") || edtPass2.getText().toString().equals("") ||
                edtPhone.getText().toString().equals("") || edtFirstName.getText().toString().equals("") || edtLastName.getText().toString().equals("")) {
            alertdialog.setTitle("Error");
            alertdialog.setMessage("Enter the information.");
            alertdialog.show();
            return;
        } else if (/*!edtEmail.getText().toString().equals(checkId) ||*/ !checkIdFlag) {
            alertdialog.setTitle("Error");
            alertdialog.setMessage("Check your E-mail. ");
            alertdialog.show();
            return;
        } else if (!edtPass.getText().toString().equals(edtPass2.getText().toString())) {
            alertdialog.setTitle("Error");
            alertdialog.setMessage("Check your password.");
            alertdialog.show();
            return;
        } else{
            joinFlag = true;
        }

        String type = "newId";

        String Pass = edtPass.getText().toString();
        String FirstName = edtFirstName.getText().toString();
        String LastName = edtLastName.getText().toString();
        String Email = edtEmail.getText().toString();
        String Phone = edtPhone.getText().toString();

        newIdBW newidBW = new newIdBW(this);
        newidBW.execute(type, Email, Pass, FirstName, LastName, Phone);
        //aHandler.sendEmptyMessageDelayed(0, 1000);

    }

    Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent i = new Intent(getApplicationContext(), loginActivity.class);
            startActivity(i);
        }
    };
    Handler aHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    public void checkIdButtonClick(View v) {
        if (edtEmail.getText().toString().equals("")) {
            alertdialog.setTitle("Error");
            alertdialog.setMessage("Input your E-mail");
            alertdialog.show();
            return;
        } else {
            checkId = edtEmail.getText().toString();
            String type = "checkEmail";

            checkIdBW checkBW = new checkIdBW(this);
            checkBW.execute(type, checkId);

            //checkIdFlag = true;
        }
    }
}

class checkIdBW extends AsyncTask<String, Void, String> {
    String type = "";
    Context context;
    AlertDialog alertdialog;

    checkIdBW(Context etx) {
        context = etx;
    }

    public checkIdBW() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        type = params[0];
        String checkId_url = "http://" + FirstActivity.connectIP + "/checkEmail.php";
        if (type.equals("checkEmail")) {
            try {
                String EmailAddress = params[1];

                URL url = new URL(checkId_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputstream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputstream, "UTF-8"));
                String post_data = URLEncoder.encode("EmailAddress", "UTF-8") + "=" + URLEncoder.encode(EmailAddress, "UTF-8");
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

        alertdialog = new AlertDialog.Builder(context).create();
        alertdialog.setTitle("Check E-mail");
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        if (type.equals("checkEmail")) {

            if(result.substring(1,2).equals("Y")){
                Toast.makeText(context,"You can use your E-mail.",Toast.LENGTH_SHORT).show();
                newIdActivity.checkIdFlag = true;
            }
            else{
                Toast.makeText(context,"The Email is already registered. Please enter other email or sign-in.",Toast.LENGTH_SHORT).show();
                //newIdActivity.checkIdFlag = true;
                //newIdActivity.joinFlag = true;
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
    AlertDialog alertdialog;

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
        String newId_url = "http://" + FirstActivity.connectIP + "/newid.php";
        if (type.equals("newId")) {
            try {
                String EmailAddress = params[1];
                String HPassword = params[2];
                String FirstName = params[3];
                String LastName = params[4];
                String PhoneNum = params[5];

                URL url = new URL(newId_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputstream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputstream, "UTF-8"));
                String post_data = URLEncoder.encode("EmailAddress", "UTF-8") + "=" + URLEncoder.encode(EmailAddress, "UTF-8")
                        + "&" + URLEncoder.encode("HPassword", "UTF-8") + "=" + URLEncoder.encode(HPassword, "UTF-8")
                        + "&" + URLEncoder.encode("FirstName", "UTF-8") + "=" + URLEncoder.encode(FirstName, "UTF-8")
                        + "&" + URLEncoder.encode("LastName", "UTF-8") + "=" + URLEncoder.encode(LastName, "UTF-8")
                        + "&" + URLEncoder.encode("PhoneNum", "UTF-8") + "=" + URLEncoder.encode(PhoneNum, "UTF-8");
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

        alertdialog = new AlertDialog.Builder(context).create();
        alertdialog.setTitle("Message");
    }
    Handler sHandler = new Handler();
    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        if (type.equals("newId")) {
            if (newIdActivity.joinFlag) {
                alertdialog.setTitle("Sign UP");
                alertdialog.setMessage("Sign up Successfully");
                sHandler.sendEmptyMessageDelayed(0, 1000);
                Intent i = new Intent(context, loginActivity.class);
                context.startActivity(i);
            } else {
                alertdialog.setTitle("failed");
                alertdialog.setMessage("Check your information");
            }
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }
}