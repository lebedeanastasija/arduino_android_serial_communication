package com.example.anastasiya.arduinoserialcom.services;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.anastasiya.arduinoserialcom.CustomJSONObjectRequest;
import com.example.anastasiya.arduinoserialcom.CustomVolleyRequestQueue;
import com.example.anastasiya.arduinoserialcom.R;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class PupilService implements Response.Listener<JSONObject>, Response.ErrorListener {
    private static PupilService mInstance;
    private RequestQueue mQueue;
    private static Object responseObject;
    public static final String REQUEST_TAG = "PupilService";
    private static final Object syncObject = new Object();
    private Resources res;

    private PupilService(Context context) {
        mQueue = CustomVolleyRequestQueue.getInstance(context).getRequestQueue();
        res = context.getResources();
    }

    public Object getPupils() throws InterruptedException {
        responseObject = null;
        String url = res.getString(R.string.server_address) + "/pupils";
        CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.GET, url, new JSONObject(), this, this);
        jsonRequest.setTag(REQUEST_TAG);
        mQueue.add(jsonRequest);
        synchronized (syncObject) {
            try {
                syncObject.wait();
                return responseObject;
            } catch (InterruptedException e) {
                return new VolleyError("Error occurred");
            }
        }
    }

    public Object getPupilByUid(String uid) throws InterruptedException {
        responseObject = null;
        String url = res.getString(R.string.server_address) + "/pupils/uid/" + uid;
        writeToLogFile("pupils uid: " + uid);
        CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.GET, url, new JSONObject(), this, this);
        jsonRequest.setTag(REQUEST_TAG);

        mQueue.add(jsonRequest);
        synchronized (syncObject) {
            try {
                syncObject.wait();
                return responseObject;
            } catch (InterruptedException e) {
                return new VolleyError("Error occurred");
            }
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        responseObject = response;
        writeToLogFile(response.toString());
        synchronized (syncObject) {
            syncObject.notify();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        responseObject = error;
    }

    public static synchronized PupilService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PupilService(context);
        }
        return mInstance;
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
}
