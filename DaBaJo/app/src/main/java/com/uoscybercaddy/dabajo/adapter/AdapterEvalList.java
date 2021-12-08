package com.uoscybercaddy.dabajo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.models.ModelEvalList;
import com.uoscybercaddy.dabajo.view.Comment;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdapterEvalList extends RecyclerView.Adapter<AdapterEvalList.EvalViewHolder>{
    private ArrayList<ModelEvalList> modelEvalLists;
    private Activity activity;

    public static class EvalViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public EvalViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
    @NonNull
    @Override
    public AdapterEvalList.EvalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eval, parent, false);
        final EvalViewHolder galleryViewHolder = new EvalViewHolder(cardView);
        return galleryViewHolder;
    }

    public AdapterEvalList( ArrayList<ModelEvalList> myDataset) {
        modelEvalLists = myDataset;
    }

    @Override
    public void onBindViewHolder(@NonNull final EvalViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView body = cardView.findViewById(R.id.item_eval_body);
        TextView name = cardView.findViewById(R.id.item_eval_writer);
        RatingBar rating = cardView.findViewById(R.id.ratingBarEval);
        rating.setIsIndicator(true);
        body.setText(modelEvalLists.get(position).getBody());
        name.setText(modelEvalLists.get(position).getName());
        double d = modelEvalLists.get(position).getRating();
        float f = (float)d;
        rating.setRating(f);

    }

    public void setModelEvalLists(ArrayList<ModelEvalList> list){
        this.modelEvalLists = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return modelEvalLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView body;
        TextView name;
        RatingBar rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rating = (RatingBar) itemView.findViewById(R.id.ratingBarEval);
            rating.setIsIndicator(true);
            name = (TextView) itemView.findViewById(R.id.item_eval_writer);
            body = (TextView) itemView.findViewById(R.id.item_eval_body);
        }

        void onBind(ModelEvalList item){
            double d = item.getRating();
            float f = (float)d;
            rating.setRating(f);
            name.setText(item.getName());
            body.setText(item.getBody());
        }
    }
}
