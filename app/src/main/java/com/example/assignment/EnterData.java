package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EnterData extends AppCompatActivity {
    EditText name, id, designation;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri photoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);

        photoUri = Uri.parse(getIntent().getStringExtra("Image"));

        name = findViewById(R.id.name);
        id = findViewById(R.id.empid);
        designation = findViewById(R.id.des);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Employees");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public void submit(View view){
        String name = this.name.getText().toString();
        String id = this.id.getText().toString();
        String designation = this.designation.getText().toString();
        if(!name.isEmpty() || !id.isEmpty() || !designation.isEmpty()){
           String key =  databaseReference.push().getKey();
           uploadImage(photoUri,key,new Employees(name,id,designation));

        }else{
            Toast.makeText(getApplicationContext(),"Please check all fields",Toast.LENGTH_LONG).show();
        }

    }


    private void uploadImage(Uri filePath, String key, Employees emp) {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUri.onSuccessTask((SuccessContinuation<Uri, Uri>) uri -> {
                            databaseReference.child(key).setValue(emp.setPhotoUrl(uri.toString()));
                            startActivity(new Intent(EnterData.this,MainActivity.class));
                            finish();
                            return null;
                        });

                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }
}

class Employees{
    public String Name ;
    public String EmpId ;
    public String Designation ;

    public Employees setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
        return this;
    }

    public String PhotoUrl;

    public Employees(){}

    public Employees(String name, String empId, String designation) {
        Name = name;
        EmpId = empId;
        Designation = designation;
    }


}
