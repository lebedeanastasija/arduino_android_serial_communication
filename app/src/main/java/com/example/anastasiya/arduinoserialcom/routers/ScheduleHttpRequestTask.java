package com.example.anastasiya.arduinoserialcom.routers;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.example.anastasiya.arduinoserialcom.helpers.AlertManager;
import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.services.ScheduleService;

public class ScheduleHttpRequestTask extends AsyncTask<String, Object, Object> {
    private ScheduleService scheduleService;
    private FileLogger fileLogger;
    private AlertManager alertManager;

    IAsyncResponse delegate = null;


    public ScheduleHttpRequestTask(IAsyncResponse delegate, Context context, Activity activity) {
        this.delegate = delegate;
        scheduleService = ScheduleService.getInstance(context, activity);
        fileLogger = FileLogger.getInstance(context, activity);
        alertManager = AlertManager.getInstance(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(String... params) {
        Object response = null;
        String methodName = params[0];
        try {
            //alertManager.show(methodName, "TEST");
            switch (methodName) {
                case "getCurrentByTeacher":
                    String id = params[1];
                    //alertManager.show(id, "TEST");
                    response = scheduleService.getCurrentByTeacher(id);
                    break;
                case "getAll":
                    response = scheduleService.getAll();
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
        fileLogger.writeToLogFile("Teacher http request task onPostExecute.");
        delegate.processFinish(result);
    }
}
