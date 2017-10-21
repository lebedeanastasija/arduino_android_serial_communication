package com.example.anastasiya.arduinoserialcom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PupilActivity extends AppCompatActivity {
    TextView tvName;
    TextView tvSurname;
    TextView tvPatronymic;
    TextView tvClass;
    TextView tvSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pupil);

        tvName = (TextView) findViewById(R.id.tvName);
        tvSurname = (TextView) findViewById(R.id.tvSurname);
        tvPatronymic = (TextView) findViewById(R.id.tvPatronymic);
        tvClass = (TextView) findViewById(R.id.tvClass);
        tvSubject = (TextView) findViewById(R.id.tvSubject);

        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");


        HttpRequestTask asyncTask = new HttpRequestTask(new IAsyncResponse() {
            @Override
            public void processFinish(Object output){
                try {
                    tvName.setText("Name: " + ((JSONObject)output).getJSONObject("data").getString("name"));
                    tvSurname.setText("Surname: " + ((JSONObject)output).getJSONObject("data").getString("surname"));
                    tvPatronymic.setText("Patronymic: " + ((JSONObject)output).getJSONObject("data").getString("patronymic"));
                    tvClass.setText("Class: " + ((JSONObject)output).getJSONObject("data").getString("classId"));
                    tvSubject.setText("Subject: Math");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this.getApplicationContext());
        asyncTask.execute(uid);
    }
}
