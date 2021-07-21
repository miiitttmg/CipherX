package com.example.cipherxchatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cipherxchatapp.Activitys.ChatActivity;
import com.example.cipherxchatapp.Activitys.HomeActivity;
import com.example.cipherxchatapp.Activitys.LoginActivity;
import com.example.cipherxchatapp.Models.Users;
import com.example.cipherxchatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context homeActivity;
    ArrayList<Users> usersArrayList;
    boolean isChat;
    public UserAdapter(HomeActivity homeActivity, ArrayList<Users> usersArrayList,boolean isChat) {
        this.homeActivity=homeActivity;
        this.usersArrayList=usersArrayList;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(homeActivity).inflate(R.layout.item_user_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users=usersArrayList.get(position);


        holder.user_name.setText(users.getName());
        holder.user_status.setText(users.getStatus());
        Picasso.get().load(users.getImageURi()).into(holder.user_img);

        if(isChat){
            if(users.getStatusOnOff().equals("online")){
                holder.img_online.setVisibility(View.VISIBLE);
                holder.img_offline.setVisibility(View.GONE);
            }
            else {
                holder.img_online.setVisibility(View.GONE);
                holder.img_offline.setVisibility(View.VISIBLE);
            }
        }else{
            holder.img_online.setVisibility(View.GONE);
            holder.img_offline.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(homeActivity, ChatActivity.class);
                intent.putExtra("name",users.getName());
                intent.putExtra("receiverimage",users.getImageURi());
                intent.putExtra("uid",users.getUid());
                homeActivity.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView user_img;
        TextView user_name,user_status;
        CircleImageView img_online,img_offline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_img=itemView.findViewById(R.id.user_image);
            user_name=itemView.findViewById(R.id.user_name);
            user_status=itemView.findViewById(R.id.user_status);
            img_online=itemView.findViewById(R.id.img_online);
            img_offline=itemView.findViewById(R.id.img_offline);

        }
    }
}
