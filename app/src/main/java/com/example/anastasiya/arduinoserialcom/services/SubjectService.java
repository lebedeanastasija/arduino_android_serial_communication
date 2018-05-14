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

import org.json.JSONException;
import org.json.JSONObject;

public class SubjectService implements Response.Listener<JSONObject>, Response.ErrorListener {
    private static SubjectService mInstance;
    private RequestQueue mQueue;
    private static Object responseObject;
    private FileLogger fileLogger;
    public static final String REQUEST_TAG = "SubjectService";
    private static final Object syncObject = new Object();
    private Resources res;

    private SubjectService(Context context, Activity activity) {
        mQueue = CustomVolleyRequestQueue.getInstance(context).getRequestQueue();
        res = context.getResources();
        fileLogger = FileLogger.getInstance(context, activity);
    }

    public Object getSubjects() {
        responseObject = null;
        String url = res.getString(R.string.server_address) + "/subjects";
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

    public Object create(String name, String shortName, String description) {
        responseObject = null;
        String url = res.getString(R.string.server_address) + "/subjects";
        JSONObject subject = new JSONObject();
        try {
            subject.put("name", name);
            subject.put("shortName", shortName);
            if(description != null && !description.isEmpty()) {
                subject.put("description", description);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.POST, url, subject, this, this);
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

    public Object removeById(String id) {
        responseObject = null;
        String url = res.getString(R.string.server_address) + "/subjects/" + id;
        CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.DELETE, url, new JSONObject(), this, this);
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
        fileLogger.writeToLogFile(response.toString());
        synchronized (syncObject) {
            syncObject.notify();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        responseObject = error;
    }

    public static synchronized SubjectService getInstance(Context context, Activity activity) {
        if(mInstance == null) {
            mInstance = new SubjectService(context, activity);
        }
        return mInstance;
    }

}
