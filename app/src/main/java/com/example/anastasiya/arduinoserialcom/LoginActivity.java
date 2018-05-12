package com.example.anastasiya.arduinoserialcom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void ChooseTeacher(View v) {
        Intent intent = new Intent(LoginActivity.this, TeacherLoginActivity.class);
        startActivity(intent);
    }

    public void ChooseAdmin(View v) {
        Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
        startActivity(intent);
    }
}
