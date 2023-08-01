package com.example.go4lunch.adapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;

import java.util.List;

public class RestaurantDetailWorkmatesAdapter extends MyWorkmatesAdapter{

    public RestaurantDetailWorkmatesAdapter(Context context, List<User> workmatesList) {
        super(context, workmatesList);
        //Log.d("restaurant detail adapter", "has been created, attending: " + workmatesList.get(0).getDisplayName());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = workmatesList.get(position);

        Log.d("attending workmates Adapter", user.getDisplayName() + " is attending");

        holder.info.setText(user.getDisplayName() + " " + mContext.getString(R.string.is_joining));
        Glide.with(holder.itemView).load(user.getPhotoUrl()).circleCrop().into(holder.profilePic);
    }
}
