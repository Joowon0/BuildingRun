package com.example.samsung.team_a;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Find_pwActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_MENU = 101;
    AlertDialog alertDialog;

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
    public void OnVerifyClicked(View v) {
        alertDialog= new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Information");
        alertDialog.setMessage("We sent you an activation link. Please check your E-mail");
        alertDialog.show();
        sHandler.sendEmptyMessageDelayed(0,5000);

    }

}
