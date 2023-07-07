package com.example.go4lunch.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;

public class RestaurantDetailDialogue extends DialogFragment {
    private Restaurant currentRestaurant;
    public static RestaurantDetailDialogue newInstance(){
        return new RestaurantDetailDialogue();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogueTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_restaurant_detail,container,false);

        ImageView restaurantImage = view.findViewById(R.id.restaurant_detail_restaurant_iv);
        ImageView star3 = view.findViewById(R.id.restaurant_list_star_3_iv);
        ImageView star2 = view.findViewById(R.id.restaurant_list_star_2_iv);
        ImageView star1 = view.findViewById(R.id.restaurant_list_star_1_iv);

        TextView restaurantName = view.findViewById(R.id.restaurant_detail_name_tv);
        TextView restaurantDetail = view.findViewById(R.id.restaurant_detail_address_tv);

        Log.d("Restaurant Detail", "name: " + currentRestaurant.getName());
        Glide.with(view).load(currentRestaurant.getImageUrl()).centerCrop().into(restaurantImage);
        restaurantName.setText(currentRestaurant.getName());
        restaurantDetail.setText(currentRestaurant.getAddress());

        if(currentRestaurant.getRating() <= 0){
            star1.setVisibility(View.INVISIBLE);
            star2.setVisibility(View.INVISIBLE);
            star3.setVisibility(View.INVISIBLE);
        } else if (currentRestaurant.getRating() == 1) {
            star2.setVisibility(View.INVISIBLE);
            star3.setVisibility(View.INVISIBLE);
        } else if (currentRestaurant.getRating() == 2) {
            star3.setVisibility(View.INVISIBLE);
        } else {
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.VISIBLE);
            star3.setVisibility(View.VISIBLE);
        }


        return view;
    }
    public void setCurrentRestaurant(Restaurant currentRestaurant) {
        this.currentRestaurant = currentRestaurant;
    }
}
