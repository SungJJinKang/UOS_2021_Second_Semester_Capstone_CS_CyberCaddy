package com.uoscybercaddy.dabajo.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.PictureActivity;
import com.uoscybercaddy.dabajo.activity.VideoActivity;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder>{

    private List<List> uris;
    private ViewPager2 viewPager2;
    Context context;

    public SliderAdapter(Context context, List<List> uris, ViewPager2 viewPager2) {
        this.context = context;
        this.uris = uris;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slide_item_container,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        if(((String)uris.get(position).get(0)).equals("image")){
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
            Uri message = (Uri)uris.get(position).get(1);
            holder.setImage(message);
            holder.viewPageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PictureActivity.class);
                    Log.e("이게 왜 안넘어가지지",""+message);
                    intent.putExtra("message",""+message);
                    context.startActivity(intent);
                }
            });
        }else if(((String)uris.get(position).get(0)).equals("video")){
            holder.imageView.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.VISIBLE);
            Uri videoUri = (Uri)uris.get(position).get(1);
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.videoView);
            holder.videoView.setMediaController(mediaController);
            holder.videoView.setVideoURI(videoUri);
            holder.videoView.requestFocus();
            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            holder.viewPageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("message",videoUri.toString());
                    context.startActivity(intent);
                }
            });
            holder.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch(what){
                        case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            holder.progressBar.setVisibility(View.VISIBLE);
                            return true;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            holder.progressBar.setVisibility(View.VISIBLE);
                            return true;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            holder.progressBar.setVisibility(View.GONE);
                            return true;
                    }
                    return false;
                }
            });
            holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return uris.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder{

        //private RoundedImageView imageView;
        private ImageView imageView;
        private FrameLayout viewPageLayout;
        VideoView videoView;
        ProgressBar progressBar;
        public SliderViewHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            viewPageLayout = itemView.findViewById(R.id.viewPageLayout);

            videoView = itemView.findViewById(R.id.videoView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
        void setImage(Uri uri){
            imageView.setImageURI(uri);
        }
    }
}
