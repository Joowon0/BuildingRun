package com.example.samsung.team_a;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class Change_pwActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_MENU = 101;
    public Button btnFindCancel, btnFindDone, btnFindConfirm;
    public EditText edtNewPW, edtOldPW, edtNewPW2;
    public static String oldPW = "";
    public static boolean checkpwFlag = false;
    AlertDialog alertdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);
        btnFindCancel = (Button) findViewById(R.id.btnFindCancel);
        btnFindConfirm = (Button) findViewById(R.id.btnFindConfirm);
        btnFindDone = (Button) findViewById(R.id.btnFindDone);
        edtOldPW = (EditText) findViewById(R.id.edtOldPW);
        edtNewPW = (EditText) findViewById(R.id.edtNewPW);
        edtNewPW2 = (EditText) findViewById(R.id.edtNewPW2);
    }

    public void OnCancelClicked(View v) {
        Intent i = new Intent(Change_pwActivity.this, MainActivity.class);
        startActivityForResult(i, REQUEST_CODE_MENU);
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
                updatepassBW.execute("updatePass", loginActivity.STuser_id, edtNewPW.getText().toString());
                edtOldPW.setText("");
                edtNewPW.setText("");
                edtNewPW2.setText("");
            }
            else{
                alertdialog = new AlertDialog.Builder(this).create();
                alertdialog.setTitle("information");
                alertdialog.setMessage("Confirm First.");
                alertdialog.show();
            }
        }
    }

    public void OnVerifyClicked(View v) {

        oldPW = edtOldPW.getText().toString();
        if (oldPW.equals(loginActivity.STuser_pass)) {
            Toast.makeText(getApplicationContext(), "Verified", Toast.LENGTH_SHORT).show();
            Change_pwActivity.checkpwFlag = true;
        }
        //checkpwBW checkBW= new checkpwBW(this);
        //checkBW.execute("checkpw",loginActivity.STuser_id);
        else {
            Toast.makeText(getApplicationContext(), "Check your password", Toast.LENGTH_SHORT).show();
            edtOldPW.setText("");
        }

    }
}

class updatePassBW extends AsyncTask<String, Void, String> {
    String type = "";
    Context context;
    AlertDialog alertdialog;

    updatePassBW(Context etx) {
        context = etx;
    }

    public updatePassBW() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        type = params[0];
        String postState_url = "";
        if (type.equals("updatePass")) {
            try {
                String user_id = params[1];
                String new_pass = params[2];

                URL url = new URL(postState_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputstream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputstream, "UTF-8"));
                String post_data = URLEncoder.encode("EmailAddress", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")
                        + "&" + URLEncoder.encode("HPassword", "UTF-8") + "=" + URLEncoder.encode(new_pass, "UTF-8");
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
        alertdialog.setTitle("information");
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        if (type.equals("updatePass")) {
            if (result.charAt(1) == '0' || result.substring(1, 2).equals("0")) {
                alertdialog.setMessage("Please check your password");
                alertdialog.show();
            } else {
                alertdialog.setMessage("Password change success.");
                alertdialog.show();
                Intent i = new Intent(context, MainActivity.class);
                context.startActivity(i);
            }
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }
}

/*class checkpwBW extends AsyncTask<String, Void, String> {
    String type = "";
    Context context;
    AlertDialog alertdialog;
    checkpwBW(Context etx){
        context =etx;
    }

    public checkpwBW() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        type=params[0];
        String postState_url="http://"+FirstActivity.connectIP+"/change_checkPW.php";
        if(type.equals("checkpw")){
            try {
                loginActivity.STuser_id = params[1];

                URL url=new URL(postState_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputstream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(outputstream,"UTF-8"));
                String post_data = URLEncoder.encode("EmailAddress","UTF-8")+"="+ URLEncoder.encode(loginActivity.STuser_id,"UTF-8");
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

        alertdialog= new AlertDialog.Builder(context).create();
        alertdialog.setTitle("information");
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        if(type.equals("checkpw"))
        {
            if(result.equals(Change_pwActivity.oldPW))
            {
                alertdialog.setMessage("Verified");
                alertdialog.show();
                Change_pwActivity.checkpwFlag = true;

            }
            else
            {
                alertdialog.setMessage("Please Check your password.");
                alertdialog.show();
            }
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }
}*/