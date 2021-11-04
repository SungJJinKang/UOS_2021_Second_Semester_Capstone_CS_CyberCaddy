package com.uoscybercaddy.dabajo.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<WriteInfo> datas;

    public PostAdapter(List<WriteInfo> datas) {
        this.datas = datas;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        WriteInfo data = datas.get(position);
        holder.title.setText(data.getTitle());
        holder.contents.setText(data.getBody());
        holder.createdAt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(data.getCreatedAt()));

        Log.e("로그 :", "데이터"+datas.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView contents;
        private TextView createdAt;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_post_title);
            contents = itemView.findViewById(R.id.item_post_contents);
            createdAt = itemView.findViewById(R.id.item_post_createdAt);
        }
    }
}
