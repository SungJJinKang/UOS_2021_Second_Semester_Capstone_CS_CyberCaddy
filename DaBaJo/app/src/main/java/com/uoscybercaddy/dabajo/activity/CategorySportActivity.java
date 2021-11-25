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

import java.util.ArrayList;

public class CategorySportActivity extends AppCompatActivity {
    ActionBar actionBar;
    ImageButton goBackCategory;
    ArrayList<Button> CategoryButtons;

    private void InitializeCategoryButton()
    {
        CategoryButtons = new ArrayList<>();

        CategoryButtons.add(findViewById(R.id.buttonSoccer));
        CategoryButtons.add(findViewById(R.id.buttonBaseball));
        CategoryButtons.add(findViewById(R.id.buttonBasketball));
        CategoryButtons.add(findViewById(R.id.buttonTennis));
        CategoryButtons.add(findViewById(R.id.buttonSnowboarding));
        CategoryButtons.add(findViewById(R.id.buttonGolf));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);
        goBackCategory = findViewById(R.id.buttonBack);

        actionBar = getSupportActionBar();
        actionBar.hide();

        InitializeCategoryButton();

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


        for(Button button : CategoryButtons)
        {
            if(intent.hasExtra("튜터")) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CategorySportActivity.this, FeedActivity.class);
                        intent.putExtra("튜터", 1);
                        //UI에 쓰여있는 텍스트를 기반으로 카테고리 글 가져온다.
                        intent.putExtra(PostHelper.GetCategoryIntentExtraName(), button.getText());
                        startActivity(intent);
                    }
                });
            }
            else {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CategorySportActivity.this, FeedActivity.class);
                        intent.putExtra("튜티", 1);
                        //UI에 쓰여있는 텍스트를 기반으로 카테고리 글 가져온다.
                        intent.putExtra(PostHelper.GetCategoryIntentExtraName(), button.getText());
                        startActivity(intent);
                    }
                });
            }
        }

    }
}
