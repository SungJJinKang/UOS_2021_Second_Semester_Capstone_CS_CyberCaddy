package com.uoscybercaddy.dabajo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.uoscybercaddy.dabajo.R;

public class ReviewTutorActivity extends AppCompatActivity {
    ActionBar actionBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_tutor);
        actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
