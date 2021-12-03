package com.uoscybercaddy.dabajo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.AdapterPosts;
import com.uoscybercaddy.dabajo.models.ModelPost;
import com.uoscybercaddy.dabajo.models.URLS;
import com.uoscybercaddy.dabajo.models.UsersCategoriesCount;
import com.uoscybercaddy.dabajo.notifications.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PostFeedActivity extends AppCompatActivity {
    Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    ImageButton writePostButton;
    ActionBar actionBar;
    String mUID;
    RecyclerView recyclerView;
    List<ModelPost> postList;
    String category;
    AdapterPosts adapterPosts;
    Intent intent;
    TextView pCategoryEt;
    ImageButton goBackButton;
    String tutortuty;
    private Menu menu;
    private static final String TAG = "PostFeedActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_feed);
        firebaseAuth = FirebaseAuth.getInstance();

        pCategoryEt = (TextView) findViewById(R.id.CategorryLabel);
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        goBackButton = (ImageButton)findViewById(R.id.goBackButton);
        findViewById(R.id.writePostButton).setOnClickListener(onClickListener);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("deletePost"));
        tutortuty = "튜티";
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000ff")));
        checkUserStatus();
        intent = getIntent();

        if(intent.hasExtra("category")){
            category = intent.getStringExtra("category");
        }else{
            category = "축구";
        }
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        pCategoryEt.setText(category);
        recyclerView = (RecyclerView)findViewById(R.id.postsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);
        postList = new ArrayList<>();
        loadPosts();

        //디폴트
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        updateToken(token);
                    }
                });


    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.writePostButton:
                    Intent intent = new Intent(PostFeedActivity.this, AddPostActivity.class);
                    intent.putExtra("category", category);
                    startActivity(intent);
                    break;

            }
        }
    };
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("브로드케스트리시버작동", "브로드케스트리시버작동");
            loadPosts();
        }
    };
    private void realTimeListener(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        menu.getItem(1).setIcon(ContextCompat.getDrawable(PostFeedActivity.this, R.drawable.ic_refresh_black));
        db.collection("Posts").document(tutortuty).collection(category)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    //Log.d(TAG, "New city: " + dc.getDocument().getData());
                                        break;
                                case MODIFIED:
                                    //Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    menu.getItem(1).setIcon(ContextCompat.getDrawable(PostFeedActivity.this, R.drawable.ic_refresh_red));
                                    break;
                                case REMOVED:
                                    //Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    menu.getItem(1).setIcon(ContextCompat.getDrawable(PostFeedActivity.this, R.drawable.ic_refresh_red));
                                    break;
                            }
                        }

                    }
                });
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void loadPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        postList.clear();
        db.collection("Posts").document(tutortuty).collection(category)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                ModelPost modelPost = document.toObject(ModelPost.class);
                                //List<URLS> post = modelPost.getpImage();
                                //if(modelPost.getArrayCount() == 0 || post!= null && modelPost.getArrayCount() == post.size())
                                //{
                                    postList.add(modelPost);
                                //}
                            }
                            adapterPosts = new AdapterPosts(PostFeedActivity.this, postList, false);
                            recyclerView.setAdapter(adapterPosts);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    private void searchPosts(String searchQuery){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        postList.clear();
        db.collection("Posts").document(tutortuty).collection(category)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                ModelPost modelPost = document.toObject(ModelPost.class);
                                if(modelPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) || modelPost.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())){
                                   // List<URLS> post = modelPost.getpImage();
                                   // if(modelPost.getArrayCount() == 0 || post!= null && modelPost.getArrayCount() == post.size())
                                    //{
                                        postList.add(modelPost);
                                    //}
                                }
                            }
                            adapterPosts = new AdapterPosts(PostFeedActivity.this, postList, false);
                            recyclerView.setAdapter(adapterPosts);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("tokens").document(mUID);
        Token mToken = new Token(token);
        docRef.set(mToken);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_tuty:
                            tutortuty = "튜티";
                            loadPosts();
                            return true;
                        case R.id.nav_tutor:
                            tutortuty = "튜터";
                            loadPosts();
                            //nav_category fragment transaction
                            return true;
                    }
                    return false;
                }
            };

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            mUID = user.getUid();
            //shared preferences에 지금 로그인된 uid 저장
            SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();
        } else{
            startActivity(new Intent(PostFeedActivity.this, MainActivity.class));
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
        getMenuInflater().inflate(R.menu.menu_search_post, menu);
        this.menu = menu;
        //realTimeListener();
        MenuItem item = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    searchPosts(query);
                }
                else{
                    loadPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    searchPosts(newText);
                }
                else{
                    loadPosts();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //handle menu item clicks

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }else if(id == R.id.action_refresh){
            loadPosts();
            item.setIcon(ContextCompat.getDrawable(PostFeedActivity.this, R.drawable.ic_refresh_black));

        }
        return super.onOptionsItemSelected(item);
    }
}