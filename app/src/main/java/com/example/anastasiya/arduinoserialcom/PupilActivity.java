package com.example.anastasiya.arduinoserialcom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private Activity activity;

    TextView tvName;
    TextView tvSurname;
    TextView tvPatronymic;
    TextView tvClass;
    TextView tvSubject;
    ImageView imvPupil;
    RadioGroup rgScoreType;
    NumberPicker npScore;

    Resources res;

    String scoreType;
    Integer scoreValue = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pupil);

        activity = this;
        context = getApplicationContext();

        fileLogger = FileLogger.getInstance(context, activity);

        tvName = (TextView) findViewById(R.id.tvName);
        tvSurname = (TextView) findViewById(R.id.tvSurname);
        tvPatronymic = (TextView) findViewById(R.id.tvPatronymic);
        tvClass = (TextView) findViewById(R.id.tvClass);
        tvSubject = (TextView) findViewById(R.id.tvTSubject);
        imvPupil = (ImageView) findViewById(R.id.image_pupil);
        rgScoreType = (RadioGroup) findViewById(R.id.rgScoreType);
        rgScoreType.check(R.id.rbClassWork);
        npScore = (NumberPicker) findViewById(R.id.npScore);
        npScore.setMinValue(1);
        npScore.setMaxValue(10);
        npScore.setValue(scoreValue);

        npScore.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                scoreValue = newVal;
            }
        });

        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        fileLogger.writeToLogFile(uid);
        res = context.getResources();

        PupilHttpRequestTask asyncTask = new PupilHttpRequestTask(new IAsyncResponse() {
            @Override
            public void processFinish(Object output) {
                try {
                    tvName.setText(((JSONObject) output).getJSONObject("data").getString("name"));
                    tvSurname.setText(((JSONObject) output).getJSONObject("data").getString("surname"));
                    tvPatronymic.setText(((JSONObject) output).getJSONObject("data").getString("patronymic"));
                    tvClass.setText("Class: " + ((JSONObject) output).getJSONObject("data").getJSONObject("class").getString("number") +
                            ((JSONObject) output).getJSONObject("data").getJSONObject("class").getString("letter"));
                    tvSubject.setText("Subject: unknown");
                    String url = res.getString(R.string.server_address) + "/pupils/avatar/" +
                            ((JSONObject) output).getJSONObject("data").getString("avatarId");
                    Picasso
                    .with(context)
                    .load(url)
                    .into(imvPupil);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, context, activity);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "getPupilByUid", uid);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.rbClassWork:
                if (checked)
                    scoreType = "class";
                    break;
            case R.id.rbTestWork:
                if (checked)
                    scoreType = "test";
                    break;
            case R.id.rbHomeWork:
                if (checked)
                    scoreType = "home";
                    break;
        }
    }
}
