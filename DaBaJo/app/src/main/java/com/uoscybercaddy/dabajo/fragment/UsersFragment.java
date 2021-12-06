package com.uoscybercaddy.dabajo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uoscybercaddy.dabajo.activity.SettingsActivity;
import com.uoscybercaddy.dabajo.models.ModelUsers;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.MainActivity;
import com.uoscybercaddy.dabajo.adapter.AdapterUsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersFragment extends Fragment {
    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    FirebaseAuth firebaseAuth;
    List<ModelUsers> usersList;
    String tutortuty;
    Toolbar toolbar;
    private static final String TAG = "UserFragment";
    public UsersFragment(){

    }
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types and number of parameters
    private String mParam1;
    private String mParam2;
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);


        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = view.findViewById(R.id.users_recyclerView);
        recyclerView.setHasFixedSize(true);
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        BottomNavigationView navigationView = (BottomNavigationView) view.findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        toolbar.setTitle("");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        usersList = new ArrayList<>();
        tutortuty = "튜티";
        getAllUsers();

        return view;
    }
    private void setAllUsersUnBlocked() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(fUser.getUid());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("tutortuty", tutortuty)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            usersList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name, nickName, photoUrl, sex, tutortuty, introduction, search,uid;
                                name = document.getData().get("name").toString();
                                nickName = document.getData().get("nickName").toString();
                                if(document.getData().get("photoUrl") != null){
                                    photoUrl = document.getData().get("photoUrl").toString();
                                }else{
                                    photoUrl = null;
                                }
                                if(document.getData().get("tutortuty") != null){
                                    tutortuty = document.getData().get("tutortuty").toString();
                                }else{
                                    tutortuty = "튜티";
                                }

                                introduction = document.getData().get("introduction").toString();
                                sex = document.getData().get("sex").toString();
                                uid = document.getId();

                                ModelUsers modelUser = new ModelUsers(name, nickName, photoUrl, sex,  tutortuty, introduction,uid);
                                if(!(document.getId().equals(fUser.getUid()))){
                                    usersList.add(modelUser);
                                    HashMap<String, Object> blockUsers = new HashMap<>();
                                    blockUsers.put("blockedUsers."+uid, FieldValue.delete());
                                    docRef.update(blockUsers)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.e("","차단 성공");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("","차단 실패");
                                                }
                                            });
                                }
                            }
                            adapterUsers = new AdapterUsers(getActivity(), usersList);
                            recyclerView.setAdapter(adapterUsers);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setAllUsersBlocked() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(fUser.getUid());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("tutortuty", tutortuty)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            usersList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name, nickName, photoUrl, sex, tutortuty, introduction, search,uid;
                                name = document.getData().get("name").toString();
                                nickName = document.getData().get("nickName").toString();
                                if(document.getData().get("photoUrl") != null){
                                    photoUrl = document.getData().get("photoUrl").toString();
                                }else{
                                    photoUrl = null;
                                }
                                if(document.getData().get("tutortuty") != null){
                                    tutortuty = document.getData().get("tutortuty").toString();
                                }else{
                                    tutortuty = "튜티";
                                }

                                introduction = document.getData().get("introduction").toString();
                                sex = document.getData().get("sex").toString();
                                uid = document.getId();

                                ModelUsers modelUser = new ModelUsers(name, nickName, photoUrl, sex,  tutortuty, introduction,uid);
                                if(!(document.getId().equals(fUser.getUid()))){
                                    usersList.add(modelUser);
                                    HashMap<String, Object> blockUsers = new HashMap<>();
                                    blockUsers.put("blockedUsers."+uid, uid);
                                    docRef.update(blockUsers)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.e("","차단 성공");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("","차단 실패");
                                                }
                                            });
                                }
                            }
                            adapterUsers = new AdapterUsers(getActivity(), usersList);
                            recyclerView.setAdapter(adapterUsers);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getAllUsers() {
        // 현재 user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("tutortuty", tutortuty)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            usersList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String name, nickName, photoUrl, sex, tutortuty, introduction, search,uid;
                                name = document.getData().get("name").toString();
                                nickName = document.getData().get("nickName").toString();
                                if(document.getData().get("photoUrl") != null){
                                    photoUrl = document.getData().get("photoUrl").toString();
                                }else{
                                    photoUrl = null;
                                }
                                if(document.getData().get("tutortuty") != null){
                                    tutortuty = document.getData().get("tutortuty").toString();
                                }else{
                                    tutortuty = "튜티";
                                }

                                introduction = document.getData().get("introduction").toString();
                                sex = document.getData().get("sex").toString();
                                uid = document.getId();

                                ModelUsers modelUser = new ModelUsers(name, nickName, photoUrl, sex,  tutortuty, introduction,uid);
                                if(!(document.getId().equals(fUser.getUid()))){
                                    usersList.add(modelUser);
                                }
                                adapterUsers = new AdapterUsers(getActivity(), usersList);
                                recyclerView.setAdapter(adapterUsers);
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void searchUsers(String query) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("tutortuty", tutortuty)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            usersList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name, nickName, photoUrl, sex, tutortuty, introduction, search,uid;
                                name = document.getData().get("name").toString();
                                nickName = document.getData().get("nickName").toString();
                                Object photo = document.getData().get("photoUrl");
                                if(photo != null){
                                    photoUrl = photo.toString();
                                }else{
                                    photoUrl = null;
                                }
                                Object tt = document.getData().get("tutortuty");
                                if(tt!=null){
                                    tutortuty = tt.toString();
                                }else{
                                    tutortuty = "튜티";
                                }

                                introduction = document.getData().get("introduction").toString();
                                sex = document.getData().get("sex").toString();
                                uid = document.getId();

                                ModelUsers modelUser = new ModelUsers(name, nickName, photoUrl, sex,  tutortuty, introduction,uid);
                                if(!(uid.equals(fUser.getUid()))){
                                    if(modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                    modelUser.getNickName().toLowerCase().contains(query.toLowerCase())
                                    ||modelUser.getIntroduction().toLowerCase().contains(query.toLowerCase())
                                    ){
                                        usersList.add(modelUser);
                                    }
                                }
                                adapterUsers = new AdapterUsers(getActivity(), usersList);
                                adapterUsers.notifyDataSetChanged();
                                recyclerView.setAdapter(adapterUsers);
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            //profileTv.setText(user.getEmail());
        } else{
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_tuty:
                            tutortuty = "튜티";
                            getAllUsers();
                            return true;
                        case R.id.nav_tutor:
                            tutortuty = "튜터";
                            getAllUsers();
                            //nav_category fragment transaction
                            return true;
                    }
                    return false;
                }
            };
    //inflate option menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_search_users, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    searchUsers(query);
                }
                else{
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    searchUsers(newText);
                }
                else{
                    getAllUsers();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, menuInflater);
    }



    //handle menu item clicks

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        else if (id==R.id.action_blockUsers){
            setAllUsersBlocked();
        }
        else if (id==R.id.action_unblockUsers){
            setAllUsersUnBlocked();
        }
        return super.onOptionsItemSelected(item);
    }

    
}
