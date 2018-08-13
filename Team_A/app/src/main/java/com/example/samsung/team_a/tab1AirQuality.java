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
    public static BluetoothSocket mmSocket = null;
    public static BluetoothDevice mmDevice = null;
    ArrayList<info> SensorInfoList = new ArrayList<>();
    Context context;
    private LineChart mChart;
    private ArrayList<String> DataTypeInfoFromServer, DateInfoFromServer, XAixsLabels;
    private String[] DateTempArray;
    private String yourdevice = "";
    //XAxis xAxis;
    private static final String TAG = "tab1_airquality";
    //private LineChart mChart;
    private TextView txtSearch;
    private DatePickerDialog.OnDateSetListener mDateSetListener1;
    private DatePickerDialog.OnDateSetListener mDateSetListener2;
    private RadioGroup radio_g;
    private RadioButton radio_CO, radio_NO2, radio_SO2, radio_O3, radio_Tmp, radio_PM25;
    public static TextView deviceview;
    public static String mac = "";
    //private //배열을 만들어 key개수 만큼
    XAxis xAxis;
    YAxis yAxis;
    public String DateString_s;
    public String DateString_e;    // 데이터 전송,수신시 어떤타입을 송,수신 하는지 저장하는 변수
    private String time = "0";
    public static float a = 0;
    private String[] CO, NO2, SO2, O3, PM25, TMP;
    private double VCO = 0.0, CO_AQI, CO2, CO2_AQI, VSO2 = 0.0, SO2_AQI, VNO2 = 0.0, NO2_AQI,
            VO3 = 0.0, O3_AQI, VPM25 = 0.0, PM25_AQI, VPM10, PM10_AQI, AQI_avg, VTEMP = 0.0;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static ArrayList<Entry> COchart = new ArrayList<>();
    public static ArrayList<Entry> SO2chart = new ArrayList<>();
    public static ArrayList<Entry> NO2chart = new ArrayList<>();
    public static ArrayList<Entry> PM25chart = new ArrayList<>();
    public static ArrayList<Entry> TEMPchart = new ArrayList<>();
    public static ArrayList<Entry> O3chart = new ArrayList<>();
    Timestamp tendaysago = new Timestamp(new Date().getTime());
    Timestamp ninedaysago = new Timestamp(new Date().getTime());
    Timestamp eightdaysago = new Timestamp(new Date().getTime());
    Timestamp sevendaysago = new Timestamp(new Date().getTime());
    Timestamp sixdaysago = new Timestamp(new Date().getTime());
    Timestamp fivedaysago = new Timestamp(new Date().getTime());
    Timestamp fourdaysago = new Timestamp(new Date().getTime());
    Timestamp threedaysago = new Timestamp(new Date().getTime());
    Timestamp twodaysago = new Timestamp(new Date().getTime());
    Timestamp onedaysago = new Timestamp(new Date().getTime());
    Timestamp today = new Timestamp(new Date().getTime());
    Calendar cal9 = Calendar.getInstance();
    Calendar cal8 = Calendar.getInstance();
    Calendar cal7 = Calendar.getInstance();
    Calendar cal6 = Calendar.getInstance();
    Calendar cal5 = Calendar.getInstance();
    Calendar cal4 = Calendar.getInstance();
    Calendar cal3 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    Calendar cal1 = Calendar.getInstance();
    Calendar cal0 = Calendar.getInstance();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        cal9.add(Calendar.DAY_OF_MONTH, -9);
        ninedaysago = new Timestamp(cal9.getTime().getTime());
        cal8.add(Calendar.DAY_OF_MONTH, -8);
        eightdaysago = new Timestamp(cal8.getTime().getTime());
        cal7.add(Calendar.DAY_OF_MONTH, -7);
        sevendaysago = new Timestamp(cal7.getTime().getTime());
        cal6.add(Calendar.DAY_OF_MONTH, -6);
        sixdaysago = new Timestamp(cal6.getTime().getTime());
        cal5.add(Calendar.DAY_OF_MONTH, -5);
        fivedaysago = new Timestamp(cal5.getTime().getTime());

        cal4.add(Calendar.DAY_OF_MONTH, -4);
        fourdaysago = new Timestamp(cal4.getTime().getTime());

        cal3.add(Calendar.DAY_OF_MONTH, -3);
        threedaysago = new Timestamp(cal3.getTime().getTime());

        cal2.add(Calendar.DAY_OF_MONTH, -2);
        twodaysago = new Timestamp(cal2.getTime().getTime());
        cal1.add(Calendar.DAY_OF_MONTH, -1);

        twodaysago = new Timestamp(cal1.getTime().getTime());
        cal0.add(Calendar.DAY_OF_MONTH, 0);
        today = new Timestamp(cal0.getTime().getTime());


        DataTypeInfoFromServer = new ArrayList<>();
        DateInfoFromServer = new ArrayList<>();
        XAixsLabels = new ArrayList<>();
        DateTempArray = new String[DateInfoFromServer.size()];
        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radiogroup);
        radio_CO = (RadioButton) rootView.findViewById(R.id.radio_CO);
        radio_NO2 = (RadioButton) rootView.findViewById(R.id.radio_NO2);
        radio_SO2 = (RadioButton) rootView.findViewById(R.id.radio_SO2);
        radio_O3 = (RadioButton) rootView.findViewById(R.id.radio_O3);
        radio_Tmp = (RadioButton) rootView.findViewById(R.id.radio_Tmp);
        txtSearch = (TextView) rootView.findViewById(R.id.txtSearch);
        ImageView bluetooth = (ImageView)rootView.findViewById(R.id.imageBT);
        Random r = new Random();
        mChart = (LineChart) rootView.findViewById(R.id.graph);
        deviceview = (TextView) rootView.findViewById(R.id.txt_device);
        deviceview.setText("your device : " + DeviceListActivity.address);
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0f, 0));
        entries.add(new Entry(0f, 1));
        entries.add(new Entry(0f, 2));
        entries.add(new Entry(0f, 3));
        entries.add(new Entry(0f, 4));
        entries.add(new Entry(0f, 5));
        entries.add(new Entry(0f, 6));
        entries.add(new Entry(0f, 7));
        entries.add(new Entry(0f, 8));
        entries.add(new Entry(0f, 9));
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
        labels.add("1");
        labels.add("2");
        labels.add("3");
        labels.add("4");
        labels.add("5");
        labels.add("6");
        labels.add("7");
        labels.add("8");
        labels.add("9");
        labels.add("10");
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

                        sHandler.sendEmptyMessageDelayed(0,3000);
                        mChart = (LineChart) rootView.findViewById(R.id.graph);

                        ArrayList<Entry> entries = new ArrayList<>();
                        entries.add(new Entry(Float.parseFloat("346.53"), 0));
                        entries.add(new Entry(Float.parseFloat("325.23"), 1));
                        entries.add(new Entry(Float.parseFloat("335.16"), 2));
                        entries.add(new Entry(Float.parseFloat("362.12"), 3));
                        entries.add(new Entry(Float.parseFloat("358.12"), 4));
                        entries.add(new Entry(Float.parseFloat("342.11"), 5));
                        entries.add(new Entry(Float.parseFloat("381.34"), 6));
                        entries.add(new Entry(Float.parseFloat("452.12"), 7));
                        entries.add(new Entry(Float.parseFloat("293.12"), 8));
                        entries.add(new Entry(Float.parseFloat("312.32"), 9));
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

                        labels.add(ninedaysago.toString());
                        labels.add(eightdaysago.toString());
                        labels.add(sevendaysago.toString());
                        labels.add(sixdaysago.toString());
                        labels.add(fivedaysago.toString());
                        labels.add(fourdaysago.toString());
                        labels.add(threedaysago.toString());
                        labels.add(twodaysago.toString());
                        labels.add(onedaysago.toString());
                        labels.add(today.toString());

                        LineData lineData = new LineData(labels, lineDataSet);
                        mChart.setData(lineData);
                        mChart.setDescription("");
                        mChart.setDrawGridBackground(false);
                        mChart.setTouchEnabled(true);
                        mChart.setDragEnabled(true);
                        mChart.setScaleEnabled(true);
                        mChart.animateY(800, Easing.EasingOption.EaseInCubic);
                        mChart.invalidate();

                        break;
                    case R.id.radio_NO2:
                        ArrayList<Entry> entries_No2 = new ArrayList<>();

                        entries_No2.add(new Entry(Float.parseFloat("1089.12"), 0));
                        entries_No2.add(new Entry(Float.parseFloat("1101.42"), 1));
                        entries_No2.add(new Entry(Float.parseFloat("1092.12"), 2));
                        entries_No2.add(new Entry(Float.parseFloat("1100.12"), 3));
                        entries_No2.add(new Entry(Float.parseFloat("1252.12"), 4));
                        entries_No2.add(new Entry(Float.parseFloat("1111.32"), 5));
                        entries_No2.add(new Entry(Float.parseFloat("1002.31"), 6));
                        entries_No2.add(new Entry(Float.parseFloat("1091.12"), 7));
                        entries_No2.add(new Entry(Float.parseFloat("1100.21"), 8));
                        entries_No2.add(new Entry(Float.parseFloat("1201.45"), 9));
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

                        labels_No2.add(ninedaysago.toString());
                        labels_No2.add(eightdaysago.toString());
                        labels_No2.add(sevendaysago.toString());
                        labels_No2.add(sixdaysago.toString());
                        labels_No2.add(fivedaysago.toString());
                        labels_No2.add(fourdaysago.toString());
                        labels_No2.add(threedaysago.toString());
                        labels_No2.add(twodaysago.toString());
                        labels_No2.add(onedaysago.toString());
                        labels_No2.add(today.toString());

                        LineData lineData_No2 = new LineData(labels_No2, lineDataSet_No2);
                        mChart.setData(lineData_No2);
                        mChart.setDescription("");
                        mChart.setDrawGridBackground(false);
                        mChart.setTouchEnabled(true);
                        mChart.setDragEnabled(true);
                        mChart.setScaleEnabled(true);
                        mChart.animateY(800, Easing.EasingOption.EaseInCubic);
                        mChart.invalidate();


                        break;
                    case R.id.radio_O3:
                        mChart = (LineChart) rootView.findViewById(R.id.graph);

                        ArrayList<Entry> O3entries = new ArrayList<>();

                        O3entries.add(new Entry(Float.parseFloat("1689.52"), 0));
                        O3entries.add(new Entry(Float.parseFloat("1601.41"), 1));
                        O3entries.add(new Entry(Float.parseFloat("1652.15"), 2));
                        O3entries.add(new Entry(Float.parseFloat("1500.32"), 3));
                        O3entries.add(new Entry(Float.parseFloat("1612.22"), 4));
                        O3entries.add(new Entry(Float.parseFloat("1611.32"), 5));
                        O3entries.add(new Entry(Float.parseFloat("1603.11"), 6));
                        O3entries.add(new Entry(Float.parseFloat("1491.12"), 7));
                        O3entries.add(new Entry(Float.parseFloat("1604.51"), 8));
                        O3entries.add(new Entry(Float.parseFloat("1621.45"), 9));
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

                        O3labels.add(ninedaysago.toString());
                        O3labels.add(eightdaysago.toString());
                        O3labels.add(sevendaysago.toString());
                        O3labels.add(sixdaysago.toString());
                        O3labels.add(fivedaysago.toString());
                        O3labels.add(fourdaysago.toString());
                        O3labels.add(threedaysago.toString());
                        O3labels.add(twodaysago.toString());
                        O3labels.add(onedaysago.toString());
                        O3labels.add(today.toString());

                        LineData O3data = new LineData(O3labels, O3dataset);
                        mChart.setData(O3data);
                        mChart.setDescription("");
                        mChart.setDrawGridBackground(false);
                        mChart.setTouchEnabled(true);
                        mChart.setDragEnabled(true);
                        mChart.setScaleEnabled(true);
                        mChart.animateY(800, Easing.EasingOption.EaseInCubic);
                        mChart.invalidate();

                        break;
                    case R.id.radio_PM25:
                        mChart = (LineChart) rootView.findViewById(R.id.graph);

                        ArrayList<Entry> PM25entries = new ArrayList<>();
                        PM25entries.add(new Entry(Float.parseFloat("3.72"), 0));
                        PM25entries.add(new Entry(Float.parseFloat("3.11"), 1));
                        PM25entries.add(new Entry(Float.parseFloat("3.25"), 2));
                        PM25entries.add(new Entry(Float.parseFloat("3.42"), 3));
                        PM25entries.add(new Entry(Float.parseFloat("3.52"), 4));
                        PM25entries.add(new Entry(Float.parseFloat("3.22"), 5));
                        PM25entries.add(new Entry(Float.parseFloat("3.51"), 6));
                        PM25entries.add(new Entry(Float.parseFloat("3.12"), 7));
                        PM25entries.add(new Entry(Float.parseFloat("3.51"), 8));
                        PM25entries.add(new Entry(Float.parseFloat("3.44"), 9));
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

                        PMlabel.add(ninedaysago.toString());
                        PMlabel.add(eightdaysago.toString());
                        PMlabel.add(sevendaysago.toString());
                        PMlabel.add(sixdaysago.toString());
                        PMlabel.add(fivedaysago.toString());
                        PMlabel.add(fourdaysago.toString());
                        PMlabel.add(threedaysago.toString());
                        PMlabel.add(twodaysago.toString());
                        PMlabel.add(onedaysago.toString());
                        PMlabel.add(today.toString());

                        LineData PMdata = new LineData(PMlabel, PMdataset);
                        mChart.setData(PMdata);
                        mChart.setDescription("");
                        mChart.setDrawGridBackground(false);
                        mChart.setTouchEnabled(true);
                        mChart.setDragEnabled(true);
                        mChart.setScaleEnabled(true);
                        mChart.animateY(800, Easing.EasingOption.EaseInCubic);
                        mChart.invalidate();

                        break;
                    case R.id.radio_SO2:
                        mChart = (LineChart) rootView.findViewById(R.id.graph);

                        ArrayList<Entry> SO2entries = new ArrayList<>();

                        SO2entries.add(new Entry(Float.parseFloat("543.72"), 0));
                        SO2entries.add(new Entry(Float.parseFloat("523.11"), 1));
                        SO2entries.add(new Entry(Float.parseFloat("553.25"), 2));
                        SO2entries.add(new Entry(Float.parseFloat("513.42"), 3));
                        SO2entries.add(new Entry(Float.parseFloat("562.52"), 4));
                        SO2entries.add(new Entry(Float.parseFloat("633.22"), 5));
                        SO2entries.add(new Entry(Float.parseFloat("621.51"), 6));
                        SO2entries.add(new Entry(Float.parseFloat("613.12"), 7));
                        SO2entries.add(new Entry(Float.parseFloat("643.51"), 8));
                        SO2entries.add(new Entry(Float.parseFloat("594.44"), 9));
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

                        SO2labels.add(ninedaysago.toString());
                        SO2labels.add(eightdaysago.toString());
                        SO2labels.add(sevendaysago.toString());
                        SO2labels.add(sixdaysago.toString());
                        SO2labels.add(fivedaysago.toString());
                        SO2labels.add(fourdaysago.toString());
                        SO2labels.add(threedaysago.toString());
                        SO2labels.add(twodaysago.toString());
                        SO2labels.add(onedaysago.toString());
                        SO2labels.add(today.toString());

                        LineData SO2setdata = new LineData(SO2labels, SO2dataset);
                        mChart.setData(SO2setdata);
                        mChart.setDescription("");
                        mChart.setDrawGridBackground(false);
                        mChart.setTouchEnabled(true);
                        mChart.setDragEnabled(true);
                        mChart.setScaleEnabled(true);
                        mChart.animateY(800, Easing.EasingOption.EaseInCubic);
                        mChart.invalidate();

                        break;
                    case R.id.radio_Tmp:
                        mChart = (LineChart) rootView.findViewById(R.id.graph);

                        ArrayList<Entry> TMPentries = new ArrayList<>();
                        TMPentries.add(new Entry(Float.parseFloat("23.72"), 0));
                        TMPentries.add(new Entry(Float.parseFloat("23.11"), 1));
                        TMPentries.add(new Entry(Float.parseFloat("20.25"), 2));
                        TMPentries.add(new Entry(Float.parseFloat("21.42"), 3));
                        TMPentries.add(new Entry(Float.parseFloat("22.52"), 4));
                        TMPentries.add(new Entry(Float.parseFloat("20.22"), 5));
                        TMPentries.add(new Entry(Float.parseFloat("21.51"), 6));
                        TMPentries.add(new Entry(Float.parseFloat("23.12"), 7));
                        TMPentries.add(new Entry(Float.parseFloat("24.51"), 8));
                        TMPentries.add(new Entry(Float.parseFloat("22.44"), 9));
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

                        TMPlabels.add(ninedaysago.toString());
                        TMPlabels.add(eightdaysago.toString());
                        TMPlabels.add(sevendaysago.toString());
                        TMPlabels.add(sixdaysago.toString());
                        TMPlabels.add(fivedaysago.toString());
                        TMPlabels.add(fourdaysago.toString());
                        TMPlabels.add(threedaysago.toString());
                        TMPlabels.add(twodaysago.toString());
                        TMPlabels.add(onedaysago.toString());
                        TMPlabels.add(today.toString());

                        LineData TMPline = new LineData(TMPlabels, TMPdataset);
                        mChart.setData(TMPline);
                        mChart.setDescription("");
                        mChart.setDrawGridBackground(false);
                        mChart.setTouchEnabled(true);
                        mChart.setDragEnabled(true);
                        mChart.setScaleEnabled(true);
                        mChart.animateY(800, Easing.EasingOption.EaseInCubic);
                        mChart.invalidate();

                        break;
                    default:
                        // do operations specific to this selection
                        break;
                }
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
            int usn;
            try {
                URL url = new URL("http://teama-iot.calit2.net/app/airQualityHistory");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("MAC", DeviceListActivity.address);
                    json.put("period", 4);
                    json.put("startDate", ninedaysago);
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
                            if (!String.valueOf(airqualitys.getDouble("CO")).equals(null)||!String.valueOf(airqualitys.getDouble("CO")).equals("")) {
                                try {
                                    VCO = airqualitys.getDouble("CO");
                                    VSO2 = airqualitys.getDouble("SO2");
                                    VNO2 = airqualitys.getDouble("NO2");
                                    VO3 = airqualitys.getDouble("O3");
                                    VPM25 = airqualitys.getDouble("PM25");
                                    VTEMP = airqualitys.getDouble("TEMP");
                                    Log.d("CO", String.valueOf(VCO));
                                    Log.d("SO2", String.valueOf(VSO2));
                                    Log.d("NO2", String.valueOf(VNO2));
                                    Log.d("O3", String.valueOf(VO3));
                                    Log.d("PM25", String.valueOf(VPM25));
                                    Log.d("TEMP", String.valueOf(VTEMP));
                                    Log.d("time", time);

                                    CO[i] = String.valueOf(VCO);
                                    SO2[i] = String.valueOf(VSO2);
                                    NO2[i] = String.valueOf(VNO2);
                                    O3[i] = String.valueOf(VO3);
                                    PM25[i] = String.valueOf(VPM25);
                                    TMP[i] = String.valueOf(VTEMP);
                                    Log.d("testSADAS", CO[i].toString() + NO2[i].toString() + SO2[i].toString() + O3[i].toString() + PM25[i].toString() + TMP[i].toString());
                                    result = 1;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
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