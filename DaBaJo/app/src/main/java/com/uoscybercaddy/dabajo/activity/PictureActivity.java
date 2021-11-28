package com.uoscybercaddy.dabajo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.uoscybercaddy.dabajo.R;

public class PictureActivity extends AppCompatActivity {
    Intent intent;
    String message;
    ImageView pictureView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        intent = getIntent();
        message = intent.getStringExtra("message");
        pictureView = (ImageView) findViewById(R.id.pictureView);
        try{
            Glide.with(PictureActivity.this).load(message).override(1000).into(pictureView);
        }catch (Exception e){
            Glide.with(PictureActivity.this).load(R.drawable.ic_profile_black).override(1000).into(pictureView);
        }
    }
}