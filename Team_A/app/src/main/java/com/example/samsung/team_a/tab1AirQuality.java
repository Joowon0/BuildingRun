package com.example.samsung.team_a;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class tab1AirQuality extends Fragment {
    ListView listview = null;
    private HttpURLConnection conn;
    Context context;
    private LineChart mChart;
    private ArrayList<String> DataTypeInfoFromServer, DateInfoFromServer, XAixsLabels;
    private static final String TAG = "tab1_airquality";
    private TextView txtSearch,txt_date;
    private RadioGroup radio_g;
    private RadioButton radio_CO, radio_NO2, radio_SO2, radio_O3, radio_Tmp, radio_PM25;
    public static TextView deviceview;
    private String time = "0";
    public static float a = 0;
    private int count = 0;
    private String[] CO, NO2, SO2, O3, PM25, TMP, TIME;
    private double VCO = 0.0, VSO2 = 0.0, VNO2 = 0.0, VO3 = 0.0, VPM25 = 0.0, VTEMP = 0.0;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar cal = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String currentDateandTime = sdf.format(new Date());
    //ArrayList<info> SensorInfoList = new ArrayList<info>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab1_airquality, container, false);
        //Intent in = new Intent(getContext(), DeviceListActivity.class);
        //startActivity(in);
        CO = new String[10];
        NO2 = new String[10];
        SO2 = new String[10];
        O3 = new String[10];
        PM25 = new String[10];
        TMP = new String[10];
        TIME = new String[10];

        cal.add(Calendar.DAY_OF_MONTH, -10);
        Date from = cal.getTime();
        String a =new SimpleDateFormat("yyyy-MM-dd").format(from);

        Date toToday = new Date();
        SimpleDateFormat s1;
        s1 = new SimpleDateFormat("yyyy-MM-dd");
        DataTypeInfoFromServer = new ArrayList<>();
        DateInfoFromServer = new ArrayList<>();
        XAixsLabels = new ArrayList<>();
        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radiogroup);
        radio_CO = (RadioButton) rootView.findViewById(R.id.radio_CO);
        radio_NO2 = (RadioButton) rootView.findViewById(R.id.radio_NO2);
        radio_SO2 = (RadioButton) rootView.findViewById(R.id.radio_SO2);
        radio_O3 = (RadioButton) rootView.findViewById(R.id.radio_O3);
        radio_Tmp = (RadioButton) rootView.findViewById(R.id.radio_Tmp);
        txtSearch = (TextView) rootView.findViewById(R.id.txtSearch);
        ImageView bluetooth = (ImageView) rootView.findViewById(R.id.imageBT);
        mChart = (LineChart) rootView.findViewById(R.id.graph);
        deviceview = (TextView) rootView.findViewById(R.id.txt_device);
        deviceview.setText("your device : " + DeviceListActivity.address);
        txt_date = (TextView) rootView.findViewById(R.id.txt_date);
        txt_date.setText(a+" ~ "+s1.format(toToday));
        ArrayList<Entry> entries = new ArrayList<>();

        LineDataSet lineDataSet = new LineDataSet(entries, "Click the radio button");
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setValueTextSize(10);
        lineDataSet.setColor(Color.parseColor("#B5C2C3"));

        lineDataSet.setLineWidth(3);
        lineDataSet.setCircleSize(6);
        lineDataSet.setCircleColor(Color.parseColor("#B5C2C3"));
        lineDataSet.setCircleColorHole(Color.parseColor("#B5C2C3"));
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);

        ArrayList<String> labels = new ArrayList<String>();

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serverIntent = new Intent(getContext(), DeviceListActivity.class);
                startActivity(serverIntent);
            }
        });
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setLabelCount(5, false);
        leftAxis.setSpaceTop(20f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setLabelCount(5, false);
        rightAxis.setSpaceTop(20f);

        LineData lineData = new LineData(labels, lineDataSet);
        mChart.setData(lineData);
        mChart.setDescription("");

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.animateY(800, Easing.EasingOption.EaseInCubic);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        AQtoServer aqdvbw = new AQtoServer();
                        aqdvbw.execute();
                        deviceview = (TextView) getView().findViewById(R.id.txt_device);
                        deviceview.setText("your device : " + DeviceListActivity.address);

                        switch (checkedId) {
                            case R.id.radio_CO:

                                Toast.makeText(getContext(), "Data loading...", Toast.LENGTH_LONG).show();
                                mChart = (LineChart) rootView.findViewById(R.id.graph);

                                ArrayList<Entry> entries = new ArrayList<>();
                                for (int i = 0; i < count; i++) {
                                    entries.add(new Entry(Float.parseFloat(CO[i]), i));
                                    Log.d("entry", entries.toString());
                                }
                                LineDataSet lineDataSet = new LineDataSet(entries, "CO");
                                lineDataSet.setValueTextColor(Color.WHITE);
                                lineDataSet.setValueTextSize(15);
                                lineDataSet.setColor(Color.parseColor("#B5C2C3"));
                                lineDataSet.setLineWidth(3);
                                lineDataSet.setCircleSize(6);
                                lineDataSet.setCircleColor(Color.parseColor("#B5C2C3"));
                                lineDataSet.setCircleColorHole(Color.parseColor("#B5C2C3"));
                                lineDataSet.setDrawCircles(true);
                                lineDataSet.setDrawCircleHole(true);

                                ArrayList<String> labels = new ArrayList<String>();
                                for (int i = 0; i < count; i++) {
                                    labels.add(TIME[i]);
                                }
                                LineData lineData = new LineData(labels, lineDataSet);
                                mChart.setData(lineData);


                                break;
                            case R.id.radio_NO2:
                                ArrayList<Entry> entries_No2 = new ArrayList<>();

                                Toast.makeText(getContext(), "Data loading...", Toast.LENGTH_LONG).show();
                                for (int i = 0; i < count; i++) {
                                    entries_No2.add(new Entry(Float.parseFloat(NO2[i]), i));
                                }
                                LineDataSet lineDataSet_No2 = new LineDataSet(entries_No2, "NO2");
                                lineDataSet_No2.setValueTextColor(Color.WHITE);
                                lineDataSet_No2.setValueTextSize(15);
                                lineDataSet_No2.setColor(Color.parseColor("#B5C2C3"));
                                lineDataSet_No2.setLineWidth(3);
                                lineDataSet_No2.setCircleSize(6);
                                lineDataSet_No2.setCircleColor(Color.parseColor("#B5C2C3"));
                                lineDataSet_No2.setCircleColorHole(Color.parseColor("#B5C2C3"));
                                lineDataSet_No2.setDrawCircles(true);
                                lineDataSet_No2.setDrawCircleHole(true);

                                ArrayList<String> labels_No2 = new ArrayList<String>();

                                for (int i = 0; i < count; i++) {
                                    labels_No2.add(TIME[i]);
                                }
                                LineData lineData_No2 = new LineData(labels_No2, lineDataSet_No2);
                                mChart.setData(lineData_No2);



                                break;
                            case R.id.radio_O3:

                                Toast.makeText(getContext(), "Data loading...", Toast.LENGTH_LONG).show();
                                mChart = (LineChart) rootView.findViewById(R.id.graph);

                                ArrayList<Entry> O3entries = new ArrayList<>();

                                for (int i = 0; i < count; i++) {
                                    O3entries.add(new Entry(Float.parseFloat(O3[i]), i));
                                }
                                LineDataSet O3dataset = new LineDataSet(O3entries, "O3");
                                O3dataset.setValueTextColor(Color.WHITE);
                                O3dataset.setValueTextSize(15);
                                O3dataset.setColor(Color.parseColor("#B5C2C3"));
                                O3dataset.setLineWidth(3);
                                O3dataset.setCircleSize(6);
                                O3dataset.setCircleColor(Color.parseColor("#B5C2C3"));
                                O3dataset.setCircleColorHole(Color.parseColor("#B5C2C3"));
                                O3dataset.setDrawCircles(true);
                                O3dataset.setDrawCircleHole(true);

                                ArrayList<String> O3labels = new ArrayList<String>();

                                for (int i = 0; i < count; i++) {
                                    O3labels.add(TIME[i]);
                                }
                                LineData O3data = new LineData(O3labels, O3dataset);
                                mChart.setData(O3data);


                                break;
                            case R.id.radio_PM25:

                                Toast.makeText(getContext(), "Data loading...", Toast.LENGTH_LONG).show();
                                mChart = (LineChart) rootView.findViewById(R.id.graph);

                                ArrayList<Entry> PM25entries = new ArrayList<>();
                                for (int i = 0; i < count; i++) {
                                    PM25entries.add(new Entry(Float.parseFloat(PM25[i]), i));
                                }
                                LineDataSet PMdataset = new LineDataSet(PM25entries, "PM2.5");
                                PMdataset.setValueTextColor(Color.WHITE);
                                PMdataset.setValueTextSize(15);
                                PMdataset.setColor(Color.parseColor("#B5C2C3"));
                                PMdataset.setLineWidth(3);
                                PMdataset.setCircleSize(6);
                                PMdataset.setCircleColor(Color.parseColor("#B5C2C3"));
                                PMdataset.setCircleColorHole(Color.parseColor("#B5C2C3"));
                                PMdataset.setDrawCircles(true);
                                PMdataset.setDrawCircleHole(true);

                                ArrayList<String> PMlabel = new ArrayList<String>();
                                for (int i = 0; i < count; i++) {
                                    PMlabel.add(TIME[i]);
                                }
                                LineData PMdata = new LineData(PMlabel, PMdataset);
                                mChart.setData(PMdata);


                                break;
                            case R.id.radio_SO2:

                                Toast.makeText(getContext(), "Data loading...", Toast.LENGTH_LONG).show();
                                mChart = (LineChart) rootView.findViewById(R.id.graph);

                                ArrayList<Entry> SO2entries = new ArrayList<>();

                                for (int i = 0; i < count; i++) {
                                    SO2entries.add(new Entry(Float.parseFloat(SO2[i]), i));
                                }
                                LineDataSet SO2dataset = new LineDataSet(SO2entries, "SO2");
                                SO2dataset.setValueTextColor(Color.WHITE);
                                SO2dataset.setValueTextSize(15);
                                SO2dataset.setColor(Color.parseColor("#B5C2C3"));
                                SO2dataset.setLineWidth(3);
                                SO2dataset.setCircleSize(6);
                                SO2dataset.setCircleColor(Color.parseColor("#B5C2C3"));
                                SO2dataset.setCircleColorHole(Color.parseColor("#B5C2C3"));
                                SO2dataset.setDrawCircles(true);
                                SO2dataset.setDrawCircleHole(true);

                                ArrayList<String> SO2labels = new ArrayList<String>();

                                for (int i = 0; i < count; i++) {
                                    SO2labels.add(TIME[i]);
                                }
                                LineData SO2setdata = new LineData(SO2labels, SO2dataset);
                                mChart.setData(SO2setdata);


                                break;
                            case R.id.radio_Tmp:
                                Toast.makeText(getContext(), "Data loading...", Toast.LENGTH_LONG).show();
                                mChart = (LineChart) rootView.findViewById(R.id.graph);

                                ArrayList<Entry> TMPentries = new ArrayList<>();
                                for (int i = 0; i < count; i++) {
                                    TMPentries.add(new Entry(Float.parseFloat(TMP[i]), i));
                                }
                                LineDataSet TMPdataset = new LineDataSet(TMPentries, "Temperature");
                                TMPdataset.setValueTextColor(Color.WHITE);
                                TMPdataset.setValueTextSize(15);
                                TMPdataset.setColor(Color.parseColor("#B5C2C3"));
                                TMPdataset.setLineWidth(3);
                                TMPdataset.setCircleSize(6);
                                TMPdataset.setCircleColor(Color.parseColor("#B5C2C3"));
                                TMPdataset.setCircleColorHole(Color.parseColor("#B5C2C3"));
                                TMPdataset.setDrawCircles(true);
                                TMPdataset.setDrawCircleHole(true);

                                ArrayList<String> TMPlabels = new ArrayList<String>();

                                for (int i = 0; i < count; i++) {
                                    TMPlabels.add(TIME[i]);
                                }
                                LineData TMPline = new LineData(TMPlabels, TMPdataset);
                                mChart.setData(TMPline);


                                break;
                            default:
                                // do operations specific to this selection
                                break;
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mChart.setDescription("");
                                mChart.setDrawGridBackground(false);
                                mChart.setTouchEnabled(true);
                                mChart.setDragEnabled(true);
                                mChart.setScaleEnabled(true);
                                mChart.animateY(800, Easing.EasingOption.EaseInCubic);
                                mChart.invalidate();
                            }
                        }, 3000);
                        count = 0;
                    }
                });
        // 데이터 목록을 저장하는 어레이 변수


        return rootView;
    }

    Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    class AQtoServer extends AsyncTask<String, Integer, Integer> {
        Context context;

        AQtoServer() {
        }

        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... value) {
            int result = aqtoserver();
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
                Log.d("history", "success");
            } else if (value[0] == 2) {
                Log.d("history", "failed");
            }
        }

        public int aqtoserver() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            int result = 0;
            String Vtime = "";
            int usn;
            try {
                URL url = new URL("http://teama-iot.calit2.net/app/airQualityHistory");
                conn = (HttpURLConnection) url.openConnection();
                cal.add(Calendar.DAY_OF_MONTH, -10);
                Date tendaysago = cal.getTime();
                JSONObject json = new JSONObject();
                try {
                    json.put("MAC", DeviceListActivity.address);
                    json.put("period", 4);
                    json.put("startDate", new SimpleDateFormat("yyyy-MM-dd").format(tendaysago));
                    json.put("endDate", currentDateandTime);

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
                        JSONArray responseJSON = new JSONArray(response);
                        for (int i = 0; i < responseJSON.length(); i++) {
                            JSONObject airqualitys = responseJSON.getJSONObject(i);
                            if (!String.valueOf(airqualitys.getDouble("CO")).equals("")) {
                                try {
                                    VCO = airqualitys.getDouble("CO");
                                    VSO2 = airqualitys.getDouble("SO2");
                                    VNO2 = airqualitys.getDouble("NO2");
                                    VO3 = airqualitys.getDouble("O3");
                                    VPM25 = airqualitys.getDouble("PM25");
                                    VTEMP = airqualitys.getDouble("TEMP");

                                    TIME[i] = airqualitys.getString("TIME");
                                    CO[i] = String.valueOf(VCO);
                                    SO2[i] = String.valueOf(VSO2);
                                    NO2[i] = String.valueOf(VNO2);
                                    O3[i] = String.valueOf(VO3);
                                    PM25[i] = String.valueOf(VPM25);
                                    TMP[i] = String.valueOf(VTEMP);
                                    Log.d("TIME", String.valueOf(TIME[i]));
                                    Log.d("CO", String.valueOf(VCO));
                                    Log.d("SO2", String.valueOf(VSO2));
                                    Log.d("NO2", String.valueOf(VNO2));
                                    Log.d("O3", String.valueOf(VO3));
                                    Log.d("PM25", String.valueOf(VPM25));
                                    Log.d("TEMP", String.valueOf(VTEMP));
                                    Log.d("time", time);
                                    Log.d("testSADAS", CO[i].toString() + NO2[i].toString() + SO2[i].toString() + O3[i].toString() + PM25[i].toString() + TMP[i].toString());
                                    result = 1;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                CO[i] = "0";
                            }
                            Log.d("CO[i]", CO[i]);
                            count++;
                        }
                        is.close();
                        os.close();
                        conn.disconnect();
                    }
                } else {
                    Log.d("JSON", "Connection fail");
                    result = 2;
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                result = 2;
            } catch (Exception ex) {
                ex.printStackTrace();
                result = 2;
                Log.d("JSON_2line:", "problem");
            }
            return result;
        }
    }
}