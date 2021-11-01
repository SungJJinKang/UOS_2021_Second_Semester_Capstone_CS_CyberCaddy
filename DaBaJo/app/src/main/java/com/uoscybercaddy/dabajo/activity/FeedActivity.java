package com.uoscybercaddy.dabajo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.PostAdapter;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedActivity extends AppCompatActivity  {

    private RecyclerView feedRecyclerView;
    private PostAdapter mAdapter;
    private List<WriteInfo> mDatas;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        findViewById(R.id.writePostButton).setOnClickListener(onClickListener);
        feedRecyclerView = findViewById(R.id.feedRecyclerView);
        mDatas = new ArrayList<>();
        mDatas.add(new WriteInfo("title","contents","writer",new Date()));
        mDatas.add(new WriteInfo("title","contents","writer",new Date()));
        mDatas.add(new WriteInfo("title","contents","writer",new Date()));

        mAdapter = new PostAdapter(mDatas);
        feedRecyclerView.setAdapter(mAdapter);
        }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.writePostButton:
                    startToast("works");
                    startActivityShortcut(PostActivity.class);
                    break;

            }
        }
    };

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startActivityShortcut(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    } // startactivity 한번에 사용하기\
}
