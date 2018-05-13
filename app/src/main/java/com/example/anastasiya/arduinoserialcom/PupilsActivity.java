package com.example.anastasiya.arduinoserialcom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.anastasiya.arduinoserialcom.helpers.AlertManager;
import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.example.anastasiya.arduinoserialcom.routers.PupilHttpRequestTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PupilsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<String> pupils = new ArrayList<String>();
    private List<String> pupilIds = new ArrayList<String>();
    private List<String> avatarIds = new ArrayList<String>();

    private Context context;
    private Activity activity;

    private AlertManager alertManager;
    private FileLogger fileLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pupils);

        context = this.getApplicationContext();
        activity = this;

        alertManager = AlertManager.getInstance(activity);
        fileLogger = FileLogger.getInstance(context, activity);

        fileLogger.writeToLogFile("on create before recycle init");
        mRecyclerView = (RecyclerView) findViewById(R.id.rvPupils);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        fileLogger.writeToLogFile("on create end");
        //getPupils();
    }

    @Override
    public void onStart() {
        fileLogger.writeToLogFile("on start");
        super.onStart();
        getPupils();
    }

    private void getPupils() {

        fileLogger.writeToLogFile("get pupils");
        try {
            PupilHttpRequestTask asyncTask = new PupilHttpRequestTask(new IAsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    try {
                        JSONArray jsonArray = ((JSONObject) output).getJSONArray("data");
                        for(int i = 0; i < jsonArray.length(); i++) {
                            pupils.add(jsonArray.getJSONObject(i).getString("surname") + " " +
                                    jsonArray.getJSONObject(i).getString("name") + " " +
                                    jsonArray.getJSONObject(i).getString("patronymic"));
                            pupilIds.add(jsonArray.getJSONObject(i).getString("id"));
                            if(!jsonArray.getJSONObject(i).getString("avatarId").equals("null")) {
                                avatarIds.add(jsonArray.getJSONObject(i).getString("avatarId"));
                            } else {
                                avatarIds.add("1");
                            }
                        }
                        mAdapter = new PupilsListAdapter(pupils, pupilIds, avatarIds, context, activity);
                        mRecyclerView.setAdapter(mAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, context, activity);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "getPupils");
        } catch(Exception e) {
            fileLogger.writeToLogFile("\nERROR:\n" + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pupils_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_add_pupil:
                Intent add_pupil_intent = new Intent(this, AddPupilActivity.class);
                startActivity(add_pupil_intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
