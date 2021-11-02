package com.uoscybercaddy.dabajo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.fragment.CategoryFragment;
import com.uoscybercaddy.dabajo.fragment.FavoriteFragment;
import com.uoscybercaddy.dabajo.fragment.HomeFragment;
import com.uoscybercaddy.dabajo.fragment.ProfileFragment;
import com.uoscybercaddy.dabajo.fragment.UsersFragment;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();
        actionBar.setTitle("프로필");
        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        Intent intent = getIntent();
        if(intent.hasExtra("fromProfileEdit")){
            actionBar.setTitle("프로필");
            ProfileFragment fragment4= new ProfileFragment();
            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
            ft4.replace(R.id.content, fragment4, "");
            ft4.commit();
            navigationView.getMenu().getItem(3).setChecked(true);
            //nav_profile fragment transaction
        }else{
            actionBar.setTitle("홈");
            HomeFragment fragment1= new HomeFragment();
            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            ft1.replace(R.id.content, fragment1, "");
            ft1.commit();
        }
        //디폴트

    }
    public void replaceFragment(Fragment fragment) {
        actionBar.setTitle("유저");
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
                            actionBar.setTitle("홈");
                            HomeFragment fragment1= new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                            ft1.commit();
                            return true;
                        case R.id.nav_category:
                            actionBar.setTitle("카테고리");
                            CategoryFragment fragment2= new CategoryFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, fragment2, "");
                            ft2.commit();
                            //nav_category fragment transaction
                            return true;
                        case R.id.nav_favorite:
                            actionBar.setTitle("좋아요");
                            FavoriteFragment fragment3= new FavoriteFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content, fragment3, "");
                            ft3.commit();
                            //nav_favorite fragment transaction
                            return true;
                        case R.id.nav_profile:
                            actionBar.setTitle("프로필");
                            ProfileFragment fragment4= new ProfileFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content, fragment4, "");
                            ft4.commit();
                            //nav_profile fragment transaction
                            return true;

                    }
                    return false;
                }
            };

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            //profileTv.setText(user.getEmail());
        } else{
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
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


}