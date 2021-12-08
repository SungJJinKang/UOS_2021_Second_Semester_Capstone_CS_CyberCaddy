package com.uoscybercaddy.dabajo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.AdapterUsers;
import com.uoscybercaddy.dabajo.models.ModelPost;
import com.uoscybercaddy.dabajo.models.ModelUsers;

import java.util.ArrayList;
import java.util.List;

public class PostLikedByActivity extends AppCompatActivity {
    private static final String TAG = "PostLIkedByActivity";
    String pId, pCategory, pTutortuty;
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private List<ModelUsers> userList;
    private AdapterUsers adapterUsers;
    TextView noPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_liked_by);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        Intent intent = getIntent();
        pId = intent.getStringExtra("pId");
        pTutortuty = intent.getStringExtra("pTutortuty");
        pCategory = intent.getStringExtra("pCategory");
        userList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        noPost = (TextView)findViewById(R.id.noPost);
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Posts")
                .document(pTutortuty).collection(pCategory).document(pId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userList.clear();
                        ModelPost modelPost = document.toObject(ModelPost.class);
                        List<String> pLikers = modelPost.getpLikers();
                        if(pLikers.size()==0){
                            noPost.setVisibility(View.VISIBLE);
                        }else{
                            noPost.setVisibility(View.GONE);
                            getUsers(pLikers);
                        }
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void getUsers(List<String> pLikers) {
        for(int i = 0; i< pLikers.size(); i++){
            DocumentReference docRef = FirebaseFirestore.getInstance()
                    .collection("users").document(pLikers.get(i));
            int finalI = i;
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ModelUsers modelUsers = document.toObject(ModelUsers.class);
                            modelUsers.setUid(pLikers.get(finalI));
                            userList.add(modelUsers);
                            if( finalI == pLikers.size()-1){
                                adapterUsers = new AdapterUsers(PostLikedByActivity.this, userList);
                                recyclerView.setAdapter(adapterUsers);
                            }
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }
    }
}