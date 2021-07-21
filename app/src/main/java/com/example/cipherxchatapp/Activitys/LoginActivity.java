package com.example.cipherxchatapp.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cipherxchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView txt_signup;
    TextInputEditText login_email,login_password;
    TextView singin_btn,forgotpass;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txt_signup =findViewById(R.id.txt_signup);
        singin_btn=findViewById(R.id.signin_btn);
        login_email=findViewById(R.id.login_email);
        login_password=findViewById(R.id.login_password);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        forgotpass=findViewById(R.id.forgotpass);

        auth=FirebaseAuth.getInstance();

        singin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String email=login_email.getText().toString();
                String password=login_password.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Fields cant be empty ",Toast.LENGTH_SHORT).show();
                }else if(!email.matches(emailPattern)){
                    progressDialog.dismiss();
                    login_email.setError("Enter Valid Email ");
                    Toast.makeText(LoginActivity.this,"Enter Valid Email ",Toast.LENGTH_SHORT).show();
                }else if(password.length()<6){
                    progressDialog.dismiss();
                    login_password.setError("Password should be more than 6 character long ");
                    Toast.makeText(LoginActivity.this,"Password should be more than 6 character long ",Toast.LENGTH_LONG).show();
                }
                else {
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this,"Error in login ",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            }
        });


        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPassActivity.class));
            }
        });
    }
}