package com.example.cipherxchatapp.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cipherxchatapp.Adapters.UserAdapter;
import com.example.cipherxchatapp.Models.Users;
import com.example.cipherxchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    FirebaseFirestore db;
    ArrayList<Users> usersArrayList;
    ImageView img_logout;
    CircleImageView img_setting;
    TextView current_user_name;
    FirebaseUser firebaseUser;
    SwipeRefreshLayout refreshLayout;
    boolean doubleBackToExitPressedOnce=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        current_user_name=findViewById(R.id.current_user_name);

        img_logout=findViewById(R.id.img_logout);
        img_setting=findViewById(R.id.img_setting);
        mainUserRecyclerView=findViewById(R.id.mainUserRecyclerView);
        refreshLayout=findViewById(R.id.refresh_layout);
        usersArrayList=new ArrayList<>();

        CollectionReference reference=db.collection("Users");
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                refreshLayout.setRefreshing(false);
            }
        });

        if(auth.getCurrentUser() == null) {
            Intent intent=new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else {
            getData();
            mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter=new UserAdapter(HomeActivity.this,usersArrayList,true);
            mainUserRecyclerView.setAdapter(adapter);

            img_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(HomeActivity.this,SettingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });

            img_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog=new Dialog(HomeActivity.this,R.style.Dialoge);
                    dialog.setContentView(R.layout.dialoge_layout);
                    dialog.show();
                    TextView noBtn,yesBtn;
                    noBtn=dialog.findViewById(R.id.noBtn_dialoge);
                    yesBtn=dialog.findViewById(R.id.yesBtn_dialoge);
                    yesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            FirebaseAuth.getInstance().signOut();
                            Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });

                    noBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                }
            });

        }

    }

    private void getData(){
        CollectionReference reference=db.collection("Users");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    usersArrayList.clear();
                    for (DocumentSnapshot snapshot:task.getResult()){
                        Users users=new Users(snapshot.getString("uid"),snapshot.getString("name"),snapshot.getString("email"),
                                snapshot.getString("imageURi"),snapshot.getString("status"),snapshot.getString("statusOnOff"));
                        assert firebaseUser != null;
                        if(!users.getUid().equals(firebaseUser.getUid())){
                            usersArrayList.add(users);
                        }
                        else if(users.getUid().equals(firebaseUser.getUid())){
                            String user_name=snapshot.getString("name");
                            String user_img=snapshot.getString("imageURi");
                            Picasso.get().load(user_img).into(img_setting);
                            current_user_name.setText(user_name);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this, "Oops something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void statusOnOff(String statusOnOff){
        DocumentReference reference=FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("statusOnOff",statusOnOff);
        reference.update(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusOnOff("online");
        getData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        statusOnOff("offline");
        getData();
    }

    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce){
            super.onBackPressed();
            return;
        }
        Toast.makeText(this, "Please press back again to exit", Toast.LENGTH_SHORT).show();
        doubleBackToExitPressedOnce=true;
    }

    @Override
    public void onRefresh() {
        getData();
        refreshLayout.setRefreshing(false);
    }

}

