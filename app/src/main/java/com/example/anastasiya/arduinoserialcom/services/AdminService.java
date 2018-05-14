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

import org.json.JSONException;
import org.json.JSONObject;

public class AdminService implements Response.Listener<JSONObject>, Response.ErrorListener {
    private static AdminService mInstance;

    private RequestQueue mQueue;
    private static final String REQUEST_TAG = "AdminService";

    private static Object responseObject;
    private static final Object syncObject = new Object();

    private Resources res;

    private AdminService(Context context, Activity activity) {
        mQueue  = CustomVolleyRequestQueue.getInstance(context).getRequestQueue();
        res = context.getResources();
    }

    public Object signIn(String login, String password) {
        responseObject = null;
        String url = res.getString(R.string.server_address) + "/admins/login";
        JSONObject admin = new JSONObject();
        try {
            admin.put("login", login);
            admin.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.POST, url, admin, this, this);
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

    public static synchronized AdminService getInstance(Context context, Activity activity) {
        if (mInstance == null) {
            mInstance = new AdminService(context, activity);
        }
        return mInstance;
    }
}
