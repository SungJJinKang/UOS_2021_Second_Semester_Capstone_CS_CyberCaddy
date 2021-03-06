package com.uoscybercaddy.dabajo.activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uoscybercaddy.dabajo.R;

import org.w3c.dom.Text;

public class TuteeToTutorProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileTutor";
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    ImageView avatarIv;
    TextView nickNameTv, descriptionTv, fieldTv;
    String profileUid, profileTutority, myName, myUid;
    ActionBar actionBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutee_to_tutor_profile);
        actionBar = getSupportActionBar();
        actionBar.hide();

        avatarIv = (ImageView) findViewById(R.id.avatarIv);
        nickNameTv = (TextView) findViewById(R.id.nickNameTv);
        descriptionTv = (TextView) findViewById(R.id.descriptionTv);
        fieldTv = (TextView) findViewById(R.id.fieldTv);

        findViewById(R.id.startChatButton).setOnClickListener(onClickListener);
        findViewById(R.id.reviewButton).setOnClickListener(onClickListener);
        findViewById(R.id.showUserPostButton).setOnClickListener(onClickListener);
        findViewById(R.id.evalButton).setOnClickListener(onClickListener);
        findViewById(R.id.showCommentPostButton).setOnClickListener(onClickListener);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();



        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        profileUid = intent.getStringExtra("profileUid");
//        myName = intent.getStringExtra("myName");
        DocumentReference docRef_tutor = db.collection("users").document(profileUid);
        docRef_tutor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if(document.getData().get("photoUrl") != null){
                            Glide.with(TuteeToTutorProfileActivity.this).load(document.getData().get("photoUrl")).centerCrop().override(500).into(avatarIv);
                        }
                        nickNameTv.setText(document.getData().get("nickName").toString());
                        descriptionTv.setText(document.getData().get("introduction").toString());
                        if(document.getData().get("tutortuty") != null) {
                            profileTutority = document.getData().get("tutortuty").toString();
                       }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        if(user!=null) {
            String clientUID = user.getUid();
            DocumentReference docRef = db.collection("users").document(clientUID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            myName = document.getData().get("nickName").toString();
                        } else {
                            Log.d(TAG, " ");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // ????????? ???????????? ?????? ??????(?????? ??????)
                case R.id.startChatButton:
                     Intent profileIntent = new Intent(TuteeToTutorProfileActivity.this, ChatActivity.class);
                     profileIntent.putExtra("hisUid", profileUid);
                     startActivity(profileIntent);
                    break;
                //
                case R.id.reviewButton:
                    Intent reviewIntent = new Intent(TuteeToTutorProfileActivity.this, ReviewTutorActivity.class);
                    reviewIntent.putExtra("tutorUid", profileUid);
                    reviewIntent.putExtra("myName", myName);
                    startActivity(reviewIntent);
                    break;

                case R.id.evalButton:
                    if(profileTutority.equals("??????")) {
                        Intent evalIntent = new Intent(TuteeToTutorProfileActivity.this, EvalTutorActivity.class);
                        evalIntent.putExtra("tUid", profileUid);
                        evalIntent.putExtra("myName", myName);
                        startActivity(evalIntent);
                    }
                    else {
                        Toast.makeText(TuteeToTutorProfileActivity.this, "????????? ????????? ??? ????????????", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.showUserPostButton:
                    Intent showPostIntent = new Intent(TuteeToTutorProfileActivity.this, PostFeedActivityUsers.class);
                    showPostIntent.putExtra("uid", profileUid);
                    startActivity(showPostIntent);
                    break;
                case R.id.showCommentPostButton:
                    Intent intent = new Intent(TuteeToTutorProfileActivity.this, PostFeedActivityUsers.class);
                    intent.putExtra("uid", profileUid);
                    intent.putExtra("OnlyCommnets", true);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void startActivityShortcut(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

}
