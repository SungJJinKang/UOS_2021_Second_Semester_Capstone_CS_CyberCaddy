package com.uoscybercaddy.dabajo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.ChatActivity;
import com.uoscybercaddy.dabajo.activity.TuteeToTutorProfileActivity;
import com.uoscybercaddy.dabajo.models.ModelComment;
import com.uoscybercaddy.dabajo.models.ModelPost;
import com.uoscybercaddy.dabajo.models.ModelUsers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.MyHolder>{
    Context context;
    List<ModelComment> commentList;
    String mUID, pId, pCategory, pTutortuty;

    public AdapterComments(Context context, List<ModelComment> commentList, String mUID, String pId, String pCategory, String pTutortuty) {
        this.context = context;
        this.commentList = commentList;
        this.mUID = mUID;
        this.pId = pId;
        this.pCategory = pCategory;
        this.pTutortuty = pTutortuty;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String uid = commentList.get(position).getUid();
        String name = commentList.get(position).getuName();
        String email = commentList.get(position).getuEmail();
        String image = commentList.get(position).getuDp();
        String cid = commentList.get(position).getcId();
        String comment = commentList.get(position).getComment();
        String timeStamp = commentList.get(position).getTimeStamp();

        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        holder.nameTv.setText(name);
        holder.commentTv.setText(comment);
        holder.timeTv.setText(pTime);

        try{
            Glide.with(context).load(image).centerCrop().override(500).placeholder(R.drawable.ic_profile_black).into(holder.avatarIv);
        }
        catch(Exception e){
            Glide.with(context).load(R.drawable.ic_profile_black).centerCrop().override(500).into(holder.avatarIv);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUID.equals(uid)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    builder.setTitle("삭제");
                    builder.setMessage("정말로 삭제하시겠습니까?");
                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteComment(cid);
                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
                else{
                    // 댓글 누르면 채팅으로 넘어가게 수정
//                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
//                    builder.setTitle("채팅");
//                    builder.setMessage(name+"와 채팅을 하시겠습니까?");
//                    builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(context, "취소", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(context, ChatActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("hisUid", uid);
//                            context.startActivity(intent);
//                        }
//                    });

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    builder.setTitle("프로필");
                    builder.setMessage(name+"의 프로필을 보시겠습니까?");
                    builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "취소", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, TuteeToTutorProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("profileUid", uid);
                            context.startActivity(intent);
                        }
                    });
                    builder.create().show();


//                    Toast.makeText(context,"다른 사람의 댓글은 지울 수 없습니다.",Toast.LENGTH_SHORT).show();
//                    원래 있었던 코드
                }
            }
        });
    }

    private void deleteComment(String cid) {
        // Remove the 'capital' field from the document
        Map<String,Object> updates = new HashMap<>();
        updates.put("Comments."+cid, FieldValue.delete());
        updates.put("pCommenters."+mUID, FieldValue.increment(-1));
        updates.put("pComments", FieldValue.increment(-1));
        FirebaseFirestore.getInstance().collection("Posts")
                .document(pTutortuty).collection(pCategory).document(pId)
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("", "Error updating document", e);
                    }
                });

        FirebaseFirestore.getInstance().collection("Posts")
                .document(pTutortuty).collection(pCategory).document(pId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ModelPost modelPost = document.toObject(ModelPost.class);
                        HashMap<String, Integer> commenter = modelPost.getpCommenters();
                        if(commenter.get(mUID)<2){
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String commentsCount = "commentsCount." + pCategory;
                            FirebaseFirestore.getInstance().collection("users").document(mUID)
                                    .update(
                                            commentsCount, FieldValue.increment(-1)
                                    );
                            Map<String,Object> updates1 = new HashMap<>();
                            updates1.put("comments."+pTutortuty+"."+pCategory+"."+pId, FieldValue.delete());
                            db.collection("users").document(mUID).update(updates1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("", "DocumentSnapshot successfully updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("", "Error updating document", e);
                                        }
                                    });

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

        String commentsCount = "commentsCount." + pCategory;
        FirebaseFirestore.getInstance().collection("users").document(mUID)
                .update(
                        commentsCount, FieldValue.increment(-1)
                );
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView avatarIv;
        TextView nameTv, commentTv, timeTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            commentTv = itemView.findViewById(R.id.commentTv);
            timeTv = itemView.findViewById(R.id.timeTv);

        }
    }
}
