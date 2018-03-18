package com.example.anastasiya.arduinoserialcom.routers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.services.TeacherService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class TeacherHttpRequestTask extends AsyncTask<String, Object, Object>{
    private TeacherService teacherService;
    private FileLogger fileLogger;

    IAsyncResponse delegate = null;


    public TeacherHttpRequestTask(IAsyncResponse delegate, Context context, Activity activity) {
        this.delegate = delegate;
        teacherService = TeacherService.getInstance(context, activity);
        fileLogger = FileLogger.getInstance(context, activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(String... params) {
        fileLogger.writeToLogFile("dddddd");
        Object response = null;
        String methodName = params[0];
        try {
            switch (methodName) {
                case "getScheduleByUID":
                    String uid = params[1];
                    response = teacherService.getScheduleByUid(uid);
                    break;
                case "getTeacherByUID":
                    uid = params[1];
                    fileLogger.writeToLogFile("UID: " + uid);
                    response = teacherService.getTeacherByUid(uid);
                    fileLogger.writeToLogFile("Response: "+ response.toString());
                    break;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}
