package com.uoscybercaddy.dabajo.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uoscybercaddy.dabajo.R;

public class MainActivity extends FragmentActivity {
    //firebase auth
    FirebaseAuth firebaseAuth;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user == null)
        {
            startActivityShortcut(LoginActivity.class);
        }else{
            //임시startActivityShortcut(CameraActivity.class);
            //startActivityShortcut(MemberinfoinitActivity.class);//임시
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null){
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            } else {
                                Log.d(TAG, "No such document");
                                startActivityShortcut(MemberinfoinitActivity.class);
                            }
                        }

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
            /*
            if (user != null) {
                for (UserInfo profile : user.getProviderData()) {

                    // Id of the provider (ex: google.com)
                    String providerId = profile.getProviderId();
                    if(providerId.equals("firebase")){
                        String uid = profile.getUid();

                        // Name, email address, and profile photo Url
                        String name = profile.getDisplayName();
                        Log.e("providerId "," providerId : "+providerId);
                        Log.e("uid "," uid : "+providerId);
                        Log.e("이름: ","이름 : "+name);
                        if(name==null || name.equals("test")){
                            //startActivityShortcut(MemberinfoinitActivity.class);
                        }

                    }
                    // UID specific to the provider

                    //String email = profile.getEmail();
                    //Uri photoUrl = profile.getPhotoUrl();
                }
            }
            */
        }

        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.writeButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoMainButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoFeedButton).setOnClickListener(onClickListener);

    }
    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed in stay here

        }else{
            //user not signed in, go to loginAcitivity
            startActivityShortcut(LoginActivity.class);
            finish();
        }
    }

    @Override
    protected void onStart() {
        //check on start of app
        checkUserStatus();
        super.onStart();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logoutButton:
                    FirebaseAuth.getInstance().signOut();
                    checkUserStatus();
                    //startActivityShortcut(LoginActivity.class);
                    break;
                case R.id.writeButton:
                    startPostActivity();
                    break;
                case R.id.gotoMainButton:
                    startDashboardActivity();
                    break;
                case R.id.gotoFeedButton:
                    startActivityShortcut(FeedActivity.class);


            }
        }
    };

    private void startSignUpActivity() {
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
    private void startDashboardActivity(){
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
    private void startPostActivity() {
        Intent intent = new Intent(this, WritePostActivity.class);
        startActivity(intent);
    }

    private void startActivityShortcut(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    } // startactivity 한번에 사용하기
}