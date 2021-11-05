package com.uoscybercaddy.dabajo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.uoscybercaddy.dabajo.models.MemberInfo;

import java.util.ArrayList;

public class LikeActivity  extends AppCompatActivity
{

    private final String like_button_id = "tablerow_button";
    private ArrayList<Button> tablerow_buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.like_tutor);

        tablerow_buttons.add(findViewById(R.id.tablerow_button1));
        tablerow_buttons.add(findViewById(R.id.tablerow_button2));
        tablerow_buttons.add(findViewById(R.id.tablerow_button3));
        tablerow_buttons.add(findViewById(R.id.tablerow_button4));
        tablerow_buttons.add(findViewById(R.id.tablerow_button5));
        tablerow_buttons.add(findViewById(R.id.tablerow_button6));
        tablerow_buttons.add(findViewById(R.id.tablerow_button7));
        tablerow_buttons.add(findViewById(R.id.tablerow_button8));
        for(View button : tablerow_buttons)
        {
            button.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onStart() {
        super.onStart();


    }



    private void SetLikeButton(MemberInfo memberInfo, Button likeButton)
    {
        likeButton.setText(memberInfo.getName());
        likeButton.setBackground(memberInfo.GetProfilePhotoDrawable());

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move to profile
            }
        });
    }

    public void ShowLikeTutorList(ArrayList<MemberInfo> likedTutorList)
    {
        for(int i = 0 ; i < likedTutorList.size() && i < 8 ; i++)
        {
            tablerow_buttons.get(i).setVisibility(View.VISIBLE);
            SetLikeButton(likedTutorList.get(i), tablerow_buttons.get(i));
        }
    }


}