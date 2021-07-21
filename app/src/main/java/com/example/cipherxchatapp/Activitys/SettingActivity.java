package com.example.cipherxchatapp.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cipherxchatapp.Models.Users;
import com.example.cipherxchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    CircleImageView userSetting_img;
    EditText setting_name,setting_status;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    ImageView save;
    Uri selectedImageURI;
    String email,statusOnOff;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();

        userSetting_img=findViewById(R.id.userSetting_img);
        setting_name=findViewById(R.id.setting_name);
        setting_status=findViewById(R.id.setting_status);
        save=findViewById(R.id.save);

        DocumentReference reference=db.collection("Users").document(auth.getUid());

        StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                email=task.getResult().getString("email");
                String name=task.getResult().getString("name");
                String status=task.getResult().getString("status");
                String image=task.getResult().getString("imageURi");
                statusOnOff=task.getResult().getString("statusOnOff");

                setting_name.setText(name);
                setting_status.setText(status);
                Picasso.get().load(image).into(userSetting_img);
            }
        });

        userSetting_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String name= setting_name.getText().toString();
                String status=setting_status.getText().toString();

                if(selectedImageURI !=null){
                    storageReference.putFile(selectedImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri=uri.toString();
                                    Users users=new Users(auth.getUid(),name,email,finalImageUri,status,statusOnOff);
                                    reference.set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if(task.isSuccessful()){
                                                Toast.makeText(SettingActivity.this, "Data Updated", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SettingActivity.this,HomeActivity.class));
                                            }
                                            else {
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                else{
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImageUri=uri.toString();
                            Users users=new Users(auth.getUid(),name,email,finalImageUri,status,statusOnOff);
                            reference.set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, "Data Updated", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SettingActivity.this,HomeActivity.class));
                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingActivity.this,HomeActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10){
            if(data != null){
                selectedImageURI=data.getData();
                userSetting_img.setImageURI(selectedImageURI);
            }
        }
    }
}