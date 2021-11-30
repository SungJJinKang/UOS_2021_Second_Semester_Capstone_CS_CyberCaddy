package com.uoscybercaddy.dabajo.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.AddPostActivity;
import com.uoscybercaddy.dabajo.activity.MemberinfoinitActivity;
import com.uoscybercaddy.dabajo.models.ModelPost;
import com.uoscybercaddy.dabajo.models.URLS;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {
    Context context;
    List<ModelPost> postList;
    public AdapterPosts(){}

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        SliderAdapterforFeed sliderAdapterforFeed;
        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescr();
        String pTimeStamp = postList.get(position).getpTime();
        int arrayCount = postList.get(position).getArrayCount();
        List<URLS> pImage = postList.get(position).getpImage();
        if(pImage == null){
            holder.viewPager2.setVisibility(View.GONE);
        }
        else{
            sliderAdapterforFeed  = (new SliderAdapterforFeed(context, pImage, holder.viewPager2));
            holder.viewPager2.setAdapter(sliderAdapterforFeed);
            for(int i = 0; i< arrayCount; i++) {
                ImageView[] indicators = new ImageView[arrayCount];
                indicators = setupIndicators(arrayCount);
                holder.layoutIndicators.addView(indicators[i]);
            }
            holder.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    int childCount = holder.layoutIndicators.getChildCount();
                    for (int i =0; i< childCount ; i++){
                        ImageView imageView = (ImageView) holder.layoutIndicators.getChildAt(i);
                        if(i==position){
                            imageView.setImageDrawable(
                                    ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.indicator_active)
                            );
                        }else{
                            imageView.setImageDrawable(
                                    ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.indicator_inactive)
                            );
                        }
                    }
                }
            });
        }

        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        try{
            Glide.with(context).load(uDp).centerCrop().override(500).into(holder.uPictureIv);
        }
        catch(Exception e){
            Glide.with(context).load(R.drawable.ic_profile_black).centerCrop().override(500).into(holder.uPictureIv);
        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
            }
        });
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }
        });
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
            }
        });

        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescription);


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private ImageView[] setupIndicators(int arrayCount){
        ImageView[] indicators = new ImageView[arrayCount];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for(int i = 0 ; i< arrayCount; i++){
            indicators[i] = new ImageView(context.getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    context.getApplicationContext(),
                    R.drawable.indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
        }
        return indicators;
    }


    class MyHolder extends RecyclerView.ViewHolder{
        ImageView uPictureIv;
        TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv;
        ImageButton moreBtn;
        Button likeBtn, commentBtn, shareBtn;
        ViewPager2 viewPager2;
        LinearLayout layoutIndicators;
        public MyHolder(@NonNull View itemView){
            super(itemView);
            layoutIndicators = itemView.findViewById(R.id.layoutIndicators);
            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            viewPager2 = itemView.findViewById(R.id.viewPagerImageSlider);
        }
    }
}
