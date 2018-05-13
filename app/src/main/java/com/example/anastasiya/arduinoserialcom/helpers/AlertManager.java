package com.example.anastasiya.arduinoserialcom.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class AlertManager {
    private static AlertManager mInstance;
    private Activity activity;

    private AlertManager(Activity activity) {
        this.activity = activity;
    }

    public static synchronized AlertManager getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new AlertManager(activity);
        }
        return mInstance;
    }

    public void show(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(title)
            .setMessage(message)
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
}
