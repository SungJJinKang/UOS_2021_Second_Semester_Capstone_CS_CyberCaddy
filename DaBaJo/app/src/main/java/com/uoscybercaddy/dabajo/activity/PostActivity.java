package com.uoscybercaddy.dabajo.activity;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.CommentAdapter;
import com.uoscybercaddy.dabajo.adapter.PostAdapter;
import com.uoscybercaddy.dabajo.view.Comment;
import com.uoscybercaddy.dabajo.view.WriteInfo;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private LinearLayout parent;
    private RecyclerView postRecyclerView;
    private CommentAdapter commentAdapter;
    ArrayList<Comment> commentListi;

    FirebaseAuth firebaseAuth;
    private EditText commentTextImputUI;
    ActionBar actionBar;

    WriteInfo CurrentWriteInfo;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseAuth = FirebaseAuth.getInstance();

        CurrentWriteInfo = (WriteInfo) getIntent().getSerializableExtra("writeInfo");
        TextView titleTextView = findViewById(R.id.item_post_title);
        titleTextView.setText(CurrentWriteInfo.getTitle());

        TextView createdAtTextView = findViewById(R.id.item_post_createdAt);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(CurrentWriteInfo.getCreatedAt()));

        TextView contentTextView = findViewById(R.id.item_post_contents);
        contentTextView.setText(CurrentWriteInfo.getBody());

        TextView writeNickName = findViewById(R.id.item_post_writer_nickname);
        writeNickName.setText( getIntent().getStringExtra("writerNickName"));

        String writerProfileImgUrl = getIntent().getStringExtra("writerImage");
        if(writerProfileImgUrl != null)
        {
            ImageView writerProfileImageImageView = findViewById(R.id.item_post_profileImage);
            Glide.with(this).load(writerProfileImgUrl).centerCrop().override(500).into(writerProfileImageImageView);
        }

        commentTextImputUI = findViewById(R.id.writeCommentText);

        parent = findViewById(R.id.item_post_layout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        for(int i = 0; i< CurrentWriteInfo.imageCount; i++) {
            ImageView imageView2 = new ImageView(PostActivity.this);
            imageView2.setLayoutParams(layoutParams);

            FeedVideoImgHelper.SetImageToImageView(imageView2, CurrentWriteInfo, i);
            parent.addView(imageView2);
        }

        if(CurrentWriteInfo.videoCount > 0)
        {
            //test test!!!!!!!!!!!!!!!!!!!
            //test test!!!!!!!!!!!!!!!!!!!
            //test test!!!!!!!!!!!!!!!!!!!
            //FeedVideoImgHelper.PlayVideo(getApplication(), writeInfo, 0)
            PlayerView videoPlayerView = new PlayerView(PostActivity.this);
            videoPlayerView.setLayoutParams(layoutParams);
            FeedVideoImgHelper.PlayVideo(getApplication(), videoPlayerView, CurrentWriteInfo, 0);
            parent.addView(videoPlayerView);
        }


        ViewGroup.LayoutParams commentParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        findViewById(R.id.commentSendButton).setOnClickListener(onClickListener);

        RecyclerView recyclerView = findViewById(R.id.item_post_comment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.Adapter mAdapter = new CommentAdapter(this, CurrentWriteInfo.commentList);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    void AddComment(String addCommentString, String WriterUID )
    {
        if(CurrentWriteInfo.commentList == null)
        {
            CurrentWriteInfo.commentList = new ArrayList<Comment>();
        }

        Comment newComment = new Comment();
        newComment.setCommentText(addCommentString);
        newComment.setWriterUID(WriterUID);
        newComment.setCreatedAt(new Date());

        CurrentWriteInfo.commentList.add(newComment);

        updatePost(CurrentWriteInfo);
        UpdateCommnetList();
        finish();//????????? ??????
        overridePendingTransition(0, 0);//????????? ?????? ?????????
        Intent intent = getIntent(); //?????????
        startActivity(intent); //???????????? ??????
        overridePendingTransition(0, 0);//????????? ?????? ?????????
    }

    public void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void AddCommentFromCurrentClient(String inputString)
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user!=null){
            String clientUID = user.getUid();
            String nickName;
            DocumentReference docRef = db.collection("users").document(clientUID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String nickName = document.getData().get("nickName").toString();
                            AddComment(inputString, clientUID);
                        } else {
                            Log.d(TAG, " ");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

//            AddComment(inputString, clientUID);
        } else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.commentSendButton:


                    AddCommentFromCurrentClient(commentTextImputUI.getText().toString());
                    commentTextImputUI.getText().clear();
                    commentTextImputUI.clearFocus();

                    hideKeyboard();

                    startToast("Success to update comment");

                    break;
            }
        }
    };

    void UpdateCommnetList()
    {
        // use CurrentWriteInfo.commentList

    }

    // ??? ????????????????????? ?????? ??????????????? ?????? ??????
    private void updatePost(WriteInfo writeInfo){

        db.collection("posts")
                .document(writeInfo.FirebaseWriteInfoID)
                .set(writeInfo);

    }

    // ?????? ????????? ??? ???????????? ???????????? ?????? ?????????
    public void ShowPostOrCommentsWrittenByTargetUser(String targetUID)
    {
        Intent intent = new Intent(this, FeedActivity.class);
        intent.putExtra("????????????", targetUID);
        startActivity(intent);
    }


    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}