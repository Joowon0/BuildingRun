package com.example.samsung.team_a;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class FirstActivity extends AppCompatActivity {
    public static String connectIP = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }
    public void enterOnClick(View arg0) {
        Intent in = new Intent(getApplicationContext(), loginActivity.class);
        startActivity(in);
    }
}
