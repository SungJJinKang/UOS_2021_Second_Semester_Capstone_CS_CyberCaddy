package com.uoscybercaddy.dabajo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.CategorySportActivity;
import com.uoscybercaddy.dabajo.activity.DashboardActivity;
import com.uoscybercaddy.dabajo.activity.DashboardActivityTutor;
import com.uoscybercaddy.dabajo.activity.MainActivity;

public class HomeFragmentTutor extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView showUsersTv,showChattingListTv;
    FirebaseAuth firebaseAuth;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragmentTutor() {
        // Required empty public constructor
    }

    Button sportButton;
    TextView showAll;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragmentTutor newInstance(String param1, String param2) {
        HomeFragmentTutor fragment = new HomeFragmentTutor();
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
        //        textView = (TextView)view.findViewById(R.id.textView5);
        View view = inflater.inflate(R.layout.fragment_home_tutor, container, false);
        sportButton = (Button)view.findViewById(R.id.buttonSport);
        showAll = (TextView)view.findViewById(R.id.viewAll);

        sportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CategorySportActivity.class);
                intent.putExtra("튜터", "tutor");
                startActivity(intent);
                getActivity().finish();
            }
        });

        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DashboardActivityTutor.class);
                intent.putExtra("카테고리로", "tutor");
                startActivity(intent);
                getActivity().finish();
            }
        });
        // Inflate the layout for this fragment
        firebaseAuth = FirebaseAuth.getInstance();

        showUsersTv = (TextView) view.findViewById(R.id.showUsersTv);
        showChattingListTv = (TextView) view.findViewById(R.id.showChattingListTv);
        showUsersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersFragment usersFragment= new UsersFragment();
                ((DashboardActivityTutor)getActivity()).replaceFragment(usersFragment, "유저");
            }
        });
        showChattingListTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatListFragment chatListFragment= new ChatListFragment();
                ((DashboardActivityTutor)getActivity()).replaceFragment(chatListFragment,"채팅");
            }
        });
        return view;
    }
    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            //profileTv.setText(user.getEmail());
        } else{
            startActivity(new Intent(getActivity(), DashboardActivityTutor.class));
            getActivity().finish();
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
        switch(id){
            case R.id.action_logout:
                firebaseAuth.signOut();
                checkUserStatus();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

