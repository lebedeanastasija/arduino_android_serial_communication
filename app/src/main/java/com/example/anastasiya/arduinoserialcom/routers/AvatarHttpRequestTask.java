package com.example.anastasiya.arduinoserialcom.routers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.services.AvatarService;

public class AvatarHttpRequestTask  extends AsyncTask<String, Object, Object> {
    IAsyncResponse delegate = null;
    private AvatarService avatarService;
    //private FileLogger fileLogger;

    public AvatarHttpRequestTask(IAsyncResponse delegate, Context context, Activity activity){
        this.delegate = delegate;
        avatarService = AvatarService.getInstance(context, activity);
        //fileLogger = FileLogger.getInstance(context, activity);
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
            switch (methodName) {
                case "uploadPupilAvatar":
                    String pupilId = params[1];
                    String data = params[2];
                    String name = params[3];
                    response = avatarService.uploadPupilAvatar(pupilId, data, name);
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
