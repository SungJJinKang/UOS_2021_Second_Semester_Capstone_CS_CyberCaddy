package com.uoscybercaddy.dabajo.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.AdapterComments;
import com.uoscybercaddy.dabajo.adapter.SliderAdapterforFeed;
import com.uoscybercaddy.dabajo.models.ModelComment;
import com.uoscybercaddy.dabajo.models.ModelPost;
import com.uoscybercaddy.dabajo.models.ModelUsers;
import com.uoscybercaddy.dabajo.models.URLS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class PostDetailActivity extends AppCompatActivity {
    ImageView uPictureIv;
    String mUID;
    boolean mProcessComment = false;
    boolean mProcessLike = false;
    String myEmail, myName, myDp;
    String pId, pCategory, pTutortuty, hisDp, hisName;
    Integer pLikes;
    SliderAdapterforFeed sliderAdapterforFeed;
    ViewPager2 viewPager2;
    TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv, subTitle, pCommentsTv;
    ImageButton moreBtn;
    Button likeBtn, shareBtn;
    LinearLayout profileLayout;
    LinearLayout layoutIndicators;
    Toolbar toolbar;
    EditText commentEt;
    ImageButton sendBtn;
    ImageView cAvatarIv;
    FirebaseAuth firebaseAuth;
    ProgressDialog pd;
    boolean commented;
    Integer pComments;
    String timeStamp;
    String hisUid;
    Integer arrayCount;
    RecyclerView recyclerView;
    List<ModelComment> commentList;
    AdapterComments adapterComments;
    List<URLS> pImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        pId = intent.getStringExtra("pId");
        pTutortuty = intent.getStringExtra("pTutortuty");
        pCategory = intent.getStringExtra("pCategory");
        subTitle = findViewById(R.id.subTitle);
        firebaseAuth = FirebaseAuth.getInstance();
        pCommentsTv = findViewById(R.id.pCommentsTv);
        layoutIndicators = findViewById(R.id.layoutIndicators);
        uPictureIv = findViewById(R.id.uPictureIv);
        uNameTv = findViewById(R.id.uNameTv);
        pTimeTv = findViewById(R.id.pTimeTv);
        pTitleTv = findViewById(R.id.pTitleTv);
        pDescriptionTv = findViewById(R.id.pDescriptionTv);
        pLikesTv = findViewById(R.id.pLikesTv);
        moreBtn = findViewById(R.id.moreBtn);
        likeBtn = findViewById(R.id.likeBtn);
        shareBtn = findViewById(R.id.shareBtn);
        viewPager2 = findViewById(R.id.viewPagerImageSlider);
        profileLayout = findViewById(R.id.profileLayout);
        commentEt = findViewById(R.id.commentEt);
        sendBtn = findViewById(R.id.sendBtn);
        cAvatarIv = findViewById(R.id.cAvatarIv);
        recyclerView = findViewById(R.id.recyclerView);
        checkUserStatus();
        loadMyInfo();
        loadPostInfo();
        setLikes();
        subTitle.setText("로그인한 계정 : "+myEmail);
        loadComments();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions();
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pTitle = pTitleTv.getText().toString().trim();
                String pDescription = pDescriptionTv.getText().toString().trim();
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
                startActivity(Intent.createChooser(sIntent, "공유 하기"));

            }
        });
    }

    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        commentList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Posts")
                .document(pTutortuty).collection(pCategory).document(pId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());
                            commentList.clear();
                            ModelPost modelPost = snapshot.toObject(ModelPost.class);
                            Integer pComments = modelPost.getpComments();
                            pCommentsTv.setText((pComments)+" 댓글");
                            HashMap<String, HashMap<String, Object>> Comments = modelPost.getComments();
                            if(Comments != null){
                                SortedSet<String> keys = new TreeSet<>(Comments.keySet());
                                for (String key : keys) {
                                    HashMap<String, Object> value = Comments.get(key);
                                    ModelComment modelComment = new ModelComment(value);
                                    commentList.add(modelComment);
                                    // do something
                                }
                                adapterComments = new AdapterComments(getApplicationContext(), commentList, mUID, pId, pCategory, pTutortuty);
                                recyclerView.setAdapter(adapterComments);
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });



    }

    private void showMoreOptions() {
        PopupMenu popupMenu = new PopupMenu(PostDetailActivity.this ,moreBtn, Gravity.END);
        if(hisUid == null){
            Toast.makeText(PostDetailActivity.this,"삭제된 게시판입니다.",Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent("deletePost");
            LocalBroadcastManager.getInstance(PostDetailActivity.this).sendBroadcast(intent1);
            onBackPressed();
        }
        if(hisUid.equals(mUID)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "삭제");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "수정");
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if( id ==0 ){
                    //삭제
                    beginDelete(pId, arrayCount, pCategory, pTutortuty, hisUid);
                }
                else if( id == 1 ){
                    //수정
                    Intent intent = new Intent(PostDetailActivity.this, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId",pId);
                    intent.putExtra("pCategory",pCategory);
                    intent.putExtra("pTutortuty",pTutortuty);
                    startActivity(intent);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(String pId, Integer arrayCount, String pCategory, String pTutortuty, String hisUid) {
        String categoriesCount = "categoriesCount."+pCategory;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(hisUid)
                .update(
                        categoriesCount, FieldValue.increment(-1)
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, ".update(categoriesCount, FieldValue.increment(-1)\n)");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document"+".update(categoriesCount, FieldValue.increment(-1)\n)", e);
                    }
                });
        Map<String,Object> updates = new HashMap<>();
        updates.put("comments."+pTutortuty+"."+pCategory+"."+pId, FieldValue.delete());
        db.collection("users").document(hisUid).update(updates)
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
        ProgressDialog pd = new ProgressDialog(PostDetailActivity.this);
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
                                        Toast.makeText(PostDetailActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                        Intent intent = new Intent("deletePost");
                                        //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                                        LocalBroadcastManager.getInstance(PostDetailActivity.this).sendBroadcast(intent);
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PostDetailActivity.this, "삭제 실패...", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PostDetailActivity.this, "삭제 실패...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteWithoutImage(String pId,String pCategory,String pTutortuty) {
        ProgressDialog pd = new ProgressDialog(PostDetailActivity.this);
        pd.setMessage("Deleting...");
        pd.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Posts").document(pTutortuty).collection(pCategory).document(pId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PostDetailActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        Intent intent = new Intent("deletePost");
                        //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                        LocalBroadcastManager.getInstance(PostDetailActivity.this).sendBroadcast(intent);
                        finish();
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostDetailActivity.this, "삭제 실패...", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void setLikes() {
        FirebaseFirestore.getInstance().collection("Posts").document(pTutortuty).collection(pCategory).document(pId)
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
                        if(likers!=null && likers.contains(mUID)){
                            likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red_filled, 0,0,0);
                            likeBtn.setText("관심있음");
                        }else{
                            likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_orange_border, 0,0,0);
                            likeBtn.setText("관심");
                        }
                    } else {
                        Log.d(TAG, "No such document");
                        Intent intent1 = new Intent("deletePost");
                        LocalBroadcastManager.getInstance(PostDetailActivity.this).sendBroadcast(intent1);
                        onBackPressed();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void likePost() {
        mProcessLike = true;
        FirebaseFirestore.getInstance().collection("Posts")
        .document(pTutortuty).collection(pCategory).document(pId)
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
                        if(likers!=null && likers.contains(mUID)){
                            docRef.update("pLikes", FieldValue.increment(-1));
                            docRef.update("pLikers", FieldValue.arrayRemove(mUID));
                            likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_orange_border, 0,0,0);
                            likeBtn.setText("관심");
                            pLikesTv.setText((likes-1) + " 관심");
                            mProcessLike = false;
                        }else{
                            docRef.update("pLikes", FieldValue.increment(1));
                            docRef.update("pLikers", FieldValue.arrayUnion(mUID));
                            likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red_filled, 0,0,0);
                            likeBtn.setText("관심있음");
                            pLikesTv.setText((likes+1) + " 관심");
                            mProcessLike = false;
                        }
                    } else {
                        Log.d(TAG, "No such document");
                        Intent intent1 = new Intent("deletePost");
                        LocalBroadcastManager.getInstance(PostDetailActivity.this).sendBroadcast(intent1);
                        onBackPressed();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void postComment() {
        pd = new ProgressDialog(this);
        pd.setMessage("댓글 업로드 중...");
        String comment = commentEt.getText().toString().trim();
        if(TextUtils.isEmpty(comment)){
            Toast.makeText(this,"글이 비어있습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        timeStamp = String.valueOf(System.currentTimeMillis());
        Map<String, Object> city = new HashMap<>();
        city.put("cId", timeStamp+mUID);
        city.put("comment", comment);
        city.put("timeStamp", timeStamp);
        city.put("uid", mUID);
        city.put("uEmail", myEmail);
        city.put("uDp", myDp);
        city.put("uName", myName);
        FirebaseFirestore.getInstance().collection("Posts")
                .document(pTutortuty).collection(pCategory).document(pId)
                .update(
                    "Comments."+timeStamp+mUID, city
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this, "댓글 업로드 완료...", Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                        updateCommentCount();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this, "삭제된 게시판...", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent("deletePost");
                        LocalBroadcastManager.getInstance(PostDetailActivity.this).sendBroadcast(intent1);
                        onBackPressed();
                    }
                });


    }

    private void updateCommentCount() {
        mProcessComment = true;
        if(mProcessComment){

            FirebaseFirestore.getInstance().collection("Posts")
                    .document(pTutortuty).collection(pCategory).document(pId)
                    .update("pComments", FieldValue.increment(1))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mProcessComment = false;

                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            FirebaseFirestore.getInstance().collection("Posts")
                                    .document(pTutortuty).collection(pCategory).document(pId)
                                    .update("pCommenters."+mUID, FieldValue.increment(1));
                            DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(mUID);
                            docRef.update("comments."+pTutortuty+"."+pCategory+"."+pId, timeStamp+mUID);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                            Toast.makeText(PostDetailActivity.this, "실패...", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadMyInfo(){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(mUID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        ModelUsers modelUsers = document.toObject(ModelUsers.class);
                        myDp = modelUsers.getPhotoUrl();
                        if(myDp != null){
                            Glide.with(PostDetailActivity.this)
                                    .load(myDp)
                                    .centerCrop().override(500)
                                    .placeholder(R.drawable.ic_default_image_black)
                                    .into(cAvatarIv);
                        }else{
                            Glide.with(PostDetailActivity.this)
                                    .load(R.drawable.ic_default_image_black)
                                    .centerCrop().override(500)
                                    .into(cAvatarIv);
                        }
                        myName = modelUsers.getNickName();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                }
            }
        });
    }
    private void loadPostInfo() {
        FirebaseFirestore.getInstance().collection("Posts")
                .document(pTutortuty).collection(pCategory).document(pId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        ModelPost modelPost = document.toObject(ModelPost.class);
                        String pTitle = modelPost.getpTitle();
                        String pDescr = modelPost.getpDescr();
                        pLikes = modelPost.getpLikes();
                        String pTimeStamp = modelPost.getpTime();
                        pImage = modelPost.getpImage();
                        hisDp = modelPost.getuDp();
                        hisUid = modelPost.getUid();
                        pComments = modelPost.getpComments();
                        String uEmail = modelPost.getuEmail();
                        hisName = modelPost.getuName();
                        arrayCount = modelPost.getArrayCount();
                        HashMap<String, Integer> pCommenters = modelPost.getpCommenters();
                        if(pCommenters!=null && pCommenters.containsKey(mUID)){
                            commented = true;
                        }else{
                            commented = false;
                        }
                        pCommentsTv.setText(pComments+" 댓글");
                        Calendar calendar = Calendar.getInstance(Locale.KOREA);
                        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                        if(hisDp != null){
                            Glide.with(PostDetailActivity.this)
                                    .load(hisDp)
                                    .centerCrop().override(500)
                                    .placeholder(R.drawable.ic_default_image_black)
                                    .into(uPictureIv);
                        }
                        pTitleTv.setText(pTitle);
                        pDescriptionTv.setText(pDescr);
                        pLikesTv.setText(pLikes+" 관심");
                        pTimeTv.setText(pTime);

                        uNameTv.setText(hisName);

                        //콘텐츠 설정
                        if(pImage == null){
                            viewPager2.setVisibility(View.GONE);
                        }
                        else{
                            viewPager2.setVisibility(View.VISIBLE);
                            sliderAdapterforFeed  = (new SliderAdapterforFeed(PostDetailActivity.this, pImage, viewPager2));
                            viewPager2.setAdapter(sliderAdapterforFeed);
                            ImageView[] indicators;
                            indicators = setupIndicators(arrayCount);
                            for(int i = 0; i< arrayCount; i++) {
                                layoutIndicators.addView(indicators[i]);
                            }
                            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                @Override
                                public void onPageSelected(int position) {
                                    super.onPageSelected(position);
                                    int childCount = layoutIndicators.getChildCount();
                                    for (int i =0; i< childCount ; i++){
                                        ImageView imageView = (ImageView) layoutIndicators.getChildAt(i);
                                        if(i==position){
                                            imageView.setImageDrawable(
                                                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active)
                                            );
                                        }else{
                                            imageView.setImageDrawable(
                                                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive)
                                            );
                                        }
                                    }
                                }
                            });
                        }

                    } else {
                        Log.d(TAG, "No such document");
                            Toast.makeText(PostDetailActivity.this,"삭제된 게시물 입니다.", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent("deletePost");
                        LocalBroadcastManager.getInstance(PostDetailActivity.this).sendBroadcast(intent1);
                        onBackPressed();

                    }
                } else {
                    Toast.makeText(PostDetailActivity.this,"삭제된 게시물 입니다.", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent("deletePost");
                    LocalBroadcastManager.getInstance(PostDetailActivity.this).sendBroadcast(intent1);
                    onBackPressed();
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private ImageView[] setupIndicators(int arrayCount){
        ImageView[] indicators = new ImageView[arrayCount];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for(int i = 0 ; i< arrayCount; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
        }
        return indicators;
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            mUID = user.getUid();
            myEmail = user.getEmail();
            //shared preferences에 지금 로그인된 uid 저장
            SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();
        } else{
            startActivity(new Intent(PostDetailActivity.this, MainActivity.class));
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }
    //inflate option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.nav_chat).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    //handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}