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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.AdapterEvalList;
import com.uoscybercaddy.dabajo.models.ModelEvalList;

import java.util.ArrayList;

public class ReviewTutorActivity extends AppCompatActivity {
    private static final String TAG = "ProfileTutor";
    ActionBar actionBar;
    FirebaseAuth firebaseAuth;
    ImageView avatarIv;
    TextView nickNameTv, fieldTv, descriptionTv, ratingTv, raterCntTv;
    RatingBar ratingBar;
    FirebaseFirestore db;
    RecyclerView recyclerView;
    private ArrayList<ModelEvalList> tmp;
    String tutorUid; // 튜터 uid

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_tutor);
        actionBar = getSupportActionBar();
        actionBar.hide();

        avatarIv = (ImageView) findViewById(R.id.avatarIv);
        nickNameTv = (TextView) findViewById(R.id.nickNameTv);
        fieldTv = (TextView) findViewById(R.id.fieldTv);
        ratingTv = (TextView) findViewById(R.id.ratingTv);
        raterCntTv = (TextView) findViewById(R.id.raterCnt);
        descriptionTv = (TextView) findViewById(R.id.descriptionTv);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setIsIndicator(true);

        // 튜터, 튜티 구분용
        Intent intent = getIntent();
        tutorUid = getIntent().getExtras().getString("tutorUid");

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // 튜터 프로필 화면상에 출력
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef_tutor = db.collection("users").document(tutorUid/*여기 tUid가 들어가야함*/);
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
//                        fieldTv.setText(document.getData().get("field").toString());
                        descriptionTv.setText(document.getData().get("introduction").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        // 튜터 평균평점 표시하는 ratingBar 출력
        DocumentReference docRef_eval = db.collection("eval").document(tutorUid/*여기 tUid가 들어가야함*/).collection("forAverage").document("total");
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
                        // 전체 평점
                        totalRating = document.getDouble("totalRating");
                        // 평가자 수
                        raterCnt = document.getLong("raterCnt");
                        // 평균평점 = 전체 평점 / 평가자 수
                        averageRating = totalRating / raterCnt;
                        averageRating = (Math.round(averageRating*100)/100.0);
                        ratingTv.setText(averageRating.toString() +" 점");
                        raterCntTv.setText("(" +Long.toString(raterCnt) + ")");
                        // Double형태를 float으로 구현현
                       ratingBar.setRating((float) (Math.round(averageRating*100)/100.0));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        // 후기들 recyclerView로 구현
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        tmp = new ArrayList<>();

        // arrylist에 데이터 넣는 법 샘플
        // tmp.add(new ModelEvalList(3.5, "수현", "너무 좋아용"));
        // tmp.add(new ModelEvalList(4.5, "수현", "너무 좋아용 다음에도 레슨 받고싶습니다!!"));
        // recyclerView.setAdapter(new AdapterEvalList(tmp));

        // rating_list에서 평가 데이터 꺼내오기
        db.collection("eval").document(tutorUid/*여기 tUid가 들어가야함*/).collection("rating_list")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Double ratingEach;
                    String nickName;
                    String body;
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Log.d(TAG, document.getId() + " => " + document.getData().get("body"));
                        // 튜터를 평가한 한 사람의 rating 점수 꺼내오기
                        ratingEach = Double.parseDouble(document.getData().get("rating").toString());
                        nickName = document.getData().get("nickname").toString();
                        body = document.getData().get("body").toString();
                        // rating_list에서 rating 점수, 평가한 튜티 닉네임, 평가내용 꺼내와서 arrayList에 추가
                        tmp.add(new ModelEvalList(ratingEach, nickName, body));
                    }
                    // 평가리스트 카드뷰 담은 recyclerView 실행
                    recyclerView.setAdapter(new AdapterEvalList(tmp));
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
