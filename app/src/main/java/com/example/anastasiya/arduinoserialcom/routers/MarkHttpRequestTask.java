package com.example.anastasiya.arduinoserialcom.routers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.services.MarkService;

public class MarkHttpRequestTask extends AsyncTask<String, Object, Object>{
    IAsyncResponse delegate = null;
    private MarkService markService;
    private FileLogger fileLogger;

    public MarkHttpRequestTask(IAsyncResponse delegate, Context context, Activity activity){
        this.delegate = delegate;
        markService = MarkService.getInstance(context, activity);
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

        //try {
            switch (methodName) {
                case "create":
                    Integer pupilId = Integer.parseInt(params[1]);
                    Integer typeId = Integer.parseInt(params[2]);
                    Integer valueId = Integer.parseInt(params[3]);
                    Integer subjectId = null; // params[4]
                    Integer scheduleId = null; // params[5]

                    if(!params[4].equals("")){
                        subjectId = Integer.parseInt(params[4]);
                    }
                    if(!params[5].equals("")) {
                        scheduleId = Integer.parseInt(params[5]);
                    }

                    response = markService.create(pupilId, typeId, valueId, subjectId, scheduleId);
                    break;
            }

        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
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
