package com.example.anastasiya.arduinoserialcom.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FileLogger {
    private static FileLogger mInstance;
    private Context context;
    private Activity activity;

    private FileLogger(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public static synchronized FileLogger getInstance(Context context, Activity activity) {
        if (mInstance == null) {
            mInstance = new FileLogger(context, activity);
        }
        return mInstance;
    }

    public void writeToLogFile(String text) {
        if(text != null) {
            File myFile = new File("/storage/emulated/0/ElectronicDiaryLog.txt");

            if (myFile.exists()) {
                try {
                    FileOutputStream fostream = new FileOutputStream(myFile, true);
                    PrintWriter pw = new PrintWriter(fostream, true);
                    Date date = new Date();
                    pw.println("# " + date.toString() + " : ");
                    pw.println(text);
                    pw.close();
                    fostream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if(ContextCompat.checkSelfPermission(this.context.getApplicationContext(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this.activity, WRITE_EXTERNAL_STORAGE)) {
                            ActivityCompat.requestPermissions(this.activity, new String[]{WRITE_EXTERNAL_STORAGE}, 0);
                        }
                    }

                    boolean created = myFile.createNewFile();
                    if(created) {
                        writeToLogFile(text);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
