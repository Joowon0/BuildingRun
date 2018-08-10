package com.example.samsung.team_a;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Find_pwActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_MENU = 101;
    public static boolean verifyFlag = false;
    AlertDialog alertdialog;
    EditText edtFindEmail;
    Button btnverify;
    private HttpURLConnection conn;
    private SharedPreferences prf;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);
        edtFindEmail = (EditText) findViewById(R.id.edtFindEmail);
        btnverify = (Button) findViewById(R.id.btnverify);
    }

    Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent i = new Intent(Find_pwActivity.this, loginActivity.class);
            startActivityForResult(i, REQUEST_CODE_MENU);
        }
    };
    Handler aHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    public void OnVerifyClicked(View v) {
        FindPWBW findpwBW = new FindPWBW(this);
        findpwBW.execute();

    }

    class FindPWBW extends AsyncTask<String, Integer, Integer> {
        Context context;

        FindPWBW(Context etx) {
            context = etx;
        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            int result = forgottenPW();

            switch (result) {
                case Constants.RP_EMAIL_EXIST:
                    publishProgress(1);
                    sHandler.sendEmptyMessageDelayed(0, 1000);
                    break;

                case Constants.RP_EMAIL_NOT_EXIST:
                    publishProgress(2);
                    edtFindEmail.setText("");
                    break;

                default:
                    //Toast.makeText(, "System Error/Connection Fail", Toast.LENGTH_SHORT).show();
                    Log.d("JSON ERROR : ", "system error / connection Fail.");
                    break;
            }
            Log.d("JSON result : ", String.valueOf(result));
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            AlertDialog alertdialog = new AlertDialog.Builder(context).create();
            if (value[0] == Constants.RP_EMAIL_EXIST) {
                alertdialog.setTitle("information");
                alertdialog.setMessage("We sent you a link to your Email. Pleas check your Email.");
                alertdialog.show();
                sHandler.sendEmptyMessageDelayed(0, 1000);
            } else if (value[0] == Constants.RP_EMAIL_NOT_EXIST) {
                alertdialog.setTitle("information");
                alertdialog.setMessage("Email not Exist.");
                alertdialog.show();

            }
        }

        public int forgottenPW() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            int result = 0;

            try {
                URL url = new URL("http://teama-iot.calit2.net/app/forgotPW");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("email", edtFindEmail.getText().toString());

                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Log.d("JSON put : ", "도 안돼 아오");
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
                    Log.d("JSONos.flush : ", "Success");
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
                        Log.d("JSONTEST/byteData : ", byteData.toString());
                        response = new String(byteData);
                        Log.d("JSONTEST/response : ", response);
                        JSONObject responseJSON = new JSONObject(response);
                        Log.d("JSONTEST/responseJSON : ", responseJSON.toString());
                        result = (Integer) responseJSON.get("Result");
                        Log.d("JSONTEST/result : ", String.valueOf(result));

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
