package com.uoscybercaddy.dabajo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.DashboardActivity;
import com.uoscybercaddy.dabajo.activity.MainActivity;
import com.uoscybercaddy.dabajo.activity.SettingsActivity;
import com.uoscybercaddy.dabajo.adapter.AdapterComments;
import com.uoscybercaddy.dabajo.adapter.AdapterNotification;
import com.uoscybercaddy.dabajo.models.ModelComment;
import com.uoscybercaddy.dabajo.models.ModelNotification;
import com.uoscybercaddy.dabajo.models.ModelUsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {
    SwitchCompat postSwitch;
    TextView noPost;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    private static final String TOPIC_POST_NOTIFICATION = "POST";
    RecyclerView notificationsRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelNotification> notificationList;

    private AdapterNotification adapterNotification;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        View view =  inflater.inflate(R.layout.fragment_notifications, container, false);
        notificationsRv = view.findViewById(R.id.notificationsRv);
        firebaseAuth = FirebaseAuth.getInstance();
        getAllNotifications();
        postSwitch = view.findViewById(R.id.postSwitch);
        noPost = (TextView)view.findViewById(R.id.noPost);
        sp = this.getActivity().getSharedPreferences("Notification_SP", Context.MODE_PRIVATE);
        boolean isPostEnabled = sp.getBoolean(""+TOPIC_POST_NOTIFICATION, false);

        if (isPostEnabled) {
            postSwitch.setChecked(true);
        }
        else{
            postSwitch.setChecked(false);
        }

        postSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = sp.edit();
                editor.putBoolean(""+TOPIC_POST_NOTIFICATION, isChecked);
                editor.apply();
                if(isChecked){
                    subscribePostNotification();
                }
                else{
                    unsubscribePostNotification();
                }
            }
        });
        return view;
    }
    private void unsubscribePostNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "게시물에 대한 알림을 받지 않습니다.";
                        if(!task.isSuccessful()){
                            msg = "알림 해제 설정 실패...";
                        }
                        Toast.makeText((DashboardActivity)getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void subscribePostNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "게시물에 대한 알림을 받습니다.";
                        if(!task.isSuccessful()){
                            msg = "알림 설정 실패...";
                        }
                        Toast.makeText((DashboardActivity)getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getAllNotifications() {
        notificationList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(firebaseAuth.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "Listen failed.", e);
                    noPost.setVisibility(View.VISIBLE);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    notificationList.clear();
                    ModelUsers modelUsers = snapshot.toObject(ModelUsers.class);
                    HashMap<String, HashMap<String,String>> notifications = modelUsers.getNotifications();
                    if(notifications == null){
                        noPost.setVisibility(View.VISIBLE);
                    }else{
                        noPost.setVisibility(View.GONE);
                    }
                    if(notifications != null){
                        SortedSet<String> keys = new TreeSet<>(notifications.keySet());
                        for (String key : keys) {
                            HashMap<String, String> value = notifications.get(key);
                            ModelNotification modelNotification = new ModelNotification(value);
                            notificationList.add(modelNotification);
                            // do something
                        }
                        adapterNotification = new AdapterNotification(getActivity(), notificationList);
                        notificationsRv.setAdapter(adapterNotification);
                    }
                    Log.d("", "Current data: " + snapshot.getData());
                } else {
                    noPost.setVisibility(View.VISIBLE);
                    Log.d("", "Current data: null");
                }
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.nav_chat).setVisible(false);
        super.onCreateOptionsMenu(menu, menuInflater);
    }



    //handle menu item clicks
    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            //profileTv.setText(user.getEmail());
        } else{
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }
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