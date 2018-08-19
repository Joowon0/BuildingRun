package com.example.samsung.team_a;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class newIdActivity extends AppCompatActivity {
    Button btnNewId, btnLogin;
    public static EditText edtEmail;
    EditText edtPass, edtPass2, edtPhone, edtFirstName, edtLastName;
    public static boolean joinFlag = false;
    public boolean resultFlag = false;
    private HttpURLConnection conn;
    private SharedPreferences prf;
    private SharedPreferences.Editor editor;
    String checkId;
    AlertDialog alertdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_id);

        btnNewId = (Button) findViewById(R.id.btnNewID);
        btnLogin = (Button) findViewById(R.id.btnLogin);
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
        } else if (!edtPass.getText().toString().equals(edtPass2.getText().toString())) {
            alertdialog.setTitle("Error");
            alertdialog.setMessage("Check your password.");
            alertdialog.show();
            return;
        } else {
            newIdBW newidBW = new newIdBW(this);
            newidBW.execute();
        }

        String type = "newId";

        String Pass = edtPass.getText().toString();
        String FirstName = edtFirstName.getText().toString();
        String LastName = edtLastName.getText().toString();
        String Email = edtEmail.getText().toString();
        String Phone = edtPhone.getText().toString();


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

    class newIdBW extends AsyncTask<String, Integer, Integer> {
        Context context;

        newIdBW(Context etx) {
            context = etx;
        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            int result = newid();
            if(result == Constants.SU_SUCCESS){
                publishProgress(1);
            }
            else if(result == Constants.SU_DUPLICATED){
                publishProgress(2);
            }
            else{
                publishProgress(3);
            }
            Log.d("JSON result : ", String.valueOf(result));
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            AlertDialog alertdialog = new AlertDialog.Builder(context).create();

            Log.d("value[0]: ", value[0].toString());
            if (value[0]== Constants.SU_SUCCESS) {
                alertdialog.setTitle("information");
                alertdialog.setMessage("Sign Up Successfully.");
                alertdialog.show();
                sHandler.sendEmptyMessageDelayed(0, 2000);
            } else if (value[0] ==Constants.SU_DUPLICATED){
                alertdialog.setTitle("information");
                alertdialog.setMessage("It is duplicated E-mail. Please check your E-mail.");
                alertdialog.show();
                edtEmail.setText("");
                edtPass.setText("");
                edtPass2.setText("");
            }else{
                alertdialog.setTitle("information");
                alertdialog.setMessage("System Error");
                alertdialog.show();

            }
        }

        public int newid() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            //String urlStr = prf.getString("IP", "");
            int result = 0;


            try {
                URL url = new URL("http://teama-iot.calit2.net/app/register");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("email", edtEmail.getText().toString());
                    json.put("firstName", edtFirstName.getText().toString());
                    json.put("lastName", edtLastName.getText().toString());
                    json.put("password", edtPass.getText().toString());
                    json.put("phoneNum", edtPhone.getText().toString());

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                String body = json.toString();
                Log.d("JSON_body : ", body);
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStream os = conn.getOutputStream();
                    os.write(body.getBytes());
                    os.flush();
                    String response;
                    int responseCode = conn.getResponseCode();

                    Log.d("JSONresponseconnection", String.valueOf(responseCode));

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();
                        baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int nLength = 0;
                        while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, nLength);
                        }
                        byteData = baos.toByteArray();
                        response = new String(byteData);
                        JSONObject responseJSON = new JSONObject(response);
                        result = (Integer) responseJSON.get("Result");

                        is.close();
                        os.close();
                        conn.disconnect();
                    }
                } else {
                    Log.d("JSON", "Connection fail");
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d("JSON_2line:", "problem");
            }
            return result;
        }
    }
}

