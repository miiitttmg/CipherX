package com.example.cipherxchatapp.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cipherxchatapp.Models.Users;
import com.example.cipherxchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    TextView txt_signin,btn_signup;
    CircleImageView profile_image;
    TextInputEditText reg_name,reg_email,reg_pass,reg_cPass;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    FirebaseDatabase database;
    FirebaseFirestore db;
    FirebaseStorage storage;
    String imageURI;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        txt_signin=findViewById(R.id.txt_signin);
        profile_image=findViewById(R.id.profile_image);
        reg_name=findViewById(R.id.reg_name);
        reg_email=findViewById(R.id.reg_email);
        reg_pass=findViewById(R.id.reg_pass);
        reg_cPass=findViewById(R.id.reg_cPass);
        btn_signup=findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                String name=reg_name.getText().toString();
                String email=reg_email.getText().toString();
                String password=reg_pass.getText().toString();
                String cPassword=reg_cPass.getText().toString();
                String status="Hey there it's "+name+" ";
                String statusOnOff="offline";

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword)){
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this,"Fields Cant Be Empty",Toast.LENGTH_SHORT).show();
                }else if(!email.matches(emailPattern)){
                    progressDialog.dismiss();
                    reg_email.setError("Enter Valid Email ");
                    Toast.makeText(RegistrationActivity.this,"Enter Valid Email ",Toast.LENGTH_SHORT).show();
                }else if(password.length()<6){
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this,"Password should be more than 6 character long",Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(cPassword)){
                    progressDialog.dismiss();
                    reg_cPass.setError("Password does not match");
                    Toast.makeText(RegistrationActivity.this,"Password does not match",Toast.LENGTH_SHORT).show();
                }else {
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                DocumentReference reference=db.collection("Users").document(auth.getUid());
                                //DatabaseReference reference=database.getReference().child("Users").child(auth.getUid());
                                StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());

                                if(imageUri !=null){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI= uri.toString();
                                                        Users users=new Users(auth.getUid(),name,email,imageURI,status,statusOnOff);
                                                        reference.set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    progressDialog.dismiss();
                                                                    Intent intent=new Intent(RegistrationActivity.this,HomeActivity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }else {
                                                                    Toast.makeText(RegistrationActivity.this, "Error in Creating a new User", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else {
                                    String status="Hey there it's "+name+" ";
                                    imageURI="https://firebasestorage.googleapis.com/v0/b/cipherx-chatapp.appspot.com/o/default_profile.png?alt=media&token=b0ec4914-6801-44f6-95be-92e636aee8c0";
                                    Users users=new Users(auth.getUid(),name,email,imageURI,status,statusOnOff);
                                    reference.set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Intent intent=new Intent(RegistrationActivity.this,HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(RegistrationActivity.this, "Error in Creating a new User", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(RegistrationActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });

        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10){
            if(data !=null){
                imageUri=data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}