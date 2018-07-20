package com.example.samsung.team_a;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Change_pwActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_MENU = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);
    }
    public void OnCancelClicked(View v){
        Intent i = new Intent(Change_pwActivity.this, loginActivity.class);
        startActivityForResult(i,REQUEST_CODE_MENU);
    }
    public void FindDoneClicked(View v){
        Intent i = new Intent(Change_pwActivity.this, loginActivity.class);
        startActivityForResult(i,REQUEST_CODE_MENU);
    }
}
