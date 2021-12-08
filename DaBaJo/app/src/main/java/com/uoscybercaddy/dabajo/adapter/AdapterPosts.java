package com.uoscybercaddy.dabajo.adapter;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.AddPostActivity;
import com.uoscybercaddy.dabajo.activity.PostDetailActivity;
import com.uoscybercaddy.dabajo.activity.PostFeedActivityUsers;
import com.uoscybercaddy.dabajo.activity.PostLikedByActivity;
import com.uoscybercaddy.dabajo.models.ModelPost;
import com.uoscybercaddy.dabajo.models.URLS;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {
    Context context;
    List<ModelPost> postList;
    String myUid;
    boolean isUsers;
    boolean mProcessLike = false;

    CollectionReference postsColl;

    public AdapterPosts(){}
    public AdapterPosts(Context context, List<ModelPost> postList, boolean isUsers) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.isUsers = isUsers;
        postsColl = FirebaseFirestore.getInstance().collection("Posts");
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
        String pCategory = postList.get(position).getpCategory();
        String pTutortuty = postList.get(position).getpTutortuty();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescr();
        String pTimeStamp = postList.get(position).getpTime();
        int arrayCount = postList.get(position).getArrayCount();
        List<URLS> pImage = postList.get(position).getpImage();
        Integer pLikes = postList.get(position).getpLikes();
        List<String> pLikers= postList.get(position).getpLikers();
        Integer pComments = postList.get(position).getpComments();

        if(pImage == null){
            holder.viewPager2.setVisibility(View.GONE);
        }
        else{

            holder.viewPager2.setVisibility(View.VISIBLE);
            sliderAdapterforFeed  = (new SliderAdapterforFeed(context, pImage, holder.viewPager2));
            holder.viewPager2.setAdapter(sliderAdapterforFeed);
            ImageView[] indicators;
            indicators = setupIndicators(arrayCount);
            for(int i = 0; i< arrayCount; i++) {
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

        holder.pLikesTv.setText(pLikes + " 관심");
        holder.pCommentsTv.setText(pComments + " 댓글");
        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescription);

        setLikes(holder, pId, pTutortuty, pCategory);

        try{
            Glide.with(context).load(uDp).centerCrop().override(500).into(holder.uPictureIv);
        }
        catch(Exception e){
            Glide.with(context).load(R.drawable.ic_profile_black).centerCrop().override(500).into(holder.uPictureIv);
        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "클릭 !!! ");
                showMoreOptions(holder.moreBtn, uid, myUid, pId, pImage, arrayCount, pCategory, pTutortuty);
            }
        });
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessLike = true;
                postsColl.document(pTutortuty).collection(pCategory).document(pId)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                ModelPost modelPost = document.toObject(ModelPost.class);
                                Integer likes = modelPost.getpLikes();
                                List<String> likers = modelPost.getpLikers();
                                DocumentReference docRef = document.getReference();
                                if(likers!=null && likers.contains(myUid)){
                                    docRef.update("pLikes", FieldValue.increment(-1));
                                    docRef.update("pLikers", FieldValue.arrayRemove(myUid));
                                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_orange_border, 0,0,0);
                                    holder.likeBtn.setText("관심");
                                    holder.pLikesTv.setText((likes-1) + " 관심");
                                    mProcessLike = false;
                                }else{
                                    docRef.update("pLikes", FieldValue.increment(1));
                                    docRef.update("pLikers", FieldValue.arrayUnion(myUid));
                                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red_filled, 0,0,0);
                                    holder.likeBtn.setText("관심있음");
                                    holder.pLikesTv.setText((likes+1) + " 관심");
                                    mProcessLike = false;

                                    addToHisNotifications(""+uid, pId, pCategory, pTutortuty, "관심 있어 합니다.");
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("pId", pId);
                intent.putExtra("pTutortuty",pTutortuty);
                intent.putExtra("pCategory",pCategory);
                context.startActivity(intent);
            }
        });
        holder.pTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("pId", pId);
                intent.putExtra("pTutortuty",pTutortuty);
                intent.putExtra("pCategory",pCategory);
                context.startActivity(intent);
            }
        });
        holder.pDescriptionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("pId", pId);
                intent.putExtra("pTutortuty",pTutortuty);
                intent.putExtra("pCategory",pCategory);
                context.startActivity(intent);
            }
        });
        holder.pLikesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostLikedByActivity.class);
                intent.putExtra("pId", pId);
                intent.putExtra("pTutortuty",pTutortuty);
                intent.putExtra("pCategory",pCategory);
                context.startActivity(intent);
            }
        });
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareBody = pTitle +"\n" + pDescription +"\n\n";
                if(arrayCount > 0){
                    shareBody += "콘텐츠"+"\n";
                    for(int i=0; i< arrayCount; i++){
                        shareBody += pImage.get(i).getUrls() + "\n";
                    }
                }
                Intent sIntent = new Intent(Intent.ACTION_SEND);
                sIntent.setType("text/plain");
                sIntent.putExtra(Intent.EXTRA_SUBJECT, "제목은 여기에");
                sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sIntent, "공유 하기"));

            }
        });
        if(!isUsers){
            holder.profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostFeedActivityUsers.class);
                    intent.putExtra("uid",uid);
                    context.startActivity(intent);
                }
            });
        }
    }
    private void addToHisNotifications(String hisUid, String pId, String pCategory, String pTutortuty,String notification){
        String timeStamp =  ""+System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("pCategory", pCategory);
        hashMap.put("pTutortuty", pTutortuty);
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", notification);
        hashMap.put("sUid", myUid);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(hisUid)
                .update("notifications."+timeStamp,hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void setLikes(MyHolder holder, String pId, String pTutortuty, String pCategory) {
        postsColl.document(pTutortuty).collection(pCategory).document(pId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        ModelPost modelPost = document.toObject(ModelPost.class);
                        List<String> likers = modelPost.getpLikers();
                        DocumentReference docRef = document.getReference();
                        if(likers!=null && likers.contains(myUid)){
                            holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red_filled, 0,0,0);
                            holder.likeBtn.setText("관심있음");
                        }else{
                            holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_orange_border, 0,0,0);
                            holder.likeBtn.setText("관심");
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, String pId, List<URLS> pImage,int arrayCount,String pCategory,String pTutortuty ) {
        PopupMenu popupMenu = new PopupMenu(context ,moreBtn, Gravity.END);
        
        if(uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "삭제");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "수정");
        }
        popupMenu.getMenu().add(Menu.NONE, 2, 0 , "자세히 보기");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if( id ==0 ){
                    //삭제
                    beginDelete(pId, arrayCount, pCategory, pTutortuty, uid);
                }
                else if( id == 1 ){
                    //수정
                    Intent intent = new Intent(context, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId",pId);
                    intent.putExtra("pCategory",pCategory);
                    intent.putExtra("pTutortuty",pTutortuty);
                    context.startActivity(intent);
                }
                else if( id==2){
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("pId", pId);
                    intent.putExtra("pTutortuty",pTutortuty);
                    intent.putExtra("pCategory",pCategory);
                    context.startActivity(intent);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(String pId, int arrayCount,String pCategory,String pTutortuty, String uid) {
        String categoriesCount = "categoriesCount."+pCategory;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid)
                .update(
                        categoriesCount, FieldValue.increment(-1)
                );
        Map<String,Object> updates = new HashMap<>();
        updates.put("comments."+pTutortuty+"."+pCategory+"."+pId, FieldValue.delete());
        db.collection("users").document(uid).update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updates.put(\"comments.\"+pTutortuty+\".\"+pCategory+\".\"+pId, FieldValue.delete());");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document"+"updates.put(\"comments.\"+pTutortuty+\".\"+pCategory+\".\"+pId, FieldValue.delete());", e);
                    }
                });
        if(arrayCount == 0){
            deleteWithoutImage(pId,pCategory, pTutortuty);
        }else{
            deleteWithImage(pId, pCategory, pTutortuty, arrayCount);
        }

    }

    private void deleteWithImage(String pId,String pCategory,String pTutortuty, int arrayCount) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");
        pd.show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        for(int i=0; i<arrayCount; i++){
            final int index = i;
            StorageReference desertRef = storageRef.child("Posts/"+"post_" + pId + "/"+ index);
            // Delete the file
            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    //데이터베이스 삭제 ㄱ
                    if(index == arrayCount -1){
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Posts").document(pTutortuty).collection(pCategory).document(pId)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                        Intent intent = new Intent("deletePost");
                                        //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "삭제 실패...", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    pd.dismiss();
                    Toast.makeText(context, "삭제 실패...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteWithoutImage(String pId,String pCategory,String pTutortuty) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");
        pd.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Posts").document(pTutortuty).collection(pCategory).document(pId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        Intent intent = new Intent("deletePost");
                        //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "삭제 실패...", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
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
        TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv, pCommentsTv;
        ImageButton moreBtn;
        Button likeBtn, commentBtn, shareBtn;
        ViewPager2 viewPager2;
        LinearLayout layoutIndicators;
        LinearLayout profileLayout;
        public MyHolder(@NonNull View itemView){
            super(itemView);
            layoutIndicators = itemView.findViewById(R.id.layoutIndicators);
            pCommentsTv = itemView.findViewById(R.id.pCommentsTv);
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
            profileLayout = itemView.findViewById(R.id.profileLayout);
        }
    }
}
