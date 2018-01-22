package com.example.anastasiya.arduinoserialcom.routers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.example.anastasiya.arduinoserialcom.services.PupilService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class PupilHttpRequestTask extends AsyncTask<String, Object, Object>{
        IAsyncResponse delegate = null;
        private PupilService pupilService;

        public PupilHttpRequestTask(IAsyncResponse delegate, Context context){
            this.delegate = delegate;
            pupilService = PupilService.getInstance(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(String... params) {
            Object response = null;

            writeToLogFile("test2");
            String methodName = params[0];

            try {
                switch (methodName) {
                    case "getPupilByUid":
                        String uid = params[1];
                        response = pupilService.getPupilByUid(uid);
                        break;
                    case "getPupils":
                        response = pupilService.getPupils();
                        break;
                    case "getPupilsByClassId":
                        String classId = params[1];
                        response = pupilService.getPupilsByClassId(classId);
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
        File externalStorageDir = Environment.getExternalStorageDirectory();
        File myFile = new File(externalStorageDir, "yourfilename.txt");

        if (myFile.exists()) {
            try {
                FileOutputStream fostream = new FileOutputStream(myFile);
                OutputStreamWriter oswriter = new OutputStreamWriter(fostream);
                BufferedWriter bwriter = new BufferedWriter(oswriter);
                bwriter.write(text);
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
