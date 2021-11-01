package com.uoscybercaddy.dabajo.activity;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FeedVideoImgHelper
{
    public static Uri[] GetVideoUriFromWriteInfo(WriteInfo writeinfo)
    {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        ArrayList<String> videoDirectoryList = ImageDirectoryHelper.GetVideoDirecotry(writeinfo);

        writeinfo.videoUries = new Uri[writeinfo.videoCount];
        Uri[] videoUries = new Uri[writeinfo.videoCount];

        for(int i = 0 ; i < writeinfo.imageCount ; i++)
        {
            final StorageReference videoRef = storageRef.child(videoDirectoryList.get(i));
            Task<Uri> videoMetaDataFetchTask = videoRef.getDownloadUrl();

            try {
                com.google.android.gms.tasks.Tasks.await(videoMetaDataFetchTask);

                Uri videoUri = videoMetaDataFetchTask.getResult();

                writeinfo.videoUries[i] = videoUri;
                videoUries[i] = videoUri;
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return videoUries;
    }

    // 이거 사용 추천!!!!!!!!!
    public static void SetImageToImageView(ImageView imgView, WriteInfo writeinfo, int index)
    {
        Bitmap bitmap = GetImageFromWriteInfo(writeinfo, index);
        if(bitmap != null)
        {
            imgView.setImageBitmap(bitmap);
        }
    }

    public static Bitmap GetImageFromWriteInfo(WriteInfo writeinfo, int index)
    {
        if(index < 0 || index >= writeinfo.imageCount)
        {
            return null;
        }

        if(writeinfo.imageBitmap == null || writeinfo.imageBitmap[index] == null)
        {
            GetImageFromWriteInfo(writeinfo);
        }

        return writeinfo.imageBitmap[index];
    }

    public static Bitmap[] GetImageFromWriteInfo(WriteInfo writeinfo)
    {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference to "mountains.jpg"
        Bitmap[] bitmaps = new Bitmap[writeinfo.imageCount];
        writeinfo.imageBitmap = new Bitmap[writeinfo.videoCount];

        ArrayList<String> imgDirectoryList = ImageDirectoryHelper.GetImageDirecotry(writeinfo);

        for(int i = 0 ; i < writeinfo.imageCount ; i++)
        {
            final StorageReference imageRef = storageRef.child(imgDirectoryList.get(i));

            final int imageSize = writeinfo.imageSize[i];

            Task<byte[]> tasks = imageRef.getBytes(imageSize);

            try {
                com.google.android.gms.tasks.Tasks.await(tasks);

                Bitmap bitmap = BitmapFactory.decodeByteArray( tasks.getResult(), 0, imageSize ) ;

                bitmaps[i] = bitmap;
                writeinfo.imageBitmap[i] = bitmap;

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return bitmaps;
    }

    //application는 그냥 getApplication() 넣어주면 됩니다. targetExoPlayer랑 playerView는 호출하는 액티비티에서 만들어야함.
    //2021-11-11 아직 테스트 안됨
    // 이거 사용 추천!!!!!!!!!
    // 참고 코드 : https://github.com/Akshayrrao/Firebase.video.streaming.app/blob/master/app/src/main/java/com/example/netflix/ViewHolder.java
    public static void PlayVideo(Application application, SimpleExoPlayer targetExoPlayer, PlayerView playerView, WriteInfo writeinfo, int index)
    {
        if(index < 0 || index >= writeinfo.videoCount)
        {
            return;
        }

        if(writeinfo.videoUries == null || writeinfo.videoUries[index] == null)
        {
            GetVideoUriFromWriteInfo(writeinfo);
        }


        Uri targetVideoUri = writeinfo.videoUries[index];

        if(targetVideoUri != null && targetVideoUri.toString() != "")
        {
            try {
                targetExoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(application);
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(targetVideoUri,dataSourceFactory,extractorsFactory,null,null);
                playerView.setPlayer(targetExoPlayer);
                targetExoPlayer.prepare(mediaSource);
                targetExoPlayer.setPlayWhenReady(false);
            }catch (Exception e){
                Log.e("ViewHolder","exoplayer error"+e.toString());
            }
        }

    }
}
