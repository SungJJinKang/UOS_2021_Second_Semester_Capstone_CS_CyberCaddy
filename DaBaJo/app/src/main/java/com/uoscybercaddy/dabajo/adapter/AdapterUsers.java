package com.uoscybercaddy.dabajo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.uoscybercaddy.dabajo.activity.PostFeedActivityUsers;
import com.uoscybercaddy.dabajo.models.ModelUsers;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.ChatActivity;

import java.util.HashMap;
import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{
    Context context;
    List<ModelUsers> userList;

    FirebaseAuth firebaseAuth;
    String myUid;

    public AdapterUsers(Context context, List<ModelUsers> userList) {
        this.context = context;
        this.userList = userList;

        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
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
            Glide.with(context).load(R.drawable.ic_profile_black).centerCrop().override(500).into(holder.mAvatarIv);
        }
        holder.blockIv.setImageResource(R.drawable.ic_unblocked_orange);
        checkIsBlocked(hisUID, holder, holder.getAdapterPosition());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"프로필", "채팅"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            //프로필
                            Intent intent = new Intent(context, PostFeedActivityUsers.class);
                            intent.putExtra("uid",hisUID);
                            context.startActivity(intent);
                        }
                        if(which == 1){
                            //채팅
                            imBlockedORNot(hisUID);
                        }
                    }
                });
                builder.create().show();
            }
        });

        holder.blockIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userList.get(holder.getAdapterPosition()).isBlocked()){
                    unBlockUser(hisUID, holder, holder.getAdapterPosition());
                }
                else{
                    blockUser(hisUID, holder, holder.getAdapterPosition());
                }
            }
        });
    }
    private void imBlockedORNot(String hisUID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(hisUID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        HashMap<String, String> blockedUsers = (HashMap<String, String>) document.getData().get("blockedUsers");
                        if(blockedUsers != null && blockedUsers.containsKey(myUid)){
                            Toast.makeText(context,"메세지를 보낼 수 없는 상태입니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("hisUid",hisUID);
                            context.startActivity(intent);
                        }
                        Log.d("", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("", "No such document");
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });
    }

    private void checkIsBlocked(String hisUID, MyHolder holder, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(myUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("", "DocumentSnapshot data: " + document.getData());
                        HashMap<String, String> blockedUsers = (HashMap<String, String>) document.getData().get("blockedUsers");

                        if(blockedUsers != null && blockedUsers.containsKey(hisUID)){
                            holder.blockIv.setImageResource(R.drawable.ic_blocked_gray);
                            userList.get(position).setBlocked(true);
                        }
                    } else {
                        Log.d("", "No such document");
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });



    }

    private void blockUser(String hisUID, MyHolder holder, int position) {
        HashMap<String, Object> blockUsers = new HashMap<>();
        blockUsers.put("blockedUsers."+hisUID, hisUID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(myUid)
                .update(blockUsers)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"차단 완료", Toast.LENGTH_SHORT).show();
                        holder.blockIv.setImageResource(R.drawable.ic_blocked_gray);
                        userList.get(position).setBlocked(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"차단 실패...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unBlockUser(String hisUID, MyHolder holder, int position) {
        HashMap<String, Object> blockUsers = new HashMap<>();
        blockUsers.put("blockedUsers."+hisUID, FieldValue.delete());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(myUid)
                .update(blockUsers)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"차단 해제 완료", Toast.LENGTH_SHORT).show();
                        holder.blockIv.setImageResource(R.drawable.ic_unblocked_orange);
                        userList.get(position).setBlocked(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"차단 해제 실패...", Toast.LENGTH_SHORT).show();
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
        ImageView mAvatarIv, blockIv;
        TextView mNickNameTv, mIntroductionTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            blockIv = itemView.findViewById(R.id.blockIv);
            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            mNickNameTv = itemView.findViewById(R.id.nickNameTv);
            mIntroductionTv = itemView.findViewById(R.id.introductionTv);
        }
    }
}
