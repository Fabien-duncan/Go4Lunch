package com.example.go4lunch.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.adapter.RestaurantDetailWorkmatesAdapter;
import com.example.go4lunch.adapter.WorkmatesRecyclerViewInterface;
import com.example.go4lunch.dataSource.GooglePlacesDetailsApi;
import com.example.go4lunch.di.Injection;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.repository.RestaurantDetailRepository;
import com.example.go4lunch.viewmodel.ConnectedActivityViewModel;
import com.example.go4lunch.viewmodel.RestaurantDetailViewModel;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RestaurantDetailDialogue extends DialogFragment implements WorkmatesRecyclerViewInterface {
    private Restaurant currentRestaurant;
    private Uri restaurantUrl;
    private Button websiteLink;
    private ConnectedActivityViewModel mConnectedActivityViewModel;
    private RestaurantDetailViewModel mRestaurantDetailViewModel;
    private User currentUser;
    private boolean isAttending;
    private boolean isFavorite;
    private ImageView restaurantImage, star1, star2, star3;
    private TextView restaurantName,restaurantDetail;

    public static RestaurantDetailDialogue newInstance(){
        return new RestaurantDetailDialogue();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogueTheme);
        mConnectedActivityViewModel = ((ConnectedActivity) requireActivity()).getConnectedActivityViewModel();
        AuthenticationRepository authenticationRepository = Injection.createAuthenticationRepository(this.getContext());
        isAttending = false;
        isFavorite = false;

        /*String key = BuildConfig.GMP_key;
        Places.initialize(getContext(), key);
        PlacesClient placesClient = Places.createClient(getContext());*/

        RestaurantDetailRepository restaurantDetailRepository = Injection.createRestaurantDetailRepository(getContext());
        mRestaurantDetailViewModel = new RestaurantDetailViewModel(authenticationRepository, restaurantDetailRepository);
    }


    @SuppressLint("UseCompatTextViewDrawableApis")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_restaurant_detail,container,false);

        restaurantImage = view.findViewById(R.id.restaurant_detail_restaurant_iv);
        star3 = view.findViewById(R.id.restaurant_list_star_3_iv);
        star2 = view.findViewById(R.id.restaurant_list_star_2_iv);
        star1 = view.findViewById(R.id.restaurant_list_star_1_iv);

        websiteLink = view.findViewById(R.id.restaurant_detail_website_btn);
        Button like = view.findViewById(R.id.restaurant_detail_like_btn);
        Button phone = view.findViewById(R.id.restaurant_detail_call_btn);
        FloatingActionButton attend = view.findViewById(R.id.restaurant_detail_attend_fb);

        like.setAlpha(0.5f);
        /*like.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#86FB7540")));
        like.setTextColor(ColorStateList.valueOf(Color.parseColor("#86FB7540")));*/

        restaurantName = view.findViewById(R.id.restaurant_detail_name_tv);
        restaurantDetail = view.findViewById(R.id.restaurant_detail_address_tv);


        List<User> attendingWorkmatesList = mRestaurantDetailViewModel.getAllWorkmates().getValue();

        RecyclerView attendingWorkmatesRecyclerView = view.findViewById(R.id.restaurant_detail_attend_rv);
        attendingWorkmatesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        RestaurantDetailWorkmatesAdapter restaurantDetailWorkmatesAdapter = new RestaurantDetailWorkmatesAdapter(getContext(), attendingWorkmatesList, this);
        attendingWorkmatesRecyclerView.setAdapter(restaurantDetailWorkmatesAdapter);
        restaurantDetailWorkmatesAdapter.setWorkmatesList(attendingWorkmatesList);



        mRestaurantDetailViewModel.getAllWorkmates().observe(this, users -> {
            if(users.size()>0)Log.d("attending workmates", users.get(0).getDisplayName() + " is attending");
            restaurantDetailWorkmatesAdapter.setWorkmatesList(users);
        });
        mRestaurantDetailViewModel.getCurrentRestaurantMutableLiveDate().observe(this, restaurant -> {
            currentRestaurant = restaurant;
            setRestaurantDetail(view);
        });


        mConnectedActivityViewModel.getCurrentUserMutableLiveData().observe(this, user -> {
            Log.d("User data Rest detail", "id: " + user.getDisplayName());
            currentUser = user;

            mRestaurantDetailViewModel.retrieveFilteredWorkmates(currentRestaurant.getId());

            /*String key = BuildConfig.GMP_key;
            Places.initialize(getContext(), key);*/
            mRestaurantDetailViewModel.setDetail(currentRestaurant);

            if(currentRestaurant != null){
                if(user.getLunchChoiceId()!= null && user.getLunchChoiceId().equals(currentRestaurant.getId()) && user.isToday()){
                    attend.setImageResource(R.drawable.baseline_check_circle_24);
                    isAttending = true;
                }
                if(user.isFavorite(currentRestaurant.getId())){
                    like.setAlpha(1f);
                    isFavorite = true;
                }
            }
            else{
                attend.setImageResource(R.drawable.baseline_check_circle_24);
                isAttending = true;
                like.setAlpha(0.5f);
                isFavorite = true;
            }

        });
        websiteLink.setOnClickListener(view1 -> {
            if(currentRestaurant.getWebsite()!=null){
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentRestaurant.getWebsite().toString()));
                startActivity(i);
            }
            else Log.d("RestaurantURL", "there is no website!");

        });
        phone.setOnClickListener(view12 -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CALL_PHONE) ==
                    PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + currentRestaurant.getPhoneNumber()));
                startActivity(intent);


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

        });
        like.setOnClickListener(view13 -> {
            if(isFavorite){
                like.setAlpha(0.5f);
            }else {
                like.setAlpha(1.0f);
            }
            isFavorite = !isFavorite;
        });
        attend.setOnClickListener(view14 -> {
            if(isAttending){
                attend.setImageResource(R.drawable.baseline_check_circle_transparent_24);
            }else{
                attend.setImageResource(R.drawable.baseline_check_circle_24);
            }
            isAttending = !isAttending;
        });

        return view;
    }
    public void setCurrentRestaurant(Restaurant currentRestaurant) {
        this.currentRestaurant = currentRestaurant;
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    Log.d("Call permissions", "Call granted!!");
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Log.d("Call permission", "not granted to use phone, please change your settings in order to have access to all the features ");
                }
            });

    @Override
    public void onDestroy() {

        Calendar timeChoiceStamp = Calendar.getInstance();
        timeChoiceStamp.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(timeChoiceStamp.getTime());

        if(isAttending ){
            if(!currentUser.getLunchChoiceId().equals(currentRestaurant.getId()) || (isAttending && currentUser.getLunchChoiceId().equals(currentRestaurant.getId()) && !currentUser.isToday())){
                Log.d("Restaurant details", "updating choice...");
                mConnectedActivityViewModel.updateUserRestaurantChoice(currentRestaurant.getId(), currentRestaurant.getName(), formattedDate);
                saveData();
            }
        }else if(currentUser.getLunchChoiceId().equals(currentRestaurant.getId())&& currentUser.isToday()){
            Log.d("Restaurant details", "clearing current choice...");
            mConnectedActivityViewModel.updateUserRestaurantChoice("", "", formattedDate);
        }

        if(isFavorite && !currentUser.isFavorite(currentRestaurant.getId())){
            mConnectedActivityViewModel.updateUserRestaurantFavorite(currentRestaurant.getId(), "add");
        } else if (!isFavorite && currentUser.isFavorite(currentRestaurant.getId())) {
            mConnectedActivityViewModel.updateUserRestaurantFavorite(currentRestaurant.getId(), "remove");
        }

        mConnectedActivityViewModel.setCurrentWorkmates();

        super.onDestroy();
    }
    public void saveData(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(currentUser.getEmail(), currentRestaurant.getAddress());
        editor.apply();
    }

    private void setRestaurantDetail(View view){
        Log.d("Restaurant Detail", "name: " + currentRestaurant.getName());
        if(currentRestaurant.getImageUrl()!=null)Glide.with(view).load(currentRestaurant.getImageUrl()).centerCrop().into(restaurantImage);
        else if(currentRestaurant.getImageBitmap()!=null) restaurantImage.setImageBitmap(currentRestaurant.getImageBitmap());

        Log.d("setRestaurantDetails", "currentRestaurant Name " +  currentRestaurant.getName());
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

        if(currentRestaurant.getWebsite()==null){
            websiteLink.setEnabled(false);
            websiteLink.setAlpha(0.3f);
        }else{
            websiteLink.setEnabled(true);
            websiteLink.setAlpha(1);
        }
    }

    @Override
    public void onItemClicked(int position) {

    }
}
