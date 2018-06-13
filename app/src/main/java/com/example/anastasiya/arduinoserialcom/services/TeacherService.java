package com.example.anastasiya.arduinoserialcom.services;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.anastasiya.arduinoserialcom.routers.CustomJSONObjectRequest;
import com.example.anastasiya.arduinoserialcom.routers.CustomVolleyRequestQueue;
import com.example.anastasiya.arduinoserialcom.R;
import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;

import org.json.JSONObject;

public class TeacherService implements Response.Listener<JSONObject>, Response.ErrorListener{
    private static TeacherService mInstance;
    private RequestQueue mQueue;
    private static Object responseObject;
    private static final Object syncObject = new Object();
    private static final String REQUEST_TAG = "TeacherService";
    private Resources res;
    private FileLogger fileLogger;

    private TeacherService(Context context, Activity activity) {
        mQueue = CustomVolleyRequestQueue.getInstance(context).getRequestQueue();
        res = context.getResources();
        fileLogger = FileLogger.getInstance(context, activity);
    }

    public Object getLessonByUid(String uid) throws InterruptedException {
        responseObject = null;
        String url = res.getString(R.string.server_address) + "/schedules/teacher/" + uid;
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

        String url = res.getString(R.string.server_address) + "/teachers/uid/" + uid;
        fileLogger.writeToLogFile("GET: " + url);
        CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.GET, url, new JSONObject(), this, this);
        jsonRequest.setTag("GetTeacher");
        mQueue.add(jsonRequest);
        synchronized (syncObject) {
            try {
                fileLogger.writeToLogFile("Waiting for response...");
                syncObject.wait();
                return responseObject;
            } catch (InterruptedException e) {
                fileLogger.writeToLogFile("Error:" + e.getMessage());
                return new VolleyError("Error occurred");
            } catch (Exception e) {
                fileLogger.writeToLogFile("Error:" + e.getMessage());
                return new VolleyError("Error occurred");
            }
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        fileLogger.writeToLogFile("On response!");
        responseObject = response;
        synchronized (syncObject) {
            syncObject.notify();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        responseObject = error;
    }

    public static synchronized TeacherService getInstance(Context context, Activity activity) {
        if (mInstance == null) {
            mInstance = new TeacherService(context, activity);
        }
        return mInstance;
    }
}
