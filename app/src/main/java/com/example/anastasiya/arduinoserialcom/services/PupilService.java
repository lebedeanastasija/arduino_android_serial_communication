package com.example.anastasiya.arduinoserialcom.services;

import android.content.Context;
import android.os.AsyncTask;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.anastasiya.arduinoserialcom.CustomJSONObjectRequest;
import com.example.anastasiya.arduinoserialcom.CustomVolleyRequestQueue;
import org.json.JSONObject;

public class PupilService implements Response.Listener<JSONObject>, Response.ErrorListener {
    private static PupilService mInstance;
    private RequestQueue mQueue;
    //private Context mCtx;
    private static Object responseObject;
    public static final String REQUEST_TAG = "PupilService";
    private static final Object syncObject = new Object();

    private PupilService(Context context) {
        mQueue = CustomVolleyRequestQueue.getInstance(context).getRequestQueue();
        //mCtx = context;
    }

    public Object getPupils() throws InterruptedException {
        responseObject = null;
        String url = "http://192.168.31.221:3000/pupils";
        CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.GET, url, new JSONObject(), this, this);
        jsonRequest.setTag(REQUEST_TAG);
        mQueue.add(jsonRequest);
        synchronized (syncObject) {
            try {
                syncObject.wait();
                return responseObject;
            } catch (InterruptedException e) {
                return new VolleyError("Error occured");
            }
        }
    }

    public Object getPupilByUid(String uid) throws InterruptedException {
        responseObject = null;
        String url = "http://192.168.31.221:3000/pupils/uid/" + uid;
        CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.GET, url, new JSONObject(), this, this);
        jsonRequest.setTag(REQUEST_TAG);
        mQueue.add(jsonRequest);
        synchronized (syncObject) {
            try {
                syncObject.wait();
                return responseObject;
            } catch (InterruptedException e) {
                return new VolleyError("Error occured");
            }
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        responseObject = response;
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
}
