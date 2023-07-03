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

    public void setGooglePlacesData(String googlePlacesUrl){
        Log.d("ConnectedActivity", "placesUrl: " + googlePlacesUrl);
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                googlePlacesLiveData.postValue(mGooglePlacesReadTask.getGooglePlacesData(googlePlacesUrl));
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
