package com.example.go4lunch.repository;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.dataSource.ApiService;
import com.example.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConnectedActivityRepository {
    private Context mContext;
    private Activity mActivity;
    private MutableLiveData<List<Restaurant>> restaurantsMutableLiveData;
    private ApiService mGooglePlacesReadTask;

    public ConnectedActivityRepository(){
        /*this.mContext = context;
        this.mActivity = (Activity)context;*/

        restaurantsMutableLiveData = new MutableLiveData<>(new ArrayList<>());
        mGooglePlacesReadTask = new ApiService();
    }

    public void setGooglePlacesData(Location currentLocation){
        String key = BuildConfig.GMP_key;
        Executor executor = Executors.newSingleThreadExecutor();

        System.out.println("Map api key: " + key);

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude());
        googlePlacesUrl.append("&radius=" + 360);
        googlePlacesUrl.append("&types=" + "restaurant");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + key);

        Log.d("ConnectedActivity", "placesUrl: " + googlePlacesUrl.toString());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //googlePlacesLiveData.postValue(mGooglePlacesReadTask.getGooglePlacesData(googlePlacesUrl.toString()));
                restaurantsMutableLiveData.postValue(mGooglePlacesReadTask.getGooglePlacesData(googlePlacesUrl.toString(), currentLocation));
            }
        });

    }

    public MutableLiveData<List<Restaurant>> getRestaurantsMutableLiveData() {
        return restaurantsMutableLiveData;
    }
    public void setRestaurantsDistance(Location currentLocation){
        List<Restaurant> restaurants = restaurantsMutableLiveData.getValue();
        Location restaurantLocation = new Location("");

        for(int i = 0; i<restaurants.size();i++){
            restaurantLocation.setLatitude(restaurants.get(i).getLat());
            restaurantLocation.setLongitude(restaurants.get(i).getLng());

            restaurants.get(i).setDistance((int)restaurantLocation.distanceTo(currentLocation));
        }
        restaurantsMutableLiveData.postValue(restaurants);
    }

}
