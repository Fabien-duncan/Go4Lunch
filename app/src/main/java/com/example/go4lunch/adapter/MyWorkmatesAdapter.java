package com.example.go4lunch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class MyWorkmatesAdapter extends RecyclerView.Adapter<MyWorkmatesAdapter.MyViewHolder> {

    Context mContext;
    List<User> workmatesArrayList;

    public MyWorkmatesAdapter(Context context, List<User> workmatesArrayList) {
        mContext = context;
        this.workmatesArrayList = workmatesArrayList;
    }

    @NonNull
    @Override
    public MyWorkmatesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.workmates_items,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyWorkmatesAdapter.MyViewHolder holder, int position) {
        User user = workmatesArrayList.get(position);

        holder.name.setText(user.getDisplayName());
        holder.extraInfo.setText(" no extra info");
        //Glide.with(holder).load(user.).circleCrop().into(holder.profilePic);


    }

    @Override
    public int getItemCount() {
        return workmatesArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView profilePic;
        TextView name, extraInfo;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.workmates_profile_picture_img);
            name = itemView.findViewById(R.id.workmates_name_tv);
            extraInfo = itemView.findViewById(R.id.workmates_extra_info_tv);

        }
    }
}
