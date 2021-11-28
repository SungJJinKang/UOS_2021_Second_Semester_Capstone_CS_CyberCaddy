package com.uoscybercaddy.dabajo.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.CategorySportActivity;
import com.uoscybercaddy.dabajo.activity.DashboardActivity;
import com.uoscybercaddy.dabajo.activity.MainActivity;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    TextView showUsersTv,showChattingListTv;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FirebaseAuth firebaseAuth;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    Button buttons[] = new Button[4];
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
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        showAll = (TextView)view.findViewById(R.id.viewAll);


        buttons[0] = (Button)view.findViewById(R.id.buttonSport);
        buttons[1] = (Button)view.findViewById(R.id.buttonArtPhy);
        buttons[2] = (Button)view.findViewById(R.id.buttonHealth);
        buttons[3] = (Button)view.findViewById(R.id.buttonEdu);

        for(int i = 0 ; i < 4 ; i++) {
            int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), CategorySportActivity.class);
                    intent.putExtra("튜티", "tutee");
                    intent.putExtra("BigCategory", buttons[finalI].getText());
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }

        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                intent.putExtra("카테고리로", 1);
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
                ((DashboardActivity)getActivity()).replaceFragment(usersFragment, "유저");
            }
        });
        showChattingListTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatListFragment chatListFragment= new ChatListFragment();
                ((DashboardActivity)getActivity()).replaceFragment(chatListFragment,"채팅");
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
            case R.id.nav_chat:
                ChatListFragment chatListFragment= new ChatListFragment();
                ((DashboardActivity)getActivity()).replaceFragment(chatListFragment,"채팅");
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}