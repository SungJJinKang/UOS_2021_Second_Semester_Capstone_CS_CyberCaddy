package com.uoscybercaddy.dabajo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.view.WriteInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        findViewById(R.id.postButton).setOnClickListener(onClickListener);
        findViewById(R.id.imageButton).setOnClickListener(onClickListener);
        findViewById(R.id.videoButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.postButton:
                    startToast("works");
                    writePost();
                    break;
                case R.id.imageButton:

                    break;
                case R.id.videoButton:

                    break;
            }
        }
    };


    private void writePost() {
        final String title = ((EditText) findViewById(R.id.postTitle)).getText().toString();
        final String contents = ((EditText) findViewById(R.id.postBody)).getText().toString();

        if (title.length() > 0 && contents.length() > 0) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            WriteInfo writeInfo = new WriteInfo(title, contents, user.getUid());
            uploader(writeInfo);

        } else {
            startToast("내용을 입력해주세요.");
        }
    }

    private void uploader(WriteInfo writeInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").add(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startActivityShortcut(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    } // startactivity 한번에 사용하기

}