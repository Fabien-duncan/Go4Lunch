package com.example.go4lunch.dataSource;

import android.util.Log;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.util.CreateNearbyRestaurants;
import com.example.go4lunch.util.HttpReader;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.util.List;

public class ApiService {
    private String googlePlacesData = null;
    private GoogleMap mGoogleMap;
    //private List<Restaurant> nearbyRestaurants;
    //private JSONObject mJSONObject;

    /*public String getGooglePlacesData( String googlePlacesUrl){
        try {
            HttpReader http = new HttpReader();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return googlePlacesData;
    }*/
    public List<Restaurant> getGooglePlacesData( String googlePlacesUrl){
        try {
            HttpReader http = new HttpReader();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return getGooglePlacesRestaurants(googlePlacesData);
    }
    public List<Restaurant> getGooglePlacesRestaurants(String googlePlacesJson){
        List<Restaurant> nearbyRestaurants = null;
        CreateNearbyRestaurants placeJsonParser = new CreateNearbyRestaurants();

        try {
            nearbyRestaurants = placeJsonParser.parse(new JSONObject(googlePlacesJson));
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return nearbyRestaurants;
    }
}
