package com.example.samsung.team_a;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Frag_realtime extends Fragment {
    Context context;
    public static TextView txt_CO, txt_NO2, txt_temperature, txt_O3, txt_SO2, txt_PM25, textView5;
    public static double FCO = 0.0, FNO2 = 0.0, Ftemperature = 0.0, FO3 = 0.0, FSO2 = 0.0, FPM25=0.0;
    public static String date;
    public static String Fresult = "";
    public static String MAC;
    private String time;
    private HttpURLConnection conn;
    Handler mhandler = new Handler();
    private BluetoothAdapter bt_adapter = null;
    private BluetoothConnection bt_connection;
    private BluetoothDevice bt_device;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;
    Intent main_to_devicelist;
    private String Realtime;

    public Frag_realtime() {
        // required
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bt_adapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (!bt_adapter.isEnabled()) {
            Intent bt_enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bt_enable, REQUEST_ENABLE_BT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.frag_realtime, container, false);
        bt_adapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (!bt_adapter.isEnabled()) {
            Intent bt_enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bt_enable, REQUEST_ENABLE_BT);
        }
        TextView txt_CO = (TextView) layout.findViewById(R.id.txt_CO);
        TextView txt_NO2 = (TextView) layout.findViewById(R.id.txt_NO2);
        TextView txt_temperature = (TextView) layout.findViewById(R.id.txt_temperature);
        TextView txt_O3 = (TextView) layout.findViewById(R.id.txt_O3);
        TextView txt_SO2 = (TextView) layout.findViewById(R.id.txt_SO2);
        TextView txt_PM25 = (TextView) layout.findViewById(R.id.txt_PM25);
        TextView textView5 = (TextView) layout.findViewById(R.id.textView5);
        txt_CO.setText("CO : " + String.valueOf(MapsActivity.CO));
        txt_NO2.setText("NO2 : " + String.valueOf(MapsActivity.NO2));
        txt_O3.setText("O3 : " + String.valueOf(MapsActivity.O3));
        txt_SO2.setText("SO2 : " + String.valueOf(MapsActivity.SO2));
        txt_temperature.setText("Temperature : " + String.valueOf(MapsActivity.temperature));
        txt_PM25.setText("PM2.5 : " + String.valueOf(MapsActivity.PM25));
        textView5.setText("conn : " + Fresult);
        main_to_devicelist = new Intent(getActivity(), DeviceListActivity.class);
        //startActivityForResult(main_to_devicelist, REQUEST_CONNECT_DEVICE_SECURE);
        if (BluetoothConnection.connectFlag) {
            dataset();
        }

        //getdata();

        return layout;
    }

    public void settxt(Double co, Double no2, Double o3, Double so2, Double temp, Double pm25, String results) {
        txt_CO.setText("CO : " + co);
        txt_NO2.setText("NO2 : " + no2);
        txt_O3.setText("O3 : " + o3);
        txt_SO2.setText("SO2 : " + so2);
        txt_temperature.setText("Temperature : " + temp);
        txt_PM25.setText("PM2.5 : " + pm25);
        textView5.setText("conn : " + results);

    }


    class AirtoServer extends AsyncTask<String, Integer, Integer> {
        Context context;

        AirtoServer() {

        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            int result = airtoserver();
            switch (result) {
                case 1:
                    publishProgress(1);
                    break;

                case 2:
                    publishProgress(2);

                    break;
                default:
//                    Toast.makeText(getApplicationContext(), "System Error/Connection Fail", Toast.LENGTH_SHORT).show();
                    break;
            }
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            if (value[0] == 1) {
                Fresult = MAC+"connecting..";
            } else if (value[0] == 2) {
                Fresult = "Please connect again";
            }
        }

        public int airtoserver() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            int result = 0;
            int usn;
            if (MapsActivity.CO==0.0&&MapsActivity.SO2==0.0&&MapsActivity.NO2==0.0&&MapsActivity.O3==0.0&&MapsActivity.PM25==0.0&&MapsActivity.temperature==0.0) {
            }else{
                try {
                    URL url = new URL("http://teama-iot.calit2.net/app/airQualityDataTransfer");
                    conn = (HttpURLConnection) url.openConnection();

                    JSONObject json = new JSONObject();
                    try {
                        json.put("USN", loginActivity.ST_usn);
                        json.put("TIME", currentDateandTime);
                        json.put("MAC", MapsActivity.MAC);
                        json.put("latitude", MapsActivity.mLastKnownLocation.getLatitude());
                        json.put("longitude", MapsActivity.mLastKnownLocation.getLongitude());
                        json.put("CO", txt_CO.getText());
                        json.put("SO2", txt_SO2.getText());
                        json.put("NO2", txt_NO2.getText());
                        json.put("O3", txt_O3.getText());
                        json.put("PM25", txt_PM25.getText());
                        json.put("TEMP", txt_temperature.getText());


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
                            Log.d("response", response);
                            JSONObject responseJSON = new JSONObject(response);
                            result = (Integer) responseJSON.getInt("Result");
                            Log.d("AIRQUAILITY RESULT: ", String.valueOf(result));
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
            }
            return result;
        }

    }

    public void getdata() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dataset();
            }
        };
        new Thread(runnable).start();
    }

    public void dataset() {
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (BluetoothConnection.connectFlag = true) {
                        TextView txt_CO = (TextView) getView().findViewById(R.id.txt_CO);
                        TextView txt_NO2 = (TextView) getView().findViewById(R.id.txt_NO2);
                        TextView txt_O3 = (TextView) getView().findViewById(R.id.txt_O3);
                        TextView txt_SO2 = (TextView) getView().findViewById(R.id.txt_SO2);
                        TextView txt_temperature = (TextView) getView().findViewById(R.id.txt_temperature);
                        TextView txt_PM25 = (TextView) getView().findViewById(R.id.txt_PM25);
                        TextView textView5 = (TextView) getView().findViewById(R.id.textView5);
                        txt_CO.setText("CO : " + MapsActivity.CO);
                        txt_NO2.setText("NO2 : " + MapsActivity.NO2);
                        txt_O3.setText("O3 : " + MapsActivity.O3);
                        txt_SO2.setText("SO2 : " + MapsActivity.SO2);
                        txt_temperature.setText("Temperature : " + MapsActivity.temperature);
                        txt_PM25.setText("PM2.5 : " + MapsActivity.PM25);
                        textView5.setText("conn : " + Fresult);

                        AirtoServer airtoServer = new AirtoServer();
                        airtoServer.execute();
                    } else {
                        Thread.interrupted();
                    }

                    if (MapsActivity.mapback == true) {
                        Thread.interrupted();
                    }
                    MapsActivity.mapback = false;
                }
            });
        }
    }

}


