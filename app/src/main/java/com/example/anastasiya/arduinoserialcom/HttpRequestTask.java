package com.example.anastasiya.arduinoserialcom;

import android.content.Context;
import android.os.AsyncTask;

import com.example.anastasiya.arduinoserialcom.services.PupilService;

import org.json.JSONException;
import org.json.JSONObject;

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
            String guid = params[0];
            try {
                response = pupilService.getPupilByGuid(guid);
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
