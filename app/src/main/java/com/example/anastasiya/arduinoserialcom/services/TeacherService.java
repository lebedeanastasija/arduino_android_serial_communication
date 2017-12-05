package com.example.anastasiya.arduinoserialcom.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.anastasiya.arduinoserialcom.CustomJSONObjectRequest;
import com.example.anastasiya.arduinoserialcom.CustomVolleyRequestQueue;
import com.example.anastasiya.arduinoserialcom.R;

import org.json.JSONObject;

public class TeacherService implements Response.Listener<JSONObject>, Response.ErrorListener{
    private static TeacherService mInstance;
    private RequestQueue mQueue;

    private static Object responseObject;
    public static final String REQUEST_TAG = "TeacherService";
    private static final Object syncObject = new Object();

    private TeacherService(Context context) {
        mQueue = CustomVolleyRequestQueue.getInstance(context).getRequestQueue();
    }

    public Object getScheduleByUid(String uid) throws InterruptedException {
        responseObject = null;
        String url = "http://192.168.1.5:3000/schedules/teacher/" + uid;
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

    public Object getTeacherByUid(String uid) throws InterruptedException {
        responseObject = null;

        String url = "http://192.168.1.5:3000/teachers/" + uid;
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
        synchronized (syncObject) {
            syncObject.notify();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        responseObject = error;
    }

    public static synchronized TeacherService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TeacherService(context);
        }
        return mInstance;
    }
}
