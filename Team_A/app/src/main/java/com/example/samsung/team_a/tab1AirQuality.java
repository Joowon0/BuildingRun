package com.example.samsung.team_a;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class tab1AirQuality extends Fragment {
    static final String[] LIST_MENU = {"SSN : 1\nMAC_Addr : CC-B0-DA-50-51-0B", "SSN : 2\nMAC_Addr : 6F-23-A5-6E-89-7B", "SSN : 3\nMAC_Addr : 6F-23-A5-6E-89-7C"};
    ListView listview = null;
    private HttpURLConnection conn;
    private String time;
    private double CO,CO_AQI,CO2,CO2_AQI,SO2,SO2_AQI,NO2,NO2_AQI,
            O3,O3_AQI,PM25,PM25_AQI,PM10,PM10_AQI,AQI_avg,TEMP;
    ArrayList<info> SensorInfoList = new ArrayList<info>();
    class info{
        public int SSN;
        public String address;
        public LatLng location;
        //위치값은 한번더 확인해봐야겠지 Latlng를 위도 경도 나눠서 double롤 받아야할듯
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_airquality, container, false);
        GraphView graph = (GraphView) rootView.findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)

        });
        graph.addSeries(series);

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, LIST_MENU);


        listview = (ListView) rootView.findViewById(R.id.ListView);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                // get TextView's Text.
                String strText = (String) parent.getItemAtPosition(position);


                // TODO : use strText
            }
        });


        return rootView;
    }
    class AQDVBW extends AsyncTask<String, Integer, Integer> {
        Context context;

        AQDVBW(Context etx) {
            context = etx;
        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            int result = aqdvbw();
            switch (result) {
                case Constants.SI_SUCCESS:
                    publishProgress(1);
                    break;

                case Constants.SI_NO_SUCH_EMAIL:
                    publishProgress(2);

                    break;

                case Constants.SI_WRONG_PASSWORD:
                    publishProgress(3);

                    break;

                default:
//                    Toast.makeText(getApplicationContext(), "System Error/Connection Fail", Toast.LENGTH_SHORT).show();
                    break;
            }
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            android.app.AlertDialog alertdialog = new android.app.AlertDialog.Builder(context).create();
            if (value[0] == Constants.SU_SUCCESS) {

            } else if (value[0] == Constants.SI_NO_SUCH_EMAIL) {


            } else if (value[0] == Constants.SI_WRONG_PASSWORD) {

            }
        }

        public int aqdvbw() {
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
                    json.put("SSN", 1);//SSN 받아와서 하깅
                    json.put("Type", 1);
                    json.put("startDate", "2018-08-08");
                    json.put("endDate", "2018-08-09");

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
                        JSONArray jsonarray  = new JSONArray(response);
                        for(int i =0; i<jsonarray .length(); i++) {
                            JSONObject jsonObject = jsonarray.getJSONObject(i);
                             time= (String) jsonObject.getString("TIME");
                            CO = (Double) jsonObject.getDouble("CO");
                            CO_AQI = (Double) jsonObject.getDouble("CO_AQI");
                            CO2 = (Double) jsonObject.getDouble("CO2");
                            CO2_AQI = (Double) jsonObject.getDouble("CO2_AQI");
                            SO2 = (Double) jsonObject.getDouble("SO2");
                            SO2_AQI = (Double) jsonObject.getDouble("SO2_AQI");
                            NO2 = (Double) jsonObject.getDouble("NO2");
                            NO2_AQI= (Double) jsonObject.getDouble("NO2_AQI");
                            O3= (Double) jsonObject.getDouble("O3");
                            O3_AQI = (Double) jsonObject.getDouble("O3_AQI");
                            PM25 = (Double) jsonObject.getDouble("PM25");
                            PM25_AQI = (Double) jsonObject.getDouble("PM25_AQI");
                            PM10 = (Double) jsonObject.getDouble("PM10");
                            PM10_AQI = (Double) jsonObject.getDouble("PM10_AQI");
                            AQI_avg = (Double) jsonObject.getDouble("AQI_avg");
                            TEMP = (Double) jsonObject.getDouble("TEMP");

                        }
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
