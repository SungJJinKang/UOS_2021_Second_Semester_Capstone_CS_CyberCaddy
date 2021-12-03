package com.uoscybercaddy.dabajo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.fragment.CategoryFragment;
import com.uoscybercaddy.dabajo.fragment.ChatListFragment;
import com.uoscybercaddy.dabajo.fragment.HomeFragment;
import com.uoscybercaddy.dabajo.fragment.ProfileFragment;
import com.uoscybercaddy.dabajo.notifications.Token;

import java.util.Stack;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    String mUID;
    int fragmentFlag = 0;
    int currentFragmentFlag = 0;
    Stack<Integer> flagStack = new Stack<>(); // flagstack
    // fragmentFlag 0 : home
    // fragmentFlag 1 : category
    // fragmentFlag 2 : chat
    // fragmentFlag 3 : profile
    private static final String TAG = "DashboardActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        actionBar = getSupportActionBar();
        actionBar.hide();


        Intent intent = getIntent();
        if(intent.hasExtra("fromProfileEdit")){
            ProfileFragment fragment4= new ProfileFragment();
            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
            ft4.replace(R.id.content, fragment4, "");
            ft4.commit();
            navigationView.getMenu().getItem(3).setChecked(true);
            //nav_profile fragment transaction
        } else if(intent.hasExtra("카테고리로")) {
            CategoryFragment fragment2 = new CategoryFragment();
            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
            ft2.replace(R.id.content, fragment2, "");
            ft2.commit();
            navigationView.getMenu().getItem(1).setChecked(true);
        } else {
            HomeFragment fragment1= new HomeFragment();
            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            ft1.replace(R.id.content, fragment1, "");
            ft1.commit();
        }
        checkUserStatus();
        //디폴트


    }

    long pressedTime = 0;

//    @Override
//    public void onBackPressed() {
//            if (pressedTime == 0) {
//                Toast.makeText(DashboardActivity.this, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
//                pressedTime = System.currentTimeMillis();
//            } else {
//                int seconds = (int) (System.currentTimeMillis() - pressedTime);
//
//                if (seconds > 2000) {
//                    Toast.makeText(DashboardActivity.this, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
//                    pressedTime = 0;
//                } else {
//                    super.onBackPressed();
//                    System.exit(0);
////                    finish(); // app 종료 시키기
//                }
//            }
//        }

        @Override
    public void onBackPressed() {
        if(flagStack.isEmpty()) {
            if (pressedTime == 0) {
                Toast.makeText(DashboardActivity.this, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
                pressedTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);

                if (seconds > 2000) {
                    Toast.makeText(DashboardActivity.this, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
                    pressedTime = 0;
                } else {
                    System.exit(0);
//                    finish(); // app 종료 시키기
                }
            }
        }
        else {
            super.onBackPressed();
            BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
            navigationView.setOnNavigationItemSelectedListener(selectedListener);
            fragmentFlag = flagStack.pop();
            navigationView.getMenu().getItem(fragmentFlag).setChecked(true);
//        finish();
        }
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

    public void replaceFragment(Fragment fragment, String s) {
        actionBar.setTitle(s);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment).commit();      // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            HomeFragment fragment1= new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                            ft1.addToBackStack(null);
                            ft1.commit();
                            flagStack.push(currentFragmentFlag);
                            currentFragmentFlag = 0;
                            return true;
                        case R.id.nav_category:
                            CategoryFragment fragment2= new CategoryFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, fragment2, "");
                            ft2.addToBackStack(null);
                            ft2.commit();
                            flagStack.push(currentFragmentFlag);
                            currentFragmentFlag = 1;
                            //nav_category fragment transaction
                            return true;
                        case R.id.nav_menu_chat:
                            ChatListFragment fragment3= new ChatListFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content, fragment3, "");
                            ft3.addToBackStack(null);
                            ft3.commit();
                            flagStack.push(currentFragmentFlag);
                            currentFragmentFlag = 2;
                            //nav_favorite fragment transaction
                            return true;
                        case R.id.nav_profile:
                            ProfileFragment fragment4= new ProfileFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content, fragment4, "");
                            ft4.addToBackStack(null);
                            ft4.commit();
                            flagStack.push(currentFragmentFlag);
                            currentFragmentFlag = 3;
                            //nav_profile fragment transaction
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
        } else{
            startActivity(new Intent(DashboardActivity.this, DashboardActivity.class));
            finish();
        }
    }



    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }


}