package com.example.go4lunch.repository;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.data_source.AutoCompleteApi;
import com.example.go4lunch.data_source.NearbyPlacesApi;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.util.AutoCompleteBounds;
import com.google.android.libraries.places.api.model.RectangularBounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * ConnectedActivityRepository is responsible for managing data related to nearby restaurants and user actions
 * when a User is connected.
 */
public class ConnectedActivityRepository {
    private Location currentLocation;
    private RectangularBounds bounds;
    //private Activity mActivity;
    private List<Restaurant> nearbyRestaurants;
    private final MutableLiveData<List<Restaurant>> restaurantsMutableLiveData;
    private final NearbyPlacesApi mGooglePlacesReadTask;
    private final AutoCompleteApi mAutoCompleteApi;
    private final Executor executor;

    public ConnectedActivityRepository(NearbyPlacesApi nearbyPlacesApi, AutoCompleteApi autoCompleteApi, MutableLiveData<List<Restaurant>> restaurantsMutableLiveData, Executor executor){
        this.executor = executor;
        nearbyRestaurants = new ArrayList<>();
        this.restaurantsMutableLiveData = restaurantsMutableLiveData;
        mGooglePlacesReadTask = nearbyPlacesApi;
        mAutoCompleteApi = autoCompleteApi;
    }

    /**
     * Fetches Google Places data and updates the MutableLiveData with the retrieved restaurants.
     */
    public void setGooglePlacesData(){
        String key = BuildConfig.GMP_key;

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(currentLocation.getLatitude()).append(",").append(currentLocation.getLongitude());
        googlePlacesUrl.append("&radius=" + 400); //360
        googlePlacesUrl.append("&types=" + "restaurant");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=").append(key);

        executor.execute(() -> {
            nearbyRestaurants = mGooglePlacesReadTask.getGooglePlacesData(googlePlacesUrl.toString(), currentLocation);
            //setBounds();
            restaurantsMutableLiveData.postValue(nearbyRestaurants);
        });
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsMutableLiveData() {
        return restaurantsMutableLiveData;
    }

    /**
     * Updates the attendance number for nearby restaurants based on workmates' choices.
     *
     * @param workmates List of User objects representing workmates and their choices.
     */
    public void updateAttending(List<User> workmates){
        List<Restaurant> nearbyRestaurants = restaurantsMutableLiveData.getValue();
        List<String> restaurantID = new ArrayList<>();
        for(int i = 0; i<workmates.size();i++){
            if(!workmates.get(i).getLunchChoiceId().isEmpty() && workmates.get(i).isToday()){
                restaurantID.add(workmates.get(i).getLunchChoiceId()); }
        }
        if(nearbyRestaurants != null){
            for(int i = 0; i<nearbyRestaurants.size();i++){
                nearbyRestaurants.get(i).setAttendanceNum(Collections.frequency(restaurantID, nearbyRestaurants.get(i).getId()));
            }
        }

        restaurantsMutableLiveData.postValue(nearbyRestaurants);
    }
    /**
     * Initiates an autocomplete request for the given text and updates the MutableLiveData with the resulting restaurant data.
     *
     * @param text The text to be used for autocomplete suggestions.
     */
    public void autocomplete(String text){
        if(bounds==null) bounds = AutoCompleteBounds.setBounds(currentLocation);
        mAutoCompleteApi.autocomplete(text, nearbyRestaurants, bounds, currentLocation);
        restaurantsMutableLiveData.postValue(mAutoCompleteApi.getRestaurantsMutableLiveData().getValue());
    }

    /**
     * When Autocomplete search is stopped it will reset the Nearby restaurants in the activity,
     * this avoids doing a new call to the Nearby Places Api
     */
    public void resetNearbyRestaurants(){
        restaurantsMutableLiveData.postValue(nearbyRestaurants);
    }

    /**
     * Updates the current location
     * @param currentLocation the new current location
     */
    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
        bounds = AutoCompleteBounds.setBounds(currentLocation);
    }
}
