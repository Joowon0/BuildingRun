package com.example.samsung.team_a;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    public static Context context;
    TextView txt_AQinfo;
    private HttpURLConnection conn;
    public static int sensorview = 0;
    public static int viewresult = 0;
    public static int sensorcount = 0;
    public static boolean Register_BT = false;
    public static String VMAC = "", Vname = "";
    public static double Vlatitude = 0.0, Vlongitude = 0.0;
    private BluetoothAdapter bt_adapter = null;
    private BluetoothConnection bt_connection;
    private BluetoothDevice bt_device;
    private String Vregister ="";
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;
     //여기서 선택된 MAC주소를 public string으로 박고 tab1AirQuality에 보내야해
    public static ArrayList<String> sensorAList = new ArrayList<>();
    Intent main_to_devicelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,sensorAList);
        BluetoothConnection.state =1;
        ListView sensorLV = (ListView)findViewById(R.id.sensorList);
        Button btn_AQRG = (Button) findViewById(R.id.btn_AQRG);
        Button btn_AQDG = (Button) findViewById(R.id.btn_AQDG);
        sensorLV.setAdapter(mAdapter);
        SensorView sensorView = new SensorView(this);
        //sensorView.execute();
        aHandler.sendEmptyMessageDelayed(0, 2000);
        main_to_devicelist = new Intent(RegisterActivity.this, DeviceListActivity.class);

        // register 버튼 클릭시 bluetooth 설정을 하고, / 센서에게 REQ : M을 보내고 MAC을 받는다 / 서버에게 USN,MAC,latitude,longitude 를 보낸다.
        // deregister 버튼 클릭시 서버에게 MAC을 보내고 result(int)를 받는다.
        btn_AQRG.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Register_BT = true;
            BluetoothConnection.state =1;
            startActivityForResult(main_to_devicelist, REQUEST_CONNECT_DEVICE_SECURE);
            BluetoothConnection.state= 0;
            Register_BT =false;
        }
    });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
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

        Toast.makeText(context,"CONNECTED WITH " + bt_device.getAddress() + " " + bt_device.getName(),Toast.LENGTH_SHORT).show();
        BluetoothConnection.checkconnect = 1;

    }
    private Handler bt_receivemsg = new Handler(){ // ★ 메세지
        @Override
        public void handleMessage(Message message){
            Bundle data = message.getData();
            Vregister = data.getString("register");
            try {
                JSONObject jsonRegister = new JSONObject(Vregister);
                VMAC = jsonRegister.getString("MAC");
                sensorAList.add(VMAC);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    Handler aHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    class SensorView extends AsyncTask<String, Integer, Integer> {
        Context context;

        SensorView(Context etx) {
            context = etx;
        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            if (sensorview() > 1) {
                publishProgress(1);
            } else publishProgress(2);
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            AlertDialog alertdialog = new AlertDialog.Builder(context).create();
            if (value[0] == 1) {
                //listview에 값 추가
                txt_AQinfo.setText("User : " + loginActivity.ST_email + "\nMAC : " + VMAC);//MAC address 받기 즉 이 activity안에도 mason코드를 써야해
            } else {
                alertdialog.setTitle("Register");
                alertdialog.setMessage("You don't have any sensor. Register First.");
                alertdialog.show();
            }
        }

        public int sensorview() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;


            try {
                URL url = new URL("http://teama-iot.calit2.net/app/sensorListView");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("USN", loginActivity.ST_usn);

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
                        try {
                            JSONArray jsonview = new JSONArray(response);
                            for (int i = 0; i < jsonview.length(); i++) {
                                JSONObject jsonObject = jsonview.getJSONObject(i);
                                VMAC = (String) jsonObject.getString("MAC");
                                Vlatitude = (Double) jsonObject.getDouble("latitude");
                                Vlongitude = (Double) jsonObject.getDouble("longitude");
                                sensorcount++;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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
            return sensorcount;
        }

    }

    class Register extends AsyncTask<String, Integer, Integer> {
        Context context;

        Register(Context etx) {
            context = etx;
        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            boolean result = register();
            if (result) {
                publishProgress(1);
            } else publishProgress(2);
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            AlertDialog alertdialog = new AlertDialog.Builder(context).create();
            if (value[0] == 1) {
                alertdialog.setTitle("Register");
                alertdialog.setMessage("Success");
                alertdialog.show();
                sensorAList.add("User : " + loginActivity.ST_email + "\nMAC : " + VMAC);

                //MAC address 받기 즉 이 activity안에도 mason코드를 써야해
            } else {
                alertdialog.setTitle("Register");
                alertdialog.setMessage("Success");
                alertdialog.show();
            }
        }

        public boolean register() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            boolean result = false;
            try {
                URL url = new URL("http://teama-iot.calit2.net/app/sensorRegister");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("USN", loginActivity.ST_usn);
                    json.put("MAC", DeviceListActivity.address);//mac은 bluetooth 클릭시 연결되었을 때 mdevice.get뭐시기
                    json.put("latitude", MapsActivity.mLastKnownLocation.getLatitude());
                    json.put("longitude", MapsActivity.mLastKnownLocation.getLongitude());

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
                        result = (Boolean) responseJSON.getBoolean("ACK");

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

    class Deregister extends AsyncTask<String, Integer, Integer> {
        Context context;

        Deregister(Context etx) {
            context = etx;
        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            int result = deregister();
            switch (result) {
                case Constants.SI_SUCCESS:
                    publishProgress(1);
                    break;

                case Constants.SI_NO_SUCH_EMAIL:
                    publishProgress(2);
                    break;
                default:
//                    Toast.makeText(getApplicationContext(), "System Error/Connection Fail", Toast.LENGTH_SHORT).show();
                    break;
            }
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            AlertDialog alertdialog = new AlertDialog.Builder(context).create();
            if (value[0] == 1) {
                alertdialog.setTitle("Deregister");
                alertdialog.setMessage("Success");
                alertdialog.show();
                //listview 다시 reset
                txt_AQinfo.setText("User :" + loginActivity.ST_email + "\nMAC : ");

            } else if (value[0] == 2) {
                alertdialog.setTitle("Deregister");
                alertdialog.setMessage("It's not your device");
                alertdialog.show();
            }
        }

        public int deregister() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            int result = 0;
            int usn;
            try {
                URL url = new URL("http://teama-iot.calit2.net/app/sensorDeregister");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("MAC", BluetoothConnection.mmDevice.getAddress());//MAC주소 입력

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
