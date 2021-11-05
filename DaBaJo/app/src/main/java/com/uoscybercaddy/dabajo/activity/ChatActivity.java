package com.uoscybercaddy.dabajo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.AdapterChat;
import com.uoscybercaddy.dabajo.models.Modelchat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv, userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;
    FirebaseAuth firebaseAuth;
    private static final String TAG = "ChatActivity";
    ListenerRegistration registration;
    List<Modelchat> chatList;
    AdapterChat adapterChat;

    String hisUid;
    String myUid;
    String hisImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_recyclerView);
        profileIv = findViewById(R.id.profileIv);
        nameTv = findViewById(R.id.nameTv);
        userStatusTv = findViewById(R.id.userStatusTv);
        messageEt = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.e("hisUid : ",""+hisUid);
        DocumentReference hisdocRef = db.collection("users").document(hisUid);
        hisdocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    if(snapshot.getData().get("onlineStatus") == null){
                        userStatusTv.setText("");
                    }
                    else{
                        String onlineStatus = snapshot.getData().get("onlineStatus").toString();
                        if(onlineStatus.equals("online")){
                            userStatusTv.setText(onlineStatus);
                        }
                        else{
                            Calendar cal = Calendar.getInstance(Locale.KOREA);
                            cal.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
                            userStatusTv.setText("마지막에 본 시간 : "+dateTime);
                        }
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        hisdocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nickName = document.getData().get("nickName").toString();
                        String name = document.getData().get("name").toString();


                        nameTv.setText(nickName);

                        try{
                            Glide.with(ChatActivity.this).load(document.getData().get("photoUrl")).centerCrop().override(500).into(profileIv);
                        }catch (Exception e){
                            Glide.with(ChatActivity.this).load(R.drawable.ic_profile_black).centerCrop().override(500).into(profileIv);
                        }
                    } else {
                        Log.d(TAG, "정보를 등록해주세요");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEt.getText().toString().trim();
                if(TextUtils.isEmpty(message)){

                }else{
                    sendMessage(message);
                }
            }
        });
        readMessages();
        seenMessage();
    }

    private void seenMessage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("chats");
        registration = query
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.getString("sender").equals(hisUid) && doc.getString("receiver").equals(myUid) ){
                                if (doc.getBoolean("isSeen") != true) {
                                    doc.getReference()
                                            .update("isSeen", true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error updating document", e);
                                                }
                                            });
                                }
                            }
                        }
                    }
                });

    }

    private void readMessages() {
        chatList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference citiesRef = db.collection("chats");
        /*
        db.collection("chats")
                .whereIn("receiver", Arrays.asList(myUid, hisUid))
                .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        Log.d(TAG, doc.getId() + " => " + doc.getData());
                                        if (doc.getString("sender").equals(hisUid) || doc.getString("sender").equals(myUid)) {
                                            String message = doc.getString("message");
                                            String receiver = doc.getString("receiver");
                                            String sender = doc.getString("sender");
                                            String timestamp = doc.getString("timstamp");
                                            Boolean isSeen = doc.getBoolean("isSeen");
                                            Modelchat modelchat = new Modelchat(message,receiver,sender,timestamp,isSeen);
                                            chatList.add(modelchat);
                                        }
                                        adapterChat = new AdapterChat(ChatActivity.this, chatList, hisImage);
                                        adapterChat.notifyDataSetChanged();
                                        recyclerView.setAdapter(adapterChat);
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
*/
        db.collection("chats")
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        chatList.clear();
                        
                        for (QueryDocumentSnapshot doc : value) {
                            if ((doc.getString("sender").equals(hisUid) && doc.getString("receiver").equals(myUid) )||
                                    ( doc.getString("sender").equals(myUid) && doc.getString("receiver").equals(hisUid))) {

                                String message = doc.getString("message");
                                String receiver = doc.getString("receiver");
                                String sender = doc.getString("sender");
                                String timestamp = doc.getString("timestamp");
                                Boolean isSeen = doc.getBoolean("isSeen");
                                Modelchat modelchat = new Modelchat(message,receiver,sender,timestamp,isSeen);
                                chatList.add(modelchat);
                            }
                            adapterChat = new AdapterChat(ChatActivity.this, chatList, hisImage);
                           // adapterChat.notifyDataSetChanged();
                            recyclerView.setAdapter(adapterChat);
                        }
                    }
                });

    }

    private void sendMessage(String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        Log.e("timestamp : ",""+timestamp);
        Map<String, Object> data = new HashMap<>();
        data.put("sender", myUid);
        data.put("receiver", hisUid);
        data.put("message",message);
        data.put("timestamp", timestamp);
        data.put("isSeen",false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chats")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        messageEt.setText("");
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            myUid = user.getUid();
        } else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
    private void checkOnlineStatus(String status){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(myUid)
                .update("onlineStatus", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "온라인 ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.nav_chat).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    public void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
        registration.remove();
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}