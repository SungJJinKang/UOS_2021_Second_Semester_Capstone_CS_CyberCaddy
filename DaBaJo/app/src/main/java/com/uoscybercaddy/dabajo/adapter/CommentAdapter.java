package com.uoscybercaddy.dabajo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.activity.PostActivity;
import com.uoscybercaddy.dabajo.view.Comment;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private ArrayList<Comment> mDataset;
    private Activity activity;

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public CommentViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public CommentAdapter(Activity activity, ArrayList<Comment> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        final CommentViewHolder galleryViewHolder = new CommentViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView writerView = cardView.findViewById(R.id.item_comment_writer);
        TextView contentView = cardView.findViewById(R.id.item_comment_contents);
        TextView createdAtView = cardView.findViewById(R.id.item_comment_createdAt);
        writerView.setText(mDataset.get(position).WriterUID123);
        contentView.setText(mDataset.get(position).CommentContent123);
        createdAtView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).createdAt));
//        TextView textView = cardView.findViewById(R.id.testText);
//        textView.setText(mDataset.get(position));
//        Log.e("??????: ","?????????: "+mDataset.get(position));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
//    private WriteInfo data;
//    private Activity activity;
//    private ArrayList<Comment> list;
//
//    public CommentAdapter(Activity activity, WriteInfo data) {
//        this.activity = activity;
//        this.data = data;
//        this.list = data.commentList;
//    }
//
//    @NonNull
//    @Override
//    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        CardView cardView = (CardView)LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false);
//        final CommentViewHolder commentViewHolder = new CommentViewHolder(cardView);
//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(activity, PostActivity.class);
//                intent.putExtra("writeInfo", list.get(commentViewHolder.getAdapterPosition()));
//                activity.startActivity(intent);
//            }
//        });
//
//
//        return commentViewHolder;
////        public void onClick(View v) {
////            Intent intent = new Intent(this, PostActivity.class);
////
////        }
////        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
//        Comment data = list.get(position);
//        holder.writer.setText(data.getWriterUID());
//        holder.contents.setText(data.getCommentContent());
////        holder.createdAt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(data.getCreatedAt()));
//
//        Log.e("?????? :", "?????????"+list.get(position).getCommentContent());
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    class CommentViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView writer;
//        private TextView contents;
////        private TextView createdAt;
//
//        public CommentViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            writer = itemView.findViewById(R.id.item_comment_writer);
//            contents = itemView.findViewById(R.id.item_comment_contents);
////            createdAt = itemView.findViewById(R.id.item_comment_createdAt);
//        }
//    }
//}