package com.example.samsung.team_a;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
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

public class loginActivity extends AppCompatActivity {
    EditText edtID, edtPass;
    Button btnLogin, btnFind, btnNewId;
    AlertDialog alertdialog;
    public static String STuser_id = "";
    public static String STuser_pass = "";
    public static int ST_usn;
    public static String ST_email, ST_firstname, ST_lastname;
    private HttpURLConnection conn;
    private SharedPreferences prf;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        edtID = (EditText) findViewById(R.id.edtId);
        edtPass = (EditText) findViewById(R.id.edtPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnFind = (Button) findViewById(R.id.btn_find);
        btnNewId = (Button) findViewById(R.id.btnNewID);

    } //onCreate

    Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    };
    Handler aHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent i = new Intent(getApplicationContext(), loginActivity.class);
            startActivity(i);
        }
    };

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), FirstActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void loginButtonClick(View v) {
        String userIdValue = edtID.getText().toString();
        String userPassValue = edtPass.getText().toString();
        alertdialog = new AlertDialog.Builder(loginActivity.this).create();
        if (edtID.getText().toString().equals("") || edtPass.getText().toString().equals("")) {
            alertdialog.setTitle("message");
            alertdialog.setMessage("Please input your information");
            alertdialog.show();
        } else {

            loginBW loginbw = new loginBW(this);
            loginbw.execute();
        }
    }

    public void newIDButtonClick(View v) {
        edtID.setText("");
        edtPass.setText("");
        Intent i = new Intent(loginActivity.this, newIdActivity.class);
        loginActivity.this.startActivity(i);
    }

    public void find_pwButtonClick(View v) {
        Intent i = new Intent(loginActivity.this, Find_pwActivity.class);
        loginActivity.this.startActivity(i);
    }

    class loginBW extends AsyncTask<String, Integer, Integer> {
        Context context;

        loginBW(Context etx) {
            context = etx;
        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            int result = signIn();
            switch (result) {
                case Constants.SI_SUCCESS:
                    publishProgress(1);
                    break;

                case Constants.SI_NO_SUCH_EMAIL:
                    publishProgress(2);
                    edtID.setText("");
                    edtPass.setText("");
                    break;

                case Constants.SI_WRONG_PASSWORD:
                    publishProgress(3);
                    edtPass.setText("");
                    break;

                case Constants.SI_NONCE_EXIST:
                    publishProgress(4);
                    break;

                default:
//                    Toast.makeText(getApplicationContext(), "System Error/Connection Fail", Toast.LENGTH_SHORT).show();
                    break;
            }
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            AlertDialog alertdialog = new AlertDialog.Builder(context).create();
            if (value[0] == Constants.SU_SUCCESS) {
                sHandler.sendEmptyMessageDelayed(0, 10);
            } else if (value[0] == Constants.SI_NO_SUCH_EMAIL) {
                alertdialog.setTitle("information");
                alertdialog.setMessage("There is no account corresponding to input Email.");
                alertdialog.show();
                edtID.setText("");
                edtPass.setText("");
            } else if (value[0] == Constants.SI_WRONG_PASSWORD) {
                alertdialog.setTitle("information");
                alertdialog.setMessage("Wrong password. Please enter again.");
                alertdialog.show();
                edtPass.setText("");
            } else if (value[0] == Constants.SI_NONCE_EXIST) {
                alertdialog.setTitle("information");
                alertdialog.setMessage("Please activate your account. The activation link is sent to you email.");
                alertdialog.show();
            }
        }

        public int signIn() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            int result = 0;
            int usn;
            try {
                URL url = new URL("http://teama-iot.calit2.net/app/login");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("email", edtID.getText().toString());
                    json.put("password", edtPass.getText().toString());

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
                        Log.d("response",response);
                        JSONObject responseJSON = new JSONObject(response);
                        result = (Integer) responseJSON.getInt("Result");
                        ST_usn = (Integer) responseJSON.getInt("USN");
                        ST_email = (String) responseJSON.getString("email");
                        ST_firstname = (String) responseJSON.getString("firstName");
                        ST_lastname = (String) responseJSON.getString("lastName");
                        Log.d("ST_USN in Login",String.valueOf(ST_usn));
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

