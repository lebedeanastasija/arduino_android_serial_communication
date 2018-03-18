package com.example.anastasiya.arduinoserialcom.routers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.example.anastasiya.arduinoserialcom.services.PupilService;

public class PupilHttpRequestTask extends AsyncTask<String, Object, Object>{
        IAsyncResponse delegate = null;
        private PupilService pupilService;
        private FileLogger fileLogger;

        public PupilHttpRequestTask(IAsyncResponse delegate, Context context, Activity activity){
            this.delegate = delegate;
            pupilService = PupilService.getInstance(context, activity);
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
                    case "createPupil":
                        String surname = params[1];
                        String name = params[2];
                        String patronymic = params[3];
                        String newPupilUID = params[4];
                        Integer newPupilClassId = Integer.parseInt(params[5]);
                        Integer newPupilAvatarId = 1;
                        response = pupilService.create(surname, name, patronymic, newPupilUID, newPupilClassId, newPupilAvatarId);
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
