package com.example.anastasiya.arduinoserialcom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.example.anastasiya.arduinoserialcom.routers.TeacherHttpRequestTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    private Context context;
    private Activity activity;
    private Resources res;

    TextView tName;
    TextView tSurname;
    TextView tPatronymic;
    TextView tUID;
    TextView tSubject;
    ImageView tImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tName = (TextView)findViewById(R.id.tvTName);
        tSurname = (TextView)findViewById(R.id.tvTSurname);
        tPatronymic = (TextView)findViewById(R.id.tvTPatronymic);
        tSubject = (TextView)findViewById(R.id.tvTSubject);
        tUID = (TextView)findViewById(R.id.tvTUID);
        tImage = (ImageView)findViewById(R.id.image_teacher);

        Intent intent = getIntent();
        String teacher_uid = intent.getStringExtra("teacher_uid");
        context = this.getApplicationContext();
        activity = this;
        res = context.getResources();

        TeacherHttpRequestTask asyncTask = new TeacherHttpRequestTask(new IAsyncResponse() {
            @Override
            public void processFinish(Object output) {
                try {
                    tName.setText(((JSONObject) output).getJSONObject("data").getString("name"));
                    tSurname.setText(((JSONObject) output).getJSONObject("data").getString("surname"));
                    tPatronymic.setText(((JSONObject) output).getJSONObject("data").getString("patronymic"));
                    if (!((JSONObject) output).getJSONObject("data").isNull("subjectId")) {
                        tSubject.setText("Subject: " + ((JSONObject) output).getJSONObject("data").getJSONObject("subject").getString("name"));
                    } else {
                        tSubject.setText("");
                    }
                    tUID.setText("UID: " + ((JSONObject) output).getJSONObject("data").getString("uid"));
                    String url = res.getString(R.string.server_address) + "/teachers/avatar/" +
                            ((JSONObject) output).getJSONObject("data").getString("avatarId");
                    Picasso.with(context)
                            .load(url)
                            .into(tImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, context, activity);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "getTeacherByUID", teacher_uid);
    }
}
