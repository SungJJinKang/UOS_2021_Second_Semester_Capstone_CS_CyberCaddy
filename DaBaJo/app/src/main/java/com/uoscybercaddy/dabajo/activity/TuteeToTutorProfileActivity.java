package com.uoscybercaddy.dabajo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uoscybercaddy.dabajo.R;

import org.w3c.dom.Text;

public class TuteeToTutorProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    ImageView avatarIv;
    TextView nickNameTv, descriptionTv, fieldTv;
    ImageView likeButton;
    int i =0;
    ActionBar actionBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutee_to_tutor_profile);
        actionBar = getSupportActionBar();
        actionBar.hide();
        firebaseAuth = FirebaseAuth.getInstance();

        avatarIv = (ImageView) findViewById(R.id.avatarIv);
        nickNameTv = (TextView) findViewById(R.id.nickNameTv);
        descriptionTv = (TextView) findViewById(R.id.descriptionTv);
        fieldTv = (TextView) findViewById(R.id.fieldTv);
        likeButton = (ImageView) findViewById(R.id.likeButton);

        findViewById(R.id.startChatButton).setOnClickListener(onClickListener);
        findViewById(R.id.reviewButton).setOnClickListener(onClickListener);
        findViewById(R.id.evalButton).setOnClickListener(onClickListener);

        likeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                i = 1 - i;
                if(i == 0) {
                    likeButton.setImageResource(R.drawable.ic_heart_filled);
                }
                else {
                    likeButton.setImageResource(R.drawable.ic_heart_border);
                }
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 튜티가 튜터에게 채팅 걸음(구현 필요)
                case R.id.startChatButton:

                    break;
                //
                case R.id.reviewButton:
                    startActivityShortcut(ReviewTutorActivity.class);
                    break;
                case R.id.evalButton:
                    startActivityShortcut(EvalTutorActivity.class);
            }
        }
    };

    private void startActivityShortcut(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

}
