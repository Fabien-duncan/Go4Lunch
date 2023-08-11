package com.example.go4lunch.dataSource;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.model.Restaurant;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutoCompleteApi {

    private MutableLiveData<List<Restaurant>> restaurantsMutableLiveData;
    private List<Restaurant> nearbyRestaurants;
    private Context mContext;

    public AutoCompleteApi(Context context){
        mContext = context;
        restaurantsMutableLiveData = new MutableLiveData<>(new ArrayList<>());
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsMutableLiveData() {
        return restaurantsMutableLiveData;
    }
    public void autocomplete(String text, List<Restaurant> nearbyRestaurants, RectangularBounds bounds, Location currentLocation){
        this.nearbyRestaurants = nearbyRestaurants;
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationRestriction(bounds)
                .setOrigin(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .setCountries("FR")
                .setTypesFilter(Collections.singletonList("restaurant"))
                .setSessionToken(token)
                .setQuery(text)
                .build();

        String key = BuildConfig.GMP_key;

        // Initialize Places.
        Places.initialize(mContext.getApplicationContext(), key);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(mContext);
        List<String> placeIds = new ArrayList<>();
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                placeIds.add(prediction.getPlaceId());
                Log.i("autocomplete", prediction.getPlaceId());
                Log.i("autocomplete", prediction.getPrimaryText(null).toString());
            }
            filterRestaurantsByIds(placeIds);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("autocomplete", "Place not found: " + apiException.getStatusCode());
            }
        });
    }
    private void filterRestaurantsByIds(List<String> placeIds){
        List<Restaurant> filteredNearbyRestaurants = new ArrayList<>();
        for(int i = 0; i < placeIds.size(); i++){
            int j = 0;
            while(j<nearbyRestaurants.size()){
                if(placeIds.get(i).equals(nearbyRestaurants.get(j).getId())){
                    filteredNearbyRestaurants.add(nearbyRestaurants.get(j));
                    j=nearbyRestaurants.size();
                }
                j++;
            }
        }
        Log.d("filteredRestaurant", "size: " + filteredNearbyRestaurants.size());
        restaurantsMutableLiveData.postValue(filteredNearbyRestaurants);
    }
}
