package com.example.samsung.team_a;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class Frag_historic extends Fragment {
    static final String[] LIST_MENU = {"SSN : 1\nMAC_Addr : CC-B0-DA-50-51-0B", "SSN : 2\nMAC_Addr : 6F-23-A5-6E-89-7B", "SSN : 3\nMAC_Addr : 6F-23-A5-6E-89-7C"};
    ListView listview = null;
    ArrayList<tab1AirQuality.info> SensorInfoList = new ArrayList<tab1AirQuality.info>();
    class info{
        public int SSN;
        public String address;
        public LatLng location;
        //위치값은 한번더 확인해봐야겠지 Latlng를 위도 경도 나눠서 double롤 받아야할듯
    }
    public Frag_historic() {
        // required
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.frag_historic, container, false);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, LIST_MENU);

        listview = (ListView) layout.findViewById(R.id.ListView);
        listview.setFastScrollAlwaysVisible(true);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                // get TextView's Text.
                String strText = (String) parent.getItemAtPosition(position);


                // TODO : use strText
            }
        });

        return layout;
    }
}


