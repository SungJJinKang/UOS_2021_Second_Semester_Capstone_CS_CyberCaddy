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
import com.uoscybercaddy.dabajo.models.URLS;

import java.util.List;

public class SliderAdapterforFeed extends RecyclerView.Adapter<SliderAdapterforFeed.SliderViewHolder>{

    private List<URLS> pImage;
    private ViewPager2 viewPager2;
    Context context;

    public SliderAdapterforFeed(Context context, List<URLS> pImage, ViewPager2 viewPager2) {
        this.context = context;
        this.pImage = pImage;
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
        if((pImage.get(position).getImagevideo()).equals("image")){
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
            Uri message = Uri.parse(pImage.get(position).getUrls());
            holder.setImage(message);
            holder.viewPageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PictureActivity.class);
                    intent.putExtra("message",""+message);
                    context.startActivity(intent);
                }
            });
        }else if(((String)pImage.get(position).getImagevideo()).equals("video")){
            holder.imageView.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.VISIBLE);
            Uri videoUri = Uri.parse(pImage.get(position).getUrls());
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
        if(pImage != null)
            return pImage.size();
        else
            return 0;
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
            Glide.with(context)
                    .load(uri)
                    .fitCenter()
                    .placeholder(R.drawable.ic_default_image_black)
                    .into(imageView);
        }
    }
}
