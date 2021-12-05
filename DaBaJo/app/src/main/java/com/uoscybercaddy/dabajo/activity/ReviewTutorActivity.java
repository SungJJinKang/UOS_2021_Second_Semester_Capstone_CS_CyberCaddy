package com.uoscybercaddy.dabajo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uoscybercaddy.dabajo.R;

public class ReviewTutorActivity extends AppCompatActivity {
    private static final String TAG = "ProfileTutor";
    ActionBar actionBar;
    FirebaseAuth firebaseAuth;
    ImageView avatarIv;
    TextView nickNameTv, fieldTv, descriptionTv, ratingTv;
    RatingBar ratingBar;
    FirebaseFirestore db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_tutor);
        actionBar = getSupportActionBar();
        actionBar.hide();

        avatarIv = (ImageView) findViewById(R.id.avatarIv);
        nickNameTv = (TextView) findViewById(R.id.nickNameTv);
        fieldTv = (TextView) findViewById(R.id.fieldTv);
        ratingTv = (TextView) findViewById(R.id.ratingTv);
        descriptionTv = (TextView) findViewById(R.id.descriptionTv);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setIsIndicator(true);

        // 튜터, 튜티 구분용
        Intent intent = getIntent();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef_tutor = db.collection("users_tutor").document(user.getUid()/*여기 tUid가 들어가야함*/);
        docRef_tutor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if(document.getData().get("photoUrl") != null){
                            Glide.with(ReviewTutorActivity.this).load(document.getData().get("photoUrl")).centerCrop().override(500).into(avatarIv);
                        }
                        nickNameTv.setText(document.getData().get("nickName").toString());
                        fieldTv.setText(document.getData().get("field").toString());
                        descriptionTv.setText(document.getData().get("introduction").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        DocumentReference docRef_eval = db.collection("eval").document(user.getUid()/*여기 tUid가 들어가야함*/).collection("forAverage").document("total");
        docRef_eval.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Double totalRating;
                Double averageRating;
                long raterCnt;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        totalRating = document.getDouble("totalRating");
                        raterCnt = document.getLong("raterCnt");
                        averageRating = totalRating / raterCnt;
                        averageRating = (Math.round(averageRating*100)/100.0);
                        ratingTv.setText(averageRating.toString() +" 점");
                        ratingBar.setRating((float) (Math.round(averageRating*100)/100.0));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
