package com.uoscybercaddy.dabajo.activity;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.util.ArrayList;

public class FeedVideoImgHelper
{


    public interface OnLoadImageListener {
        void OnImageLoad(Bitmap bitmap);
    }


    // 이거 사용 추천!!!!!!!!!
    public static void SetImageToImageView(ImageView imgView, WriteInfo writeinfo, int index)
    {
        if(index < 0 || index >= writeinfo.imageCount)
        {
            Log.e("post image load", " index is out of bound");
            return;
        }

        ArrayList<String> imgDirectoryList = ImageDirectoryHelper.GetImageDirecotry(writeinfo);
        String targetImgFirebaseDirectory = imgDirectoryList.get(index);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child(targetImgFirebaseDirectory).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(imgView).load(uri).override(1000).into(imgView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("image load fail", exception.toString());
            }
        });
    }



    //application는 그냥 getApplication() 넣어주면 됩니다. targetExoPlayer랑 playerView는 호출하는 액티비티에서 만들어야함.
    //2021-11-11 아직 테스트 안됨
    // 이거 사용 추천!!!!!!!!!
    // 참고 코드 : https://github.com/Akshayrrao/Firebase.video.streaming.app/blob/master/app/src/main/java/com/example/netflix/ViewHolder.java
    public static void PlayVideo(Application application, PlayerView playerView, WriteInfo writeinfo, int index)
    {
        if(index < 0 || index >= writeinfo.videoCount)
        {
            Log.e("post video load", " index is out of bound");
            return;
        }


        ArrayList<String> videoDirectoryList = ImageDirectoryHelper.GetVideoDirecotry(writeinfo);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference videoRef = storageRef.child(videoDirectoryList.get(index));
        Task<Uri> videoMetaDataFetchTask = videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                final SimpleExoPlayer _targetExoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(application);
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(uri,dataSourceFactory,extractorsFactory,null,null);
                playerView.setPlayer(_targetExoPlayer);
                _targetExoPlayer.prepare(mediaSource);
                _targetExoPlayer.setPlayWhenReady(false);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("video load fail", exception.toString());
            }
        });


    }
}
