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
import com.example.anastasiya.arduinoserialcom.routers.MarkHttpRequestTask;
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

    Boolean attendanceCreated = false;
    Boolean pupilFound = false;

    Spinner spSubjects;
    Spinner spMarkTypes;

    Resources res;

    Integer scoreType = 1;
    Integer scoreValue = 1;
    String subjectName;
    String pupilId = null;

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
                    if(!((JSONObject)output).getJSONObject("data").isNull("id")) {
                        pupilFound = true;
                        pupilId = ((JSONObject)output).getJSONObject("data").getString("id");

                        if(!((JSONObject)output).getJSONObject("data").isNull("attendance")) {
                           attendanceCreated = true;
                        };
                    }

                    if(pupilFound) {
                        if(attendanceCreated) {
                            alertManager.show("Отметка о присутствии:", "Выставлена успешна.");
                        } else {
                            alertManager.show("Отметка о присутствии:", "Не выставлена. В расписании отсутствуют занятия на данное время.");
                        }
                    } else {
                        alertManager.show("Ученик не найден:", "В системе отсутствуют ученики с такой картой.");
                    }

                    tvName.setText(((JSONObject) output).getJSONObject("data").getString("name"));
                    tvSurname.setText(((JSONObject) output).getJSONObject("data").getString("surname"));
                    tvPatronymic.setText(((JSONObject) output).getJSONObject("data").getString("patronymic"));
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

    public void saveMark(View v) {
        if(pupilId == null || pupilId.equals(JSONObject.NULL) || pupilId.isEmpty()) {
            alertManager.show("Оценка не может быть выставлена:", "Ученик с данной картой отсутствует в системе.");
        } else {
            MarkHttpRequestTask asyncTask = new MarkHttpRequestTask(new IAsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    alertManager.show("Оценка:", "Оценка выставлена ученику успешно.");
                }
            }, context, activity);

            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "create", pupilId, "1", scoreValue.toString(), "1", "");
        }
    }
}
