package com.uoscybercaddy.dabajo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    EditText emailEditText,passwordEditText, passwordCheckEditText;
    //progressbar to display while registering user
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordCheckEditText = (EditText) findViewById(R.id.passwordCheckEditText);

        findViewById(R.id.signupButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoLoginButton).setOnClickListener(onClickListener);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("계정 생성");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("회원가입중...");
    }

    @Override
    public boolean onSupportNavigateUp() {
        //onBackPressed();
        finish();
        return super.onSupportNavigateUp();
    }
/*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
*/
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.signupButton:
                    signUp();
                    //startLoginActivity();
                    //startToast("회원가입을 성공했습니다");
                    break;
                case R.id.gotoLoginButton:
                    startLoginActivity();

                    break;
            }
        }
    };


    private void signUp() {

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordCheck = passwordCheckEditText.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("이메일 형식이 아닙니다");
            emailEditText.setFocusable(true);
        }
        else if(password.length()<6){
            passwordEditText.setError("최소 6자 이상입니다.");
            passwordEditText.setFocusable(true);
        }
        else if(!password.equals(passwordCheck)){
            passwordCheckEditText.setError("비밀번호가 일치하지 않습니다.");
            passwordCheckEditText.setFocusable(true);
        }
        else {
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) { //success
                                progressDialog.dismiss();
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("회원가입을 성공했습니다\n"+user.getEmail());

                                startLoginActivity();
                                finish();
                                //(user);
                            } else { // fail
                                // If sign in fails, display a message to the user.

                                progressDialog.dismiss();
                                startToast("회원가입 실패!");
                                try {
                                    throw task.getException();
                                }
                                catch(FirebaseAuthUserCollisionException e) {
                                    startToast("이미존재하는 email 입니다.");
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                                /*
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                    System.out.println(task);
                                }*/
                            }
                        }
                    });

        }

    }
    private void startToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    private void startLoginActivity() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}