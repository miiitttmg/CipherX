package com.example.cipherxchatapp.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cipherxchatapp.Adapters.MessagesAdapter;
import com.example.cipherxchatapp.Models.Messages;
import com.example.cipherxchatapp.Models.Users;
import com.example.cipherxchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReceiverImage,ReceiverName,ReceiverUID, SenderUID;
    CircleImageView receiver_Pimage;
    TextView receiver_name;
    EditText edittextMessage;
    CardView sendBtn;
    RecyclerView messageAdapterRecycler;
    MessagesAdapter adapter;
    RelativeLayout chatActivityView;

    String AES="AES";
    String key;

    FirebaseFirestore db;
    FirebaseAuth auth;
    public static String sImage;
    public static String rImage;

    String senderRoom,receiverRoom;

    ArrayList<Messages> messagesArrayList;

    ListenerRegistration seenListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        chatActivityView=findViewById(R.id.chatActivityView);

        edittextMessage=findViewById(R.id.edittextMessage);
        sendBtn=findViewById(R.id.sendBtn);

        ReceiverName=getIntent().getStringExtra("name");
        ReceiverImage=getIntent().getStringExtra("receiverimage");
        ReceiverUID=getIntent().getStringExtra("uid");
        SenderUID=auth.getUid();
        messagesArrayList=new ArrayList<>();

        messageAdapterRecycler=findViewById(R.id.messageAdapter);
        messageAdapterRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messageAdapterRecycler.setLayoutManager(linearLayoutManager);
        adapter=new MessagesAdapter(ChatActivity.this,messagesArrayList);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                linearLayoutManager.smoothScrollToPosition(messageAdapterRecycler,null,adapter.getItemCount());
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                linearLayoutManager.smoothScrollToPosition(messageAdapterRecycler,null,adapter.getItemCount());
            }
        });
        messageAdapterRecycler.setAdapter(adapter);
        senderRoom=SenderUID+ReceiverUID;
        receiverRoom=ReceiverUID+SenderUID;

        receiver_Pimage=findViewById(R.id.receiver_Pimage);
        receiver_name=findViewById(R.id.receiver_name);

        Picasso.get().load(ReceiverImage).into(receiver_Pimage);
        receiver_name.setText(""+ReceiverName);
        key="CipherX";

        DocumentReference reference=db.collection("Users").document(auth.getUid());
        getChatMessages();
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                sImage=task.getResult().getString("imageURi");
                rImage=ReceiverImage;
            }
        });

       addChatMessages();
       seenMessage(ReceiverUID);
        
    }

    public void addChatMessages(){
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String message=edittextMessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Please Enter Some Message First", Toast.LENGTH_SHORT).show();
                    return;
                }
                edittextMessage.setText("");
                Date date=new Date();
                String out = null;
                try {
                    out=encrypt(message,key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Messages messages=new Messages(out,SenderUID,date.getTime());

                db.collection("Chats").document(senderRoom).collection("messages")
                        .add(messages).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        db.collection("Chats").document(receiverRoom).collection("messages")
                                .add(messages).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                            }
                        });
                    }
                });

            }
        });
    }

    private void seenMessage(final String userid){
        FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
        Query reference=db.collection("Chats").document(receiverRoom).collection("messages");
        seenListener=reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    for (DocumentChange doc : value.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            Messages messages = doc.getDocument().toObject(Messages.class);
                            if(ReceiverUID.equals(fuser.getUid()) && messages.getSenderId().equals(userid)){
                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("",true);
                                doc.getDocument().getReference().update(hashMap);
                            }
                        }
                    }
                }
            }
        });
    }

    public void getChatMessages(){
        Query chatReference=db.collection("Chats").document(senderRoom).
                collection("messages").orderBy("timeStamp" ,Query.Direction.ASCENDING);
        chatReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    for (DocumentChange doc : value.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            Messages messages = doc.getDocument().toObject(Messages.class);
                            String outmsg=messages.getMessage();
                            String msg = null;
                            try {
                                msg =decrypt(outmsg,key);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Messages messages1=new Messages(msg,messages.getSenderId(),messages.getTimeStamp());
                            messagesArrayList.add(messages1);
                            DocumentSnapshot lastVisible = value.getDocuments()
                                    .get(value.size() - 1);
                            Query next = db.collection("Chat").document(auth.getUid()).collection("messages")
                                    .orderBy("timeStamp").startAfter(lastVisible);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private String encrypt(String data,String password) throws Exception {
        SecretKeySpec key=generateKey(password);
        Cipher c=Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal=c.doFinal(data.getBytes());
        String encryptedValue= Base64.encodeToString(encVal,Base64.DEFAULT);
        return encryptedValue;
    }
    private String decrypt(String outString, String password) throws Exception{
        SecretKeySpec key=generateKey(password);
        Cipher c=Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decodeValue=Base64.decode(outString,Base64.DEFAULT);
        byte[] decValue=c.doFinal(decodeValue);
        String decryptedValue=new String(decValue);
        return decryptedValue;
    }
    private  SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest=MessageDigest.getInstance("SHA-256");
        byte[] bytes=password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key=digest.digest();
        SecretKeySpec secretKeySpec=new SecretKeySpec(key,"AES");
        return secretKeySpec;
    }

    private void statusOnOff(String statusOnOff){
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference reference=FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("statusOnOff",statusOnOff);
        reference.update(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusOnOff("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        seenListener.remove();
        statusOnOff("offline");
    }


}
