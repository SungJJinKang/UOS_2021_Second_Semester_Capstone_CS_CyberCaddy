package com.uoscybercaddy.dabajo.activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.PostAdapter;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity  {

    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView feedRecyclerView;
    private PostAdapter mAdapter;
    private ArrayList<WriteInfo> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        findViewById(R.id.writePostButton).setOnClickListener(onClickListener);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatas = new ArrayList<>();
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                mDatas.add(new WriteInfo(document.getData().get("title").toString(),
                                        document.getData().get("body").toString(), // body or contents
                                        document.getData().get("writer").toString(),
                                        new Date(document.getDate("createdAt").getTime())));

                            }
                            feedRecyclerView = findViewById(R.id.feedRecyclerView);

                            mAdapter = new PostAdapter(mDatas);
                            feedRecyclerView.setAdapter(mAdapter);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.writePostButton:
                    startToast("works");
                    startActivityShortcut(WritePostActivity.class);
                    break;

            }
        }
    };

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startActivityShortcut(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    } // startactivity 한번에 사용하기
}
