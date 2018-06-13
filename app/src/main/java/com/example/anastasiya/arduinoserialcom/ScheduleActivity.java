package com.example.anastasiya.arduinoserialcom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.anastasiya.arduinoserialcom.adapters.ScheduleListAdapter;
import com.example.anastasiya.arduinoserialcom.helpers.AlertManager;
import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.example.anastasiya.arduinoserialcom.routers.ScheduleHttpRequestTask;
import com.example.anastasiya.arduinoserialcom.routers.SubjectHttpRequestTask;
import com.example.anastasiya.arduinoserialcom.routers.TeacherHttpRequestTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    private TextView tvWeekDay;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<String> subjectNames = new ArrayList<String>();
    private List<String> timeNames = new ArrayList<String>();
    private List<String> roomNames = new ArrayList<String>();

    private Context context;
    private Activity activity;

    private AlertManager alertManager;
    private FileLogger fileLogger;
    private String teacherId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        context = this.getApplicationContext();
        activity = this;

        alertManager = AlertManager.getInstance(activity);
        fileLogger = FileLogger.getInstance(context, activity);

        final Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");

        mRecyclerView = (RecyclerView) findViewById(R.id.rvSchedule);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        tvWeekDay = (TextView) findViewById(R.id.tvWeekDay);
    }

    @Override
    public void onStart() {
        super.onStart();
        getSchedule();
    }

    private void getSchedule() {
        subjectNames.clear();
        timeNames.clear();
        roomNames.clear();
        fileLogger.writeToLogFile("GET /schedule");

        try {
            ScheduleHttpRequestTask asyncTask = new ScheduleHttpRequestTask(new IAsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    try {
                        JSONArray jsonArray = ((JSONObject) output).getJSONArray("data");
                        for(int i = 0; i < jsonArray.length(); i++) {
                            if(i == 0) {
                                tvWeekDay.setText(jsonArray.getJSONObject(i).getString("weekDay"));
                            }
                            subjectNames.add(jsonArray.getJSONObject(i).getString("subjectName"));
                            timeNames.add(jsonArray.getJSONObject(i).getString("time"));
                            roomNames.add(jsonArray.getJSONObject(i).getString("roomName"));
                        }
                        mAdapter = new ScheduleListAdapter(subjectNames, timeNames, roomNames, context, activity);
                        mRecyclerView.setAdapter(mAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, context, activity);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "getDayByTeacher", teacherId);
        } catch(Exception e) {
            fileLogger.writeToLogFile("\nERROR:\n" + e.getMessage());
        }
    }
}
