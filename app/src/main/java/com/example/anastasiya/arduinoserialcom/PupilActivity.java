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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.anastasiya.arduinoserialcom.helpers.AlertManager;
import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.routers.PupilHttpRequestTask;
import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PupilActivity extends AppCompatActivity {
    private FileLogger fileLogger;
    public AlertManager alertManager;
    private Context context;
    private Activity activity;

    TextView tvName;
    TextView tvSurname;
    TextView tvPatronymic;
    TextView tvClass;
    TextView tvScoreValue;
    ImageView imvPupil;

    Spinner spSubjects;
    Spinner spMarkTypes;

    Resources res;

    Integer scoreType = 1;
    Integer scoreValue = 1;
    String subjectName;

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
        tvScoreValue = (TextView) findViewById(R.id.tvScoreValue);
        imvPupil = (ImageView) findViewById(R.id.image_pupil);

        Intent intent = getIntent();
        final String uid = intent.getStringExtra("uid");
        subjectName = intent.getStringExtra("subjectName");
        fileLogger.writeToLogFile(uid);
        alertManager = AlertManager.getInstance(activity);
        res = context.getResources();

        PupilHttpRequestTask asyncTask = new PupilHttpRequestTask(new IAsyncResponse() {
            @Override
            public void processFinish(Object output) {
                try {
                    tvName.setText(((JSONObject) output).getJSONObject("data").getString("name"));
                    tvSurname.setText(((JSONObject) output).getJSONObject("data").getString("surname"));
                    tvPatronymic.setText(((JSONObject) output).getJSONObject("data").getString("patronymic"));
                    tvClass.setText("Класс: 1A");
                    String url = res.getString(R.string.server_address) + "/pupils/avatar/" +
                            ((JSONObject) output).getJSONObject("data").getString("avatarId");
                   fileLogger.writeToLogFile("\nURL AVATAR:\n" + url + "\n");
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
        setSubjects();
        setMarkTypes();
    }

    private void setSubjects() {
        spSubjects = (Spinner)findViewById(R.id.spSubjects);
        List<String> subjects = new ArrayList<String>();
        subjects.add("Бел.мова");
        subjects.add("Русск.яз.");
        subjects.add("Матем.");
        subjects.add("Музыка");
        subjects.add("ФК и зд.");
        subjects.add("ОБЖ");
        subjects.add("Чел. и мир");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjects);

        spSubjects.setAdapter(dataAdapter);

        if (subjectName != null) {
            int spinnerPosition = dataAdapter.getPosition(subjectName);
            spSubjects.setSelection(spinnerPosition);
        }
    }

    private void setMarkTypes() {
        spMarkTypes = (Spinner)findViewById(R.id.spMarkTypes);
        List<String> types = new ArrayList<String>();
        types.add("класс");
        types.add("дом");
        types.add("тест");
        types.add("четверть");
        types.add("год");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);

        spMarkTypes.setAdapter(dataAdapter);
    }

    public void setMark(View v) {
        Button b = (Button)  findViewById(v.getId());
        this.scoreValue = Integer.parseInt(b.getText().toString());
        tvScoreValue.setText(b.getText().toString());
    }

    public void setMark2(View v) {
        this.scoreValue = 2;
        tvScoreValue.setText("2");
    }

    public void setMark3(View v) {
        this.scoreValue = 3;
        tvScoreValue.setText("3");
    }

    public void setMark4(View v) {
        this.scoreValue = 4;
        tvScoreValue.setText("4");
    }

    public void setMark5(View v) {
        this.scoreValue = 5;
        tvScoreValue.setText("5");
    }

    public void setMark6(View v) {
        this.scoreValue = 6;
        tvScoreValue.setText("6");
    }

    public void setMark7(View v) {
        this.scoreValue = 7;
        tvScoreValue.setText("7");
    }

    public void setMark8(View v) {
        this.scoreValue = 8;
        tvScoreValue.setText("8");
    }

    public void setMark9(View v) {
        this.scoreValue = 9;
        tvScoreValue.setText("9");
    }

    public void setMark10(View v) {
        this.scoreValue = 10;
        tvScoreValue.setText("10");
    }
}
