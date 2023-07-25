package com.example.go4lunch.repository;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.dataSource.ApiService;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        googlePlacesUrl.append("&radius=" + 400); //360
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

    public void updateAttending(List<User> workmates){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String endOfLunch = now.format(formatter).toString() +"T15:00";
        String  startOfLunch = now.minusDays(1).format(formatter) +"T15:00";

        Log.d("checkTime", "start: " + startOfLunch + " end: " + endOfLunch + " now: " + now.toString());

        List<Restaurant> nearbyRestaurants = restaurantsMutableLiveData.getValue();
        List<String> restaurantID = new ArrayList<>();
        for(int i = 0; i<workmates.size();i++){
            if(!workmates.get(i).getLunchChoiceId().isEmpty()){
                restaurantID.add(workmates.get(i).getLunchChoiceId()); }
        }
        Log.d("updateAttending", restaurantID.toString());
        for(int i = 0; i<nearbyRestaurants.size();i++){
            Log.d("updateAttending", "Restaurant Id " + nearbyRestaurants.get(i).getId() + " number of occurence " + Collections.frequency(restaurantID, nearbyRestaurants.get(i).getId()));
            nearbyRestaurants.get(i).setAttendanceNum(Collections.frequency(restaurantID, nearbyRestaurants.get(i).getId()));
            //nearbyRestaurants.get(i).setAttendanceNum(5);
        }
        Log.d("updateAttending", "number of restaurants " + nearbyRestaurants.size());
        restaurantsMutableLiveData.postValue(nearbyRestaurants);
    }
    /*private void autocomplete(String text,RectangularBounds bounds, Location currentLocation){
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Create a RectangularBounds object.
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                .setLocationRestriction(bounds)
                .setOrigin(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .setCountries("FR")
                .setTypesFilter(Arrays.asList("restaurant"))
                .setSessionToken(token)
                .setQuery(text)
                .build();

        String key = BuildConfig.GMP_key;

        // Initialize Places.
        Places.initialize(this.getApplicationContext(), key);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        List<String> placeIds = new ArrayList<>();
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                placeIds.add(prediction.getPlaceId());
                Log.i("autocomplete", prediction.getPlaceId());
                Log.i("autocomplete", prediction.getPrimaryText(null).toString());
            }
            filterRestaurantsByIds(placeIds);
            autocompleteAdapter.setRestaurantList(filteredNearbyRestaurants);
            mConnectedActivityViewModel.updateRestaurantsListForFilter(filteredNearbyRestaurants);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("autocomplete", "Place not found: " + apiException.getStatusCode());
            }
        });

    }*/
}
