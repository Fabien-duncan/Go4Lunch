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
import com.example.go4lunch.util.FormatString;

import java.util.List;

public class MyWorkmatesAdapter extends RecyclerView.Adapter<MyWorkmatesAdapter.MyViewHolder> {

    Context mContext;
    List<User> workmatesList;

    public MyWorkmatesAdapter(Context context, List<User> workmatesList) {
        mContext = context;
        this.workmatesList = workmatesList;
    }

    @NonNull
    @Override
    public MyWorkmatesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.workmates_item,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyWorkmatesAdapter.MyViewHolder holder, int position) {
        User user = workmatesList.get(position);

        if(user.getLunchChoiceId().isEmpty()){
            holder.info.setText(user.getDisplayName() + " hasn't decided yet");
            holder.info.setAlpha(0.5f);
        }else{
            holder.info.setText(user.getDisplayName() + " is eating at " + user.getLunchChoiceName());
        }
        //Glide.with(holder).load(user.).circleCrop().into(holder.profilePic);
        Glide.with(holder.itemView).load(user.getPhotoUrl()).circleCrop().into(holder.profilePic);


    }

    @Override
    public int getItemCount() {
        return workmatesList.size();
    }

    public void setWorkmatesList(List<User> workmatesList){
        this.workmatesList = workmatesList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView profilePic;
        TextView info, extraInfo;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.workmates_profile_picture_img);
            info = itemView.findViewById(R.id.workmates_info_tv);
            //extraInfo = itemView.findViewById(R.id.workmates_extra_info_tv);

        }
    }
}
