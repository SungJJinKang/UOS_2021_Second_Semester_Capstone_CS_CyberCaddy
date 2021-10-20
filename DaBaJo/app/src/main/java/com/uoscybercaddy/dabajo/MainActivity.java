package com.uoscybercaddy.dabajo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
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
        findViewById(R.id.profileButton).setOnClickListener(onClickListener);

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
                case R.id.profileButton:
                    startProfileActivity();
                    break;
            }
        }
    };

    private void startSignUpActivity() {
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
    private void startProfileActivity(){
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }
    private void startPostActivity() {
        Intent intent = new Intent(this,PostActivity.class);
        startActivity(intent);
    }

    private void startActivityShortcut(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    } // startactivity 한번에 사용하기
}