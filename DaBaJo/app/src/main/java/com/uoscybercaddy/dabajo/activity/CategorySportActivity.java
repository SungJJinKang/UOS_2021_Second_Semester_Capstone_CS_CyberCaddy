package com.uoscybercaddy.dabajo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import androidx.appcompat.app.ActionBar;


import androidx.appcompat.app.AppCompatActivity;

import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.fragment.CategoryFragment;

public class CategorySportActivity extends AppCompatActivity {
    ActionBar actionBar;
    ImageButton goBackCategory;
    Button soccerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);
        goBackCategory = findViewById(R.id.buttonBack);
        soccerButton = findViewById(R.id.buttonSoccer);
        actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();

        if(intent.hasExtra("튜터")) {
            goBackCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CategorySportActivity.this, DashboardActivityTutor.class);
                    intent.putExtra("카테고리로", 1);
                    startActivity(intent);
                }
            });
        }
        else {
            goBackCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CategorySportActivity.this, DashboardActivity.class);
                    intent.putExtra("카테고리로", 1);
                    startActivity(intent);
                }
            });
        }

        if(intent.hasExtra("튜터")) {
            soccerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CategorySportActivity.this, FeedActivity.class);
                    intent.putExtra("튜터", 1);
                    startActivity(intent);
                }
            });
        }
        else {
            soccerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CategorySportActivity.this, FeedActivity.class);
                    intent.putExtra("튜티", 1);
                    startActivity(intent);
                }
            });
        }
    }
}
