package com.uoscybercaddy.dabajo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class MemberinfoinitActivity extends AppCompatActivity {
    EditText editTextNickName, nameEditText,phoneEditText,dateEditText;
    RadioGroup sexRadiGroup, tutortutyRadiGroup;
    Button infoSubmitButton;
    String sex, tutortuty;
    private FirebaseAuth mAuth;
    private static final String TAG = "MemberinfoinitActivity";
    private static final int RC_SIGN_IN = 100;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info_init);
        mAuth = FirebaseAuth.getInstance();
        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("회원 정보 업데이트");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        editTextNickName = (EditText)findViewById(R.id.editTextNickName);
        nameEditText = (EditText)findViewById(R.id.nameEditText);
        phoneEditText = (EditText)findViewById(R.id.phoneEditText);
        dateEditText = (EditText)findViewById(R.id.dateEditText);
        sexRadiGroup = (RadioGroup)findViewById(R.id.sexRadiGroup);
        tutortutyRadiGroup = (RadioGroup)findViewById(R.id.tutortutyRadiGroup);
        infoSubmitButton = (Button)findViewById(R.id.infoSubmitButton);
        infoSubmitButton.setOnClickListener(onClickListener);

        sexRadiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_man:
                        sex = "남자";
                    case R.id.rb_woman:
                        sex = "여자";
                }
            }
        });
        tutortutyRadiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_tuty:
                        tutortuty = "튜티";
                    case R.id.rb_tutor:
                        tutortuty = "튜터";
                }
            }
        });


        //findViewById(R.id.gotoSignUpTV).setOnClickListener(onClickListener);
        progressDialog = new ProgressDialog(this);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
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
    }*/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){

        } else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
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
                case R.id.infoSubmitButton:
                    profileUpdate();
                    finish();
                    break;

            }
        }
    };


    private void profileUpdate() {
        String nickName = editTextNickName.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();

        if( name.length()>0 && nickName.length()>0 && phone.length()>0 && date.length()>0){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            MemberInfo memberInfo = new MemberInfo(nickName, name, phone, date, sex,  tutortuty);

            if(user!=null){
                db.collection("users").document(user.getUid()).set(memberInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                startToast("회원정보 등록 성공");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                                startToast("회원정보 등록 실패");
                            }
                        });

            }

/*
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            if(user != null){
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                    startToast("회원 정보 등록 완료.");

                                }
                            }
                        });
            }
*/
        } else{
            startToast("회원 정보를 입력해주세요...");
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
    private void startActivityShortcut(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    } // startactivity 한번에 사용하기
    private void startSignupActivity() {
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
}