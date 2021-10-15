package com.uoscybercaddy.dabajo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    //firebase auth
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        /*
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startSignUpActivity();
        }*/

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
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
                    startSignUpActivity();
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