package com.uoscybercaddy.dabajo.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    private LinearLayout parent;
    private LinearLayout comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        WriteInfo writeInfo = (WriteInfo) getIntent().getSerializableExtra("writeInfo");
        TextView titleTextView = findViewById(R.id.item_post_title);
        titleTextView.setText(writeInfo.getTitle());

        TextView createdAtTextView = findViewById(R.id.item_post_createdAt);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(writeInfo.getCreatedAt()));

        TextView contentTextView = findViewById(R.id.item_post_contents);
        contentTextView.setText(writeInfo.getBody());

        parent = findViewById(R.id.item_post_layout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        for(int i=0; i<writeInfo.imageCount; i++) {
            ImageView imageView2 = new ImageView(PostActivity.this);
            imageView2.setLayoutParams(layoutParams);

            FeedVideoImgHelper.SetImageToImageView(imageView2, writeInfo, i);
            parent.addView(imageView2);
        }

        if(writeInfo.videoCount > 0)
        {
            //test test!!!!!!!!!!!!!!!!!!!
            //test test!!!!!!!!!!!!!!!!!!!
            //test test!!!!!!!!!!!!!!!!!!!
            //FeedVideoImgHelper.PlayVideo(getApplication(), writeInfo, 0)
            PlayerView videoPlayerView = new PlayerView(PostActivity.this);
            videoPlayerView.setLayoutParams(layoutParams);
            FeedVideoImgHelper.PlayVideo(getApplication(), videoPlayerView, writeInfo, 0);
            parent.addView(videoPlayerView);
        }

        comment = findViewById(R.id.item_post_comment);
        ViewGroup.LayoutParams commentParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

}