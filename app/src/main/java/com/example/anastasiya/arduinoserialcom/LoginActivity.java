package com.example.anastasiya.arduinoserialcom;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.example.anastasiya.arduinoserialcom.routers.TeacherHttpRequestTask;
import com.example.anastasiya.arduinoserialcom.services.PupilService;
import com.example.anastasiya.arduinoserialcom.services.TeacherService;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {
    TextView loginInfo;
    TextView readerInfo;

    private static String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbDeviceConnection connection;
    UsbSerialDevice serialPort;
    Handler handler;
    String data = null;

    Context context;

    public static final String REQUEST_TAG = "LoginActivity";

    private TeacherService teacherService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this.getApplicationContext();
        loginInfo = (TextView) findViewById(R.id.tvLoginInfo);
        loginInfo.setText(R.string.attach_teacher_card);
        readerInfo = (TextView) findViewById(R.id.tvReaderInfo);
        teacherService = TeacherService.getInstance(this);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        handler = new Handler();
        connectUsbDevice();
    }

    public void writeToLogFile(String text) {
        if(text != null) {
            File externalStorageDir = Environment.getExternalStorageDirectory();
            File myFile = new File(externalStorageDir, "yourfilename.txt");

            if (myFile.exists()) {
                try {
                    FileOutputStream fostream = new FileOutputStream(myFile);
                    OutputStreamWriter oswriter = new OutputStreamWriter(fostream);
                    BufferedWriter bwriter = new BufferedWriter(oswriter);
                    bwriter.write(text);
                    bwriter.newLine();
                    bwriter.close();
                    oswriter.close();
                    fostream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    myFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void connectUsbDevice() {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        IntentFilter deviceAttachedFilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        IntentFilter deviceUsbPermissions = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(broadcastReceiver, deviceAttachedFilter);
        registerReceiver(broadcastReceiver, deviceUsbPermissions);
        while(deviceIterator.hasNext()){
            device = deviceIterator.next();
            int pid = device.getProductId();
            if(pid == 29987) {
                PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                usbManager.requestPermission(device, pi);
            }
            else {
                connection = null;
                device = null;
            }
        }
        if(device == null) {
            readerInfo.setText(R.string.no_rfid_readers);
        }
    }

    public void disconnectUsbDevice(){
        serialPort.close();
        ((Activity) this).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                readerInfo.setText(R.string.no_rfid_readers);
            }
        });
    }

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0) {
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
                writeToLogFile(data);
                TeacherHttpRequestTask asyncTask = new TeacherHttpRequestTask(new IAsyncResponse() {
                    @Override
                    public void processFinish(Object output){

                        writeToLogFile("Server response");

                        try {
                             if(((JSONObject)output).getJSONObject("data") != null) {
                                 Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                 intent.putExtra("teacher_uid", data);
                                 startActivity(intent);
                                 LoginActivity.this.finish();
                                 overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
                                 System.exit(0);
                             } else {
                                 writeToLogFile(R.string.unknown_teacher_card + "\n" + R.string.attach_teacher_card);
                             }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, context);
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"getTeacherByUID", data);
                //executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) {
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            readerInfo.setText(R.string.rfid_reader_found);
                            serialPort.read(mCallback);
                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                connectUsbDevice();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                disconnectUsbDevice();
            }
        }
    };
}
