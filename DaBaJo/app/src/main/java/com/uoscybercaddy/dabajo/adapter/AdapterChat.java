package com.uoscybercaddy.dabajo.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.ChatActivity;
import com.uoscybercaddy.dabajo.models.Modelchat;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<Modelchat> chatList;
    String imageUrl;
    FirebaseUser fUser;

    public AdapterChat(Context context, List<Modelchat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MyHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimeStamp();
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.setTimeInMillis(Long.parseLong(timeStamp));

        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();


        holder.timeTv.setText(dateTime);
        holder.messageTv.setText(message);


        try{
            Glide.with(context).load(imageUrl).centerCrop().override(500).into(holder.profileIv);
        }catch (Exception e){
            Glide.with(context).load(R.drawable.ic_profile_black).centerCrop().override(500).into(holder.profileIv);
        }

        if(position == chatList.size()-1){
            if(chatList.get(position).isSeen()){
                holder.isSeenTv.setText("Seen");
            }else{
                holder.isSeenTv.setText("Delivered");
            }
        }else{
            holder.isSeenTv.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView profileIv;
        TextView messageTv, timeTv, isSeenTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);

        }
    }
}
