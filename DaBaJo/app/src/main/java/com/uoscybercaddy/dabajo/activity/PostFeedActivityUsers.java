package com.uoscybercaddy.dabajo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class PostFeedActivityUsers extends AppCompatActivity {
    Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    String mUID;
    String uid;
    int check;
    private MenuItem item;
    MenuItem refreshitem;
    TextView noPost;
    RecyclerView recyclerView;
    List<ModelPost> postList;
    HashMap<String, Long> usersCategoriesCounts;
    Spinner categoriesSpinner;
    AdapterPosts adapterPosts;
    Intent intent;
    ImageButton goBackButton;
    //String tutortuty;
    List<String> categories;
    String category;
    private static final String TAG = "PostFeedActivityUsers";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_feed_users);
        firebaseAuth = FirebaseAuth.getInstance();
        goBackButton = (ImageButton)findViewById(R.id.goBackButton);
        //tutortuty = "??????";
        categoriesSpinner = (Spinner) findViewById(R.id.categoriesSpinner);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        noPost = (TextView)findViewById(R.id.noPost);
        category = "";
        check = 0;
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("deletePost"));
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000ff")));
        checkUserStatus();
        intent = getIntent();
        uid = intent.getStringExtra("uid");
        Log.e("uid : ",""+uid);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostFeedActivityUsers.super.onBackPressed();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        //Object position = document.getData().get("tutortuty");
                        //if(position != null){
                        //    tutortuty = position.toString();
                        //}else{
                        //    tutortuty = "??????";
                        //}

                        String filter;
                        if(getIntent().hasExtra("OnlyCommnets") && getIntent().getBooleanExtra("OnlyCommnets", false) == true)
                        {
                            filter = "commentsCount";
                        }
                        else
                        {
                            filter = "categoriesCount";
                        }
                        usersCategoriesCounts = (HashMap<String, Long>) document.getData().get(filter);


                        if(usersCategoriesCounts != null){
                            noPost.setVisibility(View.GONE);
                            List<Map.Entry<String, Long>> list_entries = new ArrayList<Map.Entry<String, Long>>(usersCategoriesCounts.entrySet());

                            list_entries.removeIf(element -> (element.getValue() <= 0));

                            // ???????????? Comparator??? ???????????? ?????????????????? ??????
                            Collections.sort(list_entries, new Comparator<Map.Entry<String, Long>>() {
                                // compare??? ?????? ??????
                                public int compare(Map.Entry<String, Long> obj1, Map.Entry<String, Long> obj2) {
                                    // ?????? ?????? ??????
                                    return obj2.getValue().compareTo(obj1.getValue());
                                }
                            });
                            // ?????? ??????
                            List<String> catgs = new ArrayList<String>();
                            categories = new ArrayList<String>();
                            if(filter.equals("commentsCount")){
                                catgs.add("???????????? ?????? : (?????? ???)");
                            }else{
                                catgs.add("???????????? ?????? (????????? ???)");
                            }
                            for(Map.Entry<String, Long> entry : list_entries) {
                                categories.add(entry.getKey()+"");
                                catgs.add(entry.getKey() + " : " + entry.getValue()+ " ???");
                            }
                            loadPostsAll();
                            ArrayAdapter<String> adapter;
                            adapter = new ArrayAdapter<String>(PostFeedActivityUsers.this,
                                    android.R.layout.simple_spinner_item,catgs);
                            categoriesSpinner.setAdapter(adapter);
                        }else{
                            noPost.setVisibility(View.VISIBLE);
                            item.setVisible(false);
                            categoriesSpinner.setVisibility(View.INVISIBLE);
                            refreshitem.setVisible(false);
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(++check > 1){
                    if(position>0){
                        category = categories.get(position-1);
                        loadPosts(category);
                    }else{
                        category = "";
                        loadPostsAll();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(PostFeedActivityUsers.this,"???????????? ??????",Toast.LENGTH_SHORT).show();
            }
        });
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.postsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);
        postList = new ArrayList<>();


        //?????????
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
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("?????????????????????????????????", "?????????????????????????????????");
            if(category.equals("")){
                loadPostsAll();
            }else{
                loadPosts(category);
            }
        }
    };
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void FetchPost(final String categoryName, OnCompleteListener<List<Object>> onCompleteListener)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query tutorQuery = null;
        Query tuteeQuery = null;
        {
            CollectionReference colRef = db.collection("Posts").document("??????").collection(categoryName);
            boolean noOption = true;
            if(getIntent().hasExtra("OnlyCommnets") && getIntent().getBooleanExtra("OnlyCommnets", false) == true)
            {
                tutorQuery = colRef.whereGreaterThan("pCommenters." + uid, 0);
            }
            else
            {
                tutorQuery = colRef.whereEqualTo("uid", uid);
            }
        }
        {
            CollectionReference colRef = db.collection("Posts").document("??????").collection(categoryName);
            boolean noOption = true;
            if(getIntent().hasExtra("OnlyCommnets") && getIntent().getBooleanExtra("OnlyCommnets", false) == true)
            {
                tuteeQuery = colRef.whereGreaterThan("pCommenters." + uid, 0);
            }
            else
            {
                tuteeQuery = colRef.whereEqualTo("uid", uid);
            }
        }

        Task<QuerySnapshot> tutorTask = tutorQuery.get();
        Task<QuerySnapshot> tuteeTask = tuteeQuery.get();

        Tasks.whenAllSuccess(tutorTask, tuteeTask).addOnCompleteListener(onCompleteListener);

    }


    private void loadPostsAll(){
        postList.clear();
        Log.e("???????????? ????????? ", categories.size()+"");
        for(int i = 0; i< categories.size() ; i++){
            final int index = i;
            FetchPost(categories.get(i), new OnCompleteListener<List<Object>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Object>> task) {
                        if (task.isSuccessful()) {
                            List<Object> result = task.getResult();
                            for (Object documentObj : result)
                            {
                                QuerySnapshot queySnapshot = (QuerySnapshot)documentObj;
                                for(DocumentSnapshot document : queySnapshot.getDocuments()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    ModelPost modelPost = document.toObject(ModelPost.class);
                                    // List<URLS> post = modelPost.getpImage();
                                    // if(modelPost.getArrayCount() == 0 || post!= null && modelPost.getArrayCount() == post.size())
                                    //{
                                    postList.add(modelPost);
                                    // }
                                }
                            }
                            if(index == categories.size()-1){
                                Collections.sort(postList);
                                adapterPosts = new AdapterPosts(PostFeedActivityUsers.this, postList, true);
                                recyclerView.setAdapter(adapterPosts);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        }
    }
    private void searchPostsAll(String searchQuery){
        postList.clear();
        for(int i = 0; i< categories.size() ; i++){
            final int index = i;
            FetchPost(categories.get(index), new OnCompleteListener<List<Object>>() {
                @Override
                public void onComplete(@NonNull Task<List<Object>> task) {
                            if (task.isSuccessful()) {
                                List<Object> result = task.getResult();
                                for (Object documentObj : result)
                                {
                                    QuerySnapshot queySnapshot = (QuerySnapshot)documentObj;
                                    for(DocumentSnapshot document : queySnapshot.getDocuments()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        ModelPost modelPost = document.toObject(ModelPost.class);
                                        if (modelPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) || modelPost.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())) {
                                            // List<URLS> post = modelPost.getpImage();
                                            //if(modelPost.getArrayCount() == 0 || post!= null && modelPost.getArrayCount() == post.size())
                                            //{
                                            postList.add(modelPost);
                                            //}
                                        }
                                    }
                                }
                                if(index == categories.size()-1){
                                    Collections.sort(postList);
                                    adapterPosts = new AdapterPosts(PostFeedActivityUsers.this, postList, true);
                                    recyclerView.setAdapter(adapterPosts);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
    private void loadPosts(String category) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        postList.clear();
        FetchPost(category, new OnCompleteListener<List<Object>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Object>> task) {
                        if (task.isSuccessful()) {
                            List<Object> result = task.getResult();
                            for (Object documentObj : result)
                            {
                                QuerySnapshot queySnapshot = (QuerySnapshot)documentObj;
                                for(DocumentSnapshot document : queySnapshot.getDocuments()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    ModelPost modelPost = document.toObject(ModelPost.class);
                                    //List<URLS> post = modelPost.getpImage();
                                    //if(modelPost.getArrayCount() == 0 || post!= null && modelPost.getArrayCount() == post.size())
                                    // {
                                    postList.add(modelPost);
                                    // }
                                }
                            }
                            adapterPosts = new AdapterPosts(PostFeedActivityUsers.this, postList, true);
                            recyclerView.setAdapter(adapterPosts);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void searchPosts(String searchQuery, String category){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        postList.clear();
        FetchPost(category, new OnCompleteListener<List<Object>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Object>> task) {
                        if (task.isSuccessful()) {
                            List<Object> result = task.getResult();
                            for (Object documentObj : result)
                            {
                                QuerySnapshot queySnapshot = (QuerySnapshot)documentObj;
                                for(DocumentSnapshot document : queySnapshot.getDocuments()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    ModelPost modelPost = document.toObject(ModelPost.class);
                                    if (modelPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) || modelPost.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())) {
                                        //List<URLS> post = modelPost.getpImage();
                                        // if(modelPost.getArrayCount() == 0 || post!= null && modelPost.getArrayCount() == post.size())
                                        // {
                                        postList.add(modelPost);
                                        // }
                                    }
                                }
                            }
                            adapterPosts = new AdapterPosts(PostFeedActivityUsers.this, postList, true);
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

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            mUID = user.getUid();
            //shared preferences??? ?????? ???????????? uid ??????
            SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();
        } else{
            startActivity(new Intent(PostFeedActivityUsers.this, MainActivity.class));
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
        item = menu.findItem(R.id.action_search);
        refreshitem = menu.findItem(R.id.action_refresh);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    if(category.equals("")){
                        searchPostsAll(query);
                    }else{
                        searchPosts(query, category);
                    }
                }
                else{
                    if(category.equals("")){
                        loadPostsAll();
                    }else{
                        loadPosts(category);
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    if(category.equals("")){
                        searchPostsAll(newText);
                    }else{
                        searchPosts(newText, category);
                    }
                }
                else{
                    if(category.equals("")){
                        loadPostsAll();
                    }else{
                        loadPosts(category);
                    }
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //handle menu item clicks

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.item = item;
        int id = item.getItemId();
        if(id== R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }else if(id == R.id.action_refresh){
            if(category.equals("")){
                loadPostsAll();
            }else{
                loadPosts(category);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
