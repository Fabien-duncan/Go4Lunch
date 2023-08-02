package com.example.go4lunch.repository;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.model.Restaurant;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class RestaurantDetailRepository {
    private Activity mActivity;
    private Context mContext;

    private MutableLiveData<Restaurant> currentRestaurantMutableLiveData;

    public RestaurantDetailRepository(Context context){
        this.mContext = context;
        this.mActivity = (Activity)context;

        currentRestaurantMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Restaurant> getCurrentRestaurantMutableLiveData() {
        return currentRestaurantMutableLiveData;
    }
    public void setDetail(Restaurant currentRestaurant){
        String key = BuildConfig.GMP_key;

        // Initialize Places.
        Places.initialize(mActivity.getApplicationContext(), key);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(mContext);
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
            currentRestaurant.setWebsite(place.getWebsiteUri());
            currentRestaurant.setPhoneNumber(place.getPhoneNumber());
            currentRestaurantMutableLiveData.postValue(currentRestaurant);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e("Places detail", "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
            }
        });
    }
}
