package com.example.anastasiya.arduinoserialcom.routers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.anastasiya.arduinoserialcom.services.TeacherService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class TeacherHttpRequestTask extends AsyncTask<String, Object, Object>{
    IAsyncResponse delegate = null;
    private TeacherService teacherService;

    public TeacherHttpRequestTask(IAsyncResponse delegate, Context context) {
        this.delegate = delegate;
        teacherService = TeacherService.getInstance(context);
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
                case "getScheduleByUID":
                    String uid = params[1];
                    response = teacherService.getScheduleByUid(uid);
                    break;
                case "getTeacherByUID":
                    uid = params[1];
                    response = teacherService.getTeacherByUid(uid);
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

    public void writeToLogFile(String text) {
        if(text != null) {
            File externalStorageDir = Environment.getExternalStorageDirectory();
            File myFile = new File(externalStorageDir, "yourfilename.txt");

            if (myFile.exists()) {
                try {
                    FileOutputStream fostream = new FileOutputStream(myFile);
                    OutputStreamWriter oswriter = new OutputStreamWriter(fostream);
                    BufferedWriter bwriter = new BufferedWriter(oswriter);
                    bwriter.append(text);
                    bwriter.newLine();
                    bwriter.close();
                    oswriter.close();
                    fostream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    myFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
