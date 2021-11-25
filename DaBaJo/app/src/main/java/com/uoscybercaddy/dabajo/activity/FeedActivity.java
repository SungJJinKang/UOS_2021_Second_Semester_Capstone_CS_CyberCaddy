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
import com.google.firestore.v1.Write;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.PostAdapter;
import com.uoscybercaddy.dabajo.view.Comment;
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

    // 특정 유저가 쓴 글, 댓글 보는 방법
    //
    // Intent intent = ~~activity~~~~;
    // intent.putExtra("특정유저", targetUID); <- Intent로 특정유저랑 원하는 유저의 UID를 입력해주세요
    // startActivity(intent);
    //
    ///////////////////////////////

    // FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    // firebaseAuth.getUid() <- 현재 클라이언트 유저의 UID
    //

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
        else if(intent.hasExtra("튜티")){
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

    private ArrayList<WriteInfo> GetAllPost(@NonNull Task<QuerySnapshot> task)
    {
        ArrayList<WriteInfo> posts = new ArrayList<>();

        for (QueryDocumentSnapshot document : task.getResult()) {
            Log.d(TAG, document.getId() + " => " + document.getData());

            WriteInfo writeInfo = document.toObject(WriteInfo.class);
            writeInfo.FirebaseWriteInfoID = document.getId();
            posts.add(writeInfo);

        } // DATE 순으로 정렬 필요

        return posts;
    }

    enum PostFilter
    {
        PostsWrittenByTargetUser,
        CommentsWrittenByTargetUser,
        PostsAndCommnetsWrittenByTargetUser
    }


    private ArrayList<WriteInfo> GetAllPostWrittenByTargetUser
            (
                    @NonNull Task<QuerySnapshot> task,
                    String targetUsetUID,
                    PostFilter postFilter
            )
    {
        assert(targetUsetUID.isEmpty() == false);

        ArrayList<WriteInfo> posts = new ArrayList<>();

        for (QueryDocumentSnapshot document : task.getResult())
        {
            WriteInfo writeInfo = document.toObject(WriteInfo.class);
            writeInfo.FirebaseWriteInfoID = document.getId();

            boolean isIncluded = false;
            if
            (
                writeInfo.FirebaseWriteInfoID == targetUsetUID &&
                (
                (postFilter == PostFilter.PostsWrittenByTargetUser) ||
                (postFilter == PostFilter.PostsAndCommnetsWrittenByTargetUser)
                )
            )
            {
                isIncluded = true;
            }
            else if
            (
                (postFilter == PostFilter.CommentsWrittenByTargetUser) ||
                (postFilter == PostFilter.PostsAndCommnetsWrittenByTargetUser)
            )
            {
                for(Comment comment : writeInfo.commentList)
                {
                    if(comment.getWriterUID() == targetUsetUID)
                    {
                        isIncluded = true;
                        break;
                    }
                }
            }

            if(isIncluded == true)
            {
                posts.add(writeInfo);
            }

        } // DATE 순으로 정렬 필요

        return posts;
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

                            if(getIntent().hasExtra("특정유저"))
                            {// 특정 유저가 쓴 글이나 특정 유저가 쓴 댓글의 글을 보기 위한 용도
                                String targetUserUID = getIntent().getStringExtra("특정유저");
                                mDatas = GetAllPostWrittenByTargetUser(task, targetUserUID, PostFilter.PostsAndCommnetsWrittenByTargetUser);
                            }
                            else
                            {// 일반적으로 사용
                                // 모든 유저의 글을 보기 위한 용도
                                mDatas = GetAllPost(task);
                            }
                            
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
