package com.uoscybercaddy.dabajo.activity;

import static com.uoscybercaddy.dabajo.activity.FeedVideoImgHelper.SetImageToImageView;

import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    private LinearLayout parent;

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

        String img_url = "https://firebasestorage.googleapis.com/v0/b/dabajo-test.appspot.com/o/posts%2FvHEVbyMFbCaDBK5Ook6u8dnaNi82%2FSun%20Oct%2031%2022%3A11%3A48%20GMT%2B09%3A00%202021%2F1.jpg?alt=media&token=6b402735-f5a8-4709-a51a-dce7ef037eaa";

        parent = findViewById(R.id.item_post_layout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(PostActivity.this);
        imageView.setLayoutParams(layoutParams);
        Glide.with(this).load(img_url).override(1000).into(imageView);
        parent.addView(imageView);
        final int imgCount = writeInfo.imageCount;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        ArrayList<String> imgDirectoryList = ImageDirectoryHelper.GetImageDirecotry(writeInfo);

        if(imgCount>0) {
            for(int i=0; i<imgCount; i++) {
                ImageView imageView2 = new ImageView(PostActivity.this);
                imageView2.setLayoutParams(layoutParams);
                String temp = imgDirectoryList.get(i);
                storageRef.child(temp).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
                Glide.with(this).load(temp).override(1000).into(imageView2);
//                Bitmap bitmap = GetImageFromWriteInfo(writeInfo, i);
//                imageView.setImageBitmap(bitmap);
                parent.addView(imageView2);

            }
        }

    }
}