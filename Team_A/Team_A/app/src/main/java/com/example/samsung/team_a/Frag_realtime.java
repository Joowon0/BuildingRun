package com.example.samsung.team_a;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import java.util.Random;


public class Frag_realtime extends Fragment {
    Context context;
    private TextView txt_CO, txt_NO2, txt_temperature, txt_O3, txt_SO2, txt_PM25, textView5;
    private double FCO = 0.0, FNO2 = 0.0, Ftemperature = 0.0, FO3 = 0.0, FSO2 = 0.0, FPM25 = 0.0;
    public String Dtype;
    private String Fdate;
    private String Fresult = "";
    public static String FMAC = "";
    private String time;
    private String result;
    private HttpURLConnection conn;
    Handler mhandler = new Handler();
    private BluetoothAdapter bt_adapter = null;
    private BluetoothConnection bt_connection;
    private BluetoothDevice bt_device;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;
    public static int types = 0;
    Intent main_to_devicelist;
    private String Realtime;
    dataTransfer datas = new dataTransfer();
    historytoServer histoserver = new historytoServer();
    private ProgressBar mProgressBar;
    private int mProgressStatus = 0;

    public static Frag_realtime newInstance(Context context) {
        Frag_realtime frag_realtime = new Frag_realtime();
        frag_realtime.context = context;
        return frag_realtime;
        // required
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.frag_realtime, container, false);

        main_to_devicelist = new Intent(getActivity(), DeviceListActivity.class);
        startActivityForResult(main_to_devicelist, REQUEST_CONNECT_DEVICE_SECURE);
        bt_adapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (!bt_adapter.isEnabled()) {
            Intent bt_enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bt_enable, REQUEST_ENABLE_BT);
        }

        txt_CO = (TextView) layout.findViewById(R.id.txt_CO);
        txt_NO2 = (TextView) layout.findViewById(R.id.txt_NO2);
        txt_temperature = (TextView) layout.findViewById(R.id.txt_temperature);
        txt_O3 = (TextView) layout.findViewById(R.id.txt_O3);
        txt_SO2 = (TextView) layout.findViewById(R.id.txt_SO2);
        txt_PM25 = (TextView) layout.findViewById(R.id.txt_PM25);
        textView5 = (TextView) layout.findViewById(R.id.textView5);
        txt_CO.setText("CO : " + FCO);
        txt_NO2.setText("NO2 : " + FNO2);
        txt_O3.setText("O3 : " + FO3);
        txt_SO2.setText("SO2 : " + FSO2);
        txt_temperature.setText("Temperature : " + Ftemperature);
        txt_PM25.setText("PM2.5 : " + FPM25);
        textView5.setText("conn : " + Fresult);
        Handler sHandler = new Handler() {
            public void handleMessage(Message msg) {

            }
        };
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(5000);
                        if (!FMAC.equals("")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Log.d("들어옴", "ㅅㅂ");
                                    Log.d("datas.get들", String.valueOf(datas.getCO()) + String.valueOf(datas.getNO2()) + String.valueOf(datas.getO3()));

                                    TextView txt_CO = (TextView) layout.findViewById(R.id.txt_CO);
                                    TextView txt_NO2 = (TextView) layout.findViewById(R.id.txt_NO2);
                                    TextView txt_temperature = (TextView) layout.findViewById(R.id.txt_temperature);
                                    TextView txt_O3 = (TextView) layout.findViewById(R.id.txt_O3);
                                    TextView txt_SO2 = (TextView) layout.findViewById(R.id.txt_SO2);
                                    TextView txt_PM25 = (TextView) layout.findViewById(R.id.txt_PM25);
                                    TextView textView5 = (TextView) layout.findViewById(R.id.textView5);
                                    txt_CO.setText("CO : " + FCO);
                                    txt_NO2.setText("NO2 : " + FNO2);
                                    txt_O3.setText("O3 : " + FO3);
                                    txt_SO2.setText("SO2 : " + FSO2);
                                    txt_temperature.setText("Temperature : " + Ftemperature);
                                    txt_PM25.setText("PM2.5 : " + FPM25);
                                    textView5.setText("conn : " + Fresult);
                                    ProgressBar progressCo = (ProgressBar) layout.findViewById(R.id.progressCo);
                                    ProgressBar progressNo2 = (ProgressBar) layout.findViewById(R.id.progressNo2);
                                    ProgressBar progressTemp = (ProgressBar) layout.findViewById(R.id.progressTemp);
                                    ProgressBar progressO3 = (ProgressBar) layout.findViewById(R.id.progressO3);
                                    ProgressBar progressSo2 = (ProgressBar) layout.findViewById(R.id.progressSo2);
                                    ProgressBar progressPm25 = (ProgressBar) layout.findViewById(R.id.progressPm25);
                                    progressCo.setMax(500);
                                    progressNo2.setMax(1000);
                                    progressSo2.setMax(300);
                                    progressPm25.setMax(25);
                                    progressO3.setMax(1850);
                                    progressTemp.setMax(30);
                                    AirtoServer airtoServer = new AirtoServer();
                                    airtoServer.execute();
                                    if (FCO <= 4.4) {
                                        progressCo.getProgressDrawable().setColorFilter(Color.rgb(51, 204, 51), PorterDuff.Mode.SRC_IN);
                                    } else if ((FCO) <= 9.4) {
                                        progressCo.getProgressDrawable().setColorFilter(Color.rgb(255, 153, 51), PorterDuff.Mode.SRC_IN);
                                    } else if ((FCO) <= 12.4) {
                                        progressCo.getProgressDrawable().setColorFilter(Color.rgb(255, 0, 0), PorterDuff.Mode.SRC_IN);
                                    } else {
                                        progressCo.getProgressDrawable().setColorFilter(Color.rgb(77, 0, 25), PorterDuff.Mode.SRC_IN);
                                    }

                                    if ((FNO2) <= 53.0) {
                                        progressNo2.getProgressDrawable().setColorFilter(Color.rgb(51, 204, 51), PorterDuff.Mode.SRC_IN);
                                    } else if ((FNO2) <= 100.0) {
                                        progressNo2.getProgressDrawable().setColorFilter(Color.rgb(255, 153, 51), PorterDuff.Mode.SRC_IN);
                                    } else if ((FNO2) <= 360.0) {
                                        progressNo2.getProgressDrawable().setColorFilter(Color.rgb(255, 0, 0), PorterDuff.Mode.SRC_IN);
                                    } else {
                                        progressNo2.getProgressDrawable().setColorFilter(Color.rgb(77, 0, 25), PorterDuff.Mode.SRC_IN);
                                    }

                                    if ((Ftemperature) <= 17.0) {
                                        progressTemp.getProgressDrawable().setColorFilter(Color.rgb(51, 204, 51), PorterDuff.Mode.SRC_IN);
                                    } else if ((Ftemperature) <= 20.0) {
                                        progressTemp.getProgressDrawable().setColorFilter(Color.rgb(255, 153, 51), PorterDuff.Mode.SRC_IN);
                                    } else if ((Ftemperature) <= 23.0) {
                                        progressTemp.getProgressDrawable().setColorFilter(Color.rgb(255, 0, 0), PorterDuff.Mode.SRC_IN);
                                    } else {
                                        progressTemp.getProgressDrawable().setColorFilter(Color.rgb(77, 0, 25), PorterDuff.Mode.SRC_IN);
                                    }

                                    if ((FO3) <= 54.0) {
                                        progressO3.getProgressDrawable().setColorFilter(Color.rgb(51, 204, 51), PorterDuff.Mode.SRC_IN);
                                    } else if ((FO3) <= 70.0) {
                                        progressO3.getProgressDrawable().setColorFilter(Color.rgb(255, 153, 51), PorterDuff.Mode.SRC_IN);
                                    } else if ((FO3) <= 85.0) {
                                        progressO3.getProgressDrawable().setColorFilter(Color.rgb(255, 0, 0), PorterDuff.Mode.SRC_IN);
                                    } else {
                                        progressO3.getProgressDrawable().setColorFilter(Color.rgb(77, 0, 25), PorterDuff.Mode.SRC_IN);
                                    }

                                    if ((FSO2) <= 35.0) {
                                        progressSo2.getProgressDrawable().setColorFilter(Color.rgb(51, 204, 51), PorterDuff.Mode.SRC_IN);
                                    } else if ((FSO2) <= 75.0) {
                                        progressSo2.getProgressDrawable().setColorFilter(Color.rgb(255, 153, 51), PorterDuff.Mode.SRC_IN);
                                    } else if ((FSO2) <= 185.0) {
                                        progressSo2.getProgressDrawable().setColorFilter(Color.rgb(255, 0, 0), PorterDuff.Mode.SRC_IN);
                                    } else {
                                        progressSo2.getProgressDrawable().setColorFilter(Color.rgb(77, 0, 25), PorterDuff.Mode.SRC_IN);
                                    }

                                    if ((FPM25) <= 12.0) {
                                        progressPm25.getProgressDrawable().setColorFilter(Color.rgb(51, 204, 51), PorterDuff.Mode.SRC_IN);
                                    } else if ((FPM25) <= 35.4) {
                                        progressPm25.getProgressDrawable().setColorFilter(Color.rgb(255, 153, 51), PorterDuff.Mode.SRC_IN);
                                    } else if ((FPM25) <= 55.4) {
                                        progressPm25.getProgressDrawable().setColorFilter(Color.rgb(255, 0, 0), PorterDuff.Mode.SRC_IN);
                                    } else {
                                        progressPm25.getProgressDrawable().setColorFilter(Color.rgb(77, 0, 25), PorterDuff.Mode.SRC_IN);
                                    }

                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
        return layout;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            ConnectDevice(data, true);
        }
    }

    private void ConnectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        // Get the BluetoothDevice object

        bt_device = bt_adapter.getRemoteDevice(address);
        // Attempt to connect to the device
        bt_connection = new BluetoothConnection(bt_device, bt_adapter, bt_receivemsg);
        bt_connection.start();
        FMAC = bt_device.getAddress().toString();
        Toast.makeText(context, "CONNECTED WITH " + bt_device.getAddress() + " " + bt_device.getName(), Toast.LENGTH_SHORT).show();
        BluetoothConnection.checkconnect = 1;

    }


    private Handler bt_receivemsg = new Handler() { // ★ 메세지
        @Override
        public void handleMessage(Message message) {
            Bundle data = message.getData();
            Realtime = data.getString("realtime");
            try {
                JSONObject jsonDataAir = new JSONObject(Realtime);
                Dtype = (String) jsonDataAir.getString("TYPE");
                Log.d("TYPE CHECK", Dtype);
                if (Dtype.equals("H")) {
                    JSONArray jsonHistoryARRAY = jsonDataAir.getJSONArray("DATA");
                    for (int i = 0; i < jsonHistoryARRAY.length(); i++) {
                        JSONObject jsonhistory = jsonHistoryARRAY.getJSONObject(i);
                        FMAC = (String) jsonhistory.getString("MAC");
                        time = (String) jsonhistory.getString("TIME");
                        FNO2 = (Double) jsonhistory.getDouble("NO2");
                        FO3 = (Double) jsonhistory.getDouble("O3");
                        FCO = (Double) jsonhistory.getDouble("CO");
                        FSO2 = (Double) jsonhistory.getDouble("SO2");
                        FPM25 = (Double) jsonhistory.getDouble("PM25");
                        Ftemperature = (Double) jsonhistory.getDouble("TEMP");
                        result = "Received History Data";
                        Log.d("HISTORY DATA UPDATE :" + String.valueOf(i), "MAC : " + FMAC + " time : " + time + " NO2 : " + FNO2 + " O3 : " + FO3
                                + " CO : " + FCO + " SO2 : " + FSO2 + " PM25 : " + FPM25 + " temperature : " + Ftemperature);
                        Frag_realtime.historytoServer histooserver = new historytoServer();
                        histooserver.execute();

                    }
                } else if (Dtype.equals("R")) {
                    JSONObject jsonRealAir = jsonDataAir.getJSONObject("DATA");
                    FMAC = (String) jsonRealAir.getString("MAC");
                    time = (String) jsonRealAir.getString("TIME");
                    FNO2 = (Double) jsonRealAir.getDouble("NO2");
                    FO3 = (Double) jsonRealAir.getDouble("O3");
                    FCO = (Double) jsonRealAir.getDouble("CO");
                    FSO2 = (Double) jsonRealAir.getDouble("SO2");
                    FPM25 = (Double) jsonRealAir.getDouble("PM25");
                    Ftemperature = (Double) jsonRealAir.getDouble("TEMP");
                    result = "Received REAL Data";
                    datas.setCO(FCO);
                    datas.setNO2(FNO2);
                    datas.setSO2(FSO2);
                    datas.setO3(FO3);
                    datas.setPM25(FPM25);
                    datas.setTemperature(Ftemperature);
                    Log.d("REAL DATA UPDATE :", "MAC : " + FMAC + " time : " + time + " NO2 : " + FNO2 + " O3 : " + FO3
                            + " CO : " + FCO + " SO2 : " + FSO2 + " PM25 : " + FPM25 + " temperature : " + Ftemperature);


                }
            } catch (JSONException e) {
                result = "Received Failed";
                e.printStackTrace();
            }

        }
    };

    class AirtoServer extends AsyncTask<String, Integer, Integer> {
        Context context;

        AirtoServer() {

        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            int result = airtoserver();
            if (result == 1) {
                publishProgress(1);
            } else {
                publishProgress(2);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            if (value[0] == 1) {
                Fresult = dataTransfer.getMac() + "connecting..";
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

            try {
                URL url = new URL("http://teama-iot.calit2.net/app/airQualityDataTransfer");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("USN", loginActivity.ST_usn);
                    json.put("TIME", currentDateandTime);
                    json.put("MAC", BluetoothConnection.mmDevice.getAddress());
                    json.put("latitude", MapsActivity.mLastKnownLocation.getLatitude());
                    json.put("longitude", MapsActivity.mLastKnownLocation.getLongitude());
                    json.put("CO", dataTransfer.getCO());
                    json.put("SO2", dataTransfer.getSO2());
                    json.put("NO2", dataTransfer.getNO2());
                    json.put("O3", dataTransfer.getO3());
                    json.put("PM25", dataTransfer.getPM25());
                    json.put("TEMP", dataTransfer.getTemperature());
                    Log.d("to server", BluetoothConnection.mmDevice.getAddress() + dataTransfer.getCO() + dataTransfer.getSO2());
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

            return result;
        }

    }

    class historytoServer extends AsyncTask<String, Integer, Integer> {
        Context context;

        historytoServer() {

        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            int result = historytoServer();
            if (result == 1) {
                publishProgress(1);
            } else {
                publishProgress(2);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            if (value[0] == 1) {
                Fresult = dataTransfer.getMac() + "connecting..";
            } else if (value[0] == 2) {
                Fresult = "Please connect again";
            }
        }

        public int historytoServer() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            int result = 0;
            int usn;

            try {
                URL url = new URL("http://teama-iot.calit2.net/app/airQualityDataTransfer");
                conn = (HttpURLConnection) url.openConnection();
                JSONArray jsonhis = new JSONArray();

                for (int i = 0; i < jsonhis.length(); i++) {

                    JSONObject json = jsonhis.getJSONObject(i);
                    try {
                        json.put("USN", loginActivity.ST_usn);
                        json.put("TIME", currentDateandTime);
                        json.put("MAC", BluetoothConnection.mmDevice.getAddress());
                        json.put("latitude", MapsActivity.mLastKnownLocation.getLatitude());
                        json.put("longitude", MapsActivity.mLastKnownLocation.getLongitude());
                        json.put("CO", dataTransfer.getCO());
                        json.put("SO2", dataTransfer.getSO2());
                        json.put("NO2", dataTransfer.getNO2());
                        json.put("O3", dataTransfer.getO3());
                        json.put("PM25", dataTransfer.getPM25());
                        json.put("TEMP", dataTransfer.getTemperature());
                        Log.d("to server", BluetoothConnection.mmDevice.getAddress() + dataTransfer.getCO() + dataTransfer.getSO2());
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



