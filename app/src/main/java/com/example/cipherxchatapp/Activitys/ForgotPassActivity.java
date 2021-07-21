package com.example.cipherxchatapp.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cipherxchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {

    TextInputEditText forgotpass_email;
    TextView reset_btn;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        auth=FirebaseAuth.getInstance();

        forgotpass_email=findViewById(R.id.forgotpass_email);
        reset_btn=findViewById(R.id.reset_btn);

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=forgotpass_email.getText().toString();
                if(email.equals("")){
                    Toast.makeText(ForgotPassActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    forgotpass_email.setError("Please Enter Email");

                }else {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPassActivity.this, "please Check your email", Toast.LENGTH_SHORT).show();
                                Intent gmail = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                                startActivity(gmail);
                            }else {
                                Toast.makeText(ForgotPassActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ForgotPassActivity.this,LoginActivity.class));
    }
}