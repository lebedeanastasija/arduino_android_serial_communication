package com.example.anastasiya.arduinoserialcom;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.anastasiya.arduinoserialcom.services.PupilService;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.HashMap;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigation;

    TextView tv;
    private static String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    protected static final String TAG = null;

    UsbManager usbManager;
    UsbDevice device;
    UsbDeviceConnection connection;
    UsbSerialDevice serialPort;
    Handler handler;
    String data = null;

    public static final String REQUEST_TAG = "MainActivity";
    private RequestQueue mQueue;
    public CustomJSONObjectRequest jsonRequest;

    private PupilService pupilService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    case R.id.nav_home:
                        break;
                    case R.id.nav_pupil:
                        startActivity(new Intent(MainActivity.this, PupilActivity.class));
                        break;
                    case R.id.nav_class:
                        break;
                }
                return false;
            }
        });


        tv = (TextView) findViewById(R.id.textView);
        tv.setText("");
        pupilService = PupilService.getInstance(this);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        handler = new Handler();
        connectUsbDevice();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void requestBtnOnClick(View view) {
        HttpRequestTask asyncTask = new HttpRequestTask(new IAsyncResponse() {
            @Override
            public void processFinish(Object output){
                try {
                    tv.setText(tv.getText() + "\n\n" + ((JSONObject)output).getString("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this.getApplicationContext());
        asyncTask.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mQueue != null) {
            mQueue.cancelAll(REQUEST_TAG);
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
            tv.setText("\nNo devices found!");
        }
    }

    public void disconnectUsbDevice(){
        serialPort.close();
    }

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0) {
        try {
            data = new String(arg0, "UTF-8");
            data.concat("/n");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv.append(data);
                }
            }, 500);
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
                        tv.setText("");
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
