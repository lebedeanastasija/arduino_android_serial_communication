package com.example.anastasiya.arduinoserialcom;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.anastasiya.arduinoserialcom.routers.AdminHttpRequestTask;
import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;

import org.json.JSONObject;

public class AdminLoginActivity extends AppCompatActivity {
    private Context context;
    private Activity activity;

    EditText login;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        activity = this;
        context = getApplicationContext();

        login = (EditText)findViewById(R.id.etLogin);
        password = (EditText)findViewById(R.id.etPassword);
    }

    public void SignIn(View v) {
        String login = this.login.getText().toString();
        String password = this.password.getText().toString();

        AdminHttpRequestTask asyncTask = new AdminHttpRequestTask(new IAsyncResponse() {
            @Override
            public void processFinish(Object output) {
                JSONObject response = null;
                try {
                    response = ((JSONObject) output).getJSONObject("data");

                    if(response.equals(JSONObject.NULL)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminLoginActivity.this);
                        builder.setTitle("Ошибка входа!")
                                .setMessage("Неверный логин или пароль!")
                                .setCancelable(false)
                                .setNegativeButton("ОК",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, context, activity);

        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "signIn", login, password);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
