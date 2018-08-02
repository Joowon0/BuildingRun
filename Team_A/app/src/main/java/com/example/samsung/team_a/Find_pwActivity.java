package com.example.samsung.team_a;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class Find_pwActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_MENU = 101;
    public static boolean verifyFlag = false;
    AlertDialog alertDialog;
    EditText edtEmail= (EditText)findViewById(R.id.edtFindEmail);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);
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
        alertDialog = new AlertDialog.Builder(this).create();
        //findpwBW.execute("findpw", Email,);
        /*if(!edtEmail.equals("")) {
            alertDialog.setTitle("Information");
            alertDialog.setMessage("We sent you an activation link. Please check your E-mail");
            alertDialog.show();
            sHandler.sendEmptyMessageDelayed(0, 4000);
        }
        else{
            alertDialog.setTitle("Information");
            alertDialog.setMessage("Input your Email.");
            alertDialog.show();
            aHandler.sendEmptyMessageDelayed(0, 2000);
        }*/
    }

}
class FindPWBW extends AsyncTask<String, Void, String> {
    String type = "";
    Context context;
    AlertDialog alertdialog;

    FindPWBW(Context etx) {
        context = etx;
    }

    public FindPWBW() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        type = params[0];
        String newId_url = "http://teama-iot.calit2.net/controllers/sendEmail.php";
        if (type.equals("findpw")) {
            try {
                String EmailAddress = params[1];
                String HPassword = params[2];
                String FirstName = params[3];
                String LastName = params[4];

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
                        + "&" + URLEncoder.encode("LastName", "UTF-8") + "=" + URLEncoder.encode(LastName, "UTF-8");
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
        if (type.equals("findpw")) {
            if (Find_pwActivity.verifyFlag) {
                alertdialog.setTitle("Information");
                alertdialog.setMessage("We sent you an activation link. Please check your E-mail");
                alertdialog.show();
                sHandler.sendEmptyMessageDelayed(0, 5000);
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
