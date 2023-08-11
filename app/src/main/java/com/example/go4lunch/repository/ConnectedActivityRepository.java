package com.example.go4lunch.repository;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.dataSource.ApiService;
import com.example.go4lunch.dataSource.AutoCompleteApi;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConnectedActivityRepository {
    private final Context mContext;
    private Location currentLocation;
    private RectangularBounds bounds;
    //private Activity mActivity;
    private List<Restaurant> nearbyRestaurants;
    private MutableLiveData<List<Restaurant>> restaurantsMutableLiveData;
    private final ApiService mGooglePlacesReadTask;
    private final AutoCompleteApi mAutoCompleteApi;
    private Executor executor;

    public ConnectedActivityRepository(Context context){
        this.mContext = context;
        this.executor = Executors.newSingleThreadExecutor();
        nearbyRestaurants = new ArrayList<>();
        restaurantsMutableLiveData = new MutableLiveData<>(new ArrayList<>());
        mGooglePlacesReadTask = new ApiService();
        mAutoCompleteApi = new AutoCompleteApi(mContext);
    }
    //constructor for unit tests
    ConnectedActivityRepository(Context context,ApiService apiService, AutoCompleteApi autoCompleteApi, MutableLiveData<List<Restaurant>> restaurantsMutableLiveData, Executor executor){
        this.mContext = context;
        this.executor = executor;
        nearbyRestaurants = new ArrayList<>();
        this.restaurantsMutableLiveData = restaurantsMutableLiveData;
        mGooglePlacesReadTask = apiService;
        mAutoCompleteApi = autoCompleteApi;
    }

    public void setGooglePlacesData(){
        String key = BuildConfig.GMP_key;
        /*Executor executor = Executors.newSingleThreadExecutor();*/

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(currentLocation.getLatitude()).append(",").append(currentLocation.getLongitude());
        googlePlacesUrl.append("&radius=" + 400); //360
        googlePlacesUrl.append("&types=" + "restaurant");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=").append(key);

        //Log.d("ConnectedActivity", "placesUrl: " + googlePlacesUrl);
        executor.execute(() -> {
            //googlePlacesLiveData.postValue(mGooglePlacesReadTask.getGooglePlacesData(googlePlacesUrl.toString()));
            nearbyRestaurants = mGooglePlacesReadTask.getGooglePlacesData(googlePlacesUrl.toString(), currentLocation);
            setBounds();
            restaurantsMutableLiveData.postValue(nearbyRestaurants);
        });

        //restaurantsMutableLiveData.postValue(mGooglePlacesReadTask.getGooglePlacesData(googlePlacesUrl.toString(), currentLocation));

    }


    public MutableLiveData<List<Restaurant>> getRestaurantsMutableLiveData() {
        return restaurantsMutableLiveData;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateAttending(List<User> workmates){
        //Log.d("checkTime", "start: " + startOfLunch + " end: " + endOfLunch + " now: " + now + "\n workmate Time: " + workmates.get(5).getChoiceTimeStamp() + " is today " + workmates.get(5).isToday());
        List<Restaurant> nearbyRestaurants = restaurantsMutableLiveData.getValue();
        List<String> restaurantID = new ArrayList<>();
        for(int i = 0; i<workmates.size();i++){
            if(!workmates.get(i).getLunchChoiceId().isEmpty() && workmates.get(i).isToday()){
                restaurantID.add(workmates.get(i).getLunchChoiceId()); }
        }
        //Log.d("updateAttending", restaurantID.toString());
        if(nearbyRestaurants != null){
            for(int i = 0; i<nearbyRestaurants.size();i++){
                //Log.d("updateAttending", "Restaurant Id " + nearbyRestaurants.get(i).getId() + " number of occurance " + Collections.frequency(restaurantID, nearbyRestaurants.get(i).getId()));
                nearbyRestaurants.get(i).setAttendanceNum(Collections.frequency(restaurantID, nearbyRestaurants.get(i).getId()));
            }
        }

        restaurantsMutableLiveData.postValue(nearbyRestaurants);
    }
    public void autocomplete(String text){
        if(bounds==null)setBounds();
        mAutoCompleteApi.autocomplete(text, nearbyRestaurants, bounds, currentLocation);
        restaurantsMutableLiveData.postValue(mAutoCompleteApi.getRestaurantsMutableLiveData().getValue());
    }
    public void resetNearbyRestaurants(){
        restaurantsMutableLiveData.postValue(nearbyRestaurants);
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
    private void setBounds() {
        int mDistanceInMeters = 400;
        double latRadian = Math.toRadians(currentLocation.getLatitude());

        double degLatKm = 110.574235;
        double degLongKm = 110.572833 * Math.cos(latRadian);
        double deltaLat = mDistanceInMeters / 1000.0 / degLatKm;
        double deltaLong = mDistanceInMeters / 1000.0 / degLongKm;

        double minLat = currentLocation.getLatitude() - deltaLat;
        double minLong = currentLocation.getLongitude() - deltaLong;
        double maxLat = currentLocation.getLatitude() + deltaLat;
        double maxLong = currentLocation.getLongitude() + deltaLong;

        bounds = RectangularBounds.newInstance(
                new LatLng(minLat, minLong),
                new LatLng(maxLat, maxLong));
    }
}
