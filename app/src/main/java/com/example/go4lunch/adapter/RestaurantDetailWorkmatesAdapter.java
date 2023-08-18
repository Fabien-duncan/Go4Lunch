package com.example.go4lunch.adapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;

import java.util.List;

/**
 * Extends the MyWorkmatesAdapter and is used to display the attending workmates in the RestaurantDetailDialogue Fragment
 */
public class RestaurantDetailWorkmatesAdapter extends MyWorkmatesAdapter{

    public RestaurantDetailWorkmatesAdapter(Context context, List<User> workmatesList, WorkmatesRecyclerViewInterface workmatesRecyclerViewInterface) {
        super(context, workmatesList, workmatesRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = workmatesList.get(position);

        String text =user.getDisplayName() + " " + mContext.getString(R.string.is_joining);
        holder.info.setText(text);
        Glide.with(holder.itemView).load(user.getPhotoUrl()).circleCrop().into(holder.profilePic);
    }
}
