package com.example.anastasiya.arduinoserialcom;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anastasiya.arduinoserialcom.helpers.FileLogger;
import com.example.anastasiya.arduinoserialcom.routers.AvatarHttpRequestTask;
import com.example.anastasiya.arduinoserialcom.routers.IAsyncResponse;
import com.example.anastasiya.arduinoserialcom.routers.PupilHttpRequestTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AddPupilActivity extends AppCompatActivity {
    private Context context;
    private Activity activity;

    private FileLogger fileLogger;

    ImageView ivPupil;
    EditText etSurname;
    EditText etName;
    EditText etPatronymic;
    EditText etCardId;
    EditText etClassId;

    Integer REQUEST_CAMERA=1, SELECT_FILE=0;
    String avatarData;
    String avatarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pupil);

        activity = this;
        context = getApplicationContext();
        fileLogger = FileLogger.getInstance(context, activity);


        ivPupil = (ImageView) findViewById(R.id.image_add_pupil);

        etSurname = (EditText) findViewById(R.id.input_create_surname);
        etName = (EditText) findViewById(R.id.input_create_name);
        etPatronymic = (EditText) findViewById(R.id.input_create_patronymic);
        etCardId = (EditText) findViewById(R.id.input_create_card);
        etClassId = (EditText) findViewById(R.id.input_create_class);

        FloatingActionButton fabAvatar;
        fabAvatar = (FloatingActionButton) findViewById(R.id.button_add_pupil_avatar);
        fabAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                SelectImage();
            }
        });
    }

    private void SelectImage() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddPupilActivity.this);
        builder.setTitle("Add image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(items[i].equals("Camera")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if(items[i].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select file"), SELECT_FILE);
                } else if(items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    public void CreatePupil(View v) {
        PupilHttpRequestTask asyncTask = new PupilHttpRequestTask(new IAsyncResponse() {
            @Override
            public void processFinish(Object output) {
                String pupilId = null;
                try {
                    pupilId = ((JSONObject) output).getJSONObject("data").getString("id");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(avatarData != null && !avatarData.isEmpty()) {
                    AvatarHttpRequestTask asyncTask1 = new AvatarHttpRequestTask(new IAsyncResponse() {
                        @Override
                        public void processFinish(Object output1) {

                        }
                    }, context, activity);

                    asyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "uploadPupilAvatar", pupilId, avatarData, avatarName);
                }
            }
        }, context, activity);

        String surname = etSurname.getText().toString();
        String name = etName.getText().toString();
        String patronymic = etPatronymic.getText().toString();
        String cardId = etCardId.getText().toString();
        String classId = etClassId.getText().toString();
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "createPupil", surname, name, patronymic, cardId, classId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            avatarName = "avatar";
            if(requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                final Bitmap bitmap = (Bitmap) bundle.get("data");
                ivPupil.setImageBitmap(bitmap);

                avatarData = BitmapToBase64(bitmap);
            } else if(requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                ivPupil.setImageURI(selectedImageUri);
                try {
                    ivPupil.buildDrawingCache();
                    Bitmap bitmap = ivPupil.getDrawingCache();
                    avatarData = BitmapToBase64(bitmap);
                } catch (Exception e) {
                    fileLogger.writeToLogFile(e.getMessage());
                }
            }
        }
    }

    private String BitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
