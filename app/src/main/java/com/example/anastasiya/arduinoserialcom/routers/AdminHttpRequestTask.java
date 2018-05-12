package com.example.anastasiya.arduinoserialcom.routers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.services.AdminService;

public class AdminHttpRequestTask extends AsyncTask<String, Object, Object> {
    IAsyncResponse delegate = null;
    private AdminService adminService;
    private FileLogger fileLogger;

    public AdminHttpRequestTask(IAsyncResponse delegate, Context context, Activity activity) {
       this.delegate = delegate;
       adminService = AdminService.getInstance(context, activity);
       fileLogger = FileLogger.getInstance(context, activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(String... params) {
        Object response = null;
        String methodName = params[0];

        fileLogger.writeToLogFile("\nRoute: " + methodName);

        switch (methodName) {
            case "signIn":
                String login = params[1];
                String password = params[2];
                response = adminService.signIn(login, password);
                break;
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
