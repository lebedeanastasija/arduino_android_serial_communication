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

import com.example.anastasiya.arduinoserialcom.adapters.SubjectsListAdapter;
import com.example.anastasiya.arduinoserialcom.helpers.AlertManager;
import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.example.anastasiya.arduinoserialcom.routers.SubjectHttpRequestTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubjectsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<String> subjectNames = new ArrayList<String>();
    private List<String> shortNames = new ArrayList<String>();
    private List<String> subjectIds = new ArrayList<String>();

    private Context context;
    private Activity activity;

    private AlertManager alertManager;
    private FileLogger fileLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        context = this.getApplicationContext();
        activity = this;

        alertManager = AlertManager.getInstance(activity);
        fileLogger = FileLogger.getInstance(context, activity);

        mRecyclerView = (RecyclerView) findViewById(R.id.rvSubjects);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getSubjects();
    }

    @Override
    public void onStart() {
        super.onStart();
        getSubjects();
    }

    private void getSubjects() {
        subjectNames.clear();
        shortNames.clear();
        subjectIds.clear();
        fileLogger.writeToLogFile("GET /subjects");

        try {
            SubjectHttpRequestTask asyncTask = new SubjectHttpRequestTask(new IAsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    try {
                        JSONArray jsonArray = ((JSONObject) output).getJSONArray("data");
                        for(int i = 0; i < jsonArray.length(); i++) {
                            subjectNames.add(jsonArray.getJSONObject(i).getString("name"));
                            shortNames.add(jsonArray.getJSONObject(i).getString("shortName"));
                            subjectIds.add(jsonArray.getJSONObject(i).getString("id"));
                        }
                        mAdapter = new SubjectsListAdapter(subjectNames, shortNames, subjectIds, context, activity);
                        mRecyclerView.setAdapter(mAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, context, activity);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "getSubjects");
        } catch(Exception e) {
            fileLogger.writeToLogFile("\nERROR:\n" + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subjects_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_add_subject:
                Intent add_subject_intent = new Intent(this, AddSubjectActivity.class);
                startActivity(add_subject_intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
