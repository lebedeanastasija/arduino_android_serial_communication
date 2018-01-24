package com.example.anastasiya.arduinoserialcom;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.routers.PupilHttpRequestTask;
import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PupilActivity extends AppCompatActivity {
    private FileLogger fileLogger;
    private Context context;

    TextView tvName;
    TextView tvSurname;
    TextView tvPatronymic;
    TextView tvClass;
    TextView tvSubject;
    ImageView imvPupil;
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pupil);
        fileLogger = FileLogger.getInstance(this.getApplicationContext(), this);

        tvName = (TextView) findViewById(R.id.tvName);
        tvSurname = (TextView) findViewById(R.id.tvSurname);
        tvPatronymic = (TextView) findViewById(R.id.tvPatronymic);
        tvClass = (TextView) findViewById(R.id.tvClass);
        tvSubject = (TextView) findViewById(R.id.tvTSubject);
        imvPupil = (ImageView) findViewById(R.id.image_pupil);

        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        fileLogger.writeToLogFile(uid);
        context = getApplicationContext();
        res = context.getResources();

        PupilHttpRequestTask asyncTask = new PupilHttpRequestTask(new IAsyncResponse() {
            @Override
            public void processFinish(Object output) {
                try {
                    tvName.setText("Name: " + ((JSONObject) output).getJSONObject("data").getString("name"));
                    tvSurname.setText("Surname: " + ((JSONObject) output).getJSONObject("data").getString("surname"));
                    tvPatronymic.setText("Patronymic: " + ((JSONObject) output).getJSONObject("data").getString("patronymic"));
                    tvClass.setText("Class: " + ((JSONObject) output).getJSONObject("data").getJSONObject("class").getString("number") +
                            ((JSONObject) output).getJSONObject("data").getJSONObject("class").getString("letter"));
                    tvSubject.setText("Subject: unknown");
                    String url = res.getString(R.string.server_address) + "/pupils/avatar/" +
                            ((JSONObject) output).getJSONObject("data").getString("avatarId");
                    Picasso.with(context)
                            .load(url)
                            .into(imvPupil);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this.getApplicationContext());
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "getPupilByUid", uid);
    }
}
