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

import org.json.JSONException;
import org.json.JSONObject;

public class PupilService implements Response.Listener<JSONObject>, Response.ErrorListener {
    private static PupilService mInstance;
    private RequestQueue mQueue;
    private static Object responseObject;
    private FileLogger fileLogger;
    public static final String REQUEST_TAG = "PupilService";
    private static final Object syncObject = new Object();
    private Resources res;

    private PupilService(Context context, Activity activity) {
        mQueue = CustomVolleyRequestQueue.getInstance(context).getRequestQueue();
        res = context.getResources();
        fileLogger = FileLogger.getInstance(context, activity);
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
        fileLogger.writeToLogFile("pupils uid: " + uid);
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

    public Object getPupilsByClassId(String id) throws InterruptedException {
        responseObject = null;
        String url = res.getString(R.string.server_address) + "/pupils/class/" + id;
        fileLogger.writeToLogFile("class id: " + id);
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

    public Object create(String surname, String name, String patronymic, Integer cardId, Integer classId, Integer avatarId) {
        responseObject = null;
        String url = res.getString(R.string.server_address) + "/pupils";
        JSONObject pupil = new JSONObject();
        try {
            pupil.put("surname", surname);
            pupil.put("name", name);
            pupil.put("patronymic", patronymic);
            if(cardId != null) {
                pupil.put("cardId", cardId);
            }
            if(classId != null) {
                pupil.put("classId", classId);
            }
            pupil.put("avatarId", avatarId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.POST, url, pupil, this, this);
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
        String url = res.getString(R.string.server_address) + "/pupils/" + id;
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

    public static synchronized PupilService getInstance(Context context, Activity activity) {
        if (mInstance == null) {
            mInstance = new PupilService(context, activity);
        }
        return mInstance;
    }
}
