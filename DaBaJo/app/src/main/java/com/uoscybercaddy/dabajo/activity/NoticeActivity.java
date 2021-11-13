package com.uoscybercaddy.dabajo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.uoscybercaddy.dabajo.R;

public class NoticeActivity extends AppCompatActivity {
    ActionBar actionBar;
    ImageButton goBackProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        goBackProfile = findViewById(R.id.goBackButton);
        actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        if(intent.hasExtra("튜터")) {
            goBackProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(NoticeActivity.this, DashboardActivityTutor.class);
                    intent.putExtra("fromProfileEdit", 1);
                    startActivity(intent);
                }
            });
        }
        else {
            goBackProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(NoticeActivity.this, DashboardActivity.class);
                    intent.putExtra("fromProfileEdit", 1);
                    startActivity(intent);
                }
            });
        }
    }
}

