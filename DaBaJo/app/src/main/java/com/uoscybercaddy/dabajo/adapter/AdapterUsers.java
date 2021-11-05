package com.uoscybercaddy.dabajo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uoscybercaddy.dabajo.models.ModelUsers;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.ChatActivity;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{
    Context context;
    List<ModelUsers> userList;

    public AdapterUsers(Context context, List<ModelUsers> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout(row_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_users,parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String hisUID = userList.get(position).getUid();
        String userImage = userList.get(position).getPhotoUrl();
        String userNickName = userList.get(position).getNickName();
        String introduction = userList.get(position).getIntroduction();
        holder.mNickNameTv.setText(userNickName);
        holder.mIntroductionTv.setText(introduction);
        try{
            Glide.with(context).load(userImage).centerCrop().override(500).into(holder.mAvatarIv);
        }catch(Exception e){

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid",hisUID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    private void startToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    //holder 클래스
    class MyHolder extends RecyclerView.ViewHolder{
        ImageView mAvatarIv;
        TextView mNickNameTv, mIntroductionTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            mNickNameTv = itemView.findViewById(R.id.nickNameTv);
            mIntroductionTv = itemView.findViewById(R.id.introductionTv);
        }
    }
}
