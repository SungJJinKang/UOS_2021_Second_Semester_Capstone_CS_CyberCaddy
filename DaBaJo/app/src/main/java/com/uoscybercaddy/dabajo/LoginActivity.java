package com.uoscybercaddy.dabajo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    EditText emailEditText, passwordEditText;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("로그인");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoSignupButton).setOnClickListener(onClickListener);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("로그인 중...");
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        //finish();
        return super.onSupportNavigateUp();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

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
                case R.id.gotoSignupButton:
                    startSignupActivity();
                    break;
                case R.id.loginButton:
                    login();
                    break;
            }
        }
    };


    private void login() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("이메일 형식이 아닙니다");
            emailEditText.setFocusable(true);
        }
        else if(password.length()<6){
            passwordEditText.setError("최소 6자 이상입니다.");
            passwordEditText.setFocusable(true);
        }
        else {
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인 성공");
                                startMainActivity();
                                finish();
                            } else {
                                progressDialog.dismiss();
                                startToast("로그인 실패!");


                                Log.w(TAG, "LoginUser:failure", task.getException());
                                if (task.getException() != null) {
                                    System.out.println(task.getException().toString());
                                }

                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    startToast("존재하지 않는 계정입니다.");
                                } catch (FirebaseNetworkException e) {
                                    startToast("Firebase 네트워크 에러");
                                } catch(FirebaseAuthInvalidCredentialsException e){
                                    startToast("비밀번호가 일치하지 않습니다!");
                                }catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }
                    });
        }
    }
    private void startToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void startSignupActivity() {
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
}