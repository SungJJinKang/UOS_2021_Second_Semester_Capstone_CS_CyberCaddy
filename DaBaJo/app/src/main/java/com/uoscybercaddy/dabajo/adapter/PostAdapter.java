package com.uoscybercaddy.dabajo.adapter;

import static com.uoscybercaddy.dabajo.activity.UserProfileHelepr.GetUserProfile;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.PostActivity;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import com.bumptech.glide.Glide;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private ArrayList<WriteInfo> datas;
    private Activity activity;

    public PostAdapter(Activity activity, ArrayList<WriteInfo> datas) {
        this.activity = activity;
        this.datas = datas;
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView)LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false);
        final PostViewHolder postViewHolder = new PostViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra("writeInfo", datas.get(postViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });


        return postViewHolder;
//        public void onClick(View v) {
//            Intent intent = new Intent(this, PostActivity.class);
//
//        }
//        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        WriteInfo data = datas.get(position);
        holder.title.setText(data.getTitle());
        holder.contents.setText(data.getBody());
        holder.createdAt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(data.getCreatedAt()));

        holder.WriteNickname.setText(data.getWriter().toString()); // 프로필 데이터 없으면 UID로 WRITER 설정된다.
        holder.WriterUserImageImageView.setVisibility(View.INVISIBLE);


        GetUserProfile
                (
                        data.getWriter(),
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists())
                                    {

                                        String name = document.getData().get("name").toString();

                                        String nickName = document.getData().get("nickName").toString();
                                        holder.WriteNickname.setText(nickName);

                                        Object photo = document.getData().get("photoUrl");
                                        if(photo != null)
                                        {
                                            String photoUrl = photo.toString();
                                            Glide.with(activity).load(photoUrl).centerCrop().override(500).into(holder.WriterUserImageImageView);
                                            holder.WriterUserImageImageView.setVisibility(View.VISIBLE);
                                        }
                                        else
                                        {
                                            holder.WriterUserImageImageView.setVisibility(View.INVISIBLE);
                                        }


                                    }
                                }
                            }
                        }
                );



    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView contents;
        public TextView createdAt;

        public TextView WriteNickname;
        public ImageView WriterUserImageImageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_post_title);
            contents = itemView.findViewById(R.id.item_post_contents);
            createdAt = itemView.findViewById(R.id.item_post_createdAt);
            WriteNickname = itemView.findViewById(R.id.item_post_writerid);
            WriterUserImageImageView = itemView.findViewById(R.id.item_post_writer_img);
        }
    }
}
