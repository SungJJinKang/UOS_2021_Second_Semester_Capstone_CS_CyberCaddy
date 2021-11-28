package com.uoscybercaddy.dabajo.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.uoscybercaddy.dabajo.R;

public class EvalTutorActivity extends AppCompatActivity {
    ActionBar actionBar;
    RatingBar ratingBar;
    TextView ratingScore;
    EditText postBody;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eval_tutor);
        actionBar = getSupportActionBar();
        actionBar.hide();

        findViewById(R.id.goBackButton).setOnClickListener(onClickListener);
        findViewById(R.id.evalSubmitButton).setOnClickListener(onClickListener);

        ratingScore = (TextView) findViewById(R.id.ratingScore);
        ratingBar = findViewById(R.id.ratingBar);
        postBody = findViewById(R.id.postBody);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromuser) {
                ratingScore.setText(String.valueOf(rating) + " 점");
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.goBackButton:
                    startActivityShortcut(TuteeToTutorProfileActivity.class);
                    break;
                case R.id.evalSubmitButton:
                    int i = 0;
                    startActivityShortcut(TuteeToTutorProfileActivity.class);
                    break;
            }
        }
    };

    private void startActivityShortcut(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
