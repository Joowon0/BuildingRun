package com.example.samsung.team_a;

import android.content.Context;
import android.content.DialogInterface;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Id_CancellationActivity extends AppCompatActivity {
    EditText edtCancelPW;
    Button btn_Cancellation;
    private HttpURLConnection conn;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id__cancellation);
        edtCancelPW = (EditText) findViewById(R.id.edtCancelPW);
        btn_Cancellation = (Button) findViewById(R.id.btn_Cancellation);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent i = new Intent(Id_CancellationActivity.this, loginActivity.class);
            startActivity(i);
        }
    };

    public void OnVerifyClicked(View v) {
        alertDialog = new AlertDialog.Builder(Id_CancellationActivity.this).create();
        if (edtCancelPW.equals("")) {
            alertDialog.setTitle("information");
            alertDialog.setMessage("Please input your password");
            alertDialog.show();
        } else {
            IDCancelBW idcancelBW = new IDCancelBW(this);
            idcancelBW.execute();
            edtCancelPW.setText("");
        }
    }

    class IDCancelBW extends AsyncTask<String, Integer, Integer> {
        Context context;

        IDCancelBW(Context etx) {
            context = etx;
        }

        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... value) {
            int result = idcancellation();
            if(result ==Constants.IC_EMAIL_EXIST){
                publishProgress(1);
            }else if(result == Constants.IC_EMAIL_NOT_EXIST){
                publishProgress(2);
            }else {
                publishProgress(3);
            }
            Log.d("JSON result : ", String.valueOf(result));
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            AlertDialog alertdialog = new AlertDialog.Builder(context).create();
            //AlertDialog.Builder alert = new AlertDialog.Builder(context);
            if (value[0] == Constants.IC_EMAIL_EXIST) {
                alertdialog.setTitle("information");
                alertdialog.setMessage("ID cancellation complete.");
                mHandler.sendEmptyMessageDelayed(0, 2000);
                alertdialog.show();
            } else if (value[0] == Constants.IC_EMAIL_NOT_EXIST) {
                alertdialog.setTitle("information");
                alertdialog.setMessage("Wrong password.");
                alertdialog.show();
            } else {
                alertdialog.setTitle("information");
                alertdialog.setMessage("Wrong password. Try again!");
                alertdialog.show();
            }
        }

        public int idcancellation() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            int result = 0;

            try {
                URL url = new URL("http://teama-iot.calit2.net/app/accountCancel");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("USN", loginActivity.ST_usn);
                    json.put("password", edtCancelPW.getText().toString());

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
}
