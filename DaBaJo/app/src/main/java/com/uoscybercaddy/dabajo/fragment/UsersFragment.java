package com.uoscybercaddy.dabajo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uoscybercaddy.dabajo.ModelUsers;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.AdapterUsers;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUsers> usersList;

    public UsersFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        recyclerView = view.findViewById(R.id.users_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        usersList = new ArrayList<>();

        getAllUsers();

        return view;
    }

    private void getAllUsers() {
        // 현재 user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //
    }
}
