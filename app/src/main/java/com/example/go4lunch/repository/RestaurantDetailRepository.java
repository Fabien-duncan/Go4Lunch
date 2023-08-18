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

/**
 * Repository class responsible for managing the details of a restaurant.
 */
public class RestaurantDetailRepository {

    private final MutableLiveData<Restaurant> currentRestaurantMutableLiveData;

    private final PlacesClient placesClient;

    private GooglePlacesDetailsApi mGooglePlacesDetailsApi;

    public RestaurantDetailRepository( PlacesClient placesClient, GooglePlacesDetailsApi googlePlacesDetailsApi){
        this.placesClient = placesClient;
        this.mGooglePlacesDetailsApi = googlePlacesDetailsApi;

        currentRestaurantMutableLiveData = mGooglePlacesDetailsApi.getRestaurantDetailMutableLiveData();
    }

    public MutableLiveData<Restaurant> getCurrentRestaurantMutableLiveData() {
        return currentRestaurantMutableLiveData;
    }
    /**
     * Sets the details of the given restaurant using the Places API. Retrieves more ot less details depending on if the currentRestaurant doesn't have any details
     *
     * @param currentRestaurant The restaurant for which to fetch and set the details.
     */
    public void setDetail(Restaurant currentRestaurant){
        final String placeId = currentRestaurant.getId();

        if(currentRestaurant.getName()!=null) {
            mGooglePlacesDetailsApi.setSmallDetail(currentRestaurant, placesClient, placeId);

        }else{
            mGooglePlacesDetailsApi.setAllDetails(currentRestaurant, placesClient, placeId);
        }
    }
}
