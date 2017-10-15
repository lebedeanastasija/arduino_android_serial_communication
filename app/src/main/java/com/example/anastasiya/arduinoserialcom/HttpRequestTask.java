package com.example.anastasiya.arduinoserialcom;

import android.content.Context;
import android.os.AsyncTask;

import com.example.anastasiya.arduinoserialcom.services.PupilService;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpRequestTask extends AsyncTask<Void, Object, Object>{
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
        protected Object doInBackground(Void... params) {
            Object response = null;
            try {
                response = pupilService.getPupils();
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
