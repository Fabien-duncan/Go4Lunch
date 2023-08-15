package com.example.go4lunch.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;

import java.util.List;

public class MyWorkmatesAdapter extends RecyclerView.Adapter<MyWorkmatesAdapter.MyViewHolder> {
    private final WorkmatesRecyclerViewInterface mWorkmatesRecyclerViewInterface;
    Context mContext;
    List<User> workmatesList;

    public MyWorkmatesAdapter(Context context, List<User> workmatesList, WorkmatesRecyclerViewInterface workmatesRecyclerViewInterface) {
        mContext = context;
        this.workmatesList = workmatesList;
        this.mWorkmatesRecyclerViewInterface = workmatesRecyclerViewInterface;
    }

    @NonNull
    @Override
    public MyWorkmatesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.workmates_item,parent,false);

        return new MyViewHolder(v,mWorkmatesRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyWorkmatesAdapter.MyViewHolder holder, int position) {
        User user = workmatesList.get(position);

        if(user.getLunchChoiceId().isEmpty() || !user.isToday()){
            String text = user.getDisplayName() + " " + mContext.getString(R.string.not_decided);
            holder.info.setText(text);
            holder.info.setAlpha(0.5f);
        }else{
            String text = user.getDisplayName() + " " + mContext.getString(R.string.eating_at) + " " + user.getLunchChoiceName();
            holder.info.setText(text);
        }
        Glide.with(holder.itemView).load(user.getPhotoUrl()).circleCrop().into(holder.profilePic);


    }

    @Override
    public int getItemCount() {
        Log.d("MyWorkmateAdapter", "Item count " + workmatesList.size());
        return workmatesList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setWorkmatesList(List<User> workmatesList){
        this.workmatesList = workmatesList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView profilePic;
        TextView info, extraInfo;
        public MyViewHolder(@NonNull View itemView, WorkmatesRecyclerViewInterface workmatesRecyclerViewInterface) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.workmates_profile_picture_img);
            info = itemView.findViewById(R.id.workmates_info_tv);
            //extraInfo = itemView.findViewById(R.id.workmates_extra_info_tv);

            itemView.setOnClickListener(view -> {
                if (workmatesRecyclerViewInterface != null) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        workmatesRecyclerViewInterface.onItemClicked(pos);
                    }
                }
            });
        }
    }
}
