package com.example.go4lunch.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        Button websiteLink = view.findViewById(R.id.restaurant_detail_website_btn);
        Button like = view.findViewById(R.id.restaurant_detail_like_btn);
        Button phone = view.findViewById(R.id.restaurant_detail_call_btn);
        FloatingActionButton attend = view.findViewById(R.id.restaurant_detail_attend_fb);

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

        websiteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.amazon.fr/";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getContext(), Manifest.permission.CALL_PHONE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.

                } else if (shouldShowRequestPermissionRationale("")) {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected, and what
                    // features are disabled if it's declined. In this UI, include a
                    // "cancel" or "no thanks" button that lets the user continue
                    // using your app without granting the permission.

                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                            Manifest.permission.CALL_PHONE);
                }
                /*Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);*/
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return view;
    }
    public void setCurrentRestaurant(Restaurant currentRestaurant) {
        this.currentRestaurant = currentRestaurant;
    }
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
}
