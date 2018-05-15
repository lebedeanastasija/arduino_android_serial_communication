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
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.anastasiya.arduinoserialcom.helpers.AlertManager;
import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.example.anastasiya.arduinoserialcom.routers.ScheduleHttpRequestTask;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.HashMap;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import android.os.Handler;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private Activity activity;

    private FileLogger fileLogger;
    private AlertManager alertManager;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigation;
    private static String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    TextView tv;
    TextView readerInfo;

    UsbManager usbManager;
    UsbDevice device;
    UsbDeviceConnection connection;
    UsbSerialDevice serialPort;
    Handler handler;
    String data = null;
    String teacher_uid = null;
    String teacherId = null;
    String lessonInfo;
    Boolean activityIsActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        context = activity.getApplicationContext();
        fileLogger = FileLogger.getInstance(this.getApplicationContext(), this);
        alertManager = AlertManager.getInstance(activity);

        final Intent intent = getIntent();
        teacher_uid = intent.getStringExtra("teacher_uid");
        teacherId = intent.getStringExtra("teacherId");

        fileLogger.writeToLogFile("main activity for teacher with uid: " + teacher_uid);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNavigation = (NavigationView) findViewById(R.id.navigation_view);
        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_profile:
                        Intent profile_intent = new Intent(MainActivity.this, TeacherProfileActivity.class);
                        profile_intent.putExtra("teacher_uid", teacher_uid);
                        startActivity(profile_intent);
                        break;
                    case R.id.nav_class:
                        Intent class_intent = new Intent(MainActivity.this, ClassActivity.class);
                        class_intent.putExtra("teacher_uid", teacher_uid);
                        startActivity(class_intent);
                        break;
                }
                return false;
            }
        });

        tv = (TextView) findViewById(R.id.textView);
        readerInfo = (TextView) findViewById(R.id.readerInfo);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        handler = new Handler();

        ScheduleHttpRequestTask asyncTask = new ScheduleHttpRequestTask(new IAsyncResponse() {
            @Override
            public void processFinish(Object output) {
                JSONObject response = null;

                if(((JSONObject) output).isNull("data")) {
                    alertManager.show("Уроков нет", "На данный момент в вашем расписании отсутствуют уроки.");
                } else {
                    try {
                        response = ((JSONObject) output).getJSONObject("data");
                        lessonInfo = response.getString("subjectName")
                                + ", " + response.getString("className")
                                + ", " + response.getString("roomName")
                                + ", " + response.getString("weekDay")
                                + " " + response.getString("time");
                        fileLogger.writeToLogFile(lessonInfo);
                        alertManager.show("Идет урок.", lessonInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }, context, activity);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "getCurrentByTeacher", teacherId);
        connectUsbDevice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityIsActive = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityIsActive = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                tv.setText("");
                readerInfo.setText(R.string.no_rfid_readers);
            }
        });
    }

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0) {
            if(activityIsActive) {
                try {
                    data = new String(arg0, "UTF-8");
                    data.concat("/n");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv.append(data);
                            Intent intent = new Intent(MainActivity.this, PupilActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("uid", data);
                            startActivity(intent);
                        }
                    }, 500);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
                            tv.setText(R.string.received_uids);
                            tv.append("\n");
                            readerInfo.setText(R.string.attach_pupil_card);
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
