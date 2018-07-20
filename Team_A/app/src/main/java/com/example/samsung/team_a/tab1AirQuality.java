package com.example.samsung.team_a;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.design.widget.Snackbar;

public class tab1AirQuality extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_airquality, container, false);
         Button btnChat = (Button)rootView.findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BlueChatActivity.class);
                startActivity(i);
            }
        });
        Button btnChangepw = (Button)rootView.findViewById(R.id.btnChangepw);
        btnChangepw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Change_pwActivity.class);
                startActivity(i);
            }
        });
        return rootView;
    }

}
