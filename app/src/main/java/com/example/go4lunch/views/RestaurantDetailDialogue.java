package com.example.go4lunch.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.adapter.RestaurantDetailWorkmatesAdapter;
import com.example.go4lunch.adapter.WorkmatesRecyclerViewInterface;
import com.example.go4lunch.di.Injection;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.repository.RestaurantDetailRepository;
import com.example.go4lunch.viewmodel.ConnectedActivityViewModel;
import com.example.go4lunch.viewmodel.RestaurantDetailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
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

        restaurantName = view.findViewById(R.id.restaurant_detail_name_tv);
        restaurantDetail = view.findViewById(R.id.restaurant_detail_address_tv);


        List<User> attendingWorkmatesList = mRestaurantDetailViewModel.getAllWorkmates().getValue();

        RecyclerView attendingWorkmatesRecyclerView = view.findViewById(R.id.restaurant_detail_attend_rv);
        attendingWorkmatesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        RestaurantDetailWorkmatesAdapter restaurantDetailWorkmatesAdapter = new RestaurantDetailWorkmatesAdapter(getContext(), attendingWorkmatesList, this);
        attendingWorkmatesRecyclerView.setAdapter(restaurantDetailWorkmatesAdapter);
        restaurantDetailWorkmatesAdapter.setWorkmatesList(attendingWorkmatesList);


        setObservers(view, like, attend, restaurantDetailWorkmatesAdapter);

        websiteLink.setOnClickListener(view1 -> {
            if(currentRestaurant.getWebsite()!=null){
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentRestaurant.getWebsite().toString()));
                startActivity(i);
            }
            else Log.d("RestaurantURL", "there is no website!");

        });
        setClickListeners(like, phone, attend);

        return view;
    }

    private void setClickListeners(Button like, Button phone, FloatingActionButton attend) {
        phone.setOnClickListener(view12 -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CALL_PHONE) ==
                    PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + currentRestaurant.getPhoneNumber()));
                startActivity(intent);


            } else if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(R.string.permissions_needed)
                        .setMessage(R.string.phone_permission_msg)
                        .setPositiveButton(R.string.grant, (dialog, which) ->
                                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE))
                        .setNegativeButton(R.string.cancel, null)
                        .show();

            } else {
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
    }

    private void setObservers(View view, Button like, FloatingActionButton attend, RestaurantDetailWorkmatesAdapter restaurantDetailWorkmatesAdapter) {
        mRestaurantDetailViewModel.getAllWorkmates().observe(this, restaurantDetailWorkmatesAdapter::setWorkmatesList);
        mRestaurantDetailViewModel.getCurrentRestaurantMutableLiveDate().observe(this, restaurant -> {
            currentRestaurant = restaurant;
            setRestaurantDetail(view);
        });


        mConnectedActivityViewModel.getCurrentUserMutableLiveData().observe(this, user -> {
            currentUser = user;

            mRestaurantDetailViewModel.retrieveFilteredWorkmates(currentRestaurant.getId());

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
    }

    public void setCurrentRestaurant(Restaurant currentRestaurant) {
        this.currentRestaurant = currentRestaurant;
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d("Call permissions", "Call granted!!");
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", requireActivity().getPackageName(), null));
                    startActivity(intent);
                }
            });

    //this is to check if the like status or attending changes when the dialogue is closed.
    //This is done to avoid making calls to firebase firestore if the user keeps checking and unchecking the
    @Override
    public void onDestroy() {
        Calendar timeChoiceStamp = Calendar.getInstance();
        timeChoiceStamp.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(timeChoiceStamp.getTime());

        if(isAttending ){
            if(!currentUser.getLunchChoiceId().equals(currentRestaurant.getId()) || (isAttending && currentUser.getLunchChoiceId().equals(currentRestaurant.getId()) && !currentUser.isToday())){
                mConnectedActivityViewModel.updateUserRestaurantChoice(currentRestaurant.getId(), currentRestaurant.getName(), formattedDate);
                saveData();
            }
        }else if(currentUser.getLunchChoiceId().equals(currentRestaurant.getId())&& currentUser.isToday()){
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
        if(currentRestaurant.getImageUrl()!=null)Glide.with(view).load(currentRestaurant.getImageUrl()).centerCrop().into(restaurantImage);
        else if(currentRestaurant.getImageBitmap()!=null) restaurantImage.setImageBitmap(currentRestaurant.getImageBitmap());

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
