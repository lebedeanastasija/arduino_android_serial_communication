package com.example.anastasiya.arduinoserialcom.routers;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.services.SubjectService;

public class SubjectHttpRequestTask extends AsyncTask<String, Object, Object> {
    IAsyncResponse delegate = null;
    private SubjectService subjectService;
    private FileLogger fileLogger;

    public SubjectHttpRequestTask(IAsyncResponse delegate, Context context, Activity activity){
        this.delegate = delegate;
        subjectService = SubjectService.getInstance(context, activity);
        fileLogger = FileLogger.getInstance(context, activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(String... params) {
        Object response = null;

        fileLogger.writeToLogFile("test2");
        String methodName = params[0];

        switch (methodName) {
            case "removeById":
                String id = params[1];
                response = subjectService.removeById(id);
            case "getSubjects":
                response = subjectService.getSubjects();
                break;
            case "createSubject":
                String name = params[1];
                String shortName = params[2];
                String description = params[3];

                response = subjectService.create(name, shortName, description);
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
