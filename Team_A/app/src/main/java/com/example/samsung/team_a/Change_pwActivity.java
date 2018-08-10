package com.example.samsung.team_a;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

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


public class Change_pwActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_MENU = 101;
    public Button btnFindCancel, btnFindDone, btnFindConfirm;
    public EditText edtNewPW, edtOldPW, edtNewPW2;
    public static String oldPW = "";
    public static boolean checkpwFlag = false;
    private HttpURLConnection conn;
    AlertDialog alertdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);
        btnFindCancel = (Button) findViewById(R.id.btnverify);
        btnFindConfirm = (Button) findViewById(R.id.btnFindConfirm);
        btnFindDone = (Button) findViewById(R.id.btnFindDone);
        edtOldPW = (EditText) findViewById(R.id.edtOldPW);
        edtNewPW = (EditText) findViewById(R.id.edtNewPW);
        edtNewPW2 = (EditText) findViewById(R.id.edtNewPW2);
        Log.d("changepw on create USN",String.valueOf(loginActivity.ST_usn));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkpwFlag = false;
    }

    public void OnCancelClicked(View v) {
        Intent i = new Intent(Change_pwActivity.this, MainActivity.class);
        startActivityForResult(i, REQUEST_CODE_MENU);
        checkpwFlag = false;
    }

    public void FindDoneClicked(View v) {
        if (!edtNewPW.getText().toString().equals(edtNewPW2.getText().toString())) {
            alertdialog = new AlertDialog.Builder(this).create();
            alertdialog.setTitle("information");
            alertdialog.setMessage("Check your new password.");
            alertdialog.show();
        } else {
            if (checkpwFlag) {
                updatePassBW updatepassBW = new updatePassBW(this);
                updatepassBW.execute();
                edtOldPW.setText("");
                edtNewPW.setText("");
                edtNewPW2.setText("");
            } else {
                alertdialog = new AlertDialog.Builder(this).create();
                alertdialog.setTitle("information");
                alertdialog.setMessage("Confirm First.");
                alertdialog.show();
            }
        }
    }

    public void OnVerifyClicked(View v) {

        checkpwBW checkBW = new checkpwBW(this);
        checkBW.execute();

    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent i = new Intent(Change_pwActivity.this, MainActivity.class);
            startActivityForResult(i, REQUEST_CODE_MENU);
        }
    };

    class checkpwBW extends AsyncTask<String, Integer, Integer> {
        Context context;

        checkpwBW(Context etx) {
            context = etx;
        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            int result = checkpw();
            if (result == Constants.PC_CORRECT_PASSWORD) {
                publishProgress(1);
                checkpwFlag = true;

            } else {
                publishProgress(2);
                edtOldPW.setText("");
            }

            Log.d("JSON result : ", String.valueOf(result));
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            AlertDialog alertdialog = new AlertDialog.Builder(context).create();
            if (value[0] == Constants.PC_CORRECT_PASSWORD) {
                alertdialog.setTitle("information");
                alertdialog.setMessage("Correct password. Please input your new password.");
                alertdialog.show();
            } else {
                alertdialog.setTitle("information");
                alertdialog.setMessage("Wrong password. Check your password.");
                alertdialog.show();
            }
        }

        public int checkpw() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            int result = 0;

            try {
                URL url = new URL("http://teama-iot.calit2.net/app/checkCurrentPW");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    Log.d("loginActivity.ST_usn",String.valueOf(loginActivity.ST_usn));

                    json.put("USN", loginActivity.ST_usn);
                    json.put("password", edtOldPW.getText().toString());


                    Log.d("ST_USN in Change pw",Integer.valueOf(loginActivity.ST_usn).toString());
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

    class updatePassBW extends AsyncTask<String, Integer, Integer> {
        Context context;

        updatePassBW(Context etx) {
            context = etx;
        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            boolean result = updatePW();

            if (result) {
                publishProgress(1);
            } else {
                publishProgress(2);
                edtNewPW.setText("");
                edtNewPW2.setText("");
            }

            Log.d("JSON result : ", String.valueOf(result));
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            AlertDialog alertdialog = new AlertDialog.Builder(context).create();
            if (value[0] == 1) {
                alertdialog.setTitle("information");
                alertdialog.setMessage("Password Change Success");
                alertdialog.show();
                mHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
                alertdialog.setTitle("information");
                alertdialog.setMessage("failed. Try again");
                alertdialog.show();
            }
        }

        public boolean updatePW() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            boolean result = false;

            try {
                URL url = new URL("http://teama-iot.calit2.net/app/setNewPW");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("USN", loginActivity.ST_usn);
                    json.put("password", edtNewPW.getText().toString());

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
                        result = (boolean) responseJSON.get("ACK");
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



