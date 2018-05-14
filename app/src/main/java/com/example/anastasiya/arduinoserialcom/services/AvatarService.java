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

public class AvatarService implements Response.Listener<JSONObject>, Response.ErrorListener {
    private static AvatarService mInstance;
    private RequestQueue mQueue;
    private static Object responseObject;
    private FileLogger fileLogger;
    public static final String REQUEST_TAG = "AvatarService";
    private static final Object syncObject = new Object();
    private Resources res;

    private AvatarService(Context context, Activity activity) {
        mQueue = CustomVolleyRequestQueue.getInstance(context).getRequestQueue();
        res = context.getResources();
        fileLogger = FileLogger.getInstance(context, activity);
    }

    public Object uploadPupilAvatar(String pupilId, String data, String name) throws InterruptedException {
        responseObject = null;
        String url = res.getString(R.string.server_address) + "/avatars/pupil/" + pupilId;
        fileLogger.writeToLogFile("file name: " + name);
        fileLogger.writeToLogFile("string: " + data);
        JSONObject avatar = new JSONObject();
        try {
            avatar.put("name", name);
            avatar.put("data", data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.POST, url, avatar, this, this);
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

    public static synchronized AvatarService getInstance(Context context, Activity activity) {
        if (mInstance == null) {
            mInstance = new AvatarService(context, activity);
        }
        return mInstance;
    }
}
