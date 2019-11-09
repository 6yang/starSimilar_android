package com.example.cameraalbumtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cameraalbumtest.entity.Our;

import java.util.List;


public class OursAdapter extends RecyclerView.Adapter<OursAdapter.ViewHolder> {

    private Context mcontext;

    private List<Our> ourList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View fruitView;
        ImageView imageView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fruitView = itemView;
            imageView = itemView.findViewById(R.id.f_img);
            textView = itemView.findViewById(R.id.f_name);
        }
    }

    public OursAdapter(List<Our> ourList) {
        this.ourList = ourList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        mcontext = parent.getContext();
        final View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.our_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Our our = ourList.get(position);
        Glide.with(mcontext)
                .load(our.getUrl())
                .into(holder.imageView);
        holder.textView.setText(our.getName());
    }

    @Override
    public int getItemCount() {
        return ourList.size();
    }

}