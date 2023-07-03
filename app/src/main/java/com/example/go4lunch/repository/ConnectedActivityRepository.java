package com.example.go4lunch.repository;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Restaurant;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConnectedActivityRepository {
    private Context mContext;
    private Activity mActivity;
    private MutableLiveData<String> googlePlacesLiveData;
    private MutableLiveData<List<Restaurant>> restaurantsMutableLiveData;
    private GooglePlacesReadTask mGooglePlacesReadTask;

    public ConnectedActivityRepository(){
        /*this.mContext = context;
        this.mActivity = (Activity)context;*/

        googlePlacesLiveData = new MutableLiveData<>();
        restaurantsMutableLiveData = new MutableLiveData<>(new ArrayList<>());
        mGooglePlacesReadTask = new GooglePlacesReadTask();
    }

    public void setGooglePlacesData(double lat, double lng, String key){
        Executor executor = Executors.newSingleThreadExecutor();

        System.out.println("Map api key: " + key);

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + lat + "," + lng);
        googlePlacesUrl.append("&radius=" + 5000);
        googlePlacesUrl.append("&types=" + "restaurant");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + key);

        Log.d("ConnectedActivity", "placesUrl: " + googlePlacesUrl.toString());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                googlePlacesLiveData.postValue(mGooglePlacesReadTask.getGooglePlacesData(googlePlacesUrl.toString()));
            }
        });

    }
    public void setGooglePlacesRestaurants(){
        //Log.d("setGooglePlaceRestaurant", googlePlacesLiveData.getValue());
        restaurantsMutableLiveData.postValue(mGooglePlacesReadTask.getGooglePlacesRestaurants(googlePlacesLiveData.getValue()));
    }

    public MutableLiveData<String> getGooglePlacesLiveData() {
        return googlePlacesLiveData;
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsMutableLiveData() {
        return restaurantsMutableLiveData;
    }

}
