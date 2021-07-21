package com.example.cipherxchatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cipherxchatapp.Models.Messages;
import com.example.cipherxchatapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.cipherxchatapp.Activitys.ChatActivity.rImage;
import static com.example.cipherxchatapp.Activitys.ChatActivity.sImage;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList;

    int ITEM_SEND=1;
    int ITEM_RECEIVE=2;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_layout_item,parent,false);
            return new SenderViewHolder(view);
        }else{
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_layout_item,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages=messagesArrayList.get(position);
        long date = messages.getTimeStamp();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        SimpleDateFormat format1 = new SimpleDateFormat("h:mm a");
        String hour=format1.format(calendar.getTime());
        if(holder.getClass()==SenderViewHolder.class){
            SenderViewHolder viewHolder=(SenderViewHolder) holder;
            viewHolder.txtmessage.setText(messages.getMessage());
            Picasso.get().load(sImage).into(viewHolder.circleImageView);
            ((SenderViewHolder) holder).txt_time.setText(hour);

        }else {
            ReceiverViewHolder viewHolder=(ReceiverViewHolder) holder;
            viewHolder.txtmessage.setText(messages.getMessage());
            Picasso.get().load(rImage).into(viewHolder.circleImageView);
            ((ReceiverViewHolder) holder).txt_time.setText(hour);
        }

    }



    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages=messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())){
            return ITEM_SEND;
        }else{
            return ITEM_RECEIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        TextView txtmessage,txt_time;


        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.senderpic);
            txtmessage=itemView.findViewById(R.id.txtmsgsender);
            txt_time=itemView.findViewById(R.id.txt_time);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        TextView txtmessage,txt_time;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.receiverpic);
            txtmessage=itemView.findViewById(R.id.txtmsgreceiver);
            txt_time=itemView.findViewById(R.id.txt_time);

        }
    }
}
