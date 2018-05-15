package com.example.anastasiya.arduinoserialcom.services;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.anastasiya.arduinoserialcom.R;
import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.routers.CustomJSONObjectRequest;
import com.example.anastasiya.arduinoserialcom.routers.CustomVolleyRequestQueue;

import org.json.JSONObject;

public class ScheduleService implements Response.Listener<JSONObject>, Response.ErrorListener {
    private static ScheduleService mInstance;
    private RequestQueue mQueue;
    private static Object responseObject;
    private static final Object syncObject = new Object();
    private static final String REQUEST_TAG = "ScheduleService";
    private Resources res;
    private FileLogger fileLogger;

    private ScheduleService(Context context, Activity activity) {
        mQueue = CustomVolleyRequestQueue.getInstance(context).getRequestQueue();
        res = context.getResources();
        fileLogger = FileLogger.getInstance(context, activity);
    }

    public Object getCurrentByTeacher(String id) throws InterruptedException {
        responseObject = null;

        String url = res.getString(R.string.server_address) + "/schedules/current/teacher/" + id;
        fileLogger.writeToLogFile("GET: " + url);
        CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.GET, url, new JSONObject(), this, this);
        jsonRequest.setTag(REQUEST_TAG);
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

    public Object getAll() throws InterruptedException {
        responseObject = null;

        String url = res.getString(R.string.server_address) + "schedules/";
        fileLogger.writeToLogFile("GET: " + url);
        CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.GET, url, new JSONObject(), this, this);
        jsonRequest.setTag(REQUEST_TAG);
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

    public static synchronized ScheduleService getInstance(Context context, Activity activity) {
        if (mInstance == null) {
            mInstance = new ScheduleService(context, activity);
        }
        return mInstance;
    }
}
