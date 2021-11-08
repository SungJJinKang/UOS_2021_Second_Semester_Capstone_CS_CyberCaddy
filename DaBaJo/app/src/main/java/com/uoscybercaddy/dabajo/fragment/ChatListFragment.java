package com.uoscybercaddy.dabajo.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.MainActivity;
import com.uoscybercaddy.dabajo.adapter.AdapterChatlist;
import com.uoscybercaddy.dabajo.models.ModelChatlist;
import com.uoscybercaddy.dabajo.models.ModelUsers;
import com.uoscybercaddy.dabajo.models.Modelchat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatListFragment extends Fragment {

    private static final String TAG = "ChatListFragment";
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelChatlist> chatlistList;
    List<ModelUsers> userList;
    FirebaseUser currentUser;
    CollectionReference dbRef;
    AdapterChatlist adapterChatlist;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatListFragment newInstance(String param1, String param2) {
        ChatListFragment fragment = new ChatListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.recyclerView);

        chatlistList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        dbRef = db.collection("chatlist");

        dbRef.document(currentUser.getUid())
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
                            chatlistList.clear();
                            for(String key : snapshot.getData().keySet()){
                                Log.e("key : ",""+key);
                                ModelChatlist chatlist = new ModelChatlist(key);
                                chatlistList.add(chatlist);
                            }
                            loadChats();
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });

        return view;

    }

    private void loadChats() {
        userList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        userList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            ModelUsers user = doc.toObject(ModelUsers.class);
                            String getuid = user.getUid();
                            if(getuid == null){
                                getuid = doc.getId();
                                user.setUid(doc.getId());
                            }
                            for(ModelChatlist chatlist: chatlistList){
                                if(getuid != null && getuid.equals(chatlist.getId())){
                                    userList.add(user);
                                    break;
                                }
                            }
                            adapterChatlist = new AdapterChatlist(getContext(),userList);
                            recyclerView.setAdapter(adapterChatlist);
                            for(int i=0; i<userList.size(); i++){
                                lastMessage(userList.get(i).getUid());
                            }
                        }
                    }
                });
    }

    private void lastMessage(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        String theLastMessage = "default";
                        for (QueryDocumentSnapshot doc : value) {
                            Modelchat chat = doc.toObject(Modelchat.class);
                            if(chat==null){
                                continue;
                            }
                            String sender = chat.getSender();
                            String receiver = chat.getReceiver();
                            if(sender ==null || receiver ==null){
                                continue;
                            }
                            if(chat.getReceiver().equals(currentUser.getUid()) &&
                            chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) &&
                                    chat.getSender().equals(currentUser.getUid())){
                                theLastMessage = chat.getMessage();
                            }
                        }
                        adapterChatlist.setLastMessageMap(userId, theLastMessage);
                        adapterChatlist.notifyDataSetChanged();
                    }
                });
    }
}