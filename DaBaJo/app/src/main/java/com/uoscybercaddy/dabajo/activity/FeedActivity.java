package com.uoscybercaddy.dabajo.activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.PostAdapter;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView feedRecyclerView;
    private PostAdapter mAdapter;
    private ArrayList<WriteInfo> mDatas;
    ActionBar actionBar;
    ImageButton goBackSport;
    ImageButton writeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        actionBar = getSupportActionBar();
        actionBar.hide();
        goBackSport = findViewById(R.id.goBackButton);
        findViewById(R.id.writePostButton).setOnClickListener(onClickListener);

        Intent intent = getIntent();

        if(intent.hasExtra("튜터")) {
            goBackSport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FeedActivity.this, CategorySportActivity.class);
                    intent.putExtra("튜터", 1);
                    startActivity(intent);
                }
            });
        }
        else {
            goBackSport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FeedActivity.this, CategorySportActivity.class);
                    intent.putExtra("튜티", 1);
                    startActivity(intent);
                }
            });
        }
    }

    // 피드 화면에서 최대로 글 몇줄 출력할 것인지
    @Override
    protected void onStart() {
        super.onStart();

        db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mDatas = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                mDatas.add(
                                        document.toObject(WriteInfo.class)
                                );

                            } // DATE 순으로 정렬 필요
                            // 댓글 postactivity
                            feedRecyclerView = findViewById(R.id.feedRecyclerView);

                            mAdapter = new PostAdapter(FeedActivity.this, mDatas);
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
            switch (v.getId()) {
                case R.id.writePostButton:
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
        intent.putExtra("튜티", 1);
        startActivity(intent);
    }
}
