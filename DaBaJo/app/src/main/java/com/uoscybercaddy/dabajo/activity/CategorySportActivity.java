package com.uoscybercaddy.dabajo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


import androidx.appcompat.app.ActionBar;


import androidx.appcompat.app.AppCompatActivity;

import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.fragment.CategoryFragment;

public class CategorySportActivity extends AppCompatActivity {
    ActionBar actionBar;
    ImageButton goBackCategory;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);
        goBackCategory = findViewById(R.id.buttonBack);
        actionBar = getSupportActionBar();
        actionBar.hide();


        goBackCategory.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategorySportActivity.this, DashboardActivity.class);
                intent.putExtra("카테고리로", 1);
                startActivity(intent);
            }
        });
    }
}
