package com.example.go4lunch.util;

import android.location.Location;
import android.util.Log;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateNearbyRestaurants {
    public List<Restaurant> parse(JSONObject jsonObject, Location currentLocation) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert jsonArray != null;
        return getRestaurants(jsonArray, currentLocation);
    }

    private List<Restaurant> getRestaurants(JSONArray jsonArray, Location currentLocation) {
        int placesCount = jsonArray.length();
        List<Restaurant> restaurantsList = new ArrayList<>();
        Restaurant aRestaurant;

        for (int i = 0; i < placesCount; i++) {
            try {
                aRestaurant = getRestaurant((JSONObject) jsonArray.get(i), currentLocation);
                restaurantsList.add(aRestaurant);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return restaurantsList;
    }

    private Restaurant getRestaurant(JSONObject googlePlaceJson, Location currentLocation) {
        Restaurant restaurant = null;
        String name = "-NA-";
        String address = "-NA-";
        String id;
        double lat;
        double lng;
        double rating = -1.0;
        String photo_reference = "";
        String openNow = "Unknown open status";


        String latitude;
        String longitude;

        try {
            if (!googlePlaceJson.isNull("name")) {
                name = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                address = googlePlaceJson.getString("vicinity");
            }
            if(!googlePlaceJson.isNull("rating")) {
                rating = Double.parseDouble(googlePlaceJson.getString("rating"));
            }
            if(!googlePlaceJson.isNull("photos")) {
                photo_reference = googlePlaceJson.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
            }
            if(!googlePlaceJson.isNull("opening_hours")) {
                openNow = googlePlaceJson.getJSONObject("opening_hours").getString("open_now");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            id = googlePlaceJson.getString("place_id");




            lat = Double.parseDouble(latitude);
            lng = Double.parseDouble(longitude);

            Location restaurantLocation = new Location("");
            restaurantLocation.setLatitude(lat);
            restaurantLocation.setLongitude(lng);

            int distance =(int)restaurantLocation.distanceTo(currentLocation);

            //convert to Restaurant object
            restaurant = new Restaurant(id,name,address,lat,lng,rating,distance);

            String key = BuildConfig.GMP_key;
            if(photo_reference.isEmpty()){
                restaurant.setImageUrl("https://t4.ftcdn.net/jpg/04/00/24/31/360_F_400243185_BOxON3h9avMUX10RsDkt3pJ8iQx72kS3.jpg");
            }else{
                String photoUrl = String.format("https://maps.googleapis.com/maps/api/place/photo" +
                        "?maxwidth=400" +
                        "&photo_reference="+photo_reference +
                        "&key=" + key);
                restaurant.setImageUrl(photoUrl);
            }
            restaurant.setOpeningHours(openNow);
            Log.d("createRestaurant","restaurant " + restaurant.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return restaurant;
    }
}
