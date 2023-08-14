package com.example.go4lunch.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.dataSource.GooglePlacesDetailsApi;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.util.FormatString;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class RestaurantDetailRepository {

    private final MutableLiveData<Restaurant> currentRestaurantMutableLiveData;

    private final PlacesClient placesClient;

    private GooglePlacesDetailsApi mGooglePlacesDetailsApi;

    public RestaurantDetailRepository( PlacesClient placesClient, GooglePlacesDetailsApi googlePlacesDetailsApi){
        this.placesClient = placesClient;
        this.mGooglePlacesDetailsApi = googlePlacesDetailsApi;
        //this.mActivity = (Activity)context;


        currentRestaurantMutableLiveData = mGooglePlacesDetailsApi.getRestaurantDetailMutableLiveData();
    }

    public MutableLiveData<Restaurant> getCurrentRestaurantMutableLiveData() {
        return currentRestaurantMutableLiveData;
    }
    public void setDetail(Restaurant currentRestaurant){
        // Define a Place ID.
        final String placeId = currentRestaurant.getId();

        if(currentRestaurant.getName()!=null) {
            mGooglePlacesDetailsApi.setSmallDetail(currentRestaurant, placesClient, placeId);

        }else{
            mGooglePlacesDetailsApi.setAllDetails(currentRestaurant, placesClient, placeId);
        }
    }
}
