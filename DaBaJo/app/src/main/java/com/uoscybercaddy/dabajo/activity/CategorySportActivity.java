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
import java.util.HashMap;

public class CategorySportActivity extends AppCompatActivity {
    ActionBar actionBar;
    ImageButton goBackCategory;
    ArrayList<Button> CategoryButtons;

    HashMap<String, ArrayList<String>> CategoryNameHashmap = new HashMap<>();

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

    private void UpdateCategoryButtons(String bigCategory)
    {
        if(CategoryNameHashmap.size() == 0)
        {
            InitCategoryMap();
        }

        ArrayList<String> smalLCateories = CategoryNameHashmap.get(bigCategory);
        assert(smalLCateories != null);

        for(int i = 0 ; i < 6 ; i++)
        {
            CategoryButtons.get(i).setText(smalLCateories.get(i));;
        }
    }

    private void InitCategoryMap()
    {
        {
            ArrayList<String> list= new ArrayList<String>();
            list.add("골프");
            list.add("테니스");
            list.add("축구");
            list.add("농구");
            list.add("야구");
            list.add("스노보딩");
            CategoryNameHashmap.put("스포츠", list);
        }

        {
            ArrayList<String> list= new ArrayList<String>();
            list.add("예체능1");
            list.add("예체능2");
            list.add("예체능3");
            list.add("예체능4");
            list.add("예체능5");
            list.add("예체능6");
            CategoryNameHashmap.put("예체능", list);
        }

        {
            ArrayList<String> list= new ArrayList<String>();
            list.add("헬스1");
            list.add("헬스2");
            list.add("헬스3");
            list.add("헬스4");
            list.add("헬스5");
            list.add("헬스6");
            CategoryNameHashmap.put("헬스", list);
        }

        {
            ArrayList<String> list= new ArrayList<String>();
            list.add("영어");
            list.add("일본어");
            list.add("독어");
            list.add("수학");
            list.add("공무원 공부");
            list.add("과학");
            CategoryNameHashmap.put("교육", list);
        }
    }
    private void startActivityShortcut(Class c, String category) {
        Intent intent = new Intent(this, c);
        intent.putExtra("category", category);
        startActivity(intent);
    }
    @Override
    public void onStart()
    {
        super.onStart();

        Intent intent = getIntent();

        UpdateCategoryButtons(intent.getStringExtra("BigCategory"));


        for(Button button : CategoryButtons)
        {
           // if(intent.hasExtra("튜터")) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityShortcut(PostFeedActivity.class, button.getText().toString());
                        /*
                        Intent intent = new Intent(CategorySportActivity.this, FeedActivity.class);
                        intent.putExtra("튜터", 1);
                        //UI에 쓰여있는 텍스트를 기반으로 카테고리 글 가져온다.
                        intent.putExtra(PostHelper.GetCategoryIntentExtraName(), button.getText());
                        intent.putExtra("BigCategory", getIntent().getStringExtra("BigCategory"));
                        startActivity(intent);
                         */
                    }
                });
                /*
            }
            else {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CategorySportActivity.this, FeedActivity.class);
                        intent.putExtra("튜티", 1);
                        //UI에 쓰여있는 텍스트를 기반으로 카테고리 글 가져온다.
                        intent.putExtra(PostHelper.GetCategoryIntentExtraName(), button.getText());
                        intent.putExtra("BigCategory", getIntent().getStringExtra("BigCategory"));
                        startActivity(intent);
                    }
                });
            }*/
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        InitCategoryMap();


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
                    CategorySportActivity.super.onBackPressed();
                }
            });
        }
        else {
            goBackCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CategorySportActivity.super.onBackPressed();
//                    Intent intent = new Intent(CategorySportActivity.this, DashboardActivity.class);
//                    intent.putExtra("카테고리로", 1);
//                    startActivity(intent);
                }
            });
        }




    }
}
