package com.uoscybercaddy.dabajo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.models.EvalData;
import com.uoscybercaddy.dabajo.models.Total;

import java.util.HashMap;
import java.util.Map;

public class EvalTutorActivity extends AppCompatActivity {
    private static final String TAG = "EvalTutor";
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    ActionBar actionBar;
    RatingBar ratingBar;
    TextView ratingScore;
    EditText postBody;

    // 닉네임을 받와아야하는데 final 형태로하면 에러가 생겨서 부득이하게 밖으로 뺌, 이것때매 오류 생길 가능성 높음
    // IDE에서 시키는 대로 배열로 만들어지만 값이 안받아지는 것 같아서 이 방식 선택
    String nickName, tUid;

    ProgressDialog progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eval_tutor);
        actionBar = getSupportActionBar();
        actionBar.hide();

        mAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // tutorID intent로 받기
        tUid = getIntent().getExtras().getString("tUid");
        nickName = getIntent().getExtras().getString("myName");
        findViewById(R.id.goBackButton).setOnClickListener(onClickListener);
        findViewById(R.id.evalSubmitButton).setOnClickListener(onClickListener);

        ratingScore = (TextView) findViewById(R.id.ratingScore);
        ratingBar = findViewById(R.id.ratingBar);
        postBody = findViewById(R.id.postBody);
        postBody.setGravity(Gravity.CENTER);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromuser) {
                ratingScore.setText(String.valueOf(rating) + " 점");
            }
        });

        progressDialog = new ProgressDialog(this);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    private void evalUpdate() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String body = postBody.getText().toString().trim();
        final float rating = ratingBar.getRating();
        final String uUid = user.getUid();

//        DocumentReference docRef_tutor = db.collection("users").document(uUid);

        // 닉네임을 user의 uid를 통해 users에서 받아오는 작업

//        docRef_tutor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        // user 닉네임을 users에서 받아와서 nickName 변수에 할당
//                        nickName = document.getData().get("nickName").toString();
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });



        progressDialog.setMessage("리뷰 업데이트중");
        progressDialog.show();

        if (body.length() < 1) {
            postBody.setError("리뷰를 입력해주세요.");
            postBody.setFocusable(true);
            progressDialog.dismiss();
        }
        else {
            //////////////// 여기 바로 아래 코드 body, rating, uUid에서 nickName 추가함, 따라서 EvalData 모델도 nickName 추가
            EvalData evalData = new EvalData(body, rating, uUid, nickName);

            Map<String, Object> total = new HashMap<>();
            // 저장 방식 개선 필요
            // uid로 만들면 한사람 당 평가 한개씩밖에 안들어감
            // 숫자방식으로 처리 필요
            db.collection("eval").document(tUid/*여기 tUid가 들어가야함*/).collection("rating_list").document(uUid).set(evalData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            DocumentReference docRef = db.collection("eval").document(tUid/*여기 tUid가 들어가야함*/).collection("forAverage").document("total");
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                            Double totalRating = document.getDouble("totalRating");
                                            long raterCnt = document.getLong("raterCnt");
                                            Map<String, Object> total = new HashMap<>();
                                            total.put("raterCnt", raterCnt + 1);
                                            total.put("totalRating", totalRating + rating);

                                            docRef.set(total)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error writing document", e);
                                                        }
                                                    });
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });

                            startToast("리뷰 등록 성공");
                            progressDialog.dismiss();
                            Intent intent = new Intent(EvalTutorActivity.this, TuteeToTutorProfileActivity.class);
                            intent.putExtra("profileUid",tUid);
                            intent.putExtra("myName", nickName);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.goBackButton:
                    EvalTutorActivity.super.onBackPressed();
                    break;
                case R.id.evalSubmitButton:
                    evalUpdate();
                    break;
            }
        }
    };

    private void startToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void startActivityShortcut(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
