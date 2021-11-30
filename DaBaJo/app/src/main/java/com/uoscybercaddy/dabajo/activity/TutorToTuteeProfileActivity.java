package com.uoscybercaddy.dabajo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.uoscybercaddy.dabajo.R;

public class TutorToTuteeProfileActivity extends AppCompatActivity {
    ActionBar actionBar;
    ImageButton goLikedTutorPage;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_to_tutee_profile);
        actionBar = getSupportActionBar();
        actionBar.hide();

        goLikedTutorPage = (ImageButton) findViewById(R.id.likedTutorButton);
        goLikedTutorPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 구현 필요
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
