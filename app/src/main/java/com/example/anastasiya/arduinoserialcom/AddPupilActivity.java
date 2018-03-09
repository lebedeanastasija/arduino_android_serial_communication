package com.example.anastasiya.arduinoserialcom;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AddPupilActivity extends AppCompatActivity {
    ImageView ivPupil;
    TextView tvAvatarPath;
    Integer REQUEST_CAMERA=1, SELECT_FILE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pupil);
        FloatingActionButton fabAvatar;
        ivPupil = (ImageView) findViewById(R.id.image_add_pupil);
        tvAvatarPath = (TextView) findViewById(R.id.label_avatar_path);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                final Bitmap bitmap = (Bitmap) bundle.get("data");
                ivPupil.setImageBitmap(bitmap);
            } else if(requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                ivPupil.setImageURI(selectedImageUri);
                String rowPath = selectedImageUri.getPath();
                File imageFile = new File(rowPath);
                tvAvatarPath.setText(imageFile.getPath());
                try {
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(imageFile));

                } catch (FileNotFoundException e) {
                    Log.e("ERROR", e.getMessage());
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
