package com.uoscybercaddy.dabajo.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.FeedActivity;
import com.uoscybercaddy.dabajo.activity.NoticeActivity;
import com.uoscybercaddy.dabajo.activity.LoginActivity;
import com.uoscybercaddy.dabajo.activity.MainActivity;
import com.uoscybercaddy.dabajo.activity.MemberinfoinitActivity;
import com.uoscybercaddy.dabajo.activity.PostFeedActivityUsers;
import com.uoscybercaddy.dabajo.activity.SignUpActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    ImageView avatarIv;
    TextView nickNameTv, descriptionTv;
    ImageButton notice, logout, commentedByMe, writtenByMe;
    FloatingActionButton fab;
    ProgressDialog pd;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        avatarIv = (ImageView) view.findViewById(R.id.avatarIv);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        nickNameTv = (TextView) view.findViewById(R.id.nickNameTv);
        descriptionTv = (TextView) view.findViewById(R.id.descriptionTv);
        notice = (ImageButton) view.findViewById(R.id.noticeButton);
        logout = (ImageButton) view.findViewById(R.id.logoutButton);
        commentedByMe = (ImageButton) view.findViewById(R.id.commentedByMeButton);
        writtenByMe = (ImageButton) view.findViewById(R.id.writtenByMeButton);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(getActivity());


        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if(document.getData().get("photoUrl") != null){
                            Glide.with(getActivity()).load(document.getData().get("photoUrl")).centerCrop().override(500).into(avatarIv);
                        }
                        nickNameTv.setText(document.getData().get("nickName").toString());
                        descriptionTv.setText(document.getData().get("introduction").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MemberinfoinitActivity.class);
                intent.putExtra("fromProfileEdit","fromProfileEdit");
                startActivity(intent);
//                getActivity().finish();
            }
        });

        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoticeActivity.class);
                intent.putExtra("튜티",1);
                startActivity(intent);
//                getActivity().finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
//                getActivity().finish();
            }
        });

        writtenByMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostFeedActivityUsers.class);
                intent.putExtra("uid", firebaseAuth.getCurrentUser().getUid());
                getActivity().startActivity(intent);
            }
        });

        commentedByMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostFeedActivityUsers.class);
                intent.putExtra("uid", firebaseAuth.getCurrentUser().getUid());
                intent.putExtra("OnlyCommnets", true);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }
    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            //profileTv.setText(user.getEmail());
        } else{
            startActivity(new Intent(getActivity(), MainActivity.class));
//            getActivity().finish();
        }
    }

    //inflate option menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu);
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
        return super.onOptionsItemSelected(item);
    }
}