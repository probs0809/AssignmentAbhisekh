package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static android.content.Intent.ACTION_DIAL;

public class MainActivity extends AppCompatActivity {

    Button camera_open_id;
    ImageView click_image_id;
    Uri photoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera_open_id = findViewById(R.id.camera_button);
        click_image_id = findViewById(R.id.click_image);
        camera_open_id.setOnClickListener(v -> {
//            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(camera_intent, pic_id);
             dispatchTakePictureIntent();

        });
    }

    static final int REQUEST_TAKE_PHOTO = 123;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                photoUri = Uri.fromFile(photoFile);
            } catch (IOException ex) {
                Toast.makeText(getApplicationContext(),"Problem",Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.assignment.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    String currentPhotoPath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            Bitmap photo = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                try {
                    photo = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(),photoUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(),photoUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            click_image_id.setImageBitmap(photo);

        }
    }

    public void dial(View view) throws SecurityException {
        Intent i = new Intent(ACTION_DIAL);
        startActivity(i);
    }

    public void show_list(View view) {
        Intent i1=new Intent(MainActivity.this, ListActivity.class);
        startActivity(i1);
    }

    public void enterDatabase(View view) {
        if(photoUri!=null) {
            Intent i = new Intent(MainActivity.this, EnterData.class);
            i.putExtra("Image", photoUri.toString());
            startActivity(i);
        }
    }


}

