package com.example.go4lunch.dataSource;

import android.location.Location;
import android.util.Log;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.util.CreateNearbyRestaurants;
import com.example.go4lunch.util.HttpReader;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.util.List;

/**
 * NearbyPlacesApi is allow the ConnectedActivityRepository to retrieve nearby restaurant information using Google Places API.
 */
public class NearbyPlacesApi {
    private String googlePlacesData = null;
    private GoogleMap mGoogleMap;

    /**
     * Retrieves nearby restaurant data from the provided Google Places URL.
     *
     * @param googlePlacesUrl  The URL for retrieving nearby restaurant data.
     * @param currentLocation  The current location of the device.
     * @return List of Restaurant objects representing nearby restaurants.
     */
    public List<Restaurant> getGooglePlacesData( String googlePlacesUrl, Location currentLocation){
        try {
            HttpReader http = new HttpReader();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return getGooglePlacesRestaurants(googlePlacesData, currentLocation);
    }
    /**
     * Parses the provided Google Places JSON data to create a list of nearby Restaurant objects.
     *
     * @param googlePlacesJson The JSON data containing nearby restaurant information.
     * @param currentLocation  The current location of the device.
     * @return List of Restaurant objects representing nearby restaurants.
     */
    public List<Restaurant> getGooglePlacesRestaurants(String googlePlacesJson, Location currentLocation){
        List<Restaurant> nearbyRestaurants = null;
        CreateNearbyRestaurants placeJsonParser = new CreateNearbyRestaurants();

        try {
            nearbyRestaurants = placeJsonParser.parse(new JSONObject(googlePlacesJson), currentLocation);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return nearbyRestaurants;
    }
}
