package com.example.go4lunch.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;

import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.MyViewHolder> {
    Context mContext;
    List<Restaurant> mRestaurantList;

    public RestaurantsAdapter(Context context, List<Restaurant> restaurantList) {
        mContext = context;
        mRestaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.restaurant_item,parent,false);

        return new RestaurantsAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantsAdapter.MyViewHolder holder, int position) {
        Restaurant restaurant = mRestaurantList.get(position);

        holder.name.setText(restaurant.getName());
        holder.address.setText(restaurant.getAddress());
        holder.distance.setText(restaurant.getDistance() + "m");

        if(restaurant.getOpeningHours().equals("true")){
            holder.openingHours.setText("Open");
            holder.openingHours.setTextColor(Color.GREEN);
        }else if(restaurant.getOpeningHours().equals("false")){
            holder.openingHours.setText("Closed");
            holder.openingHours.setTextColor(Color.RED);
        }else {
            holder.openingHours.setText(restaurant.getOpeningHours());
            holder.openingHours.setTextColor(Color.BLUE);
        }


        if(restaurant.getRating() <= 0){
            holder.star1_img.setVisibility(View.INVISIBLE);
            holder.star2_img.setVisibility(View.INVISIBLE);
            holder.star3_img.setVisibility(View.INVISIBLE);
        } else if (restaurant.getRating() == 1) {
            holder.star2_img.setVisibility(View.INVISIBLE);
            holder.star3_img.setVisibility(View.INVISIBLE);
        } else if (restaurant.getRating() == 2) {
            holder.star3_img.setVisibility(View.INVISIBLE);
        } else {
            holder.star1_img.setVisibility(View.VISIBLE);
            holder.star2_img.setVisibility(View.VISIBLE);
            holder.star3_img.setVisibility(View.VISIBLE);

        }

        if(restaurant.getAttendanceNum() == 0){
            holder.workmatesNumber.setText("");
            holder.workmatesNumber_img.setVisibility(View.INVISIBLE);
        }else{
            holder.workmatesNumber.setText("(" + restaurant.getAttendanceNum() + ")");
        }

        Glide.with(holder.itemView).load(restaurant.getImageUrl()).centerCrop().into(holder.restaurant_img);

    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }
    public void setRestaurantList(List<Restaurant> restaurantList){
        this.mRestaurantList = restaurantList;
        notifyDataSetChanged();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView restaurant_img, star1_img, star2_img, star3_img, workmatesNumber_img;
        TextView name, address, openingHours, distance, workmatesNumber;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurant_img = itemView.findViewById(R.id.restaurant_list_picture_img);
            star1_img = itemView.findViewById(R.id.restaurant_list_star_1_iv);
            star2_img = itemView.findViewById(R.id.restaurant_list_star_2_iv);
            star3_img = itemView.findViewById(R.id.restaurant_list_star_3_iv);
            name = itemView.findViewById(R.id.restaurant_list_name_tv);
            address = itemView.findViewById(R.id.restaurant_list_address_tv);
            openingHours = itemView.findViewById(R.id.restaurant_list_open_hours_tv);
            distance = itemView.findViewById(R.id.restaurant_list_distance_tv);
            workmatesNumber = itemView.findViewById(R.id.restaurant_list_workmates_num_tv);
            workmatesNumber_img = itemView.findViewById(R.id.restaurant_list_workmates_num_iv);
        }
    }

}
