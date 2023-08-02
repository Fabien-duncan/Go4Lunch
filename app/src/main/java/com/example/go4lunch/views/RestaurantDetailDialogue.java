package com.example.go4lunch.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.adapter.RestaurantDetailWorkmatesAdapter;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.util.FormatString;
import com.example.go4lunch.viewmodel.ConnectedActivityViewModel;
import com.example.go4lunch.viewmodel.RestaurantDetailViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantDetailDialogue extends DialogFragment{
    private Restaurant currentRestaurant;
    private Uri restaurantUrl;
    private Button websiteLink;
    private ConnectedActivityViewModel mConnectedActivityViewModel;
    private RestaurantDetailViewModel mRestaurantDetailViewModel;
    private User currentUser;
    private boolean isAttending;
    private boolean isFavorite;
    private RecyclerView attendingWorkmatesRecyclerView;
    private List<User> attendingWorkmatesList;
    private AuthenticationRepository mAuthenticationRepository;
    private ImageView restaurantImage, star1, star2, star3;
    private TextView restaurantName,restaurantDetail;

    public static RestaurantDetailDialogue newInstance(){
        return new RestaurantDetailDialogue();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogueTheme);
        mConnectedActivityViewModel = ((ConnectedActivity) getActivity()).getConnectedActivityViewModel();
        mAuthenticationRepository = new AuthenticationRepository(getContext());
        isAttending = false;
        isFavorite = false;
        mRestaurantDetailViewModel = new RestaurantDetailViewModel(mAuthenticationRepository, getContext());
    }


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

        like.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#86FB7540")));
        like.setTextColor(ColorStateList.valueOf(Color.parseColor("#86FB7540")));

        restaurantName = view.findViewById(R.id.restaurant_detail_name_tv);
        restaurantDetail = view.findViewById(R.id.restaurant_detail_address_tv);



        attendingWorkmatesList = mRestaurantDetailViewModel.getAllWorkmates().getValue();

        attendingWorkmatesRecyclerView = view.findViewById(R.id.restaurant_detail_attend_rv);
        attendingWorkmatesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        RestaurantDetailWorkmatesAdapter restaurantDetailWorkmatesAdapter = new RestaurantDetailWorkmatesAdapter(getContext(), attendingWorkmatesList);
        attendingWorkmatesRecyclerView.setAdapter(restaurantDetailWorkmatesAdapter);
        restaurantDetailWorkmatesAdapter.setWorkmatesList(attendingWorkmatesList);



        mRestaurantDetailViewModel.getAllWorkmates().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if(users.size()>0)Log.d("attending workmates", users.get(0).getDisplayName() + " is attending");
                restaurantDetailWorkmatesAdapter.setWorkmatesList(users);
            }
        });
        mRestaurantDetailViewModel.getCurrentRestaurantMutableLiveDate().observe(this, new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurant) {
                currentRestaurant = restaurant;
                setRestaurantDetail(view);
            }
        });


        mConnectedActivityViewModel.getCurrentUserMutableLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d("User data Rest detail", "id: " + user.getDisplayName());
                currentUser = user;
                mRestaurantDetailViewModel.retrieveFilteredWorkmates(user.getLunchChoiceId());
                if(currentRestaurant!= null){
                    mRestaurantDetailViewModel.retrieveFilteredWorkmates(currentRestaurant.getId());
                    mRestaurantDetailViewModel.setDetail(currentRestaurant);
                    //getDetail();
                    //setRestaurantDetail(view);
                }
                else{
                    getAllInformation(currentUser.getLunchChoiceId(), view);
                    Log.d("restaurantDetail", "there is no restaurant");
                }

                //loadData();//temp
                if(currentRestaurant != null){
                    if(user.getLunchChoiceId()!= null && user.getLunchChoiceId().equals(currentRestaurant.getId()) && user.isToday()){
                        attend.setImageResource(R.drawable.baseline_check_circle_24);
                        isAttending = true;
                    }
                    if(user.isFavorite(currentRestaurant.getId())){
                        like.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#FB7540")));
                        like.setTextColor(ColorStateList.valueOf(Color.parseColor("#FB7540")));
                        isFavorite = true;
                    }
                }
                else{
                    attend.setImageResource(R.drawable.baseline_check_circle_24);
                    isAttending = true;
                    like.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#FB7540")));
                    like.setTextColor(ColorStateList.valueOf(Color.parseColor("#FB7540")));
                    isFavorite = true;
                }

            }
        });
        websiteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentRestaurant.getWebsite()!=null){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(currentRestaurant.getWebsite().toString()));
                    startActivity(i);
                }
                else Log.d("RestaurantURL", "there is no website!");

            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getContext(), Manifest.permission.CALL_PHONE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    Log.d("calling", "phone num: " + currentRestaurant.getPhoneNumber());
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

            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavorite){
                    //like.setAlpha(0.5f);
                    like.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#86FB7540")));
                    like.setTextColor(ColorStateList.valueOf(Color.parseColor("#86FB7540")));
                }else {
                    //like.setAlpha(1.0f);
                    like.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#FB7540")));
                    like.setTextColor(ColorStateList.valueOf(Color.parseColor("#FB7540")));
                }
                isFavorite = !isFavorite;
            }
        });
        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAttending){
                    attend.setImageResource(R.drawable.baseline_check_circle_transparent_24);
                }else{
                    attend.setImageResource(R.drawable.baseline_check_circle_24);
                }
                isAttending = !isAttending;
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
    private void getDetail(){
            String key = BuildConfig.GMP_key;

            // Initialize Places.
            Places.initialize(getActivity().getApplicationContext(), key);

            // Create a new Places client instance.
            PlacesClient placesClient = Places.createClient(getContext());
            // Define a Place ID.
            final String placeId = currentRestaurant.getId();

            // Specify the fields to return.
            final List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME,Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);

            // Construct a request object, passing the place ID and fields array.
            final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                Log.i("Places detail", "Place found: " + place.getName()
                        + " Phone Num: " + place.getPhoneNumber()
                        + " web: " + place.getWebsiteUri());
                restaurantUrl = place.getWebsiteUri();
                currentRestaurant.setPhoneNumber(place.getPhoneNumber());
            }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e("Places detail", "Place not found: " + exception.getMessage());
                        final int statusCode = apiException.getStatusCode();
                    }
            });
    }

    public void getAllInformation(String restaurantID, View view){
        String key = BuildConfig.GMP_key;

        // Initialize Places.
        Places.initialize(getActivity().getApplicationContext(), key);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getContext());
        Restaurant restaurant =new Restaurant();
        // Define a Place ID.

        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME,Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS, Place.Field.RATING);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(restaurantID, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();



            restaurant.setName(FormatString.capitalizeEveryWord(place.getName()));
            restaurant.setId(restaurantID);
            restaurant.setPhoneNumber(place.getPhoneNumber());
            restaurant.setAddress(place.getAddress());
            restaurant.setRating(place.getRating());
            Log.i("Places detail", "Place found: " + restaurant.getName()
                    + " Phone Num: " + restaurant.getPhoneNumber()
                    + " address: " + restaurant.getAddress());

            currentRestaurant = restaurant;
            mRestaurantDetailViewModel.retrieveFilteredWorkmates(currentRestaurant.getId());
            setRestaurantDetail(view);

            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w("PlaceImage", "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(700) // Optional.
                    .setMaxHeight(500) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                restaurantImage.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e("PlaceImage", "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e("Places detail", "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });
    }

    @Override
    public void onDestroy() {
        LocalDateTime timeChoiceStamp = LocalDateTime.now();
        //Log.d("Restaurant Details", "closing page. Status of attend: " + isAttending);
        if(isAttending && !currentUser.getLunchChoiceId().equals(currentRestaurant.getId())){
            //Log.d("Restaurant details", "updating choice...");
            mConnectedActivityViewModel.updateUserRestaurantChoice(currentRestaurant.getId(), currentRestaurant.getName(), timeChoiceStamp);
            saveData();
        }else if(!isAttending && currentUser.getLunchChoiceId().equals(currentRestaurant.getId())){
            //Log.d("Restaurant details", "clearing current choice...");
            mConnectedActivityViewModel.updateUserRestaurantChoice("", "", timeChoiceStamp);
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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(currentUser.getEmail(), currentRestaurant.getAddress());
        editor.apply();
    }

    private void setRestaurantDetail(View view){
        Log.d("Restaurant Detail", "name: " + currentRestaurant.getName());
        if(currentRestaurant.getImageUrl()!=null)Glide.with(view).load(currentRestaurant.getImageUrl()).centerCrop().into(restaurantImage);
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
}
