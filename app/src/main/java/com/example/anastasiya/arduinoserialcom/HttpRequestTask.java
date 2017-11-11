package com.example.anastasiya.arduinoserialcom;

import android.content.Context;
import android.os.AsyncTask;

import com.example.anastasiya.arduinoserialcom.services.PupilService;

public class HttpRequestTask extends AsyncTask<String, Object, Object>{
        IAsyncResponse delegate = null;
        private PupilService pupilService;

        public HttpRequestTask(IAsyncResponse delegate, Context context){
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
