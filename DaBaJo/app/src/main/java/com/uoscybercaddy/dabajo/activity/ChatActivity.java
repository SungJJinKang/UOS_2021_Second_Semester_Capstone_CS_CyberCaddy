package com.uoscybercaddy.dabajo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.gson.Gson;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.AdapterChat;
import com.uoscybercaddy.dabajo.models.Modelchat;
import com.uoscybercaddy.dabajo.notifications.Data;
import com.uoscybercaddy.dabajo.notifications.Sender;
import com.uoscybercaddy.dabajo.notifications.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private RequestQueue requestQueue;
    private boolean notify = false;
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

        requestQueue = Volley.newRequestQueue(getApplicationContext());

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
                    Object typingTo = snapshot.getData().get("typingTo");
                    Object onlineStatusObject = snapshot.getData().get("onlineStatus");

                    if (typingTo != null && typingTo.toString().equals(myUid)) {
                        userStatusTv.setText("입력중... ");
                    }
                    else if(onlineStatusObject == null){
                        userStatusTv.setText("");
                    }
                    else{
                        String onlineStatus = onlineStatusObject.toString();
                        if(onlineStatus.equals("online")){
                            userStatusTv.setText("온라인");
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
        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() == 0){
                    checkTypingStatus("noOne");
                }else{
                    checkTypingStatus(hisUid);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String message = messageEt.getText().toString().trim();
                if(TextUtils.isEmpty(message)){

                }else{
                    sendMessage(message);
                }
                messageEt.setText("");
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


        final DocumentReference docRef = db.collection("users").document(myUid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG+"여기는??", "Current data: " + snapshot.getData());
                    if(notify){
                        senNotification(hisUid, snapshot.getData().get("name").toString(), message);
                    }
                    notify = false;

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }

    private void senNotification(String hisUid, String name, String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("tokens").document(hisUid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG+"왓", "Current data: " + snapshot.getData());
                    String tokenString = snapshot.getData().get("token").toString();
                    Token token = new Token(tokenString);
                    Data data = new Data(myUid, name+": "+message, "새로운 메세지", hisUid, R.drawable.ic_chat_black);
                    Sender sender = new Sender(data, token.getToken());

                    //fcm json object request
                    try {
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("JSON_RESPONSE", "onResponse: "+response.toString());
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse: "+error.toString());
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAA9RV8rQM:APA91bHAUdhZt04zg97Y1I5yzAG3Pq5x7qEhCb8WS0saCHGDlaP8SgjixsE_PvRX8EmEIyEPV0mIdsoQsQj_U29F3yyN1cWveuCslPKW2-zr0lRHVNnP2ZQv_S5RopltAV6r-5xSO72R");
                                return headers;
                            }
                        };
                        //큐에 request 추가
                        requestQueue.add(jsonObjectRequest);


                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }


                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
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
    private void checkTypingStatus(String typing){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(myUid)
                .update("typingTo", typing)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "입력중 ");
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
        checkTypingStatus("noOne");
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