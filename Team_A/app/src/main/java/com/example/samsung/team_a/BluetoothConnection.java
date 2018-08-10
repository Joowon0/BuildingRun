package com.example.samsung.team_a;


/**
 * Created by BOM2 on 2016-02-16.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by matteo on 10/1/15.
 * QueueJob Manager
 * <p>
 * QUEUE MANAGER
 */

public class BluetoothConnection extends Thread {
    private BluetoothSocket mmSocket = null;
    public static BluetoothDevice mmDevice = null;
    private static final UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter adapter = null;
    private Handler mConnectionData = null;
    private BluetoothSocket sockFallback = null;
    private BluetoothChatService bluetoothChatService = null;
    public static boolean connectFlag = false;
    public static int checkconnect = 0;
    public static String MAC = "", TIME = "";
    public static double NO2 = 0.0, O3 = 0.0, CO = 0.0, PM25 = 0.0, TEMP = 0.0, SO2=0.0;
    public static int state = 0;

    public BluetoothConnection(BluetoothDevice device, BluetoothAdapter adapter_tmp,
                               Handler connectionData) {
        mmDevice = device;
        adapter = adapter_tmp;
        //NECESSARY FOR THE UIss
        mConnectionData = connectionData;
    }


    public void run() {
        // Cancel discovery because it will slow down the connection
        adapter.cancelDiscovery();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            Log.d("BluetoothTest", "CONNECTION " + mmDevice.getAddress() + " " + mmDevice.getName());
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(BT_UUID);
            //Log.d("socket.inputstream.read", String.valueOf(mmSocket.getInputStream().read()));
            mmSocket.connect();


        } catch (Exception connectException) {
            Log.e("BluetoothTest", "There was an error while establishing Bluetooth connection. Falling back..", connectException);
            Class<?> clazz = mmSocket.getRemoteDevice().getClass();
            Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
            try {
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                sockFallback = (BluetoothSocket) m.invoke(mmSocket.getRemoteDevice(), params);
                sockFallback.connect();
                mmSocket = sockFallback;
            } catch (Exception e2) {
                Log.e("BluetoothTest", "Couldn't fallback while establishing Bluetooth connection. Stopping app..", e2);
//                stopService();
                try {
                    Log.d("BluetoothTest", "CLOSE " + mmDevice.getAddress() + " " + mmDevice.getName());
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.d("BluetoothTest", "CONNECTION CLOSE ERROR " + mmDevice.getAddress() + " " + mmDevice.getName());
                }
            }
        }
        // Do work to manage the connection (in a separate thread)
        InputStream bluetooth_inputStream;
        if (mmSocket != null) {
            Log.d("BluetoothTest", "CONNECTED WITH " + mmDevice.getAddress() + " " + mmDevice.getName());
            connectFlag = true;
            try {
                Log.d("state", String.valueOf(state));
                if (state == 0) {
                    Log.d("state", "0");
                } else if (state == Constants.BT_REGISTER) {
                    bluetooth_inputStream = mmSocket.getInputStream();
                    Log.d("register ", "들어옴");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("REQ", "M");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String body = jsonObject.toString();
                    Log.d("body", body);
                    OutputStream os = mmSocket.getOutputStream();
                    Log.d("OS", os.toString());
                    os.write(body.getBytes());
                    os.flush();
                    Log.d("JSON_body : ", body);
                    byte[] buffer = new byte[10240];  // buffer store for the stream
                    int bytes; // bytes returned from read()

                    // Keep listening to the InputStream until an exception occurs
                    while (true) {
                        Log.d("BluetoothTest", "waiting for data");
                        try {
                            // Read from the InputStream
                            bytes = bluetooth_inputStream.read(buffer);        // Get number of bytes and message in "buffer"
                            byte[] readBuf = (byte[]) buffer;
                            String strIncom = new String(readBuf, 0, bytes);

                            Log.d("BluetoothTest", "Data " + strIncom);
                            Message msg1 = new Message();
                            Bundle bundle_sensor_data = new Bundle();
                            bundle_sensor_data.putString("register", strIncom);
                            msg1.setData(bundle_sensor_data);
                            mConnectionData.sendMessage(msg1);

                        } catch (IOException e) {
                            break;
                        }
                    }
                } else if (state == Constants.BT_USING) {
                    bluetooth_inputStream = mmSocket.getInputStream();

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("REQ", "D");
                        jsonObject.put("startTime", currentDateandTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("air quality map", "들어옴");
                    String body = jsonObject.toString();
                    Log.d("body", body);
                    OutputStream os = mmSocket.getOutputStream();
                    Log.d("OS", os.toString());
                    os.write(body.getBytes());
                    os.flush();
                    Log.d("JSON_body : ", body);
                    byte[] buffer = new byte[10240];  // buffer store for the stream
                    int bytes; // bytes returned from read()

                    // Keep listening to the InputStream until an exception occurs
                    while (true) {
                        Log.d("BluetoothTest", "waiting for data");
                        try {
                            // Read from the InputStream
                            bytes = bluetooth_inputStream.read(buffer);        // Get number of bytes and message in "buffer"
                            byte[] readBuf = (byte[]) buffer;
                            String strIncom = new String(readBuf, 0, bytes);

                            Log.d("BluetoothTest", "Data " + strIncom);
                            Message msg1 = new Message();
                            Bundle bundle_sensor_data = new Bundle();
                            bundle_sensor_data.putString("realtime", strIncom);
                            msg1.setData(bundle_sensor_data);
                            mConnectionData.sendMessage(msg1);


                        } catch (IOException e) {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("BluetoothTest", "error");
            }
        } else {
            StopConnection();
            connectFlag = false;
        }
    }

    public void StopConnection() {
        try {
            Log.d("BluetoothTest", "CLOSE " + mmDevice.getAddress() + " " + mmDevice.getName());
            OutputStream cmd = mmSocket.getOutputStream();
            String ss = "CLOSE";
            cmd.write(ss.getBytes());
            mmSocket.close();
            state = 0;
        } catch (IOException closeException) {
            Log.d("BluetoothTest", "CONNECTION CLOSE ERROR " + mmDevice.getAddress() + " " + mmDevice.getName());
        }
    }

}